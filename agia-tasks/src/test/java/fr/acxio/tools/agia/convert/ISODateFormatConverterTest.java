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
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ISODateFormatConverterTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testConvert() throws ConversionException {
		ISODateFormatConverter aConverter = new ISODateFormatConverter("yyyy-MM-dd");
		List<String> aValues = aConverter.convert("2012-07-02");
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertEquals("2012-07-02T00:00:00.000+02:00", aValues.get(0));
	}

	@Test
	public void testConvertFrToISO() throws ConversionException {
		ISODateFormatConverter aConverter = new ISODateFormatConverter("dd/MM/yyyy");
		List<String> aValues = aConverter.convert("13/03/2013");
		assertNotNull(aValues);
		assertEquals(1, aValues.size());
		assertEquals("2013-03-13T00:00:00.000+01:00", aValues.get(0));
	}

}
