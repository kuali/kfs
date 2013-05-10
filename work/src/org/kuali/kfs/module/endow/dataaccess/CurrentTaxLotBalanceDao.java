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

import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;

public interface CurrentTaxLotBalanceDao {

    /**
     * Gets all records for the Security in END_CURR_TAX_LOT_BAL_T.
     * 
     * @param securityId the id of the security for which we retrieve the entries
     * @return all records for the Security in END_CURR_TAX_LOT_BAL_T
     */
    public Collection<CurrentTaxLotBalance> getAllCurrentTaxLotBalanceEntriesForSecurity(String securityId);

    /**
     * Gets the security ids for a given securityClassCode in END_FEE_SEC_T table
     * 
     * @feeMethodCode FEE_MTH
     * @return securityIds
     */
    public Collection getSecurityIds(String feeMethodCode);

    /**
     * Gets the security codes for a given securityClassCode in END_FEE_CLS_CD_T table
     * 
     * @feeMethodCode FEE_MTH
     * @return securityCodes
     */
    public Collection getSecurityClassCodes(String feeMethodCode);
    
    /**
     * Prepares the criteria and selects the records from END_CRNT_TAX_LOT_BAL_T table
     */
    public Collection<CurrentTaxLotBalance> getCurrentTaxLotBalances(FeeMethod feeMethod, Collection securityClassCodes, Collection securityIds);

}
