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
