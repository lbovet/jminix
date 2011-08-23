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
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

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
	
	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.ServerConnectionProvider#getConnectionKeys()
	 */
	public List<String> getConnectionKeys() {
		String[] parts = serviceUrl.split("/");
		return Arrays.asList(new String[] { parts[parts.length-1] });		
	}

	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.ServerConnectionProvider#getConnection(java.lang.String)
	 */
	public MBeanServerConnection getConnection(String name) {
		
		JMXServiceURL url;
		try {
			url = new JMXServiceURL(serviceUrl);

			JMXConnector jmxc;
			if(username != null && password != null) {
				String[] creds = {username, password};
				Map<String,Object> env = new HashMap<String,Object>();
				env.put(JMXConnector.CREDENTIALS, creds);
				jmxc = JMXConnectorFactory.connect(url, env);
			} else {			
				jmxc = JMXConnectorFactory.connect(url, null);
			}
            return jmxc.getMBeanServerConnection();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	} 

	/**
	 * Sets the attribute serviceUrl.
	 * @param serviceUrl The serviceUrl to set.
	 */
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	/**
	 * Sets the attribute username.
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Sets the attribute password.
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
