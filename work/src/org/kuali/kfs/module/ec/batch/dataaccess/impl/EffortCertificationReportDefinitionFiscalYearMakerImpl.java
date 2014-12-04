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
package org.kuali.kfs.module.ec.batch.dataaccess.impl;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.dataaccess.impl.FiscalYearMakerImpl;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;

/**
 * Performs custom population of report definition records for a new year being created in the fiscal year maker process
 */
public class EffortCertificationReportDefinitionFiscalYearMakerImpl extends FiscalYearMakerImpl {

    /**
     * @see org.kuali.kfs.coa.dataaccess.impl.FiscalYearMakerImpl#changeForNewYear(java.lang.Integer,
     *      org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public void changeForNewYear(Integer baseFiscalYear, FiscalYearBasedBusinessObject currentRecord) {
        super.changeForNewYear(baseFiscalYear, currentRecord);

        EffortCertificationReportDefinition reportDefinition = (EffortCertificationReportDefinition) currentRecord;
        reportDefinition.setEffortCertificationReportReturnDate(null);

        // set the various fiscal year fields up by 1
        reportDefinition.setExpenseTransferFiscalYear(reportDefinition.getExpenseTransferFiscalYear() + 1);
        reportDefinition.setEffortCertificationReportBeginFiscalYear(reportDefinition.getEffortCertificationReportBeginFiscalYear() + 1);
        reportDefinition.setEffortCertificationReportEndFiscalYear(reportDefinition.getEffortCertificationReportEndFiscalYear() + 1);

        // all reporting period status codes to "not yet opened--updates allowed" at start of year
        reportDefinition.setEffortCertificationReportPeriodStatusCode(KFSConstants.PeriodStatusCodes.NOT_OPEN);
    }

}
