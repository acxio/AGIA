package fr.acxio.tools.agia.item.database;

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

public class PreparedStatementCreationException extends Exception {

    private static final long serialVersionUID = 7743978505836302285L;

    public PreparedStatementCreationException() {
    }

    public PreparedStatementCreationException(String sMessage) {
        super(sMessage);
    }

    public PreparedStatementCreationException(Throwable sCause) {
        super(sCause);
    }

    public PreparedStatementCreationException(String sMessage, Throwable sCause) {
        super(sMessage, sCause);
    }

}
