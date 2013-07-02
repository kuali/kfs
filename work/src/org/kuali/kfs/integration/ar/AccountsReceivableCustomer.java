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
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public interface AccountsReceivableCustomer extends ExternalizableBusinessObject {

    public String getCustomerNumber();

    public void setCustomerNumber(String customerNumber);

    public String getCustomerName();

    public void setCustomerName(String customerName);

    public String getCustomerParentCompanyNumber();

    public String getCustomerTypeCode();

    public void setCustomerTypeCode(String customerTypeCode);

    public String getCustomerTypeDescription();

    public Date getCustomerAddressChangeDate();

    public void setCustomerAddressChangeDate(Date customerAddressChangeDate);

    public Date getCustomerRecordAddDate();

    public Date getCustomerLastActivityDate();

    public boolean isActive();

    public void setActive(boolean active);

    public String getCustomerPhoneNumber();

    public void setCustomerPhoneNumber(String customerPhoneNumber);

    public String getCustomer800PhoneNumber();

    public String getCustomerContactName();

    public String getCustomerContactPhoneNumber();

    public String getCustomerFaxNumber();

    public Date getCustomerBirthDate();

    public KualiDecimal getCustomerCreditLimitAmount();

    public String getCustomerCreditApprovedByName();

    public String getCustomerEmailAddress();

    public void setCustomerEmailAddress(String customerEmailAddress);

    public boolean isCustomerTaxExemptIndicator();

    public String getCustomerTaxNbr();

    public String getCustomerTaxTypeCode();

    public AccountsReceivableCustomerAddress getPrimaryAddress();

    public List<AccountsReceivableCustomerAddress> getAccountsReceivableCustomerAddresses();

    public void setAccountsReceivableCustomerAddresses(List<AccountsReceivableCustomerAddress> customerAddresses);

    public void setCustomerRecordAddDate(Date customerRecordAddDate);

    public void setCustomerLastActivityDate(Date customerLastActivityDate);

    public void setCustomerBirthDate(Date customerBirthDate);

    public void setCustomerParentCompanyNumber(String customerParentCompanyNumber);
    
    public boolean isStopWorkIndicator();
}