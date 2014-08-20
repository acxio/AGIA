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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.pdmodel.PDDocument;

import fr.acxio.tools.agia.io.ResourcesFactory;

public abstract class AbstractPDDocumentFactory implements PDDocumentFactory {
    
    public static final String PARAM_NONSEQ = "NONSEQ";
    
    protected Boolean useNonSeq;
    
    public void setUseNonSeq(Boolean sUseNonSeq) {
        useNonSeq = sUseNonSeq;
    }
    
    protected Map<String, Object> getMergedParameters(Map<String, Object> sParameters) {
        Map<String, Object> aParameters = new HashMap<String, Object>();
        if (useNonSeq != null) {
            aParameters.put(PARAM_NONSEQ, useNonSeq);
        }
        if (sParameters != null) {
            aParameters.putAll(sParameters);
        }
        return aParameters;
    }

    @Override
    public PDDocumentContainer getDocument(File sFile) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer getDocument(File sFile, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer getDocument(String sFileName) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer getDocument(String sFileName, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer getDocument(URL sURL) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer getDocument(URL sURL, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer getDocument(InputStream sInputStream) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer getDocument(InputStream sInputStream, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer fromParts(ResourcesFactory sResourcesFactory) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer fromParts(ResourcesFactory sResourcesFactory, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer addParts(PDDocumentContainer sContainer, ResourcesFactory sResourcesFactory) throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocumentContainer addParts(PDDocumentContainer sContainer, ResourcesFactory sResourcesFactory, Map<String, Object> sParameters)
            throws PDDocumentFactoryException {
        throw new UnsupportedOperationException();
    }

    protected PDDocument loadDocument(InputStream sInputStream, Map<String, Object> sParameters) throws IOException, CryptographyException, InvalidPasswordException {
        PDDocument aDocument = PDDocument.load(sInputStream);
        if (aDocument.isEncrypted()) {
            aDocument.decrypt((String) sParameters.get(PDDocumentFactoryConstants.PARAM_PASSWORD));
        }
        return aDocument;
    }
    
    protected PDDocument loadDocument(File sFile, Map<String, Object> sParameters) throws IOException, CryptographyException, InvalidPasswordException {
        PDDocument aDocument = null;
        if (Boolean.TRUE.equals(sParameters.get(PARAM_NONSEQ))) {
            aDocument  = PDDocument.loadNonSeq(sFile, (RandomAccess) sParameters.get(PDDocumentFactoryConstants.PARAM_SCRATCHFILE),
                    (String) sParameters.get(PDDocumentFactoryConstants.PARAM_PASSWORD));
        } else {
            aDocument = PDDocument.load(sFile);
            if (aDocument.isEncrypted()) {
                aDocument.decrypt((String) sParameters.get(PDDocumentFactoryConstants.PARAM_PASSWORD));
            }
        }
        return aDocument;
    }
}
