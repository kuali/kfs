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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.validation.SuspensionCategoryBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Abstract Suspension Category base class for Customer related suspension categories.
 */
public abstract class CustomerAddressSuspensionCategoryBase extends SuspensionCategoryBase {

    /**
     * @param agencyAddress
     * @return
     */
    protected boolean isCustomerAddressComplete(CustomerAddress customerAddress) {
        if (ObjectUtils.isNull(customerAddress)) {
            return false;
        }

        if (StringUtils.isBlank(customerAddress.getCustomerLine1StreetAddress()) ||
                StringUtils.isBlank(customerAddress.getCustomerCityName())) {
            return false;
        }

        String countryCode = customerAddress.getCustomerCountryCode();
        if (StringUtils.isBlank(countryCode)) {
            return false;
        } else {
            if (StringUtils.equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_US, countryCode)) {
                return !StringUtils.isBlank(customerAddress.getCustomerStateCode())
                        && !StringUtils.isBlank(customerAddress.getCustomerZipCode());

            } else {
                return !StringUtils.isBlank(customerAddress.getCustomerAddressInternationalProvinceName())
                        && !StringUtils.isBlank(customerAddress.getCustomerInternationalMailCode());
            }
        }
    }

}
