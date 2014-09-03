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
package org.kuali.kfs.module.ar.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contains Utility methods used by CGB.
 */
public class ContractsGrantsBillingUtilityServiceImpl implements ContractsGrantsBillingUtilityService {
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService#returnProperStringValue(java.lang.Object)
     */
    @Override
    public String formatForCurrency(KualiDecimal amount) {
        if (!ObjectUtils.isNull(amount)) {
            Map<String, String> settings = new HashMap<>();
            settings.put(CurrencyFormatter.SHOW_SYMBOL, KFSConstants.Booleans.TRUE);
            CurrencyFormatter currencyFormatter = new CurrencyFormatter();
            currencyFormatter.setSettings(settings);
            String formattedAmount = (String)currencyFormatter.format(amount);
            return formattedAmount;
        }
        return "";
    }

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService#buildFullAddress(org.kuali.kfs.module.ar.businessobject.CustomerAddress)
     */
    @Override
    public String buildFullAddress(CustomerAddress address) {
        String fullAddress = "";
        if (ObjectUtils.isNotNull(address)) {
            if (StringUtils.isNotEmpty(address.getCustomerLine1StreetAddress())) {
                fullAddress += address.getCustomerLine1StreetAddress() + "\n";
            }
            if (StringUtils.isNotEmpty(address.getCustomerLine2StreetAddress())) {
                fullAddress += address.getCustomerLine2StreetAddress() + "\n";
            }
            if (StringUtils.isNotEmpty(address.getCustomerCityName())) {
                fullAddress += address.getCustomerCityName();
            }
            if (StringUtils.isNotEmpty(address.getCustomerStateCode())) {
                fullAddress += " " + address.getCustomerStateCode();
            }
            if (StringUtils.isNotEmpty(address.getCustomerZipCode())) {
                fullAddress += "-" + address.getCustomerZipCode();
            }
        }
        return fullAddress;
    }

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService#putValueOrEmptyString(java.util.Map, java.lang.String, java.lang.String)
     */
    @Override
    public void putValueOrEmptyString(Map<String, String> map, String key, String value) {
        map.put(key, (ObjectUtils.isNull(value) ? KFSConstants.EMPTY_STRING : value));
    }

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService#getActiveBillsForProposalNumber(java.lang.Long)
     */
    @Override
    public List<Bill> getActiveBillsForProposalNumber(Long proposalNumber) {
        if (proposalNumber == null) {
            throw new IllegalArgumentException("proposalNumber may not be null");
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        map.put(KFSPropertyConstants.ACTIVE, true);
        final List<Bill> bills = (List<Bill>) businessObjectService.findMatching(Bill.class, map);
        return bills;
    }

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService#getActiveMilestonesForProposalNumber(java.lang.Long)
     */
    @Override
    public List<Milestone> getActiveMilestonesForProposalNumber(Long proposalNumber) {
        if (proposalNumber == null) {
            throw new IllegalArgumentException("proposalNumber may not be null");
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        map.put(KFSPropertyConstants.ACTIVE, true);
        final List<Milestone> milestones = (List<Milestone>)(businessObjectService.findMatching(Milestone.class, map));
        return milestones;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}