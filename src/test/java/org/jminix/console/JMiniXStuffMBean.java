package org.jminix.console;

import org.jminix.type.HtmlContent;
import org.jminix.type.InputStreamContent;

/** Our Test MBean. */
public interface JMiniXStuffMBean {
  /** Attribute that returns a simple String. */
  public boolean getBoolean();

  public void setBoolean(boolean f);

  public int getInt();

  public void setInt(int v);

  /** Attribute that returns a simple String. */
  public String getSimpleString();

  /** Attribute that returns a String array. */
  public String[] getStringArray();

  /** Operation that Returns a simple String. */
  public abstract String invokeStringOperation();

  /** Attribute that returns a HTML String. */
  public abstract HtmlContent getHtmlString();

  /** Operation that returns a HTML String. */
  public abstract HtmlContent invokeHtmlStringOperation();

  /** Attribute that returns an {@link InputStreamContent}. */
  public abstract InputStreamContent getInputStream();

  /** Operation that returns an {@link InputStreamContent}. */
  public abstract InputStreamContent invokeStreamOperation();
}
