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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PageSplittingPDDocumentFactoryTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetDocumentByFilename() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content1.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentByFile() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        PDDocumentContainer aDocumentContainer = aFactory.getDocument(new File("src/test/resources/testFiles/content1.pdf"));
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentByStream() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        PDDocumentContainer aDocumentContainer = aFactory.getDocument(new FileInputStream("src/test/resources/testFiles/content1.pdf"));
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentByURL() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        PDDocumentContainer aDocumentContainer = aFactory.getDocument(new URL("file:src/test/resources/testFiles/content1.pdf"));
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentSplitAtPage2() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setSplitAtPage(2);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(2, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentSplitAtPage1() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setSplitAtPage(1);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(3, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentSplitAtPage5() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setSplitAtPage(5);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentSplitAtPage0() throws Exception {
        exception.expect(Exception.class);
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setSplitAtPage(0);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(3, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentStartPage2() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setStartPage(2);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentStartPage2Split1() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setStartPage(2);
        aFactory.setSplitAtPage(1);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(2, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }

    @Test
    public void testGetDocumentEndPage2() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setEndPage(2);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentEndPage2Split1() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setEndPage(2);
        aFactory.setSplitAtPage(1);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(2, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentStartPage1EndPage2() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setStartPage(1);
        aFactory.setEndPage(2);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentStartPage1EndPage2Split1() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setStartPage(1);
        aFactory.setEndPage(2);
        aFactory.setSplitAtPage(1);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(2, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentFlowParams() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        Map<String, Object> aParameters = new HashMap<String, Object>(1);
        aParameters.put(PageSplittingPDDocumentFactory.PARAM_SPLITATPAGE, 1);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf", aParameters);
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(3, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentFlowOverrideParams() throws Exception {
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setSplitAtPage(3);
        Map<String, Object> aParameters = new HashMap<String, Object>(1);
        aParameters.put(PageSplittingPDDocumentFactory.PARAM_SPLITATPAGE, 1);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/content2.pdf", aParameters);
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(3, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentNotExists() throws Exception {
        exception.expect(Exception.class);
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setSplitAtPage(1);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/contentX.pdf");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
    
    @Test
    public void testGetDocumentNotPDF() throws Exception {
        exception.expect(Exception.class);
        PageSplittingPDDocumentFactory aFactory = new PageSplittingPDDocumentFactory();
        aFactory.setSplitAtPage(1);
        PDDocumentContainer aDocumentContainer = aFactory.getDocument("src/test/resources/testFiles/input.csv");
        assertNotNull(aDocumentContainer);
        assertNotNull(aDocumentContainer.getDocument());
        assertNotNull(aDocumentContainer.getParts());
        assertEquals(1, aDocumentContainer.getParts().size());
        aDocumentContainer.close();
    }
}
