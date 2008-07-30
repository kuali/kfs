/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.bo.Campus;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceContact;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoicePostalAddress;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

public class ElectronicInvoiceRejectDocument extends FinancialSystemTransactionalDocumentBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceRejectDocument.class);
    private static BigDecimal zero = new BigDecimal(0);

    // NOT NULL FIELDS
    private Integer purapDocumentIdentifier;
    private Integer invoiceLoadSummaryIdentifier;
    private Timestamp invoiceProcessTimestamp;
    private Boolean fileHeaderTypeIndicator = Boolean.FALSE;
    private Boolean fileInformationOnlyIndicator = Boolean.FALSE;
    private Boolean fileTaxInLineIndicator = Boolean.FALSE;
    private Boolean fileSpecHandlingInLineIndicator = Boolean.FALSE;
    private Boolean fileShippingInLineIndicator = Boolean.FALSE;
    private Boolean fileDiscountInLineIndicator = Boolean.FALSE;

    private String invoiceFileName;
    private String vendorDunsNumber;
    private Integer vendorHeaderID;
    private Integer vendorDetailID;
    private java.sql.Date invoiceDate;
    private String invoiceNumber;
    private String filePurposeId;
    private String fileOperationId;
    private String deploymentMode;
    private String referenceOrderId;
    private String referenceDocumentRefPayloadId;
    private String referenceDocumentRefText;
    private String masterAgreementReferenceId;
    private String masterAgreementReferenceDate;
    private String masterAgreementInfoId;
    private String masterAgreementInfoDate;
    private String invoiceOrderId;
    private String invoiceOrderDate;
    private String supplierOrderInfoId;
    private String invoiceShipDate;
    private String shipToAddressName;
    private String shipToAddressType;
    private String shipToAddressLine1;
    private String shipToAddressLine2;
    private String shipToAddressLine3;
    private String shipToAddressCityName;
    private String shipToAddressStateCode;
    private String shipToAddressPostalCode;
    private String shipToAddressCountryCode;
    private String shipToAddressCountryName;
    private String billToAddressName;
    private String billToAddressType;
    private String billToAddressLine1;
    private String billToAddressLine2;
    private String billToAddressLine3;
    private String billToAddressCityName;
    private String billToAddressStateCode;
    private String billToAddressPostalCode;
    private String billToAddressCountryCode;
    private String billToAddressCountryName;
    private String remitToAddressName;
    private String remitToAddressType;
    private String remitToAddressLine1;
    private String remitToAddressLine2;
    private String remitToAddressLine3;
    private String remitToAddressCityName;
    private String remitToAddressStateCode;
    private String remitToAddressPostalCode;
    private String remitToAddressCountryCode;
    private String remitToAddressCountryName;

    private String invoiceCustomerNumber;
    private String invoiceOrderPurchaseOrderId;
    private Integer purchaseOrderId;
    private String purchaseOrderDeliveryCampusCode;

    private String invoiceSubtotalAmountCurrency;
    private String invoiceSpecialHandlingAmountCurrency;
    private String invoiceSpecialHandlingDescription;
    private String invoiceShippingAmountCurrency;
    private String invoiceShippingDescription;
    private String invoiceTaxAmountCurrency;
    private String invoiceTaxDescription;
    private String invoiceGrossAmountCurrency;
    private String invoiceDiscountAmountCurrency;
    private String invoiceNetAmountCurrency;

    private BigDecimal invoiceSubtotalAmount;
    private BigDecimal invoiceSpecialHandlingAmount;
    private BigDecimal invoiceShippingAmount;
    private BigDecimal invoiceTaxAmount;
    private BigDecimal invoiceGrossAmount;
    private BigDecimal invoiceDiscountAmount;
    private BigDecimal invoiceNetAmount;

    private Timestamp lastUpdateTimestamp; // lst_updt_ts
    private Integer version; // ver_nbr

    private ElectronicInvoiceLoadSummary invoiceLoadSummary;
    private Campus purchaseOrderDeliveryCampus;
    private List<ElectronicInvoiceRejectItem> electronicInvoiceRejectItems = new ArrayList<ElectronicInvoiceRejectItem>();
    private List<ElectronicInvoiceRejectReason> electronicInvoiceRejectReasons = new ArrayList<ElectronicInvoiceRejectReason>();

    private VendorDetail vendorDetail;
    private PurchaseOrderDocument purchaseOrderDocument;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;


    public void setFileLevelData(ElectronicInvoice ei) {
        this.invoiceProcessTimestamp = new Timestamp((new Date()).getTime());
        this.fileHeaderTypeIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isHeaderInvoiceIndicator());
        this.fileInformationOnlyIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isInformationOnly());
        this.fileTaxInLineIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isTaxInLine());
        this.fileSpecHandlingInLineIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isSpecialHandlingInLine());
        this.fileShippingInLineIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isShippingInLine());
        this.fileDiscountInLineIndicator = new Boolean(ei.getInvoiceDetailRequestHeader().isDiscountInLine());

        this.invoiceFileName = ei.getFileName();
        this.vendorDunsNumber = ei.getDunsNumber();
        this.vendorHeaderID = ei.getVendorHeaderID();
        this.vendorDetailID = ei.getVendorDetailID();
        // TODO this.invoiceDate = ei.getInvoiceDateDisplayText();
        this.invoiceNumber = ei.getInvoiceDetailRequestHeader().getInvoiceId();
        this.filePurposeId = ei.getInvoiceDetailRequestHeader().getPurpose();
        this.fileOperationId = ei.getInvoiceDetailRequestHeader().getOperation();
        this.deploymentMode = ei.getInvoiceDetailRequestHeader().getDeploymentMode();
        this.invoiceCustomerNumber = ei.getCustomerNumber();

        for (Iterator<ElectronicInvoiceRejectReason> fileReasonIter = ei.getFileRejectReasons().iterator(); fileReasonIter.hasNext();) {
            ElectronicInvoiceRejectReason eirr = fileReasonIter.next();
            //FIXME fix this as part of fixing the reject document
//            eirr.setElectronicInvoiceReject(this);
            this.electronicInvoiceRejectReasons.add(eirr);
        }
    }

    public void setInvoiceOrderLevelData(ElectronicInvoice ei, ElectronicInvoiceOrder eio) {
        this.referenceOrderId = eio.getOrderReferenceOrderID();
        this.referenceDocumentRefPayloadId = eio.getOrderReferenceDocumentRefPayloadID();
        this.referenceDocumentRefText = eio.getOrderReferenceDocumentRef();
        this.masterAgreementReferenceId = eio.getMasterAgreementReferenceID();
        this.masterAgreementReferenceDate = ei.getMasterAgreementReferenceDateDisplayText(eio);
        this.masterAgreementInfoId = eio.getMasterAgreementIDInfoID();
        this.masterAgreementInfoDate = ei.getMasterAgreementIDInfoDateDisplayText(eio);
        this.invoiceOrderId = eio.getOrderIDInfoID();
        this.invoiceOrderDate = ei.getOrderIDInfoDateDisplayText(eio);
        this.supplierOrderInfoId = eio.getSupplierOrderInfoID();
        this.invoiceShipDate = ei.getShippingDateDisplayText(eio);

        ElectronicInvoiceContact shipToContact = ei.getCxmlContact(eio, ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_ROLE_ID);
        ElectronicInvoicePostalAddress shipToAddress = this.getCxmlPostalAddressByAddressName(shipToContact, ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_NAME);
        // ElectronicInvoicePostalAddress shipToAddress =
        // ei.getCxmlPostalAddress(eio,ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_ROLE_ID,
        // ElectronicInvoiceMappingService.CXML_ADDRESS_SHIP_TO_NAME);
        if (shipToAddress != null) {
            this.shipToAddressName = shipToContact.getName();
            this.shipToAddressType = shipToAddress.getType();
            this.shipToAddressLine1 = shipToAddress.getLine1();
            this.shipToAddressLine2 = shipToAddress.getLine2();
            this.shipToAddressLine3 = shipToAddress.getLine3();
            this.shipToAddressCityName = shipToAddress.getCityName();
            this.shipToAddressStateCode = shipToAddress.getStateCode();
            this.shipToAddressPostalCode = shipToAddress.getPostalCode();
            this.shipToAddressCountryCode = shipToAddress.getCountryCode();
            this.shipToAddressCountryName = shipToAddress.getCountryName();
        }

        ElectronicInvoiceContact billToContact = ei.getCxmlContact(eio, ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_ROLE_ID);
        // ElectronicInvoicePostalAddress billToAddress =
        // this.getCxmlPostalAddressByAddressName(billToContact,ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_NAME);
        ElectronicInvoicePostalAddress billToAddress = ei.getCxmlPostalAddress(eio, ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_ROLE_ID, ElectronicInvoiceMappingService.CXML_ADDRESS_BILL_TO_NAME);
        if (billToAddress != null) {
            this.billToAddressName = billToContact.getName();
            this.billToAddressType = billToAddress.getType();
            this.billToAddressLine1 = billToAddress.getLine1();
            this.billToAddressLine2 = billToAddress.getLine2();
            this.billToAddressLine3 = billToAddress.getLine3();
            this.billToAddressCityName = billToAddress.getCityName();
            this.billToAddressStateCode = billToAddress.getStateCode();
            this.billToAddressPostalCode = billToAddress.getPostalCode();
            this.billToAddressCountryCode = billToAddress.getCountryCode();
            this.billToAddressCountryName = billToAddress.getCountryName();
        }

        ElectronicInvoiceContact remitToContact = ei.getCxmlContact(eio, ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_ROLE_ID);
        ElectronicInvoicePostalAddress remitToAddress = this.getCxmlPostalAddressByAddressName(remitToContact, ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_NAME);
        // ElectronicInvoicePostalAddress remitToAddress =
        // ei.getCxmlPostalAddress(eio,ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_ROLE_ID,
        // ElectronicInvoiceMappingService.CXML_ADDRESS_REMIT_TO_NAME);
        if (remitToAddress != null) {
            this.remitToAddressName = remitToContact.getName();
            this.remitToAddressType = remitToAddress.getType();
            this.remitToAddressLine1 = remitToAddress.getLine1();
            this.remitToAddressLine2 = remitToAddress.getLine2();
            this.remitToAddressLine3 = remitToAddress.getLine3();
            this.remitToAddressCityName = remitToAddress.getCityName();
            this.remitToAddressStateCode = remitToAddress.getStateCode();
            this.remitToAddressPostalCode = remitToAddress.getPostalCode();
            this.remitToAddressCountryCode = remitToAddress.getCountryCode();
            this.remitToAddressCountryName = remitToAddress.getCountryName();
        }
        this.invoiceOrderPurchaseOrderId = eio.getInvoicePurchaseOrderID();
        this.purchaseOrderId = eio.getPurchaseOrderID();
        this.purchaseOrderDeliveryCampusCode = eio.getPurchaseOrderCampusCode();

        try {
            this.invoiceSubtotalAmount = ei.getInvoiceSubtotalAmount(eio);
            this.invoiceSubtotalAmountCurrency = ei.getInvoiceSubtotalCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceSubtotalAmount = null;
            this.invoiceSubtotalAmountCurrency = "INVALID Amount";
        }
        try {
            this.invoiceSpecialHandlingAmount = ei.getInvoiceSpecialHandlingAmount(eio);
            this.invoiceSpecialHandlingAmountCurrency = ei.getInvoiceSpecialHandlingCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceSpecialHandlingAmount = null;
            this.invoiceSpecialHandlingAmountCurrency = "INVALID AMOUNT";
        }
        this.invoiceSpecialHandlingDescription = ei.getInvoiceSpecialHandlingDescription(eio);
        try {
            this.invoiceShippingAmount = ei.getInvoiceShippingAmount(eio);
            this.invoiceShippingAmountCurrency = ei.getInvoiceShippingCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceShippingAmount = null;
            this.invoiceShippingAmountCurrency = "INVALID AMOUNT";
        }
        this.invoiceShippingDescription = ei.getInvoiceShippingDescription(eio);
        try {
            this.invoiceTaxAmount = ei.getInvoiceTaxAmount(eio);
            this.invoiceTaxAmountCurrency = ei.getInvoiceTaxCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceTaxAmount = null;
            this.invoiceTaxAmountCurrency = "INVALID AMOUNT";
        }
        this.invoiceTaxDescription = ei.getInvoiceTaxDescription(eio);
        try {
            this.invoiceGrossAmount = ei.getInvoiceGrossAmount(eio);
            this.invoiceGrossAmountCurrency = ei.getInvoiceGrossCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceGrossAmount = null;
            this.invoiceGrossAmountCurrency = "INVALID AMOUNT";
        }
        try {
            this.invoiceDiscountAmount = ei.getInvoiceDiscountAmount(eio);
            this.invoiceDiscountAmountCurrency = ei.getInvoiceDiscountCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceDiscountAmount = null;
            this.invoiceDiscountAmountCurrency = "INVALID AMOUNT";
        }
        try {
            this.invoiceNetAmount = ei.getInvoiceNetAmount(eio);
            this.invoiceNetAmountCurrency = ei.getInvoiceNetCurrencyIfNotValid(eio);
        }
        catch (Exception e) {
            this.invoiceNetAmount = null;
            this.invoiceNetAmountCurrency = "INVALID AMOUNT";
        }

        for (Iterator<ElectronicInvoiceItem> rejectItemIter = eio.getInvoiceItems().iterator(); rejectItemIter.hasNext();) {
            ElectronicInvoiceItem eii = rejectItemIter.next();
            ElectronicInvoiceRejectItem eiri = new ElectronicInvoiceRejectItem(this, eii);
            this.electronicInvoiceRejectItems.add(eiri);
        }

        for (Iterator<ElectronicInvoiceRejectReason> invoiceReasonIter = eio.getOrderRejectReasons().iterator(); invoiceReasonIter.hasNext();) {
            ElectronicInvoiceRejectReason eirr = invoiceReasonIter.next();
            //FIXME fix this as part of fixing the reject document
//            eirr.setElectronicInvoiceReject(this);
            this.electronicInvoiceRejectReasons.add(eirr);
        }
    }

    private ElectronicInvoicePostalAddress getCxmlPostalAddressByAddressName(ElectronicInvoiceContact contact, String addressName) {
        if (contact != null) {
            for (Iterator<ElectronicInvoicePostalAddress> iterator = contact.getPostalAddresses().iterator(); iterator.hasNext();) {
                ElectronicInvoicePostalAddress cpa = iterator.next();
                if (addressName == null) {
                    return cpa;
                }
                else {
                    if (addressName.equalsIgnoreCase(cpa.getName())) {
                        return cpa;
                    }
                }
            }
        }
        return null;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal returnValue = zero;
        try {
            for (Iterator<ElectronicInvoiceRejectItem> iter = this.electronicInvoiceRejectItems.iterator(); iter.hasNext();) {
                ElectronicInvoiceRejectItem eiri = iter.next();
                BigDecimal toAddAmount = zero;
                if ((eiri.getInvoiceNetAmount() != null) && ((zero.compareTo(eiri.getInvoiceNetAmount())) != 0)) {
                    toAddAmount = eiri.getInvoiceNetAmount();
                }
                else if ((eiri.getInvoiceGrossAmount() != null) && ((zero.compareTo(eiri.getInvoiceGrossAmount())) != 0)) {
                    toAddAmount = eiri.getInvoiceGrossAmount();
                }
                else if ((eiri.getInvoiceSubtotalAmount() != null) && ((zero.compareTo(eiri.getInvoiceSubtotalAmount())) != 0)) {
                    toAddAmount = eiri.getInvoiceSubtotalAmount();
                }
                else if ((eiri.getInvoiceUnitPrice() != null) && ((zero.compareTo(eiri.getInvoiceUnitPrice())) != 0)) {
                    if (eiri.getInvoiceItemQuantity() != null) {
                        toAddAmount = eiri.getInvoiceUnitPrice().multiply(eiri.getInvoiceItemQuantity());
                    }
                    else {
                        toAddAmount = eiri.getInvoiceUnitPrice();
                    }
                }
                LOG.debug("getTotalAmount() setting returnValue with arithmatic => '" + returnValue.doubleValue() + "' + '" + toAddAmount.doubleValue() + "'");
                returnValue = returnValue.add(toAddAmount);
            }
            LOG.debug("getTotalAmount() returning amount " + returnValue.doubleValue());
            return returnValue;
        }
        catch (NumberFormatException n) {
            // do nothing this is already rejected
            LOG.error("getTotalAmount() Error attempting to calculate total amount for invoice with filename " + this.invoiceFileName);
            return zero;
        }
    }

    /**
     * @return Returns the invoiceLoadSummaryIdentifier.
     */
    public Integer getInvoiceLoadSummaryIdentifier() {
        return invoiceLoadSummaryIdentifier;
    }

    /**
     * @param invoiceLoadSummaryIdentifier The invoiceLoadSummaryIdentifier to set.
     */
    public void setInvoiceLoadSummaryIdentifier(Integer loadSummaryId) {
        this.invoiceLoadSummaryIdentifier = loadSummaryId;
    }

    /**
     * @return Returns the invoiceLoadSummary.
     */
    public ElectronicInvoiceLoadSummary getInvoiceLoadSummary() {
        return invoiceLoadSummary;
    }

    /**
     * @param invoiceLoadSummary The invoiceLoadSummary to set.
     */
    public void setInvoiceLoadSummary(ElectronicInvoiceLoadSummary electronicInvoiceLoadSummary) {
        this.invoiceLoadSummary = electronicInvoiceLoadSummary;
        this.invoiceLoadSummaryIdentifier = electronicInvoiceLoadSummary.getInvoiceLoadSummaryIdentifier();
    }

    /**
     * @return Returns the billToAddressCityName.
     */
    public String getBillToAddressCityName() {
        return billToAddressCityName;
    }

    /**
     * @param billToAddressCityName The billToAddressCityName to set.
     */
    public void setBillToAddressCityName(String billToAddressCityName) {
        this.billToAddressCityName = billToAddressCityName;
    }

    /**
     * @return Returns the billToAddressCountryCode.
     */
    public String getBillToAddressCountryCode() {
        return billToAddressCountryCode;
    }

    /**
     * @param billToAddressCountryCode The billToAddressCountryCode to set.
     */
    public void setBillToAddressCountryCode(String billToAddressCountryCode) {
        this.billToAddressCountryCode = billToAddressCountryCode;
    }

    /**
     * @return Returns the billToAddressCountryName.
     */
    public String getBillToAddressCountryName() {
        return billToAddressCountryName;
    }

    /**
     * @param billToAddressCountryName The billToAddressCountryName to set.
     */
    public void setBillToAddressCountryName(String billToAddressCountryName) {
        this.billToAddressCountryName = billToAddressCountryName;
    }

    /**
     * @return Returns the billToAddressLine1.
     */
    public String getBillToAddressLine1() {
        return billToAddressLine1;
    }

    /**
     * @param billToAddressLine1 The billToAddressLine1 to set.
     */
    public void setBillToAddressLine1(String billToAddressLine1) {
        this.billToAddressLine1 = billToAddressLine1;
    }

    /**
     * @return Returns the billToAddressLine2.
     */
    public String getBillToAddressLine2() {
        return billToAddressLine2;
    }

    /**
     * @param billToAddressLine2 The billToAddressLine2 to set.
     */
    public void setBillToAddressLine2(String billToAddressLine2) {
        this.billToAddressLine2 = billToAddressLine2;
    }

    /**
     * @return Returns the billToAddressLine3.
     */
    public String getBillToAddressLine3() {
        return billToAddressLine3;
    }

    /**
     * @param billToAddressLine3 The billToAddressLine3 to set.
     */
    public void setBillToAddressLine3(String billToAddressLine3) {
        this.billToAddressLine3 = billToAddressLine3;
    }

    /**
     * @return Returns the billToAddressName.
     */
    public String getBillToAddressName() {
        return billToAddressName;
    }

    /**
     * @param billToAddressName The billToAddressName to set.
     */
    public void setBillToAddressName(String billToAddressName) {
        this.billToAddressName = billToAddressName;
    }

    /**
     * @return Returns the billToAddressPostalCode.
     */
    public String getBillToAddressPostalCode() {
        return billToAddressPostalCode;
    }

    /**
     * @param billToAddressPostalCode The billToAddressPostalCode to set.
     */
    public void setBillToAddressPostalCode(String billToAddressPostalCode) {
        this.billToAddressPostalCode = billToAddressPostalCode;
    }

    /**
     * @return Returns the billToAddressStateCode.
     */
    public String getBillToAddressStateCode() {
        return billToAddressStateCode;
    }

    /**
     * @param billToAddressStateCode The billToAddressStateCode to set.
     */
    public void setBillToAddressStateCode(String billToAddressStateCode) {
        this.billToAddressStateCode = billToAddressStateCode;
    }

    /**
     * @return Returns the billToAddressType.
     */
    public String getBillToAddressType() {
        return billToAddressType;
    }

    /**
     * @param billToAddressType The billToAddressType to set.
     */
    public void setBillToAddressType(String billToAddressType) {
        this.billToAddressType = billToAddressType;
    }

    /**
     * @return Returns the deploymentMode.
     */
    public String getDeploymentMode() {
        return deploymentMode;
    }

    /**
     * @param deploymentMode The deploymentMode to set.
     */
    public void setDeploymentMode(String deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    /**
     * @return Returns the electronicInvoiceRejectItems.
     */
    public List<ElectronicInvoiceRejectItem> getElectronicInvoiceRejectItems() {
        return electronicInvoiceRejectItems;
    }

    /**
     * @param electronicInvoiceRejectItems The electronicInvoiceRejectItems to set.
     */
    public void setElectronicInvoiceRejectItems(List<ElectronicInvoiceRejectItem> electronicInvoiceRejectItems) {
        this.electronicInvoiceRejectItems = electronicInvoiceRejectItems;
    }

    /**
     * @return Returns the electronicInvoiceRejectReasons.
     */
    public List<ElectronicInvoiceRejectReason> getElectronicInvoiceRejectReasons() {
        return electronicInvoiceRejectReasons;
    }

    /**
     * @param electronicInvoiceRejectReasons The electronicInvoiceRejectReasons to set.
     */
    public void setElectronicInvoiceRejectReasons(List<ElectronicInvoiceRejectReason> electronicInvoiceRejectReasons) {
        this.electronicInvoiceRejectReasons = electronicInvoiceRejectReasons;
    }

    /**
     * @return Returns the purchaseOrderDeliveryCampusCode.
     */
    public String getPurchaseOrderDeliveryCampusCode() {
        return purchaseOrderDeliveryCampusCode;
    }

    /**
     * @param purchaseOrderDeliveryCampusCode The purchaseOrderDeliveryCampusCode to set.
     */
    public void setPurchaseOrderDeliveryCampusCode(String epicPODeliveryCampusCode) {
        this.purchaseOrderDeliveryCampusCode = epicPODeliveryCampusCode;
    }

    /**
     * @return Returns the purchaseOrderId.
     */
    public Integer getPurchaseOrderId() {
        return purchaseOrderId;
    }

    /**
     * @param purchaseOrderId The purchaseOrderId to set.
     */
    public void setPurchaseOrderId(Integer epicPurchaseOrderId) {
        this.purchaseOrderId = epicPurchaseOrderId;
    }

    /**
     * @return Returns the fileDiscountInLineIndicator.
     */
    public Boolean getFileDiscountInLineIndicator() {
        return fileDiscountInLineIndicator;
    }

    /**
     * @param fileDiscountInLineIndicator The fileDiscountInLineIndicator to set.
     */
    public void setFileDiscountInLineIndicator(Boolean fileDiscountInLineIndicator) {
        this.fileDiscountInLineIndicator = fileDiscountInLineIndicator;
    }

    /**
     * @return Returns the fileHeaderTypeIndicator.
     */
    public Boolean getFileHeaderTypeIndicator() {
        return fileHeaderTypeIndicator;
    }

    /**
     * @param fileHeaderTypeIndicator The fileHeaderTypeIndicator to set.
     */
    public void setFileHeaderTypeIndicator(Boolean fileHeaderTypeIndicator) {
        this.fileHeaderTypeIndicator = fileHeaderTypeIndicator;
    }

    /**
     * @return Returns the fileInformationOnlyIndicator.
     */
    public Boolean getFileInformationOnlyIndicator() {
        return fileInformationOnlyIndicator;
    }

    /**
     * @param fileInformationOnlyIndicator The fileInformationOnlyIndicator to set.
     */
    public void setFileInformationOnlyIndicator(Boolean fileInformationOnlyIndicator) {
        this.fileInformationOnlyIndicator = fileInformationOnlyIndicator;
    }

    /**
     * @return Returns the fileOperationId.
     */
    public String getFileOperationId() {
        return fileOperationId;
    }

    /**
     * @param fileOperationId The fileOperationId to set.
     */
    public void setFileOperationId(String fileOperationId) {
        this.fileOperationId = fileOperationId;
    }

    /**
     * @return Returns the filePurposeId.
     */
    public String getFilePurposeId() {
        return filePurposeId;
    }

    /**
     * @param filePurposeId The filePurposeId to set.
     */
    public void setFilePurposeId(String filePurposeId) {
        this.filePurposeId = filePurposeId;
    }

    /**
     * @return Returns the fileShippingInLineIndicator.
     */
    public Boolean getFileShippingInLineIndicator() {
        return fileShippingInLineIndicator;
    }

    /**
     * @param fileShippingInLineIndicator The fileShippingInLineIndicator to set.
     */
    public void setFileShippingInLineIndicator(Boolean fileShippingInLineIndicator) {
        this.fileShippingInLineIndicator = fileShippingInLineIndicator;
    }

    /**
     * @return Returns the fileSpecHandlingInLineIndicator.
     */
    public Boolean getFileSpecHandlingInLineIndicator() {
        return fileSpecHandlingInLineIndicator;
    }

    /**
     * @param fileSpecHandlingInLineIndicator The fileSpecHandlingInLineIndicator to set.
     */
    public void setFileSpecHandlingInLineIndicator(Boolean fileSpecHandlingInLineIndicator) {
        this.fileSpecHandlingInLineIndicator = fileSpecHandlingInLineIndicator;
    }

    /**
     * @return Returns the fileTaxInLineIndicator.
     */
    public Boolean getFileTaxInLineIndicator() {
        return fileTaxInLineIndicator;
    }

    /**
     * @param fileTaxInLineIndicator The fileTaxInLineIndicator to set.
     */
    public void setFileTaxInLineIndicator(Boolean fileTaxInLineIndicator) {
        this.fileTaxInLineIndicator = fileTaxInLineIndicator;
    }

    /**
     * @return Returns the purapDocumentIdentifier.
     */
    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    /**
     * @param purapDocumentIdentifier The purapDocumentIdentifier to set.
     */
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    /**
     * @return Returns the invoiceCustomerNumber.
     */
    public String getInvoiceCustomerNumber() {
        return invoiceCustomerNumber;
    }

    /**
     * @param invoiceCustomerNumber The invoiceCustomerNumber to set.
     */
    public void setInvoiceCustomerNumber(String invoiceCustomerNumber) {
        this.invoiceCustomerNumber = invoiceCustomerNumber;
    }

    /**
     * @return Returns the invoiceDate.
     */
    public java.sql.Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * @param invoiceDate The invoiceDate to set.
     */
    public void setInvoiceDate(java.sql.Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * @return Returns the invoiceDiscountAmount.
     */
    public BigDecimal getInvoiceDiscountAmount() {
        return invoiceDiscountAmount;
    }

    /**
     * @param invoiceDiscountAmount The invoiceDiscountAmount to set.
     */
    public void setInvoiceDiscountAmount(BigDecimal invoiceDiscountAmount) {
        this.invoiceDiscountAmount = invoiceDiscountAmount;
    }

    /**
     * @return Returns the invoiceDiscountAmountCurrency.
     */
    public String getInvoiceDiscountAmountCurrency() {
        return invoiceDiscountAmountCurrency;
    }

    /**
     * @param invoiceDiscountAmountCurrency The invoiceDiscountAmountCurrency to set.
     */
    public void setInvoiceDiscountAmountCurrency(String invoiceDiscountAmountCurrency) {
        this.invoiceDiscountAmountCurrency = invoiceDiscountAmountCurrency;
    }

    /**
     * @return Returns the invoiceFileName.
     */
    public String getInvoiceFileName() {
        return invoiceFileName;
    }

    /**
     * @param invoiceFileName The invoiceFileName to set.
     */
    public void setInvoiceFileName(String invoiceFileName) {
        this.invoiceFileName = invoiceFileName;
    }

    /**
     * @return Returns the invoiceGrossAmount.
     */
    public BigDecimal getInvoiceGrossAmount() {
        return invoiceGrossAmount;
    }

    /**
     * @param invoiceGrossAmount The invoiceGrossAmount to set.
     */
    public void setInvoiceGrossAmount(BigDecimal invoiceGrossAmount) {
        this.invoiceGrossAmount = invoiceGrossAmount;
    }

    /**
     * @return Returns the invoiceGrossAmountCurrency.
     */
    public String getInvoiceGrossAmountCurrency() {
        return invoiceGrossAmountCurrency;
    }

    /**
     * @param invoiceGrossAmountCurrency The invoiceGrossAmountCurrency to set.
     */
    public void setInvoiceGrossAmountCurrency(String invoiceGrossAmountCurrency) {
        this.invoiceGrossAmountCurrency = invoiceGrossAmountCurrency;
    }

    /**
     * @return Returns the invoiceNetAmount.
     */
    public BigDecimal getInvoiceNetAmount() {
        return invoiceNetAmount;
    }

    /**
     * @param invoiceNetAmount The invoiceNetAmount to set.
     */
    public void setInvoiceNetAmount(BigDecimal invoiceNetAmount) {
        this.invoiceNetAmount = invoiceNetAmount;
    }

    /**
     * @return Returns the invoiceNetAmountCurrency.
     */
    public String getInvoiceNetAmountCurrency() {
        return invoiceNetAmountCurrency;
    }

    /**
     * @param invoiceNetAmountCurrency The invoiceNetAmountCurrency to set.
     */
    public void setInvoiceNetAmountCurrency(String invoiceNetAmountCurrency) {
        this.invoiceNetAmountCurrency = invoiceNetAmountCurrency;
    }

    /**
     * @return Returns the invoiceNumber.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * @param invoiceNumber The invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * @return Returns the invoiceOrderDate.
     */
    public String getInvoiceOrderDate() {
        return invoiceOrderDate;
    }

    /**
     * @param invoiceOrderDate The invoiceOrderDate to set.
     */
    public void setInvoiceOrderDate(String invoiceOrderDate) {
        this.invoiceOrderDate = invoiceOrderDate;
    }

    /**
     * @return Returns the invoiceOrderId.
     */
    public String getInvoiceOrderId() {
        return invoiceOrderId;
    }

    /**
     * @param invoiceOrderId The invoiceOrderId to set.
     */
    public void setInvoiceOrderId(String invoiceOrderId) {
        this.invoiceOrderId = invoiceOrderId;
    }

    /**
     * @return Returns the invoiceProcessTimestamp.
     */
    public Timestamp getInvoiceProcessTimestamp() {
        return invoiceProcessTimestamp;
    }

    /**
     * @param invoiceProcessTimestamp The invoiceProcessTimestamp to set.
     */
    public void setInvoiceProcessTimestamp(Timestamp invoiceProcessTimestamp) {
        this.invoiceProcessTimestamp = invoiceProcessTimestamp;
    }

    /**
     * @return Returns the invoiceOrderPurchaseOrderId.
     */
    public String getInvoiceOrderPurchaseOrderId() {
        return invoiceOrderPurchaseOrderId;
    }

    /**
     * @param invoiceOrderPurchaseOrderId The invoiceOrderPurchaseOrderId to set.
     */
    public void setInvoiceOrderPurchaseOrderId(String invoicePurchaseOrderId) {
        this.invoiceOrderPurchaseOrderId = invoicePurchaseOrderId;
    }

    /**
     * @return Returns the invoiceShipDate.
     */
    public String getInvoiceShipDate() {
        return invoiceShipDate;
    }

    /**
     * @param invoiceShipDate The invoiceShipDate to set.
     */
    public void setInvoiceShipDate(String invoiceShipDate) {
        this.invoiceShipDate = invoiceShipDate;
    }

    /**
     * @return Returns the invoiceShippingAmount.
     */
    public BigDecimal getInvoiceShippingAmount() {
        return invoiceShippingAmount;
    }

    /**
     * @param invoiceShippingAmount The invoiceShippingAmount to set.
     */
    public void setInvoiceShippingAmount(BigDecimal invoiceShippingAmount) {
        this.invoiceShippingAmount = invoiceShippingAmount;
    }

    /**
     * @return Returns the invoiceShippingAmountCurrency.
     */
    public String getInvoiceShippingAmountCurrency() {
        return invoiceShippingAmountCurrency;
    }

    /**
     * @param invoiceShippingAmountCurrency The invoiceShippingAmountCurrency to set.
     */
    public void setInvoiceShippingAmountCurrency(String invoiceShippingAmountCurrency) {
        this.invoiceShippingAmountCurrency = invoiceShippingAmountCurrency;
    }

    /**
     * @return Returns the invoiceShippingDescription.
     */
    public String getInvoiceShippingDescription() {
        return invoiceShippingDescription;
    }

    /**
     * @param invoiceShippingDescription The invoiceShippingDescription to set.
     */
    public void setInvoiceShippingDescription(String invoiceShippingDescription) {
        this.invoiceShippingDescription = invoiceShippingDescription;
    }

    /**
     * @return Returns the invoiceSpecialHandlingAmount.
     */
    public BigDecimal getInvoiceSpecialHandlingAmount() {
        return invoiceSpecialHandlingAmount;
    }

    /**
     * @param invoiceSpecialHandlingAmount The invoiceSpecialHandlingAmount to set.
     */
    public void setInvoiceSpecialHandlingAmount(BigDecimal invoiceSpecialHandlingAmount) {
        this.invoiceSpecialHandlingAmount = invoiceSpecialHandlingAmount;
    }

    /**
     * @return Returns the invoiceSpecialHandlingAmountCurrency.
     */
    public String getInvoiceSpecialHandlingAmountCurrency() {
        return invoiceSpecialHandlingAmountCurrency;
    }

    /**
     * @param invoiceSpecialHandlingAmountCurrency The invoiceSpecialHandlingAmountCurrency to set.
     */
    public void setInvoiceSpecialHandlingAmountCurrency(String invoiceSpecialHandlingAmountCurrency) {
        this.invoiceSpecialHandlingAmountCurrency = invoiceSpecialHandlingAmountCurrency;
    }

    /**
     * @return the invoiceSpecialHandlingDescription
     */
    public String getInvoiceSpecialHandlingDescription() {
        return invoiceSpecialHandlingDescription;
    }

    /**
     * @param invoiceSpecialHandlingDescription the invoiceSpecialHandlingDescription to set
     */
    public void setInvoiceSpecialHandlingDescription(String invoiceSpecialHandlingDescription) {
        this.invoiceSpecialHandlingDescription = invoiceSpecialHandlingDescription;
    }

    /**
     * @return Returns the invoiceSubtotalAmount.
     */
    public BigDecimal getInvoiceSubtotalAmount() {
        return invoiceSubtotalAmount;
    }

    /**
     * @param invoiceSubtotalAmount The invoiceSubtotalAmount to set.
     */
    public void setInvoiceSubtotalAmount(BigDecimal invoiceSubtotalAmount) {
        this.invoiceSubtotalAmount = invoiceSubtotalAmount;
    }

    /**
     * @return Returns the invoiceSubtotalAmountCurrency.
     */
    public String getInvoiceSubtotalAmountCurrency() {
        return invoiceSubtotalAmountCurrency;
    }

    /**
     * @param invoiceSubtotalAmountCurrency The invoiceSubtotalAmountCurrency to set.
     */
    public void setInvoiceSubtotalAmountCurrency(String invoiceSubtotalAmountCurrency) {
        this.invoiceSubtotalAmountCurrency = invoiceSubtotalAmountCurrency;
    }

    /**
     * @return Returns the invoiceTaxAmount.
     */
    public BigDecimal getInvoiceTaxAmount() {
        return invoiceTaxAmount;
    }

    /**
     * @param invoiceTaxAmount The invoiceTaxAmount to set.
     */
    public void setInvoiceTaxAmount(BigDecimal invoiceTaxAmount) {
        this.invoiceTaxAmount = invoiceTaxAmount;
    }

    /**
     * @return Returns the invoiceTaxAmountCurrency.
     */
    public String getInvoiceTaxAmountCurrency() {
        return invoiceTaxAmountCurrency;
    }

    /**
     * @param invoiceTaxAmountCurrency The invoiceTaxAmountCurrency to set.
     */
    public void setInvoiceTaxAmountCurrency(String invoiceTaxAmountCurrency) {
        this.invoiceTaxAmountCurrency = invoiceTaxAmountCurrency;
    }

    /**
     * @return Returns the invoiceTaxDescription.
     */
    public String getInvoiceTaxDescription() {
        return invoiceTaxDescription;
    }

    /**
     * @param invoiceTaxDescription The invoiceTaxDescription to set.
     */
    public void setInvoiceTaxDescription(String invoiceTaxDescription) {
        this.invoiceTaxDescription = invoiceTaxDescription;
    }

    /**
     * @return Returns the lastUpdateTimestamp.
     */
    public Timestamp getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    /**
     * @param lastUpdateTimestamp The lastUpdateTimestamp to set.
     */
    public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    /**
     * @return Returns the masterAgreementInfoDate.
     */
    public String getMasterAgreementInfoDate() {
        return masterAgreementInfoDate;
    }

    /**
     * @param masterAgreementInfoDate The masterAgreementInfoDate to set.
     */
    public void setMasterAgreementInfoDate(String masterAgreementInfoDate) {
        this.masterAgreementInfoDate = masterAgreementInfoDate;
    }

    /**
     * @return Returns the masterAgreementInfoId.
     */
    public String getMasterAgreementInfoId() {
        return masterAgreementInfoId;
    }

    /**
     * @param masterAgreementInfoId The masterAgreementInfoId to set.
     */
    public void setMasterAgreementInfoId(String masterAgreementInfoId) {
        this.masterAgreementInfoId = masterAgreementInfoId;
    }

    /**
     * @return Returns the masterAgreementReferenceDate.
     */
    public String getMasterAgreementReferenceDate() {
        return masterAgreementReferenceDate;
    }

    /**
     * @param masterAgreementReferenceDate The masterAgreementReferenceDate to set.
     */
    public void setMasterAgreementReferenceDate(String masterAgreementReferenceDate) {
        this.masterAgreementReferenceDate = masterAgreementReferenceDate;
    }

    /**
     * @return Returns the masterAgreementReferenceId.
     */
    public String getMasterAgreementReferenceId() {
        return masterAgreementReferenceId;
    }

    /**
     * @param masterAgreementReferenceId The masterAgreementReferenceId to set.
     */
    public void setMasterAgreementReferenceId(String masterAgreementReferenceId) {
        this.masterAgreementReferenceId = masterAgreementReferenceId;
    }

    /**
     * @return Returns the referenceDocumentRefPayloadId.
     */
    public String getReferenceDocumentRefPayloadId() {
        return referenceDocumentRefPayloadId;
    }

    /**
     * @param referenceDocumentRefPayloadId The referenceDocumentRefPayloadId to set.
     */
    public void setReferenceDocumentRefPayloadId(String referenceDocumentRefPayloadId) {
        this.referenceDocumentRefPayloadId = referenceDocumentRefPayloadId;
    }

    /**
     * @return Returns the referenceDocumentRefText.
     */
    public String getReferenceDocumentRefText() {
        return referenceDocumentRefText;
    }

    /**
     * @param referenceDocumentRefText The referenceDocumentRefText to set.
     */
    public void setReferenceDocumentRefText(String referenceDocumentRefText) {
        this.referenceDocumentRefText = referenceDocumentRefText;
    }

    /**
     * @return Returns the referenceOrderId.
     */
    public String getReferenceOrderId() {
        return referenceOrderId;
    }

    /**
     * @param referenceOrderId The referenceOrderId to set.
     */
    public void setReferenceOrderId(String referenceOrderId) {
        this.referenceOrderId = referenceOrderId;
    }

    /**
     * @return Returns the remitToAddressCityName.
     */
    public String getRemitToAddressCityName() {
        return remitToAddressCityName;
    }

    /**
     * @param remitToAddressCityName The remitToAddressCityName to set.
     */
    public void setRemitToAddressCityName(String remitToAddressCityName) {
        this.remitToAddressCityName = remitToAddressCityName;
    }

    /**
     * @return Returns the remitToAddressCountryCode.
     */
    public String getRemitToAddressCountryCode() {
        return remitToAddressCountryCode;
    }

    /**
     * @param remitToAddressCountryCode The remitToAddressCountryCode to set.
     */
    public void setRemitToAddressCountryCode(String remitToAddressCountryCode) {
        this.remitToAddressCountryCode = remitToAddressCountryCode;
    }

    /**
     * @return Returns the remitToAddressCountryName.
     */
    public String getRemitToAddressCountryName() {
        return remitToAddressCountryName;
    }

    /**
     * @param remitToAddressCountryName The remitToAddressCountryName to set.
     */
    public void setRemitToAddressCountryName(String remitToAddressCountryName) {
        this.remitToAddressCountryName = remitToAddressCountryName;
    }

    /**
     * @return Returns the remitToAddressLine1.
     */
    public String getRemitToAddressLine1() {
        return remitToAddressLine1;
    }

    /**
     * @param remitToAddressLine1 The remitToAddressLine1 to set.
     */
    public void setRemitToAddressLine1(String remitToAddressLine1) {
        this.remitToAddressLine1 = remitToAddressLine1;
    }

    /**
     * @return Returns the remitToAddressLine2.
     */
    public String getRemitToAddressLine2() {
        return remitToAddressLine2;
    }

    /**
     * @param remitToAddressLine2 The remitToAddressLine2 to set.
     */
    public void setRemitToAddressLine2(String remitToAddressLine2) {
        this.remitToAddressLine2 = remitToAddressLine2;
    }

    /**
     * @return Returns the remitToAddressLine3.
     */
    public String getRemitToAddressLine3() {
        return remitToAddressLine3;
    }

    /**
     * @param remitToAddressLine3 The remitToAddressLine3 to set.
     */
    public void setRemitToAddressLine3(String remitToAddressLine3) {
        this.remitToAddressLine3 = remitToAddressLine3;
    }

    /**
     * @return Returns the remitToAddressName.
     */
    public String getRemitToAddressName() {
        return remitToAddressName;
    }

    /**
     * @param remitToAddressName The remitToAddressName to set.
     */
    public void setRemitToAddressName(String remitToAddressName) {
        this.remitToAddressName = remitToAddressName;
    }

    /**
     * @return Returns the remitToAddressPostalCode.
     */
    public String getRemitToAddressPostalCode() {
        return remitToAddressPostalCode;
    }

    /**
     * @param remitToAddressPostalCode The remitToAddressPostalCode to set.
     */
    public void setRemitToAddressPostalCode(String remitToAddressPostalCode) {
        this.remitToAddressPostalCode = remitToAddressPostalCode;
    }

    /**
     * @return Returns the remitToAddressStateCode.
     */
    public String getRemitToAddressStateCode() {
        return remitToAddressStateCode;
    }

    /**
     * @param remitToAddressStateCode The remitToAddressStateCode to set.
     */
    public void setRemitToAddressStateCode(String remitToAddressStateCode) {
        this.remitToAddressStateCode = remitToAddressStateCode;
    }

    /**
     * @return Returns the remitToAddressType.
     */
    public String getRemitToAddressType() {
        return remitToAddressType;
    }

    /**
     * @param remitToAddressType The remitToAddressType to set.
     */
    public void setRemitToAddressType(String remitToAddressType) {
        this.remitToAddressType = remitToAddressType;
    }

    /**
     * @return Returns the shipToAddressCityName.
     */
    public String getShipToAddressCityName() {
        return shipToAddressCityName;
    }

    /**
     * @param shipToAddressCityName The shipToAddressCityName to set.
     */
    public void setShipToAddressCityName(String shipToAddressCityName) {
        this.shipToAddressCityName = shipToAddressCityName;
    }

    /**
     * @return Returns the shipToAddressCountryCode.
     */
    public String getShipToAddressCountryCode() {
        return shipToAddressCountryCode;
    }

    /**
     * @param shipToAddressCountryCode The shipToAddressCountryCode to set.
     */
    public void setShipToAddressCountryCode(String shipToAddressCountryCode) {
        this.shipToAddressCountryCode = shipToAddressCountryCode;
    }

    /**
     * @return Returns the shipToAddressCountryName.
     */
    public String getShipToAddressCountryName() {
        return shipToAddressCountryName;
    }

    /**
     * @param shipToAddressCountryName The shipToAddressCountryName to set.
     */
    public void setShipToAddressCountryName(String shipToAddressCountryName) {
        this.shipToAddressCountryName = shipToAddressCountryName;
    }

    /**
     * @return Returns the shipToAddressLine1.
     */
    public String getShipToAddressLine1() {
        return shipToAddressLine1;
    }

    /**
     * @param shipToAddressLine1 The shipToAddressLine1 to set.
     */
    public void setShipToAddressLine1(String shipToAddressLine1) {
        this.shipToAddressLine1 = shipToAddressLine1;
    }

    /**
     * @return Returns the shipToAddressLine2.
     */
    public String getShipToAddressLine2() {
        return shipToAddressLine2;
    }

    /**
     * @param shipToAddressLine2 The shipToAddressLine2 to set.
     */
    public void setShipToAddressLine2(String shipToAddressLine2) {
        this.shipToAddressLine2 = shipToAddressLine2;
    }

    /**
     * @return Returns the shipToAddressLine3.
     */
    public String getShipToAddressLine3() {
        return shipToAddressLine3;
    }

    /**
     * @param shipToAddressLine3 The shipToAddressLine3 to set.
     */
    public void setShipToAddressLine3(String shipToAddressLine3) {
        this.shipToAddressLine3 = shipToAddressLine3;
    }

    /**
     * @return Returns the shipToAddressName.
     */
    public String getShipToAddressName() {
        return shipToAddressName;
    }

    /**
     * @param shipToAddressName The shipToAddressName to set.
     */
    public void setShipToAddressName(String shipToAddressName) {
        this.shipToAddressName = shipToAddressName;
    }

    /**
     * @return Returns the shipToAddressPostalCode.
     */
    public String getShipToAddressPostalCode() {
        return shipToAddressPostalCode;
    }

    /**
     * @param shipToAddressPostalCode The shipToAddressPostalCode to set.
     */
    public void setShipToAddressPostalCode(String shipToAddressPostalCode) {
        this.shipToAddressPostalCode = shipToAddressPostalCode;
    }

    /**
     * @return Returns the shipToAddressStateCode.
     */
    public String getShipToAddressStateCode() {
        return shipToAddressStateCode;
    }

    /**
     * @param shipToAddressStateCode The shipToAddressStateCode to set.
     */
    public void setShipToAddressStateCode(String shipToAddressStateCode) {
        this.shipToAddressStateCode = shipToAddressStateCode;
    }

    /**
     * @return Returns the shipToAddressType.
     */
    public String getShipToAddressType() {
        return shipToAddressType;
    }

    /**
     * @param shipToAddressType The shipToAddressType to set.
     */
    public void setShipToAddressType(String shipToAddressType) {
        this.shipToAddressType = shipToAddressType;
    }

    /**
     * @return Returns the supplierOrderInfoId.
     */
    public String getSupplierOrderInfoId() {
        return supplierOrderInfoId;
    }

    /**
     * @param supplierOrderInfoId The supplierOrderInfoId to set.
     */
    public void setSupplierOrderInfoId(String supplierOrderInfoId) {
        this.supplierOrderInfoId = supplierOrderInfoId;
    }

    /**
     * @return Returns the vendorDetailID.
     */
    public Integer getVendorDetailID() {
        return vendorDetailID;
    }

    /**
     * @param vendorDetailID The vendorDetailID to set.
     */
    public void setVendorDetailID(Integer vendorDetailID) {
        this.vendorDetailID = vendorDetailID;
    }

    /**
     * @return Returns the vendorDunsNumber.
     */
    public String getVendorDunsNumber() {
        return vendorDunsNumber;
    }

    /**
     * @param vendorDunsNumber The vendorDunsNumber to set.
     */
    public void setVendorDunsNumber(String vendorDunsNumber) {
        this.vendorDunsNumber = vendorDunsNumber;
    }

    /**
     * @return Returns the vendorHeaderID.
     */
    public Integer getVendorHeaderID() {
        return vendorHeaderID;
    }

    /**
     * @param vendorHeaderID The vendorHeaderID to set.
     */
    public void setVendorHeaderID(Integer vendorHeaderID) {
        this.vendorHeaderID = vendorHeaderID;
    }

    /**
     * @return Returns the version.
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version The version to set.
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    public PurchaseOrderDocument getPurchaseOrderDocument() {
        return purchaseOrderDocument;
    }

    public void setPurchaseOrderDocument(PurchaseOrderDocument purchaseOrderDocument) {
        this.purchaseOrderDocument = purchaseOrderDocument;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    // persistence broker aware methods + override
    public void beforeInsert(PersistenceBroker broker) throws PersistenceBrokerException {
        // set last update timestamp
        lastUpdateTimestamp = new Timestamp((new Date()).getTime());
    }

    public void afterInsert(PersistenceBroker broker) throws PersistenceBrokerException {
    }

    public void beforeUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
        lastUpdateTimestamp = new Timestamp((new Date()).getTime());
    }

    public void afterUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
    }

    public void beforeDelete(PersistenceBroker broker) throws PersistenceBrokerException {

    }

    public void afterDelete(PersistenceBroker broker) throws PersistenceBrokerException {

    }

    public void afterLookup(PersistenceBroker broker) throws PersistenceBrokerException {
    }

    public void initiateDocument() {
        // TODO Auto-generated method stub
    }

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public Campus getPurchaseOrderDeliveryCampus() {
        return purchaseOrderDeliveryCampus;
    }

    public void setPurchaseOrderDeliveryCampus(Campus purchaseOrderDeliveryCampus) {
        this.purchaseOrderDeliveryCampus = purchaseOrderDeliveryCampus;
    }

}
/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers, Cornell University, Trustees of
 * Indiana University, Michigan State University Board of Trustees, Trustees of San Joaquin Delta College, University of Hawai'i,
 * The Arizona Board of Regents on behalf of the University of Arizona, and the rsmart group. Licensed under the Educational
 * Community License Version 1.0 (the "License"); By obtaining, using and/or copying this Original Work, you agree that you have
 * read, understand, and will comply with the terms and conditions of the Educational Community License. You may obtain a copy of
 * the License at: http://kualiproject.org/license.html THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
