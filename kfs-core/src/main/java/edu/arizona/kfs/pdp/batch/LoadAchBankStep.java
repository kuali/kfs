package edu.arizona.kfs.pdp.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import edu.arizona.kfs.pdp.service.AchBankService;

public class LoadAchBankStep extends AbstractStep {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadAchBankStep.class);
	
	private AchBankService achBankService;
	private BatchInputFileService batchInputFileService;
	private BatchInputFileType achBankInputFileType;
	private String reportPath;
	private String reportPrefix;
	private String reportExtension;
	private DateTimeService dateTimeService;

	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		LOG.debug("execute() started");
		List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(achBankInputFileType);
		boolean processSuccess = true;
		
		for (String inputFileName : fileNamesToLoad) {
			try {
				removeDoneFile(inputFileName);
				
				processSuccess = achBankService.loadAchBankFile(inputFileName);
				
			}
			catch (Exception e) {
				writeErrorReport(inputFileName);
				throw new RuntimeException("Error encountered while loading bank file: " + e.getMessage(), e);
			}
		}
		
		return processSuccess;
	}

	protected void writeErrorReport(String fileName) {
		File reportFile = new File(reportPath + "/" + reportPrefix + "-" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + "." + reportExtension);
		BufferedWriter writer = null;
		try {
			writer =  new BufferedWriter(new PrintWriter(reportFile));
			
			writer.write(dateTimeService.toDateString(dateTimeService.getCurrentDate()));
			writer.newLine();
			writer.write("ACH Bank Interface Failed Files");
			writer.newLine();
			writer.write(fileName);
			writer.flush();
			writer.close();
		}
		catch (FileNotFoundException e) {
			LOG.error(reportFile + " not found " + " " + e.getMessage());
			throw new RuntimeException(reportFile + " not found " + e.getMessage(), e);
		}
		catch (IOException e) {
			LOG.error("Error writing to BufferedWriter " + e.getMessage());
			throw new RuntimeException("Error writing to BufferedWriter " + e.getMessage(), e);
		}
		finally {
			try {
				writer.close();
			}
			catch (Exception e) {
				LOG.error("Error when closing BufferedWriter");
			}
		}
	}
	
	protected void removeDoneFile(String dataFileName) {
		File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
		if (doneFile.exists()) {
			doneFile.delete();
		}
	}

	public void setAchBankService(AchBankService achBankService) {
		this.achBankService = achBankService;
	}

	public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
		this.batchInputFileService = batchInputFileService;
	}

	public void setAchBankInputFileType(BatchInputFileType achBankInputFileType) {
		this.achBankInputFileType = achBankInputFileType;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public void setReportPrefix(String reportPrefix) {
		this.reportPrefix = reportPrefix;
	}

	public void setReportExtension(String reportExtension) {
		this.reportExtension = reportExtension;
	}

	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}
	
}
