#Dead IF

```
dead check interval 60
dead if mbean WebSphere:type=JvmStats attribute UpTime > 10000
dead if mbean WebSphere:type=ThreadPoolStats,name=Default Executor attribute ActiveThreads > 100 for 5 minutes
dead if mbean Application:type=ApplicationTester,name=myTest operation TestMQ returns >100
 ```
 
In the platform there are probes that checks if the application is up and running.
Theese are configured by creating rules that are set as environment variables on the application server environment.
An example of  an rule is "dead if mbean WebSphere:type=ThreadPoolStats,name=Default Executor attribute ActiveThreads > 100 for 5 minutes".
 
The probes will check the running application servers MBeans (Management Beans) and evaluate if the application is up and running or has died and the application server needs to re-spawn.
 
The syntax for the rules are:
 
To evaulate an attribute:
                             dead if mbean <object name> attribute  <name> {<,>,=,!=}  <value> [for <time> minutes]
                            
                             examples:
                             "The application is dead if the application server has been running for more than 1000 seconds"
                             dead if mbean WebSphere:type=JvmStats attribute UpTime > 10000
 
                             "The application is dead if Default Executor has had more that 100 active threads for more than 5 minutes"
                             dead if mbean WebSphere:type=ThreadPoolStats,name=Default Executor attribute ActiveThreads > 100 for 5 minutes
 
To evaulate an operation:
                             dead if mbean <object name> operation <name> {<,>,!=,=}  <value> [for <time> minutes]
                                                         
                             examples:
                             "The application is dead if myTest operation TestMQ
                             dead if mbean name=myTest operation TestMQ = false
 
                             "The application is dead if myTest operation TestMQ
                             dead if mbean name=myTest operation QueueDepth > 100 for 100 minutes
 
An application has the possibillity to implement it's own MXBean and have the probes evalute if the application is up and running.
 
A basic example of an  MXBean that is a part of an EAR file is available on GIT/BB: Link.
The  example describes a scenario where we know that an application needs to restarted if the connection to a system has been terminated.
More information about MXBeans are available on Oracles site: https://docs.oracle.com/javase/tutorial/jmx/mbeans/mxbeans.html    
 
The defualt setting is that the rules are evaluted every 10:th second.
This is configurable by setting the dead check interval.
 
                             "Check all rules every 60 second"
                             dead check interval 60