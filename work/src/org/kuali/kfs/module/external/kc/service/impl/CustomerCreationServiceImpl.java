/*
 * Copyright 2014 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.businessobject.Customer;
import org.kuali.kfs.module.external.kc.businessobject.Agency;
import org.kuali.kfs.module.external.kc.dto.CustomerCreationStatusDto;
import org.kuali.kfs.module.external.kc.dto.CustomerTypeDto;
import org.kuali.kfs.module.external.kc.dto.SponsorDTO;
import org.kuali.kfs.module.external.kc.service.CustomerCreationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.GlobalVariables;

public class CustomerCreationServiceImpl implements CustomerCreationService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreationServiceImpl.class);

    private static final String CREATED_BY_AGENCY_DOC = "message.ar.createdByAgencyDocument";

    private ConfigurationService configurationService;
    private AccountsReceivableModuleService accountsReceivableModuleService;
    private KeyValuesService keyValuesService;

    @Override
    public CustomerCreationStatusDto createCustomer(SponsorDTO sponsor, String initiatedByPrincipalName) {
        CustomerCreationStatusDto result = new CustomerCreationStatusDto();
        try {
            if (GlobalVariables.getUserSession() == null) {
                GlobalVariables.setUserSession(new UserSession(initiatedByPrincipalName));
            } else {
                GlobalVariables.getUserSession().setBackdoorUser(initiatedByPrincipalName);
            }
            Agency agency = new Agency(sponsor);
            String description = configurationService.getPropertyValueAsString(CREATED_BY_AGENCY_DOC);
            String customerNumber = accountsReceivableModuleService.createAndSaveCustomer(description, agency);
            result.setCustomerNumber(customerNumber);
            GlobalVariables.getUserSession().clearBackdoorUser();
        } catch (Exception e) {
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(e.getMessage());
            LOG.error("Unable to create customer.", e);
        }

        return result;
    }

    @Override
    public List<CustomerTypeDto> getCustomerTypes() {
        List<CustomerTypeDto> dtos = new ArrayList<CustomerTypeDto>();
        Collection<AccountsReceivableCustomerType> customerTypes = getKeyValuesService().findAll(AccountsReceivableCustomerType.class);
        for (AccountsReceivableCustomerType type : customerTypes) {
            CustomerTypeDto dto = new CustomerTypeDto();
            dto.setCustomerTypeCode(type.getCustomerTypeCode());
            dto.setCustomerTypeDescription(type.getCustomerTypeDescription());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public boolean isValidCustomer(@WebParam(name = "customerNumber") String customerNumber) {
        ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(Customer.class);
        if (responsibleModuleService!=null && responsibleModuleService.isExternalizable(Customer.class)) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("customerNumber", customerNumber);
            return (responsibleModuleService.getExternalizableBusinessObject(Customer.class, values) != null);
        } else {
            return false;
        }

    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        return accountsReceivableModuleService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }

    protected KeyValuesService getKeyValuesService() {
        if (keyValuesService == null) {
            keyValuesService = SpringContext.getBean(KeyValuesService.class);
        }
        return keyValuesService;
    }

    public void setKeyValuesService(KeyValuesService keyValuesService) {
        this.keyValuesService = keyValuesService;
    }

}
