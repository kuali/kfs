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

package edu.arizona.kfs.module.purap.document;

import java.math.BigDecimal;
import java.util.List;
import org.kuali.kfs.sys.businessobject.Building;
import edu.arizona.kfs.sys.businessobject.BuildingExtension;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Purchase Order Retransmit Document
 */
public class PurchaseOrderRetransmitDocument extends PurchaseOrderDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderRetransmitDocument.class);

    protected boolean shouldDisplayRetransmitTab;
    protected String routeCode;
    protected Building buildingObj;

    /**
     * Default constructor.
     */
    public PurchaseOrderRetransmitDocument() {
        super();
    }

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
     * Adds up the total amount of the items selected by the user for retransmit, then return the amount.
     *
     * @return KualiDecimal the total amount of the items selected by the user for retransmit.
     */
    public KualiDecimal getTotalDollarAmountForRetransmit() {
        // We should only add up the amount of the items that were selected for retransmit.
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getItems()) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                KualiDecimal totalAmount = item.getTotalAmount();
                KualiDecimal itemTotal = (totalAmount != null) ? totalAmount : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }

        return total;
    }

    public KualiDecimal getTotalPreTaxDollarAmountForRetransmit() {
        // We should only add up the amount of the items that were selected for retransmit.
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getItems()) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                KualiDecimal extendedPrice = item.getExtendedPrice();
                KualiDecimal itemTotal = (extendedPrice != null) ? extendedPrice : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }

        return total;
    }

    public KualiDecimal getTotalTaxDollarAmountForRetransmit() {
        // We should only add up the amount of the items that were selected for retransmit.
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getItems()) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                KualiDecimal taxAmount = item.getItemTaxAmount();
                KualiDecimal itemTotal = (taxAmount != null) ? taxAmount : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }

        return total;
    }

    /**
     * When Purchase Order Retransmit document has been Processed through Workflow, the PO status remains to "OPEN" and the last
     * transmit date is updated.
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
                setPurchaseOrderLastTransmitTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
                updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_OPEN);
            }
            // DOCUMENT DISAPPROVED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isDisapproved()) {
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(this);

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
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledChangePODocuments(this);
                // for app doc status
                updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_CANCELLED);
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    public boolean isShouldDisplayRetransmitTab() {
        return shouldDisplayRetransmitTab;
    }

    public void setShouldDisplayRetransmitTab(boolean shouldDisplayRetransmitTab) {
        this.shouldDisplayRetransmitTab = shouldDisplayRetransmitTab;
    }

    @Override
    protected boolean shouldAdhocFyi() {
        return false;
    }
    
    /**
     * Gets the routeCodeObj attribute. 
     * @return Returns the routeCodeObj.
     */
    public Building getBuildingObj() {
        return buildingObj;
    }

    /**
     * Sets the routeCodeObj attribute value.
     * @param routeCodeObj The routeCodeObj to set.
     */
    public void setBuildingObj(Building buildingObj) {
        this.buildingObj = buildingObj;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#getRouteCode()
     */
    public String getRouteCode() {
    	
    	try{
    		BuildingExtension be = (BuildingExtension)(buildingObj.getExtension());
    		routeCode = be.getRouteCode();
    	}catch(Exception e){
    		LOG.debug("Routing Code was not Retrieved");
    	}
    	
        return routeCode;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingDocument#setRouteCode(java.lang.String)
     */
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }
}
