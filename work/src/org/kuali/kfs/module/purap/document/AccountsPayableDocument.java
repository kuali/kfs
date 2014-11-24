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

import java.sql.Timestamp;

import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Accounts Payable Document Interface
 */
public interface AccountsPayableDocument extends PurchasingAccountsPayableDocument {

    public Integer getPurchaseOrderIdentifier();

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier);

    public String getAccountsPayableProcessorIdentifier();

    public void setAccountsPayableProcessorIdentifier(String accountsPayableProcessorIdentifier);

    public String getLastActionPerformedByPersonId();

    public void setLastActionPerformedByPersonId(String lastActionPerformedByPersonId);

    public String getProcessingCampusCode();

    public void setProcessingCampusCode(String processingCampusCode);

    public Timestamp getAccountsPayableApprovalTimestamp();

    public void setAccountsPayableApprovalTimestamp(Timestamp accountsPayableApprovalTimestamp);

    public Timestamp getExtractedTimestamp();

    public void setExtractedTimestamp(Timestamp extractedTimestamp);

    public boolean isHoldIndicator();

    public void setHoldIndicator(boolean holdIndicator);

    public String getNoteLine1Text();

    public void setNoteLine1Text(String noteLine1Text);

    public String getNoteLine2Text();

    public void setNoteLine2Text(String noteLine2Text);

    public String getNoteLine3Text();

    public void setNoteLine3Text(String noteLine3Text);

    public CampusParameter getProcessingCampus();

    public PurchaseOrderDocument getPurchaseOrderDocument();

    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument);

    /**
     * Determines if review route node is required.
     * 
     * @return - true if review is required, false otherwise.
     */
    public boolean requiresAccountsPayableReviewRouting();

    /**
     * Determines if approval is an option during review.
     * 
     * @return - true if approval is available during review, false otherwise.
     */
    public boolean approvalAtAccountsPayableReviewAllowed();

    public boolean isUnmatchedOverride();

    public void setUnmatchedOverride(boolean unmatchedOverride);

    /**
     * Retrieves grand total amount for document.
     * 
     * @return - grand total
     */
    public KualiDecimal getGrandTotal();

    /**
     * 
     * This method returns the amount of tax to remit to the vendor
     * @return total of document tax to remit to the vendor null if none
     */
    public KualiDecimal getTotalRemitTax();
    
    /**
     * Returns the amount entered on the initial screen.
     * 
     * @return - amount entered by user on initial screen
     */
    public KualiDecimal getInitialAmount();

    public boolean isContinuationAccountIndicator();

    public void setContinuationAccountIndicator(boolean continuationAccountIndicator);

    /**
     * Determines if document has been extracted.
     * 
     * @return - true if document has been extracted, false otherwise.
     */
    public boolean isExtracted();

    public AccountsPayableItem getAPItemFromPOItem(PurchaseOrderItem poi);

    public abstract AccountsPayableDocumentSpecificService getDocumentSpecificService();
    
    public abstract boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, PurApItemUseTax offsetUseTax); 

}

