/* 
 * Copyright 2009 Laurent Bovet, Swiss Post IT <lbovet@jminix.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jminix.console.resource;

import net.sf.json.JSONSerializer;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.jminix.server.ServerConnectionProvider;
import org.jminix.type.HtmlContent;
import org.jminix.type.InputStreamContent;
import org.restlet.data.CacheDirective;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.openmbean.CompositeData;
import java.util.*;

public abstract class AbstractTemplateResource extends ServerResource
{
    public String a;

    private final static String VELOCITY_ENGINE_CONTEX_KEY = "template.resource.velocity.engine";
    protected final static String ATTRIBUTE_MODEL_ATTRIBUTE = "attribute";
    protected final static String VALUE_MODEL_ATTRIBUTE = "value";
    protected final static String ITEMS_MODEL_ATTRIBUTE = "items";
    protected final EncoderBean encoder = new EncoderBean();

    @Override
    protected void doInit() throws ResourceException {
        super.doInit();
        VelocityEngine ve =
                (VelocityEngine) getContext().getAttributes().get(VELOCITY_ENGINE_CONTEX_KEY);

        if (ve == null)
        {
            ve = new VelocityEngine();

            Properties p = new Properties();
            p.setProperty("resource.loader", "class");
            p.setProperty("class.resource.loader.description",
                    "Velocity Classpath Resource Loader");
            p.setProperty("class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            p.setProperty("runtime.log.logsystem.log4j.logger", "common.jmx.velocity");

            try
            {
                ve.init(p);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            getContext().getAttributes().put(VELOCITY_ENGINE_CONTEX_KEY, ve);
        }
    }

    protected abstract String getTemplateName();

    @Get("html|txt|json")
    public abstract Map<String, Object> getModel();

    @Override
    public Representation toRepresentation(Object source, Variant variant)
    {
        if (source instanceof Representation) {
            return (Representation) source;
        }
        if (source == null) {
            return new EmptyRepresentation();
        }
        Map<String, Object> model = (Map<String, Object>) source;

        getResponseCacheDirectives().add(CacheDirective.noCache());
        getResponseCacheDirectives().add(CacheDirective.mustRevalidate());
        getResponseCacheDirectives().add(CacheDirective.noStore());
        Representation representation;

        if (MediaType.TEXT_HTML.equals(variant.getMediaType()))
        {
            Map<String, Object> enrichedModel = new HashMap<String, Object>(model);

            String templateName = getTemplateName();
            Object resultObject = enrichedModel.get(VALUE_MODEL_ATTRIBUTE);
            
            if (resultObject instanceof InputStreamContent) {
                return new InputRepresentation((InputStreamContent) resultObject, MediaType.APPLICATION_OCTET_STREAM);
            }

            if (resultObject instanceof HtmlContent) {
            	templateName = "html-attribute";
            }
            
            Template template;
            try
            {
                VelocityEngine ve =
                        (VelocityEngine) getContext().getAttributes().get(
                                VELOCITY_ENGINE_CONTEX_KEY);

                template = ve.getTemplate("jminix/templates/" + templateName + ".vm");
                template.setEncoding("UTF-8");
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            
            String skin = getRequest().getResourceRef().getQueryAsForm().getValues("skin");                       
            if(skin==null) {
                skin="default";
            }
            String desc = getRequest().getResourceRef().getQueryAsForm().getValues("desc");  
            if(desc==null) {
                desc="on";
            }
            enrichedModel.put("query", getQueryString());
            enrichedModel.put("ok", "1".equals(getRequest().getResourceRef().getQueryAsForm().getValues("ok")));
            enrichedModel.put("margin", "embedded".equals(skin) ? 0 : 5);
            enrichedModel.put("skin", skin);
            enrichedModel.put("desc", desc);
            enrichedModel.put("encoder", new EncoderBean());
            enrichedModel.put("request", getRequest());

            representation = new TemplateRepresentation(template, enrichedModel, MediaType.TEXT_HTML);

        }
        else if (MediaType.TEXT_PLAIN.equals(variant.getMediaType()))
        {

            Map<String, Object> enrichedModel = new HashMap<String, Object>(model);

            Template template;
            try
            {
                VelocityEngine ve =
                        (VelocityEngine) getContext().getAttributes().get(
                                VELOCITY_ENGINE_CONTEX_KEY);

                template =
                        ve.getTemplate("jminix/templates/"
                                + getTemplateName() + "-plain.vm");
                template.setEncoding("UTF-8");
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            enrichedModel.put("encoder", encoder);
            enrichedModel.put("request", getRequest());

            representation = new TemplateRepresentation(template, enrichedModel, MediaType.TEXT_PLAIN);

        }
        else if (MediaType.APPLICATION_JSON.equals(variant.getMediaType()))
        {
            // Translate known models, needs a refactoring to embed that in each resource...
            HashMap<String, Object> result = new HashMap<String, Object>();

            result.put("label", unescape(getRequest().getOriginalRef().getLastSegment(true)));

            String beforeLast =
                    getRequest().getOriginalRef().getSegments().size() > 2 ? getRequest()
                            .getResourceRef().getSegments().get(
                                    getRequest().getOriginalRef().getSegments().size() - 3) : null;
            boolean leaf = "attributes".equals(beforeLast) || "operations".equals(beforeLast);

            if (model.containsKey(ITEMS_MODEL_ATTRIBUTE) && !leaf)
            {
                Object items = model.get(ITEMS_MODEL_ATTRIBUTE);

                Collection<Object> itemCollection = null;
                if (items instanceof Collection)
                {
                    itemCollection = (Collection<Object>) items;
                }
                else
                {
                    itemCollection = Arrays.asList(items);
                }
                List<Map<String, String>> children = new ArrayList<Map<String, String>>();
                for (Object item : itemCollection)
                {

                    HashMap<String, String> ref = new HashMap<String, String>();

                    if (item instanceof MBeanAttributeInfo)
                    {
                        ref.put("$ref", encoder.encode(escape(((MBeanAttributeInfo) item).getName())) + "/");
                    }
                    else if (item instanceof Map && ((Map) item).containsKey("declaration"))
                    {
                        ref.put("$ref", ((Map) item).get("declaration").toString());
                    }
                    else
                    {
                        ref.put("$ref", encoder.encode(escape(item.toString())) + "/");
                    }
                    children.add(ref);
                }
                result.put("children", children);
            }
            else
            {
                if (model.containsKey(VALUE_MODEL_ATTRIBUTE))
                {
                    if(model.get(VALUE_MODEL_ATTRIBUTE) instanceof HtmlContent) {
                        result.put("value", "...");
                    } else {
                        result.put("value", model.get("value").toString());
                    }
                }
                else if (model.containsKey(ITEMS_MODEL_ATTRIBUTE))
                {
                    Object items = model.get(ITEMS_MODEL_ATTRIBUTE);
                    String value = null;
                    if(items.getClass().isArray()) {
                        value = Arrays.deepToString(Arrays.asList(items).toArray());
                    } else {
                        value = items.toString();
                    }
                    result.put("value", value);
                }
					 else if (model.containsKey("attributes") && model.get("attributes") instanceof CompositeData)
					 {
						 CompositeData items = (CompositeData) model.get("attributes");
						 result.put("value", items.values());
					 }
            }

            // Hack because root must be a list for dojo tree...
            if ("servers".equals(getRequest().getOriginalRef().getLastSegment(true)))
            {
                representation = new StringRepresentation(JSONSerializer.toJSON(new Object[]{result})
                        .toString(), MediaType.APPLICATION_JSON, Language.ALL, CharacterSet.UTF_8);
            }
            else
            {
                representation = new StringRepresentation(JSONSerializer.toJSON(result).toString(), MediaType.APPLICATION_JSON, Language.ALL, CharacterSet.UTF_8 );
            }
        }
        else
        {
            return null;
        }
        representation.setExpirationDate(new Date(0l));
        return representation;
    }

    protected ServerConnectionProvider getServerProvider()
    {
        return (ServerConnectionProvider) getContext().getAttributes().get(
                "serverProvider");
    }
    
    protected MBeanServerConnection getServer()
    {
        return getServerProvider().getConnection(getDecodedAttribute("server"));
    }
    
    protected String getQueryString() {
        String query = getRequest().getResourceRef().getQuery();
        return query!=null ? "?"+query : "";
    }

    protected String getDecodedAttribute(String value) {
        return encoder.decode(getAttribute(value));
    }

    public String escape(String value) {
        return value.replaceAll("/", "¦");
    }

    public String unescape(String value) {
        return value.replaceAll("¦", "/");
    }
}
