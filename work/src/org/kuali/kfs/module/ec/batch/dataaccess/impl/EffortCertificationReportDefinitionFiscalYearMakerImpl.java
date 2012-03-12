/*
 * Copyright 2008-2009 The Kuali Foundation
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
