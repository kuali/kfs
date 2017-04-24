package edu.arizona.kfs.module.purap.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurchaseOrderServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.PurchaseOrderServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.purap.document.service.PurchaseOrderService#performPurchaseOrderFirstTransmitViaPrinting(java.lang.String, java.io.ByteArrayOutputStream)
     */
    @Override
    public void performPurchaseOrderFirstTransmitViaPrinting(String documentNumber, ByteArrayOutputStream baosPDF) {
        PurchaseOrderDocument po = getPurchaseOrderByDocumentNumber(documentNumber);
        String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
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
        boolean performedAction = purapWorkflowIntegrationService.takeAllActionsForGivenCriteria(po, "Action taken automatically as part of document initial print transmission", PurapConstants.PurchaseOrderStatuses.NODE_DOCUMENT_TRANSMISSION, GlobalVariables.getUserSession().getPerson(), null);
        if (!performedAction) {
            Person systemUserPerson = getPersonService().getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
            purapWorkflowIntegrationService.takeAllActionsForGivenCriteria(po, "Action taken automatically as part of document initial print transmission by user " + GlobalVariables.getUserSession().getPerson().getName(), PurapConstants.PurchaseOrderStatuses.NODE_DOCUMENT_TRANSMISSION, systemUserPerson, KFSConstants.SYSTEM_USER);
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

}
