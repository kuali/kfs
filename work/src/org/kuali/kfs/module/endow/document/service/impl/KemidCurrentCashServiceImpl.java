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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.document.service.KemidCurrentCashService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This KemidCurrentCashServiceImpl class provides the implementation for the method to test whether a KEMID has open records in
 * Current Cash: records with values greater or less than zero.
 */
public class KemidCurrentCashServiceImpl implements KemidCurrentCashService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.KemidCurrentCashService#hasKemidOpenRecordsInCurrentCash(java.lang.String)
     */
    public boolean hasKemidOpenRecordsInCurrentCash(String kemid) {
        boolean hasOpenRecords = false;

        Map pkMap = new HashMap();
        pkMap.put(EndowPropertyConstants.KEMID, kemid);
        KemidCurrentCash kemidCurrentCash = (KemidCurrentCash) businessObjectService.findByPrimaryKey(KemidCurrentCash.class, pkMap);

        if (ObjectUtils.isNotNull(kemidCurrentCash)) {
            // if the record has values greater or less than zero than return true
            if (kemidCurrentCash.getCurrentIncomeCash().isNonZero() || kemidCurrentCash.getCurrentPrincipalCash().isNonZero()) {
                hasOpenRecords = true;
            }
        }
        return hasOpenRecords;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KemidCurrentCashService#getByPrimaryKey(String) Gets the current cash record
     *      for the given kemId
     * @param kemId
     * @return KemidCurrentCash the record with the given kemId
     */
    public KemidCurrentCash getByPrimaryKey(String kemId) {
        Map<String, String> primaryKey = new HashMap<String, String>();

        primaryKey.put(EndowPropertyConstants.KEMID, kemId);

        return (KemidCurrentCash) businessObjectService.findByPrimaryKey(KemidCurrentCash.class, primaryKey);
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
