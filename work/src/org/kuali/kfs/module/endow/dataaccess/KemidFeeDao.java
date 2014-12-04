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
