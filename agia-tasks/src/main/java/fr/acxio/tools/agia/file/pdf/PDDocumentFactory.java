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
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import fr.acxio.tools.agia.io.ResourcesFactory;

/**
 * <p>
 * General PDDocument factory.
 * </p>
 * 
 * @author pcollardez
 *
 */
public interface PDDocumentFactory {

    PDDocumentContainer getDocument(File sFile) throws PDDocumentFactoryException;

    PDDocumentContainer getDocument(File sFile, Map<String, Object> sParameters) throws PDDocumentFactoryException;

    PDDocumentContainer getDocument(String sFileName) throws PDDocumentFactoryException;

    PDDocumentContainer getDocument(String sFileName, Map<String, Object> sParameters) throws PDDocumentFactoryException;

    PDDocumentContainer getDocument(URL sURL) throws PDDocumentFactoryException;

    PDDocumentContainer getDocument(URL sURL, Map<String, Object> sParameters) throws PDDocumentFactoryException;

    PDDocumentContainer getDocument(InputStream sInputStream) throws PDDocumentFactoryException;

    PDDocumentContainer getDocument(InputStream sInputStream, Map<String, Object> sParameters) throws PDDocumentFactoryException;


// Create container by reading parts. Pb: parts' descriptors may not be homogeneous => CompositeFactory
// Use this factory in a processor to create aggregated PDF
    
    PDDocumentContainer fromParts(ResourcesFactory sResourcesFactory) throws PDDocumentFactoryException;

    PDDocumentContainer fromParts(ResourcesFactory sResourcesFactory, Map<String, Object> sParameters) throws PDDocumentFactoryException;
    

    PDDocumentContainer addParts(PDDocumentContainer sContainer, ResourcesFactory sResourcesFactory) throws PDDocumentFactoryException;

    PDDocumentContainer addParts(PDDocumentContainer sContainer, ResourcesFactory sResourcesFactory, Map<String, Object> sParameters) throws PDDocumentFactoryException;

}
