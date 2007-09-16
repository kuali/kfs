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
import org.kuali.module.purap.service.AccountsPayableDocumentSpecificService;



/**
 * Accounts Payable Document Interface
 * 
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
    
    public boolean requiresAccountsPayableReviewRouting();
    
    public boolean approvalAtAccountsPayableReviewAllowed();

    public boolean isUnmatchedOverride();
        
    public void setUnmatchedOverride(boolean unmatchedOverride);
            
    public KualiDecimal getGrandTotal();
    
    public KualiDecimal getInitialAmount();

    public boolean isContinuationAccountIndicator();

    public void setContinuationAccountIndicator(boolean continuationAccountIndicator);
    
    public boolean isExtracted();
    
    public abstract AccountsPayableDocumentSpecificService getDocumentSpecificService();

}
