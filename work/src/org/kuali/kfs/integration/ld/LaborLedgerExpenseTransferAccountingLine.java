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
package org.kuali.kfs.integration.ld;

import java.math.BigDecimal;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * Labor contract methods to accounting line implementations for Expense Transfer Documents.
 */
public interface LaborLedgerExpenseTransferAccountingLine extends AccountingLine, ExternalizableBusinessObject {

    /**
     * Gets the emplid
     * 
     * @return Returns the emplid.
     */
    public String getEmplid();

    /**
     * Gets the laborObject
     * 
     * @return Returns the laborObject.
     */
    public LaborLedgerObject getLaborLedgerObject();

    /**
     * Gets the payrollEndDateFiscalPeriodCode
     * 
     * @return Returns the payrollEndDateFiscalPeriodCode.
     */
    public String getPayrollEndDateFiscalPeriodCode();

    /**
     * Gets the payrollEndDateFiscalYear
     * 
     * @return Returns the payrollEndDateFiscalYear.
     */
    public Integer getPayrollEndDateFiscalYear();

    /**
     * Gets the payrollTotalHours
     * 
     * @return Returns the payrollTotalHours.
     */
    public BigDecimal getPayrollTotalHours();

    /**
     * Gets the positionNumber
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber();

    /**
     * Sets the emplid
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid);

    /**
     * Sets the laborLedgerObject
     * 
     * @param laborLedgerObject The laborLedgerObject to set.
     */
    public void setLaborLedgerObject(LaborLedgerObject laborLedgerObject);

    /**
     * Sets the payrollEndDateFiscalPeriodCode
     * 
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     */
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode);

    /**
     * Sets the payrollEndDateFiscalYear
     * 
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     */
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear);

    /**
     * Sets the payrollTotalHours
     * 
     * @param payrollTotalHours The payrollTotalHours to set.
     */
    public void setPayrollTotalHours(BigDecimal payrollTotalHours);

    /**
     * Sets the positionNumber
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber);
}
