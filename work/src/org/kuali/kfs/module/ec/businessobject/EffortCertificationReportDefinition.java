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
import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * Business Object for the Effort Certification Report Definition Table.
 */
public class EffortCertificationReportDefinition extends PersistableBusinessObjectBase {
    
    private Integer universityFiscalYear;
    private String effortCertificationReportNumber;
    private String effortCertificationReportPeriodTitle;
    private String effortCertificationReportPeriodStatusCode;
    private Integer expenseTransferFiscalYear;
    private String expenseTransferFiscalPeriodCode;
    private String effortCertificationReportTypeCode;
    private Date effortCertificationReportReturnDate;
    private Integer effortCertificationReportBeginFiscalYear;
    private String effortCertificationReportBeginPeriodCode;
    private Integer effortCertificationReportEndFiscalYear;
    private String effortCertificationReportEndPeriodCode;
    private boolean active;
    
    private Options options;
    private AccountingPeriod expenseTransferFiscalPeriod;
    private EffortCertificationPeriodStatusCode effortCertificationPeriodStatusCode;
    private EffortCertificationReportType effortCertificationReportType;
    private Collection<EffortCertificationReportPosition> effortCertificationReportPositions;

    /**
     * Constructs a EffortCertificationReportDefinition.java.
     */
    public EffortCertificationReportDefinition() {
        super();
    }
    
    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the effortCertificationReportNumber attribute. 
     * @return Returns the effortCertificationReportNumber.
     */
    public String getEffortCertificationReportNumber() {
        return effortCertificationReportNumber;
    }

    /**
     * Sets the effortCertificationReportNumber attribute value.
     * @param effortCertificationReportNumber The effortCertificationReportNumber to set.
     */
    public void setEffortCertificationReportNumber(String effortCertificationReportNumber) {
        this.effortCertificationReportNumber = effortCertificationReportNumber;
    }

    /**
     * Gets the effortCertificationReportPeriodTitle attribute. 
     * @return Returns the effortCertificationReportPeriodTitle.
     */
    public String getEffortCertificationReportPeriodTitle() {
        return effortCertificationReportPeriodTitle;
    }

    /**
     * Sets the effortCertificationReportPeriodTitle attribute value.
     * @param effortCertificationReportPeriodTitle The effortCertificationReportPeriodTitle to set.
     */
    public void setEffortCertificationReportPeriodTitle(String effortCertificationReportPeriodTitle) {
        this.effortCertificationReportPeriodTitle = effortCertificationReportPeriodTitle;
    }

    /**
     * Gets the effortCertificationReportPeriodStatusCode attribute. 
     * @return Returns the effortCertificationReportPeriodStatusCode.
     */
    public String getEffortCertificationReportPeriodStatusCode() {
        return effortCertificationReportPeriodStatusCode;
    }

    /**
     * Sets the effortCertificationReportPeriodStatusCode attribute value.
     * @param effortCertificationReportPeriodStatusCode The effortCertificationReportPeriodStatusCode to set.
     */
    public void setEffortCertificationReportPeriodStatusCode(String effortCertificationReportPeriodStatusCode) {
        this.effortCertificationReportPeriodStatusCode = effortCertificationReportPeriodStatusCode;
    }

    /**
     * Gets the expenseTransferFiscalYear attribute. 
     * @return Returns the expenseTransferFiscalYear.
     */
    public Integer getExpenseTransferFiscalYear() {
        return expenseTransferFiscalYear;
    }

    /**
     * Sets the expenseTransferFiscalYear attribute value.
     * @param expenseTransferFiscalYear The expenseTransferFiscalYear to set.
     */
    public void setExpenseTransferFiscalYear(Integer expenseTransferFiscalYear) {
        this.expenseTransferFiscalYear = expenseTransferFiscalYear;
    }

    /**
     * Gets the expenseTransferFiscalPeriodCode attribute. 
     * @return Returns the expenseTransferFiscalPeriodCode.
     */
    public String getExpenseTransferFiscalPeriodCode() {
        return expenseTransferFiscalPeriodCode;
    }

    /**
     * Sets the expenseTransferFiscalPeriodCode attribute value.
     * @param expenseTransferFiscalPeriodCode The expenseTransferFiscalPeriodCode to set.
     */
    public void setExpenseTransferFiscalPeriodCode(String expenseTransferFiscalPeriodCode) {
        this.expenseTransferFiscalPeriodCode = expenseTransferFiscalPeriodCode;
    }

    /**
     * Gets the effortCertificationReportTypeCode attribute. 
     * @return Returns the effortCertificationReportTypeCode.
     */
    public String getEffortCertificationReportTypeCode() {
        return effortCertificationReportTypeCode;
    }

    /**
     * Sets the effortCertificationReportTypeCode attribute value.
     * @param effortCertificationReportTypeCode The effortCertificationReportTypeCode to set.
     */
    public void setEffortCertificationReportTypeCode(String effortCertificationReportTypeCode) {
        this.effortCertificationReportTypeCode = effortCertificationReportTypeCode;
    }

    /**
     * Gets the effortCertificationReportReturnDate attribute. 
     * @return Returns the effortCertificationReportReturnDate.
     */
    public Date getEffortCertificationReportReturnDate() {
        return effortCertificationReportReturnDate;
    }

    /**
     * Sets the effortCertificationReportReturnDate attribute value.
     * @param effortCertificationReportReturnDate The effortCertificationReportReturnDate to set.
     */
    public void setEffortCertificationReportReturnDate(Date effortCertificationReportReturnDate) {
        this.effortCertificationReportReturnDate = effortCertificationReportReturnDate;
    }

    /**
     * Gets the effortCertificationReportBeginFiscalYear attribute. 
     * @return Returns the effortCertificationReportBeginFiscalYear.
     */
    public Integer getEffortCertificationReportBeginFiscalYear() {
        return effortCertificationReportBeginFiscalYear;
    }

    /**
     * Sets the effortCertificationReportBeginFiscalYear attribute value.
     * @param effortCertificationReportBeginFiscalYear The effortCertificationReportBeginFiscalYear to set.
     */
    public void setEffortCertificationReportBeginFiscalYear(Integer effortCertificationReportBeginFiscalYear) {
        this.effortCertificationReportBeginFiscalYear = effortCertificationReportBeginFiscalYear;
    }

    /**
     * Gets the effortCertificationReportBeginPeriodCode attribute. 
     * @return Returns the effortCertificationReportBeginPeriodCode.
     */
    public String getEffortCertificationReportBeginPeriodCode() {
        return effortCertificationReportBeginPeriodCode;
    }

    /**
     * Sets the effortCertificationReportBeginPeriodCode attribute value.
     * @param effortCertificationReportBeginPeriodCode The effortCertificationReportBeginPeriodCode to set.
     */
    public void setEffortCertificationReportBeginPeriodCode(String effortCertificationReportBeginPeriodCode) {
        this.effortCertificationReportBeginPeriodCode = effortCertificationReportBeginPeriodCode;
    }

    /**
     * Gets the effortCertificationReportEndFiscalYear attribute. 
     * @return Returns the effortCertificationReportEndFiscalYear.
     */
    public Integer getEffortCertificationReportEndFiscalYear() {
        return effortCertificationReportEndFiscalYear;
    }

    /**
     * Sets the effortCertificationReportEndFiscalYear attribute value.
     * @param effortCertificationReportEndFiscalYear The effortCertificationReportEndFiscalYear to set.
     */
    public void setEffortCertificationReportEndFiscalYear(Integer effortCertificationReportEndFiscalYear) {
        this.effortCertificationReportEndFiscalYear = effortCertificationReportEndFiscalYear;
    }

    /**
     * Gets the effortCertificationReportEndPeriodCode attribute. 
     * @return Returns the effortCertificationReportEndPeriodCode.
     */
    public String getEffortCertificationReportEndPeriodCode() {
        return effortCertificationReportEndPeriodCode;
    }

    /**
     * Sets the effortCertificationReportEndPeriodCode attribute value.
     * @param effortCertificationReportEndPeriodCode The effortCertificationReportEndPeriodCode to set.
     */
    public void setEffortCertificationReportEndPeriodCode(String effortCertificationReportEndPeriodCode) {
        this.effortCertificationReportEndPeriodCode = effortCertificationReportEndPeriodCode;
    }

    /**
     * Gets the expenseTransferFiscalPeriod attribute. 
     * @return Returns the expenseTransferFiscalPeriod.
     */
    public AccountingPeriod getExpenseTransferFiscalPeriod() {
        return expenseTransferFiscalPeriod;
    }

    /**
     * Sets the expenseTransferFiscalPeriod attribute value.
     * @param expenseTransferFiscalPeriod The expenseTransferFiscalPeriod to set.
     */
    @Deprecated
    public void setExpenseTransferFiscalPeriod(AccountingPeriod expenseTransferFiscalPeriod) {
        this.expenseTransferFiscalPeriod = expenseTransferFiscalPeriod;
    }

    /**
     * Gets the effortCertificationPeriodStatusCode attribute. 
     * @return Returns the effortCertificationPeriodStatusCode.
     */
    public EffortCertificationPeriodStatusCode getEffortCertificationPeriodStatusCode() {
        return effortCertificationPeriodStatusCode;
    }

    /**
     * Sets the effortCertificationPeriodStatusCode attribute value.
     * @param effortCertificationPeriodStatusCode The effortCertificationPeriodStatusCode to set.
     */
    @Deprecated
    public void setEffortCertificationPeriodStatusCode(EffortCertificationPeriodStatusCode effortCertificationPeriodStatusCode) {
        this.effortCertificationPeriodStatusCode = effortCertificationPeriodStatusCode;
    }

    /**
     * Gets the effortCertificationReportType attribute. 
     * @return Returns the effortCertificationReportType.
     */
    public EffortCertificationReportType getEffortCertificationReportType() {
        return effortCertificationReportType;
    }

    /**
     * Sets the effortCertificationReportType attribute value.
     * @param effortCertificationReportType The effortCertificationReportType to set.
     */
    @Deprecated
    public void setEffortCertificationReportType(EffortCertificationReportType effortCertificationReportType) {
        this.effortCertificationReportType = effortCertificationReportType;
    }

    /**
     * Gets the effortCertificationReportPositions attribute. 
     * @return Returns the effortCertificationReportPositions.
     */
    public Collection<EffortCertificationReportPosition> getEffortCertificationReportPositions() {
        return effortCertificationReportPositions;
    }

    /**
     * Sets the effortCertificationReportPositions attribute value.
     * @param effortCertificationReportPositions The effortCertificationReportPositions to set.
     */
    public void setEffortCertificationReportPositions(Collection<EffortCertificationReportPosition> effortCertificationReportPositions) {
        this.effortCertificationReportPositions = effortCertificationReportPositions;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
       
    /**
     * Gets the options attribute. 
     * @return Returns the options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * @param options The options to set.
     */
    @Deprecated
    public void setOptions(Options options) {
        this.options = options;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("effortCertificationReportNumber", this.effortCertificationReportNumber);
        return map;
    }

    
}
