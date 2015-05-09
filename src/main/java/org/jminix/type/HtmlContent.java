/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.type;

import java.io.Serializable;

/**
 * Marker interface for text that must be rendered as an HTML content by the console. The <code>toString()</code> method is called
 * to retrieve the HTML text.
 * 
 * <p>
 * Security Note: Beware of cross-site scripting. Take care to use this type only for content that you master because
 * the content and scripts are rendered as-is without escaping.
 * 
 * @author bovetl
 * @version $Revision$
 * @see <script>links('$HeadURL$');</script>
 */
public interface HtmlContent extends Serializable {

}
