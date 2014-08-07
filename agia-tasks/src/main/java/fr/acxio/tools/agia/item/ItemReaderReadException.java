package fr.acxio.tools.agia.item;

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

public class ItemReaderReadException extends org.springframework.batch.item.ItemReaderException {

    private static final long serialVersionUID = -7286918657107682932L;

    public ItemReaderReadException(String sMessage) {
        super(sMessage);
    }

    public ItemReaderReadException(String sMessage, Throwable sCause) {
        super(sMessage, sCause);
    }

}
