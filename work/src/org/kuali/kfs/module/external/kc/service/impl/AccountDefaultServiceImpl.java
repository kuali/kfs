/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.service.AccountDefaultsService;
import org.kuali.kfs.module.external.kc.util.KcUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class AccountDefaultServiceImpl implements AccountDefaultsService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountDefaultServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * This method looks up the default table
     *
     * @param String unitNumber
     * @return AccountAutoCreateDefaults
     */
    @Override
    public AccountAutoCreateDefaults getAccountDefaults(String unitNumber) {

        AccountAutoCreateDefaults defaults = null;

        if (unitNumber == null || unitNumber.isEmpty()) {
            return null;
        }

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kcUnit", unitNumber);
        defaults = businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);

        // if the matching defaults is null, try the parents in the hierarchy
        if (defaults == null) {

            List<String> parentUnits = null;
            try {
                parentUnits = SpringContext.getBean(ContractsAndGrantsModuleService.class).getParentUnits(unitNumber);
            }
            catch (Exception ex) {
                LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, null) + ": " + ex.getMessage());

                GlobalVariables.getMessageMap().putError(KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, "kcUnit", ex.getMessage());

            }

            if (parentUnits != null) {
                for (String unit : parentUnits) {
                    criteria.put("kcUnit", unit);
                    defaults = businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);
                    if (defaults != null) {
                        break;
                    }
                }
            }

        }

        return defaults;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
