/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import java.sql.Date;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.PurapConstants.*;

public enum PurchasingDocumentFixture {

    // REQUISITION FIXTURES
    REQ_ONLY_REQUIRED_FIELDS("INST", RequisitionSources.STANDARD_ORDER, POTransmissionMethods.NOPRINT, POCostSources.ESTIMATE, null, null, 
            "UA", "PUR", "BL", null, null, null, null, null, null, "RENFROW,ROBERTA G", "test@email.com", "555-555-5555", null, null, null, null,
            "ADMIN", "Administration", "123", "1 big dog", null, "campus", "AK", "46202-5260", null, "me", null, null, null, null, null, null,
            null, null, null, "THE UNIVERSITY", "ACCOUNTS PAYABLE", null, "BUTTER NUT", "SC", "47402", "US", null, null, null, false, null, null, null),

    // PURCHASE ORDER FIXTURES
    PO_ONLY_REQUIRED_FIELDS("INST", RequisitionSources.STANDARD_ORDER, POTransmissionMethods.NOPRINT, POCostSources.ESTIMATE, null, null, 
            "UA", "PUR", "BL", null, null, null, null, null, null, "RENFROW,ROBERTA G", "test@email.com", "555-555-5555", null, null, null, null,
            "ADMIN", "Administration", "123", "1 big dog", null, "campus", "AK", "46202-5260", null, "me", null, null, null, null, null, null,
            null, null, null, "THE UNIVERSITY", "ACCOUNTS PAYABLE", null, "BUTTER NUT", "SC", "47402", "US", null, null, 10, false, null, null, null),
    ;
    
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

    private PurchasingDocumentFixture(
            String fundingSourceCode,
            String requisitionSourceCode,
            String purchaseOrderTransmissionMethodCode,
            String purchaseOrderCostSourceCode,
            String deliveryRequiredDateReasonCode,
            String recurringPaymentTypeCode,
            String chartOfAccountsCode,
            String organizationCode,
            String deliveryCampusCode,
            KualiDecimal purchaseOrderTotalLimit,
            Boolean vendorRestrictedIndicator,
            String vendorPhoneNumber,
            String vendorFaxNumber,
            Integer vendorContractGeneratedIdentifier,
            String vendorNoteText,
            String requestorPersonName,
            String requestorPersonEmailAddress,
            String requestorPersonPhoneNumber,
            String nonInstitutionFundOrgChartOfAccountsCode,
            String nonInstitutionFundOrganizationCode,
            String nonInstitutionFundChartOfAccountsCode,
            String nonInstitutionFundAccountNumber,
            String deliveryBuildingCode,
            String deliveryBuildingName,
            String deliveryBuildingRoomNumber,
            String deliveryBuildingLine1Address,
            String deliveryBuildingLine2Address,
            String deliveryCityName,
            String deliveryStateCode,
            String deliveryPostalCode,
            String deliveryCountryCode,
            String deliveryToName,
            String deliveryToEmailAddress,
            String deliveryToPhoneNumber,
            Date deliveryRequiredDate,
            String deliveryInstructionText,
            Date purchaseOrderBeginDate,
            Date purchaseOrderEndDate,
            String institutionContactName,
            String institutionContactPhoneNumber,
            String institutionContactEmailAddress,
            String billingName,
            String billingLine1Address,
            String billingLine2Address,
            String billingCityName,
            String billingStateCode,
            String billingPostalCode,
            String billingCountryCode,
            String billingPhoneNumber,
            String externalOrganizationB2bSupplierIdentifier,
            Integer contractManagerCode,
            boolean purchaseOrderAutomaticIndicator,
            String vendorPaymentTermsCode,
            String vendorShippingTitleCode,
            String vendorShippingPaymentTermsCode) {
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
        return (RequisitionDocument)createPurchasingDocument(RequisitionDocument.class, purapFixture);
    }

    public PurchaseOrderDocument createPurchaseOrderDocument(PurchasingAccountsPayableDocumentFixture purapFixture) {
        return (PurchaseOrderDocument)createPurchasingDocument(PurchaseOrderDocument.class, purapFixture);
    }

    private PurchasingDocument createPurchasingDocument(Class clazz, PurchasingAccountsPayableDocumentFixture purapFixture) {
        PurchasingDocument doc = (PurchasingDocument)purapFixture.createPurchasingAccountsPayableDocument(clazz);
        doc.setFundingSourceCode(this.fundingSourceCode);
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
        doc.setContractManagerCode(this.contractManagerCode);
        doc.setPurchaseOrderAutomaticIndicator(this.purchaseOrderAutomaticIndicator);
        doc.setVendorPaymentTermsCode(this.vendorPaymentTermsCode);
        doc.setVendorShippingTitleCode(this.vendorShippingTitleCode);
        doc.setVendorShippingPaymentTermsCode(this.vendorShippingPaymentTermsCode);
        return doc;
    }

}
