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

package org.kuali.module.purap.document;

import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * Purchase Order Payment Hold Document
 */
public class PurchaseOrderPaymentHoldDocument extends PurchaseOrderDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderPaymentHoldDocument.class);

    /**
     * Default constructor.
     */
    public PurchaseOrderPaymentHoldDocument() {
        super();
    }

    /**
     * General Ledger pending entries are not created for this document. Overriding this method so that entries are not created.
     * 
     * @see org.kuali.module.purap.document.PurchaseOrderDocument#customPrepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void customPrepareForSave(KualiDocumentEvent event) {
        // do not set the accounts in sourceAccountingLines; this document should not create GL entries
    }

    /**
     * When Purchase Order Payment Hold document has been Processed through Workflow, the PO status changes to "Payment Hold".
     * 
     * @see org.kuali.module.purap.document.PurchaseOrderDocument#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        // DOCUMENT PROCESSED
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForApprovedPODocuments(this);

            // set purap status
            SpringContext.getBean(PurapService.class).updateStatus(this, PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD);
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(this);
        }
        // DOCUMENT DISAPPROVED
        else if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(this);
        }
        // DOCUMENT CANCELED
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledChangePODocuments(this);
        }
    }

}
