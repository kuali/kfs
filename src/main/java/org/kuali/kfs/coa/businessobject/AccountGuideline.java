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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Account Guideline Business Object
 */
public class AccountGuideline extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 807136405105252199L;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String accountExpenseGuidelineText;
    private String accountIncomeGuidelineText;
    private String accountPurposeText;


    /**
     * @return Returns the accountExpenseGuidelineText.
     */
    public String getAccountExpenseGuidelineText() {
        return accountExpenseGuidelineText;
    }

    /**
     * @param accountExpenseGuidelineText The accountExpenseGuidelineText to set.
     */
    public void setAccountExpenseGuidelineText(String accountExpenseGuidelineText) {
        this.accountExpenseGuidelineText = accountExpenseGuidelineText;
    }

    /**
     * @return Returns the accountIncomeGuidelineText.
     */
    public String getAccountIncomeGuidelineText() {
        return accountIncomeGuidelineText;
    }

    /**
     * @param accountIncomeGuidelineText The accountIncomeGuidelineText to set.
     */
    public void setAccountIncomeGuidelineText(String accountIncomeGuidelineText) {
        this.accountIncomeGuidelineText = accountIncomeGuidelineText;
    }

    /**
     * @return Returns the accountNbr.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNbr The accountNbr to set.
     */
    public void setAccountNumber(String accountNbr) {
        this.accountNumber = accountNbr;
    }

    /**
     * @return Returns the accountPurposeText.
     */
    public String getAccountPurposeText() {
        return accountPurposeText;
    }

    /**
     * @param accountPurposeText The accountPurposeText to set.
     */
    public void setAccountPurposeText(String accountPurposeText) {
        this.accountPurposeText = accountPurposeText;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);

        return m;
    }
}
