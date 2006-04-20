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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.ProcurementCardSourceAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTransaction;
import org.kuali.module.financial.bo.ProcurementCardTransactionDetail;
import org.kuali.module.financial.document.ProcurementCardDocument;
import org.kuali.module.financial.rules.AccountingLineRuleUtil;
import org.kuali.module.financial.service.ProcurementCardCreateDocumentService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Implementation of ProcurementCardCreateDocumentService
 * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardCreateDocumentServiceImpl implements ProcurementCardCreateDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentServiceImpl.class);

    private final static String PCARD_DOCUMENT_PARAMETERS_SEC_GROUP = "PCardDocumentParameters";
    private final static String SINGLE_TRANSACTION_IND_PARM_NM = "SINGLE_TRANSACTION_INDICATOR";
    private final static String ERROR_TRANS_CHART_CODE_PARM_NM = "ERROR_TRANSACTION_CHART_CODE";
    private final static String ERROR_TRANS_ACCOUNT_PARM_NM = "ERROR_TRANSACTION_ACCOUNT_NUMBER";
    private final static String DEFAULT_TRANS_CHART_CODE_PARM_NM = "DEFAULT_TRANSACTION_CHART_CODE";
    private final static String DEFAULT_TRANS_ACCOUNT_PARM_NM = "DEFAULT_TRANSACTION_ACCOUNT_NUMBER";
    private final static String DEFAULT_TRANS_OBJECT_CODE_PARM_NM = "DEFAULT_TRANSACTION_OBJECT_CODE";

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private DataDictionaryService dataDictionaryService;


    /**
     * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService#createProcurementCardDocuments()
     */
    public boolean createProcurementCardDocuments() {
        List documents = new ArrayList();

        // retrieve records from transaction table order by card number
        List transactions = (List) businessObjectService.findMatchingOrderBy(ProcurementCardTransaction.class, new HashMap(),
                "transactionCreditCardNumber", true);

        // check apc for single transaction documents or multple by card
        boolean singleTransaction = false;
        String singleTransactionParmVal = kualiConfigurationService.getApplicationParameterValue(
                PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, SINGLE_TRANSACTION_IND_PARM_NM);
        if (Constants.ACTIVE_INDICATOR.equals(singleTransactionParmVal.toUpperCase())) {
            singleTransaction = true;
        }

        // iterate through trans list, if single call create document for each transaction, else build list until card number changes
        List documentTransactions = new ArrayList();
        String previousCardNumber = "";
        for (Iterator iter = transactions.iterator(); iter.hasNext();) {
            ProcurementCardTransaction transaction = (ProcurementCardTransaction) iter.next();

            if (singleTransaction) {
                documentTransactions.add(transaction);
                documents.add(createProcurementCardDocument(documentTransactions));
                documentTransactions = new ArrayList();
            }
            else {
                if ((StringUtils.isNotBlank(previousCardNumber) && !previousCardNumber.equals(transaction
                        .getTransactionCreditCardNumber()))
                        || !iter.hasNext()) {
                    documents.add(createProcurementCardDocument(documentTransactions));
                    documentTransactions = new ArrayList();
                }
                documentTransactions.add(transaction);
                previousCardNumber = transaction.getTransactionCreditCardNumber();
            }
        }

        return true;
    }


    /**
     * Creates a ProcurementCardDocument from the List of transactions
     * @param transactions - List of ProcurementCardTransaction objects
     * @return ProcurementCardDocument
     */
    private ProcurementCardDocument createProcurementCardDocument(List transactions) {
        ProcurementCardDocument pcardDocument = null;

        try {
            // get new document from doc service
            pcardDocument = (ProcurementCardDocument) documentService.getNewDocument(ProcurementCardDocument.class);

            // set the trans cycle and card details on the document from the first transaction
            setDocumentHeaderFields(pcardDocument, (ProcurementCardTransaction) transactions.get(0));

            // for each transaction, create transaction detail object and then acct lines for the detail
            List docTransactionDetails = new ArrayList();
            int transactionLineNumber = 1;
            KualiDecimal documentTotalAmount = new KualiDecimal(0);
            String errorText = "";
            for (Iterator iter = transactions.iterator(); iter.hasNext();) {
                ProcurementCardTransaction transaction = (ProcurementCardTransaction) iter.next();
                ProcurementCardTransactionDetail docTransactionDetail = new ProcurementCardTransactionDetail();

                // set the document transaction detail fields from the loaded transaction record
                docTransactionDetail.setFinancialDocumentNumber(pcardDocument.getFinancialDocumentNumber());
                docTransactionDetail.setFinancialDocumentTransactionLineNumber(new Integer(transactionLineNumber));
                docTransactionDetail.setTransactionDate(transaction.getTransactionDate());
                docTransactionDetail.setTransactionMerchantCategoryCode(transaction.getTransactionMerchantCategoryCode());
                docTransactionDetail.setTransactionReferenceNumber(transaction.getTransactionReferenceNumber());
                docTransactionDetail.setTransactionVendorName(transaction.getTransactionVendorName());

                // add transaction detail to document
                pcardDocument.getTransactionEntries().add(docTransactionDetail);

                // now create the initial source and target lines for this transaction
                errorText = createAndValidateAccountingLines(pcardDocument, transaction, docTransactionDetail);

                // add to the document transaction detail list
                docTransactionDetails.add(docTransactionDetail);

                // update document total
                documentTotalAmount = documentTotalAmount.add(transaction.getFinancialDocumentTotalAmount());

                transactionLineNumber++;
            }

            pcardDocument.getDocumentHeader().setFinancialDocumentTotalAmount(documentTotalAmount);
            pcardDocument.setExplanation(errorText);
        }
        catch (WorkflowException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return pcardDocument;
    }

    /**
     * Sets transaction and card fields on the document from the give ProcurmentCardTransaction record.
     * @param pcardDocument - document to set fields in
     * @param transaction - transaction to set fields from
     */
    private void setDocumentHeaderFields(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction) {
        pcardDocument.setTransactionCreditCardNumber(transaction.getTransactionCreditCardNumber());
        pcardDocument.setFinancialDocumentCardHolderName(transaction.getFinancialDocumentCardHolderName());
        pcardDocument.setTransactionCycleStartDate(transaction.getTransactionCycleStartDate());
        pcardDocument.setTransactionCycleEndDate(transaction.getTransactionCycleEndDate());
        pcardDocument.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
        pcardDocument.setAccountNumber(transaction.getAccountNumber());
        pcardDocument.setSubAccountNumber(transaction.getSubAccountNumber());
    }

    /**
     * From the transaction accounting attributes, creates source and target accounting lines. Attributes are validated first, and
     * replaced with default and error values if needed. There will be 1 source and 1 target line generated
     * @param transaction - transaction to process
     * @param docTransactionDetail - Object to put accounting lines into
     * @return String containing any error messages
     */
    private String createAndValidateAccountingLines(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction,
            ProcurementCardTransactionDetail docTransactionDetail) {
        // build target line, note this is not displayed on the document, (actually is the from side, reversed)
        ProcurementCardTargetAccountingLine targetLine = createTargetAccountingLine(transaction, docTransactionDetail);

        // add line to transaction through document since document contains the next sequence number fields
        pcardDocument.addTargetAccountingLine(targetLine);

        // build source line, note this is actually the displayed lines on the document
        ProcurementCardSourceAccountingLine sourceLine = createSourceAccountingLine(transaction, docTransactionDetail);

        return validateSourceAccountingLine(sourceLine);
    }

    /**
     * Creates the from record for the transaction. The clearing chart, account, and object code is used for creating the line.
     * @param transaction
     * @param docTransactionDetail
     * @return ProcurementCardTargetAccountingLine
     */
    private ProcurementCardTargetAccountingLine createTargetAccountingLine(ProcurementCardTransaction transaction,
            ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardTargetAccountingLine targetLine = new ProcurementCardTargetAccountingLine();

        targetLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        targetLine.setChartOfAccountsCode(getDefaultChartCode());
        targetLine.setAccountNumber(getDefaultAccountNumber());
        targetLine.setFinancialObjectCode(getDefaultObjectCode());
        targetLine.setSubAccountNumber("");
        targetLine.setFinancialSubObjectCode("");
        targetLine.setProjectCode("");
        targetLine.setAmount(transaction.getFinancialDocumentTotalAmount());

        return targetLine;
    }

    /**
     * Creates the to record for the transaction. The COA attributes from the transaction are used to create the accounting line.
     * @param transaction
     * @param docTransactionDetail
     * @return ProcurementCardSourceAccountingLine
     */
    private ProcurementCardSourceAccountingLine createSourceAccountingLine(ProcurementCardTransaction transaction,
            ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardSourceAccountingLine sourceLine = new ProcurementCardSourceAccountingLine();

        //TODO: Get remaining COA attributes from the transaction once they are added
        sourceLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        sourceLine.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
        sourceLine.setAccountNumber(transaction.getAccountNumber());
        sourceLine.setFinancialObjectCode("");
        sourceLine.setSubAccountNumber(transaction.getSubAccountNumber());
        sourceLine.setFinancialSubObjectCode("");
        sourceLine.setProjectCode("");
        sourceLine.setAmount(transaction.getFinancialDocumentTotalAmount());

        return sourceLine;
    }

    /**
     * Validates the COA attributes for existence and active indicator. Will substitute for defined default parameters or set fields
     * to empty that have errors.
     * @param sourceLine
     * @return String with error messages
     */
    private String validateSourceAccountingLine(ProcurementCardSourceAccountingLine sourceLine) {
        String errorText = "";

        sourceLine.refresh();

        if (!AccountingLineRuleUtil.isValidObjectCode(sourceLine.getObjectCode(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Object Code " + sourceLine.getFinancialObjectCode() + " is invalid. Using default Object Code.");
            errorText += " Object Code " + sourceLine.getFinancialObjectCode() + " is invalid. Using default Object Code.";

            sourceLine.setFinancialObjectCode(getDefaultObjectCode());
        }

        if (StringUtils.isNotBlank(sourceLine.getSubAccountNumber())
                && !AccountingLineRuleUtil.isValidSubAccount(sourceLine.getSubAccount(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Sub Account " + sourceLine.getSubAccountNumber() + " is invalid. Setting to blank");
            errorText += " Sub Account " + sourceLine.getSubAccountNumber() + " is invalid. Setting to blank";

            sourceLine.setSubAccountNumber("");
        }

        // refresh again since further checks depend on the above attributes (which could have changed)
        sourceLine.refresh();

        if (StringUtils.isNotBlank(sourceLine.getFinancialSubObjectCode())
                && !AccountingLineRuleUtil.isValidSubObjectCode(sourceLine.getSubObjectCode(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Sub Object Code " + sourceLine.getFinancialSubObjectCode() + " is invalid. Setting to blank");
            errorText += " Sub Object Code " + sourceLine.getFinancialSubObjectCode() + " is invalid. Setting to blank";

            sourceLine.setFinancialSubObjectCode("");
        }

        if (StringUtils.isNotBlank(sourceLine.getProjectCode())
                && !AccountingLineRuleUtil.isValidProjectCode(sourceLine.getProject(), dataDictionaryService.getDataDictionary())) {
            LOG.info("Project Code " + sourceLine.getProjectCode() + " is invalid. Setting to blank");
            errorText += " Project Code " + sourceLine.getProjectCode() + " is invalid. Setting to blank";

            sourceLine.setProjectCode("");
        }

        if (!AccountingLineRuleUtil.isValidAccount(sourceLine.getAccount(), dataDictionaryService.getDataDictionary()) || sourceLine.getAccount().isExpired()) {
            LOG.info("Account " + sourceLine.getAccountNumber() + " is invalid. Using error account.");
            errorText += " Account " + sourceLine.getAccountNumber() + " is invalid. Using error Chart & Account.";

            sourceLine.setChartOfAccountsCode(getErrorChartCode());
            sourceLine.setAccountNumber(getErrorAccountNumber());
        }

        return errorText;
    }

    /**
     * @return error chart code defined in the apc
     */
    private String getErrorChartCode() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP,
                ERROR_TRANS_CHART_CODE_PARM_NM);
    }

    /**
     * @return error account number defined in the apc
     */
    private String getErrorAccountNumber() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP,
                ERROR_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * @return default chart code defined in the apc
     */
    private String getDefaultChartCode() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP,
                DEFAULT_TRANS_CHART_CODE_PARM_NM);
    }

    /**
     * @return default account number defined in the apc
     */
    private String getDefaultAccountNumber() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP,
                DEFAULT_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * @return default object code defined in the apc
     */
    private String getDefaultObjectCode() {
        return kualiConfigurationService.getApplicationParameterValue(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP,
                DEFAULT_TRANS_OBJECT_CODE_PARM_NM);
    }

    /**
     * Calls businessObjectService to remove all the rows from the transaction load table.
     */
    private void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ProcurementCardTransaction.class, new HashMap());
    }

    /**
     * Loads all the parsed xml transactions into the temp transaction table.
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
}