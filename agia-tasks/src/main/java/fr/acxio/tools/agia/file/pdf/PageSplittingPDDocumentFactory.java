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
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A splitting PDDocument factory.
 * </p>
 * <p>
 * It can take a starting page, an ending page, a number of pages to extract for
 * each part, and a password.
 * </p>
 * <p>
 * The non sequential parser can be used for files, but not for streams and
 * URLs.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class PageSplittingPDDocumentFactory extends AbstractPDDocumentFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageSplittingPDDocumentFactory.class);

    public static final String PARAM_SPLITATPAGE = "SPLITATPAGE";
    public static final String PARAM_STARTPAGE = "STARTPAGE";
    public static final String PARAM_ENDPAGE = "ENDPAGE";

    private Integer splitAtPage;
    private Integer startPage;
    private Integer endPage;
    

    public void setSplitAtPage(Integer sSplitAtPage) {
        splitAtPage = sSplitAtPage;
    }

    public void setStartPage(Integer sStartPage) {
        startPage = sStartPage;
    }

    public void setEndPage(Integer sEndPage) {
        endPage = sEndPage;
    }

    @Override
    public PDDocumentContainer getDocument(File sFile) throws PDDocumentFactoryException {
        return getDocument(sFile, null);
    }

    protected Map<String, Object> getMergedParameters(Map<String, Object> sParameters) {
        Map<String, Object> aParameters = new HashMap<String, Object>();
        if (splitAtPage != null) {
            aParameters.put(PARAM_SPLITATPAGE, splitAtPage);
        }
        if (startPage != null) {
            aParameters.put(PARAM_STARTPAGE, startPage);
        }
        if (endPage != null) {
            aParameters.put(PARAM_ENDPAGE, endPage);
        }
        if (useNonSeq != null) {
            aParameters.put(PARAM_NONSEQ, useNonSeq);
        }
        if (sParameters != null) {
            aParameters.putAll(sParameters);
        }
        return aParameters;
    }

    @Override
    public PDDocumentContainer getDocument(File sFile, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        Map<String, Object> aParameters = getMergedParameters(sParameters);

        PDDocumentContainer aResult = null;
        PDDocument document = null;

        try {
            document = loadDocument(sFile, aParameters);

            aResult = splitDocument(document, (Integer) aParameters.get(PARAM_STARTPAGE), (Integer) aParameters.get(PARAM_ENDPAGE),
                    (Integer) aParameters.get(PARAM_SPLITATPAGE));

        } catch (Exception e) {
            Exception aException = e;
            try {
                if (aResult != null) {
                    aResult.close();
                } else if (document != null) {
                    document.close();
                }
            } catch (Exception ex) {
                aException = new PDDocumentFactoryException(ex);
            }
            throw new PDDocumentFactoryException(aException);
        }

        return aResult;
    }

    @Override
    public PDDocumentContainer getDocument(String sFileName) throws PDDocumentFactoryException {
        return getDocument(sFileName, null);
    }

    @Override
    public PDDocumentContainer getDocument(String sFileName, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        return getDocument(new File(sFileName), sParameters);
    }

    @Override
    public PDDocumentContainer getDocument(URL sURL) throws PDDocumentFactoryException {
        return getDocument(sURL, null);
    }

    @Override
    public PDDocumentContainer getDocument(URL sURL, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        InputStream aInputStream = null;
        try {
            aInputStream = sURL.openStream();
        } catch (IOException e) {
            throw new PDDocumentFactoryException(e);
        }
        return getDocument(aInputStream, sParameters);
    }

    @Override
    public PDDocumentContainer getDocument(InputStream sInputStream) throws PDDocumentFactoryException {
        return getDocument(sInputStream, null);
    }

    @Override
    public PDDocumentContainer getDocument(InputStream sInputStream, Map<String, Object> sParameters) throws PDDocumentFactoryException {
        Map<String, Object> aParameters = getMergedParameters(sParameters);

        PDDocumentContainer aResult = null;
        PDDocument document = null;

        try {
            document = loadDocument(sInputStream, aParameters);

            aResult = splitDocument(document, (Integer) aParameters.get(PARAM_STARTPAGE), (Integer) aParameters.get(PARAM_ENDPAGE),
                    (Integer) aParameters.get(PARAM_SPLITATPAGE));

        } catch (Exception e) {
            Exception aException = e;
            try {
                if (aResult != null) {
                    aResult.close();
                } else if (document != null) {
                    document.close();
                }
            } catch (Exception ex) {
                aException = new PDDocumentFactoryException(ex);
            }
            throw new PDDocumentFactoryException(aException);
        }

        return aResult;
    }

    private PDDocumentContainer splitDocument(PDDocument sDocument, Integer sStartPage, Integer sEndPage, Integer sSplitAtPage) throws IOException {
        Splitter aSplitter = new Splitter();
        int aNumberOfPages = sDocument.getNumberOfPages();
        boolean aStartEndPageSet = false;
        if (sStartPage != null) {
            aSplitter.setStartPage(sStartPage);
            aStartEndPageSet = true;
            if (sSplitAtPage == null) {
                aSplitter.setSplitAtPage(aNumberOfPages);
            }
        }
        if (sEndPage != null) {
            aSplitter.setEndPage(sEndPage);
            aStartEndPageSet = true;
            if (sSplitAtPage == null) {
                aSplitter.setSplitAtPage(sEndPage);
            }
        }
        if (sSplitAtPage != null) {
            aSplitter.setSplitAtPage(sSplitAtPage);
        } else if (!aStartEndPageSet) {
            aSplitter.setSplitAtPage(1);
        }

        List<PDDocument> aParts = aSplitter.split(sDocument);

        return new BasicPDDocumentContainer(sDocument, aParts);
    }

}
