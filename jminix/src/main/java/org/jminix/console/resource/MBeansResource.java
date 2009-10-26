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
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

public class MBeansResource extends AbstractListResource
{
   
    public MBeansResource(Context context, Request request, Response response)
    {
        super(context, request, response);
    }

    @Override
    protected List<? extends Object> getList()
    {
        int serverIndex = Integer.parseInt(getRequest().getAttributes().get("server").toString()); 
        
        String domain = getRequest().getAttributes().get("domain").toString();
        
        try
        {
            MBeanServerConnection server = getServer(serverIndex);
            
            List<ObjectName> names = new ArrayList<ObjectName>(server.queryNames(new ObjectName(domain+":*"), null));            

            List<String> result = new ArrayList<String>();
            
            for(Object name : names) {
                result.add(name.toString().substring(domain.length()+1));
            }
            Collections.sort(result);
            
            return result;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (MalformedObjectNameException e)
        {
            throw new RuntimeException(e);
        }
    }

}
