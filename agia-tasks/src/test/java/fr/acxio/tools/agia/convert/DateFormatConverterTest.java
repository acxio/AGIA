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
public class DateFormatConverterTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testConvert() throws ConversionException {
		DateFormatConverter aConverter = new DateFormatConverter("yyyy-MM-dd", "dd-MM-yyyy");
		List<String> aValues = aConverter.convert("2012-07-02");
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertEquals("02-07-2012", aValues.get(0));
	}

	@Test
	public void testConvertToISO() throws ConversionException {
		DateFormatConverter aConverter = new DateFormatConverter("dd-MM-yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS");
		List<String> aValues = aConverter.convert("02-07-2012");
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertEquals("2012-07-02T00:00:00.000", aValues.get(0));
	}
	
	@Test
	public void testConvertFrToISO() throws ConversionException {
		DateFormatConverter aConverter = new DateFormatConverter("dd/MM/yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS");
		List<String> aValues = aConverter.convert("13/03/2013");
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertEquals("2013-03-13T00:00:00.000", aValues.get(0));
	}
	
	@Test
	public void testNoPattern() throws ConversionException {
		exception.expect(ConversionException.class);
		DateFormatConverter aConverter = new DateFormatConverter();
		aConverter.convert("02-07-2012");
	}
	
	@Test
	public void testBadPattern() throws ConversionException {
		exception.expect(IllegalArgumentException.class);
		DateFormatConverter aConverter = new DateFormatConverter("abcde", "fghij");
		aConverter.convert("02-07-2012");
	}
	
	@Test
	public void testBadValue() throws ConversionException {
		exception.expect(ConversionException.class);
		DateFormatConverter aConverter = new DateFormatConverter("yyyy-MM-dd", "dd-MM-yyyy");
		aConverter.convert("abcde");
	}
	
	@Test
	public void testNullValue() throws ConversionException {
		exception.expect(ConversionException.class);
		DateFormatConverter aConverter = new DateFormatConverter("yyyy-MM-dd", "dd-MM-yyyy");
		aConverter.convert(null);
	}
}
