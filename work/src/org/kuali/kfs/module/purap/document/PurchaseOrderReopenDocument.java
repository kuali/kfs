/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import static org.kuali.core.util.KualiDecimal.ZERO;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * Purchase Order Document
 */
public class PurchaseOrderReopenDocument extends PurchaseOrderDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderReopenDocument.class);

    /**
     * Default constructor.
     */
    public PurchaseOrderReopenDocument() {
        super();
    }

    public void customPrepareForSave(KualiDocumentEvent event) {
        LOG.info("customPrepareForSave(KualiDocumentEvent) enter method for PO Close doc #" + getDocumentNumber());

        // Set outstanding encumbered quantity/amount on items
        for (Iterator items = this.getItems().iterator(); items.hasNext();) {
            PurchaseOrderItem item = (PurchaseOrderItem) items.next();

            String logItmNbr = "Item # " + item.getItemLineNumber();

            if (!item.isItemActiveIndicator()) {
                continue;
            }

            KualiDecimal itemAmount = null;
            if (!item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                LOG.debug("poCloseReopen() " + logItmNbr + " Calculate based on amounts");
                itemAmount = item.getItemOutstandingEncumberedAmount() == null ? ZERO  : item.getItemOutstandingEncumberedAmount();
            }
            else {
                LOG.debug("poCloseReopen() " + logItmNbr + " Calculate based on quantities");
                itemAmount = item.getItemOutstandingEncumberedQuantity().multiply(new KualiDecimal(item.getItemUnitPrice()));
            }

            KualiDecimal accountTotal = ZERO;
            PurchaseOrderAccount lastAccount = null;
            if ( itemAmount.compareTo(ZERO) != 0 ) {
                // Sort accounts
                Collections.sort((List) item.getSourceAccountingLines());
              
                for (Iterator iterAcct = item.getSourceAccountingLines().iterator(); iterAcct.hasNext();) {
                    PurchaseOrderAccount acct = (PurchaseOrderAccount) iterAcct.next();
                    if (!acct.isEmpty()) {
                        KualiDecimal acctAmount = itemAmount.multiply(new KualiDecimal(acct.getAccountLinePercent().toString())).divide(PurapConstants.HUNDRED);
                        accountTotal = accountTotal.add(acctAmount);
                        acct.setAlternateAmountForGLEntryCreation(acctAmount);
                        lastAccount = acct;
                    }
                }

                // account for rounding by adjusting last account as needed
                if (lastAccount != null) {
                    KualiDecimal difference = itemAmount.subtract(accountTotal);
                    LOG.debug("poCloseReopen() difference: " + logItmNbr + " " + difference);

                    KualiDecimal amount = lastAccount.getAlternateAmountForGLEntryCreation();
                    if (ObjectUtils.isNotNull(amount)) {
                        lastAccount.setAlternateAmountForGLEntryCreation(amount.add(difference));
                    }
                    else {
                        lastAccount.setAlternateAmountForGLEntryCreation(difference);
                    }
                }

            }
        }// endfor
          
        setSourceAccountingLines(SpringContext.getBean(PurapAccountingService.class).generateSummaryWithNoZeroTotalsUsingAlternateAmount(getItemsActiveOnly()));

    }//end customPrepareForSave(KualiDocumentEvent)

    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        
        // DOCUMENT PROCESSED
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForApprovedPODocuments(this);

            //set purap status and status history and status history note
            SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(this, PurapConstants.PurchaseOrderStatuses.OPEN );
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(this);
        }
        // DOCUMENT DISAPPROVED
        else if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedPODocuments(this);
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(this);
        }
        // DOCUMENT CANCELED
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            // TODO code
        }

    }

    public NodeDetails getNodeDetailEnum(String newNodeName) {
        // no statuses to set means no node details
        return null;
    }
    
}
