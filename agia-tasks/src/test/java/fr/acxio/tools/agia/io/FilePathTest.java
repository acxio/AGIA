package fr.acxio.tools.agia.io;

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

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class FilePathTest {

    @Test
    public void testCreateFromString() {
        FilePath aPath = FilePath.valueOf("c:\\folder1\\folder2\\file.ext");
        assertNotNull(aPath);
    }
    
    @Test
    public void testCreateFromFile() {
        FilePath aPath = FilePath.valueOf(new File("c:\\folder1\\folder2\\file.ext"));
        assertNotNull(aPath);
    }
    
    @Test
    public void testGetElement() {
        FilePath aPath = FilePath.valueOf("c:\\folder1\\folder2\\file.ext");
        assertNotNull(aPath);
        assertEquals("c:", aPath.getElement(0));
        assertEquals("folder1", aPath.getElement(1));
        assertEquals("folder2", aPath.getElement(2));
        assertEquals("file.ext", aPath.getElement(3));
        assertEquals("c:", aPath.getElement(4));
        assertEquals("file.ext", aPath.getElement(-1));
        assertEquals("folder2", aPath.getElement(-2));
        assertEquals("folder1", aPath.getElement(-3));
        assertEquals("c:", aPath.getElement(-4));
    }
    
    @Test
    public void testGetElementOne() {
        FilePath aPath = FilePath.valueOf("file.ext");
        assertNotNull(aPath);
        assertEquals("file.ext", aPath.getElement(-1));
        assertEquals("file.ext", aPath.getElement(0));
        assertEquals("file.ext", aPath.getElement(1));
    }
    
    @Test
    public void testGetElementNone() {
        FilePath aPath = FilePath.valueOf("");
        assertNotNull(aPath);
        assertEquals("", aPath.getElement(-1));
        assertEquals("", aPath.getElement(0));
        assertEquals("", aPath.getElement(1));
    }
    
    @Test
    public void testGetElementNull() {
        FilePath aPath = FilePath.valueOf((String)null);
        assertNotNull(aPath);
        assertNull(aPath.getElement(-1));
        assertNull(aPath.getElement(0));
        assertNull(aPath.getElement(1));
    }

    @Test
    public void testGetSubpath() {
        FilePath aPath = FilePath.valueOf("c:\\folder1\\folder2\\file.ext");
        assertEquals("folder1/folder2", aPath.getSubpath(1,2));
        assertEquals("folder2/file.ext", aPath.getSubpath(2,3));
        assertEquals("c:/folder1/folder2/file.ext", aPath.getSubpath(3,4));
    }

    @Test
    public void testGetSubpathNegativeIndex() {
        FilePath aPath = FilePath.valueOf("c:\\folder1\\folder2\\file.ext");
        assertEquals("folder1/folder2", aPath.getSubpath(-2,-3));
        assertEquals("folder2/file.ext", aPath.getSubpath(-1,-2));
    }

    @Test
    public void testGetSubpathMixedIndex() {
        FilePath aPath = FilePath.valueOf("c:\\folder1\\folder2\\file.ext");
        assertEquals("folder1/folder2", aPath.getSubpath(-2,1));
    }

    @Test
    public void testGetSubpathSameIndex() {
        FilePath aPath = FilePath.valueOf("c:\\folder1\\folder2\\file.ext");
        assertEquals("folder1", aPath.getSubpath(1,1));
    }

    @Test
    public void testGetSubpathSameIndexMixed() {
        FilePath aPath = FilePath.valueOf("c:\\folder1\\folder2\\file.ext");
        assertEquals("folder1", aPath.getSubpath(1,-3));
    }
    
    @Test
    public void testGetSubpathCustomSeparator() {
        FilePath aPath = FilePath.valueOf("c:\\folder1\\folder2\\file.ext");
        assertEquals("folder1\\folder2", aPath.getSubpath(1,2, "\\"));
    }

    @Test
    public void testGetSubpathOneElement() {
        FilePath aPath = FilePath.valueOf("file.ext");
        assertEquals("file.ext", aPath.getSubpath(0,0));
        assertEquals("file.ext", aPath.getSubpath(0,1));
        assertEquals("file.ext", aPath.getSubpath(-1,0));
        assertEquals("file.ext", aPath.getSubpath(1,-1));
    }

    @Test
    public void testGetSubpathEmptyElement() {
        FilePath aPath = FilePath.valueOf("");
        assertEquals("", aPath.getSubpath(0,0));
        assertEquals("", aPath.getSubpath(0,1));
        assertEquals("", aPath.getSubpath(-1,0));
        assertEquals("", aPath.getSubpath(1,-1));
    }

    @Test
    public void testGetSubpathNullElement() {
        FilePath aPath = FilePath.valueOf((String)null);
        assertEquals(null, aPath.getSubpath(0,0));
        assertEquals(null, aPath.getSubpath(0,1));
        assertEquals(null, aPath.getSubpath(-1,0));
        assertEquals(null, aPath.getSubpath(1,-1));
    }

}
