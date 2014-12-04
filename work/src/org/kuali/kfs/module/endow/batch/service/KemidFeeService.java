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
package org.kuali.kfs.module.endow.batch.service;

import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.KemidFee;

public interface KemidFeeService {

    /**
     * updates Waiver Fee Year-To-Date totals.
     * @return true if the amounts updated else return false
     */
    public boolean updateWaiverFeeYearToDateTotals();
    
    /**
     * Gets all the KemidFee records as a collection
     * @return collection <KemidFee> records
     */
    public Collection<KemidFee> getAllKemIdFee() ;

    /**
     * Saves the kemidFee record to the table END_KEMID_FEE_T
     * @param kemidFee
     * @return true if saved else false...
     */
    public boolean saveKemidFee(KemidFee kemidFee);
    
    /**
     * Retrieves all KemidFee records for the given feeMethodCode.
     */
    public Collection<KemidFee> getAllKemidForFeeMethodCode(String feeMethodCode);

    /**
     * Determines if the fee to be charged or not to the kemid based on the contions:
     * FEE_IMPL_DT is equal to or less than the current date and
     * FEE_TERM_DT is null or greater than the current date.
     *IF there is a value greater than zero in END_KEMID_FEE_T: FEE_CORPUS_PCT_TLRNC and a record exists for the KEMID in END_KEMID_CORPUS_VAL_T and CURR_CORPUSPRIN_MVAL divided by CURR_CORPUS_VAL is less than END_KEMID_FEE_T: FEE_CORPUS_PCT_TLRNC, then skip the KEMID as no fee will be charged.
     *@param feeMethod feeMethod record, kemidFee kemidFee record
     *@return true if fee to be charged to the kemid else return false
     */
    public boolean chargeFeeToKemid(FeeMethod feeMethod, KemidFee kemidFee);
}
