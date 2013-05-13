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
