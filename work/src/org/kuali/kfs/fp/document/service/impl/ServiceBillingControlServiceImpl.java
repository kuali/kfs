/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import java.util.Collection;
import java.util.HashMap;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.financial.bo.ServiceBillingControl;
import org.kuali.module.financial.service.ServiceBillingControlService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the default implementation of the ServiceBillingControlService interface.
 */
public class ServiceBillingControlServiceImpl implements ServiceBillingControlService {

    private BusinessObjectService businessObjectService;

    /**
     * This method retrieves a ServiceBillingControl object using the values provided.
     * 
     * @param chartOfAccountsCode The chart code used to retrieve the service billing control.
     * @param accountNumber The account number used to retrieve the service billing control.
     * @return An instance of a ServiceBillingControl which matches the parameters provided.
     * 
     * @see ServiceBillingControlService#getByPrimaryId(String, String)
     */
    public ServiceBillingControl getByPrimaryId(String chartOfAccountsCode, String accountNumber) {
        HashMap keys = new HashMap();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        return (ServiceBillingControl) businessObjectService.findByPrimaryKey(ServiceBillingControl.class, keys);
    }

    /**
     * This method retrieves all ServiceBillingControl objects in the database.
     * 
     * @return A collection of all ServiceBillingControls defined in the database.
     * 
     * @see ServiceBillingControlService#getAll()
     */
    public ServiceBillingControl[] getAll() {
        Collection<ServiceBillingControl> controls = businessObjectService.findAll(ServiceBillingControl.class);
        return (ServiceBillingControl[]) controls.toArray(new ServiceBillingControl[0]);
    }

    /**
     * Gets the businessObjectService attribute.
     * @return The value of the businessObjectService attribute.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
