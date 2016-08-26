package edu.arizona.kfs.gl.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.kuali.kfs.gl.batch.BatchSortUtil;
import org.kuali.kfs.gl.batch.ScrubberSortComparator;
import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.gl.GeneralLedgerConstants;

public class BudgetAdjustmentMergeSortStep extends AbstractStep {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentMergeSortStep.class);
	
	protected String batchFileDirectoryName;
	
	@Override
	public boolean execute(String jobName, Date jobRunDate) {
		String budgetOfficeFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ENTERPRISE_FEED + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
		String laborLedgerFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.LABOR_GL_ENTRY_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
		String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.BUDGET_ADJUSTMENT_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
		
		// names of the two .done files that we are checking to see if they exist
		String budgetOfficeDoneFileName = GeneralLedgerConstants.BatchFileSystem.ENTERPRISE_FEED + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
		String laborLedgerDoneFileName = GeneralLedgerConstants.BatchFileSystem.LABOR_GL_ENTRY_FILE + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
		
		// pull out a list of the .done files in the batch file directory
		File directory = new File(batchFileDirectoryName);
		FileFilter doneFileFilter = new SuffixFileFilter(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
		File[] doneFiles = directory.listFiles(doneFileFilter);
		
		boolean laborLedgerDoneFileExists = false;
		boolean budgetOfficeDoneFileExists = false;
		
		// check each file in the list to see if 
		// the files we need to merge exist in the directory
		for (File doneFile : doneFiles) {
			String fileName = doneFile.getName();
			
			if (fileName.equals(laborLedgerDoneFileName)) {
				laborLedgerDoneFileExists = true;
			} 
			
			if (fileName.equals(budgetOfficeDoneFileName)){
				budgetOfficeDoneFileExists = true;
			}
		}
		
		// checks to see which files actually exists in the directory
		// then pulls the necessary .data files to be processed and put into the output file.
		if (laborLedgerDoneFileExists && budgetOfficeDoneFileExists) {	
			String mergedFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.BUDGET_ADJUSTMENT_MERGE_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
			
			try {
				mergeFiles(laborLedgerFileName, budgetOfficeFileName, mergedFile);
			} catch (Exception e) {
				LOG.error( "Exception merging the files", e );
				e.printStackTrace();
			}
			
			BatchSortUtil.sortTextFileWithFields(mergedFile, outputFile, new ScrubberSortComparator());
			
		} else if (laborLedgerDoneFileExists && !budgetOfficeDoneFileExists) {
			// if only the labor ledger .done file exists, process the Labor Ledger File	
			BatchSortUtil.sortTextFileWithFields(laborLedgerFileName, outputFile, new ScrubberSortComparator());		
			
		} else if (budgetOfficeDoneFileExists && !laborLedgerDoneFileExists) {
			// if only the budget office .done file exists, process the Budget Office File
			BatchSortUtil.sortTextFileWithFields(budgetOfficeFileName, outputFile, new ScrubberSortComparator());
			
		} else {
			// if neither .done file exists, produce an empty file then process the file
			String emptyFileName = batchFileDirectoryName + File.separator + "temp" + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
			
			// creates the new temp file
			File emptyFile = new File(emptyFileName);
			try {
				emptyFile.createNewFile();
			} catch (IOException e) {
				LOG.error("Error creating the temp file", e);
				e.printStackTrace();
			}
			
			BatchSortUtil.sortTextFileWithFields(emptyFileName, outputFile, new ScrubberSortComparator());
			
			// deletes the new temp file once processed.
			FileUtils.deleteQuietly(emptyFile);
		}
				
		return true;
	}
	
	/**
	 * Merges the contents of the two necessary .data files into one file.
	 * @param laborLedgerFileName  One of two .data files to be merged together.  (Labor Ledger File)
	 * @param budgetOfficeFileName One of two .data files to be merged together.  (Budget Office File)
	 * @param mergedFile  This is the file that the contents of the two .data files will be copied to.
	 * @throws Exception
	 */
	private static void mergeFiles(String laborLedgerFileName, String budgetOfficeFileName, String mergedFile) throws Exception {
		BufferedReader laborLedgerFile = new BufferedReader(new FileReader(laborLedgerFileName));
		BufferedReader budgetOfficeFile = new BufferedReader(new FileReader(budgetOfficeFileName));
			
		BufferedWriter output = new BufferedWriter(new FileWriter(mergedFile));
			
		transferContents(laborLedgerFile, output);
		transferContents(budgetOfficeFile, output);
			
		laborLedgerFile.close();
		budgetOfficeFile.close();
		output.close();
	}
	
	
	/**
	 * Transfers the contents of the source file (.data) to the output file.
	 * @param sourceFile File that needs to be merged
	 * @param outputFile File that will be written to
	 * @throws Exception
	 */
	private static void transferContents(BufferedReader sourceFile, BufferedWriter outputFile) throws Exception {
		char[] buffer = new char[1024 * 16];
		int len = 0;
		while ((len = sourceFile.read(buffer)) >= 0) {
			outputFile.write(buffer, 0, len);
		}
		
		sourceFile.close();
		outputFile.close();
	}
	
	public void setBatchFileDirectoryName(String batchFileDirectoryName) {
		this.batchFileDirectoryName = batchFileDirectoryName;
	}
}
