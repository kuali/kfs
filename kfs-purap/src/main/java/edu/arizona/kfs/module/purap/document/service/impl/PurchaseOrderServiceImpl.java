package edu.arizona.kfs.module.purap.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.module.purap.document.service.PurApWorkflowIntegrationService;

@Transactional
public class PurchaseOrderServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.PurchaseOrderServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderServiceImpl.class);

    private static final String PRINT = "PRINT";

    protected PurApWorkflowIntegrationService purapWorkflowIntegrationService;

    public void setPurapWorkflowIntegrationService(PurApWorkflowIntegrationService purapWorkflowIntegrationService) {
        this.purapWorkflowIntegrationService = purapWorkflowIntegrationService;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#performPurchaseOrderFirstTransmitViaPrinting(java.lang.String, java.io.ByteArrayOutputStream)
     */
    @Override
    public void performPurchaseOrderFirstTransmitViaPrinting(String documentNumber, ByteArrayOutputStream baosPDF) {
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(documentNumber);
        String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        @SuppressWarnings("unchecked")
        Collection<String> generatePDFErrors = printService.generatePurchaseOrderPdf(po, baosPDF, environment, null);
        if (!generatePDFErrors.isEmpty()) {
            addStringErrorMessagesToMessageMap(PurapKeyConstants.ERROR_PURCHASE_ORDER_PDF, generatePDFErrors);
            throw new ValidationException("printing purchase order for first transmission failed");
        }
        if (ObjectUtils.isNotNull(po.getPurchaseOrderFirstTransmissionTimestamp())) {
            // should not call this method for first transmission if document has already been transmitted
            String errorMsg = "Method to perform first transmit was called on document (doc id " + documentNumber + ") with already filled in 'first transmit date'";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        Timestamp currentDate = dateTimeService.getCurrentTimestamp();
        po.setPurchaseOrderFirstTransmissionTimestamp(currentDate);
        po.setPurchaseOrderLastTransmitTimestamp(currentDate);
        po.setOverrideWorkflowButtons(Boolean.FALSE);
        ActionRequest printingRequest = getActionRequestForPurchaseOrderDocumentPrint(po);
        if (printingRequest != null) {
            // after we retrieve that person that's supposed to print the doc, we determine whether we should do a plain FYI or need to use the superuser to proxy the FYI to clear out the PRINT FYI
            // request
            Person currentUser = GlobalVariables.getUserSession().getPerson();
            if (currentUser.getPrincipalId().equals(printingRequest.getPrincipalId())) {
                Person systemUserPerson = getPersonService().getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
                purapWorkflowIntegrationService.takeAllActionsForGivenCriteria(po, "Action taken automatically as part of document initial print transmission by user " + GlobalVariables.getUserSession().getPerson().getName(), PurapConstants.PurchaseOrderStatuses.NODE_DOCUMENT_TRANSMISSION, systemUserPerson, KFSConstants.SYSTEM_USER);
            } else {
                // there is a print request to another user, so we need to clear that user's action requests regardless of whether this user has action requests
                purapWorkflowIntegrationService.clearFYIRequestAsSuperUser(printingRequest, "Action taken automatically as part of document initial print transmission by user " + currentUser.getName());
            }
        }
        po.setOverrideWorkflowButtons(Boolean.TRUE);
        if (!po.getApplicationDocumentStatus().equals(PurapConstants.PurchaseOrderStatuses.APPDOC_OPEN)) {
            attemptSetupOfInitialOpenOfDocument(po);
            if (shouldAdhocFyi(po.getRequisitionSourceCode())) {
                sendAdhocFyi(po);
            }
        }
        purapService.saveDocumentNoValidation(po);
    }

    private boolean shouldAdhocFyi(String reqSourceCode) {
        Collection<String> excludeList = new ArrayList<String>();
        if (parameterService.parameterExists(PurchaseOrderDocument.class, PurapParameterConstants.PO_NOTIFY_EXCLUSIONS)) {
            excludeList = parameterService.getParameterValuesAsString(PurchaseOrderDocument.class, PurapParameterConstants.PO_NOTIFY_EXCLUSIONS);
        }
        return !excludeList.contains(reqSourceCode);
    }

    /**
     * Returns the Person object representing the person that was supposed to print this document
     *
     * @param purchaseOrderDocument
     * @return
     */
    private ActionRequest getActionRequestForPurchaseOrderDocumentPrint(PurchaseOrderDocument purchaseOrderDocument) {
        String documentId = purchaseOrderDocument.getDocumentHeader().getDocumentNumber();
        List<ActionRequest> actionRequests = workflowDocumentService.getActionRequestsForPrincipalAtNode(documentId, null, null);
        for (ActionRequest actionRequest : actionRequests) {
            // trying to find the Action Request that matches the action request created in the completePurchaseOrder method above that method generated an AdHoc FYI request, with an request label of
            // PRINT
            if (actionRequest.isActivated() && actionRequest.isFyiRequest() && actionRequest.isAdHocRequest() && PRINT.equals(actionRequest.getRequestLabel()) && actionRequest.isForceAction()) {
                return actionRequest;
            }
        }
        return null;
    }

}
