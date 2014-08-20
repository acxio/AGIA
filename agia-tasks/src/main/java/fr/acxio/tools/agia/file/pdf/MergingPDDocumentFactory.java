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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;

import fr.acxio.tools.agia.io.ResourcesFactory;

public class MergingPDDocumentFactory extends AbstractPDDocumentFactory {

    @Override
    public PDDocumentContainer fromParts(ResourcesFactory sResourcesFactory) throws PDDocumentFactoryException {
        return fromParts(sResourcesFactory, null);
    }

    @Override
    public PDDocumentContainer fromParts(ResourcesFactory sResourcesFactory, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        PDDocumentContainer aContainer = null;
        if (sResourcesFactory != null) {
            Map<String, Object> aParameters = getMergedParameters(sParameters);
            PDDocument aPart = null;
            List<PDDocument> aParts = new ArrayList<PDDocument>();
            try {
                Resource[] aResources = sResourcesFactory.getResources(aParameters);
                if (aResources != null) {
                    for(Resource aResource : aResources) {
                        aPart = loadDocument(aResource.getFile(), aParameters);
                        aParts.add(aPart);
                    }
                }
                aContainer = new BasicPDDocumentContainer(null, aParts);
                
            } catch (Exception e) {
                Exception aException = e;
                try {
                    if (aContainer != null) {
                        aContainer.close();
                    } else if (aPart != null) {
                        aPart.close();
                    }
                } catch (Exception ex) {
                    aException = new PDDocumentFactoryException(ex);
                }
                throw new PDDocumentFactoryException(aException);
            }
        }
        return aContainer;
    }

    @Override
    public PDDocumentContainer addParts(PDDocumentContainer sContainer, ResourcesFactory sResourcesFactory) throws PDDocumentFactoryException {
        return addParts(sContainer, sResourcesFactory, null);
    }

    @Override
    public PDDocumentContainer addParts(PDDocumentContainer sContainer, ResourcesFactory sResourcesFactory, Map<String, Object> sParameters)
            throws PDDocumentFactoryException {
        PDDocumentContainer aContainer = sContainer;
        if (sResourcesFactory != null) {
            PDDocumentContainer aPartsContainer = fromParts(sResourcesFactory, sParameters);
            List<PDDocument> aParts = new ArrayList<PDDocument>(sContainer.getParts());
            aParts.addAll(aPartsContainer.getParts());
            aContainer = new BasicPDDocumentContainer(null, aParts); 
        }
        return aContainer;
    }

    
}
