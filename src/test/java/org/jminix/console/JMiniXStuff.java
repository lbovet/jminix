package org.jminix.console;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jminix.type.HtmlContent;
import org.jminix.type.InputStreamContent;

public final class JMiniXStuff implements JMiniXStuffMBean {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSimpleString() {
        return "This is a simple String";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String invokeStringOperation() {
        return "This text comes from 'invokeStringOperation'.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlContent getHtmlString() {
        return new HtmlContent() {
            private static final long serialVersionUID = -1997969459356542938L;

            public String toString() {
                return "JMiniX can be found <a href=\"https://code.google.com/p/jminix/\">here</a>";
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlContent invokeHtmlStringOperation() {
        return new HtmlContent() {
            private static final long serialVersionUID = -1997969459356542938L;

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("My bookmarks (from 'invokeHtmlStringOperation')<hr>");
                sb.append("<a href=\"https://code.google.com/p/jminix/\">JMiniX Homepage</a><br>");
                sb.append("<a href=\"https://github.com/lbovet/jminix/\">JMiniX on GitHub</a>");
                return sb.toString();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStreamContent getInputStream() {
        return new InputStreamContent() {
            private InputStream is = new ByteArrayInputStream("This file comes from getInputStream".getBytes());

            @Override
            public int read() throws IOException {
                return is.read();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStreamContent invokeStreamOperation() {
        return new InputStreamContent() {
            private InputStream is = new ByteArrayInputStream(
                    "This file comes from invokeStreamOperation".getBytes());

            @Override
            public int read() throws IOException {
                return is.read();
            }
        };
    }
}
