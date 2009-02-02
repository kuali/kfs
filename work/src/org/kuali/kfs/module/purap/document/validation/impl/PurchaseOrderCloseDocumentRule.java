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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Rules for Purchase Order Close Document creation.
 */
public class PurchaseOrderCloseDocumentRule extends PurchasingDocumentRuleBase {

//    /**
//     * Central method to control the processing of rule checks. Checks that the purchase order document is not null, that it is in
//     * the correct status, and delegates further rule checking.
//     * 
//     * @param document A PurchaseOrderDocument. (Not a PurchaseOrderCloseDocument at this point.)
//     * @return True if the document passes all the validations.
//     */
//    public boolean processValidation(PurchaseOrderDocument document) {
//        boolean valid = true;
//        // Check that the PO is not null
//        if (ObjectUtils.isNull(document)) {
//            throw new ValidationException("Purchase Order Close document was null on validation.");
//        }
//        else {
//            PurchaseOrderDocument currentPO = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(document.getPurapDocumentIdentifier());
//
//            // The PO must be in OPEN or PENDING_CLOSE status.
//            if (ObjectUtils.isNull(currentPO)){
//                throw new ValidationException("Current Purchase Order document cannot be found.");
//            }
//            else if (StringUtils.isEmpty(currentPO.getStatusCode())) {
//                throw new ValidationException("Current Purchase Order document has no status.");
//            }
//            else if (!(StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurchaseOrderStatuses.PENDING_CLOSE) || 
//                      (StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurchaseOrderStatuses.OPEN)))) {
//                valid = false;
//                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.STATUS_CODE, PurapKeyConstants.ERROR_PURCHASE_ORDER_STATUS_NOT_REQUIRED_STATUS, PurchaseOrderStatuses.OPEN);
//            }
//            else {
//                valid &= processPaymentRequestRules(document);
//            } 
//        }
//        return valid;
//    }
//
//    /**
//     * Processes validation rules having to do with any payment requests that the given purchase order may have. Specifically,
//     * validates that at least one payment request exists, and makes further checks about the status of such payment requests.
//     * 
//     * @param document A PurchaseOrderDocument
//     * @return True if the document passes all the validations.
//     */
//    public boolean processPaymentRequestRules(PurchaseOrderDocument document) {
//        boolean valid = true;
//        // The PO must have at least one PREQ against it.
//        Integer poDocId = document.getPurapDocumentIdentifier();
//        List<PaymentRequestDocument> pReqs = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestsByPurchaseOrderId(poDocId);
//        if (ObjectUtils.isNotNull(pReqs)) {
//            if (pReqs.size() == 0) {
//                valid = false;
//                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_PURCHASE_ORDER_CLOSE_NO_PREQ, PODocumentsStrings.OPEN_STATUS);
//            }
//            else {
//                boolean checkInProcess = true;
//                boolean hasInProcess = false;
//
//                for (PaymentRequestDocument pReq : pReqs) {
//                    // skip exception docs
//                    if (pReq.getDocumentHeader().getWorkflowDocument().stateIsException()) {
//                        continue;
//                    }
//                    // NOTE for below, this could/should be changed to look at the first route level after full entry instead of
//                    // being tied to AwaitingFiscal (in case full entry is moved)
//                    // look for a doc that is currently routing, that will probably be the one that called this close if called from
//                    // preq (with close po box)
//                    if (StringUtils.equalsIgnoreCase(pReq.getStatusCode(), PaymentRequestStatuses.AWAITING_FISCAL_REVIEW) && !StringUtils.equalsIgnoreCase(pReq.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames(), PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum.ACCOUNT_REVIEW.getName())) {
//                        // terminate the search since this close doc is probably being called by this doc, a doc should never be In
//                        // Process and enroute in any other case
//                        checkInProcess = false;
//                        break;
//                    }
//                    if (StringUtils.equalsIgnoreCase(pReq.getStatusCode(), PaymentRequestStatuses.IN_PROCESS)) {
//                        hasInProcess = true;
//                    }
//                }
//                if (checkInProcess && hasInProcess) {
//                    valid = false;
//                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_PURCHASE_ORDER_CLOSE_PREQ_IN_PROCESS, PREQDocumentsStrings.IN_PROCESS);
//                }
//            }
//        }
//
//        return valid;
//    }
}
