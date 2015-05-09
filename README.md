# jminix 

Main documentation under https://code.google.com/p/jminix/

## Standalone Mode

### Build 

The project is build with maven. Use it as a dependency to embed jminix into your application or execute the jar file for standalone usage.

### Flexable usage

This project provides HTTP based access to JMX MBeans and is embeddable to java (spring) applications. Also, there is a servlet for web servlet containers. 

Addionally, the projects executable jar file can be started to monitor a remote JMX application: 

Start your java application to monitor and enable remote monitoring:

```
-Dcom.sun.management.jmxremote 
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.local.only=false 
-Dcom.sun.management.jmxremote.authenticate=false 
-Dcom.sun.management.jmxremote.ssl=false
```

From localhost, you can now use jminix in standalone mode adding the following vm params at startup: 

```
-DserverConnectionProvider=org.jminix.server.RmiServerConnectionProvider
-DserverConnectionProviderArgs=ServiceUrl=service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi
```

There is also a possibility to add authentification parameters:

```
-DserverConnectionProvider=org.jminix.server.RmiServerConnectionProvider
-DserverConnectionProviderArgs=ServiceUrl=service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi,Username=user,Password=secret
```

jminix starts on port 8181. This can be adjusted adding a normal command line parameter:

```
java -D...VMPARAMS -jar jminix-VERSION.jar 1234 
```

would start the application in standalone mode on port 1234.

