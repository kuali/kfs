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
package org.kuali.kfs.integration.ec;

import java.sql.Date;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * Methods for getting and setting report attributes.
 */
public interface EffortCertificationReport extends ExternalizableBusinessObject{

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public abstract Integer getUniversityFiscalYear();

    /**
     * Gets the effortCertificationReportNumber attribute.
     * 
     * @return Returns the effortCertificationReportNumber.
     */
    public abstract String getEffortCertificationReportNumber();

    /**
     * Gets the effortCertificationReportPeriodTitle attribute.
     * 
     * @return Returns the effortCertificationReportPeriodTitle.
     */
    public abstract String getEffortCertificationReportPeriodTitle();

    /**
     * Gets the effortCertificationReportPeriodStatusCode attribute.
     * 
     * @return Returns the effortCertificationReportPeriodStatusCode.
     */
    public abstract String getEffortCertificationReportPeriodStatusCode();

    /**
     * Gets the expenseTransferFiscalYear attribute.
     * 
     * @return Returns the expenseTransferFiscalYear.
     */
    public abstract Integer getExpenseTransferFiscalYear();

    /**
     * Gets the expenseTransferFiscalPeriodCode attribute.
     * 
     * @return Returns the expenseTransferFiscalPeriodCode.
     */
    public abstract String getExpenseTransferFiscalPeriodCode();

    /**
     * Gets the effortCertificationReportTypeCode attribute.
     * 
     * @return Returns the effortCertificationReportTypeCode.
     */
    public abstract String getEffortCertificationReportTypeCode();

    /**
     * Gets the effortCertificationReportReturnDate attribute.
     * 
     * @return Returns the effortCertificationReportReturnDate.
     */
    public abstract Date getEffortCertificationReportReturnDate();

    /**
     * Gets the effortCertificationReportBeginFiscalYear attribute.
     * 
     * @return Returns the effortCertificationReportBeginFiscalYear.
     */
    public abstract Integer getEffortCertificationReportBeginFiscalYear();

    /**
     * Gets the effortCertificationReportBeginPeriodCode attribute.
     * 
     * @return Returns the effortCertificationReportBeginPeriodCode.
     */
    public abstract String getEffortCertificationReportBeginPeriodCode();

    /**
     * Gets the effortCertificationReportEndFiscalYear attribute.
     * 
     * @return Returns the effortCertificationReportEndFiscalYear.
     */
    public abstract Integer getEffortCertificationReportEndFiscalYear();

    /**
     * Gets the effortCertificationReportEndPeriodCode attribute.
     * 
     * @return Returns the effortCertificationReportEndPeriodCode.
     */
    public abstract String getEffortCertificationReportEndPeriodCode();

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public abstract boolean isActive();
}
