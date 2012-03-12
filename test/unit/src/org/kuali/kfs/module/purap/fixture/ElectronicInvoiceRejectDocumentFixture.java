/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public enum ElectronicInvoiceRejectDocumentFixture {

    EIR_ONLY_REQUIRED_FIELDS(
            new Timestamp(new java.util.Date().getTime()), // invoiceProcessTimestamp
            Boolean.FALSE, // fileHeaderTypeIndicator
            Boolean.FALSE, // fileInformationOnlyIndicator
            Boolean.FALSE, // fileTaxInLineIndicator
            Boolean.FALSE, // fileSpecHandlingInLineIndicator
            Boolean.FALSE, // fileShippingInLineIndicator
            Boolean.FALSE, // fileDiscountInLineIndicator
            "nullfile.xml", // invoiceFileName
            "123456789", // vendorDunsNumber
            1000, // vendorHeaderID
            0, // vendorDetailID
            "2008-07-11", // invoiceFileDate
            "35106833", // invoiceFileNumber
            "standard", // filePurposeId
            "new", // fileOperationId
            null, // deploymentMode
            "446665", // referenceOrderId
            "446665", // referenceDocumentRefPayloadId
            null, // referenceDocumentRefText
            null, // masterAgreementReferenceId
            null, // masterAgreementReferenceDate
            null, // masterAgreementInfoId
            null, // masterAgreementInfoDate
            "446665", // invoiceOrderId
            "2008-07-11", // invoiceOrderDate
            "37625311", // supplierOrderInfoId
            null, // invoiceShipDate
            "Biology, Lynch Lab, A. Seyfert", // shipToAddressName
            "BLBI", // shipToAddressType
            "1001 E 3rd St", // shipToAddressLine1
            "Room #A108", // shipToAddressLine2
            null, // shipToAddressLine3
            "Bloomington", // shipToAddressCityName
            "IN", // shipToAddressStateCode
            "47405-7005", // shipToAddressPostalCode
            "US", // shipToAddressCountryCode
            null, // shipToAddressCountryName
            "IN UNIV BLOOMINGTON", // billToAddressName
            null, // billToAddressType
            "PO BOX 4095", // billToAddressLine1
            "FINANCIAL MGMT SERVICES", // billToAddressLine2
            null, // billToAddressLine3
            "BLOOMINGTON", // billToAddressCityName
            "IN", // billToAddressStateCode
            "47402", // billToAddressPostalCode
            "US", // billToAddressCountryCode
            null, // billToAddressCountryName
            "VWR INTERNATIONAL", // remitToAddressName
            null, // remitToAddressType
            "P. O. BOX 640169", // remitToAddressLine1
            null, // remitToAddressLine2
            null, // remitToAddressLine3
            "PITTSBURGH", // remitToAddressCityName
            "PA", // remitToAddressStateCode
            "15264-0169", // remitToAddressPostalCode
            "US", // remitToAddressCountryCode
            null, // remitToAddressCountryName
            null, // invoiceCustomerNumber
            null, // invoicePurchaseOrderId
            null, // purchaseOrderId
            null, // purchaseOrderDeliveryCampusCode
            "USD", // invoiceSubTotalAmountCurrency
            null, // invoiceSpecialHandlingAmountCurrency
            null, // invoiceSpecialHandlingDescription
            null, // invoiceShippingAmountCurrency
            null, // invoiceShippingDescription
            "USD", // invoiceTaxAmountCurrency
            null, // invoiceTaxDescription
            "USD", // invoiceGrossAmountCurrency
            "USD", // invoiceDiscountAmountCurrency
            "USD", // invoiceNetAmountCurrency
            new BigDecimal("1821.00"), // invoiceSubTotalAmount
            new BigDecimal("1"), // invoiceSpecialHandlingAmount
            new BigDecimal("2"), // invoiceShippingAmount
            new BigDecimal("0"), // invoiceTaxAmount
            new BigDecimal("1821.00"), // invoiceGrossAmount
            new BigDecimal("1.00"), // invoiceDiscountAmount
            new BigDecimal("1821.00"), // invoiceNetAmount
            new ElectronicInvoiceRejectItemFixture[] {ElectronicInvoiceRejectItemFixture.EIRI_BASIC},
            new ElectronicInvoiceRejectReasonFixture[] {}            
    ),
    EIR_MATCHING(
            new Timestamp(new java.util.Date().getTime()), // invoiceProcessTimestamp
            Boolean.FALSE, // fileHeaderTypeIndicator
            Boolean.FALSE, // fileInformationOnlyIndicator
            Boolean.FALSE, // fileTaxInLineIndicator
            Boolean.FALSE, // fileSpecHandlingInLineIndicator
            Boolean.FALSE, // fileShippingInLineIndicator
            Boolean.FALSE, // fileDiscountInLineIndicator
            "nullfile.xml", // invoiceFileName
            "002254837", // vendorDunsNumber
            1001, // vendorHeaderID
            0, // vendorDetailID
            "2008-07-11", // invoiceFileDate
            "35106833", // invoiceFileNumber
            "standard", // filePurposeId
            "new", // fileOperationId
            null, // deploymentMode
            "446665", // referenceOrderId
            "446665", // referenceDocumentRefPayloadId
            null, // referenceDocumentRefText
            null, // masterAgreementReferenceId
            null, // masterAgreementReferenceDate
            null, // masterAgreementInfoId
            null, // masterAgreementInfoDate
            "446665", // invoiceOrderId
            "2008-07-11", // invoiceOrderDate
            "37625311", // supplierOrderInfoId
            null, // invoiceShipDate
            "Biology, Lynch Lab, A. Seyfert", // shipToAddressName
            "BLBI", // shipToAddressType
            "1001 E 3rd St", // shipToAddressLine1
            "Room #A108", // shipToAddressLine2
            null, // shipToAddressLine3
            "Bloomington", // shipToAddressCityName
            "IN", // shipToAddressStateCode
            "47405-7005", // shipToAddressPostalCode
            "US", // shipToAddressCountryCode
            null, // shipToAddressCountryName
            "IN UNIV BLOOMINGTON", // billToAddressName
            null, // billToAddressType
            "PO BOX 4095", // billToAddressLine1
            "FINANCIAL MGMT SERVICES", // billToAddressLine2
            null, // billToAddressLine3
            "BLOOMINGTON", // billToAddressCityName
            "IN", // billToAddressStateCode
            "47402", // billToAddressPostalCode
            "US", // billToAddressCountryCode
            null, // billToAddressCountryName
            "VWR INTERNATIONAL", // remitToAddressName
            null, // remitToAddressType
            "P. O. BOX 640169", // remitToAddressLine1
            null, // remitToAddressLine2
            null, // remitToAddressLine3
            "PITTSBURGH", // remitToAddressCityName
            "PA", // remitToAddressStateCode
            "15264-0169", // remitToAddressPostalCode
            "US", // remitToAddressCountryCode
            null, // remitToAddressCountryName
            null, // invoiceCustomerNumber
            null, // invoicePurchaseOrderId
            null, // purchaseOrderId
            null, // purchaseOrderDeliveryCampusCode
            "USD", // invoiceSubTotalAmountCurrency
            null, // invoiceSpecialHandlingAmountCurrency
            null, // invoiceSpecialHandlingDescription
            null, // invoiceShippingAmountCurrency
            null, // invoiceShippingDescription
            "USD", // invoiceTaxAmountCurrency
            null, // invoiceTaxDescription
            "USD", // invoiceGrossAmountCurrency
            "USD", // invoiceDiscountAmountCurrency
            "USD", // invoiceNetAmountCurrency
            new BigDecimal("1821.00"), // invoiceSubTotalAmount
            new BigDecimal("1"), // invoiceSpecialHandlingAmount
            new BigDecimal("2"), // invoiceShippingAmount
            new BigDecimal("0"), // invoiceTaxAmount
            new BigDecimal("1821.00"), // invoiceGrossAmount
            new BigDecimal("1.00"), // invoiceDiscountAmount
            new BigDecimal("1821.00"), // invoiceNetAmount
            new ElectronicInvoiceRejectItemFixture[] {ElectronicInvoiceRejectItemFixture.EIRI_BASIC},
            new ElectronicInvoiceRejectReasonFixture[] {}            
    ),
    ;

    public Integer purapDocumentIdentifier;
    public Integer accountsPayablePurchasingDocumentLinkIdentifier;
    public Integer invoiceLoadSummaryIdentifier;
    public Timestamp invoiceProcessTimestamp;
    public Boolean invoiceFileHeaderTypeIndicator = Boolean.FALSE;
    public Boolean invoiceFileInformationOnlyIndicator = Boolean.FALSE;
    public Boolean invoiceFileTaxInLineIndicator = Boolean.FALSE;
    public Boolean invoiceFileSpecialHandlingInLineIndicator = Boolean.FALSE;
    public Boolean invoiceFileShippingInLineIndicator = Boolean.FALSE;
    public Boolean invoiceFileDiscountInLineIndicator = Boolean.FALSE;
    
    public String invoiceFileName;
    public String vendorDunsNumber;
    public Integer vendorHeaderGeneratedIdentifier;
    public Integer vendorDetailAssignedIdentifier;
    public String invoiceFileDate;
    public String invoiceFileNumber;
    public String invoiceFilePurposeIdentifier;
    public String invoiceFileOperationIdentifier;
    public String invoiceFileDeploymentModeValue;
    public String invoiceOrderReferenceOrderIdentifier;
    public String invoiceOrderReferenceDocumentReferencePayloadIdentifier;
    public String invoiceOrderReferenceDocumentReferenceText;
    public String invoiceOrderMasterAgreementReferenceIdentifier;
    public String invoiceOrderMasterAgreementReferenceDate;
    public String invoiceOrderMasterAgreementInformationIdentifier;
    public String invoiceOrderMasterAgreementInformationDate;
    public String invoiceOrderPurchaseOrderIdentifier;
    public String invoiceOrderPurchaseOrderDate;
    public String invoiceOrderSupplierOrderInformationIdentifier;
    public String invoiceShipDate;
    public String invoiceShipToAddressName;
    public String invoiceShipToAddressType;
    public String invoiceShipToAddressLine1;
    public String invoiceShipToAddressLine2;
    public String invoiceShipToAddressLine3;
    public String invoiceShipToAddressCityName;
    public String invoiceShipToAddressStateCode;
    public String invoiceShipToAddressPostalCode;
    public String invoiceShipToAddressCountryCode;
    public String invoiceShipToAddressCountryName;
    public String invoiceBillToAddressName;
    public String invoiceBillToAddressType;
    public String invoiceBillToAddressLine1;
    public String invoiceBillToAddressLine2;
    public String invoiceBillToAddressLine3;
    public String invoiceBillToAddressCityName;
    public String invoiceBillToAddressStateCode;
    public String invoiceBillToAddressPostalCode;
    public String invoiceBillToAddressCountryCode;
    public String invoiceBillToAddressCountryName;
    public String invoiceRemitToAddressName;
    public String invoiceRemitToAddressType;
    public String invoiceRemitToAddressLine1;
    public String invoiceRemitToAddressLine2;
    public String invoiceRemitToAddressLine3;
    public String invoiceRemitToAddressCityName;
    public String invoiceRemitToAddressStateCode;
    public String invoiceRemitToAddressPostalCode;
    public String invoiceRemitToAddressCountryCode;
    public String invoiceRemitToAddressCountryName;
    
    public String invoiceCustomerNumber;
    public String invoicePurchaseOrderNumber;
    public Integer purchaseOrderIdentifier;
    public String purchaseOrderDeliveryCampusCode;
    
    public String invoiceItemSubTotalCurrencyCode;
    public String invoiceItemSpecialHandlingCurrencyCode;
    public String invoiceItemSpecialHandlingDescription;
    public String invoiceItemShippingCurrencyCode;
    public String invoiceItemShippingDescription;
    public String invoiceItemTaxCurrencyCode;
    public String invoiceItemTaxDescription;
    public String invoiceItemGrossCurrencyCode;
    public String invoiceItemDiscountCurrencyCode;
    public String invoiceItemNetCurrencyCode;
    
    public BigDecimal invoiceItemSubTotalAmount;
    public BigDecimal invoiceItemSpecialHandlingAmount;
    public BigDecimal invoiceItemShippingAmount;
    public BigDecimal invoiceItemTaxAmount;
    public BigDecimal invoiceItemGrossAmount;
    public BigDecimal invoiceItemDiscountAmount;
    public BigDecimal invoiceItemNetAmount;
    
    public ElectronicInvoiceRejectItemFixture[] invoiceRejectItemFixtures;
    public ElectronicInvoiceRejectReasonFixture[] invoiceRejectReasonFixtures;

    private ElectronicInvoiceRejectDocumentFixture(Timestamp invoiceProcessTimestamp, Boolean invoiceFileHeaderTypeIndicator, Boolean invoiceFileInformationOnlyIndicator, Boolean invoiceFileTaxInLineIndicator, Boolean invoiceFileSpecialHandlingInLineIndicator, Boolean invoiceFileShippingInLineIndicator, Boolean invoiceFileDiscountInLineIndicator, String invoiceFileName, String vendorDunsNumber,
            Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, String invoiceFileDate, String invoiceFileNumber, String invoiceFilePurposeIdentifier, String invoiceFileOperationIdentifier, String invoiceFileDeploymentModeValue, String invoiceOrderReferenceOrderIdentifier, String invoiceOrderReferenceDocumentReferencePayloadIdentifier, String invoiceOrderReferenceDocumentReferenceText,
            String invoiceOrderMasterAgreementReferenceIdentifier, String invoiceOrderMasterAgreementReferenceDate, String invoiceOrderMasterAgreementInformationIdentifier, String invoiceOrderMasterAgreementInformationDate, String invoiceOrderPurchaseOrderIdentifier, String invoiceOrderPurchaseOrderDate, String invoiceOrderSupplierOrderInformationIdentifier, String invoiceShipDate,
            String invoiceShipToAddressName, String invoiceShipToAddressType, String invoiceShipToAddressLine1, String invoiceShipToAddressLine2, String invoiceShipToAddressLine3, String invoiceShipToAddressCityName, String invoiceShipToAddressStateCode, String invoiceShipToAddressPostalCode, String invoiceShipToAddressCountryCode, String invoiceShipToAddressCountryName,
            String invoiceBillToAddressName, String invoiceBillToAddressType, String invoiceBillToAddressLine1, String invoiceBillToAddressLine2, String invoiceBillToAddressLine3, String invoiceBillToAddressCityName, String invoiceBillToAddressStateCode, String invoiceBillToAddressPostalCode, String invoiceBillToAddressCountryCode, String invoiceBillToAddressCountryName,
            String invoiceRemitToAddressName, String invoiceRemitToAddressType, String invoiceRemitToAddressLine1, String invoiceRemitToAddressLine2, String invoiceRemitToAddressLine3, String invoiceRemitToAddressCityName, String invoiceRemitToAddressStateCode, String invoiceRemitToAddressPostalCode, String invoiceRemitToAddressCountryCode, String invoiceRemitToAddressCountryName,
            String invoiceCustomerNumber, String invoicePurchaseOrderNumber, Integer purchaseOrderIdentifier, String purchaseOrderDeliveryCampusCode, String invoiceItemSubTotalCurrencyCode, String invoiceItemSpecialHandlingCurrencyCode,
            String invoiceItemSpecialHandlingDescription, String invoiceItemShippingCurrencyCode, String invoiceItemShippingDescription, String invoiceItemTaxCurrencyCode, String invoiceItemTaxDescription, String invoiceItemGrossCurrencyCode, String invoiceItemDiscountCurrencyCode, String invoiceItemNetCurrencyCode,
            BigDecimal invoiceItemSubTotalAmount, BigDecimal invoiceItemSpecialHandlingAmount, BigDecimal invoiceItemShippingAmount, BigDecimal invoiceItemTaxAmount, BigDecimal invoiceItemGrossAmount, BigDecimal invoiceItemDiscountAmount, BigDecimal invoiceItemNetAmount,
            ElectronicInvoiceRejectItemFixture[] invoiceRejectItemFixtures,
            ElectronicInvoiceRejectReasonFixture[] invoiceRejectReasonFixtures) {

        this.invoiceProcessTimestamp = invoiceProcessTimestamp;
        this.invoiceFileHeaderTypeIndicator = invoiceFileHeaderTypeIndicator;
        this.invoiceFileInformationOnlyIndicator = invoiceFileInformationOnlyIndicator;
        this.invoiceFileTaxInLineIndicator = invoiceFileTaxInLineIndicator;
        this.invoiceFileSpecialHandlingInLineIndicator = invoiceFileSpecialHandlingInLineIndicator;
        this.invoiceFileShippingInLineIndicator = invoiceFileShippingInLineIndicator;
        this.invoiceFileDiscountInLineIndicator = invoiceFileDiscountInLineIndicator;

        this.invoiceFileName = invoiceFileName;
        this.vendorDunsNumber = vendorDunsNumber;
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
        this.invoiceFileDate= invoiceFileDate;
        this.invoiceFileNumber = invoiceFileNumber;
        this.invoiceFilePurposeIdentifier = invoiceFilePurposeIdentifier;
        this.invoiceFileOperationIdentifier = invoiceFileOperationIdentifier;
        this.invoiceFileDeploymentModeValue = invoiceFileDeploymentModeValue;
        this.invoiceOrderReferenceOrderIdentifier = invoiceOrderReferenceOrderIdentifier;
        this.invoiceOrderReferenceDocumentReferencePayloadIdentifier = invoiceOrderReferenceDocumentReferencePayloadIdentifier;
        this.invoiceOrderReferenceDocumentReferenceText = invoiceOrderReferenceDocumentReferenceText;
        this.invoiceOrderMasterAgreementReferenceIdentifier = invoiceOrderMasterAgreementReferenceIdentifier;
        this.invoiceOrderMasterAgreementReferenceDate = invoiceOrderMasterAgreementReferenceDate;
        this.invoiceOrderMasterAgreementInformationIdentifier = invoiceOrderMasterAgreementInformationIdentifier;
        this.invoiceOrderMasterAgreementInformationDate = invoiceOrderMasterAgreementInformationDate;
        this.invoiceOrderPurchaseOrderIdentifier = invoiceOrderPurchaseOrderIdentifier;
        this.invoiceOrderPurchaseOrderDate = invoiceOrderPurchaseOrderDate;
        this.invoiceOrderSupplierOrderInformationIdentifier = invoiceOrderSupplierOrderInformationIdentifier;
        this.invoiceShipDate = invoiceShipDate;
        this.invoiceShipToAddressName = invoiceShipToAddressName;
        this.invoiceShipToAddressType = invoiceShipToAddressType;
        this.invoiceShipToAddressLine1 = invoiceShipToAddressLine1;
        this.invoiceShipToAddressLine2 = invoiceShipToAddressLine2;
        this.invoiceShipToAddressLine3 = invoiceShipToAddressLine3;
        this.invoiceShipToAddressCityName = invoiceShipToAddressCityName;
        this.invoiceShipToAddressStateCode = invoiceShipToAddressStateCode;
        this.invoiceShipToAddressPostalCode = invoiceShipToAddressPostalCode;
        this.invoiceShipToAddressCountryCode = invoiceShipToAddressCountryCode;
        this.invoiceShipToAddressCountryName = invoiceShipToAddressCountryName;
        this.invoiceBillToAddressName = invoiceBillToAddressName;
        this.invoiceBillToAddressType = invoiceBillToAddressType;
        this.invoiceBillToAddressLine1 = invoiceBillToAddressLine1;
        this.invoiceBillToAddressLine2 = invoiceBillToAddressLine2;
        this.invoiceBillToAddressLine3 = invoiceBillToAddressLine3;
        this.invoiceBillToAddressCityName = invoiceBillToAddressCityName;
        this.invoiceBillToAddressStateCode = invoiceBillToAddressStateCode;
        this.invoiceBillToAddressPostalCode = invoiceBillToAddressPostalCode;
        this.invoiceBillToAddressCountryCode = invoiceBillToAddressCountryCode;
        this.invoiceBillToAddressCountryName = invoiceBillToAddressCountryName;
        this.invoiceRemitToAddressName = invoiceRemitToAddressName;
        this.invoiceRemitToAddressType = invoiceRemitToAddressType;
        this.invoiceRemitToAddressLine1 = invoiceRemitToAddressLine1;
        this.invoiceRemitToAddressLine2 = invoiceRemitToAddressLine2;
        this.invoiceRemitToAddressLine3 = invoiceRemitToAddressLine3;
        this.invoiceRemitToAddressCityName = invoiceRemitToAddressCityName;
        this.invoiceRemitToAddressStateCode = invoiceRemitToAddressStateCode;
        this.invoiceRemitToAddressPostalCode = invoiceRemitToAddressPostalCode;
        this.invoiceRemitToAddressCountryCode = invoiceRemitToAddressCountryCode;
        this.invoiceRemitToAddressCountryName = invoiceRemitToAddressCountryName;

        this.invoiceCustomerNumber = invoiceCustomerNumber;
        this.invoicePurchaseOrderNumber = invoicePurchaseOrderNumber;
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
        this.purchaseOrderDeliveryCampusCode = purchaseOrderDeliveryCampusCode;

        this.invoiceItemSubTotalCurrencyCode = invoiceItemSubTotalCurrencyCode;
        this.invoiceItemSpecialHandlingCurrencyCode = invoiceItemSpecialHandlingCurrencyCode;
        this.invoiceItemSpecialHandlingDescription = invoiceItemSpecialHandlingDescription;
        this.invoiceItemShippingCurrencyCode = invoiceItemShippingCurrencyCode;
        this.invoiceItemShippingDescription = invoiceItemShippingDescription;
        this.invoiceItemTaxCurrencyCode = invoiceItemTaxCurrencyCode;
        this.invoiceItemTaxDescription = invoiceItemTaxDescription;
        this.invoiceItemGrossCurrencyCode = invoiceItemGrossCurrencyCode;
        this.invoiceItemDiscountCurrencyCode = invoiceItemDiscountCurrencyCode;
        this.invoiceItemNetCurrencyCode = invoiceItemNetCurrencyCode;

        this.invoiceItemSubTotalAmount = invoiceItemSubTotalAmount;
        this.invoiceItemSpecialHandlingAmount = invoiceItemSpecialHandlingAmount;
        this.invoiceItemShippingAmount = invoiceItemShippingAmount;
        this.invoiceItemTaxAmount = invoiceItemTaxAmount;
        this.invoiceItemGrossAmount = invoiceItemGrossAmount;
        this.invoiceItemDiscountAmount = invoiceItemDiscountAmount;
        this.invoiceItemNetAmount = invoiceItemNetAmount;
        
        this.invoiceRejectItemFixtures = invoiceRejectItemFixtures;
        this.invoiceRejectReasonFixtures = invoiceRejectReasonFixtures;
    }

    public ElectronicInvoiceRejectDocument createElectronicInvoiceRejectDocument(ElectronicInvoiceLoadSummary eils) {
        ElectronicInvoiceRejectDocument doc = null;
        try {
            doc = (ElectronicInvoiceRejectDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), ElectronicInvoiceRejectDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        
        doc.setInvoiceProcessTimestamp(invoiceProcessTimestamp);
        doc.setInvoiceFileHeaderTypeIndicator(invoiceFileHeaderTypeIndicator);
        doc.setInvoiceFileInformationOnlyIndicator(invoiceFileInformationOnlyIndicator);
        doc.setInvoiceFileTaxInLineIndicator(invoiceFileTaxInLineIndicator);
        doc.setInvoiceFileSpecialHandlingInLineIndicator(invoiceFileSpecialHandlingInLineIndicator);
        doc.setInvoiceFileShippingInLineIndicator(invoiceFileShippingInLineIndicator);
        doc.setInvoiceFileDiscountInLineIndicator(invoiceFileDiscountInLineIndicator);
        
        doc.setInvoiceFileName(invoiceFileName);
        doc.setInvoiceFileNumber(invoiceFileNumber);
        doc.setVendorDunsNumber(vendorDunsNumber);
        doc.setVendorHeaderGeneratedIdentifier(vendorHeaderGeneratedIdentifier);
        doc.setVendorDetailAssignedIdentifier(vendorDetailAssignedIdentifier);
        doc.setInvoiceFileDate(invoiceFileDate);
        doc.setInvoiceFilePurposeIdentifier(invoiceFilePurposeIdentifier);
        doc.setInvoiceFileOperationIdentifier(invoiceFileOperationIdentifier);
        doc.setInvoiceFileDeploymentModeValue(invoiceFileDeploymentModeValue);
        doc.setInvoiceOrderReferenceOrderIdentifier(invoiceOrderReferenceOrderIdentifier);
        doc.setInvoiceOrderReferenceDocumentReferencePayloadIdentifier(invoiceOrderReferenceDocumentReferencePayloadIdentifier);
        doc.setInvoiceOrderReferenceDocumentReferenceText(invoiceOrderReferenceDocumentReferenceText);
        doc.setInvoiceOrderMasterAgreementReferenceIdentifier(invoiceOrderMasterAgreementReferenceIdentifier);
        doc.setInvoiceOrderMasterAgreementReferenceDate(invoiceOrderMasterAgreementReferenceDate);
        doc.setInvoiceOrderMasterAgreementInformationIdentifier(invoiceOrderMasterAgreementInformationIdentifier);
        doc.setInvoiceOrderMasterAgreementInformationDate(invoiceOrderMasterAgreementInformationDate);
        doc.setInvoiceOrderPurchaseOrderIdentifier(invoiceOrderPurchaseOrderIdentifier);
        doc.setInvoiceOrderPurchaseOrderDate(invoiceOrderPurchaseOrderDate);
        doc.setInvoiceOrderSupplierOrderInformationIdentifier(invoiceOrderSupplierOrderInformationIdentifier);
        doc.setInvoiceShipDate(invoiceShipDate);
        doc.setInvoiceShipToAddressName(invoiceShipToAddressName);
        doc.setInvoiceShipToAddressType(invoiceShipToAddressType);
        doc.setInvoiceShipToAddressLine1(invoiceShipToAddressLine1);
        doc.setInvoiceShipToAddressLine2(invoiceShipToAddressLine2);
        doc.setInvoiceShipToAddressLine2(invoiceShipToAddressLine3);
        doc.setInvoiceShipToAddressCityName(invoiceShipToAddressCityName);
        doc.setInvoiceShipToAddressStateCode(invoiceShipToAddressStateCode);
        doc.setInvoiceShipToAddressPostalCode(invoiceShipToAddressPostalCode);
        doc.setInvoiceShipToAddressCountryCode(invoiceShipToAddressCountryCode);
        doc.setInvoiceShipToAddressCountryName(invoiceShipToAddressCountryName);
        doc.setInvoiceBillToAddressName(invoiceBillToAddressName);
        doc.setInvoiceBillToAddressType(invoiceBillToAddressType);
        doc.setInvoiceBillToAddressLine1(invoiceBillToAddressLine1);
        doc.setInvoiceBillToAddressLine2(invoiceBillToAddressLine2);
        doc.setInvoiceBillToAddressLine2(invoiceBillToAddressLine3);
        doc.setInvoiceBillToAddressCityName(invoiceBillToAddressCityName);
        doc.setInvoiceBillToAddressStateCode(invoiceBillToAddressStateCode);
        doc.setInvoiceBillToAddressPostalCode(invoiceBillToAddressPostalCode);
        doc.setInvoiceBillToAddressCountryCode(invoiceBillToAddressCountryCode);
        doc.setInvoiceBillToAddressCountryName(invoiceBillToAddressCountryName);
        doc.setInvoiceRemitToAddressName(invoiceRemitToAddressName);
        doc.setInvoiceRemitToAddressType(invoiceRemitToAddressType);
        doc.setInvoiceRemitToAddressLine1(invoiceRemitToAddressLine1);
        doc.setInvoiceRemitToAddressLine2(invoiceRemitToAddressLine2);
        doc.setInvoiceRemitToAddressLine2(invoiceRemitToAddressLine3);
        doc.setInvoiceRemitToAddressCityName(invoiceRemitToAddressCityName);
        doc.setInvoiceRemitToAddressStateCode(invoiceRemitToAddressStateCode);
        doc.setInvoiceRemitToAddressPostalCode(invoiceRemitToAddressPostalCode);
        doc.setInvoiceRemitToAddressCountryCode(invoiceRemitToAddressCountryCode);
        doc.setInvoiceRemitToAddressCountryName(invoiceRemitToAddressCountryName);
        
        doc.setInvoiceCustomerNumber(invoiceCustomerNumber);
        doc.setInvoicePurchaseOrderNumber(invoicePurchaseOrderNumber);
        doc.setPurchaseOrderIdentifier(purchaseOrderIdentifier);
        doc.setPurchaseOrderDeliveryCampusCode(purchaseOrderDeliveryCampusCode);
        
        doc.setInvoiceItemSubTotalCurrencyCode(invoiceItemSubTotalCurrencyCode);
        doc.setInvoiceItemSpecialHandlingCurrencyCode(invoiceItemSpecialHandlingCurrencyCode);
        doc.setInvoiceItemSpecialHandlingDescription(invoiceItemSpecialHandlingDescription);
        doc.setInvoiceItemShippingCurrencyCode(invoiceItemShippingCurrencyCode);
        doc.setInvoiceItemShippingDescription(invoiceItemShippingDescription);
        doc.setInvoiceItemTaxCurrencyCode(invoiceItemTaxCurrencyCode);
        doc.setInvoiceItemTaxDescription(invoiceItemTaxDescription);
        doc.setInvoiceItemGrossCurrencyCode(invoiceItemGrossCurrencyCode);
        doc.setInvoiceItemDiscountCurrencyCode(invoiceItemDiscountCurrencyCode);
        doc.setInvoiceItemNetCurrencyCode(invoiceItemNetCurrencyCode);

        doc.setInvoiceItemSubTotalAmount(invoiceItemSubTotalAmount);
        doc.setInvoiceItemSpecialHandlingAmount(invoiceItemSpecialHandlingAmount);
        doc.setInvoiceItemShippingAmount(invoiceItemShippingAmount);
        doc.setInvoiceItemTaxAmount(invoiceItemTaxAmount);
        doc.setInvoiceItemGrossAmount(invoiceItemGrossAmount);
        doc.setInvoiceItemDiscountAmount(invoiceItemDiscountAmount);
        doc.setInvoiceItemNetAmount(invoiceItemNetAmount);

        doc.setInvoiceLoadSummary(eils);
        for (ElectronicInvoiceRejectItemFixture invoiceRejectItemFixture : invoiceRejectItemFixtures) {
            invoiceRejectItemFixture.addTo(doc);
        }
        for (ElectronicInvoiceRejectReasonFixture invoiceRejectReasonFixture : invoiceRejectReasonFixtures) {
            invoiceRejectReasonFixture.addTo(doc);
        }
        
        return doc;
    }

}
