/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject;

import java.sql.Date;
import java.sql.Timestamp;

/*
 * This is a simple java bean class created for
 * Research Participant Upload. It
 * represents the entire Payment Header row, which is
 * part of the format that will appear in the spreadsheet
 * from the Accounting Office.
 */
public class PaymentHeader extends Batch {

    private String chartOfAccountsCode;
    private String unit;
    private String subUnit;
    private Timestamp creationDate;
    private String vendorOrEmployee;
    private String sourceDocNumber;
    private Date paymentDate;

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getSubUnit() {
        return subUnit;
    }
    public void setSubUnit(String subUnit) {
        this.subUnit = subUnit;
    }
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
    public String getVendorOrEmployee() {
        return vendorOrEmployee;
    }
    public void setVendorOrEmployee(String vendorOrEmployee) {
        this.vendorOrEmployee = vendorOrEmployee;
    }
    public String getSourceDocNumber() {
        return sourceDocNumber;
    }
    public void setSourceDocNumber(String sourceDocNumber) {
        this.sourceDocNumber = sourceDocNumber;
    }
    public Date getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }


}
