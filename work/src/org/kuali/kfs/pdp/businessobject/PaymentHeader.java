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
package org.kuali.kfs.pdp.businessobject;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;

/*
 * This is a simple java bean class created for
 * Research Participant Upload. It
 * represents the entire Payment Header row, which is
 * part of the format that will appear in the spreadsheet
 * from the Accounting Office.
 */
public class PaymentHeader extends TimestampedBusinessObjectBase {

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
