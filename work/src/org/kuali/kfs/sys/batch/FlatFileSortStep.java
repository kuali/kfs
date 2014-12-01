/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
