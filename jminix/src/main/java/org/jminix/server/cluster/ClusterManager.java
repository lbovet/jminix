/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.server.cluster;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.binary.BasicBinaryEncryptor;
import org.jasypt.util.binary.BinaryEncryptor;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.jminix.server.ConfigurableServerConnectionProvider;
import org.jminix.server.ServerConnectionProvider;

/**
 * Maintains clustered connection providers inside  a configurable server connection provider using JGroups clustering.
 * 
 * @author bovetl
 * @version $Revision$
 * @see <script>links('$HeadURL$');</script>
 */
public abstract class ClusterManager extends ReceiverAdapter {

	private ConfigurableServerConnectionProvider provider;
	
	protected String clusterName;
	 
	private Channel channel;
	
	private boolean ipv6 = false;
	
	private BinaryEncryptor encryptor;
	
	protected String urlPattern;
	
	protected String host;
	
	protected int port;
	
	protected String nodeName;	
	
	
	private Map<Address, Node> nodes = Collections.synchronizedMap(new HashMap<Address, Node>());
	
	private final static Log log =  LogFactory.getLog(ClusterManager.class);
	
	@SuppressWarnings("serial")
	protected static class Node implements Serializable {
		String name;
		String url;
		public String toString() { return name +" ("+url+")"; }
	}
	
	public void start() {		
		try {			
			if(provider == null) {
				throw new IllegalStateException("Property 'provider' must be set");
			}
			if(clusterName == null) {
				throw new IllegalStateException("Property 'clusterName' must be set");
			}
			
			if(!ipv6) {
				System.setProperty("java.net.preferIPv4Stack","true");
			}
			
			if(channel == null) {
				channel = new JChannel();
			}
			channel.setReceiver(this);
			log.debug("Connecting to cluster "+clusterName);
			channel.connect(clusterName);
			// Send node information to others
			channel.send(new Message(null, null, encrypt(thisNode())));
			// Get node information from others
			channel.getState(null, 2000);
			
		} catch (ChannelException e) {
			throw new RuntimeException(e);
		}        
	}

	/**
	 * @return the local node
	 */
	protected Node thisNode() {
		Node node = new Node();		
		if(host==null) {
			try {
				host = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		}
		if(nodeName==null) {
			nodeName = host;
			node.name = "node."+nodeName;
		} else {
			node.name = "node."+nodeName+"."+host;
		}
		
		node.url = formatUrl();
		return node;
	}

	protected String formatUrl() {
		return MessageFormat.format(urlPattern, host, port, nodeName);
	}	
	
	/**
	 * Returns the service url. Useful to configure the connector.
	 */
	public String getLocalUrl() {
		return thisNode().url;
	}
	
	public void close() {
		channel.close();
	}

	/**
	 * Not explicitly documented.
	 * @see org.jgroups.ReceiverAdapter#viewAccepted(org.jgroups.View)
	 */
	@Override
	public void viewAccepted(View view) {	
		// Cluster has changed. Delete the members that have disappeared.
		nodes.keySet().retainAll(view.getMembers());
		updateProvider();
	}

	/**
	 * Not explicitly documented.
	 * @see org.jgroups.ReceiverAdapter#receive(org.jgroups.Message)
	 */
	@Override
	public void receive(Message message) {
		// A new node appeared
		Object o = decrypt(message.getBuffer());
		if(o instanceof Node) {
			log.debug("Received node "+o);
			nodes.put(message.getSrc(), (Node)o);			
			updateProvider();
		}		
	}
		
	private byte[] encrypt(Node node) {
		byte[] message;
		try {
			message = Util.objectToByteBuffer(node);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		if(encryptor != null) {
			message = encryptor.encrypt(message);
		}
		return message;
	}


	private Object decrypt(byte[] buffer) {
		if(encryptor != null) {
			try {
				buffer = encryptor.decrypt(buffer);
			} catch(EncryptionOperationNotPossibleException e) {
				log.debug("Could not decrypt:", e);
				return null;
			}
		}
		try {
			return Util.objectFromByteBuffer(buffer);
		} catch (Exception e) {
			log.debug("Could not deserialize:", e);
			return null;
		}			
	}

	/**
	 * Not explicitly documented.
	 * @see org.jgroups.ReceiverAdapter#getState()
	 */
	@Override
	public byte[] getState() {
		try {
			return Util.objectToByteBuffer(nodes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Not explicitly documented.
	 * @see org.jgroups.ReceiverAdapter#setState(byte[])
	 */
	@Override
	public void setState(byte[] buffer) {
		Object o;
		try {
			o = Util.objectFromByteBuffer(buffer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if(o instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<Address,Node> nodeFromOthers = (Map<Address,Node>)o;
			nodes.putAll(nodeFromOthers);
		}
		updateProvider();
	}

	private synchronized void updateProvider() {
		log.debug("Updating providers");
		List<String> currentKeys = provider.getProviderKeys();	
		Map<String,Node> actualNodes = new HashMap<String, Node>();
		for(Node node: nodes.values()) {
			actualNodes.put(node.name, node);			
		}
		log.debug("Current providers: "+currentKeys);
		log.debug("Actual providers: "+actualNodes.keySet());
		Set<String> newKeys = new HashSet<String>(actualNodes.keySet());
		newKeys.removeAll(currentKeys);
		Set<String> removedKeys = new HashSet<String>(currentKeys);
		removedKeys.removeAll(actualNodes.keySet());
		log.debug("New providers: "+newKeys);
		log.debug("Removed providers: "+removedKeys);
		for(String key: removedKeys) {
			provider.removeServerConnectionProvider(key);
		}
		for(String key: newKeys) {
			Node node = actualNodes.get(key);
			if(node != null) {
				log.debug("Creating provider to "+node);
				provider.addServerConnectionProvider(key, createNodeProvider(node));
			}
		}
		
	}
	
	protected abstract ServerConnectionProvider createNodeProvider(Node node);
		
	/**
	 * Sets the attribute provider.
	 * @param provider The provider to set.
	 */
	public void setProvider(ConfigurableServerConnectionProvider provider) {
		this.provider = provider;
	}

	/**
	 * The logical name of the group of node. Used to discover the nodes participating in the same cluster.
	 */
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	/**
	 * Pattern used to generate the connection URLs to this node. Placeholders are 1: host 2: port 3: node name.
	 * Usually set to a default by subclasses.
	 */
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	/**
	 * Local service host. Defaults to this hostname as returned by
	 * <code> = InetAddress.getLocalHost().getHostName();</code>
	 * 
	 * @param host
	 *            The host to set.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Local service port. Subclasses usually sets the default port for the implemented protocol.
	 * @param port The port to set.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * A distinct name for this node. Defaults to the host name. You are encouraged to use a logical name if your
	 * hostnames are likely to change.
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * The underlying jgroup channel. Defaults to a bare JChannel instance.
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * A key to encrypt communication. Avoids that an unwanted member participates.
	 */
	public void setSecret(String secret) {
		encryptor = new BasicBinaryEncryptor();
		((BasicBinaryEncryptor)encryptor).setPassword(secret);
	}

	/**
	 * Enable or disable ipv6 support. Default to false.
	 */
	public void setIpv6(boolean ipv6) {
		this.ipv6=ipv6;
	}
	
}
