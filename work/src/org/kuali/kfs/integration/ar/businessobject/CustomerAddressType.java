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
package org.kuali.kfs.integration.ar.businessobject;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddressType;

public class CustomerAddressType implements AccountsReceivableCustomerAddressType {

    private String customerAddressTypeCode;
    private String customerAddressTypeDescription;
    private boolean active;

    @Override
    public String getCustomerAddressTypeCode() {
        return customerAddressTypeCode;
    }

    @Override
    public void setCustomerAddressTypeCode(String customerAddressTypeCode) {
        this.customerAddressTypeCode = customerAddressTypeCode;
    }

    @Override
    public String getCustomerAddressTypeDescription() {
        return customerAddressTypeDescription;
    }

    @Override
    public void setCustomerAddressTypeDescription(String customerAddressTypeDescription) {
        this.customerAddressTypeDescription = customerAddressTypeDescription;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void refresh() {
    }
}
