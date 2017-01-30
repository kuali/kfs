package edu.arizona.kfs.fp.batch.service.impl;

import static org.kuali.kfs.sys.KFSConstants.FinancialDocumentTypeCodes.PROCUREMENT_CARD;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.batch.ProcurementCardAutoApproveDocumentsStep;
import org.kuali.kfs.fp.batch.ProcurementCardCreateDocumentsStep;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.DocumentActionParameters;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.document.DocumentRefreshQueue;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.fp.batch.ProcurementCardRerouteDocumentsStep;
import edu.arizona.kfs.fp.businessobject.ProcurementCardDefault;
import edu.arizona.kfs.fp.businessobject.ProcurementCardHolder;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTransaction;
import edu.arizona.kfs.fp.document.ProcurementCardDocument;

public class ProcurementCardCreateDocumentServiceImpl extends org.kuali.kfs.fp.batch.service.impl.ProcurementCardCreateDocumentServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentServiceImpl.class);
    
    private GroupService procurementCardGroupService;
    
    public GroupService getProcurementCardGroupService(){
        return procurementCardGroupService;
    }
    
    public void setProcurementCardGroupService(GroupService procurementCardGroupService){
        this.procurementCardGroupService = procurementCardGroupService;
    }

    public static final String REROUTE_PCDO_DOCUMENTS_IND_PARM_NM = "REROUTE_PCDO_DOCUMENTS_IND";
    public static final String RECONCILER_GROUPS_TO_REROUTE_PARM_NM = "RECONCILER_GROUPS_TO_REROUTE";
    
    /**
     * This method looks for ProcurementCardDocuments that are in route status at the "AccountFullEdit" route node and routed to the error account FO. 
     * Then checks for a valid reconciler and if found, reroutes the document back to the Reconciler.
     * 
     * 
     * @return True if the routing was performed successfully.  A runtime exception will be thrown if any errors occur while routing.
     * 
     * @see org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService#rerouteProcurementCardDocuments(java.util.List)
     */
    @Override
    public boolean rerouteProcurementCardDocuments() {
        
        // check for reconciler group rerouting
        boolean groupRerouting = parameterService.getParameterValueAsBoolean(ProcurementCardRerouteDocumentsStep.class, REROUTE_PCDO_DOCUMENTS_IND_PARM_NM);
        
        Collection<ProcurementCardDocument> documents = null;
        documents = retrieveUAProcurementCardDocumentsToRoute(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);
        
        LOG.info("PCards to Reroute: " + documents.size());

        for (ProcurementCardDocument pcardDocument : documents) {
        
            String pcardDocumentId = pcardDocument.getObjectId();
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
                    requeueDocument(pcardDocument, pcardDocumentId, node, "HasReconciler"); 
                    // pause between reroutes
                    try {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                
              //step two, reroute documents enroute to the reconciler group(s) that had group membership problems
                else if (node.equals("ProcurementCardReconciler") && groupRerouting && 
                        parameterService.getParameter(ProcurementCardRerouteDocumentsStep.class,
                                RECONCILER_GROUPS_TO_REROUTE_PARM_NM).getValue() != null
                        && parameterService
                                .getParameter(ProcurementCardRerouteDocumentsStep.class,
                                        RECONCILER_GROUPS_TO_REROUTE_PARM_NM)
                                .getValue().contains(procurementCardHolderDetail.getReconcilerGroupId())) {
                    LOG.info("Rerouting doc #" + pcardDocumentId + " at ProcurementCardReconciler, in "
                            + RECONCILER_GROUPS_TO_REROUTE_PARM_NM + " group "
                            + procurementCardHolderDetail.getReconcilerGroupId());
                    requeueDocument(pcardDocument, pcardDocumentId, node, "ProcurementCardReconciler"); 
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
    
    private void requeueDocument(ProcurementCardDocument pcardDocument, String pcardDocumentId, String node, String prevNode) {
        //pcardDocumentId could have leading 0's; want these removed for processing calls
        String documentId = StringUtils.stripStart(pcardDocumentId, "0");
        WorkflowDocumentActionsService documentActions = KewApiServiceLocator.getWorkflowDocumentActionsService();
        // Updated System User ID, should change this to use the parameter
        DocumentActionParameters actionParameters = DocumentActionParameters.create(documentId, "T000000000000005493", node+" Batch Reroute to Reconciler");
        ReturnPoint returnPoint = ReturnPoint.create(prevNode);
        documentActions.superUserReturnToPreviousNode(actionParameters, false, returnPoint);
        // Use requeuer service to put the document back into action list of reconciler
        DocumentRefreshQueue docRequeue = KewApiServiceLocator.getDocumentRequeuerService("KFS",documentId, 0x0);
        docRequeue.refreshDocument(documentId);    
       
        LOG.info("Rerouting PCDO document # " + pcardDocumentId + ".");
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
                    transactionIssuesSummary = transactionIssuesSummary.concat(transactionIssuesSummary);
                }

                // update document total
                documentTotalAmount = documentTotalAmount.add(transaction.getFinancialDocumentTotalAmount());

                transactionLineNumber++;
            }

            pcardDocument.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(documentTotalAmount);
            // PCDO Default Description
            setupDocumentDescription(pcardDocument);

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
        //if auto approve is turned on
        boolean autoApproveOn = super.parameterService.getParameterValueAsBoolean(
                ProcurementCardAutoApproveDocumentsStep.class,
                ProcurementCardDocumentRuleConstants.AUTO_APPROVE_DOCUMENTS_IND);

        if (!autoApproveOn) { // no auto approve?  then skip out of here...
            return true;
        }

        Collection<ProcurementCardDocument> pcardDocumentList = retrieveUAProcurementCardDocumentsToRoute(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);

        // get number of days and type for auto approve
        int autoApproveNumberDays = Integer
                .parseInt(super.parameterService.getParameterValueAsString(ProcurementCardAutoApproveDocumentsStep.class,
                        ProcurementCardDocumentRuleConstants.AUTO_APPROVE_NUMBER_OF_DAYS));

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
                    if (pcardDocument.getDocumentHeader().getDocumentDescription().startsWith("FY ")) {
                        pcardDocument.getDocumentHeader().setDocumentDescription(pcardDocument.getDocumentHeader().getDocumentDescription().substring(0, 7) + " Auto Approve " + dateTimeService.toDateTimeString(currentDate));
                    } else {
                        pcardDocument.getDocumentHeader().setDocumentDescription("Auto Approved On " + dateTimeService.toDateTimeString(currentDate) + ".");
                    }
                    LOG.info("Auto approving document # " + pcardDocument.getDocumentHeader().getDocumentNumber());
                    pcardDocument.setAutoApprovedIndicator(true);
                    getDocumentService().superUserApproveDocument(pcardDocument, "");
                }
            } catch (WorkflowException e) {
                LOG.error("Error auto approving document # " + pcardDocument.getDocumentNumber() + " " + e.getMessage(),e);
                throw new RuntimeException(e.getMessage(),e);
            } 
        }

        return true;
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
}
