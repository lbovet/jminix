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

import javax.servlet.ServletException;

import org.jminix.console.application.MiniConsoleApplication;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.noelios.restlet.ext.servlet.ServerServlet;
import com.noelios.restlet.ext.servlet.ServletContextAdapter;

/**
 * MiniConsole servlet getting the MiniConsoleApplication from the Spring
 * WebApplicationContext. Useful for customizing the or creating the
 * ServerConnectionProvider as a Spring bean.
 * <p>
 * Specify the bean name of the MiniConsoleApplication in the servlet parameter
 * <code>applicationBean</code>. The default bean name is
 * <code>miniConsoleApplication</code>
 * 
 * @author laurent.bovet
 * @since 0.9
 * 
 */
public class SpringMiniConsoleServlet extends ServerServlet
{
    private static final long serialVersionUID = 1L;
    
    transient MiniConsoleApplication app;
    
    @Override
    protected Application createApplication(Context parentContext)
    {
        app.setContext(new ServletContextAdapter(this,parentContext));
        return app;
    }
    
    @Override
    public void init() throws ServletException
    {       
        String applicationBeanName = getInitParameter("applicationBean");
        if(applicationBeanName == null) {
        	applicationBeanName = "miniConsoleApplication";
        }
        
        ApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        
        app = (MiniConsoleApplication)ac.getBean(applicationBeanName);          
        
        super.init();
        
        getComponent().getClients().add(Protocol.CLAP);
    }
    
    
    
}
