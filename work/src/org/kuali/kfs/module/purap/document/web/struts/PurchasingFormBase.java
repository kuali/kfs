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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApAccountingLineBase;
import org.kuali.module.purap.bo.PurApItem;

/**
 * Struts Action Form for Purchasing documents.
 */
public class PurchasingFormBase extends PurchasingAccountsPayableFormBase {

    private Boolean notOtherDeliveryBuilding = true;
    private Boolean hideDistributeAccounts = true;
    private PurApItem newPurchasingItemLine;
    private FormFile itemImportFile; // file from which items can be imported

    // *** Note that the following variables do not use camel caps ON PURPOSE, because of how the accounting lines tag uses the
    // accountPrefix
    private Integer accountDistributionnextSourceLineNumber;
    private List<PurApAccountingLine> accountDistributionsourceAccountingLines;
    private PurApAccountingLine accountDistributionnewSourceLine;
    
    private BigDecimal totalPercentageOfAccountDistributionsourceAccountingLines;
    
    /**
     * Constructs a PurchasingFormBase instance and sets up the appropriately casted document.
     */
    public PurchasingFormBase() {
        super();
        this.setNewPurchasingItemLine(setupNewPurchasingItemLine());
        newPurchasingItemLine.setItemTypeCode("ITEM");

        this.accountDistributionnextSourceLineNumber = new Integer(1);
        setAccountDistributionsourceAccountingLines(new ArrayList());
        this.setAccountDistributionnewSourceLine(setupNewAccountDistributionAccountingLine());
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

    public BigDecimal getTotalPercentageOfAccountDistributionsourceAccountingLines() {
        if (this.totalPercentageOfAccountDistributionsourceAccountingLines == null) {
            this.totalPercentageOfAccountDistributionsourceAccountingLines = new BigDecimal(0);
            for (PurApAccountingLine line : this.getAccountDistributionsourceAccountingLines()) {
                if (line.getAccountLinePercent() != null) {
                    setTotalPercentageOfAccountDistributionsourceAccountingLines(this.totalPercentageOfAccountDistributionsourceAccountingLines.add(line.getAccountLinePercent()));
                }
            }
        }
        return this.totalPercentageOfAccountDistributionsourceAccountingLines;
    }
    
    public void setTotalPercentageOfAccountDistributionsourceAccountingLines(BigDecimal total) {
        this.totalPercentageOfAccountDistributionsourceAccountingLines = total;
    }
    
    public PurApAccountingLine getAccountDistributionnewSourceLine() {
        return accountDistributionnewSourceLine;
    }

    public void setAccountDistributionnewSourceLine(PurApAccountingLine accountDistributionnewSourceLine) {
        this.accountDistributionnewSourceLine = accountDistributionnewSourceLine;
    }

    public PurApItem getNewPurchasingItemLine() {
        return newPurchasingItemLine;
    }

    public void setNewPurchasingItemLine(PurApItem newPurchasingItemLine) {
        this.newPurchasingItemLine = newPurchasingItemLine;
    }
    
    public FormFile getItemImportFile() {
        return itemImportFile;
    }

    public void setItemImportFile(FormFile itemImportFile) {
        this.itemImportFile = itemImportFile;
    }

    /**
     * Returns the Account Distribution Source Accounting Line at the specified index.
     * 
     * @param index the index of the Account Distribution Source Accounting Line.
     * @return the specified Account Distribution Source Accounting Line.
     */
    public PurApAccountingLine getAccountDistributionsourceAccountingLine(int index) {
        while (accountDistributionsourceAccountingLines.size() <= index) {
            accountDistributionsourceAccountingLines.add(setupNewAccountDistributionAccountingLine());
        }
        return accountDistributionsourceAccountingLines.get(index);
    }

    /**
     * Returns the new Purchasing Item Line and resets it to null.
     * 
     * @return the new Purchasing Item Line.
     */
    public PurApItem getAndResetNewPurchasingItemLine() {
        PurApItem aPurchasingItemLine = getNewPurchasingItemLine();
        setNewPurchasingItemLine(setupNewPurchasingItemLine());
        return aPurchasingItemLine;
    }

    /**
     * This method should be overriden (or see accountingLines for an alternate way of doing this with newInstance)
     */
    public PurApItem setupNewPurchasingItemLine() {
        return null;
    }

    /**
     * This method should be overriden (or see accountingLines for an alternate way of doing this with newInstance)
     */
    public PurApAccountingLineBase setupNewPurchasingAccountingLine() {
        return null;
    }

    /**
     * This method should be overriden.
     */
    public PurApAccountingLineBase setupNewAccountDistributionAccountingLine() {
        return null;
    }

    /**
     * Sets the sequence number appropriately for the passed in source accounting line using the value that has been stored in the
     * nextSourceLineNumber variable, adds the accounting line to the list that is aggregated by this object, and then handles
     * incrementing the nextSourceLineNumber variable.
     * 
     * @param line the accounting line to add to the list.
     * @see org.kuali.kfs.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
     */
    public void addAccountDistributionsourceAccountingLine(PurApAccountingLine line) {
        line.setSequenceNumber(this.getAccountDistributionnextSourceLineNumber());
        this.accountDistributionsourceAccountingLines.add(line);
        this.accountDistributionnextSourceLineNumber = new Integer(this.getAccountDistributionnextSourceLineNumber().intValue() + 1);
        this.setAccountDistributionnewSourceLine(setupNewAccountDistributionAccountingLine());
    }

}