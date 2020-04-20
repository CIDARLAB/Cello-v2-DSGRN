/**
 * Copyright (C) 2020 Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.v2.DSGRN.results.netlist.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.JSON.JSONUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;

/**
 * Tests for the {@link SBOLMolecularModelToPoPSNetlistConverter} class.
 *
 * @author Timothy Jones
 *
 * @date 2020-04-09
 *
 */
public class SBOLMolecularModelToPoPSNetlistConverterTest {

	private static String path = SBOLMolecularModelToPoPSNetlistConverterTest.class.getName()
	        .replaceAll("\\.", Utils.getFileSeparator()) + "_class";
	private static String file = "DSGRN_Design_Voigt_Network_1_2020_04_17T17_11_04_105248_collection.xml";
	private static SBOLDocument document;

	/**
	 * Initialize resources for the tests.
	 *
	 * @throws SBOLValidationException Unable to validate sample file.
	 * @throws IOException             Unable to read sample file.
	 * @throws SBOLConversionException Unable to deserialize sample file.
	 */
	@BeforeClass
	public static void init() throws SBOLValidationException, IOException, SBOLConversionException {
		InputStream is = Utils.getResourceAsStream(path + Utils.getFileSeparator() + file);
		document = SBOLReader.read(is);
	}

	/**
	 * Test the {@link SBOLMolecularModelToPoPSNetlistConverter} on a DSGRN network
	 * with four nodes.
	 *
	 * @throws CelloException Unable to convert sample network.
	 * @throws IOException
	 */
	@Test
	public void convert_DSGRNVoigtNetwork_ShouldReturnValidNetlist() throws CelloException, IOException {
		SBOLMolecularModelToPoPSNetlistConverter conv = new SBOLMolecularModelToPoPSNetlistConverter();
		Netlist netlist = conv.convert(document);
		netlist.setInputFilename(file);
		// write to String
		StringWriter w = new StringWriter();
		w.write(JSONUtils.getStartEntryString());
		netlist.writeJSON(1, w);
		w.write(JSONUtils.getEndEntryString());
		w.flush();
		w.close();
		String result = w.toString();
		// reference result
		String referenceFile = "convert_DSGRNVoigtNetwork_ShouldReturnValidNetlist.json";
		String ref = Utils.getResourceAsString(path + Utils.getFileSeparator() + referenceFile);
		// compare
		assert (result.equals(ref));
	}

}
