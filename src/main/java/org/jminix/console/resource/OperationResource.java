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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import net.sf.json.JSONSerializer;
import org.jminix.exception.JMinixRuntimeException;
import org.jminix.type.HtmlContent;
import org.jminix.type.InputStreamContent;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

public class OperationResource extends AbstractTemplateResource {

  private String templateName = "operation";

  @Override
  public Map<String, Object> getModel() {
    String domain = unescape(getDecodedAttribute("domain"));

    String mbean = unescape(getDecodedAttribute("mbean"));

    String declaration = getDecodedAttribute("operation");

    String operation = declaration.split("\\(")[0];

    String signature =
        declaration.split("\\(").length > 1
            ? (declaration.split("\\(")[1].split("\\)").length > 0
                ? declaration.split("\\(")[1].split("\\)")[0]
                : "")
            : "";

    Map<String, Object> model = new HashMap<>();

    model.put("operation", getOperation(getServer(), domain, mbean, operation, signature));

    return model;
  }

  @Post("*:txt|html|json")
  public Representation execute(Representation entity) throws ResourceException {
    String[] stringParams = getStringParams(entity);

    String domain = unescape(getDecodedAttribute("domain"));

    String mbean = unescape(getDecodedAttribute("mbean"));

    String declaration = getDecodedAttribute("operation");

    String operation = declaration.split("\\(")[0];

    String[] signature =
        declaration.split("\\(").length > 1
            ? (declaration.split("\\(")[1].split("\\)").length > 0
                ? declaration.split("\\(")[1].split("\\)")[0].split(",")
                : new String[] {})
            : new String[] {};

    MBeanServerConnection server = getServer();

    try {
      Object[] params = new Object[signature.length];

      ValueParser parser = new ValueParser();
      for (int i = 0; i < stringParams.length; i++) {
        params[i] = parser.parse(stringParams[i], signature[i]);
      }

      Object result =
          server.invoke(new ObjectName(domain + ":" + mbean), operation, params, signature);

      if (result != null) {
        Variant variant = getPreferredVariant(getVariants());
        if (MediaType.APPLICATION_JSON == variant.getMediaType()) {
          return new StringRepresentation(
              JSONSerializer.toJSON(result).toString(),
              MediaType.APPLICATION_JSON,
              Language.ALL,
              CharacterSet.UTF_8);
        } else if (result instanceof InputStreamContent) {
          return new InputRepresentation(
              (InputStreamContent) result, MediaType.APPLICATION_OCTET_STREAM);
        } else {
          return new StringRepresentation(
              result.toString(),
              result instanceof HtmlContent ? MediaType.TEXT_HTML : MediaType.TEXT_PLAIN,
              Language.ALL,
              CharacterSet.UTF_8);
        }
      } else {
        String queryString = getQueryString();
        if (!queryString.contains("ok=1")) {
          if ("".equals(queryString)) {
            queryString = "?";
          } else {
            queryString += "&";
          }
          queryString += "ok=1";
        }
        redirectPermanent(encoder.encode(declaration) + queryString);
        return null;
      }
    } catch (InstanceNotFoundException | MalformedObjectNameException | MBeanException | ReflectionException | IOException e) {
      throw new JMinixRuntimeException(e);
    }    
  }

  private String[] getStringParams(Representation entity) {
    String[] stringParams = new Form(entity).getValuesArray("param");

    if (stringParams == null || isAllNulls(stringParams)) {
      stringParams = ServletUtils.getRequest(getRequest()).getParameterValues("param");
    }

    return stringParams != null ? stringParams : new String[0];
  }

  @Override
  protected String getTemplateName() {
    return templateName;
  }

  private MBeanOperationInfo getOperation(
      MBeanServerConnection server,
      String domain,
      String mbean,
      String operationName,
      String signature) {
    try {

      for (MBeanOperationInfo i :
          server.getMBeanInfo(new ObjectName(domain + ":" + mbean)).getOperations()) {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (MBeanParameterInfo p : i.getSignature()) {
          if (!first) {
            sb.append(",");
          } else {
            first = false;
          }
          sb.append(p.getType());
        }

        if (i.getName().equals(operationName)) {
          if (sb.toString().equals(signature)) {
            return i;
          }
        }
      }
      return null;
    } catch (IOException | InstanceNotFoundException | MalformedObjectNameException | ReflectionException | IntrospectionException e) {
      throw new JMinixRuntimeException(e);
    }    
  }

  private boolean isAllNulls(String[] array) {
    return Arrays.stream(array).allMatch(Objects::isNull);
  }
}
