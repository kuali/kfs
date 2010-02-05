/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
