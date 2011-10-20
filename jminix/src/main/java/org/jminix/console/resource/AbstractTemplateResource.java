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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;

import net.sf.json.JSONSerializer;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import org.jminix.server.ServerConnectionProvider;
import org.jminix.type.HtmlContent;

public abstract class AbstractTemplateResource extends Resource
{
	public String a;
	
    private final static String VELOCITY_ENGINE_CONTEX_KEY = "template.resource.velocity.engine";

    public AbstractTemplateResource(Context context, Request request, Response response)
    {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_HTML));
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));

        VelocityEngine ve =
                (VelocityEngine) context.getAttributes().get(VELOCITY_ENGINE_CONTEX_KEY);

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
            
            context.getAttributes().put(VELOCITY_ENGINE_CONTEX_KEY, ve);
        }
    }

    protected abstract String getTemplateName();

    protected abstract Map<String, Object> getModel();

    @Override
    public Representation represent(Variant variant) throws ResourceException
    {

        // To avoid IE caching causing conflicts between JSON and HTML representations in ajax
        // console
        Form responseHeaders =
                (Form) getResponse().getAttributes().get("org.restlet.http.headers");
        if (responseHeaders == null)
        {
            responseHeaders = new Form();
            getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);
        }

        responseHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate");
        responseHeaders.add("Cache-Control", "post-check=0, pre-check=0");
        responseHeaders.add("Pragma", "no-cache");
        responseHeaders.add("Expires", "0");

        if (MediaType.TEXT_HTML.equals(variant.getMediaType()))
        {

            Map<String, Object> enrichedModel = new HashMap<String, Object>(getModel());

            String templateName = getTemplateName();
            if(enrichedModel.get("value") instanceof HtmlContent) {
            	templateName = "html-attribute";
            }
            
            Template template;
            try
            {
                VelocityEngine ve =
                        (VelocityEngine) getContext().getAttributes().get(
                                VELOCITY_ENGINE_CONTEX_KEY);

                template = ve.getTemplate("jminix/templates/" + templateName + ".vm");
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

            return new TemplateRepresentation(template, enrichedModel, MediaType.TEXT_HTML);

        }
        else if (MediaType.TEXT_PLAIN.equals(variant.getMediaType()))
        {

            Map<String, Object> enrichedModel = new HashMap<String, Object>(getModel());

            Template template;
            try
            {
                VelocityEngine ve = new VelocityEngine();

                Properties p = new Properties();
                p.setProperty("resource.loader", "class");
                p.setProperty("class.resource.loader.description",
                        "Velocity Classpath Resource Loader");
                p.setProperty("class.resource.loader.class",
                        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

                ve.init(p);

                template =
                        ve.getTemplate("jminix/templates/"
                                + getTemplateName() + "-plain.vm");
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            enrichedModel.put("encoder", new EncoderBean());
            enrichedModel.put("request", getRequest());

            return new TemplateRepresentation(template, enrichedModel, MediaType.TEXT_PLAIN);

        }
        else if (MediaType.APPLICATION_JSON.equals(variant.getMediaType()))
        {
            // Translate known models, needs a refactoring to embed that in each resource...
            HashMap<String, Object> result = new HashMap<String, Object>();

            result.put("label", getRequest().getOriginalRef().getLastSegment(true));

            String beforeLast =
                    getRequest().getOriginalRef().getSegments().size() > 2 ? getRequest()
                            .getResourceRef().getSegments().get(
                                    getRequest().getOriginalRef().getSegments().size() - 3) : null;
            boolean leaf = "attributes".equals(beforeLast) || "operations".equals(beforeLast);

            if (getModel().containsKey("items") && !leaf)
            {
                Object items = getModel().get("items");

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
                        ref.put("$ref", new EncoderBean().encode(((MBeanAttributeInfo) item).getName()) + "/");
                    }
                    else if (item instanceof Map && ((Map) item).containsKey("declaration"))
                    {
                        ref.put("$ref", ((Map) item).get("declaration").toString());
                    }
                    else
                    {
                        ref.put("$ref", item.toString() + "/");
                    }
                    children.add(ref);
                }
                result.put("children", children);
            }
            else
            {
                if (getModel().containsKey("value"))
                {
                	if(getModel().get("value") instanceof HtmlContent) {
                		result.put("value", "...");
                	} else {
                		result.put("value", getModel().get("value").toString());
                	}
                }
                else if (getModel().containsKey("items"))
                {
                	Object items = getModel().get("items");
                	String value = null;
                	if(items.getClass().isArray()) {
                		value = Arrays.deepToString((Object[])items);
                	} else {
                		value = items.toString();
                	}
                    result.put("value", value);
                }
            }

            // Hack because root must be a list for dojo tree...
            if ("servers".equals(getRequest().getOriginalRef().getLastSegment(true)))
            {
                return new StringRepresentation(JSONSerializer.toJSON(new Object[]{result})
                        .toString());
            }
            else
            {
                return new StringRepresentation(JSONSerializer.toJSON(result).toString());
            }
        }
        else
        {
            return null;
        }
    }

    protected ServerConnectionProvider getServerProvider()
    {
        return (ServerConnectionProvider) getContext().getAttributes().get(
                "serverProvider");
    }
    
    protected MBeanServerConnection getServer()
    {
        return getServerProvider().getConnection(getRequest().getAttributes().get("server").toString());
    }
    
    protected String getQueryString() {
    	String query = getRequest().getResourceRef().getQuery();
    	return query!=null ? "?"+query : "";
    }
}
