package fr.acxio.tools.agia.convert;

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

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ListFormatConverterTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testConvert() throws ConversionException {
		ListFormatConverter aConverter = new ListFormatConverter("@");
		List<String> aValues = aConverter.convert("a@b@c");
		assertNotNull(aValues);
		assertEquals(3, aValues.size());
		assertEquals("a", aValues.get(0));
		assertEquals("b", aValues.get(1));
		assertEquals("c", aValues.get(2));
	}
	
	@Test
	public void testConvertExtended() throws ConversionException {
		ListFormatConverter aConverter = new ListFormatConverter("@");
		List<String> aValues = aConverter.convert("Some, more, values@some@@more");
		assertNotNull(aValues);
		assertEquals(4, aValues.size());
		assertEquals("Some, more, values", aValues.get(0));
		assertEquals("some", aValues.get(1));
		assertEquals("", aValues.get(2));
		assertEquals("more", aValues.get(3));
	}

	@Test
	public void testConvertNoSeparator() throws ConversionException {
		exception.expect(ConversionException.class);
		ListFormatConverter aConverter = new ListFormatConverter();
		aConverter.convert("a@b@c");
	}
	
	@Test
	public void testConvertNullValue() throws ConversionException {
		exception.expect(ConversionException.class);
		ListFormatConverter aConverter = new ListFormatConverter(",");
		aConverter.convert(null);
	}
	
	@Test
	public void testConvertEmptyValue() throws ConversionException {
		ListFormatConverter aConverter = new ListFormatConverter(",");
		List<String> aValues = aConverter.convert("");
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertEquals("", aValues.get(0));
	}
	
	@Test
	public void testConvertEmptySeparator() throws ConversionException {
		ListFormatConverter aConverter = new ListFormatConverter("");
		List<String> aValues = aConverter.convert("abcd");
		assertNotNull(aValues);
		assertEquals(5, aValues.size());
		assertEquals("", aValues.get(0));
		assertEquals("a", aValues.get(1));
		assertEquals("b", aValues.get(2));
		assertEquals("c", aValues.get(3));
		assertEquals("d", aValues.get(4));
	}

}
