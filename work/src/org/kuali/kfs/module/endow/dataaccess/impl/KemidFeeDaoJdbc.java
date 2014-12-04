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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.sql.Date;

import org.kuali.kfs.module.endow.dataaccess.KemidFeeDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.dao.DataAccessException;

public class KemidFeeDaoJdbc extends PlatformAwareDaoBaseJdbc implements KemidFeeDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KemidFeeDaoJdbc.class);

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.KemidFeeDao#updateKemidFeeWaivedFeeYearToDateToZero(Date, Date, boolean)
     */
    public boolean updateKemidFeeWaivedFeeYearToDateToZero(Date firstDayAfterFiscalYear, Date currentDate, boolean fiscalYearMonthAndDayParamExists) {
        boolean updated = true;

        if (!systemParametersForUpdateWaiverFeeAmounts(fiscalYearMonthAndDayParamExists)) {
            return false;
        }

        if (firstDayAfterFiscalYear.equals(currentDate)) {
            try {
                getSimpleJdbcTemplate().update("UPDATE END_KEMID_FEE_T SET WAIVED_FEE_YTD = 0");
                return true;
            }
            catch (DataAccessException dae) {
                return false;
            }
        }

        return updated;
    }

    /**
     * This method checks if the System parameters have been set up for this batch job.
     * 
     * @result return true if the system parameters exist, else false
     */
    protected boolean systemParametersForUpdateWaiverFeeAmounts(boolean fiscalYearMonthAndDayParamExists) {
        LOG.debug("systemParametersForUpdateWaiverFeeAmounts() started.");

        boolean systemParameterExists = true;

        // check to make sure the system parameter has been setup...
        if (!fiscalYearMonthAndDayParamExists) {
            LOG.warn("FISCAL_YEAR_END_MONTH_AND_DAY System parameter does not exist in the parameters list.  The job can not continue without this parameter");
            return false;
        }

        LOG.debug("systemParametersForUpdateWaiverFeeAmounts() ended.");

        return systemParameterExists;
    }
}
