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
package org.kuali.kfs.coa.dataaccess;

import java.util.Collection;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;

/**
 * This interface defines the data access methods for {@link IndirectCostRecoveryRateDetail}
 */
public interface IndirectCostRecoveryRateDetailDao {

    /**
     * This method looks up all active ICR Rate Detail records for a given university fiscal year and financial series ID/rate ID
     * 
     * @{link IndirectCostRecoveryRateDetail} by the fiscal year, series ID
     * 
     * @param universityFiscalYear
     * @param financialSeriesId
     * @param balanceTypeCode
     * @return collection of
     * @{link IndirectCostRecoveryRateDetail}s that match these criteria
     */
    public Collection<IndirectCostRecoveryRateDetail> getActiveRateDetailsByRate(Integer universityFiscalYear, String financialSeriesId);
}
