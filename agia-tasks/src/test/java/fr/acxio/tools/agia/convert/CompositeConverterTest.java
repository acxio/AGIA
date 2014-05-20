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

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CompositeConverterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testConvert() throws ConversionException {
		List<FormatConverter> aConverters = new ArrayList<FormatConverter>(2);
		aConverters.add(new DateFormatConverter("yyyy-MM-dd", "dd-MM-yyyy"));
		aConverters.add(new DateFormatConverter("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS"));
		CompositeConverter aConverter = new CompositeConverter(aConverters);
		List<String> aValues = aConverter.convert("2012-07-02");
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertEquals("2012-07-02T00:00:00.000", aValues.get(0));
	}

	@Test
	public void testConvertExtended() throws ConversionException {
		List<FormatConverter> aConverters = new ArrayList<FormatConverter>(2);
		aConverters.add(new ListFormatConverter("@"));
		aConverters.add(new ListFormatConverter(","));
		CompositeConverter aConverter = new CompositeConverter(aConverters);
		List<String> aValues = aConverter.convert("a,b,c@d,e@f");
		assertNotNull(aValues);
		assertEquals(6, aValues.size());
		assertEquals("a", aValues.get(0));
		assertEquals("b", aValues.get(1));
		assertEquals("c", aValues.get(2));
		assertEquals("d", aValues.get(3));
		assertEquals("e", aValues.get(4));
		assertEquals("f", aValues.get(5));
	}
	
	@Test
	public void testConvertNoConverter() throws ConversionException {
		CompositeConverter aConverter = new CompositeConverter();
		List<String> aValues = aConverter.convert("a,b,c@d,e@f");
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertEquals("a,b,c@d,e@f", aValues.get(0));
	}

	@Test
	public void testConvertNullValue() throws ConversionException {
		CompositeConverter aConverter = new CompositeConverter();
		List<String> aValues = aConverter.convert(null);
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertNull(aValues.get(0));
	}
	
	@Test
	public void testConvertNotNullValue() throws ConversionException {
		List<FormatConverter> aConverters = new ArrayList<FormatConverter>(2);
		aConverters.add(new NotNullConverter());
		CompositeConverter aConverter = new CompositeConverter(aConverters);
		List<String> aValues = aConverter.convert(null);
		assertNotNull(aValues);
		assertEquals(0, aValues.size());
	}
	
	@Test
	public void testConvertConverterException() throws ConversionException {
		exception.expect(ConversionException.class);
		List<FormatConverter> aConverters = new ArrayList<FormatConverter>(2);
		aConverters.add(new DateFormatConverter()); // Illegal
		aConverters.add(new DateFormatConverter("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS"));
		CompositeConverter aConverter = new CompositeConverter(aConverters);
		aConverter.convert("2012-07-02");
	}

}
