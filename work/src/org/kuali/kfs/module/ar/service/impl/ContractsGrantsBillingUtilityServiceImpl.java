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
