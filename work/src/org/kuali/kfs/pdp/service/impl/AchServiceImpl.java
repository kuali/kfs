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
package org.kuali.kfs.pdp.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.pdp.service.AchService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * @see org.kuali.kfs.pdp.service.AchService
 */
public class AchServiceImpl implements AchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.pdp.service.AchService#getAchInformation(java.lang.String, java.lang.String, java.lang.String)
     */
    public PayeeACHAccount getAchInformation(String idType, String payeeId, String achTransactionType) {
        LOG.debug("getAchInformation() started");

        Map<String, Object> fields = new HashMap<String, Object>();

        fields.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        fields.put(PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, idType);
        fields.put(PdpPropertyConstants.ACH_TRANSACTION_TYPE, achTransactionType);
        fields.put(PdpPropertyConstants.PAYEE_ID_NUMBER, payeeId);

        Collection<PayeeACHAccount> rows = businessObjectService.findMatching(PayeeACHAccount.class, fields);
        if (rows.size() != 1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getAchInformation() not found rows = " + rows.size());
            }

            return null;
        }
        else {
            LOG.debug("getAchInformation() found");

            return rows.iterator().next();
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.AchService#getActiveAchAccounts()
     */
    public List<PayeeACHAccount> getActiveAchAccounts() {
        LOG.debug("getActivePayeeAchAccounts() started");

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        Collection<PayeeACHAccount> accounts = businessObjectService.findMatchingOrderBy(PayeeACHAccount.class, fields, PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, true);
        
        return new ArrayList<PayeeACHAccount>(accounts);
    }
    
    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
