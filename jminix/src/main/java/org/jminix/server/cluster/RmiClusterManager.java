/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.server.cluster;

import org.jminix.server.CredentialsHolder;
import org.jminix.server.RmiServerConnectionProvider;
import org.jminix.server.ServerConnectionProvider;

/**
 * A cluster manager managing {{@link RmiServerConnectionProvider} instances. Default registry port is 1099.
 *
 * @author bovetl
 * @version $Revision$
 * @see <script>links('$HeadURL$');</script>
 */
public class RmiClusterManager extends ClusterManager {

	private CredentialsHolder credentials;
	
	public RmiClusterManager() {
		setUrlPattern("service:jmx:rmi://{0}/jndi/rmi://{0}:{1,number,#}/rmi");
		setPort(1099);
	}
	
	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.cluster.ClusterManager#createNodeProvider(org.jminix.server.cluster.ClusterManager.Node)
	 */
	@Override
	protected ServerConnectionProvider createNodeProvider(Node node) {
		RmiServerConnectionProvider nodeProvider = new RmiServerConnectionProvider();
		if(credentials != null) {
			nodeProvider.setUsername(credentials.getUsername());
			nodeProvider.setPassword(credentials.getPassword());
		}
		nodeProvider.setServiceUrl(node.url);
		return nodeProvider;
	}

	/**
	 * The credential source to authenticate against other members (all must use the same credentials).
	 * Also automatically set the ClusterManager secret to the provided password.
	 */
	public void setCredentialsHolder(CredentialsHolder credentials) {
		this.credentials = credentials;
		setSecret(credentials.getPassword());
	}	
}
