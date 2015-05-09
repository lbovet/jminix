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

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Gives access to MBean servers defined as Spring beans.
 * 
 * @author Laurent Bovet (lbovet@jminix.org)
 * @since 0.8
 */
public class SpringServerConnectionProvider extends AbstractListServerConnectionProvider implements BeanFactoryAware
{
    BeanFactory beanFactory;        
    String[] names;
    
    public List<MBeanServerConnection> getConnections()
    {
        List<MBeanServerConnection> servers = new ArrayList<MBeanServerConnection>();
        for(String name: names) {
            servers.add((MBeanServerConnection)beanFactory.getBean(name));
        }
        return servers;
    }

    /**
     * @param names the name(s) of Spring beans refering to MBean servers.
     */
    public void setServerBeanNames(String[] names) 
    {
        this.names = names;
    }
    
    /**
     * The Bean factory to get the MBean server from.
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }
    
}
