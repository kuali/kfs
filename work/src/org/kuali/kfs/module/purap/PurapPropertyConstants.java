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
package org.kuali.module.purap;

/**
 * Property name constants.
 * 
 */
public class PurapPropertyConstants {
    
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
    public static final String DEFAULT_APO_LIMIT = "organizationAutomaticPurchaseOrderLimit";
    public static final String VENDOR_CONTRACT_BEGIN_DATE = "vendorContractBeginningDate";
    public static final String VENDOR_CONTRACT_END_DATE = "vendorContractEndDate";
    // Vendor contract organizations
    public static final String VENDOR_CONTRACT_ORGANIZATION = "vendorContractOrganizations";
    public static final String ORGANIZATION_APO_LIMIT = "vendorContractPurchaseOrderLimitAmount";
    
    // Purchase Order & Requisition
    public static final String REQUISITION_ID = "identifier";
    public static final String RECURRING_PAYMENT_TYPE_CODE = "recurringPaymentTypeCode";
    public static final String PURCHASE_ORDER_BEGIN_DATE = "purchaseOrderBeginDate";
    public static final String PURCHASE_ORDER_END_DATE = "purchaseOrderEndDate";
    public static final String VENDOR_POSTAL_CODE = "document.vendorPostalCode";
    public static final String VENDOR_COUNTRY_CODE = "document.vendorCountryCode";
    public static final String VENDOR_STATE_CODE = "document.vendorStateCode";
    public static final String PURCHASE_ORDER_TOTAL_LIMIT = "document.purchaseOrderTotalLimit";
    public static final String REQUISITION_VENDOR_FAX_NUMBER = "document.vendorFaxNumber";
    public static final String STATUS_CODE = "statusCode";
    public static final String DOCUMENT_IDENTIFIER = "purapDocumentIdentifier";
    public static final String CONTRACT_MANAGER_CODE = "contractManagerCode";
    public static final String VENDOR_STIPULATION = "purchaseOrderVendorStipulations";
    public static final String VENDOR_STIPULATION_DESCRIPTION = "vendorStipulationDescription";
    
    public static final String SOURCE_DOCUMENT_IDENTIFIER  = "sourceDocumentIdentifier";
}