package edu.arizona.kfs.pdp.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.pdp.businessobject.ACHBank;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.pdp.service.AchBankService;

@Transactional
public class AchBankServiceImpl extends org.kuali.kfs.pdp.service.impl.AchBankServiceImpl implements AchBankService {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchBankServiceImpl.class);
	
	private BatchInputFileService batchInputFileService;
	private BatchInputFileType achBankInputFileType;

	@Override
	public boolean loadAchBankFile(String inputFileName) {
		FileInputStream fileContents;
		
		try {
			fileContents = new FileInputStream(inputFileName);
		}
		catch (FileNotFoundException e1) {
			LOG.error("file to parse not found" + inputFileName, e1);
			throw new RuntimeException("Cannot find the file requested to be parsed" + inputFileName + " " + e1.getMessage(), e1);
		}
		
		Collection<ACHBank> banks = null;
		try {
			byte[] fileByteContent = IOUtils.toByteArray(fileContents);
			banks = (Collection <ACHBank>) batchInputFileService.parse(achBankInputFileType, fileByteContent);
		}
		catch (IOException e) {
			LOG.error("error while getting file bytes: " + e.getMessage(), e);
			throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
		}
		catch (ParseException e) {
			LOG.error("Error encountered parsing file " + e.getMessage());
			throw new RuntimeException("Error encountered parsing file " + e.getMessage(), e);
		}
		
		if (banks == null || banks.isEmpty()) {
			LOG.warn("No banks in input file " + inputFileName);
		}
		
		LOG.info("Total banks loaded: " + Integer.toString(banks.size()));
		return true;
	}

	public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
		this.batchInputFileService = batchInputFileService;
	}

	public void setAchBankInputFileType(BatchInputFileType achBankInputFileType) {
		this.achBankInputFileType = achBankInputFileType;
	}

}
