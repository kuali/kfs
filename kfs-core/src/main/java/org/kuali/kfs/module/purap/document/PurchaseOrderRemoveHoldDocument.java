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

package org.kuali.kfs.module.purap.document;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Purchase Order Remove Payment Hold Document
 */
public class PurchaseOrderRemoveHoldDocument extends PurchaseOrderDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderRemoveHoldDocument.class);

    /**
     * General Ledger pending entries are not created for this document. Overriding this method so that entries are not created.
     *
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderDocument#customPrepareForSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void customPrepareForSave(KualiDocumentEvent event) {
        // do not set the accounts in sourceAccountingLines; this document should not create GL entries
    }

    /**
     * When Purchase Order Remove Payment Hold document has been Processed through Workflow, the PO status changes to "OPEN".
     *
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderDocument#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        try {
            // DOCUMENT PROCESSED
            if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForApprovedPODocuments(this);

                // for app doc status
                updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_OPEN);
            }
            // DOCUMENT DISAPPROVED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isDisapproved()) {
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedRemoveHoldPODocuments(this);

                // for app doc status
                try {
                    String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(this.getFinancialSystemDocumentHeader().getWorkflowDocument());
                    String reqStatus = PurapConstants.PurchaseOrderStatuses.getPurchaseOrderAppDocDisapproveStatuses().get(nodeName);
                    updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.getPurchaseOrderAppDocDisapproveStatuses().get(reqStatus));
                } catch (WorkflowException e) {
                    logAndThrowRuntimeException("Error saving routing data while saving App Doc Status " + getDocumentNumber(), e);
                }
            }
            // DOCUMENT CANCELED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isCanceled()) {
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledRemoveHoldPODocuments(this);
                // for app doc status
                updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_CANCELLED);
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    @Override
    protected boolean shouldAdhocFyi() {
        return false;
    }

}
