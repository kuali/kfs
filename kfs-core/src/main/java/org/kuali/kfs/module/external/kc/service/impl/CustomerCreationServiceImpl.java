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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.Agency;
import org.kuali.kfs.module.external.kc.dto.CustomerCreationStatusDto;
import org.kuali.kfs.module.external.kc.dto.CustomerTypeDto;
import org.kuali.kfs.module.external.kc.dto.SponsorDTO;
import org.kuali.kfs.module.external.kc.service.CustomerCreationService;
import org.kuali.kfs.sys.KFSConstants;
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

    protected ConfigurationService configurationService;
    protected AccountsReceivableModuleService accountsReceivableModuleService;
    protected KeyValuesService keyValuesService;

    @Override
    public CustomerCreationStatusDto createCustomer(SponsorDTO sponsor, String initiatedByPrincipalName) {
        CustomerCreationStatusDto result = new CustomerCreationStatusDto();
        UserSession oldSession = GlobalVariables.getUserSession();
        try {
            GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
            Agency agency = new Agency(sponsor);
            String description = configurationService.getPropertyValueAsString(CREATED_BY_AGENCY_DOC);
            String customerNumber = accountsReceivableModuleService.createAndSaveCustomer(description, agency);
            result.setCustomerNumber(customerNumber);
        } catch (Exception e) {
            result.setErrors(new ArrayList<String>());
            result.getErrors().add(e.getMessage());
            LOG.error("Unable to create customer.", e);
        } finally {
            GlobalVariables.setUserSession(oldSession);
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
    public boolean isValidCustomer(@WebParam(name = KcConstants.CustomerCreationService.CUSTOMER_NUMBER) String customerNumber) {
        ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(AccountsReceivableCustomer.class);
        if (responsibleModuleService!=null && responsibleModuleService.isExternalizable(AccountsReceivableCustomer.class)) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put(KcConstants.CustomerCreationService.CUSTOMER_NUMBER, customerNumber);
            return (responsibleModuleService.getExternalizableBusinessObject(AccountsReceivableCustomer.class, values) != null);
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
