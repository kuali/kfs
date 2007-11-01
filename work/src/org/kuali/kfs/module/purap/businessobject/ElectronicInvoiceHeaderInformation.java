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

package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Electronic Invoice Header Information Business Object.
 */
public class ElectronicInvoiceHeaderInformation extends PersistableBusinessObjectBase {

    private Integer invoiceHeaderInformationIdentifier;
    private Long accountsPayableElectronicInvoiceLoadSummaryIdentifier;
    private Date invoiceProcessDate;
    private String invoiceFileName;
    private String vendorDunsNumber;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String invoiceFileDate;
    private String invoiceFileNumber;
    private String invoiceFilePurposeIdentifier;
    private String invoiceFileOperationIdentifier;
    private String invoiceFileDeploymentModeValue;
    private boolean invoiceFileHeaderTypeIndicator;
    private boolean invoiceFileInformationOnlyIndicator;
    private boolean invoiceFileTaxInLineIndicator;
    private boolean invoiceFileSpecialHandlingInLineIndicator;
    private boolean invoiceFileShippingInLineIndicator;
    private boolean invoiceFileDiscountInLineIndicator;
    private String invoiceOrderReferenceOrderIdentifier;
    private String invoiceOrderReferenceDocumentReferencePayloadIdentifier;
    private String invoiceOrderReferenceDocumentReferenceText;
    private String invoiceOrderMasterAgreementReferenceIdentifier;
    private String invoiceOrderMasterAgreementReferenceDate;
    private String invoiceOrderMasterAgreementInformationIdentifier;
    private String invoiceOrderMasterAgreementInformationDate;
    private String invoiceOrderPurchaseOrderIdentifier;
    private String invoiceOrderPurchaseOrderDate;
    private String invoiceOrderSupplierOrderInformationIdentifier;
    private Integer epicPurchaseOrderIdentifier;
    private BigDecimal invoiceItemSubTotalAmount;
    private String invoiceItemSubTotalCurrencyCode;
    private BigDecimal invoiceItemSpecialHandlingAmount;
    private String invoiceItemSpecialHandlingCurrencyCode;
    private BigDecimal invoiceItemShippingAmount;
    private String invoiceItemShippingCurrencyCode;
    private String invoiceItemShippingDescription;
    private BigDecimal invoiceItemTaxAmount;
    private String invoiceItemTaxCurrencyCode;
    private String invoiceItemTaxDescription;
    private BigDecimal invoiceItemGrossAmount;
    private String invoiceItemGrossCurrencyCode;
    private BigDecimal invoiceItemDiscountAmount;
    private String invoiceItemDiscountCurrencyCode;
    private BigDecimal invoiceItemNetAmount;
    private String invoiceItemNetCurrencyCode;
    private Date invoiceRejectExtractDate;
    private String epicPurchaseOrderDeliveryCampusCode;
    private Date invoiceShipDate;
    private String invoiceAddressName;
    private String invoiceShipToLine1Address;
    private String invoiceShipToLine2Address;
    private String invoiceShipToLine3Address;
    private String invoiceCustomerNumber;
    private String invoiceShipToStateCode;
    private String invoiceShipToCountryCode;
    private String invoiceShipToCityName;
    private String invoiceShipToPostalCode;
    private String invoicePurchaseOrderNumber;

    private ElectronicInvoiceRejectReason invoiceHeaderInformation;
    private ElectronicInvoiceLoadSummary accountsPayableElectronicInvoiceLoadSummary;
    private Campus epicPurchaseOrderDeliveryCampus;

    /**
     * Default constructor.
     */
    public ElectronicInvoiceHeaderInformation() {

    }

    public ElectronicInvoiceLoadSummary getAccountsPayableElectronicInvoiceLoadSummary() {
        return accountsPayableElectronicInvoiceLoadSummary;
    }

    /**
     * @deprecated
     */
    public void setAccountsPayableElectronicInvoiceLoadSummary(ElectronicInvoiceLoadSummary accountsPayableElectronicInvoiceLoadSummary) {
        this.accountsPayableElectronicInvoiceLoadSummary = accountsPayableElectronicInvoiceLoadSummary;
    }

    public Long getAccountsPayableElectronicInvoiceLoadSummaryIdentifier() {
        return accountsPayableElectronicInvoiceLoadSummaryIdentifier;
    }

    public void setAccountsPayableElectronicInvoiceLoadSummaryIdentifier(Long accountsPayableElectronicInvoiceLoadSummaryIdentifier) {
        this.accountsPayableElectronicInvoiceLoadSummaryIdentifier = accountsPayableElectronicInvoiceLoadSummaryIdentifier;
    }

    public Campus getEpicPurchaseOrderDeliveryCampus() {
        return epicPurchaseOrderDeliveryCampus;
    }

    /**
     * @deprecated
     */
    public void setEpicPurchaseOrderDeliveryCampus(Campus epicPurchaseOrderDeliveryCampus) {
        this.epicPurchaseOrderDeliveryCampus = epicPurchaseOrderDeliveryCampus;
    }

    public String getEpicPurchaseOrderDeliveryCampusCode() {
        return epicPurchaseOrderDeliveryCampusCode;
    }

    public void setEpicPurchaseOrderDeliveryCampusCode(String epicPurchaseOrderDeliveryCampusCode) {
        this.epicPurchaseOrderDeliveryCampusCode = epicPurchaseOrderDeliveryCampusCode;
    }

    public Integer getEpicPurchaseOrderIdentifier() {
        return epicPurchaseOrderIdentifier;
    }

    public void setEpicPurchaseOrderIdentifier(Integer epicPurchaseOrderIdentifier) {
        this.epicPurchaseOrderIdentifier = epicPurchaseOrderIdentifier;
    }

    public String getInvoiceAddressName() {
        return invoiceAddressName;
    }

    public void setInvoiceAddressName(String invoiceAddressName) {
        this.invoiceAddressName = invoiceAddressName;
    }

    public String getInvoiceCustomerNumber() {
        return invoiceCustomerNumber;
    }

    public void setInvoiceCustomerNumber(String invoiceCustomerNumber) {
        this.invoiceCustomerNumber = invoiceCustomerNumber;
    }

    public String getInvoiceFileDate() {
        return invoiceFileDate;
    }

    public void setInvoiceFileDate(String invoiceFileDate) {
        this.invoiceFileDate = invoiceFileDate;
    }

    public String getInvoiceFileDeploymentModeValue() {
        return invoiceFileDeploymentModeValue;
    }

    public void setInvoiceFileDeploymentModeValue(String invoiceFileDeploymentModeValue) {
        this.invoiceFileDeploymentModeValue = invoiceFileDeploymentModeValue;
    }

    public boolean isInvoiceFileDiscountInLineIndicator() {
        return invoiceFileDiscountInLineIndicator;
    }

    public void setInvoiceFileDiscountInLineIndicator(boolean invoiceFileDiscountInLineIndicator) {
        this.invoiceFileDiscountInLineIndicator = invoiceFileDiscountInLineIndicator;
    }

    public boolean isInvoiceFileHeaderTypeIndicator() {
        return invoiceFileHeaderTypeIndicator;
    }

    public void setInvoiceFileHeaderTypeIndicator(boolean invoiceFileHeaderTypeIndicator) {
        this.invoiceFileHeaderTypeIndicator = invoiceFileHeaderTypeIndicator;
    }

    public boolean isInvoiceFileInformationOnlyIndicator() {
        return invoiceFileInformationOnlyIndicator;
    }

    public void setInvoiceFileInformationOnlyIndicator(boolean invoiceFileInformationOnlyIndicator) {
        this.invoiceFileInformationOnlyIndicator = invoiceFileInformationOnlyIndicator;
    }

    public String getInvoiceFileName() {
        return invoiceFileName;
    }

    public void setInvoiceFileName(String invoiceFileName) {
        this.invoiceFileName = invoiceFileName;
    }

    public String getInvoiceFileNumber() {
        return invoiceFileNumber;
    }

    public void setInvoiceFileNumber(String invoiceFileNumber) {
        this.invoiceFileNumber = invoiceFileNumber;
    }

    public String getInvoiceFileOperationIdentifier() {
        return invoiceFileOperationIdentifier;
    }

    public void setInvoiceFileOperationIdentifier(String invoiceFileOperationIdentifier) {
        this.invoiceFileOperationIdentifier = invoiceFileOperationIdentifier;
    }

    public String getInvoiceFilePurposeIdentifier() {
        return invoiceFilePurposeIdentifier;
    }

    public void setInvoiceFilePurposeIdentifier(String invoiceFilePurposeIdentifier) {
        this.invoiceFilePurposeIdentifier = invoiceFilePurposeIdentifier;
    }

    public boolean isInvoiceFileShippingInLineIndicator() {
        return invoiceFileShippingInLineIndicator;
    }

    public void setInvoiceFileShippingInLineIndicator(boolean invoiceFileShippingInLineIndicator) {
        this.invoiceFileShippingInLineIndicator = invoiceFileShippingInLineIndicator;
    }

    public boolean isInvoiceFileSpecialHandlingInLineIndicator() {
        return invoiceFileSpecialHandlingInLineIndicator;
    }

    public void setInvoiceFileSpecialHandlingInLineIndicator(boolean invoiceFileSpecialHandlingInLineIndicator) {
        this.invoiceFileSpecialHandlingInLineIndicator = invoiceFileSpecialHandlingInLineIndicator;
    }

    public boolean isInvoiceFileTaxInLineIndicator() {
        return invoiceFileTaxInLineIndicator;
    }

    public void setInvoiceFileTaxInLineIndicator(boolean invoiceFileTaxInLineIndicator) {
        this.invoiceFileTaxInLineIndicator = invoiceFileTaxInLineIndicator;
    }

    public ElectronicInvoiceRejectReason getInvoiceHeaderInformation() {
        return invoiceHeaderInformation;
    }

    /**
     * @deprecated
     */
    public void setInvoiceHeaderInformation(ElectronicInvoiceRejectReason invoiceHeaderInformation) {
        this.invoiceHeaderInformation = invoiceHeaderInformation;
    }

    public Integer getInvoiceHeaderInformationIdentifier() {
        return invoiceHeaderInformationIdentifier;
    }

    public void setInvoiceHeaderInformationIdentifier(Integer invoiceHeaderInformationIdentifier) {
        this.invoiceHeaderInformationIdentifier = invoiceHeaderInformationIdentifier;
    }

    public BigDecimal getInvoiceItemDiscountAmount() {
        return invoiceItemDiscountAmount;
    }

    public void setInvoiceItemDiscountAmount(BigDecimal invoiceItemDiscountAmount) {
        this.invoiceItemDiscountAmount = invoiceItemDiscountAmount;
    }

    public String getInvoiceItemDiscountCurrencyCode() {
        return invoiceItemDiscountCurrencyCode;
    }

    public void setInvoiceItemDiscountCurrencyCode(String invoiceItemDiscountCurrencyCode) {
        this.invoiceItemDiscountCurrencyCode = invoiceItemDiscountCurrencyCode;
    }

    public BigDecimal getInvoiceItemGrossAmount() {
        return invoiceItemGrossAmount;
    }

    public void setInvoiceItemGrossAmount(BigDecimal invoiceItemGrossAmount) {
        this.invoiceItemGrossAmount = invoiceItemGrossAmount;
    }

    public String getInvoiceItemGrossCurrencyCode() {
        return invoiceItemGrossCurrencyCode;
    }

    public void setInvoiceItemGrossCurrencyCode(String invoiceItemGrossCurrencyCode) {
        this.invoiceItemGrossCurrencyCode = invoiceItemGrossCurrencyCode;
    }

    public BigDecimal getInvoiceItemNetAmount() {
        return invoiceItemNetAmount;
    }

    public void setInvoiceItemNetAmount(BigDecimal invoiceItemNetAmount) {
        this.invoiceItemNetAmount = invoiceItemNetAmount;
    }

    public String getInvoiceItemNetCurrencyCode() {
        return invoiceItemNetCurrencyCode;
    }

    public void setInvoiceItemNetCurrencyCode(String invoiceItemNetCurrencyCode) {
        this.invoiceItemNetCurrencyCode = invoiceItemNetCurrencyCode;
    }

    public BigDecimal getInvoiceItemShippingAmount() {
        return invoiceItemShippingAmount;
    }

    public void setInvoiceItemShippingAmount(BigDecimal invoiceItemShippingAmount) {
        this.invoiceItemShippingAmount = invoiceItemShippingAmount;
    }

    public String getInvoiceItemShippingCurrencyCode() {
        return invoiceItemShippingCurrencyCode;
    }

    public void setInvoiceItemShippingCurrencyCode(String invoiceItemShippingCurrencyCode) {
        this.invoiceItemShippingCurrencyCode = invoiceItemShippingCurrencyCode;
    }

    public String getInvoiceItemShippingDescription() {
        return invoiceItemShippingDescription;
    }

    public void setInvoiceItemShippingDescription(String invoiceItemShippingDescription) {
        this.invoiceItemShippingDescription = invoiceItemShippingDescription;
    }

    public BigDecimal getInvoiceItemSpecialHandlingAmount() {
        return invoiceItemSpecialHandlingAmount;
    }

    public void setInvoiceItemSpecialHandlingAmount(BigDecimal invoiceItemSpecialHandlingAmount) {
        this.invoiceItemSpecialHandlingAmount = invoiceItemSpecialHandlingAmount;
    }

    public String getInvoiceItemSpecialHandlingCurrencyCode() {
        return invoiceItemSpecialHandlingCurrencyCode;
    }

    public void setInvoiceItemSpecialHandlingCurrencyCode(String invoiceItemSpecialHandlingCurrencyCode) {
        this.invoiceItemSpecialHandlingCurrencyCode = invoiceItemSpecialHandlingCurrencyCode;
    }

    public BigDecimal getInvoiceItemSubTotalAmount() {
        return invoiceItemSubTotalAmount;
    }

    public void setInvoiceItemSubTotalAmount(BigDecimal invoiceItemSubTotalAmount) {
        this.invoiceItemSubTotalAmount = invoiceItemSubTotalAmount;
    }

    public String getInvoiceItemSubTotalCurrencyCode() {
        return invoiceItemSubTotalCurrencyCode;
    }

    public void setInvoiceItemSubTotalCurrencyCode(String invoiceItemSubTotalCurrencyCode) {
        this.invoiceItemSubTotalCurrencyCode = invoiceItemSubTotalCurrencyCode;
    }

    public BigDecimal getInvoiceItemTaxAmount() {
        return invoiceItemTaxAmount;
    }

    public void setInvoiceItemTaxAmount(BigDecimal invoiceItemTaxAmount) {
        this.invoiceItemTaxAmount = invoiceItemTaxAmount;
    }

    public String getInvoiceItemTaxCurrencyCode() {
        return invoiceItemTaxCurrencyCode;
    }

    public void setInvoiceItemTaxCurrencyCode(String invoiceItemTaxCurrencyCode) {
        this.invoiceItemTaxCurrencyCode = invoiceItemTaxCurrencyCode;
    }

    public String getInvoiceItemTaxDescription() {
        return invoiceItemTaxDescription;
    }

    public void setInvoiceItemTaxDescription(String invoiceItemTaxDescription) {
        this.invoiceItemTaxDescription = invoiceItemTaxDescription;
    }

    public String getInvoiceOrderMasterAgreementInformationDate() {
        return invoiceOrderMasterAgreementInformationDate;
    }

    public void setInvoiceOrderMasterAgreementInformationDate(String invoiceOrderMasterAgreementInformationDate) {
        this.invoiceOrderMasterAgreementInformationDate = invoiceOrderMasterAgreementInformationDate;
    }

    public String getInvoiceOrderMasterAgreementInformationIdentifier() {
        return invoiceOrderMasterAgreementInformationIdentifier;
    }

    public void setInvoiceOrderMasterAgreementInformationIdentifier(String invoiceOrderMasterAgreementInformationIdentifier) {
        this.invoiceOrderMasterAgreementInformationIdentifier = invoiceOrderMasterAgreementInformationIdentifier;
    }

    public String getInvoiceOrderMasterAgreementReferenceDate() {
        return invoiceOrderMasterAgreementReferenceDate;
    }

    public void setInvoiceOrderMasterAgreementReferenceDate(String invoiceOrderMasterAgreementReferenceDate) {
        this.invoiceOrderMasterAgreementReferenceDate = invoiceOrderMasterAgreementReferenceDate;
    }

    public String getInvoiceOrderMasterAgreementReferenceIdentifier() {
        return invoiceOrderMasterAgreementReferenceIdentifier;
    }

    public void setInvoiceOrderMasterAgreementReferenceIdentifier(String invoiceOrderMasterAgreementReferenceIdentifier) {
        this.invoiceOrderMasterAgreementReferenceIdentifier = invoiceOrderMasterAgreementReferenceIdentifier;
    }

    public String getInvoiceOrderPurchaseOrderDate() {
        return invoiceOrderPurchaseOrderDate;
    }

    public void setInvoiceOrderPurchaseOrderDate(String invoiceOrderPurchaseOrderDate) {
        this.invoiceOrderPurchaseOrderDate = invoiceOrderPurchaseOrderDate;
    }

    public String getInvoiceOrderPurchaseOrderIdentifier() {
        return invoiceOrderPurchaseOrderIdentifier;
    }

    public void setInvoiceOrderPurchaseOrderIdentifier(String invoiceOrderPurchaseOrderIdentifier) {
        this.invoiceOrderPurchaseOrderIdentifier = invoiceOrderPurchaseOrderIdentifier;
    }

    public String getInvoiceOrderReferenceDocumentReferencePayloadIdentifier() {
        return invoiceOrderReferenceDocumentReferencePayloadIdentifier;
    }

    public void setInvoiceOrderReferenceDocumentReferencePayloadIdentifier(String invoiceOrderReferenceDocumentReferencePayloadIdentifier) {
        this.invoiceOrderReferenceDocumentReferencePayloadIdentifier = invoiceOrderReferenceDocumentReferencePayloadIdentifier;
    }

    public String getInvoiceOrderReferenceDocumentReferenceText() {
        return invoiceOrderReferenceDocumentReferenceText;
    }

    public void setInvoiceOrderReferenceDocumentReferenceText(String invoiceOrderReferenceDocumentReferenceText) {
        this.invoiceOrderReferenceDocumentReferenceText = invoiceOrderReferenceDocumentReferenceText;
    }

    public String getInvoiceOrderReferenceOrderIdentifier() {
        return invoiceOrderReferenceOrderIdentifier;
    }

    public void setInvoiceOrderReferenceOrderIdentifier(String invoiceOrderReferenceOrderIdentifier) {
        this.invoiceOrderReferenceOrderIdentifier = invoiceOrderReferenceOrderIdentifier;
    }

    public String getInvoiceOrderSupplierOrderInformationIdentifier() {
        return invoiceOrderSupplierOrderInformationIdentifier;
    }

    public void setInvoiceOrderSupplierOrderInformationIdentifier(String invoiceOrderSupplierOrderInformationIdentifier) {
        this.invoiceOrderSupplierOrderInformationIdentifier = invoiceOrderSupplierOrderInformationIdentifier;
    }

    public Date getInvoiceProcessDate() {
        return invoiceProcessDate;
    }

    public void setInvoiceProcessDate(Date invoiceProcessDate) {
        this.invoiceProcessDate = invoiceProcessDate;
    }

    public String getInvoicePurchaseOrderNumber() {
        return invoicePurchaseOrderNumber;
    }

    public void setInvoicePurchaseOrderNumber(String invoicePurchaseOrderNumber) {
        this.invoicePurchaseOrderNumber = invoicePurchaseOrderNumber;
    }

    public Date getInvoiceRejectExtractDate() {
        return invoiceRejectExtractDate;
    }

    public void setInvoiceRejectExtractDate(Date invoiceRejectExtractDate) {
        this.invoiceRejectExtractDate = invoiceRejectExtractDate;
    }

    public Date getInvoiceShipDate() {
        return invoiceShipDate;
    }

    public void setInvoiceShipDate(Date invoiceShipDate) {
        this.invoiceShipDate = invoiceShipDate;
    }

    public String getInvoiceShipToCityName() {
        return invoiceShipToCityName;
    }

    public void setInvoiceShipToCityName(String invoiceShipToCityName) {
        this.invoiceShipToCityName = invoiceShipToCityName;
    }

    public String getInvoiceShipToCountryCode() {
        return invoiceShipToCountryCode;
    }

    public void setInvoiceShipToCountryCode(String invoiceShipToCountryCode) {
        this.invoiceShipToCountryCode = invoiceShipToCountryCode;
    }

    public String getInvoiceShipToLine1Address() {
        return invoiceShipToLine1Address;
    }

    public void setInvoiceShipToLine1Address(String invoiceShipToLine1Address) {
        this.invoiceShipToLine1Address = invoiceShipToLine1Address;
    }

    public String getInvoiceShipToLine2Address() {
        return invoiceShipToLine2Address;
    }

    public void setInvoiceShipToLine2Address(String invoiceShipToLine2Address) {
        this.invoiceShipToLine2Address = invoiceShipToLine2Address;
    }

    public String getInvoiceShipToLine3Address() {
        return invoiceShipToLine3Address;
    }

    public void setInvoiceShipToLine3Address(String invoiceShipToLine3Address) {
        this.invoiceShipToLine3Address = invoiceShipToLine3Address;
    }

    public String getInvoiceShipToPostalCode() {
        return invoiceShipToPostalCode;
    }

    public void setInvoiceShipToPostalCode(String invoiceShipToPostalCode) {
        this.invoiceShipToPostalCode = invoiceShipToPostalCode;
    }

    public String getInvoiceShipToStateCode() {
        return invoiceShipToStateCode;
    }

    public void setInvoiceShipToStateCode(String invoiceShipToStateCode) {
        this.invoiceShipToStateCode = invoiceShipToStateCode;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorDunsNumber() {
        return vendorDunsNumber;
    }

    public void setVendorDunsNumber(String vendorDunsNumber) {
        this.vendorDunsNumber = vendorDunsNumber;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.invoiceHeaderInformationIdentifier != null) {
            m.put("invoiceHeaderInformationIdentifier", this.invoiceHeaderInformationIdentifier.toString());
        }
        return m;
    }
}
