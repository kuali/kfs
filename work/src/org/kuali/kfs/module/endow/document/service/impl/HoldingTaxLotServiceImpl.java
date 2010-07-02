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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.dataaccess.HoldingTaxLotDao;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * This class...
 */
public class HoldingTaxLotServiceImpl implements HoldingTaxLotService {
    private HoldingTaxLotDao holdingTaxLotDao;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getByPrimaryKey(java.lang.String, java.lang.String,
     *      java.lang.String, int, java.lang.String)
     */
    public HoldingTaxLot getByPrimaryKey(String kemid, String securityId, String registrationCode, int lotNumber, String ipIndicator) {
        Map<String, String> primaryKeys = new HashMap<String, String>();

        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, registrationCode);
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_NUMBER, String.valueOf(lotNumber));
        primaryKeys.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, ipIndicator);

        return (HoldingTaxLot) businessObjectService.findByPrimaryKey(HoldingTaxLot.class, primaryKeys);

    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLots(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public List<HoldingTaxLot> getAllTaxLots(String kemid, String securityId, String registrationCode, String ipIndicator) {
        Map<String, String> criteria = new HashMap<String, String>();

        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, registrationCode);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, ipIndicator);

        return (List<HoldingTaxLot>) businessObjectService.findMatching(HoldingTaxLot.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLotsOrderByAcquiredDate(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public List<HoldingTaxLot> getAllTaxLotsOrderByAcquiredDate(String kemid, String securityId, String registrationCode, String ipIndicator, boolean sortAscending) {
        Map<String, String> criteria = new HashMap<String, String>();

        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, securityId);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_REGISTRATION_CODE, registrationCode);
        criteria.put(EndowPropertyConstants.HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR, ipIndicator);

        return (List<HoldingTaxLot>) businessObjectService.findMatchingOrderBy(HoldingTaxLot.class, criteria, EndowPropertyConstants.HOLDING_TAX_LOT_ACQUIRED_DATE, sortAscending);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.HoldingTaxLotService#getAllTaxLotsWithPositiveUnits(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public List<HoldingTaxLot> getAllTaxLotsWithPositiveUnits(String kemid, String securityId, String registrationCode, String ipIndicator) {
        return (List<HoldingTaxLot>) holdingTaxLotDao.getAllTaxLotsWithPositiveUnits(kemid, securityId, registrationCode, ipIndicator);
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

    /**
     * Gets the holdingTaxLotDao.
     * 
     * @return holdingTaxLotDao
     */
    public HoldingTaxLotDao getHoldingTaxLotDao() {
        return holdingTaxLotDao;
    }

    /**
     * Sets the holdingTaxLotDao.
     * 
     * @param holdingTaxLotDao
     */
    public void setHoldingTaxLotDao(HoldingTaxLotDao holdingTaxLotDao) {
        this.holdingTaxLotDao = holdingTaxLotDao;
    }

}
