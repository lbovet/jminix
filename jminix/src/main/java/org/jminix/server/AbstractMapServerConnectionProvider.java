/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;

/**
 * Superclass for connection providers providing maps.
 *
 * @author bovetl
 * @version $Revision$
 * @see <script>links('$HeadURL$');</script>
 */
public abstract class AbstractMapServerConnectionProvider implements ServerConnectionProvider {

	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.ServerConnectionProvider#getConnections()
	 */
	public List<MBeanServerConnection> getConnections() {
		List<MBeanServerConnection> result = new ArrayList<MBeanServerConnection>();
		for(String key: getConnectionKeys()) {
			result.add(getConnection(key));
		}
		return result;
	}

}
