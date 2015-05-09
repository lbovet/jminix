/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.server;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;

/**
 * Gathers multiple connections from different server connection providers.
 *
 * @author bovetl
 * @version $Revision$
 * @see <script>links('$HeadURL$');</script>
 */
public class ConfigurableServerConnectionProvider extends AbstractMapServerConnectionProvider {

	private Map<String, ServerConnectionProvider> providerMap = new HashMap<String, ServerConnectionProvider>();
	private List<String> providerKeys = new ArrayList<String>();
	private String keyFormat="{0}-{1}"; 
	
	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.ServerConnectionProvider#getConnectionKeys()
	 */
	public List<String> getConnectionKeys() {
		List<String> result = new ArrayList<String>();
		for(String providerKey: providerKeys) {
			for(String connectionKey: providerMap.get(providerKey).getConnectionKeys()) {
				result.add(MessageFormat.format(keyFormat, providerKey, connectionKey));
			}
		}
		return result;
	}

	/**
	 * @return the keys of registered providers.
	 */
	public List<String> getProviderKeys() {
		return providerKeys;
	}
	
	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.ServerConnectionProvider#getConnection(java.lang.String)
	 */
	public MBeanServerConnection getConnection(String name) {
		Object[] keys;
		try {
			keys = new MessageFormat(keyFormat).parse(name);
		} catch (ParseException e) {
			return null;
		}
		ServerConnectionProvider provider = providerMap.get(keys[0].toString());
		if(provider==null) {
			throw new RuntimeException("No connection named '"+name+"'");
		}
		return provider.getConnection(keys[1].toString());		
	}

	/**
	 * @param format
	 *            a MessageFormat pattern to generate the key name from the server connection provider key {0} and the
	 *            connection key {1}. Defaults to {0}-{1}.
	 */
	public void setKeyFormat(String format) {
		keyFormat = format;
	}

	public synchronized void setServerConnectionProviders(Map<String,ServerConnectionProvider> providers) {
		providerMap = providers;
		providerKeys = new ArrayList<String>(providers.keySet());
		Collections.sort(providerKeys);
	}
	
	public synchronized void addServerConnectionProvider(String key, ServerConnectionProvider provider) {
		providerMap.put(key, provider);
		providerKeys.add(key);
		Collections.sort(providerKeys);
	}
	
	public synchronized void removeServerConnectionProvider(String key) {
		providerMap.remove(key);
		providerKeys.remove(key);
	}
}
