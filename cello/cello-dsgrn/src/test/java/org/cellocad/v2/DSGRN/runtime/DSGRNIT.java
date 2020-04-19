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
package org.cellocad.v2.DSGRN.runtime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLValidationException;

/**
 * DSGRN integration test.
 *
 * @author Timothy Jones
 *
 * @date 2020-04-19
 *
 */
public class DSGRNIT {

	private static String path = DSGRNIT.class.getName()
	        .replaceAll("\\.", Utils.getFileSeparator()) + "_class";
	private static String file = "DSGRN_Design_Voigt_Network_1_2020_04_17T17_11_04_105248_collection.xml";

	@BeforeClass
	public static void init() throws IOException, CelloException {
		Path dir = Files.createTempDirectory("cello_");
		args = new String[] { "-" + ArgString.INPUTNETLIST,
		        Utils.getResource(path + Utils.getFileSeparator() + file).getFile(),
		        "-" + ArgString.USERCONSTRAINTSFILE,
		        Utils.getResource("lib/files/v2/ucf/SC/SC1C1G1T1.UCF.json").getFile(),
		        "-" + ArgString.INPUTSENSORFILE,
		        Utils.getResource("lib/files/v2/input/SC/SC1C1G1T1.input.json").getFile(),
		        "-" + ArgString.OUTPUTDEVICEFILE,
		        Utils.getResource("lib/files/v2/output/SC/SC1C1G1T1.output.json").getFile(),
		        "-" + ArgString.OUTPUTDIR, dir.toString(),
		        "-" + ArgString.PYTHONENV, "python" };

	}

	@Test
	public void execute_None_ShouldReturn()
	        throws CelloException, SBOLValidationException, IOException, SBOLConversionException {
		Main.main(args);
	}

	private static String[] args;

}
