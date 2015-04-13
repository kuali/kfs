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
package org.kuali.kfs.module.ld.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferSourceAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Labor business object for special case <code>{@link SourceAccountingLine}</code> type for
 * <code>{@link org.kuali.module.labor.document.ExpenseTransferDocument}</code>
 */
public class ExpenseTransferSourceAccountingLine extends SourceAccountingLine implements ExpenseTransferAccountingLine, LaborLedgerExpenseTransferSourceAccountingLine, Comparable<ExpenseTransferSourceAccountingLine> {
    private String positionNumber;
    private BigDecimal payrollTotalHours;
    private Integer payrollEndDateFiscalYear;
    private String payrollEndDateFiscalPeriodCode;
    private String emplid;
    private LaborObject laborObject;
    private String fringeBenefitView;



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
    @Override
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber.
     *
     * @param positionNumber The positionNumber to set.
     */
    @Override
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the payrollTotalHours.
     *
     * @return Returns the payrollTotalHours
     */
    @Override
    public BigDecimal getPayrollTotalHours() {
        return payrollTotalHours;
    }

    /**
     * Sets the payrollTotalHours.
     *
     * @param payrollTotalHours The payrollTotalHours to set.
     */
    @Override
    public void setPayrollTotalHours(BigDecimal payrollTotalHours) {
        this.payrollTotalHours = payrollTotalHours;
    }

    /**
     * Gets the payrollEndDateFiscalYear.
     *
     * @return Returns the payrollEndDateFiscalYear
     */
    @Override
    public Integer getPayrollEndDateFiscalYear() {
        return payrollEndDateFiscalYear;
    }

    /**
     * Sets the payrollEndDateFiscalYear.
     *
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     */
    @Override
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
        this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
    }

    /**
     * Gets the payrollEndDateFiscalPeriodCode.
     *
     * @return Returns the payrollEndDateFiscalPeriodCode
     */
    @Override
    public String getPayrollEndDateFiscalPeriodCode() {
        return payrollEndDateFiscalPeriodCode;
    }

    /**
     * Sets the payrollEndDateFiscalPeriodCode.
     *
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     */
    @Override
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }

    /**
     * Gets the emplid.
     *
     * @return Returns the emplid
     */
    @Override
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid.
     *
     * @param emplid The emplid to set.
     */
    @Override
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the laborObject.
     *
     * @return Returns the laborObject.
     */
    @Override
    public LaborObject getLaborObject() {
        return laborObject;
    }

    /**
     * Sets the laborObject.
     *
     * @param laborObject The laborObject to set.
     */
    @Override
    @Deprecated
    public void setLaborObject(LaborObject laborObject) {
        this.laborObject = laborObject;
    }

    /**
     * @see org.kuali.kfs.bo.LaborLedgerExpenseTransferAccoutingLine#getLaborLedgerObject()
     */
    @Override
    public LaborLedgerObject getLaborLedgerObject() {
        return this.laborObject;
    }

    /**
     * @see org.kuali.kfs.bo.LaborLedgerExpenseTransferAccoutingLine#setLaborLedgerObject(org.kuali.kfs.bo.LaborLedgerObject)
     */
    @Override
    @Deprecated
    public void setLaborLedgerObject(LaborLedgerObject laborLedgerObject) {
        this.laborObject = (LaborObject) laborLedgerObject;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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
    @Override
    public int compareTo(ExpenseTransferSourceAccountingLine o) {
        if (o == null) {
            throw new NullPointerException("ExpenseTransferAccountingLine is null");
        }
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

    /**
     * This method returns a string so that an fringe benefit inquiry can have a link to view
     * inquiry page from salary transfer.
     *
     * @return the String "View Organization Reversion"
     */
    public String getFringeBenefitView() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.SALARY_TRANSFER_FRINGE_BENEFIT_INQUIRY_LABEL);
    }
}
