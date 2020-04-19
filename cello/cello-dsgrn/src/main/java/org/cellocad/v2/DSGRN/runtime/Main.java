/**
 * Copyright (C) 2018 Massachusetts Institute of Technology (MIT)
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

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.DSGRN.common.DSGRNUtils;
import org.cellocad.v2.DSGRN.results.netlist.converter.SBOLMolecularModelToPoPSNetlistConverter;
import org.cellocad.v2.DSGRN.runtime.environment.DSGRNRuntimeEnv;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.application.ApplicationConfiguration;
import org.cellocad.v2.common.application.ApplicationUtils;
import org.cellocad.v2.common.file.dot.utils.Dot2Pdf;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraintUtils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.export.runtime.EXRuntimeObject;
import org.cellocad.v2.placing.runtime.PLRuntimeObject;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.cellocad.v2.technologyMapping.runtime.TMRuntimeObject;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;

/**
 * The executable class for the <i>DSGRN</i> application.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date 2020-04-19
 *
 */
public class Main {

	/**
	 * Main method for the <i>DSGRN</i> application.
	 *
	 * @param args The command line arguments.
	 * @throws SBOLConversionException Unable to convert network file.
	 * @throws SBOLValidationException Unable to validate network file.
	 * @throws IOException             Unable to load file.
	 */
	public static void main(String[] args)
	        throws CelloException, SBOLValidationException, IOException, SBOLConversionException {
		/*
		 * Preparation
		 */
		// RuntimeEnv
		DSGRNRuntimeEnv runEnv = new DSGRNRuntimeEnv(args);
		runEnv.setName("DSGRN");
		if (!runEnv.isValid()) {
			throw new RuntimeException("DSGRNRuntimeEnv is invalid!");
		}
		/*
		 * Setup Logger
		 */
		Main.setupLogger(runEnv);
		/*
		 * Application setup
		 */
		// Netlist
		String inputFilePath = runEnv.getOptionValue(ArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new CelloException("Input file does not exist!");
		}
		SBOLMolecularModelToPoPSNetlistConverter converter = new SBOLMolecularModelToPoPSNetlistConverter();
		SBOLDocument document = SBOLReader.read(inputFile);
		Netlist netlist = converter.convert(document);
		netlist.setInputFilename(inputFilePath);
		// ApplicationConfiguration
		ApplicationConfiguration appCfg;
		try {
			appCfg = ApplicationUtils.getApplicationConfiguration(runEnv, ArgString.OPTIONS,
			        DSGRNUtils.getApplicationConfiguration());
		} catch (IOException e) {
			throw new RuntimeException("Error with application configuration file.");
		}
		if (!appCfg.isValid()) {
			throw new RuntimeException("ApplicationConfiguration is invalid!");
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, ArgString.USERCONSTRAINTSFILE,
		        ArgString.INPUTSENSORFILE, ArgString.OUTPUTDEVICEFILE);
		if (!td.isValid()) {
			throw new CelloException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv,
		        ArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		// Results
		File outputDir = new File(runEnv.getOptionValue(ArgString.OUTPUTDIR));
		Results results = new Results(outputDir);
		// Write netlist
		Main.writeJSONForNetlist(runEnv, netlist, inputFilePath);
		File importDotFile = new File(outputDir, netlist.getName() + "_dsgrn_import" + ".dot");
		NetlistUtils.writeDotFileForGraph(netlist, importDotFile.getAbsolutePath());
		Dot2Pdf.dot2pdf(importDotFile);
		/*
		 * Stages
		 */
		Stage currentStage = null;
		/*
		 * technologyMapping
		 */
		currentStage = appCfg.getStageByName("technologyMapping");
		TMRuntimeObject TM = new TMRuntimeObject(currentStage, td, netlistConstraint, netlist, results, runEnv);
		TM.execute();
		// Write netlist
		Main.writeJSONForNetlist(runEnv, netlist, inputFilePath);
		File tmDotFile = new File(outputDir, netlist.getName() + "_technologyMapping" + ".dot");
		NetlistUtils.writeDotFileForGraph(netlist, tmDotFile.getAbsolutePath());
		Dot2Pdf.dot2pdf(tmDotFile);
		/*
		 * placing
		 */
		currentStage = appCfg.getStageByName("placing");
		PLRuntimeObject PL = new PLRuntimeObject(currentStage, td, netlistConstraint, netlist, results, runEnv);
		PL.execute();
		// Write netlist
		Main.writeJSONForNetlist(runEnv, netlist, inputFilePath);
		/*
		 * export
		 */
		currentStage = appCfg.getStageByName("export");
		EXRuntimeObject EX = new EXRuntimeObject(currentStage, td, netlistConstraint, netlist, results, runEnv);
		EX.execute();
		// Write netlist
		Main.writeJSONForNetlist(runEnv, netlist, inputFilePath);
	}

	protected static void writeJSONForNetlist(DSGRNRuntimeEnv runEnv, Netlist netlist, String inputFilePath) {
		String outputNetlistFilePath = null;
		outputNetlistFilePath = runEnv.getOptionValue(ArgString.OUTPUTNETLIST);
		if (outputNetlistFilePath == null) {
			outputNetlistFilePath = "";
			outputNetlistFilePath += runEnv.getOptionValue(ArgString.OUTPUTDIR);
			outputNetlistFilePath += Utils.getFileSeparator();
			outputNetlistFilePath += Utils.getFilename(inputFilePath);
			outputNetlistFilePath += "_outputNetlist";
			outputNetlistFilePath += ".json";
		}
		NetlistUtils.writeJSONForNetlist(netlist, outputNetlistFilePath);
	}

	/**
	 * Setup the logger using the {@link DSGRNRuntimeEnv} defined by parameter
	 * {@code runEnv}.
	 *
	 * @param runEnv The runtime environment.
	 */
	protected static void setupLogger(DSGRNRuntimeEnv runEnv) {
		String logfile = runEnv.getOptionValue(ArgString.LOGFILENAME);
		if (logfile == null) {
			logfile = "log.log";
		}
		logfile = runEnv.getOptionValue(ArgString.OUTPUTDIR) + Utils.getFileSeparator() + logfile;
		// the logger will write to the specified file
		System.setProperty("logfile.name", logfile);
		logger = LogManager.getLogger(Main.class);
	}

	/**
	 * Returns the Logger for the {@link Main} class.
	 *
	 * @return The logger for the {@link Main} class.
	 */
	static protected Logger getLogger() {
		return Main.logger;
	}

	private static Logger logger;

}
