#Dead IF

```
deadif1 = "mbean WebSphere:type=JvmStats attribute UpTime > 10000"
deadif3 = "mbean Application:type=ApplicationTester,name=myTest operation TestMQ > 100"
 ```

The DeadIf application will load rules that are defined as Operating System variables and execute them when a rest service is accessed. The service returns a rest object with statuses and a HTTP return code.
The code is either HTTP/200 OK (no rules triggered) or HTTP/503 Service Unavailable (one or more rules triggered).

A good example of when to use this application would be together with Kubernetes liveness probes to verify that the application is working inside an container.
The kubernetes probes would check /DeadIf/healthz and based on the HTTP status code determine if the application server needs to re-spawn.

An example of an rule is :

```
mbean WebSphere:type=ThreadPoolStats,name=Default Executor attribute ActiveThreads is > 100
```

An example of resoponses:
```json
Rule(-s) triggered: HTTP/503
{
  "ruleResult": [
    {
      "message": "Rule check: 10011 > 50000",
      "id": "deadif4",
      "deadAccordingToRule": false
    },
    {
      "message": "Rule check: 1429275 > 70000",
      "id": "deadif1",
      "deadAccordingToRule": true
    }
  ],
  "dead": true
}
----------------------------------
Successful: HTTP/200

{
  "ruleResult": [
    {
      "message": "Rule check: 10002 > 50000",
      "id": "deadif4",
      "deadAccordingToRule": false
    }
  ],
  "dead": false
}

```
###Syntax

The syntax for the rules are:

To evaluate an attribute:

```
mbean <object name> attribute  <name> {<,>,=,!=}  <value>
```

To evaluate the result of an operation:

```
mbean <object name> operation  <name> {<,>,=,!=}  <value>
```
Note that there is no support for passing arguments to the operation at this time.

###Examples

- "The application is dead if the application server has been running for more than 1000 seconds":

    ```
    deadif0 = mbean WebSphere:type=JvmStats attribute UpTime > 10000
    ```

- "The application is dead if Default Executor has more that 100 active threads"

    ```
    deadif1 = "mbean WebSphere:type=ThreadPoolStats,name=Default Executor attribute ActiveThreads > 100"
    ```

- "The application is dead if myTest operation TestMQ return false"

  ```
  deadif2 = "mbean name=myTest operation TestMQ = false"
  ```

- "The application is dead if myTest operation getQueueDepth returns is grater then 100"

  ```
  deadif3 = "mbean name=myTest operation getQueueDepth > 100"
  ```

###Building deadif.war
Check out the code and run the maven target package.

```
git clone https://github.com/mtekkie/DeadIf
cd DeadIf
mvn package

```
The resulting artifact (deadif.war) will be placed in target/.

###Custom MXBeans
An application has the possibility to expose it's own MXBeans and have the rules tested against them.

See example: xxxx.

More information about MXBeans are available on Oracles site: https://docs.oracle.com/javase/tutorial/jmx/mbeans/mxbeans.html    


###Application Server Info
All application servers have different setups when it comes to the MBeans that they are exposing. Use Jconsole to explore which MBeans that your application server is exposing.

In Websphere Liberty profile there is an option to enable monitoring in the server.xml file. If that is enabled the PMI objects are available as MBeans and you can apply rules to them.  

```xml
<server description="unicornsRulesServer">

    <!-- Enable features -->
    <featureManager>
        <feature>javaee-7.0</feature>
        <feature>monitor-1.0</feature>
    </featureManager>

	<monitor enableTraditionalPMI="false"/>
  <!-- ... other stuff ....  -->  
</server>
```
