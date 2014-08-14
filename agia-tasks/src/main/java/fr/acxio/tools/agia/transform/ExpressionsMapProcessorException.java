package fr.acxio.tools.agia.transform;

/*
 * Copyright 2014 Acxio
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ExpressionsMapProcessorException extends Exception {

    private static final long serialVersionUID = 1598916266582664295L;

    public ExpressionsMapProcessorException() {
    }

    public ExpressionsMapProcessorException(String sMessage) {
        super(sMessage);
    }

    public ExpressionsMapProcessorException(Throwable sCause) {
        super(sCause);
    }

    public ExpressionsMapProcessorException(String sMessage, Throwable sCause) {
        super(sMessage, sCause);
    }

}
