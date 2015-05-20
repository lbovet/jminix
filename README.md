<p align='right'>A <a href="http://www.swisspush.org">swisspush</a> project <a href="http://www.swisspush.org" border=0><img align="top"  src='https://1.gravatar.com/avatar/cf7292487846085732baf808def5685a?s=32'></a></p>
<p align='center'><img src='https://raw.githubusercontent.com/wiki/lbovet/jminix/images/jminix-logo.png' /></p>
<p align='right'><a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=KMH66TMHZYND6" border=0><img src='https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif'></a></p>
<p align='center'><b><a href='https://github.com/lbovet/jminix/wiki'>Documentation</a></b> | <b><a href='https://github.com/lbovet/jminix/wiki/Screenshots'>Screenshots</a></b> | <b><a href='https://github.com/lbovet/jminix/wiki/ReleaseNotes'>Release Notes</a></b> | <b><a href='http://groups.google.com/group/jminix-users'>Support</a></b></p>

Don't want to use an external full-blown JMX console? Just want to have a **simple JMX entry point** into your new or existing apps?

Embedding JMiniX in a webapp is done **simply by declaring a servlet**. Deployed as a servlet, it benefits from your web application configuration such as filters or security constraints.

```
<servlet>
    <servlet-name>JmxMiniConsoleServlet</servlet-name>
    <servlet-class>org.jminix.console.servlet.MiniConsoleServlet</servlet-class>
</servlet> 
```

JMiniX can also be embedded in a **non-web application**, thanks to a lightweight internal webserver:

```
new StandaloneMiniConsole(8088);
```

The console is built in a **RESTful way**. Domains, MBeans, attributes, properties are resources and can be refered to directly with an URL as HTML or JSON (according to Accept header). For example:

ht<b />tp://localhost:8088/servers/0/domains/java.lang/mbeans/type=Memory/attributes/HeapMemoryUsage

Or using the pretty ajax browser:

<a href='http://code.google.com/p/jminix/wiki/Screenshots'><img src='https://raw.githubusercontent.com/wiki/lbovet/jminix/images/snap011.png' border='0' /></a>


---


Proudly provided by<br />
<a href='http://www.post.ch'>
<img src='http://www.post.ch/en/post-logo.gif' border='0' height='31' /></a>

<p align='right'><img src='https://raw.githubusercontent.com/wiki/lbovet/jminix/images/jminix-cricket.png' /></p>
