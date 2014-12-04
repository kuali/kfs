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
