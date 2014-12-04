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

    public String getCustomerInvoiceTemplateCode();
    
    public String getInvoiceTransmissionMethodCode();
    
    public Integer getCustomerCopiesToPrint(); 
    
    public Integer getCustomerEnvelopesToPrintQuantity();
    @Override
    public void refresh();
}
