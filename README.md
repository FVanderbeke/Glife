# GLIFE: Game of life

Example of game of life implementation.

## Getting Started

Before being able to run this application, make sure to fulfil the following given prerequisites.

### Prerequisites

#### Java8+ runtime 
You have installed Java8+ rutime on your environment.
The `java` execution command is declared in your `PATH` environment property.
 
To check if Java is correctly installed, open a command line console (linux terminal or DOS console) and type the following command:

 ```
$ java --version
openjdk 12.0.2 2019-07-16
OpenJDK Runtime Environment (build 12.0.2+9-Ubuntu-119.04)
OpenJDK 64-Bit Server VM (build 12.0.2+9-Ubuntu-119.04, mixed mode)
 ```

#### Maven 3.x
You have installed a maven 3 distribution on your environment.
The `mvn` execution command is declared in your `PATH` environment property.
 
To check if Maven is correctly installed, open a command line console (linux terminal or DOS console) and type the following command:
```
$ mvn --version
Apache Maven 3.6.0
Maven home: /usr/share/maven
Java version: 12.0.2, vendor: Private Build, runtime: /usr/lib/jvm/java-12-openjdk-amd64
Default locale: fr_FR, platform encoding: UTF-8
OS name: "linux", version: "5.0.0-23-generic", arch: "amd64", family: "unix"

```

### Compiling

Get the current `glife-0.0.1-SNAPSHOT.zip` file. Unzip it in your `PRJ_HOME` project folder.

You should see a new subfolder called `glife` containing the project sources.

Open a terminal (or DOS console), go in `PRJ_HOME/glife` and type the following command:

```
$ mvn clean package
```
The command should successfully display this comment:

```
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  ######
[INFO] Finished at: ######
[INFO] ------------------------------------------------------------------------
```

You will find the binaries to install in folder `PRJ_HOME/glife/target`, under the name of `glife-0.0.1-SNAPSHOT.zip`.

### Installing

Copy `PRJ_HOME/glife/target/glife-0.0.1-SNAPSHOT.zip` file into your `INSTALL_DIR` installation directory.

Unzip the file. It should create a new sub folder called `glife`. Its content is: 

```
<INSTALL_DIR>/
    |-config.properties
    |-glife-0.0.1-SNAPSHOT.jar
    |-lib/
    |   |-activation-1.1.1.jar
    |   |-jaxb-api-2.2.11.jar
    |   |-jaxb-core-2.2.11.jar
    |   |-jaxb-impl-2.2.11.jar
    |   |-vavr-1.0.0-alpha-3.jar
    |-refresh-server
    |-refresh-server.bat
    |-shutdown-server
    |-shutdown-server.bat
    |-start
    |-start.bat
    |-start-server
    |-start-server.bat
    |-startw
    |-startw.bat
```
If you are on linux, make sure to define the following files as executable:

* start
* startw 
* start-server
* shutdown-server
* refresh-server

### Configuring your environment

Finally, declare an environment variable called `GLIFE_HOME`, pointing on the `$INSTALL_DIR/glife` directory.

For windows, declare this new variable must be declare in your `Advanced system settings`.

On Linux, let update user variables with:
```
export GLIFE_HOME=$INSTALL_DIR/glife
```

### Running
To execute the application, open a terminal (or a DOS console), go to  `INSTALL_DIR/glife` and type:
* `./start` (or `./start.bat` for Windows) to start the game in console mode.
* `./startw` (or `./startw.bat` for Windows) to start the game with a graphical interface.
* `./start-server` (or `./start-server.bat` for Windows) to start the network mode.

### Network mode

On network mode, clients can use TCP client tools to connect to the server (default port: 7777).

To refresh: in a terminal, let execute `refresh-server` (or `refresh-server.bat` for Windows) command.

To stop the server: in a terminal, let execute `shutdown-server` (or `shutdown-server.bat` for Windows) command.

> Note for admins
> You can remotely refresh the server by connecting a TCP client on your server (default admin port: 8888) and type "1".
>
> You can remotely shutdown the server by connecting a TCP client on your server (default admin port: 8888) and type "2".

## Configuration

By default, Glife comes with a set of configured properties defined in the `config.properties` file.

### Available properties

* application.mode: application mode (BASIC or NETWORK). Default: BASIC
* application.output: renderer output on BASIC mode (CONSOLE or UX). Default: CONSOLE
* universe.width: default universe width. Default: 20
* universe.height: default universe length. Default: 20
* universe.refresh.rate.value: Refresh rate value (integer). Default: 5
* universe.refresh.rate.unit: Refresh rate time unit (java.util.concurrent.TimeUnit name). Default: SECONDS
* universe.expansion.strategy: how neighbors are computed in the current canvas (CIRCULAR or FIXED). Default: FIXED
* universe.rule.set: which rule set to apply on the universe (available: ANKAMA or CONWAY). Default: ANKAMA
* universe.spawn.rate: Spawning ratio in the universe (float). Default: 0.15
* server.save.rate.value: Saving rate value (integer). Default: 30
* server.save.rate.unit: Saving rate time unit (java.util.concurrent.TimeUnit name). Default: SECONDS
* server.client.port: on NETWORK mode, defines the listening port for clients wanting to get universe state (integer). Default: 7777
* server.admin.port: on NETWORK mode, defines the listening port for admins wanting to perform `refresh` or `shutdown` actions (integer). Default: 8888

### config.properties file

From the moment that the configuration file named `config.properties` is available in the `$GLIFE_HOME`, this one will be 
taken into account automatically. 

### Command line arguments

Some extra arguments exist to override the following properties.

Syntax: `<COMMAND> [OPTION...]`

Where command is:
* `./start` (or `./start.bat` for Windows)
* `./startw` (or `./startw.bat` for Windows)
* `./start-server` (or `./start-server.bat` for Windows)
* `./refresh-server` (or `./refresh-server.bat` for Windows)
* `./shutdown-server` (or `./shutdown-server.bat` for Windows)

Where option can be:
* `-h`: will display command line help.
* `-width=<value>` : matrix width (integer only).
* `-height=<value>` : matrix height (integer only).
* `-spawn=<value>` : spawning rate percent (float only).
* `-refresh=<value>` : refresh rate (long, in milliseconds).
* `-mode=<value>` : application mode (BASIC, NETWORK).
* `-out=<value>` : output renderer (CONSOLE, UX).
* `-port=<value>` : in NETWORK mode, TCP port for client connection (integer).
* `-admin-port=<value>` : in NETWORK mode, TCP port for admin connection (integer).

Examples:

```
$ ./start -width=30 -height=40

$ ./start-server -h
```

> Admin extras:
> > You can override any default property by using the option `-JVMD=<property_key>:<value>`.
> > For instance:
> > ```
> > $ ./start -JVMD=application.output:UX
> > ```

### Override order

First, values stored as system properties. 
Overridden by config.properties file values.
Overridden by command argument values.

If none define, will use global default values.

### Renderers

The following renderers are available:
* Console
* Graphical UI
* TCP

#### Console

Basic mode. No network available.

Executed with the `start` (or `start.bat`) command.

Will display the following prompt:

```
Available actions:
	- shutdown : stops the game
	- reset: resets the game's universe
	- display_on: to show the universe generation evolution
	- display_off: to stop the display of the universe generation evolution
```
You will have to type the action to perform:
* `shutdown` : will shutdown everything.
* `reset` : will reset/refresh the universe.
* `display_on` : universe generations will be now periodically displayed in the console output. Be aware that it will interfere with your typing.
* `display_off` : to stop the generation periodical display.


#### Graphical UI

A simple renderer upon basic mode opening a window. No network available.

Executed with the `startw` (or `startw.bat`) command.

#### TCP

Network mode. The server is started with `start-server` (or `start-server.bat`) command.

No action available in the console output. You will have to use `shutdown-server` and `refresh-server` commands.

## Saved data

When running, Glife periodically saves the automaton state into a `save.file` that will be stored in `$GLIFE_HOME\data`. 

## Built With

* [VAVR](https://www.vavr.io/) - Functional library. Only to use the `Try` monad.
* [Maven](https://maven.apache.org/) - Dependency Management.
* [AssertJ](https://joel-costigliola.github.io/assertj/) - For unit test assertions.
* [JUnit](https://junit.org/junit4/) - Unit test framework.
