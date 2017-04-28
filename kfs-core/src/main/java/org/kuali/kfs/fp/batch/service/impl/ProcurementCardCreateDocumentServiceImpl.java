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
package org.kuali.kfs.fp.batch.service.impl;

import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.AUTO_APPROVE_DOCUMENTS_IND;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.AUTO_APPROVE_NUMBER_OF_DAYS;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_ACCOUNT_PARM_NM;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_CHART_CODE_PARM_NM;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_OBJECT_CODE_PARM_NM;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.ERROR_TRANS_ACCOUNT_PARM_NM;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.SINGLE_TRANSACTION_IND_PARM_NM;
import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.FinancialDocumentTypeCodes.PROCUREMENT_CARD;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.fp.batch.ProcurementCardAutoApproveDocumentsStep;
import org.kuali.kfs.fp.batch.ProcurementCardCreateDocumentsStep;
import org.kuali.kfs.fp.batch.ProcurementCardLoadStep;
import org.kuali.kfs.fp.batch.ProcurementCardReportType;
import org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.ProcurementCardDefault;
import org.kuali.kfs.fp.businessobject.ProcurementCardHolder;
import org.kuali.kfs.fp.businessobject.ProcurementCardSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardVendor;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.VelocityEmailService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3Add;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3AddItem;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3AddUser;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3Fuel;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3Generic;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3Lodging;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3NonFuel;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3Rental;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3ShipSvc;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3Transport;
import edu.arizona.kfs.fp.businessobject.ProcurementCardLevel3TransportLeg;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTranAddItem;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTranNonFuel;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTranShipSvc;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTranTransportLeg;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTransaction;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTransactionDetail;

/**
 * This is the default implementation of the ProcurementCardCreateDocumentService interface.
 *
 * @see org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService
 */

public class ProcurementCardCreateDocumentServiceImpl implements ProcurementCardCreateDocumentService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentServiceImpl.class);

    protected static final String WORKFLOW_SEARCH_RESULT_KEY = "routeHeaderId";

    protected ParameterService parameterService;
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected WorkflowDocumentService workflowDocumentService;
    protected DataDictionaryService dataDictionaryService;
    protected DateTimeService dateTimeService;
    protected AccountingLineRuleHelperService accountingLineRuleUtil;
    protected CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    private VelocityEmailService procurementCardCreateEmailService;

    public static final String DOCUMENT_DESCRIPTION_PATTERN = "{0}-{1}-{2}-{3}";

    /**
     * This method retrieves a collection of credit card transactions and traverses through this list, creating
     * ProcurementCardDocuments for each card.
     *
     * @return True if the procurement card documents were created successfully.  If any problem occur while creating the
     * documents, a runtime exception will be thrown.
     *
     * @see org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService#createProcurementCardDocuments()
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional
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
                if ( LOG.isInfoEnabled() ) {
                    LOG.info("Saved Procurement Card document: "+pcardDocument.getDocumentNumber());
                }
            }
            catch (Exception e) {
                LOG.error("Error persisting document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(), e);
                throw new RuntimeException("Error persisting document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(), e);
            }
        }

        // send email notification
        sendEmailNotification(documents);

        return true;
    }

    /**
     * Sending email notification with initializing template variable details in report.
     *
     * @param documents
     */
    protected void sendEmailNotification(List<ProcurementCardDocument> documents) {
        List<ProcurementCardReportType> summaryList = getSortedReportSummaryList(documents);
        int totalBatchTransactions = getBatchTotalTransactionCnt(summaryList);
        DateFormat dateFormat = getDateFormat(KFSConstants.CoreModuleNamespaces.FINANCIAL, KFSConstants.ProcurementCardParameters.PCARD_BATCH_CREATE_DOC_STEP, KFSConstants.ProcurementCardParameters.BATCH_SUMMARY_RUNNING_TIMESTAMP_FORMAT, KFSConstants.ProcurementCardEmailTimeFormat);

        // Create formatter for payment amounts and batch run time
        String batchRunTime = dateFormat.format(dateTimeService.getCurrentDate());

        final Map<String, Object> templateVariables = new HashMap<String, Object>();

        templateVariables.put(KFSConstants.ProcurementCardEmailVariableTemplate.DOC_CREATE_DATE, batchRunTime);
        templateVariables.put(KFSConstants.ProcurementCardEmailVariableTemplate.TRANSACTION_COUNTER, totalBatchTransactions);
        templateVariables.put(KFSConstants.ProcurementCardEmailVariableTemplate.TRANSACTION_SUMMARY_LIST, summaryList);

        // Handle for email sending exception
        procurementCardCreateEmailService.sendEmailNotification(templateVariables);
    }

    /**
     * Retrieve date formatter used to format batch running timestamp in report. System parameter has precedence over system default String.
     *
     * @return
     */
    protected DateFormat getDateFormat(String namespaceCode, String componentCode, String parameterName, String defaultValue) {
        DateFormat dateFormat = new SimpleDateFormat(defaultValue);
        if (getParameterService().parameterExists(namespaceCode, componentCode, parameterName)) {
            String formatParmVal = getParameterService().getParameterValueAsString(namespaceCode, componentCode, parameterName, defaultValue);
            if (StringUtils.isNotBlank(formatParmVal)) {
                try {
                    dateFormat = new SimpleDateFormat(formatParmVal);
                }
                catch (IllegalArgumentException e) {
                    LOG.error("Parameter PCARD_BATCH_SUMMARY_RUNNING_TIMESTAMP_FORMAT used by ProcurementCardCreateDocumentsStep does not set up properly. Use system default timestamp format instead for generating report.");
                    dateFormat = new SimpleDateFormat(defaultValue);
                }
            }
        }

        dateFormat.setLenient(false);
        return dateFormat;
    }

    /**
     * Get the total number of transactions processed by this batch run
     *
     * @param summaryList
     * @return
     */
    protected int getBatchTotalTransactionCnt(List<ProcurementCardReportType> summaryList) {
        int totalBatchTransactionCnt = 0;

        for (ProcurementCardReportType procurementCardReportType : summaryList) {
            totalBatchTransactionCnt += procurementCardReportType.getTotalTranNumber();
        }
        return totalBatchTransactionCnt;
    }

    /**
     * Build the sorted batch report summary list
     *
     * @param documents
     * @return
     */
    protected List<ProcurementCardReportType> getSortedReportSummaryList(List<ProcurementCardDocument> documents) {
        KualiDecimal totalAmount;
        int totalTransactionCounter;

        HashMap<String, String> totalDocMap;

        Map<Date, HashMap<String, String>> docMapByPstDt = new HashMap<Date, HashMap<String, String>>();
        Map<Date, Integer> transctionMapByPstDt = new HashMap<Date, Integer>();
        Map<Date, KualiDecimal> totalAmountMapByPstDt = new HashMap<Date, KualiDecimal>();
        Iterator iter = null;
        Date keyDate = null;

        // walk through each doc and calculate the total amount per bank transaction posting date.
        for (ProcurementCardDocument pDoc : documents) {
            iter = pDoc.getTransactionEntries().iterator();
            while (iter.hasNext()) {
                ProcurementCardTransactionDetail pCardDetail = (ProcurementCardTransactionDetail) iter.next();

                keyDate = pCardDetail.getTransactionPostingDate();

                if (docMapByPstDt.containsKey(keyDate)) {
                    totalDocMap = docMapByPstDt.get(keyDate);
                    totalTransactionCounter = transctionMapByPstDt.get(keyDate).intValue();
                    totalAmount = totalAmountMapByPstDt.get(keyDate);
                }
                else {
                    totalDocMap = new HashMap<String, String>();
                    totalTransactionCounter = 0;
                    totalAmount = KualiDecimal.ZERO;
                }
                // update number of transactions
                totalTransactionCounter++;
                // update number of eDocs
                if (!totalDocMap.containsKey(pDoc.getDocumentNumber())) {
                    totalDocMap.put(pDoc.getDocumentNumber(), pDoc.getDocumentNumber());
                }
                // update transaction total
                totalAmount = totalAmount.add(pCardDetail.getTransactionTotalAmount());

                docMapByPstDt.put(keyDate, totalDocMap);
                transctionMapByPstDt.put(keyDate, totalTransactionCounter);
                totalAmountMapByPstDt.put(keyDate, totalAmount);
            }
        }

        List<ProcurementCardReportType> summaryList = buildBatchReportSummary(docMapByPstDt, transctionMapByPstDt, totalAmountMapByPstDt);

        sortingSummaryList(summaryList);

        return summaryList;
    }

    /**
     * Build Procurement Card report object.
     *
     * @param docMapByPstDt
     * @param transctionMapByPstDt
     * @param totalAmountMapByPstDt
     * @return
     */
    protected List<ProcurementCardReportType> buildBatchReportSummary(Map<Date, HashMap<String, String>> docMapByPstDt, Map<Date, Integer> transctionMapByPstDt, Map<Date, KualiDecimal> totalAmountMapByPstDt) {
        List<ProcurementCardReportType> summaryList = new ArrayList<ProcurementCardReportType>();
        ProcurementCardReportType reportEntry;

        Formatter currencyFormatter = new CurrencyFormatter();
        DateFormat dateFormatter = getDateFormat(KFSConstants.CoreModuleNamespaces.FINANCIAL, KFSConstants.ProcurementCardParameters.PCARD_BATCH_CREATE_DOC_STEP, KFSConstants.ProcurementCardParameters.BATCH_SUMMARY_POSTING_DATE_FORMAT,KFSConstants.ProcurementCardTransactionTimeFormat);

        for (Date keyDate : docMapByPstDt.keySet()) {
            reportEntry = new ProcurementCardReportType();
            reportEntry.setTransactionPostingDate(keyDate);

            reportEntry.setFormattedPostingDate(dateFormatter.format(keyDate));
            reportEntry.setTotalDocNumber(docMapByPstDt.get(keyDate).keySet().isEmpty() ? 0 : docMapByPstDt.get(keyDate).keySet().size());
            reportEntry.setTotalTranNumber(transctionMapByPstDt.get(keyDate));
            reportEntry.setTotalAmount(currencyFormatter.formatForPresentation(totalAmountMapByPstDt.get(keyDate)).toString());
            summaryList.add(reportEntry);
        }
        return summaryList;
    }


    /**
     * Sorting the report summary list by transaction posting date
     *
     * @param summaryList
     */
    protected void sortingSummaryList(List<ProcurementCardReportType> summaryList) {
        Comparator<ProcurementCardReportType> comparator = new Comparator<ProcurementCardReportType>() {

            @Override
            public int compare(ProcurementCardReportType o1, ProcurementCardReportType o2) {

                if (o1 == null && o1 == null) {
                    return 0;
                }

                if (o1 == null || o2 == null) {
                    return o1 == null ? -1 : 1;
                }

                return o1.getTransactionPostingDate().compareTo(o2.getTransactionPostingDate());
            }

        };

        // sort by posting date
        Collections.sort(summaryList, comparator);
    }

    /**
     * This method retrieves all the procurement card documents with a status of 'I' and routes them to the next step in the
     * routing path.
     *
     * @return True if the routing was performed successfully.  A runtime exception will be thrown if any errors occur while routing.
     *
     * @see org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService#routeProcurementCardDocuments(java.util.List)
     */
    @Override
    public boolean routeProcurementCardDocuments() {

        Collection<ProcurementCardDocument> procurementCardDocumentList = retrieveProcurementCardDocumentsToRoute(KewApiConstants.ROUTE_HEADER_SAVED_CD);

        //Collections.reverse(documentIdList);
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Number of PCards to Route: "+procurementCardDocumentList.size());
        }

        for (ProcurementCardDocument pcardDocument: procurementCardDocumentList) {
            try {
                if ( LOG.isInfoEnabled() ) {
                    LOG.info("Routing PCDO document # " + pcardDocument.getDocumentNumber() + ".");
                }
                documentService.prepareWorkflowDocument(pcardDocument);

                //** NOTE
                //
                //     Calling workflow service to BYPASS business rule checks
                //
                //** NOTE
                workflowDocumentService.route( pcardDocument.getDocumentHeader().getWorkflowDocument(), "", null);
             }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + pcardDocument.getDocumentNumber() + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(),e);
            }
        }

        return true;
    }

    /**
     * Returns a list of all initiated but not yet routed procurement card documents, using the KualiWorkflowInfo service.
     * @return a list of procurement card documents to route
     */
    protected Collection<ProcurementCardDocument> retrieveProcurementCardDocumentsToRoute(String statusCode){

        try {
            return this.getFinancialSystemDocumentService().findByWorkflowStatusCode(ProcurementCardDocument.class, DocumentStatus.fromCode(statusCode));
        } catch (WorkflowException e) {
            LOG.error("Error searching for enroute procurement card documents " + e.getMessage());
            throw new RuntimeException(e.getMessage(),e);
        }

    }

    /**
     * This method determines if procurement card documents can be auto approved.  A document can be auto approved if
     * the grace period for allowing auto approval of a procurement card document has passed.  The grace period is defined
     * by a parameter in the parameters table.  The create date of the document is then compared against the current date and
     * if the difference is larger than the grace period defined, then the document is auto approved.
     *
     * @return This method always returns true.
     *
     * @see org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService#autoApproveProcurementCardDocuments()
     */
    @Override
    public boolean autoApproveProcurementCardDocuments() {
        // check if auto approve is turned on
        boolean autoApproveOn = parameterService.getParameterValueAsBoolean(ProcurementCardAutoApproveDocumentsStep.class, AUTO_APPROVE_DOCUMENTS_IND);

        if (!autoApproveOn) { // no auto approve?  then skip out of here...
            return true;
        }

        Collection<ProcurementCardDocument> pcardDocumentList = retrieveProcurementCardDocumentsToRoute(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);

        // get number of days and type for auto approve
        int autoApproveNumberDays = Integer.parseInt(parameterService.getParameterValueAsString(ProcurementCardAutoApproveDocumentsStep.class, AUTO_APPROVE_NUMBER_OF_DAYS));

        Timestamp currentDate = dateTimeService.getCurrentTimestamp();
        for (ProcurementCardDocument pcardDocument: pcardDocumentList) {
            try {

                // prevent PCard documents from auto approving if they have capital asset info to collect
                if(capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(pcardDocument)) {
                    continue;
                }

                // if number of days in route is passed the allowed number, call doc service for super user approve
                Timestamp docCreateDate = new Timestamp( pcardDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis() );
                if (KfsDateUtils.getDifferenceInDays(docCreateDate, currentDate) > autoApproveNumberDays) {
                    // update document description to reflect the auto approval
                    pcardDocument.getDocumentHeader().setDocumentDescription("Auto Approved On " + dateTimeService.toDateTimeString(currentDate) + ".");

                    if ( LOG.isInfoEnabled() ) {
                        LOG.info("Auto approving document # " + pcardDocument.getDocumentHeader().getDocumentNumber());
                    }
                    pcardDocument.setAutoApprovedIndicator(true);
                    documentService.superUserApproveDocument(pcardDocument, "");
                }
            } catch (WorkflowException e) {
                LOG.error("Error auto approving document # " + pcardDocument.getDocumentNumber() + " " + e.getMessage(),e);
                throw new RuntimeException(e.getMessage(),e);
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
    @SuppressWarnings("rawtypes")
    protected List retrieveTransactions() {
        List groupedTransactions = new ArrayList();

        // retrieve records from transaction table order by card number
        List transactions = (List) businessObjectService.findMatchingOrderBy(ProcurementCardTransaction.class, new HashMap(), KFSPropertyConstants.TRANSACTION_CREDIT_CARD_NUMBER, true);

        // check apc for single transaction documents or multiple by card
        boolean singleTransaction = parameterService.getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, SINGLE_TRANSACTION_IND_PARM_NM);

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
    protected ProcurementCardDocument createProcurementCardDocument(List transactions) {
        ProcurementCardDocument pcardDocument = null;

        try {
            // get new document from doc service
            pcardDocument = (ProcurementCardDocument) SpringContext.getBean(DocumentService.class).getNewDocument(PROCUREMENT_CARD);

            List<CapitalAssetInformation> capitalAssets = pcardDocument.getCapitalAssetInformation();
            for (CapitalAssetInformation capitalAsset : capitalAssets) {
                if (ObjectUtils.isNotNull(capitalAsset) && ObjectUtils.isNotNull(capitalAsset.getCapitalAssetInformationDetails())) {
                    capitalAsset.setDocumentNumber(pcardDocument.getDocumentNumber());
                }
            }

            ProcurementCardTransaction trans = (ProcurementCardTransaction) transactions.get(0);
            String errors = validateTransaction(trans);
            createCardHolderRecord(pcardDocument, trans);

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

            pcardDocument.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(documentTotalAmount);
            // PCDO Default Description
            //pcardDocument.getDocumentHeader().setDocumentDescription("SYSTEM Generated");
            setupDocumentDescription(pcardDocument);

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
            LOG.error("Error creating pcdo documents: " + e.getMessage(),e);
            throw new RuntimeException("Error creating pcdo documents: " + e.getMessage(),e);
        }

        return pcardDocument;
    }

    /**
     * Set up PCDO document description as "cardholder name-card number (which is 4 digits)-chartOfAccountsCode-default account"
     *
     * @param pcardDocument
     */
    protected void setupDocumentDescription(ProcurementCardDocument pcardDocument) {
        ProcurementCardHolder cardHolder = pcardDocument.getProcurementCardHolder();

        if (ObjectUtils.isNotNull(cardHolder)) {
            String cardHolderName = StringUtils.left(cardHolder.getCardHolderName(), 23);
            String lastFourDigits = StringUtils.right(cardHolder.getTransactionCreditCardNumber(), 4);
            String chartOfAccountsCode = cardHolder.getChartOfAccountsCode();
            String accountNumber = cardHolder.getAccountNumber();

            String description = MessageFormat.format(DOCUMENT_DESCRIPTION_PATTERN, cardHolderName, lastFourDigits, chartOfAccountsCode, accountNumber);
            pcardDocument.getDocumentHeader().setDocumentDescription(description);
        }
    }

    /**
     * Creates card holder record and sets that record to the document given.
     *
     * @param pcardDocument Procurement card document to place the record in.
     * @param transaction The transaction to set the card holder record fields from.
     */
    protected void createCardHolderRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction) {
        ProcurementCardHolder cardHolder = new ProcurementCardHolder();

        cardHolder.setDocumentNumber(pcardDocument.getDocumentNumber());
        cardHolder.setTransactionCreditCardNumber(transaction.getTransactionCreditCardNumber());

            final ProcurementCardDefault procurementCardDefault = retrieveProcurementCardDefault(transaction.getTransactionCreditCardNumber());
            if (procurementCardDefault != null) {
            if (getParameterService().getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_CARD_HOLDER_DEFAULT_PARAMETER_NAME)) {
                cardHolder.setCardCycleAmountLimit(procurementCardDefault.getCardCycleAmountLimit());
                cardHolder.setCardCycleVolumeLimit(procurementCardDefault.getCardCycleVolumeLimit());
                cardHolder.setCardHolderAlternateName(procurementCardDefault.getCardHolderAlternateName());
                cardHolder.setCardHolderCityName(procurementCardDefault.getCardHolderCityName());
                cardHolder.setCardHolderLine1Address(procurementCardDefault.getCardHolderLine1Address());
                cardHolder.setCardHolderLine2Address(procurementCardDefault.getCardHolderLine2Address());
                cardHolder.setCardHolderName(procurementCardDefault.getCardHolderName());
                cardHolder.setCardHolderStateCode(procurementCardDefault.getCardHolderStateCode());
                cardHolder.setCardHolderWorkPhoneNumber(procurementCardDefault.getCardHolderWorkPhoneNumber());
                cardHolder.setCardHolderZipCode(procurementCardDefault.getCardHolderZipCode());
                cardHolder.setCardLimit(procurementCardDefault.getCardLimit());
                cardHolder.setCardNoteText(procurementCardDefault.getCardNoteText());
                cardHolder.setCardStatusCode(procurementCardDefault.getCardStatusCode());
            }
                if (getParameterService().getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME)) {
                    cardHolder.setChartOfAccountsCode(procurementCardDefault.getChartOfAccountsCode());
                    cardHolder.setAccountNumber(procurementCardDefault.getAccountNumber());
                    cardHolder.setSubAccountNumber(procurementCardDefault.getSubAccountNumber());
                }
            }

        if (StringUtils.isEmpty(cardHolder.getAccountNumber())) {
            cardHolder.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
            cardHolder.setAccountNumber(transaction.getAccountNumber());
            cardHolder.setSubAccountNumber(transaction.getSubAccountNumber());
        }
        if (StringUtils.isEmpty(cardHolder.getCardHolderName())) {
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
        }

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
    protected String createTransactionDetailRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, Integer transactionLineNumber) {
        ProcurementCardTransactionDetail transactionDetail = new ProcurementCardTransactionDetail();

        // set the document transaction detail fields from the loaded transaction record
        transactionDetail.setDocumentNumber(pcardDocument.getDocumentNumber());
        transactionDetail.setFinancialDocumentTransactionLineNumber(transactionLineNumber);
        transactionDetail.setTransactionDate(transaction.getTransactionDate());
        transactionDetail.setTransactionReferenceNumber(new Long(transaction.getTransactionReferenceNumber()).toString());        
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
        
        createTransactionLevel3AddRecord(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3ItemsRecords(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3AddUserRecord(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3FuelRecord(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3GenericRecord(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3LodgingRecord(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3NonFuelRecords(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3RentalRecord(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3ShipSvcRecords(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3TransportRecord(pcardDocument, transaction, transactionDetail);
        createTransactionLevel3TransLegRecords(pcardDocument, transaction, transactionDetail);

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
    protected void createTransactionVendorRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
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
     * Creates a transaction level 3 addendum record and adds it to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 addendum record on.
     */
    protected void createTransactionLevel3AddRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        if (ObjectUtils.isNotNull(transaction.getOrderDate())) {
            ProcurementCardLevel3Add procurementCardLevel3Add = new ProcurementCardLevel3Add();
    
            procurementCardLevel3Add.setDocumentNumber(pcardDocument.getDocumentNumber());
            procurementCardLevel3Add.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
            procurementCardLevel3Add.setInvoiceNumber(transaction.getInvoiceNumber());
            procurementCardLevel3Add.setOrderDate(transaction.getOrderDate());
            procurementCardLevel3Add.setPurchaseTime(transaction.getPurchaseTime());
            procurementCardLevel3Add.setShipPostal(transaction.getShipPostal());
            procurementCardLevel3Add.setDestinationPostal(transaction.getDestinationPostal());
            procurementCardLevel3Add.setDestinationCountryCode(transaction.getDestinationCountryCode());
            procurementCardLevel3Add.setTaxAmount(transaction.getTaxAmount());
            procurementCardLevel3Add.setTaxRate(transaction.getTaxRate());
            procurementCardLevel3Add.setDiscountAmount(transaction.getDiscountAmount());
            procurementCardLevel3Add.setFreightAmount(transaction.getFreightAmount());
            procurementCardLevel3Add.setDutyAmount(transaction.getDutyAmount());
            
            transactionDetail.setProcurementCardLevel3Add(procurementCardLevel3Add);
        }
    }
    
    /**
     * Creates transaction level 3 item records and adds them to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 item records on.
     */
    protected void createTransactionLevel3ItemsRecords(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
    	List<ProcurementCardTranAddItem> procurementCardTranAddItems = new ArrayList<ProcurementCardTranAddItem>();
        List<ProcurementCardLevel3AddItem> procurementCardLevel3AddItems = new ArrayList<ProcurementCardLevel3AddItem>();
        ProcurementCardLevel3AddItem procurementCardLevel3AddItem;
        int sequenceNumber = 1;
        
        procurementCardTranAddItems = (List<ProcurementCardTranAddItem>) businessObjectService.findAll(ProcurementCardTranAddItem.class);
        
        for (ProcurementCardTranAddItem procurementCardTranAddItem: procurementCardTranAddItems) {
            if (procurementCardTranAddItem.getTransactionSequenceRowNumber().intValue() == transaction.getTransactionSequenceRowNumber().intValue()) {
                procurementCardLevel3AddItem = new ProcurementCardLevel3AddItem();
                procurementCardLevel3AddItem.setDocumentNumber(pcardDocument.getDocumentNumber());
                procurementCardLevel3AddItem.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
                procurementCardLevel3AddItem.setSequenceNumber(sequenceNumber);
                procurementCardLevel3AddItem.setItemCommodityCode(procurementCardTranAddItem.getItemCommodityCode());
                procurementCardLevel3AddItem.setItemProductCode(procurementCardTranAddItem.getItemProductCode());
                procurementCardLevel3AddItem.setItemDescription(procurementCardTranAddItem.getItemDescription());
                procurementCardLevel3AddItem.setItemQuantity(procurementCardTranAddItem.getItemQuantity());
                procurementCardLevel3AddItem.setItemUnitCode(procurementCardTranAddItem.getItemUnitCode());
                procurementCardLevel3AddItem.setItemAmount(procurementCardTranAddItem.getItemAmount());
                procurementCardLevel3AddItem.setItemDebitCreditCode(procurementCardTranAddItem.getItemDebitCreditCode());
                procurementCardLevel3AddItem.setItemTaxRate(procurementCardTranAddItem.getItemTaxRate());
                procurementCardLevel3AddItem.setItemTaxAmount(procurementCardTranAddItem.getItemTaxAmount());
                procurementCardLevel3AddItem.setItemDiscountAmount(procurementCardTranAddItem.getItemDiscountAmount());
                procurementCardLevel3AddItem.setItemExtendedAmount(procurementCardTranAddItem.getItemExtendedAmount());
                procurementCardLevel3AddItems.add(procurementCardLevel3AddItem);     
                sequenceNumber++;
            }            
        }
        if (ObjectUtils.isNotNull(procurementCardLevel3AddItems)) {
            transactionDetail.setProcurementCardLevel3AddItems(procurementCardLevel3AddItems);
        }
    }
    
    /**
     * Creates a transaction level 3 addendum user record and adds it to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 addendum user record on.
     */
    protected void createTransactionLevel3AddUserRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        if (ObjectUtils.isNotNull(transaction.getUserEffectiveDate())) {        
            ProcurementCardLevel3AddUser procurementCardLevel3AddUser = new ProcurementCardLevel3AddUser();
    
            procurementCardLevel3AddUser.setDocumentNumber(pcardDocument.getDocumentNumber());
            procurementCardLevel3AddUser.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
            procurementCardLevel3AddUser.setUserEffectiveDate(transaction.getUserEffectiveDate());
            procurementCardLevel3AddUser.setUserAmount(transaction.getUserAmount());
            
            transactionDetail.setProcurementCardLevel3AddUser(procurementCardLevel3AddUser);
        }
    }
    
    /**
     * Creates a transaction level 3 fuel record and adds it to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 fuel record on.
     */
    protected void createTransactionLevel3FuelRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        if (ObjectUtils.isNotNull(transaction.getFuelSaleAmount())) {    
            ProcurementCardLevel3Fuel procurementCardLevel3Fuel = new ProcurementCardLevel3Fuel();
    
            procurementCardLevel3Fuel.setDocumentNumber(pcardDocument.getDocumentNumber());
            procurementCardLevel3Fuel.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
            procurementCardLevel3Fuel.setOilBrandName(transaction.getOilBrandName());
            procurementCardLevel3Fuel.setOdometerReading(transaction.getOdometerReading());
            procurementCardLevel3Fuel.setFleetId(transaction.getFleetId());
            procurementCardLevel3Fuel.setMessageId(transaction.getMessageId());
            procurementCardLevel3Fuel.setUsage(transaction.getUsage());
            procurementCardLevel3Fuel.setFuelServiceType(transaction.getFuelServiceType());
            procurementCardLevel3Fuel.setFuelProductCd(transaction.getFuelProductCd());
            procurementCardLevel3Fuel.setProductTypeCd(transaction.getProductTypeCd());
            procurementCardLevel3Fuel.setFuelQuantity(transaction.getFuelQuantity());
            procurementCardLevel3Fuel.setFuelUnitOfMeasure(transaction.getFuelUnitOfMeasure());
            procurementCardLevel3Fuel.setFuelUnitPrice(transaction.getFuelUnitPrice());
            procurementCardLevel3Fuel.setFuelSaleAmount(transaction.getFuelSaleAmount());
            procurementCardLevel3Fuel.setFuelDiscountAmount(transaction.getFuelDiscountAmount());
            procurementCardLevel3Fuel.setTaxAmount1(transaction.getTaxAmount1());
            procurementCardLevel3Fuel.setTaxAmount2(transaction.getTaxAmount2());
            procurementCardLevel3Fuel.setTotalAmount(transaction.getTotalAmount());
            
            transactionDetail.setProcurementCardLevel3Fuel(procurementCardLevel3Fuel);
        }
    }
    
    /**
     * Creates a transaction level 3 generic record and adds it to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 generic user record on.
     */
    protected void createTransactionLevel3GenericRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        if (ObjectUtils.isNotNull(transaction.getGenericEffectiveDate())) {    
            ProcurementCardLevel3Generic procurementCardLevel3Generic = new ProcurementCardLevel3Generic();
    
            procurementCardLevel3Generic.setDocumentNumber(pcardDocument.getDocumentNumber());
            procurementCardLevel3Generic.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
            procurementCardLevel3Generic.setGenericEffectiveDate(transaction.getGenericEffectiveDate());
            procurementCardLevel3Generic.setGenericData(transaction.getGenericData());
            
            transactionDetail.setProcurementCardLevel3Generic(procurementCardLevel3Generic);
        }
    }
    
    /**
     * Creates a transaction level 3 lodging record and adds it to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 lodging record on.
     */
    protected void createTransactionLevel3LodgingRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        if (ObjectUtils.isNotNull(transaction.getArriveDate())) {    
            ProcurementCardLevel3Lodging procurementCardLevel3Lodging = new ProcurementCardLevel3Lodging();
    
            procurementCardLevel3Lodging.setDocumentNumber(pcardDocument.getDocumentNumber());
            procurementCardLevel3Lodging.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
            procurementCardLevel3Lodging.setArriveDate(transaction.getArriveDate());
            procurementCardLevel3Lodging.setDepartureDate(transaction.getDepartureDate());
            procurementCardLevel3Lodging.setFolioNum(transaction.getFolioNum());
            procurementCardLevel3Lodging.setPropertyPhoneNum(transaction.getPropertyPhoneNum());
            procurementCardLevel3Lodging.setCustomerServiceNum(transaction.getCustomerServiceNum());
            procurementCardLevel3Lodging.setPrePaidAmt(transaction.getPrePaidAmt());
            procurementCardLevel3Lodging.setRoomRate(transaction.getRoomRate());
            procurementCardLevel3Lodging.setRoomTax(transaction.getRoomTax());
            procurementCardLevel3Lodging.setProgramCode(transaction.getProgramCode());
            procurementCardLevel3Lodging.setCallCharges(transaction.getCallCharges());
            procurementCardLevel3Lodging.setFoodSvcCharges(transaction.getFoodSvcCharges());
            procurementCardLevel3Lodging.setMiniBarCharges(transaction.getMiniBarCharges());
            procurementCardLevel3Lodging.setGiftShopCharges(transaction.getGiftShopCharges());
            procurementCardLevel3Lodging.setLaundryCharges(transaction.getLaundryCharges());
            procurementCardLevel3Lodging.setHealthClubCharges(transaction.getHealthClubCharges());
            procurementCardLevel3Lodging.setMovieCharges(transaction.getMovieCharges());
            procurementCardLevel3Lodging.setBusCtrCharges(transaction.getBusCtrCharges());
            procurementCardLevel3Lodging.setParkingCharges(transaction.getParkingCharges());
            procurementCardLevel3Lodging.setOtherCode(transaction.getOtherCode());
            procurementCardLevel3Lodging.setOtherCharges(transaction.getOtherCharges());
            procurementCardLevel3Lodging.setAdjustmentAmount(transaction.getAdjustmentAmount());
            
            transactionDetail.setProcurementCardLevel3Lodging(procurementCardLevel3Lodging);
        }
    }
    
    /**
     * Creates transaction level 3 item records and adds them to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 item records on.
     */
    protected void createTransactionLevel3NonFuelRecords(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
    	List<ProcurementCardTranNonFuel> procurementCardTranNonFuels = new ArrayList<ProcurementCardTranNonFuel>();
        List<ProcurementCardLevel3NonFuel> procurementCardLevel3NonFuels = new ArrayList<ProcurementCardLevel3NonFuel>();
        ProcurementCardLevel3NonFuel procurementCardLevel3NonFuel;
        int sequenceNumber = 1;
        
        procurementCardTranNonFuels = (List<ProcurementCardTranNonFuel>) businessObjectService.findAll(ProcurementCardTranNonFuel.class);
        
        for (ProcurementCardTranNonFuel procurementCardTranNonFuel: procurementCardTranNonFuels) {
            if (procurementCardTranNonFuel.getTransactionSequenceRowNumber().intValue() == transaction.getTransactionSequenceRowNumber().intValue()) {
                procurementCardLevel3NonFuel = new ProcurementCardLevel3NonFuel();
                procurementCardLevel3NonFuel.setDocumentNumber(pcardDocument.getDocumentNumber());
                procurementCardLevel3NonFuel.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
                procurementCardLevel3NonFuel.setSequenceNumber(sequenceNumber);
                procurementCardLevel3NonFuel.setNonFuelProductCode(procurementCardTranNonFuel.getNonFuelProductCode());
                procurementCardLevel3NonFuel.setItemDesc(procurementCardTranNonFuel.getItemDesc());
                procurementCardLevel3NonFuel.setItemQuant(procurementCardTranNonFuel.getItemQuant());
                procurementCardLevel3NonFuel.setItemUnitOfMeasure(procurementCardTranNonFuel.getItemUnitOfMeasure());
                procurementCardLevel3NonFuel.setTaxRateApplied(procurementCardTranNonFuel.getTaxRateApplied());
                procurementCardLevel3NonFuel.setItemTaxAmt(procurementCardTranNonFuel.getItemTaxAmt());
                procurementCardLevel3NonFuel.setAlternateTaxId(procurementCardTranNonFuel.getAlternateTaxId());
                procurementCardLevel3NonFuel.setItemDiscountAmt(procurementCardTranNonFuel.getItemDiscountAmt());
                procurementCardLevel3NonFuel.setItemTotal(procurementCardTranNonFuel.getItemTotal());                
                procurementCardLevel3NonFuels.add(procurementCardLevel3NonFuel);     
                sequenceNumber++;
            }            
        }
        if (ObjectUtils.isNotNull(procurementCardLevel3NonFuels)) {
            transactionDetail.setProcurementCardLevel3NonFuels(procurementCardLevel3NonFuels);
        }
    }
    
    /**
     * Creates a transaction level 3 rental record and adds it to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 rental record on.
     */
    protected void createTransactionLevel3RentalRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        if (ObjectUtils.isNotNull(transaction.getRegularCharges())) {  
            ProcurementCardLevel3Rental procurementCardLevel3Rental = new ProcurementCardLevel3Rental();
    
            procurementCardLevel3Rental.setDocumentNumber(pcardDocument.getDocumentNumber());
            procurementCardLevel3Rental.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
            procurementCardLevel3Rental.setCheckOutDate(transaction.getCheckOutDate());
            procurementCardLevel3Rental.setRentalAgreementNum(transaction.getRentalAgreementNum());
            procurementCardLevel3Rental.setRenterName(transaction.getRenterName());
            procurementCardLevel3Rental.setReturnCity(transaction.getReturnCity());
            procurementCardLevel3Rental.setReturnState(transaction.getReturnState());
            procurementCardLevel3Rental.setReturnCountry(transaction.getReturnCountry());
            procurementCardLevel3Rental.setReturnDate(transaction.getReturnDate());
            procurementCardLevel3Rental.setReturnLocation(transaction.getReturnLocation());
            procurementCardLevel3Rental.setCustomerSvcNum(transaction.getCustomerSvcNum());
            procurementCardLevel3Rental.setRentalClass(transaction.getRentalClass());
            procurementCardLevel3Rental.setDailyRate(transaction.getDailyRate());
            procurementCardLevel3Rental.setWeeklyRate(transaction.getWeeklyRate());
            procurementCardLevel3Rental.setRatePerMile(transaction.getRatePerMile());
            procurementCardLevel3Rental.setMaxFreeMiles(transaction.getMaxFreeMiles());
            procurementCardLevel3Rental.setTotalMiles(transaction.getTotalMiles());
            procurementCardLevel3Rental.setOneWayCharges(transaction.getOneWayCharges());
            procurementCardLevel3Rental.setInsuranceCharges(transaction.getInsuranceCharges());
            procurementCardLevel3Rental.setRegularCharges(transaction.getRegularCharges());
            procurementCardLevel3Rental.setTowingCharges(transaction.getTowingCharges());
            procurementCardLevel3Rental.setExtraCharges(transaction.getExtraCharges());
            procurementCardLevel3Rental.setLateReturnFee(transaction.getLateReturnFee());
            procurementCardLevel3Rental.setAdjustCode(transaction.getAdjustCode());
            procurementCardLevel3Rental.setAdjustAmount(transaction.getAdjustAmount());
            procurementCardLevel3Rental.setProgCode(transaction.getProgCode());
            procurementCardLevel3Rental.setPhoneCharges(transaction.getPhoneCharges());
            procurementCardLevel3Rental.setOthrCharges(transaction.getOthrCharges());
            procurementCardLevel3Rental.setTotalTaxAmount(transaction.getTotalTaxAmount());
        
            transactionDetail.setProcurementCardLevel3Rental(procurementCardLevel3Rental);
        }
    }
    
    /**
     * Creates transaction level 3 ship services records and adds them to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 ship services records on.
     */
    protected void createTransactionLevel3ShipSvcRecords(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
    	List<ProcurementCardTranShipSvc> procurementCardTranShipSvcs = new ArrayList<ProcurementCardTranShipSvc>();
        List<ProcurementCardLevel3ShipSvc> procurementCardLevel3ShipSvcs = new ArrayList<ProcurementCardLevel3ShipSvc>();
        ProcurementCardLevel3ShipSvc procurementCardLevel3ShipSvc;
        int sequenceNumber = 1;
        
        procurementCardTranShipSvcs = (List<ProcurementCardTranShipSvc>) businessObjectService.findAll(ProcurementCardTranShipSvc.class);
        
        for (ProcurementCardTranShipSvc procurementCardTranShipSvc: procurementCardTranShipSvcs) {
            if (procurementCardTranShipSvc.getTransactionSequenceRowNumber().intValue() == transaction.getTransactionSequenceRowNumber().intValue()) {
                procurementCardLevel3ShipSvc = new ProcurementCardLevel3ShipSvc();
                procurementCardLevel3ShipSvc.setDocumentNumber(pcardDocument.getDocumentNumber());
                procurementCardLevel3ShipSvc.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
                procurementCardLevel3ShipSvc.setSequenceNumber(sequenceNumber);
                procurementCardLevel3ShipSvc.setCourierName(procurementCardTranShipSvc.getCourierName());
                procurementCardLevel3ShipSvc.setTrackingNumber(procurementCardTranShipSvc.getTrackingNumber());
                procurementCardLevel3ShipSvc.setServiceDescription(procurementCardTranShipSvc.getServiceDescription());
                procurementCardLevel3ShipSvc.setPickupDate(procurementCardTranShipSvc.getPickupDate());
                procurementCardLevel3ShipSvc.setOriginZip(procurementCardTranShipSvc.getOriginZip());
                procurementCardLevel3ShipSvc.setOriginCountryCd(procurementCardTranShipSvc.getOriginCountryCd());
                procurementCardLevel3ShipSvc.setDestinationZip(procurementCardTranShipSvc.getDestinationZip());
                procurementCardLevel3ShipSvc.setDestinationCountryCd(procurementCardTranShipSvc.getDestinationCountryCd());
                procurementCardLevel3ShipSvc.setNetAmount(procurementCardTranShipSvc.getNetAmount());
                procurementCardLevel3ShipSvc.setTaxAmt(procurementCardTranShipSvc.getTaxAmt());
                procurementCardLevel3ShipSvc.setDiscAmt(procurementCardTranShipSvc.getDiscAmt());
                procurementCardLevel3ShipSvc.setNumberOfPackages(procurementCardTranShipSvc.getNumberOfPackages());
                procurementCardLevel3ShipSvc.setWeight(procurementCardTranShipSvc.getWeight());
                procurementCardLevel3ShipSvc.setUnitsOfMeasure(procurementCardTranShipSvc.getUnitsOfMeasure());
                procurementCardLevel3ShipSvc.setSenderName(procurementCardTranShipSvc.getSenderName());
                procurementCardLevel3ShipSvc.setSenderAddress(procurementCardTranShipSvc.getSenderAddress());
                procurementCardLevel3ShipSvc.setReceiverName(procurementCardTranShipSvc.getReceiverName());
                procurementCardLevel3ShipSvc.setReceiverAddress(procurementCardTranShipSvc.getReceiverAddress());
                procurementCardLevel3ShipSvc.setCustomerRefNumber(procurementCardTranShipSvc.getCustomerRefNumber());
                procurementCardLevel3ShipSvcs.add(procurementCardLevel3ShipSvc);     
                sequenceNumber++;
            }            
        }
        if (ObjectUtils.isNotNull(procurementCardLevel3ShipSvcs)) {
            transactionDetail.setProcurementCardLevel3ShipSvcs(procurementCardLevel3ShipSvcs);
        }
    }
    
    /**
     * Creates a transaction level 3 transport record and adds it to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 transport record on.
     */
    protected void createTransactionLevel3TransportRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        if (ObjectUtils.isNotNull(transaction.getPassengerName())) {  
            ProcurementCardLevel3Transport procurementCardLevel3Transport = new ProcurementCardLevel3Transport();
    
            procurementCardLevel3Transport.setDocumentNumber(pcardDocument.getDocumentNumber());
            procurementCardLevel3Transport.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
            procurementCardLevel3Transport.setPassengerName(transaction.getPassengerName());
            procurementCardLevel3Transport.setDepartDate(transaction.getDepartDate());
            procurementCardLevel3Transport.setDepartureCity(transaction.getDepartureCity());
            procurementCardLevel3Transport.setAgencyCode(transaction.getAgencyCode());
            procurementCardLevel3Transport.setAgencyName(transaction.getAgencyName());
            procurementCardLevel3Transport.setTicketNumber(transaction.getTicketNumber());
            procurementCardLevel3Transport.setCustomerCode(transaction.getCustomerCode());
            procurementCardLevel3Transport.setIssueDate(transaction.getIssueDate());
            procurementCardLevel3Transport.setIssuingCarrier(transaction.getIssuingCarrier());
            procurementCardLevel3Transport.setTotalFare(transaction.getTotalFare());
            procurementCardLevel3Transport.setTotalFees(transaction.getTotalFees());
            procurementCardLevel3Transport.setTotalTaxes(transaction.getTotalTaxes());
            
            transactionDetail.setProcurementCardLevel3Transport(procurementCardLevel3Transport);
        }
    }
    
    /**
     * Creates transaction level 3 transport leg records and adds them to the transaction detail.
     * 
     * @param pcardDocument The procurement card document to retrieve values from.
     * @param transaction Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the level 3 transport leg records on.
     */
    protected void createTransactionLevel3TransLegRecords(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
    	List<ProcurementCardTranTransportLeg> procurementCardTranTransportLegs = new ArrayList<ProcurementCardTranTransportLeg>();
        List<ProcurementCardLevel3TransportLeg> procurementCardLevel3TransportLegs = new ArrayList<ProcurementCardLevel3TransportLeg>();
        ProcurementCardLevel3TransportLeg procurementCardLevel3TransportLeg;
        int sequenceNumber = 1;
        
        procurementCardTranTransportLegs = (List<ProcurementCardTranTransportLeg>) businessObjectService.findAll(ProcurementCardTranTransportLeg.class);
        
        for (ProcurementCardTranTransportLeg procurementCardTranTransportLeg: procurementCardTranTransportLegs) {
            if (procurementCardTranTransportLeg.getTransactionSequenceRowNumber().intValue() == transaction.getTransactionSequenceRowNumber().intValue()) {
                procurementCardLevel3TransportLeg = new ProcurementCardLevel3TransportLeg();
                procurementCardLevel3TransportLeg.setDocumentNumber(pcardDocument.getDocumentNumber());
                procurementCardLevel3TransportLeg.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
                procurementCardLevel3TransportLeg.setSequenceNumber(sequenceNumber);
                procurementCardLevel3TransportLeg.setCarrierCode(procurementCardTranTransportLeg.getCarrierCode());
                procurementCardLevel3TransportLeg.setServiceClass(procurementCardTranTransportLeg.getServiceClass());
                procurementCardLevel3TransportLeg.setDepartCity(procurementCardTranTransportLeg.getDepartCity());
                procurementCardLevel3TransportLeg.setConjunctionTicket(procurementCardTranTransportLeg.getConjunctionTicket());
                procurementCardLevel3TransportLeg.setExchangeTicket(procurementCardTranTransportLeg.getExchangeTicket());
                procurementCardLevel3TransportLeg.setDestinationCity(procurementCardTranTransportLeg.getDestinationCity());
                procurementCardLevel3TransportLeg.setFareBasisCode(procurementCardTranTransportLeg.getFareBasisCode());
                procurementCardLevel3TransportLeg.setFlightNumber(procurementCardTranTransportLeg.getFlightNumber());
                procurementCardLevel3TransportLeg.setDepartTime(procurementCardTranTransportLeg.getDepartTime());
                procurementCardLevel3TransportLeg.setDepartTimeSegment(procurementCardTranTransportLeg.getDepartTimeSegment());
                procurementCardLevel3TransportLeg.setArriveTime(procurementCardTranTransportLeg.getArriveTime());
                procurementCardLevel3TransportLeg.setArriveTimeSegment(procurementCardTranTransportLeg.getArriveTimeSegment());
                procurementCardLevel3TransportLeg.setFare(procurementCardTranTransportLeg.getFare());
                procurementCardLevel3TransportLeg.setFees(procurementCardTranTransportLeg.getFees());
                procurementCardLevel3TransportLeg.setTaxes(procurementCardTranTransportLeg.getTaxes());
                procurementCardLevel3TransportLeg.setEndorsements(procurementCardTranTransportLeg.getEndorsements());
                procurementCardLevel3TransportLeg.setControlCode(procurementCardTranTransportLeg.getControlCode());
                procurementCardLevel3TransportLegs.add(procurementCardLevel3TransportLeg);     
                sequenceNumber++;
            }            
        }
        if (ObjectUtils.isNotNull(procurementCardLevel3TransportLegs)) {
            transactionDetail.setProcurementCardLevel3TransportLegs(procurementCardLevel3TransportLegs);
        }
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
    protected String createAndValidateAccountingLines(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        // build source lines
        ProcurementCardSourceAccountingLine sourceLine = createSourceAccountingLine(transaction, docTransactionDetail);
        sourceLine.setPostingYear(pcardDocument.getPostingYear());

        // add line to transaction through document since document contains the next sequence number fields
        pcardDocument.addSourceAccountingLine(sourceLine);

        // build target lines
        ProcurementCardTargetAccountingLine targetLine = createTargetAccountingLine(transaction, docTransactionDetail);
        targetLine.setPostingYear(pcardDocument.getPostingYear());

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
    protected ProcurementCardTargetAccountingLine createTargetAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardTargetAccountingLine targetLine = new ProcurementCardTargetAccountingLine();
        targetLine.setDocumentNumber(docTransactionDetail.getDocumentNumber());
        targetLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        targetLine.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
        targetLine.setAccountNumber(transaction.getAccountNumber());
        targetLine.setFinancialObjectCode(transaction.getFinancialObjectCode());
        targetLine.setSubAccountNumber(transaction.getSubAccountNumber());
        targetLine.setFinancialSubObjectCode(transaction.getFinancialSubObjectCode());
        targetLine.setProjectCode(transaction.getProjectCode());

        if (getParameterService().getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME)) {
            final ProcurementCardDefault procurementCardDefault = retrieveProcurementCardDefault(transaction.getTransactionCreditCardNumber());
            if (procurementCardDefault != null) {
                    targetLine.setChartOfAccountsCode(procurementCardDefault.getChartOfAccountsCode());
                    targetLine.setAccountNumber(procurementCardDefault.getAccountNumber());
                    targetLine.setFinancialObjectCode(procurementCardDefault.getFinancialObjectCode());
                    targetLine.setSubAccountNumber(procurementCardDefault.getSubAccountNumber());
                    targetLine.setFinancialSubObjectCode(procurementCardDefault.getFinancialSubObjectCode());
                    targetLine.setProjectCode(procurementCardDefault.getProjectCode());
            }
        }

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
    protected ProcurementCardSourceAccountingLine createSourceAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
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
    protected String validateTargetAccountingLine(ProcurementCardTargetAccountingLine targetLine) {
        String errorText = "";

        targetLine.refresh();
        final String lineNumber = targetLine.getSequenceNumber() == null ? "new" : targetLine.getSequenceNumber().toString();

        if (!accountingLineRuleUtil.isValidChart("", targetLine.getChart(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " is invalid; using error Chart Code.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setChartOfAccountsCode(getErrorChartCode());
            targetLine.refresh();
        }

        if (!accountingLineRuleUtil.isValidAccount("", targetLine.getAccount(), dataDictionaryService.getDataDictionary()) || targetLine.getAccount().isExpired()) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " is invalid; using error account.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setChartOfAccountsCode(getErrorChartCode());
            targetLine.setAccountNumber(getErrorAccountNumber());
            targetLine.refresh();
        }

        if (!accountingLineRuleUtil.isValidObjectCode("", targetLine.getObjectCode(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " Object Code " + targetLine.getFinancialObjectCode() + " is invalid; using default Object Code.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setFinancialObjectCode(getDefaultObjectCode());
            targetLine.refresh();
        }

        if (StringUtils.isNotBlank(targetLine.getSubAccountNumber()) && !accountingLineRuleUtil.isValidSubAccount("", targetLine.getSubAccount(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " Sub Account " + targetLine.getSubAccountNumber() + " is invalid; Setting Sub Account to blank.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setSubAccountNumber("");
        }

        if (StringUtils.isNotBlank(targetLine.getFinancialSubObjectCode()) && !accountingLineRuleUtil.isValidSubObjectCode("", targetLine.getSubObjectCode(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " Object Code " + targetLine.getFinancialObjectCode() + " Sub Object Code " + targetLine.getFinancialSubObjectCode() + " is invalid; setting Sub Object to blank.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setFinancialSubObjectCode("");
        }

        if (StringUtils.isNotBlank(targetLine.getProjectCode()) && !accountingLineRuleUtil.isValidProjectCode("", targetLine.getProject(), dataDictionaryService.getDataDictionary())) {
            if ( LOG.isInfoEnabled() ) {
                LOG.info("Target Accounting Line "+lineNumber+" Project Code " + targetLine.getProjectCode() + " is invalid; setting to blank.");
            }
            errorText += " Target Accounting Line "+lineNumber+" Project Code " + targetLine.getProjectCode() + " is invalid; setting to blank.";

            targetLine.setProjectCode("");
        }

        // clear out GlobalVariable message map, since we have taken care of the errors
        GlobalVariables.setMessageMap(new MessageMap());

        return errorText;
    }

    /**
     * Validates the chart of account attributes for existence and active indicator. Will substitute for defined
     * default parameters or set fields to empty that if they have errors.
     *
     * @param transaction The transaction to be validated.
     * @return String with error messages discovered during validation.  An empty string indicates no validation errors were found.
     */
    protected String validateTransaction(ProcurementCardTransaction transaction) {
        String errorText = "";
        final String lineNumber = transaction.getTransactionSequenceRowNumber() == null ? "new" : transaction.getTransactionSequenceRowNumber().toString();

        if (getParameterService().getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME)) {
            final ProcurementCardDefault procurementCardDefault = retrieveProcurementCardDefault(transaction.getTransactionCreditCardNumber());
            if (ObjectUtils.isNull(procurementCardDefault)) {
                final String tempErrorText = "Procurement Card Accounting Line Defaults are turned on but no Procurement Card Default record could be retrieved for transaction: "+transaction.getTransactionReferenceNumber() + " by card number.";
                if ( LOG.isInfoEnabled() ) {
                    LOG.info(tempErrorText);
                }
                errorText += " " + tempErrorText;
            }
        }
        else {
            transaction.refresh();

            final ChartService chartService = SpringContext.getBean(ChartService.class);
            if (transaction.getChartOfAccountsCode() == null || ObjectUtils.isNull(chartService.getByPrimaryId(transaction.getChartOfAccountsCode()))) {
                String tempErrorText = "Transaction "+lineNumber+" Chart " + transaction.getChartOfAccountsCode() + " is invalid; using error Chart Code.";
                if ( LOG.isInfoEnabled() ) {
                    LOG.info(tempErrorText);
                }
                errorText += " " + tempErrorText;
                transaction.setChartOfAccountsCode(getErrorChartCode());
                transaction.refresh();
            }

            final AccountService accountService = SpringContext.getBean(AccountService.class);
            if (transaction.getAccountNumber() == null || ObjectUtils.isNull(accountService.getByPrimaryIdWithCaching(transaction.getChartOfAccountsCode(), transaction.getAccountNumber())) || accountService.getByPrimaryIdWithCaching(transaction.getChartOfAccountsCode(), transaction.getAccountNumber()).isExpired()) {
                String tempErrorText = "Transaction "+lineNumber+" Chart " + transaction.getChartOfAccountsCode() + " Account " + transaction.getAccountNumber() + " is invalid; using error account.";
                if ( LOG.isInfoEnabled() ) {
                    LOG.info(tempErrorText);
                }
                errorText += " " + tempErrorText;
                transaction.setChartOfAccountsCode(getErrorChartCode());
                transaction.setAccountNumber(getErrorAccountNumber());
                transaction.refresh();
            }

            final UniversityDateService uds = SpringContext.getBean(UniversityDateService.class);
            final ObjectCodeService ocs = SpringContext.getBean(ObjectCodeService.class);
            if (transaction.getFinancialObjectCode() == null || ObjectUtils.isNull(ocs.getByPrimaryIdWithCaching(uds.getCurrentFiscalYear(), transaction.getChartOfAccountsCode(), transaction.getFinancialObjectCode()))) {
                String tempErrorText = "Transaction "+lineNumber+" Chart " + transaction.getChartOfAccountsCode() + " Object Code " + transaction.getFinancialObjectCode() + " is invalid; using default Object Code.";
                if ( LOG.isInfoEnabled() ) {
                    LOG.info(tempErrorText);
                }
                errorText += " " + tempErrorText;

                transaction.setFinancialObjectCode(getDefaultObjectCode());
                transaction.refresh();
            }

            if (StringUtils.isNotBlank(transaction.getSubAccountNumber())) {
                SubAccountService sas = SpringContext.getBean(SubAccountService.class);
                SubAccount subAccount = sas.getByPrimaryIdWithCaching(transaction.getChartOfAccountsCode(), transaction.getAccountNumber(), transaction.getSubAccountNumber());

                if (ObjectUtils.isNull(subAccount)) {
                    String tempErrorText = "Transaction "+lineNumber+" Chart " + transaction.getChartOfAccountsCode() + " Account " + transaction.getAccountNumber() + " Sub Account " + transaction.getSubAccountNumber() + " is invalid; Setting Sub Account to blank.";
                    if ( LOG.isInfoEnabled() ) {
                        LOG.info(tempErrorText);
                    }
                    errorText += " " + tempErrorText;

                    transaction.setSubAccountNumber("");
                }
            }

            if (StringUtils.isNotBlank(transaction.getFinancialSubObjectCode())) {

                SubObjectCodeService socs = SpringContext.getBean(SubObjectCodeService.class);
                SubObjectCode soc = socs.getByPrimaryIdForCurrentYear(transaction.getChartOfAccountsCode(), transaction.getAccountNumber(), transaction.getFinancialObjectCode(), transaction.getFinancialSubObjectCode());

                if (ObjectUtils.isNull(soc)) {
                    String tempErrorText = "Transaction "+lineNumber+" Chart " + transaction.getChartOfAccountsCode() + " Account " + transaction.getAccountNumber() + " Object Code " + transaction.getFinancialObjectCode() + " Sub Object Code " + transaction.getFinancialSubObjectCode() + " is invalid; setting Sub Object to blank.";
                    if ( LOG.isInfoEnabled() ) {
                        LOG.info(tempErrorText);
                    }
                    errorText += " " + tempErrorText;

                    transaction.setFinancialSubObjectCode("");
                }
            }

            if (StringUtils.isNotBlank(transaction.getProjectCode())) {

                ProjectCodeService pcs = SpringContext.getBean(ProjectCodeService.class);
                ProjectCode pc = pcs.getByPrimaryId(transaction.getProjectCode());

                if (ObjectUtils.isNull(pc)) {
                    if ( LOG.isInfoEnabled() ) {
                        LOG.info("Transaction "+lineNumber+" Project Code " + transaction.getProjectCode() + " is invalid; setting to blank.");
                    }
                    errorText += " Transaction "+lineNumber+" Project Code " + transaction.getProjectCode() + " is invalid; setting to blank.";

                    transaction.setProjectCode("");
                }
            }
        }

        // clear out GlobalVariable message map, since we have taken care of the errors
        GlobalVariables.setMessageMap(new MessageMap());

        return errorText;
    }

    /**
     * Retrieves the error chart code from the parameter table.
     * @return The error chart code defined in the parameter table.
     */
    protected String getErrorChartCode() {
        return parameterService.getParameterValueAsString(ProcurementCardCreateDocumentsStep.class, ProcurementCardDocumentRuleConstants.ERROR_TRANS_CHART_CODE_PARM_NM);
    }

    /**
     * Retrieves the error account number from the parameter table.
     * @return The error account number defined in the parameter table.
     */
    protected String getErrorAccountNumber() {
        return parameterService.getParameterValueAsString(ProcurementCardCreateDocumentsStep.class, ERROR_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * Gets the default Chart Code, Account from the custom Procurement Cardholder table.
     *
     */
    protected ProcurementCardDefault retrieveProcurementCardDefault(String creditCardNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.CREDIT_CARD_NUMBER, creditCardNumber);
        List<ProcurementCardDefault> matchingPcardDefaults = (List<ProcurementCardDefault>)businessObjectService.findMatching(ProcurementCardDefault.class, fieldValues);
        ProcurementCardDefault procurementCardDefault = null;
        if ( !matchingPcardDefaults.isEmpty() ) {
            procurementCardDefault = matchingPcardDefaults.get(0);
        }

        return procurementCardDefault;
    }

    /**
     * Retrieves the default chard code from the parameter table.
     * @return The default chart code defined in the parameter table.
     */
    protected String getDefaultChartCode() {
        return parameterService.getParameterValueAsString(ProcurementCardLoadStep.class, DEFAULT_TRANS_CHART_CODE_PARM_NM);
    }

    /**
     * Retrieves the default account number from the parameter table.
     * @return The default account number defined in the parameter table.
     */
    protected String getDefaultAccountNumber() {
        return parameterService.getParameterValueAsString(ProcurementCardLoadStep.class, DEFAULT_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * Retrieves the default object code from the parameter table.
     * @return The default object code defined in the parameter table.
     */
    protected String getDefaultObjectCode() {
        return parameterService.getParameterValueAsString(ProcurementCardLoadStep.class, DEFAULT_TRANS_OBJECT_CODE_PARM_NM);
    }

    /**
     * Calls businessObjectService to remove all the procurement card transaction rows from the transaction load table.
     */
    protected void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ProcurementCardTransaction.class, new HashMap());
    }

    /**
     * Loads all the parsed XML transactions into the temp transaction table.
     *
     * @param transactions List of ProcurementCardTransactions to load.
     */
    protected void loadTransactions(List transactions) {
        businessObjectService.save(transactions);
    }

    /**
     * @return retrieves the presumably injected implementation of ParameterService to use
     */
    public ParameterService getParameterService() {
        return parameterService;
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

    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
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
     * Sets the accountingLineRuleUtil attribute value.
     * @param accountingLineRuleUtil The accountingLineRuleUtil to set.
     */
    public void setAccountingLineRuleUtil(AccountingLineRuleHelperService accountingLineRuleUtil) {
        this.accountingLineRuleUtil = accountingLineRuleUtil;
    }

    /**
     * Sets the capitalAssetBuilderModuleService attribute value.
     * @param capitalAssetBuilderModuleService The capitalAssetBuilderModuleService to set.
     */
    public void setCapitalAssetBuilderModuleService(CapitalAssetBuilderModuleService capitalAssetBuilderModuleService) {
        this.capitalAssetBuilderModuleService = capitalAssetBuilderModuleService;
    }

    /**
     * Sets the procurementCardCreateEmailService attribute.
     *
     * @param procurementCardCreateEmailService The procurementCardCreateEmailService to set.
     */
    public void setProcurementCardCreateEmailService(VelocityEmailService procurementCardCreateEmailService) {
        this.procurementCardCreateEmailService = procurementCardCreateEmailService;
    }

    /**
     * Gets the financialSystemDocumentHeaderSerivce attribute
     *
     * @return
     */
    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    /**
     * Spring hook to inject financialSystemDocumentHeaderService
     *
     * @param financialSystemDocumentService
     */
    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public boolean rerouteProcurementCardDocuments(){
    	throw new UnsupportedOperationException();    	
    }
}
