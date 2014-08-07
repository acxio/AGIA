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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * <p>
 * Basic PDDocument container.
 * </p>
 * <p>
 * Parts are read only.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class BasicPDDocumentContainer implements PDDocumentContainer {

    private PDDocument document;
    private List<PDDocument> parts;

    public BasicPDDocumentContainer(PDDocument sDocument, List<PDDocument> sParts) {
        document = sDocument;
        parts = (sParts == null) ? null : Collections.unmodifiableList(sParts);
    }

    @Override
    public PDDocument getDocument() {
        return document;
    }

    @Override
    public List<PDDocument> getParts() {
        return parts;
    }

    @Override
    public void close() throws IOException {
        if (document != null) {
            document.close();
        }
        for (int i = 0; parts != null && i < parts.size(); i++) {
            PDDocument aDocument = (PDDocument) parts.get(i);
            aDocument.close();
        }
    }

}
