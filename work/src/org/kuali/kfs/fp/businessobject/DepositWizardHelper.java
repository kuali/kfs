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
package org.kuali.kfs.fp.businessobject;

import java.sql.Date;

import org.kuali.kfs.sys.KFSConstants;

/**
 * This helper class works in conjunction with the DepositWizardForm to help build the UI for the Deposit Wizard.
 */
public class DepositWizardHelper {
    private String selectedValue;
    private Date cashReceiptCreateDate;

    /**
     * Constructs a JournalVoucherAccountingLineHelper.java.
     */
    public DepositWizardHelper() {
        selectedValue = KFSConstants.ParameterValues.NO;
        cashReceiptCreateDate = new Date(0);
    }

    /**
     * Gets the selectedValue attribute.
     * 
     * @return Returns the selectedValue.
     */
    public String getSelectedValue() {
        return selectedValue;
    }

    /**
     * Sets the selectedValue attribute value.
     * 
     * @param selectedValue The selectedValue to set.
     */
    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    /**
     * @return current value of cashReceiptCreateDate.
     */
    public Date getCashReceiptCreateDate() {
        return cashReceiptCreateDate;
    }

    /**
     * Sets the cashReceiptCreateDate attribute value.
     * 
     * @param cashReceiptCreateDate The cashReceiptCreateDate to set.
     */
    public void setCashReceiptCreateDate(Date cashReceiptCreateDate) {
        this.cashReceiptCreateDate = cashReceiptCreateDate;
    }
}
