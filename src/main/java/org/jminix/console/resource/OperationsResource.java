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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.jminix.exception.JMinixRuntimeException;

public class OperationsResource extends AbstractListResource {

  @Override
  protected String getTemplateName() {
    return "operations";
  }

  @Override
  protected List<Map<String, Object>> getList() {
    String domain = unescape(getDecodedAttribute("domain"));

    String mbean = unescape(getDecodedAttribute("mbean"));

    try {
      MBeanServerConnection server = getServer();

      List<Map<String, Object>> result = new ArrayList<>();

      for (MBeanOperationInfo info :
          server.getMBeanInfo(new ObjectName(domain + ":" + mbean)).getOperations()) {

        // Skip getters
        Object role = info.getDescriptor().getFieldValue("role");
        if (role == null && info.getName().startsWith("get") && info.getSignature().length == 0
            || "getter".equals(role)) {
          continue;
        }

        StringBuilder sb = new StringBuilder(info.getName() + "(");
        boolean first = true;
        for (MBeanParameterInfo p : info.getSignature()) {
          if (!first) {
            sb.append(",");
          } else {
            first = false;
          }
          sb.append(p.getType());
        }
        sb.append(")");

        Map<String, Object> pair = new HashMap<>();
        pair.put("operation", info);
        pair.put("declaration", sb.toString());
        result.add(pair);
      }

      Collections.sort(
          result,
          (o1, o2) -> ((String) o1.get("declaration")).compareTo((String) o2.get("declaration")));

      return result;

    } catch (IOException | MalformedObjectNameException | InstanceNotFoundException | IntrospectionException | ReflectionException e) {
      throw new JMinixRuntimeException(e);
    }    
  }
}
