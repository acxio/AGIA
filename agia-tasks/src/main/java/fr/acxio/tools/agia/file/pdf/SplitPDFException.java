package fr.acxio.tools.agia.file.pdf;

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

/**
 * <p>
 * SplitPDFTasklet related exception
 * </p>
 * 
 * @author pcollardez
 *
 */
public class SplitPDFException extends Exception {

    private static final long serialVersionUID = 3302647525928406409L;

    public SplitPDFException() {
        super();
    }

    public SplitPDFException(String sMessage, Throwable sCause) {
        super(sMessage, sCause);
    }

    public SplitPDFException(String sMessage) {
        super(sMessage);
    }

    public SplitPDFException(Throwable sCause) {
        super(sCause);
    }

}
