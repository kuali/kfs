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
