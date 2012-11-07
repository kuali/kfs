/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject;

import java.math.BigDecimal;

import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferTargetAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Labor business object for special case <code>{@link TargetAccountingLine}</code> type for
 * <code>{@link org.kuali.module.labor.document.ExpenseTransferDocument}</code>
 */
public class ExpenseTransferTargetAccountingLine extends TargetAccountingLine implements ExpenseTransferAccountingLine, LaborLedgerExpenseTransferTargetAccountingLine, Comparable<ExpenseTransferTargetAccountingLine> {
    private String positionNumber;
    private BigDecimal payrollTotalHours;
    private Integer payrollEndDateFiscalYear;
    private String payrollEndDateFiscalPeriodCode;
    private String emplid;
    private LaborObject laborObject;
    private String fringeBenefitView;

    /**
     * Constructs a ExpenseTransferTargetAccountingLine.java.
     */
    public ExpenseTransferTargetAccountingLine() {
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
     * Used to for sorting <code>{@link ExpenseTransferAccountingLine}</code> instances within a java
     * <code>{@link java.util.Collection}</code> by payrollEndDateFisdalYear and payrollEndDateFiscalPeriodCode
     *
     * @see java.lang.Comparable#compareTo(T)
     */
    @Override
    public int compareTo(ExpenseTransferTargetAccountingLine o) {
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
