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

package org.jminix.console.servlet;

import org.jminix.console.application.MiniConsoleApplication;
import org.jminix.console.resource.ValueParser;
import org.jminix.server.ServerConnectionProvider;
import org.jminix.type.AttributeFilter;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.engine.util.ChildContext;
import org.restlet.ext.servlet.ServerServlet;

import javax.servlet.ServletException;

public class MiniConsoleServlet extends ServerServlet
{
    private static final long serialVersionUID = 1L;
    
    transient MiniConsoleApplication app;
    
    @Override
    protected Application createApplication(Context parentContext)
    {
        app.setContext(new ChildContext(parentContext));
        return app;
    }

    
    @Override
    public void init() throws ServletException
    {
        app = new MiniConsoleApplication();
        
        String providerClassName = getInitParameter("serverConnectionProvider");        
        String stringArraySeparator = getInitParameter("stringArraySeparator");
        if(stringArraySeparator != null) {
        	ValueParser.setStringArraySeparator(stringArraySeparator);
        }
        
        if(providerClassName != null) {

            try
            {
                @SuppressWarnings("unchecked")
                Class<ServerConnectionProvider> providerClass = (Class<ServerConnectionProvider>) loadClass(providerClassName);
                app.setServerConnectionProvider(providerClass.newInstance());
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            
        }            
        
        String attributeFilterClassName = getInitParameter("attributeFilter");        
        
        if(attributeFilterClassName != null) {

            try
            {
                @SuppressWarnings("unchecked")
                Class<AttributeFilter> attributeFilter = (Class<AttributeFilter>) loadClass(attributeFilterClassName);
                app.setAttributeFilter(attributeFilter.newInstance());
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            
        }             
        
        super.init();
        
        getComponent().getClients().add(Protocol.CLAP);
    }
    
    
    
}
