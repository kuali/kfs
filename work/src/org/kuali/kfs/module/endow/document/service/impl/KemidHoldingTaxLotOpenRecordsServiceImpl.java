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
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.document.service.KemidHoldingTaxLotOpenRecordsService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This KemidHoldingTaxLotOpenRecordsServiceImpl class provides the implementation for the method to test whether a KEMID has open
 * records in Holding Tax Lot: records with values greater or less than zero for the following fields: Holding Units, Holding Cost
 * and Current Accrual.
 */
public class KemidHoldingTaxLotOpenRecordsServiceImpl implements KemidHoldingTaxLotOpenRecordsService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.KemidHoldingTaxLotOpenRecordsService#hasKemidHoldingTaxLotOpenRecords(java.lang.String)
     */
    public boolean hasKemidHoldingTaxLotOpenRecords(String kemid) {
        boolean hasOpenRecords = false;

        Map fieldValuesMap = new HashMap();
        fieldValuesMap.put(EndowPropertyConstants.KEMID, kemid);
        List<HoldingTaxLot> kemidHoldingTaxLotList = (List<HoldingTaxLot>) businessObjectService.findMatching(HoldingTaxLot.class, fieldValuesMap);

        if (kemidHoldingTaxLotList.size() != 0) {
            for (HoldingTaxLot holdingTaxLot : kemidHoldingTaxLotList) {
                // if the record has values greater or less than zero than return true
                if (holdingTaxLot.getUnits().compareTo(BigDecimal.ZERO) != 0 || holdingTaxLot.getCost().compareTo(BigDecimal.ZERO) == 0 || holdingTaxLot.getCurrentAccrual().compareTo(BigDecimal.ZERO) == 0) {
                    hasOpenRecords = true;
                    break;
                }
            }
        }
        return hasOpenRecords;
    }


    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
