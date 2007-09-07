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
package org.kuali.module.purap.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApAccountingLineBase;
import org.kuali.module.purap.bo.PurApItem;

/**
 * This class is the form class for the Purchasing documents.
 */
public class PurchasingFormBase extends PurchasingAccountsPayableFormBase {
    
    private Boolean notOtherDeliveryBuilding = true;
    private Boolean hideDistributeAccounts = true;
    private PurApItem newPurchasingItemLine;

    // *** Note that the following variables do not use camel caps ON PURPOSE, because of how the accounting lines tag uses the accountPrefix
    private Integer accountDistributionnextSourceLineNumber;
    private List<PurApAccountingLine> accountDistributionsourceAccountingLines;
    private PurApAccountingLine accountDistributionnewSourceLine;
    
    /**
     * Constructs a RequisitionForm instance and sets up the appropriately casted document. 
     */
    public PurchasingFormBase() {
        super();
        this.setNewPurchasingItemLine(setupNewPurchasingItemLine());
        newPurchasingItemLine.setItemTypeCode("ITEM");
        
        this.accountDistributionnextSourceLineNumber = new Integer(1);
        setAccountDistributionsourceAccountingLines(new ArrayList());
        this.setAccountDistributionnewSourceLine(setupNewAccountDistributionAccountingLine());
    }
    /**
     * Gets the newPurchasingItemLine attribute. 
     * @return Returns the newPurchasingItemLine.
     */
    public PurApItem getNewPurchasingItemLine() {
        return newPurchasingItemLine;
    }
    /**
     * Sets the newPurchasingItemLine attribute value.
     * @param newPurchasingItemLine The newPurchasingItemLine to set.
     */
    public void setNewPurchasingItemLine(PurApItem newPurchasingItemLine) {
        this.newPurchasingItemLine = newPurchasingItemLine;
    }
    
    public PurApItem getAndResetNewPurchasingItemLine() {
        PurApItem aPurchasingItemLine = getNewPurchasingItemLine();
        setNewPurchasingItemLine(setupNewPurchasingItemLine());
        return aPurchasingItemLine;
    }
    
    /**
     * 
     * This method should be overriden (or see accountingLines for an alternate way of doing this with newInstance)
     * @return
     */
    public PurApItem setupNewPurchasingItemLine() {
        return null;
    }
    
    /**
     * 
     * This method should be overriden (or see accountingLines for an alternate way of doing this with newInstance)
     * @return
     */
    public PurApAccountingLineBase setupNewPurchasingAccountingLine() {
        return null;
    }
    
    /**
     * 
     * This method should be overriden 
     * @return
     */
    public PurApAccountingLineBase setupNewAccountDistributionAccountingLine() {
        return null;
    }
    
    public Boolean getNotOtherDeliveryBuilding() {
        return notOtherDeliveryBuilding;
    }
    public void setNotOtherDeliveryBuilding(Boolean notOtherDeliveryBuilding) {
        this.notOtherDeliveryBuilding = notOtherDeliveryBuilding;
    }

    public Boolean getHideDistributeAccounts() {
        return hideDistributeAccounts;
    }

    public void setHideDistributeAccounts(Boolean hideDistributeAccounts) {
        this.hideDistributeAccounts = hideDistributeAccounts;
    }
    
    public Integer getAccountDistributionnextSourceLineNumber() {
        return accountDistributionnextSourceLineNumber;
    }
    
    public void setAccountDistributionnextSourceLineNumber(Integer accountDistributionnextSourceLineNumber) {
        this.accountDistributionnextSourceLineNumber = accountDistributionnextSourceLineNumber;
    }
    
    public List<PurApAccountingLine> getAccountDistributionsourceAccountingLines() {
        return accountDistributionsourceAccountingLines;
    }
    
    public void setAccountDistributionsourceAccountingLines(List<PurApAccountingLine> accountDistributionAccountingLines) {
        this.accountDistributionsourceAccountingLines = accountDistributionAccountingLines;
    }
    
    public PurApAccountingLine getAccountDistributionnewSourceLine() {
        return accountDistributionnewSourceLine;
    }
    
    public void setAccountDistributionnewSourceLine(PurApAccountingLine accountDistributionnewSourceLine) {
        this.accountDistributionnewSourceLine = accountDistributionnewSourceLine;
    }

    public PurApAccountingLine getAccountDistributionsourceAccountingLine(int index) {
        while (accountDistributionsourceAccountingLines.size() <= index) {
            accountDistributionsourceAccountingLines.add(setupNewAccountDistributionAccountingLine());
        }
        return accountDistributionsourceAccountingLines.get(index);
    }

    /**
     * This implementation sets the sequence number appropriately for the passed in source accounting line using the value that has
     * been stored in the nextSourceLineNumber variable, adds the accounting line to the list that is aggregated by this object, and
     * then handles incrementing the nextSourceLineNumber variable for you.
     * 
     * @see org.kuali.kfs.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
     */
    public void addAccountDistributionsourceAccountingLine(PurApAccountingLine line) {
        line.setSequenceNumber(this.getAccountDistributionnextSourceLineNumber());
        this.accountDistributionsourceAccountingLines.add(line);
        this.accountDistributionnextSourceLineNumber = new Integer(this.getAccountDistributionnextSourceLineNumber().intValue() + 1);
        this.setAccountDistributionnewSourceLine(setupNewAccountDistributionAccountingLine());
    }
//    /**
//     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#getBaselineSourceAccountingLines()
//     */
//    @Override
//    public List getBaselineSourceAccountingLines() {
//        // TODO Auto-generated method stub
//        return getSou;
//    }

}