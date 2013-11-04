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
