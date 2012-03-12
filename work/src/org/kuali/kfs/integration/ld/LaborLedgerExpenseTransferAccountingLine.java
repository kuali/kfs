/*
 * Copyright 2008 The Kuali Foundation
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
