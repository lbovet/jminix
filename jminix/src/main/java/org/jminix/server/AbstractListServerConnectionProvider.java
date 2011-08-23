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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;

/**
 * Superclass for connection providers not providing server names.
 *
 * @author bovetl
 * @version $Revision$
 * @see <script>links('$HeadURL$');</script>
 */
public abstract class AbstractListServerConnectionProvider implements ServerConnectionProvider {

	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.ServerConnectionProvider#getConnectionKeys()
	 */
	public List<String> getConnectionKeys() {
		List<String>  result = new ArrayList<String>();
		for(int i=0; i<getConnections().size(); i++) {
			result.add(Integer.toString(i));
		}
		return result;
	}

	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.ServerConnectionProvider#getConnection(java.lang.String)
	 */
	public MBeanServerConnection getConnection(String name) {
		int i=0;
		try {
			i = Integer.parseInt(name);
		} catch(NumberFormatException e) {
			return null;
		}
		return getConnections().get(i);
	}

}
