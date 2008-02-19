/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapWorkflowConstants;
import org.kuali.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * Rules for Purchase Order Close Document creation.
 */
public class PurchaseOrderCloseDocumentRule extends PurchasingDocumentRuleBase {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(poDocument);
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(poDocument);
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) approveEvent.getDocument();
        return isValid;
    }

    /**
     * Central method to control the processing of rule checks. Checks that the purchase order document is not null, that it is in
     * the correct status, and delegates further rule checking.
     * 
     * @param document A PurchaseOrderDocument. (Not a PurchaseOrderCloseDocument at this point.)
     * @return True if the document passes all the validations.
     */
    public boolean processValidation(PurchaseOrderDocument document) {
        boolean valid = true;
        // Check that the PO is not null
        if (ObjectUtils.isNull(document)) {
            throw new ValidationException("Purchase Order Close document was null on validation.");
        }
        else {
            PurchaseOrderDocument currentPO = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(document.getPurapDocumentIdentifier());

            // The PO must be in OPEN status.
            if (!StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurchaseOrderStatuses.PENDING_CLOSE) && !StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurchaseOrderStatuses.OPEN)) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.STATUS_CODE, PurapKeyConstants.ERROR_PURCHASE_ORDER_STATUS_NOT_REQUIRED_STATUS, PurchaseOrderStatuses.OPEN);
            }
            else {
                valid &= processPaymentRequestRules(document);
            }
        }
        return valid;
    }

    /**
     * Processes validation rules having to do with any payment requests that the given purchase order may have. Specifically,
     * validates that at least one payment request exists, and makes furthur checks about the status of such payment requests.
     * 
     * @param document A PurchaseOrderDocument
     * @return True if the document passes all the validations.
     */
    public boolean processPaymentRequestRules(PurchaseOrderDocument document) {
        boolean valid = true;
        // The PO must have at least one PREQ against it.
        Integer poDocId = document.getPurapDocumentIdentifier();
        List<PaymentRequestDocument> pReqs = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestsByPurchaseOrderId(poDocId);
        if (ObjectUtils.isNotNull(pReqs)) {
            if (pReqs.size() == 0) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_PURCHASE_ORDER_CLOSE_NO_PREQ, PODocumentsStrings.OPEN_STATUS);
            }
            else {
                boolean checkInProcess = true;
                boolean hasInProcess = false;

                for (PaymentRequestDocument pReq : pReqs) {
                    // skip exception docs
                    if (pReq.getDocumentHeader().getWorkflowDocument().stateIsException()) {
                        continue;
                    }
                    // NOTE for below, this could/should be changed to look at the first route level after full entry instead of
                    // being tied to AwaitingFiscal (in case full entry is moved)
                    // look for a doc that is currently routing, that will probably be the one that called this close if called from
                    // preq (with close po box)
                    if (StringUtils.equalsIgnoreCase(pReq.getStatusCode(), PaymentRequestStatuses.AWAITING_FISCAL_REVIEW) && !StringUtils.equalsIgnoreCase(pReq.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames(), PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum.ACCOUNT_REVIEW.getName())) {
                        // terminate the search since this close doc is probably being called by this doc, a doc should never be In
                        // Process and enroute in any other case
                        checkInProcess = false;
                        break;
                    }
                    if (StringUtils.equalsIgnoreCase(pReq.getStatusCode(), PaymentRequestStatuses.IN_PROCESS)) {
                        hasInProcess = true;
                    }
                }
                if (checkInProcess && hasInProcess) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_PURCHASE_ORDER_CLOSE_PREQ_IN_PROCESS, PREQDocumentsStrings.IN_PROCESS);
                }
            }
        }

        return valid;
    }
}
