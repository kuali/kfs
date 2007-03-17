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

import org.kuali.PropertyConstants;
import org.kuali.kfs.bo.TargetAccountingLine;


/**
 * Special case <code>{@link TargetAccountingLine}</code> type for
 * <code>{@link org.kuali.module.labor.document.ExpenseTransferDocument}</code>
 * 
 * 
 */
public class ExpenseTransferTargetAccountingLine extends TargetAccountingLine implements ExpenseTransferAccountingLine {

    private String positionNumber;
    private BigDecimal payrollTotalHours;    
    private Integer payrollEndDateFiscalYear;
    private String payrollEndDateFiscalPeriodCode;
    private String emplid;

    private LaborObject laborObject;

    /**
     * This constructor needs to initialize the ojbConcreteClass attribute such that it sets it to its class name. This is how OJB
     * knows what grouping of objects to work with.
     */
    public ExpenseTransferTargetAccountingLine() {
        super();
        super.ojbConcreteClass = this.getClass().getName();
    }

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     * 
     */
    public String getPositionNumber() { 
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     * 
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the payrollTotalHours attribute.
     * 
     * @return Returns the payrollTotalHours
     * 
     */
    public BigDecimal getPayrollTotalHours() { 
        return payrollTotalHours;
    }

    /**
     * Sets the payrollTotalHours attribute.
     * 
     * @param payrollTotalHours The payrollTotalHours to set.
     * 
     */
    public void setPayrollTotalHours(BigDecimal payrollTotalHours) {
        this.payrollTotalHours = payrollTotalHours;
    }

    /**
     * Gets the payrollEndDateFiscalYear attribute.
     * 
     * @return Returns the payrollEndDateFiscalYear
     * 
     */
    public Integer getPayrollEndDateFiscalYear() { 
        return payrollEndDateFiscalYear;
    }

    /**
     * Sets the payrollEndDateFiscalYear attribute.
     * 
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     * 
     */
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
        this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
    }

    /**
     * Gets the payrollEndDateFiscalPeriodCode attribute.
     * 
     * @return Returns the payrollEndDateFiscalPeriodCode
     * 
     */
    public String getPayrollEndDateFiscalPeriodCode() { 
        return payrollEndDateFiscalPeriodCode;
    }

    /**
     * Sets the payrollEndDateFiscalPeriodCode attribute.
     * 
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     * 
     */
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
        this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     * 
     */
    public String getEmplid() { 
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     * 
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the laborObject attribute. 
     * @return Returns the laborObject.
     */
    public LaborObject getLaborObject() {
        return laborObject;
    }

    /**
     * Sets the laborObject attribute value.
     * @param laborObject The laborObject to set.
     */
    @Deprecated
    public void setLaborObject(LaborObject laborObject) {
        this.laborObject = laborObject;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        if (this.getSequenceNumber() != null) {
            m.put("sequenceNumber", this.getSequenceNumber().toString());
        }
        return m;
    }
     
}
