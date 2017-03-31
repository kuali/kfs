package edu.arizona.kfs.module.ld.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Date;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import edu.arizona.kfs.coa.businessobject.AccountExtension;
import edu.arizona.kfs.module.ld.LaborConstants;
import edu.arizona.kfs.module.ld.LaborParameterKeyConstants;
import edu.arizona.kfs.module.ld.batch.EreSweepStep;
import edu.arizona.kfs.module.ld.service.EreSweepFileHandlerService;

public class EreSweepFileHandlerServiceImpl implements EreSweepFileHandlerService {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EreSweepFileHandlerServiceImpl.class);

	private int rowCount = 0;
	
	private PrintWriter outWriter;
	private PrintWriter outErrorWriter;
	private PrintWriter reconPrintWriter;
	
	private ParameterService parameterService;
	private String batchFileDirectoryName;
	private DateTimeService dateTimeService;
	private  UniversityDateService universityDateService;

	
	@Override
	public void startUp() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Enter startUp() in EreSweepFileHandlerImpl.java " + System.currentTimeMillis());
		}

		String outputFile = batchFileDirectoryName + File.separator + LaborConstants.ERE_SWEEP_DATA;
		LOG.info("Output Location for data file = " + outputFile);
		try {
			outWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException("File Not Found Exception ", e);
		}
		catch (IOException e) {
			throw new RuntimeException("Error occurred when attempting to create new PrintWriter ", e);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Exit startUp() " + System.currentTimeMillis());
		}

		startUpErrorFile();
	}

	public void startUpErrorFile() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Enter startUpErrorFile() in EreSweepFileHandlerImpl.java " + System.currentTimeMillis());
		}

		String outputFile = batchFileDirectoryName + File.separator + LaborConstants.ERE_SWEEP_ERROR_DATA;
		LOG.info("Output Location for error file =  " + outputFile);
		try {
			outErrorWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException("File Not Found Exception ", e);
		}
		catch (IOException e) {
			throw new RuntimeException("Error occurred when attempting to start up the EreErrorSweep file", e);
		}
		catch (Exception e) {
			throw new RuntimeException("An Exception occurred when attempting to start up the EreErrorSweep file ", e);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Exit startup() " + System.currentTimeMillis());
		}
	}

	@Override
	public void closeConnection() {
		this.createReconFile();
		this.createDoneFile();
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Enter closeConnection() " + System.currentTimeMillis());
		}
		if (outWriter != null) {
			IOUtils.closeQuietly(outWriter);
		}
		if (reconPrintWriter != null) {
			IOUtils.closeQuietly(reconPrintWriter);
		}
		if (outErrorWriter != null) {
			IOUtils.closeQuietly(outErrorWriter);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Exit closeConnection() " + System.currentTimeMillis());
		}
	}

	private void createReconFile() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Enter createReconFile() " + System.currentTimeMillis());
		}
		
		String outputFileRecon = batchFileDirectoryName + File.separator + LaborConstants.ERE_SWEEP_RECON;
		try {
			reconPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFileRecon)));
			reconPrintWriter.format(LaborConstants.FORMAT_LINE, "c gl_entry_t " + rowCount + ";");
			reconPrintWriter.write("e 01;");
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException("File Not Found Exception ", e);
		}
		catch (IOException e) {
			throw new RuntimeException("Error occurred when attempting to create the EreSweep.recon file ", e);
		}
		catch (Exception e) {
			throw new RuntimeException("An Exception occurred when attempting to create the EreSweeo.recone file", e);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Exit createReconFile " + System.currentTimeMillis());
		}
	}

	private void createDoneFile() {
		String outputFileDone = batchFileDirectoryName + File.separator + LaborConstants.ERE_SWEEP_DONE;
		File doneFile = new File(outputFileDone);
		if (!doneFile.exists()) {
			try {
				doneFile.createNewFile();
			}
			catch (IOException e) {
				throw new RuntimeException("Unable to create EreSweep.done file  " + e);
			}
		}
	}

	@Override
	public void prepareOutputFile(AccountExtension accountExtension, KualiDecimal cbAmount, LedgerBalance ledgerBalance) {
		String originationCode = parameterService.getParameterValueAsString(EreSweepStep.class, LaborParameterKeyConstants.FRINGE_SWEEP_ORIGINATION_CODE);
		LaborLedgerPendingEntry pendingEntry = new LaborLedgerPendingEntry();
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Enter prepareOutputFile() " + System.currentTimeMillis());
		}

		LOG.info("FringeAccountExt = " + accountExtension.getInstitutionalFringeAccountExt());
		LOG.info("Zeroing out Account = " + ledgerBalance.getAccountNumber());
		Date today = new Date(dateTimeService.getCurrentTimestamp().getTime());

		// Setting up pendingEntry
		pendingEntry = setPendingEntryProperties(originationCode, today, cbAmount, ledgerBalance, pendingEntry);
		LaborOriginEntry entry = new LaborOriginEntry(pendingEntry);
		outWriter.format(LaborConstants.FORMAT_LINE, entry.getLine());
		rowCount++;

		// For Offset fringe account
		pendingEntry.setAccountNumber(accountExtension.getInstitutionalFringeAccountExt());
		pendingEntry.setSubAccountNumber(LaborConstants.EMPTY_SUB_ACCOUNT_STRING);
		pendingEntry.setFinancialSubObjectCode(LaborConstants.EMPTY_SUB_OBJECT_STRING);
		pendingEntry.setFinancialObjectCode(LaborConstants.FRINGE_FINANCIAL_OBJECT_CODE);
		pendingEntry.setDocumentNumber(LaborConstants.DOCUMENT_NUMBER_PREFIX + rowCount);
		pendingEntry.setTransactionLedgerEntryAmount(cbAmount);
		
		entry = new LaborOriginEntry(pendingEntry);
		outWriter.format(LaborConstants.FORMAT_LINE, entry.getLine());
		rowCount++;
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Exit prepareOutputFile() " + System.currentTimeMillis());
		}
	}

	@Override
	public void prepareErrorFile(KualiDecimal cbAmount, LedgerBalance ledgerBalance) {
		String originationCode = parameterService.getParameterValueAsString(EreSweepStep.class, LaborParameterKeyConstants.FRINGE_SWEEP_ORIGINATION_CODE);
		LaborLedgerPendingEntry pendingEntry = new LaborLedgerPendingEntry();
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Enter prepareErrorFile() " + System.currentTimeMillis());
		}
		LOG.info("Zeroing out Account = "+ ledgerBalance.getAccountNumber());
		Date today = new Date(dateTimeService.getCurrentTimestamp().getTime());
		
		//Setting pendingEntry attributes
		pendingEntry = setPendingEntryProperties(originationCode, today, cbAmount, ledgerBalance, pendingEntry);
		LaborOriginEntry entry = new LaborOriginEntry(pendingEntry);
		outErrorWriter.format(LaborConstants.FORMAT_LINE, entry.getLine());
		rowCount++;
		
		// For Offset fringe account
		pendingEntry.setAccountNumber(LaborConstants.PENDING_ENTRY_DEFAULT_ACCOUNT_NUMBER);
		pendingEntry.setSubAccountNumber(LaborConstants.EMPTY_SUB_ACCOUNT_STRING);
		pendingEntry.setFinancialSubObjectCode(LaborConstants.EMPTY_SUB_OBJECT_STRING);
		pendingEntry.setFinancialObjectCode(LaborConstants.FRINGE_FINANCIAL_OBJECT_CODE);
		pendingEntry.setDocumentNumber(LaborConstants.DOCUMENT_NUMBER_PREFIX + rowCount);
		pendingEntry.setTransactionLedgerEntryAmount(cbAmount);

		entry = new LaborOriginEntry(pendingEntry);
		outErrorWriter.format(LaborConstants.FORMAT_LINE, entry.getLine());
		rowCount++;
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Exit prepareOutputFile() " + System.currentTimeMillis());
		}
	}
	
	private LaborLedgerPendingEntry setPendingEntryProperties(String originationCode, Date today, KualiDecimal cbAmount, LedgerBalance ledgerBalance, LaborLedgerPendingEntry pendingEntry) {
		pendingEntry.setUniversityFiscalYear(ledgerBalance.getUniversityFiscalYear());
		pendingEntry.setChartOfAccountsCode(ledgerBalance.getChartOfAccountsCode());
		pendingEntry.setAccountNumber(ledgerBalance.getAccountNumber());
		pendingEntry.setSubAccountNumber(ledgerBalance.getSubAccountNumber());
		pendingEntry.setSubAccount(ledgerBalance.getSubAccount());
		pendingEntry.setFinancialObjectCode(ledgerBalance.getFinancialObjectCode());
		pendingEntry.setFinancialSubObjectCode(ledgerBalance.getFinancialSubObjectCode());
		pendingEntry.setFinancialBalanceTypeCode(LaborConstants.FRINGE_FINANCIAL_BALANCE_TYPE_CODE);
		pendingEntry.setFinancialObjectTypeCode(LaborConstants.FRINGE_FINANCIAL_OBJECT_TYPE_CODE);
		pendingEntry.setUniversityFiscalPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
		pendingEntry.setDocumentNumber(LaborConstants.DOCUMENT_NUMBER_PREFIX + rowCount);
		pendingEntry.setFinancialDocumentTypeCode(LaborConstants.FRINGE_FINANCIAL_DOCUMENT_TYPE_CODE);
		pendingEntry.setFinancialSystemOriginationCode(originationCode);
		pendingEntry.setTransactionLedgerEntryDescription(LaborConstants.FRINGE_TRANSACTION_LEDGER_ENTRY_DESCRIPTION);
		pendingEntry.setTransactionLedgerEntryAmount(cbAmount.multiply(new KualiDecimal(-1)));
		pendingEntry.setTransactionDebitCreditCode(KFSConstants.BLANK_SPACE);
		pendingEntry.setTransactionDate(today);
		pendingEntry.setOrganizationDocumentNumber(KFSConstants.BLANK_SPACE);
		pendingEntry.setOrganizationReferenceId(KFSConstants.BLANK_SPACE);
		pendingEntry.setReferenceOriginationCode(null);
		pendingEntry.setReferenceFinancialDocumentNumber(KFSConstants.EMPTY_STRING);
		pendingEntry.setTransactionEncumbranceUpdateCode(KFSConstants.EMPTY_STRING);
		pendingEntry.setTransactionPostingDate(today);
		pendingEntry.setPayPeriodEndDate(null);
		pendingEntry.setTransactionTotalHours(BigDecimal.ZERO);
		pendingEntry.setPayrollEndDateFiscalPeriodCode(KFSConstants.EMPTY_STRING);
		pendingEntry.setEmplid(ledgerBalance.getEmplid());
		pendingEntry.setPositionNumber(ledgerBalance.getPositionNumber());
		pendingEntry.setEmployeeRecord(null);
		pendingEntry.setEarnCode(KFSConstants.EMPTY_STRING);
		pendingEntry.setPayGroup(KFSConstants.EMPTY_STRING);
		pendingEntry.setSalaryAdministrationPlan(KFSConstants.EMPTY_STRING);
		pendingEntry.setGrade(KFSConstants.EMPTY_STRING);
		pendingEntry.setRunIdentifier(KFSConstants.EMPTY_STRING);
		pendingEntry.setLaborLedgerOriginalChartOfAccountsCode(KFSConstants.EMPTY_STRING);
		pendingEntry.setLaborLedgerOriginalAccountNumber(KFSConstants.EMPTY_STRING);
		pendingEntry.setLaborLedgerOriginalSubAccountNumber(KFSConstants.EMPTY_STRING);
		pendingEntry.setLaborLedgerOriginalFinancialObjectCode(KFSConstants.EMPTY_STRING);
		pendingEntry.setLaborLedgerOriginalSubAccountNumber(KFSConstants.EMPTY_STRING);
		return pendingEntry;
	}

	public void setBatchFileDirectoryName(String batchFileDirectoryName) {
		this.batchFileDirectoryName = batchFileDirectoryName;
	}

	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public void setUniversityDateService(UniversityDateService universityDateService) {
		this.universityDateService = universityDateService;
	}
	
}
