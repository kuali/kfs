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
package org.kuali.kfs.module.ar.batch.vo;

/**
 * Customer upload batch process CSV headers
 * 
 * The enum order should match the order of headers in the CSV file.  Also the listing of
 * enum indicate the required fields
 * 
 *  This enum class is used for file parsing and validation
 */
public enum CustomerAddressCSV {

    customerNumber,
    customerName,
    customerParentCompanyNumber,
    customerTypeCode,
    customerLastActivityDate,
    customerTaxTypeCode,
    customerTaxNbr,
    customerActiveIndicator,
    customerPhoneNumber,
    customer800PhoneNumber,
    customerContactName,
    customerContactPhoneNumber,
    customerFaxNumber,
    customerBirthDate,
    customerTaxExemptIndicator,
    customerCreditLimitAmount,
    customerCreditApprovedByName,
    customerEmailAddress,
    customerAddressName,
    customerLine1StreetAddress,
    customerLine2StreetAddress,
    customerCityName,
    customerStateCode,
    customerZipCode,
    customerCountryCode,
    customerAddressInternationalProvinceName,
    customerInternationalMailCode,
    customerAddressEmail,
    customerAddressTypeCode
    
}
