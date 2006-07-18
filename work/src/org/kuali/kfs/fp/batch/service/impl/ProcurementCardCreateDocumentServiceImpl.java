/*
 * Kuali License Info
 */
/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.rule.event.SaveOnlyDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.format.TimestampFormatter;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.financial.bo.ProcurementCardHolder;
import org.kuali.module.financial.bo.ProcurementCardSourceAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTransaction;
import org.kuali.module.financial.bo.ProcurementCardTransactionDetail;
import org.kuali.module.financial.bo.ProcurementCardVendor;
import org.kuali.module.financial.document.ProcurementCardDocument;
import org.kuali.module.financial.rules.AccountingLineRuleUtil;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants;
import org.kuali.module.financial.service.ProcurementCardCreateDocumentService;

import edu.iu.uis.eden.exception.WorkflowException;


/**
 * Implementation of ProcurementCardCreateDocumentService
 * 
 * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardCreateDocumentServiceImpl implements ProcurementCardCreateDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private DataDictionaryService dataDictionaryService;
    private DateTimeService dateTimeService;
    private WorkflowDocumentService workflowDocumentService;


    /**
     * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService#createProcurementCardDocuments()
     */
    public boolean createProcurementCardDocuments() {
        List documents = new ArrayList();
        List cardTransactions = retrieveTransactions();

        // iterate through card transaction list and create documents
        for (Iterator iter = cardTransactions.iterator(); iter.hasNext();) {
            documents.add(createProcurementCardDocument((List) iter.next()));
        }

        // now store all the documents
        for (Iterator iter = documents.iterator(); iter.hasNext();) {
            ProcurementCardDocument pcardDocument = (ProcurementCardDocument) iter.next();
            try {
                documentService.validateAndPersistDocument(pcardDocument, new SaveOnlyDocumentEvent(pcardDocument));
            }
            catch (Exception e) {
                LOG.error("Error persisting document # " + pcardDocument.getDocumentHeader().getFinancialDocumentNumber() + " " + e.getMessage());
                throw new RuntimeException("Error persisting document # " + pcardDocument.getDocumentHeader().getFinancialDocumentNumber() + " " + e.getMessage());
            }
        }

        return true;
    }

    /**
     * Routes all pcard documents in 'I' status.
     * 
     * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService#routeProcurementCardDocuments(java.util.List)
     */
    public boolean routeProcurementCardDocuments() {
        List documentList = new ArrayList();
        try {
            documentList = (List) documentService.findByDocumentHeaderStatusCode(ProcurementCardDocument.class, Constants.DocumentStatusCodes.INITIATED);
        }
        catch (WorkflowException e1) {
            LOG.error("Error retrieving pcdo documents for routing: " + e1.getMessage());
            throw new RuntimeException(e1.getMessage());
        }
        for (Iterator iter = documentList.iterator(); iter.hasNext();) {
            ProcurementCardDocument pcardDocument = (ProcurementCardDocument) iter.next();
            try {
                LOG.info("Routing PCDO document # " + pcardDocument.getDocumentHeader().getFinancialDocumentNumber() + ".");
                documentService.prepareWorkflowDocument(pcardDocument);
                // calling workflow service to bypass business rule checks
                workflowDocumentService.route(pcardDocument.getDocumentHeader().getWorkflowDocument(), "", null);
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + pcardDocument.getDocumentHeader().getFinancialDocumentNumber() + " " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }

        return true;
    }

    /**
     * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService#autoApproveProcurementCardDocuments()
     */
    public boolean autoApproveProcurementCardDocuments() {
        // check if auto approve is turned on
        boolean autoApproveOn = kualiConfigurationService.getApplicationParameterIndicator(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, AUTO_APPROVE_DOCUMENTS_IND);

        if (!autoApproveOn) {
            return true;
        }

        List documentList = new ArrayList();

        try {
            documentList = (List) documentService.findByDocumentHeaderStatusCode(ProcurementCardDocument.class, Constants.DocumentStatusCodes.ENROUTE);
        }
        catch (WorkflowException e1) {
            throw new RuntimeException(e1.getMessage());
        }

        // get number of days and type for autoapprove
        int autoApproveNumberDays = Integer.parseInt(kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, AUTO_APPROVE_NUMBER_OF_DAYS));

        Timestamp currentDate = dateTimeService.getCurrentTimestamp();
        for (Iterator iter = documentList.iterator(); iter.hasNext();) {
            ProcurementCardDocument pcardDocument = (ProcurementCardDocument) iter.next();
            Timestamp docCreateDate = pcardDocument.getDocumentHeader().getWorkflowDocument().getCreateDate();

            // if number of days in route is passed the allowed number, call doc service for super user approve
            if (DateUtils.getDifferenceInDays(docCreateDate, currentDate) > autoApproveNumberDays) {
                // update document description to reflect the auto approval
                pcardDocument.getDocumentHeader().setFinancialDocumentDescription("Auto Approved On " + (new TimestampFormatter()).format(currentDate) + ".");

                try {
                    LOG.info("Auto approving document # " + pcardDocument.getDocumentHeader().getFinancialDocumentNumber());
                    documentService.superUserApproveDocument(pcardDocument, "");
                }
                catch (WorkflowException e) {
                    LOG.error("Error auto approving document # " + pcardDocument.getDocumentHeader().getFinancialDocumentNumber() + " " + e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        return true;
    }


    /**
     * retrieves list of transactions from temporary table, and groups them into document lists, based on single transaction
     * indicator or grouping of card
     * 
     * @return List of List containing transactions for document
     */
    private List retrieveTransactions() {
        List groupedTransactions = new ArrayList();

        // retrieve records from transaction table order by card number
        List transactions = (List) businessObjectService.findMatchingOrderBy(ProcurementCardTransaction.class, new HashMap(), PropertyConstants.TRANSACTION_CREDIT_CARD_NUMBER, true);

        // check apc for single transaction documents or multple by card
        boolean singleTransaction = kualiConfigurationService.getApplicationParameterIndicator(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, SINGLE_TRANSACTION_IND_PARM_NM);

        List documentTransactions = new ArrayList();
        if (singleTransaction) {
            for (Iterator iter = transactions.iterator(); iter.hasNext();) {
                documentTransactions.add(iter.next());
                groupedTransactions.add(documentTransactions);
                documentTransactions = new ArrayList();
            }
        }
        else {
            Map cardTransactionsMap = new HashMap();
            for (Iterator iter = transactions.iterator(); iter.hasNext();) {
                ProcurementCardTransaction transaction = (ProcurementCardTransaction) iter.next();
                if (!cardTransactionsMap.containsKey(transaction.getTransactionCreditCardNumber())) {
                    cardTransactionsMap.put(transaction.getTransactionCreditCardNumber(), new ArrayList());
                }
                ((List) cardTransactionsMap.get(transaction.getTransactionCreditCardNumber())).add(transaction);
            }

            for (Iterator iter = cardTransactionsMap.values().iterator(); iter.hasNext();) {
                groupedTransactions.add(iter.next());

            }
        }

        return groupedTransactions;
    }


    /**
     * Creates a ProcurementCardDocument from the List of transactions
     * 
     * @param transactions - List of ProcurementCardTransaction objects
     * @return ProcurementCardDocument
     */
    private ProcurementCardDocument createProcurementCardDocument(List transactions) {
        ProcurementCardDocument pcardDocument = null;

        try {
            // get new document from doc service
            pcardDocument = (ProcurementCardDocument) documentService.getNewDocument(ProcurementCardDocument.class);

            // set the card holder record on the document from the first transaction
            createCardHolderRecord(pcardDocument, (ProcurementCardTransaction) transactions.get(0));

            // for each transaction, create transaction detail object and then acct lines for the detail
            int transactionLineNumber = 1;
            KualiDecimal documentTotalAmount = new KualiDecimal(0);
            String errorText = "";
            for (Iterator iter = transactions.iterator(); iter.hasNext();) {
                ProcurementCardTransaction transaction = (ProcurementCardTransaction) iter.next();

                // create transaction detail record with accounting lines
                errorText += createTransactionDetailRecord(pcardDocument, transaction, transactionLineNumber);

                // update document total
                documentTotalAmount = documentTotalAmount.add(transaction.getFinancialDocumentTotalAmount());

                transactionLineNumber++;
            }

            pcardDocument.getDocumentHeader().setFinancialDocumentTotalAmount(documentTotalAmount);
            pcardDocument.getDocumentHeader().setFinancialDocumentDescription("SYSTEM Generated");
            pcardDocument.setExplanation(errorText);
        }
        catch (WorkflowException e) {
            LOG.error("Error creating pcdo documents: " + e.getMessage());
            throw new RuntimeException("Error creating pcdo documents: " + e.getMessage());
        }

        return pcardDocument;
    }

    /**
     * Creates card holder record and sets in document.
     * 
     * @param pcardDocument - document to place record in
     * @param transaction - transaction to set fields from
     */
    private void createCardHolderRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction) {
        ProcurementCardHolder cardHolder = new ProcurementCardHolder();

        cardHolder.setFinancialDocumentNumber(pcardDocument.getFinancialDocumentNumber());
        cardHolder.setAccountNumber(transaction.getAccountNumber());
        cardHolder.setCardCycleAmountLimit(transaction.getCardCycleAmountLimit());
        cardHolder.setCardCycleVolumeLimit(transaction.getCardCycleVolumeLimit());
        cardHolder.setCardHolderAlternateName(transaction.getCardHolderAlternateName());
        cardHolder.setCardHolderCityName(transaction.getCardHolderCityName());
        cardHolder.setCardHolderLine1Address(transaction.getCardHolderLine1Address());
        cardHolder.setCardHolderLine2Address(transaction.getCardHolderLine2Address());
        cardHolder.setCardHolderName(transaction.getCardHolderName());
        cardHolder.setCardHolderStateCode(transaction.getCardHolderStateCode());
        cardHolder.setCardHolderWorkPhoneNumber(transaction.getCardHolderWorkPhoneNumber());
        cardHolder.setCardHolderZipCode(transaction.getCardHolderZipCode());
        cardHolder.setCardLimit(transaction.getCardLimit());
        cardHolder.setCardNoteText(transaction.getCardNoteText());
        cardHolder.setCardStatusCode(transaction.getCardStatusCode());
        cardHolder.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
        cardHolder.setSubAccountNumber(transaction.getSubAccountNumber());
        cardHolder.setTransactionCreditCardNumber(transaction.getTransactionCreditCardNumber());

        pcardDocument.setProcurementCardHolder(cardHolder);
    }

    /**
     * Creates transaction detail record and adds to document.
     * 
     * @param pcardDocument - document to place record in
     * @param transaction - transaction to set fields from
     */
    private String createTransactionDetailRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, Integer transactionLineNumber) {
        ProcurementCardTransactionDetail transactionDetail = new ProcurementCardTransactionDetail();

        // set the document transaction detail fields from the loaded transaction record
        transactionDetail.setFinancialDocumentNumber(pcardDocument.getFinancialDocumentNumber());
        transactionDetail.setFinancialDocumentTransactionLineNumber(transactionLineNumber);
        transactionDetail.setTransactionDate(transaction.getTransactionDate());
        transactionDetail.setTransactionReferenceNumber(transaction.getTransactionReferenceNumber());
        transactionDetail.setTransactionBillingCurrencyCode(transaction.getTransactionBillingCurrencyCode());
        transactionDetail.setTransactionCurrencyExchangeRate(transaction.getTransactionCurrencyExchangeRate());
        transactionDetail.setTransactionDate(transaction.getTransactionDate());
        transactionDetail.setTransactionOriginalCurrencyAmount(transaction.getTransactionOriginalCurrencyAmount());
        transactionDetail.setTransactionOriginalCurrencyCode(transaction.getTransactionOriginalCurrencyCode());
        transactionDetail.setTransactionPointOfSaleCode(transaction.getTransactionPointOfSaleCode());
        transactionDetail.setTransactionPostingDate(transaction.getTransactionPostingDate());
        transactionDetail.setTransactionPurchaseIdentifierDescription(transaction.getTransactionPurchaseIdentifierDescription());
        transactionDetail.setTransactionPurchaseIdentifierIndicator(transaction.getTransactionPurchaseIdentifierIndicator());
        transactionDetail.setTransactionSalesTaxAmount(transaction.getTransactionSalesTaxAmount());
        transactionDetail.setTransactionSettlementAmount(transaction.getTransactionSettlementAmount());
        transactionDetail.setTransactionTaxExemptIndicator(transaction.getTransactionTaxExemptIndicator());
        transactionDetail.setTransactionTravelAuthorizationCode(transaction.getTransactionTravelAuthorizationCode());
        transactionDetail.setTransactionUnitContactName(transaction.getTransactionUnitContactName());
        transactionDetail.setTransactionTotalAmount(transaction.getFinancialDocumentTotalAmount());

        // create transaction vendor record
        createTransactionVendorRecord(pcardDocument, transaction, transactionDetail);

        // add transaction detail to document
        pcardDocument.getTransactionEntries().add(transactionDetail);

        // now create the initial source and target lines for this transaction
        return createAndValidateAccountingLines(pcardDocument, transaction, transactionDetail);
    }


    /**
     * Creates transaction vendor detail record and adds to transaction detail.
     * 
     * @param pcardDocument - document to set fields in
     * @param transaction - transaction to set fields from
     */
    private void createTransactionVendorRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        ProcurementCardVendor transactionVendor = new ProcurementCardVendor();

        transactionVendor.setFinancialDocumentNumber(pcardDocument.getFinancialDocumentNumber());
        transactionVendor.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
        transactionVendor.setTransactionMerchantCategoryCode(transaction.getTransactionMerchantCategoryCode());
        transactionVendor.setVendorCityName(transaction.getVendorCityName());
        transactionVendor.setVendorLine1Address(transaction.getVendorLine1Address());
        transactionVendor.setVendorLine2Address(transaction.getVendorLine2Address());
        transactionVendor.setVendorName(transaction.getVendorName());
        transactionVendor.setVendorOrderNumber(transaction.getVendorOrderNumber());
        transactionVendor.setVendorStateCode(transaction.getVendorStateCode());
        transactionVendor.setVendorZipCode(transaction.getVendorZipCode());
        transactionVendor.setVisaVendorIdentifier(transaction.getVisaVendorIdentifier());

        transactionDetail.setProcurementCardVendor(transactionVendor);
    }

    /**
     * From the transaction accounting attributes, creates source and target accounting lines. Attributes are validated first, and
     * replaced with default and error values if needed. There will be 1 source and 1 target line generated
     * 
     * @param transaction - transaction to process
     * @param docTransactionDetail - Object to put accounting lines into
     * @return String containing any error messages
     */
    private String createAndValidateAccountingLines(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        // build source lines
        ProcurementCardSourceAccountingLine sourceLine = createSourceAccountingLine(transaction, docTransactionDetail);

        // add line to transaction through document since document contains the next sequence number fields
        pcardDocument.addSourceAccountingLine(sourceLine);

        // build target lines
        ProcurementCardTargetAccountingLine targetLine = createTargetAccountingLine(transaction, docTransactionDetail);

        // add line to transaction through document since document contains the next sequence number fields
        pcardDocument.addTargetAccountingLine(targetLine);

        return validateTargetAccountingLine(targetLine);
    }

    /**
     * Creates the to record for the transaction. The COA attributes from the transaction are used to create the accounting line.
     * 
     * @param transaction
     * @param docTransactionDetail
     * @return ProcurementCardSourceAccountingLine
     */
    private ProcurementCardTargetAccountingLine createTargetAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardTargetAccountingLine targetLine = new ProcurementCardTargetAccountingLine();

        targetLine.setFinancialDocumentNumber(docTransactionDetail.getFinancialDocumentNumber());
        targetLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        targetLine.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
        targetLine.setAccountNumber(transaction.getAccountNumber());
        targetLine.setFinancialObjectCode(transaction.getFinancialObjectCode());
        targetLine.setSubAccountNumber(transaction.getSubAccountNumber());
        targetLine.setFinancialSubObjectCode(transaction.getFinancialSubObjectCode());
        targetLine.setProjectCode(transaction.getProjectCode());

        if (TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT.equals(transaction.getTransactionDebitCreditCode())) {
            targetLine.setAmount(transaction.getFinancialDocumentTotalAmount().negated());
        }
        else {
            targetLine.setAmount(transaction.getFinancialDocumentTotalAmount());
        }

        return targetLine;
    }

    /**
     * Creates the from record for the transaction. The clearing chart, account, and object code is used for creating the line.
     * 
     * @param transaction
     * @param docTransactionDetail
     * @return ProcurementCardSourceAccountingLine
     */
    private ProcurementCardSourceAccountingLine createSourceAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardSourceAccountingLine sourceLine = new ProcurementCardSourceAccountingLine();

        sourceLine.setFinancialDocumentNumber(docTransactionDetail.getFinancialDocumentNumber());
        sourceLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        sourceLine.setChartOfAccountsCode(getDefaultChartCode());
        sourceLine.setAccountNumber(getDefaultAccountNumber());
        sourceLine.setFinancialObjectCode(getDefaultObjectCode());
        sourceLine.setSubAccountNumber("");
        sourceLine.setFinancialSubObjectCode("");
        sourceLine.setProjectCode("");

        if (TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT.equals(transaction.getTransactionDebitCreditCode())) {
            sourceLine.setAmount(transaction.getFinancialDocumentTotalAmount().negated());
        }
        else {
            sourceLine.setAmount(transaction.getFinancialDocumentTotalAmount());
        }

        return sourceLine;
    }

    /**
     * Validates the COA attributes for existence and active indicator. Will substitute for defined default parameters or set fields
     * to empty that have errors.
     * 
     * @param sourceLine
     * @return String with error messages
     */
    private String validateTargetAccountingLine(ProcurementCardTargetAccountingLine targetLine) {
        String errorText = "";

        targetLine.refresh();

        if (!AccountingLineRuleUtil.isValidObjectCode(targetLine.getObjectCode(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Object Code " + targetLine.getFinancialObjectCode() + " is invalid. Using default Object Code.");
            errorText += (" Object Code " + targetLine.getFinancialObjectCode() + " is invalid. Using default Object Code.");

            targetLine.setFinancialObjectCode(getDefaultObjectCode());
        }

        if (StringUtils.isNotBlank(targetLine.getSubAccountNumber()) && !AccountingLineRuleUtil.isValidSubAccount(targetLine.getSubAccount(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Sub Account " + targetLine.getSubAccountNumber() + " is invalid. Setting to blank");
            errorText += " Sub Account " + targetLine.getSubAccountNumber() + " is invalid. Setting to blank";

            targetLine.setSubAccountNumber("");
        }

        // refresh again since further checks depend on the above attributes (which could have changed)
        targetLine.refresh();

        if (StringUtils.isNotBlank(targetLine.getFinancialSubObjectCode()) && !AccountingLineRuleUtil.isValidSubObjectCode(targetLine.getSubObjectCode(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Sub Object Code " + targetLine.getFinancialSubObjectCode() + " is invalid. Setting to blank");
            errorText += " Sub Object Code " + targetLine.getFinancialSubObjectCode() + " is invalid. Setting to blank";

            targetLine.setFinancialSubObjectCode("");
        }

        if (StringUtils.isNotBlank(targetLine.getProjectCode()) && !AccountingLineRuleUtil.isValidProjectCode(targetLine.getProject(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Project Code " + targetLine.getProjectCode() + " is invalid. Setting to blank");
            errorText += " Project Code " + targetLine.getProjectCode() + " is invalid. Setting to blank";

            targetLine.setProjectCode("");
        }

        if (!AccountingLineRuleUtil.isValidAccount(targetLine.getAccount(), dataDictionaryService.getDataDictionary()) || targetLine.getAccount().isExpired()) {
            LOG.info("Account " + targetLine.getAccountNumber() + " is invalid. Using error account.");
            errorText += " Account " + targetLine.getAccountNumber() + " is invalid. Using error Chart & Account.";

            targetLine.setChartOfAccountsCode(getErrorChartCode());
            targetLine.setAccountNumber(getErrorAccountNumber());
        }

        targetLine.refresh();

        // clear out GlobalVariable error map, since we have taken care of the errors
        GlobalVariables.setErrorMap(new ErrorMap());

        return errorText;
    }

    /**
     * @return error chart code defined in the apc
     */
    private String getErrorChartCode() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, ERROR_TRANS_CHART_CODE_PARM_NM);
    }

    /**
     * @return error account number defined in the apc
     */
    private String getErrorAccountNumber() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, ERROR_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * @return default chart code defined in the apc
     */
    private String getDefaultChartCode() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, DEFAULT_TRANS_CHART_CODE_PARM_NM);
    }

    /**
     * @return default account number defined in the apc
     */
    private String getDefaultAccountNumber() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, DEFAULT_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * @return default object code defined in the apc
     */
    private String getDefaultObjectCode() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, DEFAULT_TRANS_OBJECT_CODE_PARM_NM);
    }

    /**
     * Calls businessObjectService to remove all the rows from the transaction load table.
     */
    private void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ProcurementCardTransaction.class, new HashMap());
    }

    /**
     * Loads all the parsed xml transactions into the temp transaction table.
     * 
     * @param transactions - List of ProcurementCardTransaction to load.
     */
    private void loadTransactions(List transactions) {
        businessObjectService.save(transactions);
    }

    /**
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    /**
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * @param dataDictionaryService dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }


    /**
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the workflowDocumentService attribute. 
     * @return Returns the workflowDocumentService.
     */
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
    
    
}