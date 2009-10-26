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

package org.jminix.console.tool;

import org.restlet.Component;
import org.restlet.data.Protocol;

import org.jminix.console.application.MiniConsoleApplication;

/**
 * Runs the Mini Console standalone without servlet container.
 *
 * @author Laurent Bovet (lbovet@jminix.org)
 * @since 0.8
 */
public class StandaloneMiniConsole
{
    private Component component=null;
    
    /**
     * @param port the listening HTTP port
     */
    public StandaloneMiniConsole(int port) {
        // Create a new Component.
         component = new Component();
        component.getClients().add(Protocol.CLAP);
        // Add a new HTTP server
        component.getServers().add(Protocol.HTTP, port);

        // Attach the sample application.
        component.getDefaultHost().attach(new MiniConsoleApplication());

        // Start the component.
        try
        {
            component.start();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Stops the Mini Console and frees the resources.
     */
    public void shutdown() {
        try {
            component.stop();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Runs as main, mostly for test purposes...
     * 
     * @param args if present, first args is the port. Defaults to 8181.
     */
    public static void main(String[] args)
    {
        int port=8181;
        if(args.length>0) {
            port=new Integer(args[0]);
        }
        new StandaloneMiniConsole(port);
    }
}
