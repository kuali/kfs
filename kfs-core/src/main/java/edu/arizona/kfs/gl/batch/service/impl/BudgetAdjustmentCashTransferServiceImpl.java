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
import org.kuali.kfs.coa.businessobject.Account;
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
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
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
            throw new RuntimeException(e);
        }
         
        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        int line = 0;
        try {
        	while ((GLEN_RECORD = INPUT_GLE_FILE_br.readLine()) != null) {
        		if (!org.apache.commons.lang.StringUtils.isEmpty(GLEN_RECORD) && !org.apache.commons.lang.StringUtils.isBlank(GLEN_RECORD.trim())) {
                	 line++;
                	 GLEN_RECORD = org.apache.commons.lang.StringUtils.rightPad(GLEN_RECORD, 183, ' ');
                	 OriginEntryFull originEntry = new OriginEntryFull();
                	 List<Message> parsingError = new ArrayList();
                	 parsingError = originEntry.setFromTextFileForBatch(GLEN_RECORD, line);
                	 
                	 if (parsingError.size() > 0) {
                		 //parsing error, write error and continue	   
                		 reportWriterService.writeError(originEntry, parsingError);
                		 createOutputEntry(GLEN_RECORD, OUTPUT_ERR_FILE_ps);
                    	 continue;
                     }
                     
                     if (isBudgetAdjustmentTransaction(originEntry)) {
                    	 
                    	//check to make sure that Account has Income Stream information
                    	 Account baAccount = originEntry.getAccount();    	 
                         if (ObjectUtils.isNotNull(baAccount)) {                        
                        	 if (ObjectUtils.isNull(baAccount.getIncomeStreamFinancialCoaCode()) || ObjectUtils.isNull(baAccount.getIncomeStreamAccountNumber())) {             	
	                        	 //no income stream info, write error and continue	                        	
	                             Message errorMsg = new Message("No Account Income Stream information found for this record.", Message.TYPE_FATAL);
	                             reportWriterService.writeError(originEntry, errorMsg);
	                        	 createOutputEntry(GLEN_RECORD, OUTPUT_ERR_FILE_ps);
	                        	 continue;
	                         }
                         }
                         
                         BudgetAdjustmentTransaction ba = new BudgetAdjustmentTransaction(originEntry);                    	
                    	 try {
                             budgetAdjustmentTransactionDao.save(ba);
                         }
                         catch (RuntimeException re) {
                             LOG.error("extractAndSaveBudgetAdjustmentEntries Stopped: " + re.getMessage());
                             throw new RuntimeException("extractAndSaveBudgetAdjustmentEntries Stopped: " + re.getMessage(), re);
                         }
                     }
                     
                 }
             }
             INPUT_GLE_FILE_br.close();
             INPUT_GLE_FILE.close();
             OUTPUT_ERR_FILE_ps.close();
         }
         catch (IOException e) {
             throw new RuntimeException(e);
         }         
    }

    /**
     * Reads budget adjustment transactions from holding table GL_BUDGET_ADJUST_TRN_T and generates/builds 
     * a file of budget adjustment cash transfer entries for posting to the General Ledger.
     */
    @Override
    public void generateBudgetAdjustmentCashTransferTransactions() {
    	LOG.debug("generateBudgetAdjustmentCashTransferTransactions() started");
        
        Date executionDate = dateTimeService.getCurrentSqlDate();
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());
        int reportBudgetAdjustDocLoaded = 0;
        int reportOriginEntryGenerated = 0;
    	   	
    	 try {
    		 PrintStream OUTPUT_GLE_FILE_ps = new PrintStream(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.CASH_TRANSFER_TRANSACTIONS_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    		     		              
             //retrieve unique document numbers list             
             List<String> docNumberList = retrieveUniqueDocumentNumbersList();            
             
             // don't try to create an entries file or a done file if no budget adjustment transactions were found.
             if (ObjectUtils.isNotNull(docNumberList) && !docNumberList.isEmpty()) {
            	 reportBudgetAdjustDocLoaded = docNumberList.size();
            
            	 //now that we have a list of the document numbers, generate cash transfer transactions for each document number in file
            	 reportOriginEntryGenerated = generateCashTransferGeneralLedgerEntries(docNumberList, runDate, OUTPUT_GLE_FILE_ps);
            	 
            	 // create a done file
                 String budgetAdjustmentCashTransferDoneFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.CASH_TRANSFER_TRANSACTIONS_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
                 File doneFile = new File (budgetAdjustmentCashTransferDoneFileName);
                 if (!doneFile.exists()) {
                     try {
                         doneFile.createNewFile();
                     } catch (IOException e) {
                         throw new RuntimeException("Error creating cash transfer done file", e);
                     }
            	 
                 }                
                        
             }
           
             //delete all budget adjustment transactions  
             budgetAdjustmentTransactionDao.deleteAllBudgetAdjustmentTransactions();
             OUTPUT_GLE_FILE_ps.close();             
             reportWriterService.writeStatisticLine("GLBA RBC TRANS LOADED           (GL_BUDGET_ADJUST_TRN_T) %,9d", reportBudgetAdjustDocLoaded);
             reportWriterService.writeStatisticLine("TRANSACTIONS GENERATED                                   %,9d", reportOriginEntryGenerated);             

         }
         catch (FileNotFoundException e) {
             throw new RuntimeException("generateBudgetAdjustmentCashTransferTransactions Stopped: " + e.getMessage(), e);
         }
    }
    
    /**
     * Returns a list of unique document numbers which are needed so that we can
     * group budget adjustment transactions by budget system document number in
     * order to determine if the transactions, as a group, cross income streams.
      *
     * @return docNumberList list of unique budget adjustment document numbers      
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
             throw new RuntimeException("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage(), re);
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
                 reportWriterService.writeError(ba, errorList);
             }
         }
         reportWriterService.writeStatisticLine("GLBA RECORDS READ               (GL_BUDGET_ADJUST_TRN_T) %,9d", reportBudgetAdjustTranRetrieved);
         return docNumberList;
    }
    
    /**
     * Generates any necessary cash transfer entries to transfer funds needed to make the budget adjustments. Based on income chart and
     * accounts. If there is a difference in funds between an income chart and account, a cash transfer entry needs to be created.
     *
     * @param docNumberList list of unique budget adjustment document numbers
     * @param runDate batch job run date 
     * @param group print stream for output file 
     * @return count of cash transfer entries generated    
     */
    protected int generateCashTransferGeneralLedgerEntries(List<String> docNumberList, Date runDate, PrintStream group) {
        
        // check on-off tof flag
    	boolean generateTransfer = parameterService.getParameterValueAsBoolean(BudgetAdjustmentDocument.class, BudgetAdjustmentDocumentRuleConstants.GENERATE_TOF_GLPE_ENTRIES_PARM_NM);
        int reportOriginEntryGenerated = 0;
        String documentNumber = null;
        Iterator budgetAdjustmentTransactions;        
        
        if (generateTransfer) {
            
        	for (Iterator iter = docNumberList.iterator(); iter.hasNext();) {
        		
                try {
                	documentNumber = (String)iter.next();
                    budgetAdjustmentTransactions = budgetAdjustmentTransactionDao.getByDocNumber(documentNumber);
                    
                    // assurance report sub-header
                    reportWriterService.writeSubTitle("Document Number " + documentNumber + " Budget Adjustment Transactions and Cash Transfer Entries");                            
                    reportWriterService.writeTableHeader(new BudgetAdjustmentTransaction());                    
                    
                    // map of income chart/accounts with balance as value
                    Map<String, KualiDecimal> incomeStreamMap = buildIncomeStreamBalanceMapForTransferOfFundsGeneration(budgetAdjustmentTransactions);
                    for (Iterator iter2 = incomeStreamMap.keySet().iterator(); iter2.hasNext();) {                      
                    	String chartAccount = (String) iter2.next();
                        KualiDecimal streamAmount = (KualiDecimal) incomeStreamMap.get(chartAccount);
                        if (streamAmount.isNonZero()) {                          	
                            // create cash transfer transactions 
                            String[] incomeString = StringUtils.split(chartAccount, BudgetAdjustmentDocumentRuleConstants.INCOME_STREAM_CHART_ACCOUNT_DELIMITER);
                            reportOriginEntryGenerated += buildTransactionsForTransferOfFundsGeneration(documentNumber, incomeString, runDate, group);                                                                   
                        } //streamAmount.isNonZero()
                    } //for incomeStreamMap
                }
                catch (RuntimeException re) {
                    LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage());
                    throw new RuntimeException("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage(), re);
                }  
                
            } //for docNumberList
        	
        } // if generateTransfer
        
        return reportOriginEntryGenerated;
    }
    
    /**
     * Builds a map used for balancing current adjustment amounts. The map contains income chart and accounts contained on the
     * document as the keys, and transfer amounts as the values.  The transfer amount is calculated from (curr_frm_inc - curr_frm_exp) - (curr_to_inc - curr_to_exp)
     * 
     * @param budgetAdjustmentTransactions Iterator of budget adjustment transactions
     * @return Map used to balance current amounts
     */    
    public Map buildIncomeStreamBalanceMapForTransferOfFundsGeneration(Iterator budgetAdjustmentTransactions) {
        Map<String, KualiDecimal> incomeStreamBalance = new HashMap<String, KualiDecimal>();
        List<Message> warnings = new ArrayList<Message>();
        
        ParameterEvaluatorService parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        
        while (budgetAdjustmentTransactions.hasNext()) {
            BudgetAdjustmentTransaction ba = new BudgetAdjustmentTransaction();
            try {
                ba = (BudgetAdjustmentTransaction) budgetAdjustmentTransactions.next();   
                Account baAccount = ba.getAccount();                
                              
                if (parameterEvaluatorService.getParameterEvaluator(BudgetAdjustmentDocument.class, KFSConstants.BudgetAdjustmentDocumentConstants.CROSS_INCOME_STREAM_GLPE_TRANSFER_GENERATING_FUND_GROUPS, baAccount.getSubFundGroup().getFundGroupCode()).evaluationSucceeds() &&
                    parameterEvaluatorService.getParameterEvaluator(BudgetAdjustmentDocument.class, KFSConstants.BudgetAdjustmentDocumentConstants.CROSS_INCOME_STREAM_GLPE_TRANSFER_GENERATING_SUB_FUND_GROUPS, baAccount.getSubFundGroupCode()).evaluationSucceeds()) {
                	
                	  //check to make sure that Account has Income Stream information
                    if (ObjectUtils.isNotNull(baAccount.getIncomeStreamFinancialCoaCode()) && ObjectUtils.isNotNull(baAccount.getIncomeStreamAccountNumber())) {
                                	
                        String incomeStreamKey = baAccount.getIncomeStreamFinancialCoaCode() + BudgetAdjustmentDocumentRuleConstants.INCOME_STREAM_CHART_ACCOUNT_DELIMITER + 
                            baAccount.getIncomeStreamAccountNumber() + BudgetAdjustmentDocumentRuleConstants.INCOME_STREAM_CHART_ACCOUNT_DELIMITER + 
                            ba.getUniversityFiscalYear() + BudgetAdjustmentDocumentRuleConstants.INCOME_STREAM_CHART_ACCOUNT_DELIMITER + 
                            ba.getUniversityFiscalPeriodCode();
                        // place record in balance map
                        incomeStreamBalance.put(incomeStreamKey, getIncomeStreamAmount(ba, incomeStreamBalance.get(incomeStreamKey)));   
                    
                    }
                    else {
                    	List errorList = new ArrayList();
                        errorList.add(new Message("No Account Income Stream information found for this record.", Message.TYPE_FATAL));
                        reportWriterService.writeError(ba, errorList);
                    }
                }
           }
           catch (RuntimeException re) {
               LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage());
               throw new RuntimeException("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage(), re);
           }
           catch (Exception e) {
               List errorList = new ArrayList();
               errorList.add(new Message(e.toString() + " occurred for this record.", Message.TYPE_FATAL));
               reportWriterService.writeError(ba, errorList);
           }
        }
        return incomeStreamBalance;
    }   
   
    /**
     * Calculates the appropriate income stream amount for an account using the value provided and the provided budget adjustment transaction.
     * 
     * @param budgetAdjustmentTransaction
     * @param incomeStreamAmount
     * @return incomeStreamAmount
     */
    protected KualiDecimal getIncomeStreamAmount(BudgetAdjustmentTransaction budgetAdjustmentTransaction, KualiDecimal incomeStreamAmount) {
        if(incomeStreamAmount == null) {
            incomeStreamAmount = new KualiDecimal(0);
        }      
        incomeStreamAmount = incomeStreamAmount.add(budgetAdjustmentTransaction.getTransactionLedgerEntryAmount());
        return incomeStreamAmount;
    }    
        
    /**
     * Creates cash transfer transactions from budget adjustment transactions.
     * 
     * @param documentNumber 
     * @param incomeString of key values
     * @param runDate batch job run date 
     * @param group print stream for output file 
     * @return transactions created
     */
    public int buildTransactionsForTransferOfFundsGeneration(String documentNumber, String[] incomeString, Date runDate, PrintStream group) {
        
    	String transferObjectCode = parameterService.getParameterValueAsString(BudgetAdjustmentDocument.class, BudgetAdjustmentDocumentRuleConstants.TRANSFER_OBJECT_CODE_PARM_NM);     
        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        int reportOriginEntryGenerated = 0;
        List<Message> warnings = new ArrayList<Message>();
        
        BudgetAdjustmentTransaction ba = new BudgetAdjustmentTransaction();
        OriginEntryFull e = new OriginEntryFull(); 
        Iterator budgetAdjustmentTransactions = budgetAdjustmentTransactionDao.getByDocNumber(documentNumber);
        
        while (budgetAdjustmentTransactions.hasNext()) {            
            try {
                ba = (BudgetAdjustmentTransaction) budgetAdjustmentTransactions.next();                 
                Account baAccount = ba.getAccount();
                if (baAccount.getIncomeStreamFinancialCoaCode().equals(incomeString[0]) && 
                        baAccount.getIncomeStreamAccountNumber().equals(incomeString[1]) &&
                        ba.getUniversityFiscalYear().equals(new Integer(incomeString[2])) &&
                        ba.getUniversityFiscalPeriodCode().equals(incomeString[3])) {
                    
                    //matching transaction found 
                    //write transaction to assurance report                    
                    reportWriterService.writeTableRow(ba);                     
                    
                    //create cash transfer entries
                    e.setTransactionLedgerEntrySequenceNumber(0);
                    e.setChartOfAccountsCode(incomeString[0]);
                    e.setAccountNumber(incomeString[1]);
					e.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
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
                   
                   //get current year system options
              	   SystemOptions options = getOptionsService().getOptions(e.getUniversityFiscalYear());
                   if (ObjectUtils.isNull(options)) {
                       options = getOptionsService().getCurrentYearOptions();
                   }
                                      
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
                       LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: " + ioe.getMessage());
                       throw new RuntimeException("generateBudgetAdjustmentCashTransferTransactions Stopped: " + ioe.getMessage(), ioe);
                   }                   
                   
                } //matching transaction found 
            }
            catch (RuntimeException re) {
                LOG.error("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage());
                throw new RuntimeException("generateBudgetAdjustmentCashTransferTransactions Stopped: " + re.getMessage(), re);
            }
            catch (Exception ex) {
                List errorList = new ArrayList();
                errorList.add(new Message(ex.toString() + " occurred for this record.", Message.TYPE_FATAL));
                reportWriterService.writeError(ba, errorList);
            }
        }
        return reportOriginEntryGenerated;
    }
        
    protected void createOutputEntry(OriginEntryInformation oef, PrintStream group) throws IOException {       
        try {
            group.printf("%s\n", oef.getLine());
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }
    
    protected void createOutputEntry(String line, PrintStream ps) throws IOException {
        try {
            ps.printf("%s\n", line);
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }
    
    /**
     * This will determine if this transaction is a budget adjustment transaction
     * 
     * @param transaction the transaction which is being determined to be a budget adjustment transaction
     * @return true if the transaction is a budget adjustment transaction and therefore should have a cash transfer transaction created for it; false if otherwise
     */
    public boolean isBudgetAdjustmentTransaction(OriginEntryFull transaction) {
    	    	
    	//retrieve document type and origination code parameter values
    	Collection<String> documentTypeCodes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.CASH_TRANSFER_DOC_TYPE_CODES);
    	Collection<String> originationCodes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.CASH_TRANSFER_ORIGINATION_CODES);
      
        //get current year system options
    	SystemOptions options = getOptionsService().getOptions(universityDateService.getCurrentFiscalYear());
        if (ObjectUtils.isNull(options)) {
        	options = getOptionsService().getCurrentYearOptions();
        }
         
        // Does the balance type code indicate that this transaction is for current budget?
        if (!transaction.getFinancialBalanceTypeCode().equals(options.getActualFinancialBalanceTypeCd())) {
        	return false;
        }                   
               
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
