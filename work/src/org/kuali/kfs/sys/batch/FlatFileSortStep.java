/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

import org.kuali.kfs.gl.batch.BatchSortUtil;

/**
 * General purpose sort step which can be configured with any Comparator class.
 *
 * @author jonathan
 */
public class FlatFileSortStep extends AbstractStep {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlatFileSortStep.class);
	protected String inputFileName;
	protected String outputFileName;
	protected Class<? extends Comparator<String>> lineComparatorClass;

	@Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        try {
        	if ( new File(inputFileName).canRead() ) {
        		BatchSortUtil.sortTextFileWithFields(inputFileName, outputFileName, lineComparatorClass.newInstance());
        	} else {
        		LOG.warn( "Input file: " + inputFileName + " does not exist.");
        	}
		} catch (Exception ex) {
			throw new RuntimeException( "Unable to instantiate comparator class for sort: " + lineComparatorClass, ex );
		}
		return true;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public void setLineComparatorClass( Class<? extends Comparator<String>> lineComparatorClass) {
		this.lineComparatorClass = lineComparatorClass;
	}
}
