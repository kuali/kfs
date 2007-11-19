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

import java.util.ArrayList;

import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * Purchase Order Reopen Document
 */
public class PurchaseOrderReopenDocument extends PurchaseOrderDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderReopenDocument.class);

    /**
     * Default constructor.
     */
    public PurchaseOrderReopenDocument() {
        super();
    }

    /**
     * General Ledger pending entries are not created on save for this document. They are created when the document has been finally
     * processed. Overriding this method so that entries are not created yet.
     * 
     * @see org.kuali.module.purap.document.PurchaseOrderDocument#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        LOG.info("prepareForSave(KualiDocumentEvent) do not create gl entries");
        setSourceAccountingLines(new ArrayList());
        setGeneralLedgerPendingEntries(new ArrayList());
    }


    /**
     * When Purchase Order Reopen document has been processed through Workflow, the general ledger entries are created and the PO
     * status changes to "OPEN".
     * 
     * @see org.kuali.module.purap.document.PurchaseOrderDocument#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        // DOCUMENT PROCESSED
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            // generate GL entries
            SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesReopenPurchaseOrder(this);

            // update indicators
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForApprovedPODocuments(this);

            // set purap status
            SpringContext.getBean(PurapService.class).updateStatus(this, PurapConstants.PurchaseOrderStatuses.OPEN);
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(this);
        }
        // DOCUMENT DISAPPROVED
        else if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedReopenPODocuments(this);
        }
        // DOCUMENT CANCELED
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledReopenPODocuments(this);
        }

    }

    public NodeDetails getNodeDetailEnum(String newNodeName) {
        // no statuses to set means no node details
        return null;
    }

}
