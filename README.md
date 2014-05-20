AGIA
====

Advanced Generic Injection Automaton

OVERVIEW
--------

AGIA is a generic technical tasks manager built upon
[Spring-Batch](http://projects.spring.io/spring-batch/).

This package contains [Jetty](http://www.eclipse.org/jetty/) 8
to provide the web interface and [YAJSW](http://yajsw.sourceforge.net/) to make
AGIA able to be run as a service.

CONTENT OF THE PACKAGE
----------------------

Directory |           | Content
--------- | --------- | --------------------------------------------
docs      |           | Documentation of AGIA
etc       |           | Configuration for Jetty
jobs      |           | Jobs descriptions as in Spring-Batch.
lib       |           | Jetty libraries
          | db        | JDBC drivers
logs      |           | Logs for Jetty and AGIA
overlays  |           | Contains the AGIA webapp and its extensions
resources |           | Jetty resources
work      |           | Jetty work directory
yajsw     |           | YAJSW (Yet Another Java Service Wrapper)
          | bat       | Starting scripts for Windows
          | bin       | Starting scripts for Linux
          | conf      | Configuration for YAJSW
          | doc       | Documentation of YAJSW
          | lib       | YAJSW library
          | log       | Logs for YAJSW
          | scripts   | Scripts for YAJSW
          | templates | Linux templates for YAJSW
          | tmp       | Temporary files of YAJSW
          | webapps   | YAJSW webapp
setup.*   |           | Setup scripts for Windows and Linux
start.*   |           | Jetty default bootstrap

REQUIREMENTS
------------

* Windows or Linux
* JDK 1.6.x or newer

INSTALL
-------

1. Unzip the package in the desired directory
2. Add your JDBC driver in `lib/db`
3. Run `setup.bat` or `setup.sh` depending on your OS

   The given installation path is used by YAJSW when the service is installed
   to make it find its resources.
   
   The setup can be run as many times as anyone wish UNTIL A SERVICE IS SETUP
   AND RUNNING. IF A SERVICE IS SETUP AND RUNNING, IT MUST BE STOPPED AND
   UNINSTALLED BEFORE CHANGING THE CONFIGURATION.

RUN
---

You can launch AGIA on console mode with the command:

`.\yajsw\bat\runConsole.bat`

or

`./yajsw/bin/runConsole.sh`

STOP
----

If AGIA has been started on console mode, simply use Ctrl+C and wait for it to
stop completely.

ADD JOBS
--------

Simply copy the job file into the directory `jobs` and restart AGIA.

ADD EXTENSIONS
--------------

Extensions to AGIA can be added into the directory:
`overlays/templates/agiaTemplate=agia`

Usually, it consists of jar files that will be copied into:
`overlays/templates/agiaTemplate=agia/WEB-INF/lib`

BUILDING
--------

Run:

`mvn clean site package`

The output of the build will be in `agia-jetty\target` packaged as a zip file
and unzipped in the matching sub-directory.

You can also deploy the webapp found in `agia-webapp\target`.