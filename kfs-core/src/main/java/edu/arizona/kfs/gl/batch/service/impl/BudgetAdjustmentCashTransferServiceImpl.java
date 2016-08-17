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
package edu.arizona.kfs.gl.batch.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.PosterEntriesStep;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.gl.GeneralLedgerConstants;
import edu.arizona.kfs.gl.batch.service.BudgetAdjustmentCashTransferService;
import edu.arizona.kfs.gl.businessobject.BudgetAdjustmentTransaction;
import edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao;


@Transactional
public class BudgetAdjustmentCashTransferServiceImpl implements BudgetAdjustmentCashTransferService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentCashTransferServiceImpl.class);

    protected BudgetAdjustmentTransactionDao budgetAdjustmentTransactionDao;
    protected OptionsService optionsService;
    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;
    protected RunDateService runDateService;
    protected UniversityDateService universityDateService;
    protected String batchFileDirectoryName;
    protected String inputFile;
    protected String validFile;
    protected String errorFile;
    
    @Override
    public void extractAndSaveBudgetAdjustmentEntries() {
    	
    	//retrieve document type and origination code parameter values
    	Collection<String> documentTypeCodes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.CASH_TRANSFER_DOC_TYPE_CODES);
    	Collection<String> originationCodes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.CASH_TRANSFER_ORIGINATION_CODES);
      
        //get current year system options
    	 SystemOptions options = getOptionsService().getOptions(universityDateService.getCurrentFiscalYear());
         if (ObjectUtils.isNull(options)) {
             options = getOptionsService().getCurrentYearOptions();
         }
         
         //this.inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
         //this.validFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
         //this.errorFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
    }

    @Override
    public void generateBudgetAdjustmentCashTransferTransactions() {
    	LOG.debug("generateBudgetAdjustmentCashTransferTransactions() started");
        
        Date executionDate = dateTimeService.getCurrentSqlDate();
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());
    	   	
    	 try {
    		 PrintStream OUTPUT_GLE_FILE_ps = new PrintStream(batchFileDirectoryName + File.separator + GeneralLedgerConstants.CASH_TRANSFER_TRANSACTIONS_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
             
             int reportOriginEntryGenerated = 0;            
             List<String> docNumberList = new ArrayList<String>();
             
             //retrieve unique document numbers list             
             docNumberList = retrieveUniqueDocumentNumbersList();
             int reportBudgetAdjustDocLoaded = docNumberList.size();
             
             //now that we have a list of the RBC numbers, process transactions for each RBC number in file
             //TODO reportOriginEntryGenerated = generateCashTransferGeneralLedgerEntries(docNumberList, runDate, OUTPUT_GLE_FILE_ps);
                                                        
             //delete all budget adjustment transactions  
             budgetAdjustmentTransactionDao.deleteAllBudgetAdjustmentTransactions();
             OUTPUT_GLE_FILE_ps.close();
             //reportWriterService.writeStatisticLine("GLBA RBC TRANS LOADED           (GL_BUDGET_ADJUST_TRN_T) %,9d", reportBudgetAdjustDocLoaded);
             //reportWriterService.writeStatisticLine("TRANSACTIONS GENERATED                                   %,9d", reportOriginEntryGenerated);
             
             // create a done file
             String budgetAdjustmentCashTransferDoneFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.CASH_TRANSFER_TRANSACTIONS_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
             File doneFile = new File (budgetAdjustmentCashTransferDoneFileName);
             if (!doneFile.exists()){
                 try {
                     doneFile.createNewFile();
                 } catch (IOException e) {
                     throw new RuntimeException("Error creating cash transfer done file", e);
                 }
             }         

         }
         catch (FileNotFoundException e) {
             throw new RuntimeException("generateBudgetAdjustmentCashTransferTransactions Stopped: " + e.getMessage(), e);
         }
    }
    
    /**
     * This method returns a list of unique document numbers which are needed so that
     * we can group budget adjustment transactions by budget system document number in
     * order to determine if the transactions, as a group, cross income streams.
     */
    protected List<String> retrieveUniqueDocumentNumbersList() {   
    	 int reportBudgetAdjustTranRetrieved = 0;
    	 Iterator budgetAdjustmentTransactions;
    	 List<String> docNumberList = new ArrayList<String>();
    	 
    	 try {
             budgetAdjustmentTransactions = budgetAdjustmentTransactionDao.getAllBudgetAdjustmentTransactions();
         }
         catch (RuntimeException re) {
             LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage());
             throw new RuntimeException("generateBACashTransferTransactions Stopped: " + re.getMessage(), re);
         }
         
         while (budgetAdjustmentTransactions.hasNext()) {
             BudgetAdjustmentTransaction ba = new BudgetAdjustmentTransaction();
             try {
                 ba = (BudgetAdjustmentTransaction) budgetAdjustmentTransactions.next();
                 reportBudgetAdjustTranRetrieved++;
                 
                 if (!docNumberList.contains(ba.getDocumentNumber())) {
                	 docNumberList.add(ba.getDocumentNumber());                     
                 }                      
             }
             catch (RuntimeException re) {
                 LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage());
                 throw new RuntimeException("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage(), re);
             }
             catch (Exception e) {
                 List errorList = new ArrayList();
                 errorList.add(new Message(e.toString() + " occurred for this record.", Message.TYPE_FATAL));
                 //reportWriterService.writeError(ba, errorList);
             }
         }
         //reportWriterService.writeStatisticLine("GLBA RECORDS READ               (GL_BUDGET_ADJUST_TRN_T) %,9d", reportBudgetAdjustTranRetrieved);
    }
    
    public void setBudgetAdjustmentTransactionDao(BudgetAdjustmentTransactionDao budgetAdjustmentTransactionDao) {
        this.budgetAdjustmentTransactionDao = budgetAdjustmentTransactionDao;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
  
    public OptionsService getOptionsService() {
        return optionsService;
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    public void setRunDateService(RunDateService runDateService) {
        this.runDateService = runDateService;
    }
    
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
    
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
