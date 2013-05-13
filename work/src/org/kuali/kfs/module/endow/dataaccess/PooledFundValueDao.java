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
