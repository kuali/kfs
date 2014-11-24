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
