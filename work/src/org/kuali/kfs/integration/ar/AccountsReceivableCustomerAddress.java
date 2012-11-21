/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.integration.ar;

import java.sql.Date;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;


public interface AccountsReceivableCustomerAddress extends ExternalizableBusinessObject {

    public String getCustomerNumber();

    public void setCustomerNumber(String customerNumber);

    public Integer getCustomerAddressIdentifier();

    public String getCustomerAddressName();

    public void setCustomerAddressName(String customerAddressName);

    public String getCustomerLine1StreetAddress();

    public void setCustomerLine1StreetAddress(String customerLine1StreetAddress);

    public String getCustomerLine2StreetAddress();

    public void setCustomerLine2StreetAddress(String customerLine2StreetAddress);

    public String getCustomerCityName();

    public void setCustomerCityName(String customerCityName);

    public String getCustomerStateCode();

    public void setCustomerStateCode(String customerStateCode);

    public String getCustomerZipCode();

    public void setCustomerZipCode(String customerZipCode);

    public String getCustomerAddressInternationalProvinceName();

    public String getCustomerCountryCode();

    public void setCustomerCountryCode(String customerCountryCode);

    public String getCustomerInternationalMailCode();

    public String getCustomerEmailAddress();

    public void setCustomerEmailAddress(String customerEmailAddress);

    public String getCustomerAddressTypeCode();

    public void setCustomerAddressTypeCode(String customerAddressTypeCode);

    public Date getCustomerAddressEndDate();

    public AccountsReceivableCustomerAddressType getAccountsReceivableCustomerAddressType();

    public void setCustomerAddressTypeCodeAsPrimary();

    public AccountsReceivableCustomer getAccountsReceivableCustomer();

    public void setCustomerAddressTypeCodeAsAlternate();

    @Override
    public void refresh();
}