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
import java.util.List;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.jminix.exception.JMinixRuntimeException;

public class MBeanResource extends AbstractListResource {

  @Override
  protected List<Object> getList() {
    List<Object> result = new ArrayList<>();

    String domain = unescape(getDecodedAttribute("domain"));

    String mbean = unescape(getDecodedAttribute("mbean"));

    MBeanServerConnection server = getServer();

    try {
      if (server.getMBeanInfo(new ObjectName(domain + ":" + mbean)).getAttributes().length > 0) {
        result.add("attributes");
      }

      if (server.getMBeanInfo(new ObjectName(domain + ":" + mbean)).getOperations().length > 0) {
        result.add("operations");
      }
    } catch (InstanceNotFoundException | IntrospectionException | MalformedObjectNameException | ReflectionException | NullPointerException | IOException e) {
      throw new JMinixRuntimeException(e);
    }     

    return result;
  }
}
