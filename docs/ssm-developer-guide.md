
## **Remote Debug**

### Active Smart Server

Specify debug option for active smart server when starting a SSM cluster.

`./bin/start-ssm.sh --debug master`

### Standby Smart Server

Start a standby smart server in debug mode after active smart server is ready.

`./bin/start-standby.sh --host {HOST_NAME} --debug`

Alternatively, if there is only one standby server configured, debug option can be specified for standby server when starting a SSM cluster.

`./bin/start-ssm.sh --debug standby`


### Smart Agent

Start a smart agent in debug mode after active smart server is ready.

`./bin/start-agent --host {HOST_NAME} --debug`

Alternatively, if there is only one agent configured, debug option can be specified for agent when starting a SSM cluster.

`./bin/start-ssm --debug agent`


### IDE Debug Configuration

Set IDE remote debug configuration for one of the above three kinds of processes in debug mode. Take Intellij as example.

* Add remote debug: Run -> Edit Configuration -> + -> Remote. And change the setting as the following shows.

  Debugger mode: attach

  Host: {HOSTNAME}

  Port: 8008

  The above host is the one running the above three kinds of processes in debug mode.

* Start the above remote debug in IDE.

* If a proxy server bridges your local computer and remote server, you can consider to use the below command to forward
socket message to your local computer 8008 port. And you also need to change the remote debug setting by replacing remote
server host name with your local computer's host name .

  `ssh -L 8008:{REMOTE_SERVER}:8008 {USER}@{PROXY_HOST} -N`

## Building project

Building instructions are provided in the [BUILDING.txt](../BUILDING.txt) file. 

## **IDE setup (IntelliJ IDEA)**

There's an [IDE open issue](https://youtrack.jetbrains.com/issue/IDEA-184921/AspectJ-CTW-Lombok-project-not-building) with using
Lombok and AspectJ in the same maven module, so before the issue will be resolved,
the post-compile weaving should be enabled after each import or update of maven project
by enabling `File > Project Structure > Facets > AspectJ > Post-compile weave mode` checkbox to be able to start tests from IDE.

Also, `/supports/tools/checkstyle.xml` and `/supports/tools/suppressions.xml` Checkstyle configuration files can be imported to the IDE.
If, for some reason, the indentation settings are not applied after importing the Checkstyle settings, 
consider setting the following configurations manually:
- Tab size: 2
- Indent: 2
- Continuation indent: 4

## **License header update**

After adding new source code files to the project, call the following command
in the project root directory to inject license headers to the files:

`mvn com.mycila:license-maven-plugin:format -DskipTests`

## **Third-party Lib's Doc Link**

[Hazelcast](https://docs.hazelcast.org/docs/3.7.8/manual/pdf/hazelcast-documentation-3.7.8.pdf)

[Akka](https://doc.akka.io/docs/akka/2.3/AkkaJava.pdf)