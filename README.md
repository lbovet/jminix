<p align='center'><img src='http://jminix.googlecode.com/svn/wiki/images/jminix-logo.png' /></p>

<p align='center'><b><a href='Documentation.md'>Documentation</a></b> | <b><a href='Screenshots.md'>Screenshots</a></b> | <b><a href='ReleaseNotes.md'>Release Notes</a></b> | <b><a href='http://groups.google.com/group/jminix-users'>Support</a></b> | <b><a href='Contribute.md'>Contribute</a></b></p>

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

<a href='http://code.google.com/p/jminix/wiki/Screenshots'><img src='http://jminix.googlecode.com/svn/wiki/screenshots/snap011.png' border='0' /></a>


---


Proudly provided by<br />
<a href='http://www.post.ch'>
<img src='http://www.post.ch/en/post-logo.gif' border='0' height='31' /></a>

<p align='right'><img src='http://jminix.googlecode.com/svn/wiki/images/jminix-cricket.png' /></p>