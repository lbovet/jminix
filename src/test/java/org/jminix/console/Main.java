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

package org.jminix.console;

import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.jminix.console.tool.StandaloneMiniConsole;

public class Main {
  public static final void main(String[] args)
      throws MalformedObjectNameException, InstanceAlreadyExistsException,
          MBeanRegistrationException, NotCompliantMBeanException {

    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    ObjectName name = new ObjectName("org.jminix.console:type=JMiniXStuff");
    mbs.registerMBean(new JMiniXStuff(), name);

    new StandaloneMiniConsole(8088);
  }
}
