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

import java.util.List;
import javax.management.MBeanServerConnection;

/**
 * Interface for providing mbeans server connections to various client like the miniconsole.
 *
 * @author Laurent Bovet (lbovet@jminix.org)
 * @since 0.8
 */
public interface ServerConnectionProvider {
  /**
   * List of connections. The clients are not intended to cache them, but query them before each
   * use. The ordering is guaranteed to be unchanged over time (e.g. after reboot).
   *
   * @return the list of connections.
   */
  public List<MBeanServerConnection> getConnections();

  /**
   * @return the names of connections. They can be simple numeric identifiers. They are guarantee
   *     not to change over time (e.g. after reboot), though.
   */
  public List<String> getConnectionKeys();

  /**
   * Return a connection by name.
   *
   * @param name key as returned by {@link #getConnectionKeys()}.
   * @return the connection or null if not found.
   */
  public MBeanServerConnection getConnection(String name);
}
