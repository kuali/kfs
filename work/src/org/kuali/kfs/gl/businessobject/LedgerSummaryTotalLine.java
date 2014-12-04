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
package org.kuali.kfs.gl.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Summarizes Pending Entry data for the GLPE pending entry report.
 */
public class LedgerSummaryTotalLine extends TransientBusinessObjectBase {
    private KualiDecimal debitAmount = KualiDecimal.ZERO;
    private int debitCount = 0;
    private KualiDecimal creditAmount = KualiDecimal.ZERO;
    private int creditCount = 0;
    private KualiDecimal budgetAmount = KualiDecimal.ZERO;
    private int budgetCount = 0;
    /**
     * Gets the recordCount attribute. 
     * @return Returns the recordCount.
     */
    public int getRecordCount() {
        return debitCount + creditCount + budgetCount;
    }
    /**
     * Gets the debitAmount attribute. 
     * @return Returns the debitAmount.
     */
    public KualiDecimal getDebitAmount() {
        return debitAmount;
    }
    /**
     * Gets the debitCount attribute. 
     * @return Returns the debitCount.
     */
    public int getDebitCount() {
        return debitCount;
    }
    /**
     * Gets the creditAmount attribute. 
     * @return Returns the creditAmount.
     */
    public KualiDecimal getCreditAmount() {
        return creditAmount;
    }
    /**
     * Gets the creditCount attribute. 
     * @return Returns the creditCount.
     */
    public int getCreditCount() {
        return creditCount;
    }
    /**
     * Gets the budgetAmount attribute. 
     * @return Returns the budgetAmount.
     */
    public KualiDecimal getBudgetAmount() {
        return budgetAmount;
    }
    /**
     * Gets the budgetCount attribute. 
     * @return Returns the budgetCount.
     */
    public int getBudgetCount() {
        return budgetCount;
    }
    
    /**
     * Adds a debit amount to the current debit total
     * @param debitAmount the debit amount to add to the debit total
     */
    public void addDebitAmount(KualiDecimal debitAmount) {
        this.debitAmount = this.debitAmount.add(debitAmount);
        this.debitCount += 1;
    }
    
    /**
     * Adds a credit amount to current credit total  
     * @param creditAmount the amount to add to the credit total
     */
    public void addCreditAmount(KualiDecimal creditAmount) {
        this.creditAmount = this.creditAmount.add(creditAmount);
        this.creditCount += 1;
    }
    
    /**
     * Adds a budget amount to current budget total  
     * @param budgetAmount the amount to add to the budget total
     */
    public void addBudgetAmount(KualiDecimal budgetAmount) {
        this.budgetAmount = this.budgetAmount.add(budgetAmount);
        this.budgetCount += 1;
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return new LinkedHashMap();
    }
    
    /**
     * @return the summary for this summary total line
     */
    public String getSummary() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.MESSAGE_REPORT_NIGHTLY_OUT_LEDGER_TOTAL);
    }
}
