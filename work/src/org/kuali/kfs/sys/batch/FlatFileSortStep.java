/*
 * Copyright 2011 The Regents of the University of California.
 */
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

import org.kuali.kfs.gl.batch.BatchSortUtil;
import org.kuali.kfs.sys.batch.AbstractStep;

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
