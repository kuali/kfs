package edu.arizona.kfs.pdp.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import edu.arizona.kfs.pdp.service.PayeeAchAccountService;

public class LoadAchPayeeBankAcctStep extends AbstractStep {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadAchPayeeBankAcctStep.class);
	
	private String reportPath;
	private String reportPrefix;
	private String reportExtension;
	
	private BatchInputFileService batchInputFileService;
	private BatchInputFileType achPayeeBankAcctInputFileType;
	private DateTimeService dateTimeService;
	private PayeeAchAccountService payeeAchAccountService;
	
	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		LOG.debug("execute() started");
		
		List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(achPayeeBankAcctInputFileType);
		boolean processSuccess = true;
		List<String> failedFiles = new ArrayList<String>();
		
		for (String inputFileName : fileNamesToLoad) {
			removeDoneFile(inputFileName);
			
			processSuccess = payeeAchAccountService.loadAchPayeeAccountFile(inputFileName);
			
			if (!processSuccess) {
				failedFiles.add(inputFileName);
			}
		}
		
		if (failedFiles.size() > 0) {
			writeErrorReport(failedFiles);
		}
		
		return processSuccess;
	}
	
	protected void writeErrorReport(List<String> fileNames) {
		File reportFile =  new File(reportPath + "/" + reportPrefix + "_" + dateTimeService.toDateStringForFilename(dateTimeService.getCurrentDate()) + "." + reportExtension);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new PrintWriter(reportFile));
			
			writer.write(dateTimeService.toDateString(dateTimeService.getCurrentDate()));
			writer.newLine();
			writer.write("Payee ACH Account Interface Failed Files");
			writer.newLine();
			writer.write("File Name");
			writer.newLine();
			
			for (String fileName: fileNames) {
				writer.write(fileName);
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
		}
		catch (FileNotFoundException e) {
			LOG.error(reportFile + " not found " + " " + e.getMessage());
			throw new RuntimeException(reportFile + " not found" + e.getMessage(), e);
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

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public void setReportPrefix(String reportPrefix) {
		this.reportPrefix = reportPrefix;
	}

	public void setReportExtension(String reportExtension) {
		this.reportExtension = reportExtension;
	}

	public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
		this.batchInputFileService = batchInputFileService;
	}

	public void setAchPayeeBankAcctInputFileType(BatchInputFileType achPayeeBankAcctInputFileType) {
		this.achPayeeBankAcctInputFileType = achPayeeBankAcctInputFileType;
	}

	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public void setPayeeAchAccountService(PayeeAchAccountService payeeAchAccountService) {
		this.payeeAchAccountService = payeeAchAccountService;
	}

}
