/*
 * Copyright 2011 The Kuali Foundation
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
