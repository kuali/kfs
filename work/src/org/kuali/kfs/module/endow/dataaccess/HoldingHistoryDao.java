/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.dataaccess;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.businessobject.FeeMethod;

public interface HoldingHistoryDao {

    /**
     * Calculates the total Holding Units based on FEE_BAL_TYP_CD = AU OR MU
     * @param feeMethod feeMethod object
     * @return totalHoldingUnits
     */
    public BigDecimal getHoldingHistoryTotalHoldingUnits(FeeMethod feeMethod);
    
    /**
     * Calculates the total Holding market value based on FEE_BAL_TYP_CD = AMV OR MMV
     * @param feeMethod feeMethod object
     * @return totalHoldingMarketValue
     */
    public BigDecimal getHoldingHistoryTotalHoldingMarketValue(FeeMethod feeMethod);
}
