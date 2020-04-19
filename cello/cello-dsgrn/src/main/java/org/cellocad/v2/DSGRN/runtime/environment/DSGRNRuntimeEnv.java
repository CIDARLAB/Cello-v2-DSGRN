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
package org.cellocad.v2.DSGRN.runtime.environment;

import org.cellocad.v2.common.application.runtime.environment.ApplicationRuntimeEnv;

/**
 * A class for managing and parsing command line arguments for the <i>DSGRN</i>
 * application.
 *
 * @author Timothy Jones
 *
 * @date 2020-04-19
 *
 */
public class DSGRNRuntimeEnv extends ApplicationRuntimeEnv {

	/**
	 * Initializes a newly created DSGRNRuntimeEnv with command line arguments.
	 *
	 * @param args The command line arguments.
	 */
	public DSGRNRuntimeEnv(String[] args) {
		super(args);
	}

	/**
	 * Setter for <i>options</i>
	 */
	@Override
	protected void setOptions() {
		super.setOptions();
		// uncomment the line below to add options
		// Options options = this.getOptions();
	}
}
