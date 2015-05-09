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

package org.jminix.server;

import org.springframework.web.context.ContextLoader;

/**
 * Uses MBean servers from the Spring web application context of the current web application. <br>
 * By default, returns one server, the bean named "mbeanServer". <br>
 * In future release, it will be configurable to provide other server beans from the bean factory.
 * 
 * @author Laurent Bovet (lbovet@jminix.org)
 * @since 0.8
 */
public class WebSpringServerConnectionProvider extends SpringServerConnectionProvider
{

    public WebSpringServerConnectionProvider() 
    {
        setBeanFactory(ContextLoader.getCurrentWebApplicationContext());
        setServerBeanNames(new String[] { "mbeanServer" });
    }

}
