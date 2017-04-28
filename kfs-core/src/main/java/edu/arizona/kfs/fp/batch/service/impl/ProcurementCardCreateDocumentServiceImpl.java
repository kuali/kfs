package edu.arizona.kfs.fp.batch.service.impl;

import static org.kuali.kfs.sys.KFSConstants.FinancialDocumentTypeCodes.PROCUREMENT_CARD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.batch.ProcurementCardAutoApproveDocumentsStep;
import org.kuali.kfs.fp.batch.ProcurementCardCreateDocumentsStep;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.document.DocumentRefreshQueue;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AutoPopulatingList;

import edu.arizona.kfs.fp.batch.ProcurementCardRerouteDocumentsStep;
import edu.arizona.kfs.fp.businessobject.ProcurementCardDefault;
import edu.arizona.kfs.fp.businessobject.ProcurementCardHolder;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTransaction;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import edu.arizona.kfs.fp.document.ProcurementCardDocument;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;

public class ProcurementCardCreateDocumentServiceImpl extends org.kuali.kfs.fp.batch.service.impl.ProcurementCardCreateDocumentServiceImpl implements edu.arizona.kfs.fp.batch.service.ProcurementCardCreateDocumentService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentServiceImpl.class);

    private GroupService procurementCardGroupService;
    private ConfigurationService configurationService;
    private String batchFileOutputDirectoryName;

    private static final int CARDHOLDER_NAME_MAX_LENGTH = 24;
    private static final String AUTO_APPROVE_ERROR_LOG_SEPARATOR = "-----------------\n";
    
    public GroupService getProcurementCardGroupService() {
        return procurementCardGroupService;
    }

    public void setProcurementCardGroupService(GroupService procurementCardGroupService) {
        this.procurementCardGroupService = procurementCardGroupService;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected String getBatchFileOutputDirectoryName() {
        return batchFileOutputDirectoryName;
    }

    public void setBatchFileOutputDirectoryName(String batchFileOutputDirectoryName) {
        this.batchFileOutputDirectoryName = batchFileOutputDirectoryName;
    }

    /**
     * This method looks for ProcurementCardDocuments that are in route status at the "AccountFullEdit" route node and routed to the error account FO. 
     * Then checks for a valid reconciler and if found, reroutes the document back to the Reconciler.
     * 
     * 
     * @return True if the routing was performed successfully.  A runtime exception will be thrown if any errors occur while routing.
     * 
     * @see org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService#rerouteProcurementCardDocuments(java.util.List)
     */
    @Transactional
    @Override
    public boolean rerouteProcurementCardDocuments() {
        
        // check for reconciler group rerouting
        boolean groupRerouting = parameterService.getParameterValueAsBoolean(ProcurementCardRerouteDocumentsStep.class, KFSParameterKeyConstants.ProcurementCardParameterConstants.REROUTE_PCDO_DOCUMENTS_IND_PARM_NM);
        
        Collection<ProcurementCardDocument> documents = null;
        documents = retrieveUAProcurementCardDocumentsToRoute(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);
        
        LOG.info("PCards to Reroute: " + documents.size());

        for (ProcurementCardDocument pcardDocument : documents) {
        
            String pcardDocumentId = pcardDocument.getDocumentNumber();
            //get document route node
            List<RouteNodeInstance> routeNodeInstances = pcardDocument.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeInstances();
            String node = routeNodeInstances.get(0).getName();
            //get Procurement Card Default information
            ProcurementCardDefault procurementCardHolderDetail = getProcurementCardDefault(pcardDocument.getProcurementCardHolder().getTransactionCreditCardNumber());
            boolean hasReconciler = hasReconciler(procurementCardHolderDetail);
            if (hasReconciler) {   
                //step one, reroute documents enroute to the error account Fiscal officer
                if (node.equals("AccountFullEdit") && 
                        pcardDocument.getTargetAccountingLine(0).getAccountNumber().equals(getErrorAccountNumber())) {
                    pcardDocument.getTargetAccountingLine(0).setChartOfAccountsCode(StringUtils.isNotEmpty(procurementCardHolderDetail.getChartOfAccountsCode()) ? procurementCardHolderDetail.getChartOfAccountsCode() : getErrorChartCode());
                    pcardDocument.getTargetAccountingLine(0).setAccountNumber(StringUtils.isNotEmpty(procurementCardHolderDetail.getAccountNumber()) ? procurementCardHolderDetail.getAccountNumber() : getErrorAccountNumber());
                    pcardDocument.getTargetAccountingLine(0).setFinancialObjectCode(StringUtils.isNotEmpty(procurementCardHolderDetail.getFinancialObjectCode()) ? procurementCardHolderDetail.getFinancialObjectCode() : getDefaultObjectCode());
                    pcardDocument.getTargetAccountingLine(0).setSubAccountNumber(procurementCardHolderDetail.getSubAccountNumber());
                    pcardDocument.getTargetAccountingLine(0).setFinancialSubObjectCode(procurementCardHolderDetail.getFinancialSubObjectCode());
                    LOG.info("Rerouting doc #" + pcardDocumentId + " at AccountFullEdit for error account Fiscal Officer.");             
                    requeueDocument(pcardDocument, node, "HasReconciler", " Batch Reroute to Reconciler");
                    // pause between reroutes
                    try {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e) {
                        LOG.error("Thread interrupted in rerouteProcurementCardDocuments method " + e.getMessage());
                    }
                }
                
              //step two, reroute documents enroute to the reconciler group(s) that had group membership problems
                else if (node.equals("ProcurementCardReconciler") && groupRerouting && 
                        parameterService.getParameter(ProcurementCardRerouteDocumentsStep.class,
                                KFSParameterKeyConstants.ProcurementCardParameterConstants.RECONCILER_GROUPS_TO_REROUTE_PARM_NM).getValue() != null
                        && parameterService
                                .getParameter(ProcurementCardRerouteDocumentsStep.class,
                                        KFSParameterKeyConstants.ProcurementCardParameterConstants.RECONCILER_GROUPS_TO_REROUTE_PARM_NM)
                                .getValue().contains(procurementCardHolderDetail.getReconcilerGroupId())) {
                    LOG.info("Rerouting doc #" + pcardDocumentId + " at ProcurementCardReconciler, in "
                            + KFSParameterKeyConstants.ProcurementCardParameterConstants.RECONCILER_GROUPS_TO_REROUTE_PARM_NM + " group "
                            + procurementCardHolderDetail.getReconcilerGroupId());
                    requeueDocument(pcardDocument, node, "ProcurementCardReconciler", " Batch Reroute to Reconciler");
                    // pause between reroutes
                    try {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                
            }                            
        }

        return true;
    }
    
    /**
     * Gets the default Chart Code, Account from the custom Procurement Cardholder table.
     * 
     */
    private ProcurementCardDefault getProcurementCardDefault(String creditCardNumber) {
                
        Map<String, String> pkMap = new HashMap<String, String>();
        pkMap.put("creditCardNumber", creditCardNumber);
        ProcurementCardDefault procurementCardDefault = (ProcurementCardDefault) getBusinessObjectService().findByPrimaryKey(ProcurementCardDefault.class, pkMap);
                
        return procurementCardDefault;
    }
    
    @Override
    public void requeueDocument(ProcurementCardDocument pcardDocument, String node, String prevNode, String annotation) {
        //pcardDocumentId could have leading 0's; want these removed for processing calls
        String documentId = StringUtils.stripStart(pcardDocument.getDocumentNumber(), "0");
        WorkflowDocument documentActions = pcardDocument.getDocumentHeader().getWorkflowDocument();
        String systemUserPrincipalId = getSystemUserPrincipalId();
        documentActions.switchPrincipal(systemUserPrincipalId);
        ReturnPoint returnPoint = ReturnPoint.create(prevNode);
        documentActions.superUserReturnToPreviousNode(returnPoint, node + annotation);
        // Use requeuer service to put the document back into action list of reconciler
        DocumentRefreshQueue docRequeue = KewApiServiceLocator.getDocumentRequeuerService(KFSConstants.APPLICATION_NAMESPACE_CODE, documentId, 0x0);
        docRequeue.refreshDocument(documentId);
       
        LOG.info("Rerouting PCDO document # " + documentId + ".");
    }
    
    /*
     * as the edu.arizona ProcurementCardDocument implentation has additional fields, we must duplicate this method from the extended class and use the edu.arizona.ProcurementCardDocument implementation(non-Javadoc)
     * @see org.kuali.kfs.fp.batch.service.impl.ProcurementCardCreateDocumentServiceImpl#createProcurementCardDocuments()
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Transactional
    public boolean createProcurementCardDocuments() {
        List documents = new ArrayList();
        List cardTransactions = retrieveTransactions();

        // iterate through card transaction list and create documents
        for (Iterator iter = cardTransactions.iterator(); iter.hasNext();) {
            documents.add(this.createProcurementCardDocument((List) iter.next()));
        }

        // now store all the documents
        for (Iterator iter = documents.iterator(); iter.hasNext();) {
            ProcurementCardDocument pcardDocument = (ProcurementCardDocument) iter.next();
            try {
                getDocumentService().saveDocument(pcardDocument, DocumentSystemSaveEvent.class);
                LOG.info("Saved Procurement Card document: "+pcardDocument.getDocumentNumber());
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
     * Creates a ProcurementCardDocument from the List of transactions given.
     *
     * @param transactions List of ProcurementCardTransaction objects to be used for creating the document.
     * @return A ProcurementCardDocument populated with the transactions provided.
     */
    //copy-pasted from super class to use correct ProcurementCardDocument type (edu.arizona instead of org.kuali)
    @SuppressWarnings("rawtypes")
    protected ProcurementCardDocument createProcurementCardDocument( List transactions) {
        ProcurementCardDocument pcardDocument = null;

        try {
            // get new document from doc service
            pcardDocument = (ProcurementCardDocument) getDocumentService().getNewDocument(PROCUREMENT_CARD);

            List<CapitalAssetInformation> capitalAssets = pcardDocument.getCapitalAssetInformation();
            for (CapitalAssetInformation capitalAsset : capitalAssets) {
                if (ObjectUtils.isNotNull(capitalAsset) && ObjectUtils.isNotNull(capitalAsset.getCapitalAssetInformationDetails())) {
                    capitalAsset.setDocumentNumber(pcardDocument.getDocumentNumber());
                }
            }

            ProcurementCardTransaction trans = (ProcurementCardTransaction) transactions.get(0);
            validateTransaction(trans);
            createCardHolderRecord(pcardDocument, trans);

            // for each transaction, create transaction detail object and then acct lines for the detail
            int transactionLineNumber = 1;
            KualiDecimal documentTotalAmount = KualiDecimal.ZERO;
            String transactionIssuesSummary = "";
            Integer documentExplanationMaxLength = getDataDictionaryService().getAttributeMaxLength(DocumentHeader.class.getName(), KFSPropertyConstants.EXPLANATION);
            for (Iterator iter = transactions.iterator(); iter.hasNext();) {
                ProcurementCardTransaction transaction = (ProcurementCardTransaction) iter.next();

                // create transaction detail record with accounting lines
                String transactionSummary = createTransactionDetailRecord(pcardDocument, transaction, transactionLineNumber);
                if(!transactionIssuesSummary.contains(transactionSummary)){
                    transactionIssuesSummary = transactionIssuesSummary.concat(transactionSummary);
                }

                // update document total
                documentTotalAmount = documentTotalAmount.add(transaction.getFinancialDocumentTotalAmount());

                transactionLineNumber++;
            }

            pcardDocument.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(documentTotalAmount);

            // In case errorText is still too long, truncate it and indicate so.
            if (documentExplanationMaxLength != null && transactionIssuesSummary.length() > documentExplanationMaxLength.intValue()) {
                String truncatedMessage = " ... TRUNCATED.";
                transactionIssuesSummary = transactionIssuesSummary.substring(0, documentExplanationMaxLength - truncatedMessage.length()) + truncatedMessage;
            }
            pcardDocument.getDocumentHeader().setExplanation(transactionIssuesSummary);
        }
        catch (WorkflowException e) {
            LOG.error("Error creating pcdo documents: " + e.getMessage(),e);
            throw new RuntimeException("Error creating pcdo documents: " + e.getMessage(),e);
        }

        return pcardDocument;
    }
    
    /**
     * Creates card holder record and sets that record to the document given.
     *
     * @param pcardDocument Procurement card document to place the record in.
     * @param transaction The transaction to set the card holder record fields from.
     */
    //copy-pasted from super class to use correct object types (edu.arizona instead of org.kuali)
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
     * Gets the default Chart Code, Account from the custom Procurement Cardholder table.
     *
     */
    //copy-pasted from super class to use correct object types (edu.arizona instead of org.kuali)
    protected ProcurementCardDefault retrieveProcurementCardDefault(String creditCardNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.CREDIT_CARD_NUMBER, creditCardNumber);
        List<ProcurementCardDefault> matchingPcardDefaults = (List<ProcurementCardDefault>) getBusinessObjectService().findMatching(ProcurementCardDefault.class, fieldValues);
        ProcurementCardDefault procurementCardDefault = null;
        if ( !matchingPcardDefaults.isEmpty() ) {
            procurementCardDefault = matchingPcardDefaults.get(0);
        }

        return procurementCardDefault;
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
        // if auto approve is turned on
        boolean autoApproveOn = parameterService.getParameterValueAsBoolean(ProcurementCardAutoApproveDocumentsStep.class, ProcurementCardDocumentRuleConstants.AUTO_APPROVE_DOCUMENTS_IND);

        if (!autoApproveOn) { // no auto approve? then skip out of here...
            return true;
        }

        StringBuffer autoApproveErrorStr = new StringBuffer();

        // get Parameter information
        Timestamp currentDate = dateTimeService.getCurrentTimestamp();
        int autoApproveNumberDays = Integer.parseInt(parameterService.getParameterValueAsString(ProcurementCardAutoApproveDocumentsStep.class, ProcurementCardDocumentRuleConstants.AUTO_APPROVE_NUMBER_OF_DAYS));
        Collection<ProcurementCardDocument> pcardDocumentList = retrieveUAProcurementCardDocumentsToRoute(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);
        Collection<String> accountValues = parameterService.getParameterValuesAsString(ProcurementCardAutoApproveDocumentsStep.class, KFSParameterKeyConstants.ProcurementCardParameterConstants.SKIP_AUTO_APPROVE_PARM_NM);
        Collection<String> objectSubTypeValues = parameterService.getParameterValuesAsString(ProcurementCardAutoApproveDocumentsStep.class, KFSParameterKeyConstants.ProcurementCardParameterConstants.SKIP_AUTO_APPROVE_SUB_TYPE_PARM_NM);

        for (ProcurementCardDocument pcardDocument : pcardDocumentList) {
            LOG.info("Checking document #" + pcardDocument.getDocumentNumber());
            Timestamp docCreateDate = new Timestamp(pcardDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());

            // If the document has been Enroute long enough and does not have a restricted account or restricted object subtype, then auto approve.
            boolean isEnrouteLongEnough = KfsDateUtils.getDifferenceInDays(docCreateDate, currentDate) > autoApproveNumberDays;
            boolean isNotAccountingLineRestricted = !checkAutoApproveAccountingLineRestriction(pcardDocument, accountValues, objectSubTypeValues);
            if (isEnrouteLongEnough && isNotAccountingLineRestricted) {
                try {
                    String documentErrorReason = blanketApproveDocument(pcardDocument, currentDate);
                    if (documentErrorReason.length() > 0) {
                        autoApproveErrorStr.append(documentErrorReason);
                    }
                } catch (WorkflowException e) {
                    // If an individual document has a problem with being auto approved, it should not stop the job from approving the rest.
                    LOG.error("Error auto approving document # " + pcardDocument.getDocumentNumber() + " " + e.getMessage(), e);
                }
            }
        }

        if (autoApproveErrorStr.length() > 0) {
            outputErrorFile(autoApproveErrorStr.toString());
        }

        return true;

    }

    /**
     * Returns true if the document contains an accounting line that is restricted from being auto-approved.
     * 
     * @param pcardDocument
     * @param accountValues
     * @param objectSubTypeValues
     * @return
     */
    private boolean checkAutoApproveAccountingLineRestriction(ProcurementCardDocument pcardDocument, Collection<String> accountValues, Collection<String> objectSubTypeValues) {
        @SuppressWarnings("unchecked")
        List<ProcurementCardTargetAccountingLine> procurementCardTargetAccountingLines = (List<ProcurementCardTargetAccountingLine>) pcardDocument.getTargetAccountingLines();
        for (ProcurementCardTargetAccountingLine procurementCardTargetAccountingLine : procurementCardTargetAccountingLines) {
            if (procurementCardTargetAccountingLine.getAccountNumber() != null && accountValues.contains(procurementCardTargetAccountingLine.getAccountNumber())) {
                return true;
            }

            if (procurementCardTargetAccountingLine.getObjectCode() != null && objectSubTypeValues.contains(procurementCardTargetAccountingLine.getObjectCode().getFinancialObjectSubTypeCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * blanket approve document in a transaction. This method was created so each document
     * is handled in its own transaction. This allows validation errors to be thrown without
     * rolling back the entire process.
     *
     * @param pcardDocument
     * @param accountList
     * @param objectSubTypeList
     * @param autoApproveNumberDays
     * @throws WorkflowException
     */
    private String blanketApproveDocument(ProcurementCardDocument pcardDocument, Timestamp currentDate) throws WorkflowException {
        StringBuilder errorMessage = new StringBuilder();

        // update document description to reflect the auto approval
        if (pcardDocument.getDocumentHeader().getDocumentDescription().startsWith("FY ")) {
            pcardDocument.getDocumentHeader().setDocumentDescription(pcardDocument.getDocumentHeader().getDocumentDescription().substring(0, 7) + " Auto Approve " + dateTimeService.toDateTimeString(currentDate));
        } else {
            pcardDocument.getDocumentHeader().setDocumentDescription("Auto Approved On " + dateTimeService.toDateTimeString(currentDate) + ".");
        }
        LOG.info("Auto approving document # " + pcardDocument.getDocumentHeader().getDocumentNumber());
        pcardDocument.setAutoApprovedIndicator(true);

        try {
            documentService.blanketApproveDocument(pcardDocument, "", null);
        } catch (ValidationException e) {
            LOG.error("Error auto approving document # " + pcardDocument.getDocumentNumber() + " " + e.getMessage(), e);
            errorMessage.append(pcardDocument.getDocumentNumber() + "\n");
            errorMessage.append(e.getMessage() + "\n");
            // grab and log all error messages that would have been displayed to the user.

            for (Map.Entry<String, AutoPopulatingList<ErrorMessage>> entry : GlobalVariables.getMessageMap().getErrorMessages().entrySet()) {
                for (ErrorMessage em : entry.getValue()) {
                    errorMessage.append(expandErrorString(em.getErrorKey(), em.getMessageParameters()) + "\n");
                }
            }
            GlobalVariables.getMessageMap().clearErrorMessages();
            errorMessage.append(AUTO_APPROVE_ERROR_LOG_SEPARATOR);
        }
        return errorMessage.toString();

    }

    /**
     * Take the error key and expand as would happen when displaying error to the client.
     *
     * @param errorKey
     * @param params
     * @return
     */
    private String expandErrorString(String errorKey, String[] params) {
        String questionText = configurationService.getPropertyValueAsString(errorKey);
        for (int i = 0; i < params.length; i++) {
            questionText = StringUtils.replace(questionText, "{" + i + "}", params[i]);
        }
        return questionText;
    }

    private void outputErrorFile(String errorString) {
        String errorFile = batchFileOutputDirectoryName + File.separator + "pcdo_reroute_errors_" + dateTimeService.getCurrentCalendar().getTimeInMillis() + ".txt";
        try {
            BufferedWriter autoApproveErrorWriter = new BufferedWriter(new FileWriter(errorFile));
            autoApproveErrorWriter.write("PCDO Auto Approve Errors, " + dateTimeService.toDateTimeString(new Date()) + "\n");
            autoApproveErrorWriter.write(AUTO_APPROVE_ERROR_LOG_SEPARATOR);
            autoApproveErrorWriter.write(errorString);
            autoApproveErrorWriter.close();
        } catch (IOException e) {
            LOG.error("Error logging auto approve error.", e);
            LOG.error("PCDO Auto Approve Errors, " + dateTimeService.toDateTimeString(new Date()) + "\n");
            LOG.error(AUTO_APPROVE_ERROR_LOG_SEPARATOR);
            LOG.error(errorString);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    protected String createAndValidateAccountingLines(org.kuali.kfs.fp.document.ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        //get default values from custom Procurement Cardholder table
        ProcurementCardDefault procurementCardHolderDetail = getProcurementCardDefault(transaction.getTransactionCreditCardNumber());

        String cardHolderName = transaction.getCardHolderName();
        if (cardHolderName.length() > CARDHOLDER_NAME_MAX_LENGTH) {
            cardHolderName = transaction.getCardHolderName().substring(0, CARDHOLDER_NAME_MAX_LENGTH);
        }
        if(ObjectUtils.isNotNull(procurementCardHolderDetail)) {
            pcardDocument.getDocumentHeader().setDocumentDescription(procurementCardHolderDetail.getCreditCardLastFour().trim() + " / " + cardHolderName);
        }
        else {
            pcardDocument.getDocumentHeader().setDocumentDescription("None / " + cardHolderName);
        }
        
        return super.createAndValidateAccountingLines(pcardDocument, transaction, docTransactionDetail);
    }
    
    @Override
    protected ProcurementCardTargetAccountingLine createTargetAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardTargetAccountingLine targetLine = new ProcurementCardTargetAccountingLine();
        ProcurementCardDefault procurementCardHolderDetail = getProcurementCardDefault(transaction.getTransactionCreditCardNumber());
        
        targetLine.setDocumentNumber(docTransactionDetail.getDocumentNumber());
        targetLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        //set error account info as default
        targetLine.setChartOfAccountsCode(getErrorChartCode());
        targetLine.setAccountNumber(getErrorAccountNumber());
        targetLine.setFinancialObjectCode(getDefaultObjectCode());
        if (hasReconciler(procurementCardHolderDetail)) {
            targetLine.setChartOfAccountsCode(StringUtils.isNotEmpty(procurementCardHolderDetail.getChartOfAccountsCode()) ? procurementCardHolderDetail.getChartOfAccountsCode() : getErrorChartCode());
            targetLine.setAccountNumber(StringUtils.isNotEmpty(procurementCardHolderDetail.getAccountNumber()) ? procurementCardHolderDetail.getAccountNumber() : getErrorAccountNumber());
            targetLine.setFinancialObjectCode(StringUtils.isNotEmpty(procurementCardHolderDetail.getFinancialObjectCode()) ? procurementCardHolderDetail.getFinancialObjectCode() : getDefaultObjectCode());
        }
        if (ObjectUtils.isNotNull(procurementCardHolderDetail)) {
            targetLine.setSubAccountNumber(procurementCardHolderDetail.getSubAccountNumber());
            targetLine.setFinancialSubObjectCode(procurementCardHolderDetail.getFinancialSubObjectCode());
        }
        targetLine.setProjectCode(transaction.getProjectCode());

        if (KFSConstants.GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
            targetLine.setAmount(transaction.getFinancialDocumentTotalAmount().negated());
        }
        else {
            targetLine.setAmount(transaction.getFinancialDocumentTotalAmount());
        }

        return targetLine;
    }
    
    @Override
    protected String validateTargetAccountingLine(ProcurementCardTargetAccountingLine targetLine) {
        StringBuilder errorText = new StringBuilder(KFSConstants.EMPTY_STRING);

        targetLine.refresh();
        final String lineNumber = targetLine.getSequenceNumber() == null ? KFSPropertyConstants.NEW : targetLine.getSequenceNumber().toString();

        if (!accountingLineRuleUtil.isValidChart(KFSConstants.EMPTY_STRING, targetLine.getChart(), getDataDictionaryService().getDataDictionary())) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " is invalid; using error Chart Code.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText.append(" " + tempErrorText);

            targetLine.setChartOfAccountsCode(getErrorChartCode());
            targetLine.refresh();
        }

        if (!accountingLineRuleUtil.isValidAccount(KFSConstants.EMPTY_STRING, targetLine.getAccount(), getDataDictionaryService().getDataDictionary()) || targetLine.getAccount().isExpired()) {
            //changing this line from delivered code to correct the error text
            String tempErrorText = targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " is invalid; using error account.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText.append(" " + tempErrorText);

            targetLine.setChartOfAccountsCode(getErrorChartCode());
            targetLine.setAccountNumber(getErrorAccountNumber());
            targetLine.refresh();
        }

        if (!accountingLineRuleUtil.isValidObjectCode(KFSConstants.EMPTY_STRING, targetLine.getObjectCode(), getDataDictionaryService().getDataDictionary())) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " Object Code " + targetLine.getFinancialObjectCode() + " is invalid; using default Object Code.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText.append(" " + tempErrorText);

            targetLine.setFinancialObjectCode(getDefaultObjectCode());
            targetLine.refresh();
        }

        if (StringUtils.isNotBlank(targetLine.getSubAccountNumber()) && !accountingLineRuleUtil.isValidSubAccount(KFSConstants.EMPTY_STRING, targetLine.getSubAccount(), getDataDictionaryService().getDataDictionary())) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " Sub Account " + targetLine.getSubAccountNumber() + " is invalid; Setting Sub Account to blank.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText.append(" " + tempErrorText);

            targetLine.setSubAccountNumber("");
        }

        if (StringUtils.isNotBlank(targetLine.getFinancialSubObjectCode()) && !accountingLineRuleUtil.isValidSubObjectCode(KFSConstants.EMPTY_STRING, targetLine.getSubObjectCode(), getDataDictionaryService().getDataDictionary())) {
            String tempErrorText = "Target Accounting Line "+lineNumber+" Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " Object Code " + targetLine.getFinancialObjectCode() + " Sub Object Code " + targetLine.getFinancialSubObjectCode() + " is invalid; setting Sub Object to blank.";
            if ( LOG.isInfoEnabled() ) {
                LOG.info(tempErrorText);
            }
            errorText.append(" " + tempErrorText);

            targetLine.setFinancialSubObjectCode("");
        }

        if (StringUtils.isNotBlank(targetLine.getProjectCode()) && !accountingLineRuleUtil.isValidProjectCode(KFSConstants.EMPTY_STRING, targetLine.getProject(), getDataDictionaryService().getDataDictionary())) {
            if ( LOG.isInfoEnabled() ) {
                LOG.info("Target Accounting Line "+lineNumber+" Project Code " + targetLine.getProjectCode() + " is invalid; setting to blank.");
            }
            errorText.append(" Target Accounting Line "+lineNumber+" Project Code " + targetLine.getProjectCode() + " is invalid; setting to blank.");

            targetLine.setProjectCode(KFSConstants.EMPTY_STRING);
        }

        // clear out GlobalVariable message map, since we have taken care of the errors
        GlobalVariables.setMessageMap(new MessageMap());

        return errorText.toString();
    }
    
    /**
     * Returns a list of all initiated but not yet routed procurement card documents, using the KualiWorkflowInfo service.
     * @return a list of procurement card documents to route
     */
    //copy-pasted from super class to use correct object types (edu.arizona instead of org.kuali)
    // in this case, also needed to refactor method name to avoid overloading conflicts
    private Collection<ProcurementCardDocument> retrieveUAProcurementCardDocumentsToRoute(String statusCode){

        try {
            return getFinancialSystemDocumentService().findByWorkflowStatusCode(ProcurementCardDocument.class, DocumentStatus.fromCode(statusCode));
        } catch (WorkflowException e) {
            LOG.error("Error searching for enroute procurement card documents " + e.getMessage());
            throw new RuntimeException(e.getMessage(),e);
        }

    }

    private boolean hasReconciler(ProcurementCardDefault procurementCardDefault) {
        boolean retCode = true;
        if (ObjectUtils.isNull(procurementCardDefault) ||
            ObjectUtils.isNull(procurementCardDefault.getReconcilerGroupId()) ||
            ObjectUtils.isNull(procurementCardDefault.getCardHolderSystemId())) {
            retCode = false;
        }
        else {
            List<String> groupMembers = new ArrayList<String>();
            String reconcilerGroupId = procurementCardDefault.getReconcilerGroupId();
            groupMembers = SpringContext.getBean(GroupService.class).getMemberPrincipalIds(reconcilerGroupId);
            if (groupMembers.isEmpty() ||
                (groupMembers.size() == 1 &&
                 groupMembers.get(0).equals(procurementCardDefault.getCardHolderSystemId()))) {
                retCode = false;
            }
        }
        return retCode;
    }
    
    protected String getSystemUserPrincipalId() {
        String systemUserName = parameterService.getParameterValueAsString(KRADConstants.KNS_NAMESPACE, KfsParameterConstants.ALL_COMPONENT, KFSConstants.SYSTEM_USER_NAME);
        Person systemUser = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(systemUserName);
        return systemUser.getPrincipalId();
    }
}
