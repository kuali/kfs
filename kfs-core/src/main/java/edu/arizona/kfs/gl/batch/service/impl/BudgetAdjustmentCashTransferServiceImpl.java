package edu.arizona.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.document.validation.impl.BudgetAdjustmentDocumentRuleConstants;
import org.kuali.kfs.fp.document.validation.impl.TransferOfFundsDocumentRuleConstants;
import org.kuali.kfs.gl.batch.PosterEntriesStep;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
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
    protected ObjectCodeService objectCodeService;
    protected OptionsService optionsService;
    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;
    protected RunDateService runDateService;    
    protected ReportWriterService reportWriterService;
    protected UniversityDateService universityDateService;
    protected String batchFileDirectoryName;
        
    /**
     * Reads an incoming file of general ledger transactions, extracts those transaction that qualify as budget adjustment transactions 
     * and saves those transactions to holding table GL_BUDGET_ADJUST_TRN_T.
     */
    @Override
    public void extractAndSaveBudgetAdjustmentEntries() {    	
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("extractAndSaveBudgetAdjustmentEntries() started");
        }
    	     
    	String inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.BUDGET_ADJUSTMENT_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String errorFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.CASH_TRANSFER_TRANSACTIONS_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        
        FileReader INPUT_GLE_FILE = null;
        PrintStream OUTPUT_ERR_FILE_ps;
        String GLEN_RECORD;
        BufferedReader INPUT_GLE_FILE_br;
        
        try {
        	INPUT_GLE_FILE = new FileReader(inputFile);
        	OUTPUT_ERR_FILE_ps = new PrintStream(errorFile);
        }
        catch (FileNotFoundException e) {
        	LOG.error("extractAndSaveBudgetAdjustmentEntries exception: ", e);
        	throw new RuntimeException(e);
        }
         
        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        int line = 0;
        int reportBudgetAdjustDocLoaded = 0;
        int reportBudgetAdjustDocErrors = 0;
        int maxSequenceId = 0;
        String saveDocNumber = new String("");
        
        //get current year system options
        SystemOptions options = getSystemOptions(universityDateService.getCurrentFiscalYear());
        		
        try {
        	while ((GLEN_RECORD = INPUT_GLE_FILE_br.readLine()) != null) {
        		if (StringUtils.isNotEmpty(GLEN_RECORD) && StringUtils.isNotBlank(GLEN_RECORD.trim())) {        			
        			line++;
                	// Just in case, to prevent field mapping problems in org.kuali.kfs.gl.businessobject.OriginEntryFull.setFromTextFileForBatch(line, lineNumber);
                	GLEN_RECORD = StringUtils.rightPad(GLEN_RECORD, 183, ' ');
                	OriginEntryFull originEntry = new OriginEntryFull();
                	List<Message> parsingError = new ArrayList();
                	parsingError = originEntry.setFromTextFileForBatch(GLEN_RECORD, line);
                	 
                	if (parsingError.size() > 0) {                		
		        		//parsing error, write error and continue	   
		        		reportWriterService.writeError(originEntry, parsingError);
		        		createOutputEntry(GLEN_RECORD, OUTPUT_ERR_FILE_ps);
		        		reportBudgetAdjustDocErrors++;
		        		continue;
                    }
                     
                    if (isBudgetAdjustmentTransaction(originEntry, options)) {
                    	                    	
                        //check to make sure that Object Type Code is present (BO input may not have this)
                        if (StringUtils.isBlank(originEntry.getFinancialObjectTypeCode())) {
                        	String financialObjectTypeCode = getFinancialObjectTypeCode(originEntry.getUniversityFiscalYear(), originEntry.getChartOfAccountsCode(), originEntry.getFinancialObjectCode());
                        	if (ObjectUtils.isNull(financialObjectTypeCode)) {
	                    		//no valid Object Code info, write error and continue	                        	
	                            Message errorMsg = new Message("No Object Code information found for this record.", Message.TYPE_FATAL);
	                            reportWriterService.writeError(originEntry, errorMsg);
	                            createOutputEntry(GLEN_RECORD, OUTPUT_ERR_FILE_ps);
	                            reportBudgetAdjustDocErrors++;
	                            continue;
                        	}
                        	else {
	                        	originEntry.setFinancialObjectTypeCode(financialObjectTypeCode);
                        	}
                        }
                         
                        // Make sure the row will be unique when adding to the budget adjustment table by adjusting the transaction sequence id
                        if (originEntry.getDocumentNumber().equals(saveDocNumber)) {
                        	maxSequenceId++;
                        }
                        else {
                        	maxSequenceId = 1;
                        	saveDocNumber = originEntry.getDocumentNumber();
                        }                         
                        originEntry.setTransactionLedgerEntrySequenceNumber(new Integer(maxSequenceId));
                         
                        BudgetAdjustmentTransaction ba = new BudgetAdjustmentTransaction(originEntry);                         
                    	try {
                            budgetAdjustmentTransactionDao.save(ba);
                            reportBudgetAdjustDocLoaded++;
                        }
                        catch (RuntimeException re) {
                        	//error adding budget adjustment record, write error and continue
                        	LOG.error("extractAndSaveBudgetAdjustmentEntries exception: ", re);
                            Message errorMsg = new Message("Error adding budget adjustment record for this record.", Message.TYPE_FATAL);
                            reportWriterService.writeError(originEntry, errorMsg);
                            createOutputEntry(GLEN_RECORD, OUTPUT_ERR_FILE_ps);
                            reportBudgetAdjustDocErrors++;
                            continue;
                        }
                    }
                     
                }
            }
            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
            OUTPUT_ERR_FILE_ps.close();
            reportWriterService.writeStatisticLine("SEQUENTIAL RECORDS READ                        %,9d", line);
            reportWriterService.writeStatisticLine("GLBA RECORDS INSERTED (GL_BUDGET_ADJUST_TRN_T) %,9d", reportBudgetAdjustDocLoaded);
            reportWriterService.writeStatisticLine("ERROR RECORDS WRITTEN                          %,9d", reportBudgetAdjustDocErrors);
        }
        catch (IOException e) {
        	LOG.error("extractAndSaveBudgetAdjustmentEntries exception: ", e);
        	throw new RuntimeException(e);
        } 
        
        if (LOG.isDebugEnabled()) {
     		LOG.debug("extractAndSaveBudgetAdjustmentEntries() completed");
        }
    }

    /**
     * Reads budget adjustment transactions from holding table GL_BUDGET_ADJUST_TRN_T and generates/builds 
     * a file of budget adjustment cash transfer entries for posting to the General Ledger.
     */
    @Override
    public void generateBudgetAdjustmentCashTransferTransactions() {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("generateBudgetAdjustmentCashTransferTransactions() started");
        }
    	        
        Date executionDate = dateTimeService.getCurrentSqlDate();
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());
        int reportBudgetAdjustDocLoaded = 0;
        int reportOriginEntryGenerated = 0;
    	   	
    	try {
    		 PrintStream OUTPUT_GLE_FILE_ps = new PrintStream(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.CASH_TRANSFER_TRANSACTIONS_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    		     		              
             //retrieve unique document numbers list so we can group and process records by document number              
             List<String> docNumberList = retrieveUniqueDocumentNumbersList();            
             
             // don't try to create an entries file or a done file if no budget adjustment transactions were found.
             if (!docNumberList.isEmpty()) {
            	 reportBudgetAdjustDocLoaded = docNumberList.size();
            
            	 //now that we have a list of the document numbers, generate cash transfer transactions for each document number in file
            	 reportOriginEntryGenerated = generateCashTransferGeneralLedgerEntries(docNumberList, runDate, OUTPUT_GLE_FILE_ps);
            	 
            	 // create a done file
                 String budgetAdjustmentCashTransferDoneFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.CASH_TRANSFER_TRANSACTIONS_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
                 File doneFile = new File (budgetAdjustmentCashTransferDoneFileName);
                 if (!doneFile.exists()) {
                     try {
                         doneFile.createNewFile();
                     } 
                     catch (IOException e) {                    	 
                    	 LOG.error("Error creating cash transfer done file: ", e);
                     	 throw new RuntimeException(e);                        
                     }            	 
                 }                       
             }
           
             //delete all budget adjustment transactions  
             budgetAdjustmentTransactionDao.deleteAllBudgetAdjustmentTransactions();
             OUTPUT_GLE_FILE_ps.close();
             reportWriterService.writeStatisticLine("GLBA TRANSACTIONS LOADED        (GL_BUDGET_ADJUST_TRN_T) %,9d", reportBudgetAdjustDocLoaded);
             reportWriterService.writeStatisticLine("CASH TRANSFER TRANSACTIONS GENERATED                     %,9d", reportOriginEntryGenerated);             

        }
        catch (FileNotFoundException e) {
        	LOG.error("generateBudgetAdjustmentCashTransferTransactions exception: ", e);
        	throw new RuntimeException(e);
        }
    	catch (IOException e) {
        	LOG.error("generateBudgetAdjustmentCashTransferTransactions exception: ", e);
        	throw new RuntimeException(e);
        } 
    	 
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("generateBudgetAdjustmentCashTransferTransactions() completed");
        }
    }
    
    /**
     * Returns a list of unique document numbers which are needed so that we can
     * group budget adjustment transactions by budget system document number.
     *       
     * @return docNumberList list of unique budget adjustment document numbers      
     */
    protected List<String> retrieveUniqueDocumentNumbersList() {     	
    	 Iterator budgetAdjustmentTransactions;
    	 List<String> docNumberList = new ArrayList<String>();
    	 
    	 try {
             budgetAdjustmentTransactions = budgetAdjustmentTransactionDao.getAllBudgetAdjustmentTransactions();
         }
         catch (RuntimeException re) {
             LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: ", re);
             throw new RuntimeException(re);
         }
         
         while (budgetAdjustmentTransactions.hasNext()) {
             BudgetAdjustmentTransaction ba = new BudgetAdjustmentTransaction();
             try {
                 ba = (BudgetAdjustmentTransaction) budgetAdjustmentTransactions.next();
                                  
                 if (!docNumberList.contains(ba.getDocumentNumber())) {
                	 docNumberList.add(ba.getDocumentNumber());                     
                 }                      
             }
             catch (RuntimeException re) {
                 LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: ", re);
                 throw new RuntimeException(re);
             }             
         }         
         return docNumberList;
    }
    
    /**
     * Generates cash transfer entries to transfer funds needed to make the budget adjustments. 
     *
     * @param docNumberList list of unique budget adjustment document numbers
     * @param runDate batch job run date 
     * @param group print stream for output file 
     * @return count of cash transfer entries generated    
     */
    protected int generateCashTransferGeneralLedgerEntries(List<String> docNumberList, Date runDate, PrintStream group) {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("generateCashTransferGeneralLedgerEntries() started");
        }
        
    	// check on-off tof flag
    	boolean generateTransfer = parameterService.getParameterValueAsBoolean(BudgetAdjustmentDocument.class, BudgetAdjustmentDocumentRuleConstants.GENERATE_TOF_GLPE_ENTRIES_PARM_NM);
    	int reportOriginEntryGenerated = 0;
        String documentNumber = null;
        Iterator budgetAdjustmentTransactions;        
        
        if (generateTransfer) {        	
        	if (LOG.isDebugEnabled()) {
        		LOG.debug("generateCashTransferGeneralLedgerEntries() generate transfer of funds entries parameter is set to true, generating cash transfer entries.");
        	}
        	String transferObjectCode = parameterService.getParameterValueAsString(BudgetAdjustmentDocument.class, BudgetAdjustmentDocumentRuleConstants.TRANSFER_OBJECT_CODE_PARM_NM);     
        	Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        	
        	for (Iterator iter = docNumberList.iterator(); iter.hasNext();) {
        		try {
                	documentNumber = (String)iter.next();
                                        
                    // assurance report sub-header
                    reportWriterService.writeSubTitle("Document Number " + documentNumber + " Budget Adjustment Transactions and Cash Transfer Entries");                            
                    reportWriterService.writeTableHeader(new BudgetAdjustmentTransaction());                    
                    
                    reportOriginEntryGenerated += buildTransactionsForTransferOfFundsGeneration(documentNumber, runDate, group, transferObjectCode, currentFiscalYear);           
                }
                catch (RuntimeException re) {
                    LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: ", re);
                    throw new RuntimeException(re);
                }               
            } //for docNumberList
        	
        } // if generateTransfer
        
        if (LOG.isDebugEnabled()) {
        	LOG.debug("generateCashTransferGeneralLedgerEntries() completed");
        }
        return reportOriginEntryGenerated;
    }
        
    /**
     * Creates cash transfer transactions from budget adjustment transactions.
     * 
     * @param documentNumber 
     * @param runDate batch job run date 
     * @param group print stream for output file 
     * @param transferObjectCode cash transfer entries object code value to set
     * @param currentFiscalYear current fiscal year for determining the fiscal period code
     * @return transactions created
     */
    public int buildTransactionsForTransferOfFundsGeneration(String documentNumber, Date runDate, PrintStream group, String transferObjectCode, Integer currentFiscalYear) {
            	
        BudgetAdjustmentTransaction ba = new BudgetAdjustmentTransaction();
        OriginEntryFull e = new OriginEntryFull(); 
        Iterator budgetAdjustmentTransactions = budgetAdjustmentTransactionDao.getByDocNumber(documentNumber);
        int reportOriginEntryGenerated = 0;
        
        while (budgetAdjustmentTransactions.hasNext()) {            
            try {            	
                ba = (BudgetAdjustmentTransaction) budgetAdjustmentTransactions.next();
                //write transaction to assurance report                    
                reportWriterService.writeTableRow(ba);                     
                
                //create cash transfer entries
                e.setTransactionLedgerEntrySequenceNumber(0);
                e.setChartOfAccountsCode(ba.getChartOfAccountsCode());
                e.setAccountNumber(ba.getAccountNumber());
                e.setSubAccountNumber(ba.getSubAccountNumber());
                e.setFinancialObjectCode(transferObjectCode);
                e.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                if (ba.getTransactionLedgerEntryAmount().isPositive()) {
                    e.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                    e.setTransactionLedgerEntryAmount(ba.getTransactionLedgerEntryAmount());
                }
                else {
                    e.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                    e.setTransactionLedgerEntryAmount(ba.getTransactionLedgerEntryAmount().negated());
                }                            
                /* set document type to tof */
                e.setFinancialDocumentTypeCode(TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_DOC_TYPE_CODE);
                e.setFinancialSystemOriginationCode(ba.getFinancialSystemOriginationCode());
                e.setDocumentNumber(ba.getDocumentNumber());
                e.setUniversityFiscalYear(ba.getUniversityFiscalYear());  
                e.setUniversityFiscalPeriodCode(ba.getUniversityFiscalPeriodCode());
               
                //get transaction fiscal year system options
                SystemOptions options = getSystemOptions(e.getUniversityFiscalYear());
          	                                     
                e.setFinancialBalanceTypeCode(options.getActualFinancialBalanceTypeCd());
                e.setFinancialObjectTypeCode(options.getFinancialObjectTypeTransferIncomeCd());
                e.setTransactionLedgerEntryDescription(ba.getTransactionLedgerEntryDescription());
                e.setTransactionDate(new java.sql.Date(runDate.getTime())); 
                e.setOrganizationDocumentNumber(ba.getOrganizationDocumentNumber());
                e.setProjectCode(ba.getProjectCode()); 
               
                // set fiscal period, if next fiscal year set to 01, else leave to current period
                if (currentFiscalYear.equals(e.getUniversityFiscalYear() - 1)) {
                    e.setUniversityFiscalPeriodCode(BudgetAdjustmentDocumentRuleConstants.MONTH_1_PERIOD_CODE);
                }

                try {
                    createOutputEntry(e, group);
                    reportOriginEntryGenerated++;                                                              
                }
                catch (IOException ioe) {
                    LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: ", ioe);
                    throw new RuntimeException(ioe);
                }                      
               
            }
            catch (RuntimeException re) {
                LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: ", re);
                throw new RuntimeException(re);
            }            
        }
        return reportOriginEntryGenerated;
    }
        
    protected void createOutputEntry(OriginEntryInformation oef, PrintStream group) throws IOException {       
        try {
            group.printf("%s\n", oef.getLine());
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }
    
    protected void createOutputEntry(String line, PrintStream ps) throws IOException {
        try {
            ps.printf("%s\n", line);
        } 
        catch (Exception e) {
        	throw new IOException(e);            
        }
    }
    
    /**
     * This will determine if this transaction is a budget adjustment transaction
     * 
     * @param transaction the transaction which is being determined to be a budget adjustment transaction
     * @return true if the transaction is a budget adjustment transaction and therefore should have a cash transfer transaction created for it; false if otherwise
     */
    public boolean isBudgetAdjustmentTransaction(OriginEntryFull transaction, SystemOptions options) {
    	   	
        // Does the balance type code indicate that this transaction is for current budget?
        if (!transaction.getFinancialBalanceTypeCode().equals(options.getBudgetCheckingBalanceTypeCd())) {
        	return false;
        }
        
        //retrieve document type and origination code parameter values
        Collection<String> documentTypeCodes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.CASH_TRANSFER_DOC_TYPE_CODES);
        Collection<String> originationCodes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.CASH_TRANSFER_ORIGINATION_CODES);
               
        // Does the origin code indicate that this transaction is from the Budget Office, or ERE Sweep?
        if (!originationCodes.contains(transaction.getFinancialSystemOriginationCode())) {
        	 return false;
        }                   
        
        // Is the document type correct?
        if (!documentTypeCodes.contains(transaction.getFinancialDocumentTypeCode())) {
        	return false;
        }           

        return true;  // still here?  then I guess we don't have an exclusion
       
    }
        
    protected String getFinancialObjectTypeCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {    
    	String financialObjectTypeCode = null;
    	
	    ObjectCode objectCode = getObjectCodeService().getByPrimaryId(universityFiscalYear, chartOfAccountsCode, financialObjectCode);
	    if (ObjectUtils.isNotNull(objectCode)) {   	    
	    	financialObjectTypeCode = objectCode.getFinancialObjectTypeCode();
	    }
	    return financialObjectTypeCode;
    }
    
    public SystemOptions getSystemOptions(Integer universityFiscalYear) {
    	SystemOptions options = null;
    	options = getOptionsService().getOptions(universityFiscalYear);
        if (ObjectUtils.isNull(options)) {
        	options = getOptionsService().getCurrentYearOptions();
        } 
        return options;
    }
           
    public void setBudgetAdjustmentTransactionDao(BudgetAdjustmentTransactionDao budgetAdjustmentTransactionDao) {
        this.budgetAdjustmentTransactionDao = budgetAdjustmentTransactionDao;
    }
        
    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }
    
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
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
    
    public void setReportWriterService(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }
        
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }    

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
