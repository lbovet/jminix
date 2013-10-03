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

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import javax.management.*;
import java.io.IOException;
import java.util.*;

public class AttributesResource extends AbstractListResource
{
   
    public AttributesResource(Context context, Request request, Response response)
    {
        super(context, request, response);
    }

    @Override
    protected String getTemplateName()
    {
        return "attributes";
    }



    @Override
    protected List<? extends Object> getList()
    {
        String domain = unescape(getAttribute("domain"));
        
        String mbean = unescape(getAttribute("mbean"));
        
        try
        {
            MBeanServerConnection server = getServer();
            
            List<MBeanAttributeInfo> result =new ArrayList<MBeanAttributeInfo>(Arrays.asList(server.getMBeanInfo(new ObjectName(domain+":"+mbean)).getAttributes()));
            
            Iterator<MBeanAttributeInfo> i = result.iterator();
            
            // Filters unreadable attributes
            while(i.hasNext()) {
                if(! i.next().isReadable()) {
                    i.remove();
                }
            }
            
            Collections.sort(result, new Comparator<MBeanAttributeInfo>() {
                public int compare(MBeanAttributeInfo o1, MBeanAttributeInfo o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }                
            });
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
        catch (InstanceNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (IntrospectionException e)
        {
            throw new RuntimeException(e);
        }
        catch (ReflectionException e)
        {
            throw new RuntimeException(e);
        }

    }

}
