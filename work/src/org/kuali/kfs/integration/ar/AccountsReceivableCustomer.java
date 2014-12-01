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
}
