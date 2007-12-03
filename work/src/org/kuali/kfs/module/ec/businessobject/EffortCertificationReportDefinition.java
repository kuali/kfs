/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.effort.bo;

import java.sql.Date;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * Business Object for the Effort Certification Report Definition Table.
 */
public class EffortCertificationReportDefinition extends PersistableBusinessObjectBase {
    private String a21LaborReportNumber;
    private String a21LaborReportPeriodTitle;
    private String a21LaborReportPeriodStatusCode;
    private Integer a21LaborExpenseTransferFiscalYear;
    private String a21LaborExpenseTransferFiscalPeriodCode;
    private String a21LaborReportTypeCode;
    private Integer a21LaborReportFiscalYear;
    private Date a21LaborReportReturnDate;
    private Integer a21LaborReportBeginFiscalYear;
    private String a21LaborReportBeginPeriodCode;
    private Integer a21LaborReportEndFiscalYear;
    private String a21LaborReportEndPeriodCode;

    private AccountingPeriod a21LaborExpenseTransferFiscalPeriod;
    private EffortCertificationPeriodStatusCode reportPeriodStatus;
    private EffortCertificationReportType a21LaborReportType;
    private Collection<EffortCertificationReportPosition> effortCertificationReportPositions;

    /**
     * Default constructor.
     */
    public EffortCertificationReportDefinition() {

    }

    /**
     * Gets the a21LaborReportNumber attribute.
     * 
     * @return Returns the a21LaborReportNumber
     */
    public String getA21LaborReportNumber() {
        return a21LaborReportNumber;
    }

    /**
     * Sets the a21LaborReportNumber attribute.
     * 
     * @param a21LaborReportNumber The a21LaborReportNumber to set.
     */
    public void setA21LaborReportNumber(String a21LaborReportNumber) {
        this.a21LaborReportNumber = a21LaborReportNumber;
    }

    /**
     * Gets the a21LaborReportPeriodTitle attribute.
     * 
     * @return Returns the a21LaborReportPeriodTitle
     */
    public String getA21LaborReportPeriodTitle() {
        return a21LaborReportPeriodTitle;
    }

    /**
     * Sets the a21LaborReportPeriodTitle attribute.
     * 
     * @param a21LaborReportPeriodTitle The a21LaborReportPeriodTitle to set.
     */
    public void setA21LaborReportPeriodTitle(String a21LaborReportPeriodTitle) {
        this.a21LaborReportPeriodTitle = a21LaborReportPeriodTitle;
    }


    /**
     * Gets the a21LaborReportPeriodStatusCode attribute.
     * 
     * @return Returns the a21LaborReportPeriodStatusCode
     */
    public String getA21LaborReportPeriodStatusCode() {
        return a21LaborReportPeriodStatusCode;
    }

    /**
     * Sets the a21LaborReportPeriodStatusCode attribute.
     * 
     * @param a21LaborReportPeriodStatusCode The a21LaborReportPeriodStatusCode to set.
     */
    public void setA21LaborReportPeriodStatusCode(String a21LaborReportPeriodStatusCode) {
        this.a21LaborReportPeriodStatusCode = a21LaborReportPeriodStatusCode;
    }

    /**
     * Gets the a21LaborExpenseTransferFiscalYear attribute.
     * 
     * @return Returns the a21LaborExpenseTransferFiscalYear
     */
    public Integer getA21LaborExpenseTransferFiscalYear() {
        return a21LaborExpenseTransferFiscalYear;
    }

    /**
     * Sets the a21LaborExpenseTransferFiscalYear attribute.
     * 
     * @param a21LaborExpenseTransferFiscalYear The a21LaborExpenseTransferFiscalYear to set.
     */
    public void setA21LaborExpenseTransferFiscalYear(Integer a21LaborExpenseTransferFiscalYear) {
        this.a21LaborExpenseTransferFiscalYear = a21LaborExpenseTransferFiscalYear;
    }


    /**
     * Gets the a21LaborExpenseTransferFiscalPeriodCode attribute.
     * 
     * @return Returns the a21LaborExpenseTransferFiscalPeriodCode
     */
    public String getA21LaborExpenseTransferFiscalPeriodCode() {
        return a21LaborExpenseTransferFiscalPeriodCode;
    }

    /**
     * Sets the a21LaborExpenseTransferFiscalPeriodCode attribute.
     * 
     * @param a21LaborExpenseTransferFiscalPeriodCode The a21LaborExpenseTransferFiscalPeriodCode to set.
     */
    public void setA21LaborExpenseTransferFiscalPeriodCode(String a21LaborExpenseTransferFiscalPeriodCode) {
        this.a21LaborExpenseTransferFiscalPeriodCode = a21LaborExpenseTransferFiscalPeriodCode;
    }


    /**
     * Gets the a21LaborReportTypeCode attribute.
     * 
     * @return Returns the a21LaborReportTypeCode
     */
    public String getA21LaborReportTypeCode() {
        return a21LaborReportTypeCode;
    }

    /**
     * Sets the a21LaborReportTypeCode attribute.
     * 
     * @param a21LaborReportTypeCode The a21LaborReportTypeCode to set.
     */
    public void setA21LaborReportTypeCode(String a21LaborReportTypeCode) {
        this.a21LaborReportTypeCode = a21LaborReportTypeCode;
    }


    /**
     * Gets the a21LaborReportFiscalYear attribute.
     * 
     * @return Returns the a21LaborReportFiscalYear
     */
    public Integer getA21LaborReportFiscalYear() {
        return a21LaborReportFiscalYear;
    }

    /**
     * Sets the a21LaborReportFiscalYear attribute.
     * 
     * @param a21LaborReportFiscalYear The a21LaborReportFiscalYear to set.
     */
    public void setA21LaborReportFiscalYear(Integer a21LaborReportFiscalYear) {
        this.a21LaborReportFiscalYear = a21LaborReportFiscalYear;
    }


    /**
     * Gets the a21LaborReportReturnDate attribute.
     * 
     * @return Returns the a21LaborReportReturnDate
     */
    public Date getA21LaborReportReturnDate() {
        return a21LaborReportReturnDate;
    }

    /**
     * Sets the a21LaborReportReturnDate attribute.
     * 
     * @param a21LaborReportReturnDate The a21LaborReportReturnDate to set.
     */
    public void setA21LaborReportReturnDate(Date a21LaborReportReturnDate) {
        this.a21LaborReportReturnDate = a21LaborReportReturnDate;
    }

    /**
     * Gets the a21LaborExpenseTransferFiscalPeriod attribute.
     * 
     * @return Returns the a21LaborExpenseTransferFiscalPeriod.
     */
    public AccountingPeriod getA21LaborExpenseTransferFiscalPeriod() {
        return a21LaborExpenseTransferFiscalPeriod;
    }

    /**
     * Sets the a21LaborExpenseTransferFiscalPeriod attribute value.
     * 
     * @param laborExpenseTransferFiscalPeriod The a21LaborExpenseTransferFiscalPeriod to set.
     */
    @Deprecated
    public void setA21LaborExpenseTransferFiscalPeriod(AccountingPeriod laborExpenseTransferFiscalPeriod) {
        a21LaborExpenseTransferFiscalPeriod = laborExpenseTransferFiscalPeriod;
    }

    /**
     * Gets the reportPeriodStatus attribute.
     * 
     * @return Returns the reportPeriodStatus.
     */
    public EffortCertificationPeriodStatusCode getReportPeriodStatus() {
        return reportPeriodStatus;
    }

    /**
     * Sets the reportPeriodStatus attribute value.
     * 
     * @param reportPeriodStatus The reportPeriodStatus to set.
     */
    @Deprecated
    public void setReportPeriodStatus(EffortCertificationPeriodStatusCode reportPeriodStatus) {
        this.reportPeriodStatus = reportPeriodStatus;
    }

    /**
     * Gets the a21LaborReportType attribute.
     * 
     * @return Returns the a21LaborReportType.
     */
    public EffortCertificationReportType getA21LaborReportType() {
        return a21LaborReportType;
    }

    /**
     * Sets the a21LaborReportType attribute value.
     * 
     * @param laborReportType The a21LaborReportType to set.
     */
    @Deprecated
    public void setA21LaborReportType(EffortCertificationReportType laborReportType) {
        a21LaborReportType = laborReportType;
    }


    /**
     * Gets the a21LaborReportBeginFiscalYear attribute.
     * 
     * @return Returns the a21LaborReportBeginFiscalYear.
     */
    public Integer getA21LaborReportBeginFiscalYear() {
        return a21LaborReportBeginFiscalYear;
    }

    /**
     * Sets the a21LaborReportBeginFiscalYear attribute value.
     * 
     * @param laborReportBeginFiscalYear The a21LaborReportBeginFiscalYear to set.
     */
    public void setA21LaborReportBeginFiscalYear(Integer laborReportBeginFiscalYear) {
        a21LaborReportBeginFiscalYear = laborReportBeginFiscalYear;
    }

    /**
     * Gets the a21LaborReportBeginPeriodCode attribute.
     * 
     * @return Returns the a21LaborReportBeginPeriodCode.
     */
    public String getA21LaborReportBeginPeriodCode() {
        return a21LaborReportBeginPeriodCode;
    }

    /**
     * Sets the a21LaborReportBeginPeriodCode attribute value.
     * 
     * @param laborReportBeginPeriodCode The a21LaborReportBeginPeriodCode to set.
     */
    public void setA21LaborReportBeginPeriodCode(String laborReportBeginPeriodCode) {
        a21LaborReportBeginPeriodCode = laborReportBeginPeriodCode;
    }

    /**
     * Gets the a21LaborReportEndFiscalYear attribute.
     * 
     * @return Returns the a21LaborReportEndFiscalYear.
     */
    public Integer getA21LaborReportEndFiscalYear() {
        return a21LaborReportEndFiscalYear;
    }

    /**
     * Sets the a21LaborReportEndFiscalYear attribute value.
     * 
     * @param laborReportEndFiscalYear The a21LaborReportEndFiscalYear to set.
     */
    public void setA21LaborReportEndFiscalYear(Integer laborReportEndFiscalYear) {
        a21LaborReportEndFiscalYear = laborReportEndFiscalYear;
    }

    /**
     * Gets the a21LaborReportEndPeriodCode attribute.
     * 
     * @return Returns the a21LaborReportEndPeriodCode.
     */
    public String getA21LaborReportEndPeriodCode() {
        return a21LaborReportEndPeriodCode;
    }

    /**
     * Sets the a21LaborReportEndPeriodCode attribute value.
     * 
     * @param laborReportEndPeriodCode The a21LaborReportEndPeriodCode to set.
     */
    public void setA21LaborReportEndPeriodCode(String laborReportEndPeriodCode) {
        a21LaborReportEndPeriodCode = laborReportEndPeriodCode;
    }

    /**
     * Gets the effortCertificationReportPositions attribute.
     * 
     * @return Returns the effortCertificationReportPositions.
     */
    public Collection<EffortCertificationReportPosition> getEffortCertificationReportPositions() {
        return effortCertificationReportPositions;
    }

    /**
     * Sets the effortCertificationReportPositions attribute value.
     * 
     * @param effortCertificationReportPositions The effortCertificationReportPositions to set.
     */
    public void setEffortCertificationReportPositions(Collection<EffortCertificationReportPosition> effortCertificationReportPostions) {
        this.effortCertificationReportPositions = effortCertificationReportPostions;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("a21LaborReportNumber", this.a21LaborReportNumber);
        return m;
    }

}
