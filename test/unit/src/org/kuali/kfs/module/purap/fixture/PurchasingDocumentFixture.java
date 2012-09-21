/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.fixture;

import java.sql.Date;

import org.kuali.kfs.module.purap.PurapConstants.POCostSources;
import org.kuali.kfs.module.purap.PurapConstants.POTransmissionMethods;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PurchasingDocumentFixture {

    // REQUISITION FIXTURES
    REQ_ONLY_REQUIRED_FIELDS("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            null, // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode
            
    REQ_PERFORMANCE("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "BL", // chartOfAccountsCode
            "ACSP", // organizationCode
            "BL", // deliveryCampusCode
            null, // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "PARKE, WILLIE T", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode
    
    REQ_MULTI("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "KO", // chartOfAccountsCode
            "SBSC", // organizationCode
            "KO", // deliveryCampusCode
            null, // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "WATSON,TERRENCE G", // requestorPersonName
            "tw@localhost.localhost", // requestorPersonEmailAddress
            "812-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "100", // deliveryBuildingRoomNumber
            "98 smart street", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "brainy", // deliveryCityName
            "CA", // deliveryStateCode
            "46202", // deliveryPostalCode
            "US", // deliveryCountryCode
            "front desk", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode

    REQ_WITH_RECURRING_PAYMENT_TYPE("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            "FVAR", // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            null, // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode

    REQ_WITH_PO_TOTAL_LIMIT_NON_ZERO("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            new KualiDecimal(100), // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode           
                  
    REQ_B2B_VENDOR("INST", // fundingSourceCode
            RequisitionSources.B2B, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            new KualiDecimal(100), // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode     
                  
        REQ_B2B_VENDOR_NO_TOTAL_LIMIT("INST", // fundingSourceCode
            RequisitionSources.B2B, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            null, // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            new Integer(1021), // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            "111 Hagadorn Rd", // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode     
                    
    REQ_INVALID_VENDOR_FAX_NUMBER_CONTAINS_LETTER("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            new KualiDecimal(100), // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            "ABC-DEF-1234", // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode     
                    
    REQ_INVALID_VENDOR_FAX_NUMBER_BAD_FORMAT("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            new KualiDecimal(100), // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            "1112223333", // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode     

    REQ_VALID_VENDOR_FAX_NUMBER("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            new KualiDecimal(100), // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            "111-222-3333", // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode     
                    
    REQ_PO_BEGIN_DATE_AFTER_END_DATE("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            new KualiDecimal(100), // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(), // purchaseOrderBeginDate
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight(), // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode     

    REQ_PO_BEGIN_DATE_NO_END_DATE("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            new KualiDecimal(100), // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(), // purchaseOrderBeginDate
            null, // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode     
                    
    REQ_PO_END_DATE_NO_BEGIN_DATE("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            new KualiDecimal(100), // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            null, // purchaseOrderBeginDate
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(), // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode     

    REQ_WITH_BEGIN_AND_END_DATE_WITHOUT_RECURRING_PAYMENT_TYPE("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            null, // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            null, // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()/2), // purchaseOrderBeginDate
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(), // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode            
            
    REQ_WITH_RECURRING_PAYMENT_TYPE_BEGIN_AND_END_DATE("INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            POCostSources.ESTIMATE, // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            "FVAR", // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "PUR", // organizationCode
            "BL", // deliveryCampusCode
            null, // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            null, // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()/2), // purchaseOrderBeginDate
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(), // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            null, // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null), // vendorShippingPaymentTermsCode
                    
    // PURCHASE ORDER FIXTURES
    PO_ONLY_REQUIRED_FIELDS("INST", RequisitionSources.STANDARD_ORDER, POTransmissionMethods.NOPRINT, POCostSources.ESTIMATE, null, null, "UA", "PUR", "BL", null, null, null, null, null, null, "RENFROW,ROBERTA G", "test@email.com", "555-555-5555", null, null, null, null, "ADMN", "Administration", "123", "1 big dog", null, "campus", "AK", "46202-5260", "US", "me", null, null, null, null, null, null, null, null, null, "THE UNIVERSITY", "ACCOUNTS PAYABLE", null, "BUTTER NUT", "SC", "47402", "US", "111-111-1111", null, 10, false, null, null, null),
    PO_ONLY_REQUIRED_FIELDS_2("INST", RequisitionSources.STANDARD_ORDER, POTransmissionMethods.NOPRINT, POCostSources.ESTIMATE, null, null, "UA", "PUR", "BL", null, null, null, null, null, null, "RENFROW,ROBERTA G", "test@email.com", "555-555-5555", null, null, null, null, "ADMN", "Administration", "123", "1 big dog", null, "campus", "AK", "46202-5260", "US", "me", null, null, null, null, null, null, null, null, null, "THE UNIVERSITY", "ACCOUNTS PAYABLE", null, "BUTTER NUT", "SC", "47402", "US", "111-111-1111", null, 10, false, "00N10", null, "CL"),

    PO_WITH_VENDOR_CONTRACT(
            "INST", // fundingSourceCode
            RequisitionSources.STANDARD_ORDER, // requisitionSourceCode
            POTransmissionMethods.NOPRINT, // purchaseOrderTransmissionMethodCode
            "VEN", // purchaseOrderCostSourceCode
            null, // deliveryRequiredDateReasonCode
            "FVAR", // recurringPaymentTypeCode
            "UA", // chartOfAccountsCode
            "VPIT", // organizationCode
            "BL", // deliveryCampusCode
            null, // purchaseOrderTotalLimit
            null, // vendorRestrictedIndicator
            null, // vendorPhoneNumber
            null, // vendorFaxNumber
            new Integer(1021), // vendorContractGeneratedIdentifier
            null, // vendorNoteText
            "RENFROW,ROBERTA G", // requestorPersonName
            "test@email.com", // requestorPersonEmailAddress
            "555-555-5555", // requestorPersonPhoneNumber
            null, // nonInstitutionFundOrgChartOfAccountsCode
            null, // nonInstitutionFundOrganizationCode
            null, // nonInstitutionFundChartOfAccountsCode
            null, // nonInstitutionFundAccountNumber
            "ADMN", // deliveryBuildingCode
            "Administration", // deliveryBuildingName
            "123", // deliveryBuildingRoomNumber
            "1 big dog", // deliveryBuildingLine1Address
            null, // deliveryBuildingLine2Address
            "campus", // deliveryCityName
            "AK", // deliveryStateCode
            "46202-5260", // deliveryPostalCode
            "US", // deliveryCountryCode
            "me", // deliveryToName
            null, // deliveryToEmailAddress
            null, // deliveryToPhoneNumber
            null, // deliveryRequiredDate
            null, // deliveryInstructionText
            new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()/2), // purchaseOrderBeginDate
            SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(), // purchaseOrderEndDate
            null, // institutionContactName
            null, // institutionContactPhoneNumber
            null, // institutionContactEmailAddress
            "THE UNIVERSITY", // billingName
            "ACCOUNTS PAYABLE", // billingLine1Address
            null, // billingLine2Address
            "BUTTER NUT", // billingCityName
            "SC", // billingStateCode
            "47402", // billingPostalCode
            "US", // billingCountryCode
            "111-111-1111", // billingPhoneNumber
            null, // externalOrganizationB2bSupplierIdentifier
            new Integer(10), // contractManagerCode
            false, // purchaseOrderAutomaticIndicator
            null, // vendorPaymentTermsCode
            null, // vendorShippingTitleCode
            null); // vendorShippingPaymentTermsCode
                    
    public final String fundingSourceCode;
    public final String requisitionSourceCode;
    public final String purchaseOrderTransmissionMethodCode;
    public final String purchaseOrderCostSourceCode;
    public final String deliveryRequiredDateReasonCode;
    public final String recurringPaymentTypeCode;
    public final String chartOfAccountsCode;
    public final String organizationCode;
    public final String deliveryCampusCode;
    public final KualiDecimal purchaseOrderTotalLimit;
    public final Boolean vendorRestrictedIndicator;
    public final String vendorPhoneNumber;
    public final String vendorFaxNumber;
    public final Integer vendorContractGeneratedIdentifier;
    public final String vendorNoteText;
    public final String requestorPersonName;
    public final String requestorPersonEmailAddress;
    public final String requestorPersonPhoneNumber;
    public final String nonInstitutionFundOrgChartOfAccountsCode;
    public final String nonInstitutionFundOrganizationCode;
    public final String nonInstitutionFundChartOfAccountsCode;
    public final String nonInstitutionFundAccountNumber;
    public final String deliveryBuildingCode;
    public final String deliveryBuildingName;
    public final String deliveryBuildingRoomNumber;
    public final String deliveryBuildingLine1Address;
    public final String deliveryBuildingLine2Address;
    public final String deliveryCityName;
    public final String deliveryStateCode;
    public final String deliveryPostalCode;
    public final String deliveryCountryCode;
    public final String deliveryToName;
    public final String deliveryToEmailAddress;
    public final String deliveryToPhoneNumber;
    public final Date deliveryRequiredDate;
    public final String deliveryInstructionText;
    public final Date purchaseOrderBeginDate;
    public final Date purchaseOrderEndDate;
    public final String institutionContactName;
    public final String institutionContactPhoneNumber;
    public final String institutionContactEmailAddress;
    public final String billingName;
    public final String billingLine1Address;
    public final String billingLine2Address;
    public final String billingCityName;
    public final String billingStateCode;
    public final String billingPostalCode;
    public final String billingCountryCode;
    public final String billingPhoneNumber;
    public final String externalOrganizationB2bSupplierIdentifier;
    public final Integer contractManagerCode;
    public final boolean purchaseOrderAutomaticIndicator;
    public final String vendorPaymentTermsCode;
    public final String vendorShippingTitleCode;
    public final String vendorShippingPaymentTermsCode;

    private PurchasingDocumentFixture(String fundingSourceCode, String requisitionSourceCode, String purchaseOrderTransmissionMethodCode, String purchaseOrderCostSourceCode, String deliveryRequiredDateReasonCode, String recurringPaymentTypeCode, String chartOfAccountsCode, String organizationCode, String deliveryCampusCode, KualiDecimal purchaseOrderTotalLimit, Boolean vendorRestrictedIndicator, String vendorPhoneNumber, String vendorFaxNumber, Integer vendorContractGeneratedIdentifier, String vendorNoteText, String requestorPersonName, String requestorPersonEmailAddress, String requestorPersonPhoneNumber, String nonInstitutionFundOrgChartOfAccountsCode, String nonInstitutionFundOrganizationCode, String nonInstitutionFundChartOfAccountsCode, String nonInstitutionFundAccountNumber, String deliveryBuildingCode, String deliveryBuildingName, String deliveryBuildingRoomNumber, String deliveryBuildingLine1Address, String deliveryBuildingLine2Address, String deliveryCityName,
            String deliveryStateCode, String deliveryPostalCode, String deliveryCountryCode, String deliveryToName, String deliveryToEmailAddress, String deliveryToPhoneNumber, Date deliveryRequiredDate, String deliveryInstructionText, Date purchaseOrderBeginDate, Date purchaseOrderEndDate, String institutionContactName, String institutionContactPhoneNumber, String institutionContactEmailAddress, String billingName, String billingLine1Address, String billingLine2Address, String billingCityName, String billingStateCode, String billingPostalCode, String billingCountryCode, String billingPhoneNumber, String externalOrganizationB2bSupplierIdentifier, Integer contractManagerCode, boolean purchaseOrderAutomaticIndicator, String vendorPaymentTermsCode, String vendorShippingTitleCode, String vendorShippingPaymentTermsCode) {
        this.fundingSourceCode = fundingSourceCode;
        this.requisitionSourceCode = requisitionSourceCode;
        this.purchaseOrderTransmissionMethodCode = purchaseOrderTransmissionMethodCode;
        this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.organizationCode = organizationCode;
        this.deliveryCampusCode = deliveryCampusCode;
        this.purchaseOrderTotalLimit = purchaseOrderTotalLimit;
        this.vendorRestrictedIndicator = vendorRestrictedIndicator;
        this.vendorPhoneNumber = vendorPhoneNumber;
        this.vendorFaxNumber = vendorFaxNumber;
        this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
        this.vendorNoteText = vendorNoteText;
        this.requestorPersonName = requestorPersonName;
        this.requestorPersonEmailAddress = requestorPersonEmailAddress;
        this.requestorPersonPhoneNumber = requestorPersonPhoneNumber;
        this.nonInstitutionFundOrgChartOfAccountsCode = nonInstitutionFundOrgChartOfAccountsCode;
        this.nonInstitutionFundOrganizationCode = nonInstitutionFundOrganizationCode;
        this.nonInstitutionFundChartOfAccountsCode = nonInstitutionFundChartOfAccountsCode;
        this.nonInstitutionFundAccountNumber = nonInstitutionFundAccountNumber;
        this.deliveryBuildingCode = deliveryBuildingCode;
        this.deliveryBuildingName = deliveryBuildingName;
        this.deliveryBuildingRoomNumber = deliveryBuildingRoomNumber;
        this.deliveryBuildingLine1Address = deliveryBuildingLine1Address;
        this.deliveryBuildingLine2Address = deliveryBuildingLine2Address;
        this.deliveryCityName = deliveryCityName;
        this.deliveryStateCode = deliveryStateCode;
        this.deliveryPostalCode = deliveryPostalCode;
        this.deliveryCountryCode = deliveryCountryCode;
        this.deliveryToName = deliveryToName;
        this.deliveryToEmailAddress = deliveryToEmailAddress;
        this.deliveryToPhoneNumber = deliveryToPhoneNumber;
        this.deliveryRequiredDate = deliveryRequiredDate;
        this.deliveryInstructionText = deliveryInstructionText;
        this.purchaseOrderBeginDate = purchaseOrderBeginDate;
        this.purchaseOrderEndDate = purchaseOrderEndDate;
        this.institutionContactName = institutionContactName;
        this.institutionContactPhoneNumber = institutionContactPhoneNumber;
        this.institutionContactEmailAddress = institutionContactEmailAddress;
        this.billingName = billingName;
        this.billingLine1Address = billingLine1Address;
        this.billingLine2Address = billingLine2Address;
        this.billingCityName = billingCityName;
        this.billingStateCode = billingStateCode;
        this.billingPostalCode = billingPostalCode;
        this.billingCountryCode = billingCountryCode;
        this.billingPhoneNumber = billingPhoneNumber;
        this.externalOrganizationB2bSupplierIdentifier = externalOrganizationB2bSupplierIdentifier;
        this.contractManagerCode = contractManagerCode;
        this.purchaseOrderAutomaticIndicator = purchaseOrderAutomaticIndicator;
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
        this.vendorShippingTitleCode = vendorShippingTitleCode;
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public RequisitionDocument createRequisitionDocument(PurchasingAccountsPayableDocumentFixture purapFixture) {
        return (RequisitionDocument) createPurchasingDocument(RequisitionDocument.class, purapFixture);
    }

    public PurchaseOrderDocument createPurchaseOrderDocument(PurchasingAccountsPayableDocumentFixture purapFixture) {
        return (PurchaseOrderDocument) createPurchasingDocument(PurchaseOrderDocument.class, purapFixture);
    }

    public PurchaseOrderAmendmentDocument createPurchaseOrderAmendmentDocument(PurchasingAccountsPayableDocumentFixture purapFixture) {
        return (PurchaseOrderAmendmentDocument) createPurchasingDocument(PurchaseOrderAmendmentDocument.class, purapFixture);
    }

    public PurchasingDocument createPurchasingDocument(Class clazz, PurchasingAccountsPayableDocumentFixture purapFixture) {
        PurchasingDocument doc = (PurchasingDocument) purapFixture.createPurchasingAccountsPayableDocument(clazz);
        doc.setDocumentFundingSourceCode(this.fundingSourceCode);
        doc.setRequisitionSourceCode(this.requisitionSourceCode);
        doc.setPurchaseOrderTransmissionMethodCode(this.purchaseOrderTransmissionMethodCode);
        doc.setPurchaseOrderCostSourceCode(this.purchaseOrderCostSourceCode);
        doc.setDeliveryRequiredDateReasonCode(this.deliveryRequiredDateReasonCode);
        doc.setRecurringPaymentTypeCode(this.recurringPaymentTypeCode);
        doc.setChartOfAccountsCode(this.chartOfAccountsCode);
        doc.setOrganizationCode(this.organizationCode);
        doc.setDeliveryCampusCode(this.deliveryCampusCode);
        doc.setPurchaseOrderTotalLimit(this.purchaseOrderTotalLimit);
        doc.setVendorRestrictedIndicator(this.vendorRestrictedIndicator);
        doc.setVendorPhoneNumber(this.vendorPhoneNumber);
        doc.setVendorFaxNumber(this.vendorFaxNumber);
        doc.setVendorContractGeneratedIdentifier(this.vendorContractGeneratedIdentifier);
        doc.setVendorNoteText(this.vendorNoteText);
        doc.setRequestorPersonName(this.requestorPersonName);
        doc.setRequestorPersonEmailAddress(this.requestorPersonEmailAddress);
        doc.setRequestorPersonPhoneNumber(this.requestorPersonPhoneNumber);
        doc.setNonInstitutionFundOrgChartOfAccountsCode(this.nonInstitutionFundOrgChartOfAccountsCode);
        doc.setNonInstitutionFundOrganizationCode(this.nonInstitutionFundOrganizationCode);
        doc.setNonInstitutionFundChartOfAccountsCode(this.nonInstitutionFundChartOfAccountsCode);
        doc.setNonInstitutionFundAccountNumber(this.nonInstitutionFundAccountNumber);
        doc.setDeliveryBuildingCode(this.deliveryBuildingCode);
        doc.setDeliveryBuildingName(this.deliveryBuildingName);
        doc.setDeliveryBuildingRoomNumber(this.deliveryBuildingRoomNumber);
        doc.setDeliveryBuildingLine1Address(this.deliveryBuildingLine1Address);
        doc.setDeliveryBuildingLine2Address(this.deliveryBuildingLine2Address);
        doc.setDeliveryCityName(this.deliveryCityName);
        doc.setDeliveryStateCode(this.deliveryStateCode);
        doc.setDeliveryPostalCode(this.deliveryPostalCode);
        doc.setDeliveryCountryCode(this.deliveryCountryCode);
        doc.setDeliveryToName(this.deliveryToName);
        doc.setDeliveryToEmailAddress(this.deliveryToEmailAddress);
        doc.setDeliveryToPhoneNumber(this.deliveryToPhoneNumber);
        doc.setDeliveryRequiredDate(this.deliveryRequiredDate);
        doc.setDeliveryInstructionText(this.deliveryInstructionText);
        doc.setPurchaseOrderBeginDate(this.purchaseOrderBeginDate);
        doc.setPurchaseOrderEndDate(this.purchaseOrderEndDate);
        doc.setInstitutionContactName(this.institutionContactName);
        doc.setInstitutionContactPhoneNumber(this.institutionContactPhoneNumber);
        doc.setInstitutionContactEmailAddress(this.institutionContactEmailAddress);
        doc.setBillingName(this.billingName);
        doc.setBillingLine1Address(this.billingLine1Address);
        doc.setBillingLine2Address(this.billingLine2Address);
        doc.setBillingCityName(this.billingCityName);
        doc.setBillingStateCode(this.billingStateCode);
        doc.setBillingPostalCode(this.billingPostalCode);
        doc.setBillingCountryCode(this.billingCountryCode);
        doc.setBillingPhoneNumber(this.billingPhoneNumber);
        doc.setExternalOrganizationB2bSupplierIdentifier(this.externalOrganizationB2bSupplierIdentifier);
        // Req doesn't have contract manager anymore, only PO still has contract manager.
        if (doc instanceof PurchaseOrderDocument) {
            ((PurchaseOrderDocument) doc).setContractManagerCode(this.contractManagerCode);
        }
        doc.setPurchaseOrderAutomaticIndicator(this.purchaseOrderAutomaticIndicator);
        doc.setVendorPaymentTermsCode(this.vendorPaymentTermsCode);
        doc.setVendorShippingTitleCode(this.vendorShippingTitleCode);
        doc.setVendorShippingPaymentTermsCode(this.vendorShippingPaymentTermsCode);
        return doc;
    }

}
