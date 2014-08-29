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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contains Utility methods used by CGB.
 */
public class ContractsGrantsBillingUtilityServiceImpl implements ContractsGrantsBillingUtilityService {

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

}