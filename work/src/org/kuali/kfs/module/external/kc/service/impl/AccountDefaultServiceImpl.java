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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.service.AccountDefaultsService;
import org.kuali.kfs.module.external.kc.util.KcUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class AccountDefaultServiceImpl implements AccountDefaultsService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountDefaultServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected ContractsAndGrantsModuleService contractsAndGrantsModuleService;

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
        criteria.put(KcConstants.AccountCreationDefaults.KcUnit, unitNumber);
        defaults = businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);

        // if the matching defaults is null, try the parents in the hierarchy
        if (defaults == null) {

            List<String> parentUnits = null;
            try {
                parentUnits = contractsAndGrantsModuleService.getParentUnits(unitNumber);
            }
            catch (Exception ex) {
                LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, null) + ": " + ex.getMessage());

                GlobalVariables.getMessageMap().putError(KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, KcConstants.AccountCreationDefaults.KcUnit, ex.getMessage());

            }

            if (parentUnits != null) {
                for (String unit : parentUnits) {
                    criteria.put(KcConstants.AccountCreationDefaults.KcUnit, unit);
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

    public ContractsAndGrantsModuleService getContractsAndGrantsModuleService() {
        return contractsAndGrantsModuleService;
    }

    public void setContractsAndGrantsModuleService(ContractsAndGrantsModuleService contractsAndGrantsModuleService) {
        this.contractsAndGrantsModuleService = contractsAndGrantsModuleService;
    }

}
