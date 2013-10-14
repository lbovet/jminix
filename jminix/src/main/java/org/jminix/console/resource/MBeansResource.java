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

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MBeansResource extends AbstractListResource
{
   
    @Override
    protected List<String> getList()
    {        
        String domain = unescape(getDecodedAttribute("domain"));
        
        try
        {
            MBeanServerConnection server = getServer();
            
            List<ObjectName> names = new ArrayList<ObjectName>(server.queryNames(new ObjectName(domain+":*"), null));            

            List<String> result = new ArrayList<String>();
            
            for(Object name : names) {
                result.add(escape(name.toString().substring(domain.length() + 1)));
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
