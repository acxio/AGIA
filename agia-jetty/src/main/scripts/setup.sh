#!/bin/bash

# Copyright 2014 Acxio
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

java -classpath lib/jetty-util-8.1.8.v20121106.jar:yajsw/lib/core/commons/commons-configuration-1.8.jar:yajsw/lib/core/commons/commons-lang-2.4.jar:yajsw/lib/core/commons/commons-logging-1.1.jar:yajsw/lib/core/groovy/groovy-all-1.8.6.jar groovy.ui.GroovyMain setup.groovy