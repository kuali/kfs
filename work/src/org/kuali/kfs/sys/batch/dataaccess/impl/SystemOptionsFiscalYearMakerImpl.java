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
package org.kuali.kfs.sys.batch.dataaccess.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;

/**
 * Customizes new option records created by fiscal year process
 */
public class SystemOptionsFiscalYearMakerImpl extends FiscalYearMakerImpl {

    public SystemOptionsFiscalYearMakerImpl() {
        super();
        
        super.setAllowOverrideTargetYear(false);
    }

    /**
     * @see org.kuali.kfs.coa.batch.dataaccess.impl.FiscalYearMakerHelperImpl#changeForNewYear(java.lang.Integer,
     *      org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public void changeForNewYear(Integer baseFiscalYear, FiscalYearBasedBusinessObject currentRecord) {
        super.changeForNewYear(baseFiscalYear, currentRecord);
        
        SystemOptions options = (SystemOptions) currentRecord;
        options.setUniversityFiscalYearStartYr(baseFiscalYear);

        // name string contains previous fiscal year start and end year that needs updated
        Integer previousStartYear = baseFiscalYear - 1;
        Integer nextEndYear = baseFiscalYear + 1;

        String universityFiscalYearName = options.getUniversityFiscalYearName();
        universityFiscalYearName = StringUtils.replace(universityFiscalYearName, baseFiscalYear.toString(), nextEndYear.toString());
        universityFiscalYearName = StringUtils.replace(universityFiscalYearName, previousStartYear.toString(), baseFiscalYear.toString());

        options.setUniversityFiscalYearName(universityFiscalYearName);
    }

}
