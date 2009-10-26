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

package org.jminix.console.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Directory;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.StreamRepresentation;

import org.jminix.console.resource.AttributeResource;
import org.jminix.console.resource.AttributesResource;
import org.jminix.console.resource.DomainResource;
import org.jminix.console.resource.DomainsResource;
import org.jminix.console.resource.MBeanResource;
import org.jminix.console.resource.MBeansResource;
import org.jminix.console.resource.OperationResource;
import org.jminix.console.resource.OperationsResource;
import org.jminix.console.resource.ServerResource;
import org.jminix.console.resource.ServersResource;
import org.jminix.server.DefaultLocalServerConnectionProvider;
import org.jminix.server.ServerConnectionProvider;

public class MiniConsoleApplication extends Application
{    
    {
        configureLog(null);
    }
    
    ServerConnectionProvider serverConnectionProvider = new DefaultLocalServerConnectionProvider();
    
    @Override
    public Restlet createRoot()
    {        
        configureLog(getContext());
        
        getConnectorService().getClientProtocols().add(Protocol.CLAP);
        
        getContext().getAttributes().put("serverProvider", serverConnectionProvider);
        
        final Directory jsDirectory = new Directory(getContext(),
                "clap://class/jminix/js");
        
        Router router = new Router(getContext());
              
        router.attach("/js", jsDirectory);
        router.attach("/servers/",ServersResource.class);
        router.attach("/servers/{server}", ServerResource.class);
        router.attach("/servers/{server}/domains", DomainsResource.class);          
        router.attach("/servers/{server}/domains/{domain}", DomainResource.class);
        router.attach("/servers/{server}/domains/{domain}/mbeans", MBeansResource.class);
        router.attach("/servers/{server}/domains/{domain}/mbeans/{mbean}", MBeanResource.class);
        router.attach("/servers/{server}/domains/{domain}/mbeans/{mbean}/attributes", AttributesResource.class);
        router.attach("/servers/{server}/domains/{domain}/mbeans/{mbean}/attributes/{attribute}", AttributeResource.class);
        router.attach("/servers/{server}/domains/{domain}/mbeans/{mbean}/operations", OperationsResource.class);
        router.attach("/servers/{server}/domains/{domain}/mbeans/{mbean}/operations/{operation}", OperationResource.class);        
        
        // Very ugly way to provide the index.html in a reliable way... I did not figure how to do
        // it properly.
        router.attach("/", new Restlet() {

            @Override
            public void handle(Request request, Response response)
            {
                response.setEntity(new StreamRepresentation(MediaType.TEXT_HTML) {
                    
                    @Override
                    public InputStream getStream() throws IOException
                    {                    
                        return null;
                    }

                    @Override
                    public void write(OutputStream outputStream) throws IOException
                    {
                        InputStream in = getClass().getClassLoader().getResource("jminix/console/index.html").openStream();
                        while(in.available()>0) {
                            byte buffer[] = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                                outputStream.flush();
                            }
                        }
                    } 
                    
                });
            }
            
        });
        
        return router;
    }

    public void setServerConnectionProvider(ServerConnectionProvider serverConnectionProvider)
    {
        this.serverConnectionProvider = serverConnectionProvider;
    }
    
    private static void configureLog(Context context) {
        if(! "true".equals(System.getProperty("common.jmx.show.restlet.log"))) {
            java.util.logging.Logger.getLogger("org.restlet").setLevel(java.util.logging.Level.SEVERE);
            if(context!=null) {
                context.getLogger().setLevel(java.util.logging.Level.SEVERE);
            }
        }
    }
    
}
