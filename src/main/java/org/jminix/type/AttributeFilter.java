/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Laurent Bovet <laurent.bovet@windmaster.ch>
 * ------------------------------------------------------------------------------------------------
 * $Id: HtmlContent.java 60 2011-09-01 15:38:13Z laurent.bovet $
 * ------------------------------------------------------------------------------------------------
 *
 */

package org.jminix.type;

/**
 * Users can register a concrete class implementing this interface to transform attributes on
 * mbeans. Typically, one can transform an attribute into HtmlContent using a filter in order to
 * avoid making mbeans depend on JMinix directly.
 */
public interface AttributeFilter {

  /**
   * @param object the attribute to transform.
   * @return the transformed object.
   */
  Object filter(Object object);
}
