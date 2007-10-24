/*
 * Copyright 2006 The Kuali Foundation.
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

import java.sql.Date;

import org.kuali.core.bo.Campus;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.AccountsPayableItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.service.AccountsPayableDocumentSpecificService;

/**
 * Accounts Payable Document Interface
 */
public interface AccountsPayableDocument extends PurchasingAccountsPayableDocument {

    public Integer getPurchaseOrderIdentifier();

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier);

    public String getAccountsPayableProcessorIdentifier();

    public void setAccountsPayableProcessorIdentifier(String accountsPayableProcessorIdentifier);

    public String getLastActionPerformedByUniversalUserId();

    public void setLastActionPerformedByUniversalUserId(String lastActionPerformedByUniversalUserId);

    public String getProcessingCampusCode();

    public void setProcessingCampusCode(String processingCampusCode);
    
    public Date getAccountsPayableApprovalDate();

    public void setAccountsPayableApprovalDate(Date accountsPayableApprovalDate);

    public Date getExtractedDate();

    public void setExtractedDate(Date extractedDate);

    public boolean isHoldIndicator();

    public void setHoldIndicator(boolean holdIndicator);

    public String getNoteLine1Text();

    public void setNoteLine1Text(String noteLine1Text);

    public String getNoteLine2Text();

    public void setNoteLine2Text(String noteLine2Text);

    public String getNoteLine3Text();

    public void setNoteLine3Text(String noteLine3Text);

    public Campus getProcessingCampus();

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

}
