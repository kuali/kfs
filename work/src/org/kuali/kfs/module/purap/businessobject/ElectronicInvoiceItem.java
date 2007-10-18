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
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Electronic Invoice Invoice Item Business Object.
 */
public class ElectronicInvoiceItem extends PersistableBusinessObjectBase {

	private Integer invoiceItemIdentifier;
	private Integer invoiceHeaderInformationIdentifier;
	private Integer invoiceItemLineNumber;
	private BigDecimal invoiceItemQuantity;
	private String invoiceItemUnitOfMeasureCode;
	private BigDecimal invoiceItemUnitPrice;
	private String invoiceItemUnitPriceCurrencyCode;
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
	private Integer invoiceReferenceItemLineNumber;
	private String invoiceReferenceItemSerialNumber;
	private String invoiceReferenceItemSupplierPartIdentifier;
	private String invoiceReferenceItemSupplierPartAuxiliaryIdentifier;
	private String invoiceReferenceItemDescription;
	private String invoiceReferenceItemManufacturerPartIdentifier;
	private String invoiceReferenceItemManufacturerName;
	private String invoiceReferenceItemCountryCode;
	private String invoiceReferenceItemCountryName;
	private String invoiceCatalogNumber;

    private ElectronicInvoiceHeaderInformation invoiceHeaderInformation;

	/**
	 * Default constructor.
	 */
	public ElectronicInvoiceItem() {

	}

	public String getInvoiceCatalogNumber() {
        return invoiceCatalogNumber;
    }

    public void setInvoiceCatalogNumber(String invoiceCatalogNumber) {
        this.invoiceCatalogNumber = invoiceCatalogNumber;
    }

    public ElectronicInvoiceHeaderInformation getInvoiceHeaderInformation() {
        return invoiceHeaderInformation;
    }

    /**
     * @deprecated
     */
    public void setInvoiceHeaderInformation(ElectronicInvoiceHeaderInformation invoiceHeaderInformation) {
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

    public Integer getInvoiceItemIdentifier() {
        return invoiceItemIdentifier;
    }

    public void setInvoiceItemIdentifier(Integer invoiceItemIdentifier) {
        this.invoiceItemIdentifier = invoiceItemIdentifier;
    }

    public Integer getInvoiceItemLineNumber() {
        return invoiceItemLineNumber;
    }

    public void setInvoiceItemLineNumber(Integer invoiceItemLineNumber) {
        this.invoiceItemLineNumber = invoiceItemLineNumber;
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

    public BigDecimal getInvoiceItemQuantity() {
        return invoiceItemQuantity;
    }

    public void setInvoiceItemQuantity(BigDecimal invoiceItemQuantity) {
        this.invoiceItemQuantity = invoiceItemQuantity;
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

    public String getInvoiceItemUnitOfMeasureCode() {
        return invoiceItemUnitOfMeasureCode;
    }

    public void setInvoiceItemUnitOfMeasureCode(String invoiceItemUnitOfMeasureCode) {
        this.invoiceItemUnitOfMeasureCode = invoiceItemUnitOfMeasureCode;
    }

    public BigDecimal getInvoiceItemUnitPrice() {
        return invoiceItemUnitPrice;
    }

    public void setInvoiceItemUnitPrice(BigDecimal invoiceItemUnitPrice) {
        this.invoiceItemUnitPrice = invoiceItemUnitPrice;
    }

    public String getInvoiceItemUnitPriceCurrencyCode() {
        return invoiceItemUnitPriceCurrencyCode;
    }

    public void setInvoiceItemUnitPriceCurrencyCode(String invoiceItemUnitPriceCurrencyCode) {
        this.invoiceItemUnitPriceCurrencyCode = invoiceItemUnitPriceCurrencyCode;
    }

    public String getInvoiceReferenceItemCountryCode() {
        return invoiceReferenceItemCountryCode;
    }

    public void setInvoiceReferenceItemCountryCode(String invoiceReferenceItemCountryCode) {
        this.invoiceReferenceItemCountryCode = invoiceReferenceItemCountryCode;
    }

    public String getInvoiceReferenceItemCountryName() {
        return invoiceReferenceItemCountryName;
    }

    public void setInvoiceReferenceItemCountryName(String invoiceReferenceItemCountryName) {
        this.invoiceReferenceItemCountryName = invoiceReferenceItemCountryName;
    }

    public String getInvoiceReferenceItemDescription() {
        return invoiceReferenceItemDescription;
    }

    public void setInvoiceReferenceItemDescription(String invoiceReferenceItemDescription) {
        this.invoiceReferenceItemDescription = invoiceReferenceItemDescription;
    }

    public Integer getInvoiceReferenceItemLineNumber() {
        return invoiceReferenceItemLineNumber;
    }

    public void setInvoiceReferenceItemLineNumber(Integer invoiceReferenceItemLineNumber) {
        this.invoiceReferenceItemLineNumber = invoiceReferenceItemLineNumber;
    }

    public String getInvoiceReferenceItemManufacturerName() {
        return invoiceReferenceItemManufacturerName;
    }

    public void setInvoiceReferenceItemManufacturerName(String invoiceReferenceItemManufacturerName) {
        this.invoiceReferenceItemManufacturerName = invoiceReferenceItemManufacturerName;
    }

    public String getInvoiceReferenceItemManufacturerPartIdentifier() {
        return invoiceReferenceItemManufacturerPartIdentifier;
    }

    public void setInvoiceReferenceItemManufacturerPartIdentifier(String invoiceReferenceItemManufacturerPartIdentifier) {
        this.invoiceReferenceItemManufacturerPartIdentifier = invoiceReferenceItemManufacturerPartIdentifier;
    }

    public String getInvoiceReferenceItemSerialNumber() {
        return invoiceReferenceItemSerialNumber;
    }

    public void setInvoiceReferenceItemSerialNumber(String invoiceReferenceItemSerialNumber) {
        this.invoiceReferenceItemSerialNumber = invoiceReferenceItemSerialNumber;
    }

    public String getInvoiceReferenceItemSupplierPartAuxiliaryIdentifier() {
        return invoiceReferenceItemSupplierPartAuxiliaryIdentifier;
    }

    public void setInvoiceReferenceItemSupplierPartAuxiliaryIdentifier(String invoiceReferenceItemSupplierPartAuxiliaryIdentifier) {
        this.invoiceReferenceItemSupplierPartAuxiliaryIdentifier = invoiceReferenceItemSupplierPartAuxiliaryIdentifier;
    }

    public String getInvoiceReferenceItemSupplierPartIdentifier() {
        return invoiceReferenceItemSupplierPartIdentifier;
    }

    public void setInvoiceReferenceItemSupplierPartIdentifier(String invoiceReferenceItemSupplierPartIdentifier) {
        this.invoiceReferenceItemSupplierPartIdentifier = invoiceReferenceItemSupplierPartIdentifier;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.invoiceItemIdentifier != null) {
            m.put("invoiceItemIdentifier", this.invoiceItemIdentifier.toString());
        }
	    return m;
    }
}
