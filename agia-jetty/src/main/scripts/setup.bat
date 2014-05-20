@echo off

REM   Copyright 2014 Acxio
REM   
REM   Licensed under the Apache License, Version 2.0 (the "License");
REM   you may not use this file except in compliance with the License.
REM   You may obtain a copy of the License at
REM   
REM     http://www.apache.org/licenses/LICENSE-2.0
REM   
REM   Unless required by applicable law or agreed to in writing, software
REM   distributed under the License is distributed on an "AS IS" BASIS,
REM   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM   See the License for the specific language governing permissions and
REM   limitations under the License.

java -classpath lib\jetty-util-8.1.8.v20121106.jar;yajsw\lib\core\commons\commons-configuration-1.8.jar;yajsw\lib\core\commons\commons-lang-2.4.jar;yajsw\lib\core\commons\commons-logging-1.1.jar;yajsw\lib\core\groovy\groovy-all-1.8.6.jar groovy.ui.GroovyMain setup.groovy