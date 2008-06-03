/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.integration.bo;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.effort.bo.EffortCertificationPeriodStatusCode;
import org.kuali.module.effort.bo.EffortCertificationReportPosition;
import org.kuali.module.effort.bo.EffortCertificationReportType;

/**
 * Methods for getting and setting report attributes.
 */
public interface EffortCertificationReport {

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public abstract Integer getUniversityFiscalYear();

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public abstract void setUniversityFiscalYear(Integer universityFiscalYear);

    /**
     * Gets the effortCertificationReportNumber attribute.
     * 
     * @return Returns the effortCertificationReportNumber.
     */
    public abstract String getEffortCertificationReportNumber();

    /**
     * Sets the effortCertificationReportNumber attribute value.
     * 
     * @param effortCertificationReportNumber The effortCertificationReportNumber to set.
     */
    public abstract void setEffortCertificationReportNumber(String effortCertificationReportNumber);

    /**
     * Gets the effortCertificationReportPeriodTitle attribute.
     * 
     * @return Returns the effortCertificationReportPeriodTitle.
     */
    public abstract String getEffortCertificationReportPeriodTitle();

    /**
     * Sets the effortCertificationReportPeriodTitle attribute value.
     * 
     * @param effortCertificationReportPeriodTitle The effortCertificationReportPeriodTitle to set.
     */
    public abstract void setEffortCertificationReportPeriodTitle(String effortCertificationReportPeriodTitle);

    /**
     * Gets the effortCertificationReportPeriodStatusCode attribute.
     * 
     * @return Returns the effortCertificationReportPeriodStatusCode.
     */
    public abstract String getEffortCertificationReportPeriodStatusCode();

    /**
     * Sets the effortCertificationReportPeriodStatusCode attribute value.
     * 
     * @param effortCertificationReportPeriodStatusCode The effortCertificationReportPeriodStatusCode to set.
     */
    public abstract void setEffortCertificationReportPeriodStatusCode(String effortCertificationReportPeriodStatusCode);

    /**
     * Gets the expenseTransferFiscalYear attribute.
     * 
     * @return Returns the expenseTransferFiscalYear.
     */
    public abstract Integer getExpenseTransferFiscalYear();

    /**
     * Sets the expenseTransferFiscalYear attribute value.
     * 
     * @param expenseTransferFiscalYear The expenseTransferFiscalYear to set.
     */
    public abstract void setExpenseTransferFiscalYear(Integer expenseTransferFiscalYear);

    /**
     * Gets the expenseTransferFiscalPeriodCode attribute.
     * 
     * @return Returns the expenseTransferFiscalPeriodCode.
     */
    public abstract String getExpenseTransferFiscalPeriodCode();

    /**
     * Sets the expenseTransferFiscalPeriodCode attribute value.
     * 
     * @param expenseTransferFiscalPeriodCode The expenseTransferFiscalPeriodCode to set.
     */
    public abstract void setExpenseTransferFiscalPeriodCode(String expenseTransferFiscalPeriodCode);

    /**
     * Gets the effortCertificationReportTypeCode attribute.
     * 
     * @return Returns the effortCertificationReportTypeCode.
     */
    public abstract String getEffortCertificationReportTypeCode();

    /**
     * Sets the effortCertificationReportTypeCode attribute value.
     * 
     * @param effortCertificationReportTypeCode The effortCertificationReportTypeCode to set.
     */
    public abstract void setEffortCertificationReportTypeCode(String effortCertificationReportTypeCode);

    /**
     * Gets the effortCertificationReportReturnDate attribute.
     * 
     * @return Returns the effortCertificationReportReturnDate.
     */
    public abstract Date getEffortCertificationReportReturnDate();

    /**
     * Sets the effortCertificationReportReturnDate attribute value.
     * 
     * @param effortCertificationReportReturnDate The effortCertificationReportReturnDate to set.
     */
    public abstract void setEffortCertificationReportReturnDate(Date effortCertificationReportReturnDate);

    /**
     * Gets the effortCertificationReportBeginFiscalYear attribute.
     * 
     * @return Returns the effortCertificationReportBeginFiscalYear.
     */
    public abstract Integer getEffortCertificationReportBeginFiscalYear();

    /**
     * Sets the effortCertificationReportBeginFiscalYear attribute value.
     * 
     * @param effortCertificationReportBeginFiscalYear The effortCertificationReportBeginFiscalYear to set.
     */
    public abstract void setEffortCertificationReportBeginFiscalYear(Integer effortCertificationReportBeginFiscalYear);

    /**
     * Gets the effortCertificationReportBeginPeriodCode attribute.
     * 
     * @return Returns the effortCertificationReportBeginPeriodCode.
     */
    public abstract String getEffortCertificationReportBeginPeriodCode();

    /**
     * Sets the effortCertificationReportBeginPeriodCode attribute value.
     * 
     * @param effortCertificationReportBeginPeriodCode The effortCertificationReportBeginPeriodCode to set.
     */
    public abstract void setEffortCertificationReportBeginPeriodCode(String effortCertificationReportBeginPeriodCode);

    /**
     * Gets the effortCertificationReportEndFiscalYear attribute.
     * 
     * @return Returns the effortCertificationReportEndFiscalYear.
     */
    public abstract Integer getEffortCertificationReportEndFiscalYear();

    /**
     * Sets the effortCertificationReportEndFiscalYear attribute value.
     * 
     * @param effortCertificationReportEndFiscalYear The effortCertificationReportEndFiscalYear to set.
     */
    public abstract void setEffortCertificationReportEndFiscalYear(Integer effortCertificationReportEndFiscalYear);

    /**
     * Gets the effortCertificationReportEndPeriodCode attribute.
     * 
     * @return Returns the effortCertificationReportEndPeriodCode.
     */
    public abstract String getEffortCertificationReportEndPeriodCode();

    /**
     * Sets the effortCertificationReportEndPeriodCode attribute value.
     * 
     * @param effortCertificationReportEndPeriodCode The effortCertificationReportEndPeriodCode to set.
     */
    public abstract void setEffortCertificationReportEndPeriodCode(String effortCertificationReportEndPeriodCode);

    /**
     * Gets the expenseTransferFiscalPeriod attribute.
     * 
     * @return Returns the expenseTransferFiscalPeriod.
     */
    public abstract AccountingPeriod getExpenseTransferFiscalPeriod();

    /**
     * Sets the expenseTransferFiscalPeriod attribute value.
     * 
     * @param expenseTransferFiscalPeriod The expenseTransferFiscalPeriod to set.
     */
    @Deprecated
    public abstract void setExpenseTransferFiscalPeriod(AccountingPeriod expenseTransferFiscalPeriod);

    /**
     * gets expenseTrasferYear
     * @return
     */
    public abstract Options getExpenseTransferYear();

    /**
     * sets expenseTrasferYear attribute
     * @param expenseTransferYear
     */
    @Deprecated
    public abstract void setExpenseTransferYear(Options expenseTransferYear);

    /**
     * 
     * gets reportBeginFiscalYear attribute value
     * @return
     */
    public abstract Options getReportBeginFiscalYear();

    /**
     * 
     * sets the reportBeginFiscalYear attribute
     * @param reportBeginFiscalYear
     */
    @Deprecated
    public abstract void setReportBeginFiscalYear(Options reportBeginFiscalYear);

    /**
     * 
     * gets reportEndFiscalYear attribute
     * @return
     */
    public abstract Options getReportEndFiscalYear();

    /**
     * 
     * sets reportEndFiscalYear attribute
     * @param reportEndFiscalYear
     */
    @Deprecated
    public abstract void setReportEndFiscalYear(Options reportEndFiscalYear);

    /**
     * 
     * gets reportBeginPeriod
     * @return
     */
    public abstract AccountingPeriod getReportBeginPeriod();

    /**
     * sets reportBeginFiscalPeriod
     * This method...
     * @param reportBeginPeriod
     */
    @Deprecated
    public abstract void setReportBeginPeriod(AccountingPeriod reportBeginPeriod);

    /**
     * gets reporEndPeriod
     * @return
     */
    public abstract AccountingPeriod getReportEndPeriod();

    /**
     * sets reportEndPeriod
     * @param reportEndPeriod
     */
    @Deprecated
    public abstract void setReportEndPeriod(AccountingPeriod reportEndPeriod);

    /**
     * Gets the effortCertificationPeriodStatusCode attribute.
     * 
     * @return Returns the effortCertificationPeriodStatusCode.
     */
    public abstract EffortCertificationPeriodStatusCode getEffortCertificationPeriodStatusCode();

    /**
     * Sets the effortCertificationPeriodStatusCode attribute value.
     * 
     * @param effortCertificationPeriodStatusCode The effortCertificationPeriodStatusCode to set.
     */
    @Deprecated
    public abstract void setEffortCertificationPeriodStatusCode(EffortCertificationPeriodStatusCode effortCertificationPeriodStatusCode);

    /**
     * Gets the effortCertificationReportType attribute.
     * 
     * @return Returns the effortCertificationReportType.
     */
    public abstract EffortCertificationReportType getEffortCertificationReportType();

    /**
     * Sets the effortCertificationReportType attribute value.
     * 
     * @param effortCertificationReportType The effortCertificationReportType to set.
     */
    @Deprecated
    public abstract void setEffortCertificationReportType(EffortCertificationReportType effortCertificationReportType);

    /**
     * Gets the effortCertificationReportPositions attribute.
     * 
     * @return Returns the effortCertificationReportPositions.
     */
    public abstract Collection<EffortCertificationReportPosition> getEffortCertificationReportPositions();

    /**
     * Sets the effortCertificationReportPositions attribute value.
     * 
     * @param effortCertificationReportPositions The effortCertificationReportPositions to set.
     */
    public abstract void setEffortCertificationReportPositions(Collection<EffortCertificationReportPosition> effortCertificationReportPositions);

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public abstract boolean isActive();

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public abstract void setActive(boolean active);

    /**
     * Gets the options attribute.
     * 
     * @return Returns the options.
     */
    public abstract Options getOptions();

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    @Deprecated
    public abstract void setOptions(Options options);

    /**
     * Gets the reportPeriods attribute. 
     * @return Returns the reportPeriods.
     */
    public abstract Map<Integer, Set<String>> getReportPeriods();

    /**
     * Sets the reportPeriods attribute value.
     * @param reportPeriods The reportPeriods to set.
     */
    public abstract void setReportPeriods(Map<Integer, Set<String>> reportPeriods);

}