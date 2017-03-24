package edu.arizona.kfs.module.ld.service;

import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.coa.businessobject.AccountExtension;

public interface EreSweepFileHandlerService {
	
	/**
	 * Starts the process to create the EreSweep files
	 */
	public void startUp();
	
	/**
	 * Creates EreSweep.Recon and EreSweep.done file, and closes the PrintWriter
	 */
	public void closeConnection();
	
	/**
	 * Prepares and formats data that will be written to the EreSweep report
	 * 
	 * @param accountExtension
	 * @param fiscalPeriod
	 */
	public void prepareOutputFile(AccountExtension accountExtension, String fiscalPeriod, KualiDecimal cbAmount, LedgerBalance ledgerBalance);
	
	/**
	 * Prepares and formats data that will be written to the EreSweepError report
	 * 
	 * @param fiscalPeriod
	 */
	public void prepareErrorFile(String fiscalPeriod, KualiDecimal cbAmount, LedgerBalance ledgerBalance);

}
