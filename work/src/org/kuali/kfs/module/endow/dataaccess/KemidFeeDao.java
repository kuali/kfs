/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.sql.Date;

public interface KemidFeeDao {

    /**
     * Updates WAIVED_FEE_YTD column to zero (0). If the current date is the first day of the institution's fiscal year
     * (FISCAL_YEAR_END_MONTH_AND_DAY parameter) then all values in END_KEMID_FEE_T: WAIVED_FEE_YTD are set to zero (0).
     * 
     * @param firstDayAfterFiscalYear the first Day After Fiscal Year
     * @param currentDate the current date
     * @param fiscalYearMonthAndDayParamExists a boolean that tells whether the FISCAL_YEAR_END_MONTH_AND_DAY parameter exists
     * @return true if column value updated to zero else false
     */
    public boolean updateKemidFeeWaivedFeeYearToDateToZero(Date firstDayAfterFiscalYear, Date currentDate, boolean fiscalYearMonthAndDayParamExists);

}
