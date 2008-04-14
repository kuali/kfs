/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.service.impl;

import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.AUTO_APPROVE_DOCUMENTS_IND;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.AUTO_APPROVE_NUMBER_OF_DAYS;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_ACCOUNT_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_CHART_CODE_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_OBJECT_CODE_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.ERROR_TRANS_ACCOUNT_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.ERROR_TRANS_CHART_CODE_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.SINGLE_TRANSACTION_IND_PARM_NM;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.DocumentSystemSaveEvent;
import org.kuali.kfs.service.AccountingLineRuleHelperService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.batch.pcard.ProcurementCardAutoApproveDocumentsStep;
import org.kuali.module.financial.batch.pcard.ProcurementCardCreateDocumentsStep;
import org.kuali.module.financial.batch.pcard.ProcurementCardLoadStep;
import org.kuali.module.financial.bo.ProcurementCardHolder;
import org.kuali.module.financial.bo.ProcurementCardSourceAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTransaction;
import org.kuali.module.financial.bo.ProcurementCardTransactionDetail;
import org.kuali.module.financial.bo.ProcurementCardVendor;
import org.kuali.module.financial.document.ProcurementCardDocument;
import org.kuali.module.financial.service.ProcurementCardCreateDocumentService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;


/**
 * This is the default implementation of the ProcurementCardCreateDocumentService interface.
 * 
 * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService
 */
@Transactional
public class ProcurementCardCreateDocumentServiceImpl implements ProcurementCardCreateDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentServiceImpl.class);

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private DataDictionaryService dataDictionaryService;
    private DateTimeService dateTimeService;
    private WorkflowDocumentService workflowDocumentService;
    private AccountingLineRuleHelperService accountingLineRuleUtil;


    /**
     * This method retrieves a collection of credit card transactions and traverses through this list, creating 
     * ProcurementCardDocuments for each card.
     * 
     * @return True if the procurement card documents were created successfully.  If any problem occur while creating the 
     * documents, a runtime exception will be thrown.
     * 
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
                documentService.saveDocument(pcardDocument, DocumentSystemSaveEvent.class);
                // documentService.saveDocumentWithoutRunningValidation(pcardDocument);
            }
            catch (Exception e) {
                LOG.error("Error persisting document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(), e);
                throw new RuntimeException("Error persisting document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage());
            }
        }

        return true;
    }

    /**
     * This method retrieves all the procurement card documents with a status of 'I' and routes them to the next step in the
     * routing path.
     * 
     * @return True if the routing was performed successfully.  A runtime exception will be thrown if any errors occur while routing.
     * 
     * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService#routeProcurementCardDocuments(java.util.List)
     */
    public boolean routeProcurementCardDocuments() {
        List documentList = new ArrayList();
        try {
            documentList = (List) documentService.findByDocumentHeaderStatusCode(ProcurementCardDocument.class, KFSConstants.DocumentStatusCodes.INITIATED);
        }
        catch (WorkflowException e1) {
            LOG.error("Error retrieving pcdo documents for routing: " + e1.getMessage());
            throw new RuntimeException(e1.getMessage());
        }
        for (Iterator iter = documentList.iterator(); iter.hasNext();) {
            ProcurementCardDocument pcardDocument = (ProcurementCardDocument) iter.next();
            try {
                LOG.info("Routing PCDO document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + ".");
                documentService.prepareWorkflowDocument(pcardDocument);

                // calling workflow service to bypass business rule checks
                workflowDocumentService.route(pcardDocument.getDocumentHeader().getWorkflowDocument(), "", null);
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }

        return true;
    }

    /**
     * This method determines if procurement card documents can be auto approved.  A document can be auto approved if 
     * the grace period for allowing auto approval of a procurement card document has passed.  The grace period is defined
     * by a parameter in the parameters table.  The create date of the document is then compared against the current date and 
     * if the difference is larger than the grace period defined, then the document is auto approved.
     * 
     * @return This method always returns true.
     * 
     * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService#autoApproveProcurementCardDocuments()
     */
    public boolean autoApproveProcurementCardDocuments() {
        // check if auto approve is turned on
        boolean autoApproveOn = parameterService.getIndicatorParameter(ProcurementCardAutoApproveDocumentsStep.class, AUTO_APPROVE_DOCUMENTS_IND);

        if (!autoApproveOn) { // This doesn't make sense - ????
            return true;
        }

        List documentList = new ArrayList();

        try {
            documentList = (List) documentService.findByDocumentHeaderStatusCode(ProcurementCardDocument.class, KFSConstants.DocumentStatusCodes.ENROUTE);
        }
        catch (WorkflowException e1) {
            throw new RuntimeException(e1.getMessage());
        }

        // get number of days and type for auto approve
        int autoApproveNumberDays = Integer.parseInt(parameterService.getParameterValue(ProcurementCardAutoApproveDocumentsStep.class, AUTO_APPROVE_NUMBER_OF_DAYS));

        Timestamp currentDate = dateTimeService.getCurrentTimestamp();
        for (Iterator iter = documentList.iterator(); iter.hasNext();) {
            ProcurementCardDocument pcardDocument = (ProcurementCardDocument) iter.next();
            Timestamp docCreateDate = pcardDocument.getDocumentHeader().getWorkflowDocument().getCreateDate();

            // if number of days in route is passed the allowed number, call doc service for super user approve
            if (DateUtils.getDifferenceInDays(docCreateDate, currentDate) > autoApproveNumberDays) {
                // update document description to reflect the auto approval
                pcardDocument.getDocumentHeader().setFinancialDocumentDescription("Auto Approved On " + dateTimeService.toDateTimeString(currentDate) + ".");

                try {
                    LOG.info("Auto approving document # " + pcardDocument.getDocumentHeader().getDocumentNumber());
                    documentService.superUserApproveDocument(pcardDocument, "");
                }
                catch (WorkflowException e) {
                    LOG.error("Error auto approving document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        return true;
    }


    /**
     * This method retrieves a list of transactions from a temporary table, and groups them into document lists, based on 
     * single transaction indicator or a grouping by card.
     * 
     * @return List containing transactions for document.
     */
    private List retrieveTransactions() {
        List groupedTransactions = new ArrayList();

        // retrieve records from transaction table order by card number
        List transactions = (List) businessObjectService.findMatchingOrderBy(ProcurementCardTransaction.class, new HashMap(), KFSPropertyConstants.TRANSACTION_CREDIT_CARD_NUMBER, true);

        // check apc for single transaction documents or multiple by card
        boolean singleTransaction = parameterService.getIndicatorParameter(ProcurementCardCreateDocumentsStep.class, SINGLE_TRANSACTION_IND_PARM_NM);

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
     * Creates a ProcurementCardDocument from the List of transactions given.
     * 
     * @param transactions List of ProcurementCardTransaction objects to be used for creating the document.
     * @return A ProcurementCardDocument populated with the transactions provided.
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
            KualiDecimal documentTotalAmount = KualiDecimal.ZERO;
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

            // Remove duplicate messages from errorText
            String messages[] = StringUtils.split(errorText, ".");
            for (int i = 0; i < messages.length; i++) {
                int countMatches = StringUtils.countMatches(errorText, messages[i]) - 1;
                errorText = StringUtils.replace(errorText, messages[i] + ".", "", countMatches);
            }
            // In case errorText is still too long, truncate it and indicate so.
            Integer documentExplanationMaxLength = dataDictionaryService.getAttributeMaxLength(DocumentHeader.class.getName(), KFSPropertyConstants.EXPLANATION);
            if (documentExplanationMaxLength != null && errorText.length() > documentExplanationMaxLength.intValue()) {
                String truncatedMessage = " ... TRUNCATED.";
                errorText = errorText.substring(0, documentExplanationMaxLength - truncatedMessage.length()) + truncatedMessage;
            }
            pcardDocument.getDocumentHeader().setExplanation(errorText);
        }
        catch (WorkflowException e) {
            LOG.error("Error creating pcdo documents: " + e.getMessage());
            throw new RuntimeException("Error creating pcdo documents: " + e.getMessage());
        }

        return pcardDocument;
    }

    /**
     * Creates card holder record and sets that record to the document given.
     * 
     * @param pcardDocument Procurement card document to place the record in.
     * @param transaction The transaction to set the card holder record fields from.
     */
    private void createCardHolderRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction) {
        ProcurementCardHolder cardHolder = new ProcurementCardHolder();

        cardHolder.setDocumentNumber(pcardDocument.getDocumentNumber());
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
     * Creates a transaction detail record and adds that record to the document provided.
     * 
     * @param pcardDocument Document to place record in.
     * @param transaction Transaction to set fields from.
     * @param transactionLineNumber Line number of the new transaction detail record within the procurement card document.
     * @return The error text that was generated from the creation of the detail records.  If the text is empty, no errors were encountered.
     */
    private String createTransactionDetailRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, Integer transactionLineNumber) {
        ProcurementCardTransactionDetail transactionDetail = new ProcurementCardTransactionDetail();

        // set the document transaction detail fields from the loaded transaction record
        transactionDetail.setDocumentNumber(pcardDocument.getDocumentNumber());
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

        if (GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
            transactionDetail.setTransactionTotalAmount(transaction.getFinancialDocumentTotalAmount().negated());
        }
        else {
            transactionDetail.setTransactionTotalAmount(transaction.getFinancialDocumentTotalAmount());
        }

        // create transaction vendor record
        createTransactionVendorRecord(pcardDocument, transaction, transactionDetail);

        // add transaction detail to document
        pcardDocument.getTransactionEntries().add(transactionDetail);

        // now create the initial source and target lines for this transaction
        return createAndValidateAccountingLines(pcardDocument, transaction, transactionDetail);
    }


    /**
     * Creates a transaction vendor detail record and adds it to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the vendor record on.
     */
    private void createTransactionVendorRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        ProcurementCardVendor transactionVendor = new ProcurementCardVendor();

        transactionVendor.setDocumentNumber(pcardDocument.getDocumentNumber());
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
     * replaced with default and error values if needed. There will be 1 source and 1 target line generated.
     * 
     * @param pcardDocument The procurement card document to add the new accounting lines to.
     * @param transaction The transaction to process into account lines.
     * @param docTransactionDetail The transaction detail to create source and target accounting lines from.
     * @return String containing any error messages.
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
     * Creates the to record for the transaction. The chart of account attributes from the transaction are used to create 
     * the accounting line.
     * 
     * @param transaction The transaction to pull information from to create the accounting line.
     * @param docTransactionDetail The transaction detail to pull information from to populate the accounting line.
     * @return The target accounting line fully populated with values from the parameters passed in. 
     */
    private ProcurementCardTargetAccountingLine createTargetAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardTargetAccountingLine targetLine = new ProcurementCardTargetAccountingLine();

        targetLine.setDocumentNumber(docTransactionDetail.getDocumentNumber());
        targetLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        targetLine.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
        targetLine.setAccountNumber(transaction.getAccountNumber());
        targetLine.setFinancialObjectCode(transaction.getFinancialObjectCode());
        targetLine.setSubAccountNumber(transaction.getSubAccountNumber());
        targetLine.setFinancialSubObjectCode(transaction.getFinancialSubObjectCode());
        targetLine.setProjectCode(transaction.getProjectCode());

        if (GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
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
     * @param transaction The transaction to pull information from to create the accounting line.
     * @param docTransactionDetail The transaction detail to pull information from to populate the accounting line.
     * @return The source accounting line fully populated with values from the parameters passed in.
     */
    private ProcurementCardSourceAccountingLine createSourceAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardSourceAccountingLine sourceLine = new ProcurementCardSourceAccountingLine();

        sourceLine.setDocumentNumber(docTransactionDetail.getDocumentNumber());
        sourceLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        sourceLine.setChartOfAccountsCode(getDefaultChartCode());
        sourceLine.setAccountNumber(getDefaultAccountNumber());
        sourceLine.setFinancialObjectCode(getDefaultObjectCode());

        if (GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
            sourceLine.setAmount(transaction.getFinancialDocumentTotalAmount().negated());
        }
        else {
            sourceLine.setAmount(transaction.getFinancialDocumentTotalAmount());
        }

        return sourceLine;
    }

    /**
     * Validates the chart of account attributes for existence and active indicator. Will substitute for defined 
     * default parameters or set fields to empty that if they have errors.
     * 
     * @param targetLine The target accounting line to be validated.
     * @return String with error messages discovered during validation.  An empty string indicates no validation errors were found.
     */
    private String validateTargetAccountingLine(ProcurementCardTargetAccountingLine targetLine) {
        String errorText = "";

        targetLine.refresh();

        if (!accountingLineRuleUtil.isValidObjectCode(targetLine.getObjectCode(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Chart " + targetLine.getChartOfAccountsCode() + " Object Code " + targetLine.getFinancialObjectCode() + " is invalid; using default Object Code.";
            LOG.info(tempErrorText);
            errorText += " " + tempErrorText;

            targetLine.setFinancialObjectCode(getDefaultObjectCode());
        }

        if (StringUtils.isNotBlank(targetLine.getSubAccountNumber()) && !accountingLineRuleUtil.isValidSubAccount(targetLine.getSubAccount(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " Sub Account " + targetLine.getSubAccountNumber() + " is invalid; Setting Sub Account to blank.";
            LOG.info(tempErrorText);
            errorText += " " + tempErrorText;

            targetLine.setSubAccountNumber("");
        }

        // refresh again since further checks depend on the above attributes (which could have changed)
        targetLine.refresh();

        if (StringUtils.isNotBlank(targetLine.getFinancialSubObjectCode()) && !accountingLineRuleUtil.isValidSubObjectCode(targetLine.getSubObjectCode(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " Object Code " + targetLine.getFinancialObjectCode() + " Sub Object Code " + targetLine.getFinancialSubObjectCode() + " is invalid; setting Sub Object to blank.";
            LOG.info(tempErrorText);
            errorText += " " + tempErrorText;

            targetLine.setFinancialSubObjectCode("");
        }

        if (StringUtils.isNotBlank(targetLine.getProjectCode()) && !accountingLineRuleUtil.isValidProjectCode(targetLine.getProject(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Project Code " + targetLine.getProjectCode() + " is invalid; setting to blank.");
            errorText += " Project Code " + targetLine.getProjectCode() + " is invalid; setting to blank.";

            targetLine.setProjectCode("");
        }

        if (!accountingLineRuleUtil.isValidAccount(targetLine.getAccount(), dataDictionaryService.getDataDictionary()) || targetLine.getAccount().isExpired()) {
            String tempErrorText = "Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " is invalid; using error account.";
            LOG.info(tempErrorText);
            errorText += " " + tempErrorText;

            targetLine.setChartOfAccountsCode(getErrorChartCode());
            targetLine.setAccountNumber(getErrorAccountNumber());
        }

        targetLine.refresh();

        // clear out GlobalVariable error map, since we have taken care of the errors
        GlobalVariables.setErrorMap(new ErrorMap());

        return errorText;
    }

    /**
     * Retrieves the error chart code from the parameter table.
     * @return The error chart code defined in the parameter table.
     */
    private String getErrorChartCode() {
        return parameterService.getParameterValue(ProcurementCardCreateDocumentsStep.class, ERROR_TRANS_CHART_CODE_PARM_NM);
    }

    /**
     * Retrieves the error account number from the parameter table.
     * @return The error account number defined in the parameter table.
     */
    private String getErrorAccountNumber() {
        return parameterService.getParameterValue(ProcurementCardCreateDocumentsStep.class, ERROR_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * Retrieves the default chard code from the parameter table.
     * @return The default chart code defined in the parameter table.
     */
    private String getDefaultChartCode() {
        return parameterService.getParameterValue(ProcurementCardLoadStep.class, DEFAULT_TRANS_CHART_CODE_PARM_NM);
    }

    /**
     * Retrieves the default account number from the parameter table.
     * @return The default account number defined in the parameter table.
     */
    private String getDefaultAccountNumber() {
        return parameterService.getParameterValue(ProcurementCardLoadStep.class, DEFAULT_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * Retrieves the default object code from the parameter table.
     * @return The default object code defined in the parameter table.
     */
    private String getDefaultObjectCode() {
        return parameterService.getParameterValue(ProcurementCardLoadStep.class, DEFAULT_TRANS_OBJECT_CODE_PARM_NM);
    }

    /**
     * Calls businessObjectService to remove all the procurement card transaction rows from the transaction load table.
     */
    private void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ProcurementCardTransaction.class, new HashMap());
    }

    /**
     * Loads all the parsed XML transactions into the temp transaction table.
     * 
     * @param transactions List of ProcurementCardTransactions to load.
     */
    private void loadTransactions(List transactions) {
        businessObjectService.save(transactions);
    }

    /**
     * Sets the parameterService attribute.
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the documentService attribute.
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    /**
     * Gets the dataDictionaryService attribute.
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute.
     * @param dataDictionaryService dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }


    /**
     * Gets the dateTimeService attribute.
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute.
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

    /**
     * Sets the accountingLineRuleUtil attribute value.
     * @param accountingLineRuleUtil The accountingLineRuleUtil to set.
     */
    public void setAccountingLineRuleUtil(AccountingLineRuleHelperService accountingLineRuleUtil) {
        this.accountingLineRuleUtil = accountingLineRuleUtil;
    }

}
