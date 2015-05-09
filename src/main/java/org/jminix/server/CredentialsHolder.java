/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.server;

/**
 * Provides authentication credentials.
 *
 * @author bovetl
 * @version $Revision$
 * @see <script>links('$HeadURL$');</script>
 */
public interface CredentialsHolder {

	public String getUsername();
	
	public String getPassword();
}
