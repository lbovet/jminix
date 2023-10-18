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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jminix.exception.JMinixRuntimeException;

public class DomainsResource extends AbstractListResource {

  @Override
  protected List<String> getList() {
    try {
      List<String> result = Arrays.asList(getServer().getDomains());
      for (int i = 0; i < result.size(); i++) {
        result.set(i, escape(result.get(i)));
      }
      Collections.sort(result);
      return result;
    } catch (IOException e) {
      throw new JMinixRuntimeException(e);
    }
  }
}
