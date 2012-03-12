/*
 * Copyright 2007 The Kuali Foundation
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
