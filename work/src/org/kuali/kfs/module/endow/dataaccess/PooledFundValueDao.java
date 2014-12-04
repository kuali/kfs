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
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.PooledFundValue;

public interface PooledFundValueDao {

    /**
     * Gets PooledFundValue entries where the ST_PROC_ON_DT is equal to current date and ST_PROC_COMPLT = No. Order the result list
     * by security ID and value effective date.
     * 
     * @param currentDate the current date
     * @return a list of PooledFundValue entries that meet the criteria
     */
    public List<PooledFundValue> getPooledFundValueWhereSTProcessOnDateIsCurrentDate(Date currentDate);

    /**
     * Gets PooledFundValue entries where the LT_PROC_ON_DT is equal to current date and LT_PROC_COMPLT = No. Order the result list
     * by security ID and value effective date.
     * 
     * @param currentDate the current date
     * @return a list of PooledFundValue entries that meet the criteria
     */
    public List<PooledFundValue> getPooledFundValueWhereLTProcessOnDateIsCurrentDate(Date currentDate);

    /**
     * Gets PooledFundValue entries where the Distribution Income date is equal to current date and LT_PROC_COMPLT = No.
     * 
     * @param currentDate the current date
     * @return a list of PooledFundValue entries that meet the criteria
     */
    public List<PooledFundValue> getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate(Date currentDate);

}
