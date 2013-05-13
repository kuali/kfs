/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.PooledFundValue;

public interface PooledFundValueService {

    public Date calculateValueEffectiveDate(Date valuationDate, String pooledSecurityID);

    public PooledFundValue getByPrimaryKey(String id);

    /**
     * Computes the valueEffectiveDate for the AJAX call by getting the valuationDate as a String and returning the
     * valueEffectiveDate as String
     * 
     * @param valuationDate
     * @param pooledSecurityID
     * @return the valueEffectiveDate as String
     */
    public String calculateValueEffectiveDateForAjax(String valuationDate, String pooledSecurityID);

    public boolean isValuationDateTheLatest(String pooledSecurityID, Date theValuationDate);

    public Date getLatestValueEffectiveDate(String pooledSecurityID);

    /**
     * Gets PooledFundValue entries where the short term process on date is equal to current date and value effective date is the
     * most recent per security.
     * 
     * @return a list of PooledFundValue entries that meet the criteria
     */
    public List<PooledFundValue> getPooledFundValueWhereSTProcessOnDateIsCurrentDate();

    /**
     * Gets PooledFundValue entries where the long term process on date is equal to current date and value effective date is the
     * most recent per security.
     * 
     * @return a list of PooledFundValue entries that meet the criteria
     */
    public List<PooledFundValue> getPooledFundValueWhereLTProcessOnDateIsCurrentDate();

    /**
     * Gets PooledFundValue entries where the distribution income on date is equal to current date.
     * 
     * @return a list of PooledFundValue entries that meet the criteria
     */
    public List<PooledFundValue> getPooledFundValueWhereDistributionIncomeOnDateIsCurrentDate();

    /**
     * set PooledFundValue entries where the distribution income on date is equal to current date.
     */
    public void setIncomeDistributionCompleted(List<PooledFundValue> pooledFundValueList, boolean completed);

}
