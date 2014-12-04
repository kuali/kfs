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
