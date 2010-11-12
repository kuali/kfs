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

/*
 * Created on Mar 9, 2005
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.util.ElectronicInvoiceUtils;
import org.kuali.kfs.module.purap.util.cxml.CxmlHeader;

public class ElectronicInvoice {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoice.class);

    private static BigDecimal zero = new BigDecimal(0.00);

    public static String INVOICE_AMOUNT_TYPE_CODE_ITEM = "ITEM";
    public static String INVOICE_AMOUNT_TYPE_CODE_TAX = "TAX";
    public static String INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING = "SPHD";
    public static String INVOICE_AMOUNT_TYPE_CODE_SHIPPING = "SHIP";
    public static String INVOICE_AMOUNT_TYPE_CODE_DISCOUNT = "DISC";
    public static String INVOICE_AMOUNT_TYPE_CODE_DEPOSIT = "DPST";
    public static String INVOICE_AMOUNT_TYPE_CODE_DUE = "DUE";
    public static String INVOICE_AMOUNT_TYPE_CODE_EXMT = "EXMT";

    public static boolean FILE_REJECTED = true;
    public static boolean FILE_NOT_REJECTED = false;
    public static boolean FILE_DOES_CONTAIN_REJECTS = true;
    public static boolean FILE_DOES_NOT_CONTAIN_REJECTS = false;

    private CxmlHeader cxmlHeader;
    private String customerNumber;

    private String fileName;
    private String dunsNumber;
    private Integer vendorHeaderID = null;
    private Integer vendorDetailID = null;
    private String vendorName = null;
    private boolean fileRejected = FILE_NOT_REJECTED;
    private boolean containsRejects = FILE_DOES_NOT_CONTAIN_REJECTS;
    private List fileRejectReasons = new ArrayList();

    private ElectronicInvoiceDetailRequestHeader invoiceDetailRequestHeader;
    private List invoiceDetailOrders = new ArrayList();
    private ElectronicInvoiceDetailRequestSummary invoiceDetailRequestSummary;

    /**
     * Newly Added
     */
    private String version = "1.2.019";
    private String timestamp;
    private String payloadID;
    private String locale;
    private String deploymentMode = "production";

    /**
   * 
   */
    public ElectronicInvoice() {
        super();
    }

    private boolean containsLineLevelAmounts() {
        return invoiceDetailRequestHeader.isShippingInLine() || invoiceDetailRequestHeader.isSpecialHandlingInLine() || invoiceDetailRequestHeader.isTaxInLine() || invoiceDetailRequestHeader.isDiscountInLine();
    }

    public void addFileRejectReasonToList(ElectronicInvoiceRejectReason reason) {
        this.fileRejectReasons.add(reason);
    }

    /*
     * ADDRESS METHODS
     */
    public ElectronicInvoicePostalAddress getCxmlPostalAddress(ElectronicInvoiceOrder eio, String roleID, String addressName) {
        if (this.invoiceDetailRequestHeader.isShippingInLine()) {
            return eio.getCxmlPostalAddressByRoleID(roleID, addressName);
        }
        else {
            return this.invoiceDetailRequestHeader.getCxmlPostalAddressByRoleID(roleID, addressName);
        }
    }

    public ElectronicInvoiceContact getCxmlContact(ElectronicInvoiceOrder eio, String roleID) {
        if (this.invoiceDetailRequestHeader.isShippingInLine()) {
            return eio.getCxmlContactByRoleID(roleID);
        }
        else {
            return this.invoiceDetailRequestHeader.getCxmlContactByRoleID(roleID);
        }
    }

    public String getShippingDateDisplayText(ElectronicInvoiceOrder eio) {
        Date date = null;
        String dateString = "";
        if (this.invoiceDetailRequestHeader.isShippingInLine()) {
            date = eio.getInvoiceShippingDate();
            dateString = eio.getInvoiceShippingDateString();
        }
        else {
            date = this.invoiceDetailRequestHeader.getShippingDate();
            dateString = this.invoiceDetailRequestHeader.getShippingDateString();
        }
        if (date != null) {
            return ElectronicInvoiceUtils.getDateDisplayText(date);
        }
        else {
            return dateString;
        }
    }

    public String getMasterAgreementIDInfoDateDisplayText(ElectronicInvoiceOrder eio) {
        Date date = eio.getMasterAgreementIDInfoDate();
        if (date != null) {
            return ElectronicInvoiceUtils.getDateDisplayText(date);
        }
        else {
            return eio.getMasterAgreementIDInfoDateString();
        }
    }

    public String getMasterAgreementReferenceDateDisplayText(ElectronicInvoiceOrder eio) {
        Date date = eio.getMasterAgreementReferenceDate();
        if (date != null) {
            return ElectronicInvoiceUtils.getDateDisplayText(date);
        }
        else {
            return eio.getMasterAgreementReferenceDateString();
        }
    }

    public String getOrderIDInfoDateDisplayText(ElectronicInvoiceOrder eio) {
        Date date = eio.getOrderIDInfoDate();
        if (date != null) {
            return ElectronicInvoiceUtils.getDateDisplayText(date);
        }
        else {
            return eio.getOrderIDInfoDateString();
        }
    }

    public String getInvoiceDateDisplayText() {
        Date date = this.invoiceDetailRequestHeader.getInvoiceDate();
        if (date != null) {
            return ElectronicInvoiceUtils.getDateDisplayText(date);
        }
        else {
            return this.invoiceDetailRequestHeader.getInvoiceDateString();
        }
    }

    /*
     * DESCRIPTION METHODS
     */
    public String getInvoiceShippingDescription(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isShippingInLine()) {
            return eio.getInvoiceShippingDescription();
        }
        else {
            return invoiceDetailRequestSummary.getShippingDescription();
        }
    }

    public String getInvoiceTaxDescription(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isTaxInLine()) {
            return eio.getInvoiceTaxDescription();
        }
        else {
            return invoiceDetailRequestSummary.getTaxDescription();
        }
    }

    public String getInvoiceSpecialHandlingDescription(ElectronicInvoiceOrder eio) {
       
        if (this.invoiceDetailRequestHeader.isSpecialHandlingInLine()) {
            return eio.getInvoiceSpecialHandlingDescription();
            
        }else{
            return invoiceDetailRequestSummary.getSpecialHandlingAmountDescription();
        }
    }

    /*
     * AMOUNT METHODS
     */
    public BigDecimal getFileTotalAmountForInLineItems(String invoiceLineItemType) {
        BigDecimal total = zero;
        for (Iterator orderIter = this.invoiceDetailOrders.iterator(); orderIter.hasNext();) {
            ElectronicInvoiceOrder eio = (ElectronicInvoiceOrder) orderIter.next();
            if (INVOICE_AMOUNT_TYPE_CODE_TAX.equalsIgnoreCase(invoiceLineItemType)) {
                total = total.add(eio.getInvoiceTaxAmount());
            }
            else if (INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING.equalsIgnoreCase(invoiceLineItemType)) {
                total = total.add(eio.getInvoiceSpecialHandlingAmount());
            }
            else if (INVOICE_AMOUNT_TYPE_CODE_SHIPPING.equalsIgnoreCase(invoiceLineItemType)) {
                total = total.add(eio.getInvoiceShippingAmount());
            }
            else if (INVOICE_AMOUNT_TYPE_CODE_DISCOUNT.equalsIgnoreCase(invoiceLineItemType)) {
                total = total.add(eio.getInvoiceDiscountAmount());
            }
        }
        return total;
    }

    public BigDecimal getInvoiceSubTotalAmount(ElectronicInvoiceOrder eio) {
        if (this.containsLineLevelAmounts()) {
            return eio.getInvoiceSubTotalAmount();
        }
        else {
            return invoiceDetailRequestSummary.getInvoiceSubTotalAmount();
        }
    }

    public BigDecimal getInvoiceTaxAmount(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isTaxInLine()) {
            return eio.getInvoiceTaxAmount();
        }
        else {
            return invoiceDetailRequestSummary.getInvoiceTaxAmount();
        }
    }

    public BigDecimal getInvoiceSpecialHandlingAmount(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isSpecialHandlingInLine()) {
            return eio.getInvoiceSpecialHandlingAmount();
        }
        else {
            return invoiceDetailRequestSummary.getInvoiceSpecialHandlingAmount();
        }
    }

    public BigDecimal getInvoiceShippingAmount(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isShippingInLine()) {
            return eio.getInvoiceShippingAmount();
        }
        else {
            return invoiceDetailRequestSummary.getInvoiceShippingAmount();
        }
    }

    public BigDecimal getInvoiceGrossAmount(ElectronicInvoiceOrder eio) {
        if (this.containsLineLevelAmounts()) {
            return eio.getInvoiceGrossAmount();
        }
        else {
            return invoiceDetailRequestSummary.getInvoiceGrossAmount();
        }
    }

    public BigDecimal getInvoiceDiscountAmount(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isDiscountInLine()) {
            return eio.getInvoiceDiscountAmount();
        }
        else {
            return invoiceDetailRequestSummary.getInvoiceDiscountAmount();
        }
    }

    public BigDecimal getInvoiceNetAmount(ElectronicInvoiceOrder eio) {
        if (this.containsLineLevelAmounts()) {
            return eio.getInvoiceNetAmount();
        }
        else {
            return invoiceDetailRequestSummary.getInvoiceNetAmount();
        }
    }

    public BigDecimal getInvoiceDepositAmount() {
        return invoiceDetailRequestSummary.getInvoiceDepositAmount();
    }

    public BigDecimal getInvoiceDueAmount() {
        return invoiceDetailRequestSummary.getInvoiceDueAmount();
    }

    /*
     * CURRENCY METHODS
     */
    public String getCodeOfLineItemThatContainsInvalidCurrency(String invoiceLineItemType) {
        for (Iterator orderIter = this.invoiceDetailOrders.iterator(); orderIter.hasNext();) {
            ElectronicInvoiceOrder eio = (ElectronicInvoiceOrder) orderIter.next();
            for (Iterator itemIter = eio.getInvoiceItems().iterator(); itemIter.hasNext();) {
                ElectronicInvoiceItem eii = (ElectronicInvoiceItem) itemIter.next();
                if (INVOICE_AMOUNT_TYPE_CODE_TAX.equalsIgnoreCase(invoiceLineItemType)) {
                    if (!(this.isCodeValidCurrency(eii.getTaxAmountCurrency()))) {
                        return eii.getTaxAmountCurrency();
                    }
                }
                else if (INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING.equalsIgnoreCase(invoiceLineItemType)) {
                    if (!(this.isCodeValidCurrency(eii.getInvoiceLineSpecialHandlingAmountCurrency()))) {
                        return eii.getInvoiceLineSpecialHandlingAmountCurrency();
                    }
                }
                else if (INVOICE_AMOUNT_TYPE_CODE_SHIPPING.equalsIgnoreCase(invoiceLineItemType)) {
                    if (!(this.isCodeValidCurrency(eii.getInvoiceLineShippingAmountCurrency()))) {
                        return eii.getInvoiceLineShippingAmountCurrency();
                    }
                }
                else if (INVOICE_AMOUNT_TYPE_CODE_DISCOUNT.equalsIgnoreCase(invoiceLineItemType)) {
                    if (!(this.isCodeValidCurrency(eii.getInvoiceLineDiscountAmountCurrency()))) {
                        return eii.getInvoiceLineDiscountAmountCurrency();
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method contains the mapping check for valid Currency Code(s)
     */
    public String checkCodeForValidCurrency(String code) {
        if (!(this.isCodeValidCurrency(code))) {
            return code;
        }
        else {
            return null;
        }
    }

    /**
     * This method contains the mapping check for valid Currency Code(s)
     */
    public boolean isCodeValidCurrency(String code) {
        if (code != null) {
            for (int i = 0; i < ElectronicInvoiceMappingService.CXML_VALID_CURRENCY_CODES.length; i++) {
                String validCode = ElectronicInvoiceMappingService.CXML_VALID_CURRENCY_CODES[i];
                if (code.equalsIgnoreCase(validCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getInvoiceSubTotalCurrencyIfNotValid(ElectronicInvoiceOrder eio) {
        if (this.containsLineLevelAmounts()) {
            for (Iterator iter = eio.getInvoiceItems().iterator(); iter.hasNext();) {
                ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
                String currentCode = this.checkCodeForValidCurrency(eii.getSubTotalAmountCurrency());
                if (currentCode != null) {
                    return currentCode;
                }
            }
            return null;
        }
        else {
            return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getSubTotalAmountCurrency());
        }
    }

    public String getInvoiceTaxCurrencyIfNotValid(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isTaxInLine()) {
            for (Iterator iter = eio.getInvoiceItems().iterator(); iter.hasNext();) {
                ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
                String currentCode = this.checkCodeForValidCurrency(eii.getTaxAmountCurrency());
                if (currentCode != null) {
                    return currentCode;
                }
            }
            return null;
        }
        else {
            return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getTaxAmountCurrency());
        }
    }

    public String getInvoiceSpecialHandlingCurrencyIfNotValid(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isSpecialHandlingInLine()) {
            for (Iterator iter = eio.getInvoiceItems().iterator(); iter.hasNext();) {
                ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
                String currentCode = this.checkCodeForValidCurrency(eii.getInvoiceLineSpecialHandlingAmountCurrency());
                if (currentCode != null) {
                    return currentCode;
                }
            }
            return null;
        }
        else {
            return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getSpecialHandlingAmountCurrency());
        }
    }

    public String getInvoiceShippingCurrencyIfNotValid(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isShippingInLine()) {
            for (Iterator iter = eio.getInvoiceItems().iterator(); iter.hasNext();) {
                ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
                String currentCode = this.checkCodeForValidCurrency(eii.getInvoiceLineShippingAmountCurrency());
                if (currentCode != null) {
                    return currentCode;
                }
            }
            return null;
        }
        else {
            return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getShippingAmountCurrency());
        }
    }

    public String getInvoiceGrossCurrencyIfNotValid(ElectronicInvoiceOrder eio) {
        if (this.containsLineLevelAmounts()) {
            for (Iterator iter = eio.getInvoiceItems().iterator(); iter.hasNext();) {
                ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
                String currentCode = this.checkCodeForValidCurrency(eii.getInvoiceLineGrossAmountCurrency());
                if (currentCode != null) {
                    return currentCode;
                }
            }
            return null;
        }
        else {
            return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getGrossAmountCurrency());
        }
    }

    public String getInvoiceDiscountCurrencyIfNotValid(ElectronicInvoiceOrder eio) {
        if (this.invoiceDetailRequestHeader.isDiscountInLine()) {
            for (Iterator iter = eio.getInvoiceItems().iterator(); iter.hasNext();) {
                ElectronicInvoiceItem eii = (ElectronicInvoiceItem) iter.next();
                String currentCode = this.checkCodeForValidCurrency(eii.getInvoiceLineDiscountAmountCurrency());
                if (currentCode != null) {
                    return currentCode;
                }
            }
            return null;
        }
        else {
            return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getDiscountAmountCurrency());
        }
    }

    public String getInvoiceNetCurrencyIfNotValid(ElectronicInvoiceOrder eio) {
        if (this.containsLineLevelAmounts()) {
            for (ElectronicInvoiceItem eii : eio.getInvoiceItems()) {
                String currentCode = this.checkCodeForValidCurrency(eii.getInvoiceLineNetAmountCurrency());
                if (currentCode != null) {
                    return currentCode;
                }
            }
            return null;
        }
        else {
            return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getNetAmountCurrency());
        }
    }

    public String getInvoiceDepositCurrencyIfNotValid() {
        return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getDepositAmountCurrency());
    }

    public String getInvoiceDueCurrencyIfNotValid() {
        return this.checkCodeForValidCurrency(invoiceDetailRequestSummary.getDueAmountCurrency());
    }

    /*
     * GETTERS AND SETTERS
     */

    /**
     * @return Returns the containsRejects.
     */
    public boolean isContainsRejects() {
        return containsRejects;
    }

    /**
     * @param containsRejects The containsRejects to set.
     */
    public void setContainsRejects(boolean containsRejects) {
        this.containsRejects = containsRejects;
    }

    /**
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * @return Returns the cxmlHeader.
     */
    public CxmlHeader getCxmlHeader() {
        return cxmlHeader;
    }

    /**
     * @param cxmlHeader The cxmlHeader to set.
     */
    public void setCxmlHeader(CxmlHeader cxmlHeader) {
        this.cxmlHeader = cxmlHeader;
    }

    /**
     * @return Returns the dunsNumber.
     */
    public String getDunsNumber() {
        return dunsNumber;
    }

    /**
     * @param dunsNumber The dunsNumber to set.
     */
    public void setDunsNumber(String dunsNumber) {
        this.dunsNumber = dunsNumber;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Returns the fileRejected.
     */
    public boolean isFileRejected() {
        return fileRejected;
    }

    /**
     * @param fileRejected The fileRejected to set.
     */
    public void setFileRejected(boolean fileRejected) {
        this.fileRejected = fileRejected;
    }

    /**
     * @return Returns the fileRejectReasons.
     */
    public List<ElectronicInvoiceRejectReason> getFileRejectReasons() {
        return fileRejectReasons;
    }

    /**
     * @param fileRejectReasons The fileRejectReasons to set.
     */
    public void setFileRejectReasons(List<ElectronicInvoiceRejectReason> fileRejectReasons) {
        this.fileRejectReasons = fileRejectReasons;
    }

    /**
     * @return Returns the invoiceDetailOrders.
     */
    public List<ElectronicInvoiceOrder> getInvoiceDetailOrders() {
        return invoiceDetailOrders;
    }

    /**
     * @param invoiceDetailOrders The invoiceDetailOrders to set.
     */
    public void setInvoiceDetailOrders(List<ElectronicInvoiceOrder> invoiceDetailOrders) {
        this.invoiceDetailOrders = invoiceDetailOrders;
    }

    /**
     * @return Returns the invoiceDetailRequestHeader.
     */
    public ElectronicInvoiceDetailRequestHeader getInvoiceDetailRequestHeader() {
        return invoiceDetailRequestHeader;
    }

    /**
     * @param invoiceDetailRequestHeader The invoiceDetailRequestHeader to set.
     */
    public void setInvoiceDetailRequestHeader(ElectronicInvoiceDetailRequestHeader invoiceDetailRequestHeader) {
        this.invoiceDetailRequestHeader = invoiceDetailRequestHeader;
    }

    /**
     * @return Returns the invoiceDetailRequestSummary.
     */
    public ElectronicInvoiceDetailRequestSummary getInvoiceDetailRequestSummary() {
        return invoiceDetailRequestSummary;
    }

    /**
     * @param invoiceDetailRequestSummary The invoiceDetailRequestSummary to set.
     */
    public void setInvoiceDetailRequestSummary(ElectronicInvoiceDetailRequestSummary invoiceDetailRequestSummary) {
        this.invoiceDetailRequestSummary = invoiceDetailRequestSummary;
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
     * @return the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * @param vendorName the vendorName to set
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getPayloadID() {
        return payloadID;
    }

    public void setPayloadID(String payloadID) {
        this.payloadID = payloadID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void addInvoiceDetailOrder(ElectronicInvoiceOrder electronicInvoiceOrder) {
        invoiceDetailOrders.add(electronicInvoiceOrder);
    }

    public ElectronicInvoiceOrder[] getInvoiceDetailOrdersAsArray() {
        if (invoiceDetailOrders.size() > 0) {
            ElectronicInvoiceOrder[] tempOrders = new ElectronicInvoiceOrder[invoiceDetailOrders.size()];
            invoiceDetailOrders.toArray(tempOrders);
            return tempOrders;
        }
        return null;
    }

    public String getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(String deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    public String toString() {
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("version", getVersion());
        toString.append("timestamp", getTimestamp());
        toString.append("payloadID", getPayloadID());
        toString.append("locale", getLocale());
        toString.append("customerNumber", getCustomerNumber());
        toString.append("fileName", getFileName());
        toString.append("deploymentMode", getDeploymentMode());

        toString.append("dunsNumber", getDunsNumber());
        toString.append("vendorHeaderID", getVendorHeaderID());
        toString.append("vendorDetailID", getVendorDetailID());
        toString.append("vendorName", getVendorName());
        toString.append("cxmlHeader", getCxmlHeader());
        toString.append("invoiceDetailRequestHeader", getInvoiceDetailRequestHeader());
        toString.append("invoiceDetailOrders", getInvoiceDetailOrders());
        toString.append("invoiceDetailRequestSummary", getInvoiceDetailRequestSummary());


        return toString.toString();

    }


}
