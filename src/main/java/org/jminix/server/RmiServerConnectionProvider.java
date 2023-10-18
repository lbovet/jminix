/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 *
 */

package org.jminix.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.jminix.exception.JMinixRuntimeException;

/**
 * A connection provider browsing serve
 *
 * @author bovetl
 * @version $Revision$
 * @see <script>links('$HeadURL$');</script>
 */
public class RmiServerConnectionProvider extends AbstractMapServerConnectionProvider {

  private String serviceUrl;

  private String username;

  private String password;

  private Map<String, JMXConnector> jmxcs = new HashMap<>();

  /**
   * Not explicitly documented.
   *
   * @see org.jminix.server.ServerConnectionProvider#getConnectionKeys()
   */
  @Override
  public List<String> getConnectionKeys() {
    String[] parts = serviceUrl.split("/");
    return Arrays.asList(parts[parts.length - 1]);
  }

  /**
   * Not explicitly documented.
   *
   * @see org.jminix.server.ServerConnectionProvider#getConnection(java.lang.String)
   */
  @Override
  public MBeanServerConnection getConnection(String name) {

    JMXServiceURL url;
    try {
      url = new JMXServiceURL(serviceUrl);

      JMXConnector jmxc = jmxcs.get(serviceUrl);

      if (jmxc == null) {
        if (username != null && password != null) {
          String[] creds = {username, password};
          Map<String, Object> env = new HashMap<>();
          env.put(JMXConnector.CREDENTIALS, creds);
          jmxc = JMXConnectorFactory.connect(url, env);
        } else {
          jmxc = JMXConnectorFactory.connect(url, null);
        }
        jmxcs.put(serviceUrl, jmxc);
      } else {
        jmxc.connect();
      }
      return jmxc.getMBeanServerConnection();
    } catch (IOException e) {
      throw new JMinixRuntimeException(e);
    } 
  }

  /**
   * Sets the attribute serviceUrl.
   *
   * @param serviceUrl The serviceUrl to set.
   */
  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  /**
   * Sets the attribute username.
   *
   * @param username The username to set.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Sets the attribute password.
   *
   * @param password The password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
