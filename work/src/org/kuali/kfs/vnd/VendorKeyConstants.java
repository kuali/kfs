/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.vendor;

/**
 * Holds error key constants for Vendor.
 * 
 */
public class VendorKeyConstants {

    //Vendor Maintenance
    public static final String ERROR_VENDOR_TYPE_REQUIRES_TAX_NUMBER = "error.vendorMaint.VendorTypeRequiresTaxNumber";
    public static final String ERROR_VENDOR_TAX_TYPE_AND_NUMBER_COMBO_EXISTS = "error.vendorMaint.addVendor.vendor.exists";
    public static final String ERROR_VENDOR_NAME_REQUIRED = "error.vendorMaint.vendorName.required";
    public static final String ERROR_VENDOR_BOTH_NAME_REQUIRED = "error.vendorMaint.bothNameRequired";
    public static final String ERROR_VENDOR_NAME_INVALID = "error.vendorMaint.nameInvalid";
    public static final String ERROR_VENDOR_TAX_TYPE_ALLOWED = "error.vendorMaint.tax.type.allowed";
    public static final String ERROR_VENDOR_MAX_MIN_ORDER_AMOUNT = "error.vendorMaint.minimumAmt.invalid";
    public static final String ERROR_VENDOR_TAX_TYPE_CANNOT_BE_BLANK = "error.vendorMaint.tax.type.cannot.be.blank";
    public static final String ERROR_VENDOR_TAX_TYPE_CANNOT_BE_SET = "error.vendorMaint.tax.type.cannot.be.set";
    public static final String ERROR_VENDOR_PARENT_NEEDS_CHANGED = "error.vendorMaint.vendorParent.needs.changed";
    public static final String ERROR_TAX_NUMBER_ALL_DIGITS_AND_LENGTH= "error.vendorMaint.taxNumber.isAllDigits.and.length";
    public static final String ERROR_TAX_NUMBER_ALL_ZEROES="error.vendorMaint.taxNumber.isAllZeroes";
    public static final String ERROR_FIRST_THREE_SSN = "error.vendorMaint.first.three.ssn";
    public static final String ERROR_MIDDLE_TWO_SSN = "error.vendorMaint.middle.two.ssn";
    public static final String ERROR_LAST_FOUR_SSN = "error.vendorMaint.last.four.ssn";
    public static final String ERROR_FIRST_TWO_FEIN = "error.vendorMaint.first.two.fein";
    public static final String ERROR_TAX_NUMBER_INVALID = "error.vendorMaint.taxNumber.invalid";
    public static final String ERROR_TAX_NUMBER_NOT_ALLOWED = "error.vendorMaint.taxNumber.notAllowed";
    public static final String ERROR_OWNERSHIP_CATEGORY_CODE_NOT_ALLOWED = "error.vendorMaint.ownershipCategoryCode.notAllowed";
    public static final String ERROR_OWNERSHIP_TYPE_CODE_NOT_ALLOWED = "error.vendorMaint.ownershipTypeCode.notAllowed";
    public static final String ERROR_INACTIVE_REASON_REQUIRED = "error.vendorMaint.inactiveReason.required";
    
    //Vendor Lookup
    public static final String ERROR_VENDOR_LOOKUP_NAME_TOO_SHORT = "error.vendorLookup.name.too.short";
    public static final String ERROR_VENDOR_LOOKUP_FEWER_THAN_MIN_CRITERIA = "error.vendorLookup.min.criteria.fewer";
    public static final String ERROR_VENDOR_LOOKUP_PAYEE_ID_NO_STARTING_P = "error.vendorLookup.payeeId.no.starting.p";
    public static final String ERROR_VENDOR_LOOKUP_TYPE_NO_NAME_OR_STATE = "error.vendorLookup.type.no.name.or.state";
    public static final String ERROR_VENDOR_LOOKUP_STATUS_NO_NAME = "error.vendorLookup.status.no.name";
    public static final String ERROR_VENDOR_LOOKUP_TAX_NUM_INVALID = "error.vendorLookup.taxNum.invalid";
    public static final String ERROR_VENDOR_LOOKUP_VNDR_NUM_TOO_MANY_DASHES = "error.vendorLookup.vndrNum.dashes.tooMany";
    public static final String ERROR_VENDOR_LOOKUP_VNDR_NUM_DASHES_ONLY = "error.vendorLookup.vndrNum.dashes.only";
    public static final String ERROR_VENDOR_LOOKUP_VNDR_NUM_NUMERIC_DASH_SEPARATED = "error.vendorLookup.vndrNum.numeric.dash.separated";
    public static final String ERROR_VENDOR_LOOKUP_STATE_NO_TYPE = "error.vendorLookup.state.no.type";
    
    //Vendor Maintenance Address
    public static final String ERROR_US_REQUIRES_STATE = "error.vendorMaint.vendorAddress.USRequiresStateAndZip";
    public static final String ERROR_US_REQUIRES_ZIP = "error.vendorMaint.vendorAddress.USRequiresStateAndZip";
    public static final String ERROR_FAX_NUMBER = "error.vendorMaint.vendorAddress.faxNumber";
    public static final String ERROR_ADDRESS_TYPE = "error.vendorMaint.vendorAddress.addressType";
    public static final String ERROR_ADDRESS_TYPE_DIVISIONS = "error.vendorMaint.vendorAddress.addressType.divisions";
    public static final String ERROR_ADDRESS_DEFAULT_CAMPUS = "error.vendorMaint.vendorDefaultAddress.campusCode";
    public static final String ERROR_ADDRESS_DEFAULT_INDICATOR = "error.vendorMaint.vendorAddress.vendorDefaultAddressIndicator";
    public static final String ERROR_ADDRESS_DEFAULT_CAMPUS_NOT_ALLOWED = "error.vendorMaint.vendorDefaultCampus.notAllowed";
    public static final String ERROR_ADDRESS_DEFAULT_ADDRESS_NOT_ALLOWED = "error.vendorMaint.vendorDefaultAddress.notAllowed";
    
    
    // Vendor Maintenance Contract
    public static final String ERROR_VENDOR_CONTRACT_NO_APO_LIMIT = "error.vendorContract.noApoLimit";
    public static final String ERROR_VENDOR_CONTRACT_BEGIN_DATE_AFTER_END = "error.vendorContract.beginDateAfterEnd";
    public static final String ERROR_VENDOR_CONTRACT_BEGIN_DATE_NO_END_DATE = "error.vendorContract.beginDateNoEndDate";
    public static final String ERROR_VENDOR_CONTRACT_END_DATE_NO_BEGIN_DATE = "error.vendorContract.endDateNoBeginDate";
    public static final String ERROR_VENDOR_CONTRACT_ORG_EXCLUDED_WITH_APO_LIMIT = "error.vendorContractOrg.excludedWithApoLimit";
    public static final String ERROR_VENDOR_CONTRACT_ORG_NOT_EXCLUDED_NO_APO_LIMIT = "error.vendorContractOrg.notExcludedNoApoLimit";
    public static final String ERROR_VENDOR_CONTRACT_NOT_ALLOWED="error.vendorContract.notAllowed";

}