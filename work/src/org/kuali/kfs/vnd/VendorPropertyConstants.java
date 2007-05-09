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
 * Property name constants.
 * 
 */
public class VendorPropertyConstants {
    
    public static final String DATA_OBJ_MAINT_CD_ACTIVE_IND = "dataObjectMaintenanceCodeActiveIndicator";
    
    public static final String VENDOR_HEADER_PREFIX = "vendorHeader.";
    public static final String VENDOR_NUMBER = "vendorNumber";
    public static final String VENDOR_FAX_NUMBER = "vendorFaxNumber";
    public static final String VENDOR_ADDRESS = "vendorAddresses";
    public static final String VENDOR_DEFAULT_ADDRESS = "vendorDefaultAddresses";
    public static final String VENDOR_SUPPLIER_DIVERSITIES = "vendorSupplierDiversities";
    public static final String VENDOR_ADDRESS_STATE = "vendorStateCode";
    public static final String VENDOR_ADDRESS_STATE_CODE = "vendorAddresses.vendorStateCode";
    public static final String VENDOR_ADDRESS_ZIP = "vendorZipCode";
    public static final String VENDOR_ADDRESS_TYPE_CODE = "vendorAddresses.vendorAddressTypeCode";
    public static final String VENDOR_ADDRESS_EMAIL = "vendorAddresses.vendorAddressEmailAddress";
    public static final String VENDOR_DEFAULT_ADDRESS_INDICATOR = "vendorDefaultAddressIndicator";
    public static final String VENDOR_DEFAULT_ADDRESS_CAMPUS = "vendorCampusCode";
    public static final String VENDOR_SUPPLIER_DIVERSITY_CODE = "vendorSupplierDiversities.vendorSupplierDiversityCode";
    public static final String VENDOR_FEIN_SSN_INDICATOR = "vendorFeinSocialSecurityNumberIndicator";
    public static final String VENDOR_TAX_NUMBER_ONLY = "vendorTaxNumber";
    public static final String VENDOR_NAME = "vendorName";
    public static final String VENDOR_FIRST_NAME = "vendorFirstName";
    public static final String VENDOR_LAST_NAME = "vendorLastName";
    public static final String VENDOR_TAX_NUMBER = "vendorHeader.vendorTaxNumber";
    public static final String VENDOR_TAX_TYPE_CODE = "vendorHeader.vendorTaxTypeCode";
    public static final String VENDOR_OWNERSHIP_CODE = "vendorHeader.vendorOwnershipCode";
    public static final String VENDOR_OWNERSHIP_CATEGORY_CODE = "vendorHeader.vendorOwnershipCategoryCode";
    public static final String VENDOR_FEDERAL_WITHOLDING_TAX_BEGINNING_DATE = "vendorHeader.vendorFederalWithholdingTaxBeginningDate";
    public static final String VENDOR_FEDERAL_WITHOLDING_TAX_END_DATE = "vendorHeader.vendorFederalWithholdingTaxEndDate";
    public static final String VENDOR_TYPE_CODE = "vendorHeader.vendorTypeCode";
    public static final String VENDOR_W9_RECEIVED_INDICATOR = "vendorHeader.vendorW9ReceivedIndicator";
    public static final String VENDOR_W8_BEN_RECEIVED_INDICATOR = "vendorHeader.vendorW8BenReceivedIndicator";
    public static final String VENDOR_DEBARRED_INDICATOR = "vendorHeader.vendorDebarredIndicator";
    public static final String VENDOR_FOREIGN_INDICATOR = "vendorHeader.vendorForeignIndicator";
    public static final String VENDOR_INACTIVE_REASON = "vendorInactiveReasonCode";
    
    public static final String VENDOR_TAX_TYPE_CODE_WITHOUT_HEADER = "vendorTaxTypeCode";
    public static final String VENDOR_MIN_ORDER_AMOUNT = "vendorMinimumOrderAmount";
    public static final String VENDOR_DETAIL_ASSIGNED_ID = "vendorDetailAssignedIdentifier";
    public static final String VENDOR_HEADER_GENERATED_ID = "vendorHeaderGeneratedIdentifier";
    public static final String VENDOR_ALIAS_NAME = "vendorAliasName";
    public static final String VENDOR_ALIAS_NAME_FULL_PATH = "vendorAliases.vendorAliasName";
    
    public static final String CONTRACT_LANGUAGE_CREATE_DATE = "contractLanguageCreateDate";

    // Vendor contracts
    public static final String VENDOR_CONTRACT = "vendorContracts";
    public static final String VENDOR_CONTRACT_DEFAULT_APO_LIMIT = "organizationAutomaticPurchaseOrderLimit";
    public static final String VENDOR_CONTRACT_BEGIN_DATE = "vendorContractBeginningDate";
    public static final String VENDOR_CONTRACT_END_DATE = "vendorContractEndDate";
    public static final String VENDOR_CONTRACT_NAME = "vendorContractName";
    
    // Vendor contract organizations
    public static final String VENDOR_CONTRACT_ORGANIZATION = "vendorContractOrganizations";
    public static final String VENDOR_CONTRACT_ORGANIZATION_APO_LIMIT = "vendorContractPurchaseOrderLimitAmount";
    public static final String VENDOR_CONTRACT_CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    public static final String VENDOR_CONTRACT_ORGANIZATION_CODE = "organizationCode";
    
    // Vendor customer number
    public static final String VENDOR_CUSTOMER_NUMBER_CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    public static final String VENDOR_CUSTOMER_NUMBER_ORGANIZATION_CODE = "vendorOrganizationCode";
    
}