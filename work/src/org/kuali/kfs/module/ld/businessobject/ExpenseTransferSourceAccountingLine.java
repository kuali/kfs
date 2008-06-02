/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.integration.bo.LaborLedgerObject;

/**
 * Labor business object for special case <code>{@link SourceAccountingLine}</code> type for
 * <code>{@link org.kuali.module.labor.document.ExpenseTransferDocument}</code>
 */
public class ExpenseTransferSourceAccountingLine extends SourceAccountingLine implements ExpenseTransferAccountingLine, Comparable<ExpenseTransferSourceAccountingLine> {
    private String positionNumber;
    private BigDecimal payrollTotalHours;
    private Integer payrollEndDateFiscalYear;
    private String payrollEndDateFiscalPeriodCode;
    private String emplid;
    private LaborObject laborObject;
    
    /**
     * Constructs a ExpenseTransferSourceAccountingLine.java.
     */
    public ExpenseTransferSourceAccountingLine() {
        super();
        payrollTotalHours = new BigDecimal(0);
    }

    /**
     * Gets the positionNumber.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the payrollTotalHours.
     * 
     * @return Returns the payrollTotalHours
     */
    public BigDecimal getPayrollTotalHours() {
        return payrollTotalHours;
    }

    /**
     * Sets the payrollTotalHours.
     * 
     * @param payrollTotalHours The payrollTotalHours to set.
     */
    public void setPayrollTotalHours(BigDecimal payrollTotalHours) {
        this.payrollTotalHours = payrollTotalHours;
    }

    /**
     * Gets the payrollEndDateFiscalYear.
     * 
     * @return Returns the payrollEndDateFiscalYear
     */
    public Integer getPayrollEndDateFiscalYear() {
        return payrollEndDateFiscalYear;
    }

    /**
     * Sets the payrollEndDateFiscalYear.
     * 
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     */
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
        this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
    }

    /**
     * Gets the payrollEndDateFiscalPeriodCode.
     * 
     * @return Returns the payrollEndDateFiscalPeriodCode
     */
    public String getPayrollEndDateFiscalPeriodCode() {
        return payrollEndDateFiscalPeriodCode;
    }

    /**
     * Sets the payrollEndDateFiscalPeriodCode.
     * 
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     */
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }

    /**
     * Gets the emplid.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the laborObject.
     * 
     * @return Returns the laborObject.
     */
    public LaborObject getLaborObject() {
        return laborObject;
    }

    /**
     * Sets the laborObject.
     * 
     * @param laborObject The laborObject to set.
     */
    @Deprecated
    public void setLaborObject(LaborObject laborObject) {
        this.laborObject = laborObject;
    }
    
    /**
     * @see org.kuali.kfs.bo.LaborLedgerExpenseTransferAccoutingLine#getLaborLedgerObject()
     */
    public LaborLedgerObject getLaborLedgerObject() {
        return this.laborObject;
    }

    /**
     * @see org.kuali.kfs.bo.LaborLedgerExpenseTransferAccoutingLine#setLaborLedgerObject(org.kuali.kfs.bo.LaborLedgerObject)
     */
    @Deprecated
    public void setLaborLedgerObject(LaborLedgerObject laborLedgerObject) {
        this.laborObject = (LaborObject) laborLedgerObject;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        if (this.getSequenceNumber() != null) {
            m.put("sequenceNumber", this.getSequenceNumber().toString());
        }

        return m;
    }

    /**
     * Used to for sorting <code>{@link ExpenseTransferAccountingLine}</code> instances within a java
     * <code>{@link java.util.Collection}</code> by payrollEndDateFisdalYear and payrollEndDateFiscalPeriodCode
     * 
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(ExpenseTransferSourceAccountingLine o) {
        if (o == null)
            throw new NullPointerException("ExpenseTransferAccountingLine is null");
        int retval = 0;
        retval = getPayrollEndDateFiscalYear().compareTo(o.getPayrollEndDateFiscalYear());

        if (retval == 0) {
            retval = new Integer(getPayrollEndDateFiscalPeriodCode()).compareTo(new Integer(o.getPayrollEndDateFiscalPeriodCode()));
        }

        return retval;
    }

    /**
     * Used to copy a particular transaction line into another This method...
     * 
     * @param from
     */
    public void copyFrom(ExpenseTransferAccountingLine from) {
        super.copyFrom(from);
        this.setPayrollTotalHours(from.getPayrollTotalHours());
        this.setPositionNumber(from.getPositionNumber());
        this.setPayrollEndDateFiscalYear(from.getPayrollEndDateFiscalYear());
        this.setPayrollEndDateFiscalPeriodCode(from.getPayrollEndDateFiscalPeriodCode());
        this.setEmplid(from.getEmplid());
    }
}