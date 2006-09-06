/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ElectronicInvoiceItem extends BusinessObjectBase {

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

	/**
	 * Gets the invoiceItemIdentifier attribute.
	 * 
	 * @return - Returns the invoiceItemIdentifier
	 * 
	 */
	public Integer getInvoiceItemIdentifier() { 
		return invoiceItemIdentifier;
	}

	/**
	 * Sets the invoiceItemIdentifier attribute.
	 * 
	 * @param - invoiceItemIdentifier The invoiceItemIdentifier to set.
	 * 
	 */
	public void setInvoiceItemIdentifier(Integer invoiceItemIdentifier) {
		this.invoiceItemIdentifier = invoiceItemIdentifier;
	}


	/**
	 * Gets the invoiceHeaderInformationIdentifier attribute.
	 * 
	 * @return - Returns the invoiceHeaderInformationIdentifier
	 * 
	 */
	public Integer getInvoiceHeaderInformationIdentifier() { 
		return invoiceHeaderInformationIdentifier;
	}

	/**
	 * Sets the invoiceHeaderInformationIdentifier attribute.
	 * 
	 * @param - invoiceHeaderInformationIdentifier The invoiceHeaderInformationIdentifier to set.
	 * 
	 */
	public void setInvoiceHeaderInformationIdentifier(Integer invoiceHeaderInformationIdentifier) {
		this.invoiceHeaderInformationIdentifier = invoiceHeaderInformationIdentifier;
	}


	/**
	 * Gets the invoiceItemLineNumber attribute.
	 * 
	 * @return - Returns the invoiceItemLineNumber
	 * 
	 */
	public Integer getInvoiceItemLineNumber() { 
		return invoiceItemLineNumber;
	}

	/**
	 * Sets the invoiceItemLineNumber attribute.
	 * 
	 * @param - invoiceItemLineNumber The invoiceItemLineNumber to set.
	 * 
	 */
	public void setInvoiceItemLineNumber(Integer invoiceItemLineNumber) {
		this.invoiceItemLineNumber = invoiceItemLineNumber;
	}


	/**
	 * Gets the invoiceItemQuantity attribute.
	 * 
	 * @return - Returns the invoiceItemQuantity
	 * 
	 */
	public BigDecimal getInvoiceItemQuantity() { 
		return invoiceItemQuantity;
	}

	/**
	 * Sets the invoiceItemQuantity attribute.
	 * 
	 * @param - invoiceItemQuantity The invoiceItemQuantity to set.
	 * 
	 */
	public void setInvoiceItemQuantity(BigDecimal invoiceItemQuantity) {
		this.invoiceItemQuantity = invoiceItemQuantity;
	}


	/**
	 * Gets the invoiceItemUnitOfMeasureCode attribute.
	 * 
	 * @return - Returns the invoiceItemUnitOfMeasureCode
	 * 
	 */
	public String getInvoiceItemUnitOfMeasureCode() { 
		return invoiceItemUnitOfMeasureCode;
	}

	/**
	 * Sets the invoiceItemUnitOfMeasureCode attribute.
	 * 
	 * @param - invoiceItemUnitOfMeasureCode The invoiceItemUnitOfMeasureCode to set.
	 * 
	 */
	public void setInvoiceItemUnitOfMeasureCode(String invoiceItemUnitOfMeasureCode) {
		this.invoiceItemUnitOfMeasureCode = invoiceItemUnitOfMeasureCode;
	}


	/**
	 * Gets the invoiceItemUnitPrice attribute.
	 * 
	 * @return - Returns the invoiceItemUnitPrice
	 * 
	 */
	public BigDecimal getInvoiceItemUnitPrice() { 
		return invoiceItemUnitPrice;
	}

	/**
	 * Sets the invoiceItemUnitPrice attribute.
	 * 
	 * @param - invoiceItemUnitPrice The invoiceItemUnitPrice to set.
	 * 
	 */
	public void setInvoiceItemUnitPrice(BigDecimal invoiceItemUnitPrice) {
		this.invoiceItemUnitPrice = invoiceItemUnitPrice;
	}


	/**
	 * Gets the invoiceItemUnitPriceCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemUnitPriceCurrencyCode
	 * 
	 */
	public String getInvoiceItemUnitPriceCurrencyCode() { 
		return invoiceItemUnitPriceCurrencyCode;
	}

	/**
	 * Sets the invoiceItemUnitPriceCurrencyCode attribute.
	 * 
	 * @param - invoiceItemUnitPriceCurrencyCode The invoiceItemUnitPriceCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemUnitPriceCurrencyCode(String invoiceItemUnitPriceCurrencyCode) {
		this.invoiceItemUnitPriceCurrencyCode = invoiceItemUnitPriceCurrencyCode;
	}


	/**
	 * Gets the invoiceItemSubTotalAmount attribute.
	 * 
	 * @return - Returns the invoiceItemSubTotalAmount
	 * 
	 */
	public BigDecimal getInvoiceItemSubTotalAmount() { 
		return invoiceItemSubTotalAmount;
	}

	/**
	 * Sets the invoiceItemSubTotalAmount attribute.
	 * 
	 * @param - invoiceItemSubTotalAmount The invoiceItemSubTotalAmount to set.
	 * 
	 */
	public void setInvoiceItemSubTotalAmount(BigDecimal invoiceItemSubTotalAmount) {
		this.invoiceItemSubTotalAmount = invoiceItemSubTotalAmount;
	}


	/**
	 * Gets the invoiceItemSubTotalCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemSubTotalCurrencyCode
	 * 
	 */
	public String getInvoiceItemSubTotalCurrencyCode() { 
		return invoiceItemSubTotalCurrencyCode;
	}

	/**
	 * Sets the invoiceItemSubTotalCurrencyCode attribute.
	 * 
	 * @param - invoiceItemSubTotalCurrencyCode The invoiceItemSubTotalCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemSubTotalCurrencyCode(String invoiceItemSubTotalCurrencyCode) {
		this.invoiceItemSubTotalCurrencyCode = invoiceItemSubTotalCurrencyCode;
	}


	/**
	 * Gets the invoiceItemSpecialHandlingAmount attribute.
	 * 
	 * @return - Returns the invoiceItemSpecialHandlingAmount
	 * 
	 */
	public BigDecimal getInvoiceItemSpecialHandlingAmount() { 
		return invoiceItemSpecialHandlingAmount;
	}

	/**
	 * Sets the invoiceItemSpecialHandlingAmount attribute.
	 * 
	 * @param - invoiceItemSpecialHandlingAmount The invoiceItemSpecialHandlingAmount to set.
	 * 
	 */
	public void setInvoiceItemSpecialHandlingAmount(BigDecimal invoiceItemSpecialHandlingAmount) {
		this.invoiceItemSpecialHandlingAmount = invoiceItemSpecialHandlingAmount;
	}


	/**
	 * Gets the invoiceItemSpecialHandlingCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemSpecialHandlingCurrencyCode
	 * 
	 */
	public String getInvoiceItemSpecialHandlingCurrencyCode() { 
		return invoiceItemSpecialHandlingCurrencyCode;
	}

	/**
	 * Sets the invoiceItemSpecialHandlingCurrencyCode attribute.
	 * 
	 * @param - invoiceItemSpecialHandlingCurrencyCode The invoiceItemSpecialHandlingCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemSpecialHandlingCurrencyCode(String invoiceItemSpecialHandlingCurrencyCode) {
		this.invoiceItemSpecialHandlingCurrencyCode = invoiceItemSpecialHandlingCurrencyCode;
	}


	/**
	 * Gets the invoiceItemShippingAmount attribute.
	 * 
	 * @return - Returns the invoiceItemShippingAmount
	 * 
	 */
	public BigDecimal getInvoiceItemShippingAmount() { 
		return invoiceItemShippingAmount;
	}

	/**
	 * Sets the invoiceItemShippingAmount attribute.
	 * 
	 * @param - invoiceItemShippingAmount The invoiceItemShippingAmount to set.
	 * 
	 */
	public void setInvoiceItemShippingAmount(BigDecimal invoiceItemShippingAmount) {
		this.invoiceItemShippingAmount = invoiceItemShippingAmount;
	}


	/**
	 * Gets the invoiceItemShippingCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemShippingCurrencyCode
	 * 
	 */
	public String getInvoiceItemShippingCurrencyCode() { 
		return invoiceItemShippingCurrencyCode;
	}

	/**
	 * Sets the invoiceItemShippingCurrencyCode attribute.
	 * 
	 * @param - invoiceItemShippingCurrencyCode The invoiceItemShippingCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemShippingCurrencyCode(String invoiceItemShippingCurrencyCode) {
		this.invoiceItemShippingCurrencyCode = invoiceItemShippingCurrencyCode;
	}


	/**
	 * Gets the invoiceItemShippingDescription attribute.
	 * 
	 * @return - Returns the invoiceItemShippingDescription
	 * 
	 */
	public String getInvoiceItemShippingDescription() { 
		return invoiceItemShippingDescription;
	}

	/**
	 * Sets the invoiceItemShippingDescription attribute.
	 * 
	 * @param - invoiceItemShippingDescription The invoiceItemShippingDescription to set.
	 * 
	 */
	public void setInvoiceItemShippingDescription(String invoiceItemShippingDescription) {
		this.invoiceItemShippingDescription = invoiceItemShippingDescription;
	}


	/**
	 * Gets the invoiceItemTaxAmount attribute.
	 * 
	 * @return - Returns the invoiceItemTaxAmount
	 * 
	 */
	public BigDecimal getInvoiceItemTaxAmount() { 
		return invoiceItemTaxAmount;
	}

	/**
	 * Sets the invoiceItemTaxAmount attribute.
	 * 
	 * @param - invoiceItemTaxAmount The invoiceItemTaxAmount to set.
	 * 
	 */
	public void setInvoiceItemTaxAmount(BigDecimal invoiceItemTaxAmount) {
		this.invoiceItemTaxAmount = invoiceItemTaxAmount;
	}


	/**
	 * Gets the invoiceItemTaxCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemTaxCurrencyCode
	 * 
	 */
	public String getInvoiceItemTaxCurrencyCode() { 
		return invoiceItemTaxCurrencyCode;
	}

	/**
	 * Sets the invoiceItemTaxCurrencyCode attribute.
	 * 
	 * @param - invoiceItemTaxCurrencyCode The invoiceItemTaxCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemTaxCurrencyCode(String invoiceItemTaxCurrencyCode) {
		this.invoiceItemTaxCurrencyCode = invoiceItemTaxCurrencyCode;
	}


	/**
	 * Gets the invoiceItemTaxDescription attribute.
	 * 
	 * @return - Returns the invoiceItemTaxDescription
	 * 
	 */
	public String getInvoiceItemTaxDescription() { 
		return invoiceItemTaxDescription;
	}

	/**
	 * Sets the invoiceItemTaxDescription attribute.
	 * 
	 * @param - invoiceItemTaxDescription The invoiceItemTaxDescription to set.
	 * 
	 */
	public void setInvoiceItemTaxDescription(String invoiceItemTaxDescription) {
		this.invoiceItemTaxDescription = invoiceItemTaxDescription;
	}


	/**
	 * Gets the invoiceItemGrossAmount attribute.
	 * 
	 * @return - Returns the invoiceItemGrossAmount
	 * 
	 */
	public BigDecimal getInvoiceItemGrossAmount() { 
		return invoiceItemGrossAmount;
	}

	/**
	 * Sets the invoiceItemGrossAmount attribute.
	 * 
	 * @param - invoiceItemGrossAmount The invoiceItemGrossAmount to set.
	 * 
	 */
	public void setInvoiceItemGrossAmount(BigDecimal invoiceItemGrossAmount) {
		this.invoiceItemGrossAmount = invoiceItemGrossAmount;
	}


	/**
	 * Gets the invoiceItemGrossCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemGrossCurrencyCode
	 * 
	 */
	public String getInvoiceItemGrossCurrencyCode() { 
		return invoiceItemGrossCurrencyCode;
	}

	/**
	 * Sets the invoiceItemGrossCurrencyCode attribute.
	 * 
	 * @param - invoiceItemGrossCurrencyCode The invoiceItemGrossCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemGrossCurrencyCode(String invoiceItemGrossCurrencyCode) {
		this.invoiceItemGrossCurrencyCode = invoiceItemGrossCurrencyCode;
	}


	/**
	 * Gets the invoiceItemDiscountAmount attribute.
	 * 
	 * @return - Returns the invoiceItemDiscountAmount
	 * 
	 */
	public BigDecimal getInvoiceItemDiscountAmount() { 
		return invoiceItemDiscountAmount;
	}

	/**
	 * Sets the invoiceItemDiscountAmount attribute.
	 * 
	 * @param - invoiceItemDiscountAmount The invoiceItemDiscountAmount to set.
	 * 
	 */
	public void setInvoiceItemDiscountAmount(BigDecimal invoiceItemDiscountAmount) {
		this.invoiceItemDiscountAmount = invoiceItemDiscountAmount;
	}


	/**
	 * Gets the invoiceItemDiscountCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemDiscountCurrencyCode
	 * 
	 */
	public String getInvoiceItemDiscountCurrencyCode() { 
		return invoiceItemDiscountCurrencyCode;
	}

	/**
	 * Sets the invoiceItemDiscountCurrencyCode attribute.
	 * 
	 * @param - invoiceItemDiscountCurrencyCode The invoiceItemDiscountCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemDiscountCurrencyCode(String invoiceItemDiscountCurrencyCode) {
		this.invoiceItemDiscountCurrencyCode = invoiceItemDiscountCurrencyCode;
	}


	/**
	 * Gets the invoiceItemNetAmount attribute.
	 * 
	 * @return - Returns the invoiceItemNetAmount
	 * 
	 */
	public BigDecimal getInvoiceItemNetAmount() { 
		return invoiceItemNetAmount;
	}

	/**
	 * Sets the invoiceItemNetAmount attribute.
	 * 
	 * @param - invoiceItemNetAmount The invoiceItemNetAmount to set.
	 * 
	 */
	public void setInvoiceItemNetAmount(BigDecimal invoiceItemNetAmount) {
		this.invoiceItemNetAmount = invoiceItemNetAmount;
	}


	/**
	 * Gets the invoiceItemNetCurrencyCode attribute.
	 * 
	 * @return - Returns the invoiceItemNetCurrencyCode
	 * 
	 */
	public String getInvoiceItemNetCurrencyCode() { 
		return invoiceItemNetCurrencyCode;
	}

	/**
	 * Sets the invoiceItemNetCurrencyCode attribute.
	 * 
	 * @param - invoiceItemNetCurrencyCode The invoiceItemNetCurrencyCode to set.
	 * 
	 */
	public void setInvoiceItemNetCurrencyCode(String invoiceItemNetCurrencyCode) {
		this.invoiceItemNetCurrencyCode = invoiceItemNetCurrencyCode;
	}


	/**
	 * Gets the invoiceReferenceItemLineNumber attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemLineNumber
	 * 
	 */
	public Integer getInvoiceReferenceItemLineNumber() { 
		return invoiceReferenceItemLineNumber;
	}

	/**
	 * Sets the invoiceReferenceItemLineNumber attribute.
	 * 
	 * @param - invoiceReferenceItemLineNumber The invoiceReferenceItemLineNumber to set.
	 * 
	 */
	public void setInvoiceReferenceItemLineNumber(Integer invoiceReferenceItemLineNumber) {
		this.invoiceReferenceItemLineNumber = invoiceReferenceItemLineNumber;
	}


	/**
	 * Gets the invoiceReferenceItemSerialNumber attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemSerialNumber
	 * 
	 */
	public String getInvoiceReferenceItemSerialNumber() { 
		return invoiceReferenceItemSerialNumber;
	}

	/**
	 * Sets the invoiceReferenceItemSerialNumber attribute.
	 * 
	 * @param - invoiceReferenceItemSerialNumber The invoiceReferenceItemSerialNumber to set.
	 * 
	 */
	public void setInvoiceReferenceItemSerialNumber(String invoiceReferenceItemSerialNumber) {
		this.invoiceReferenceItemSerialNumber = invoiceReferenceItemSerialNumber;
	}


	/**
	 * Gets the invoiceReferenceItemSupplierPartIdentifier attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemSupplierPartIdentifier
	 * 
	 */
	public String getInvoiceReferenceItemSupplierPartIdentifier() { 
		return invoiceReferenceItemSupplierPartIdentifier;
	}

	/**
	 * Sets the invoiceReferenceItemSupplierPartIdentifier attribute.
	 * 
	 * @param - invoiceReferenceItemSupplierPartIdentifier The invoiceReferenceItemSupplierPartIdentifier to set.
	 * 
	 */
	public void setInvoiceReferenceItemSupplierPartIdentifier(String invoiceReferenceItemSupplierPartIdentifier) {
		this.invoiceReferenceItemSupplierPartIdentifier = invoiceReferenceItemSupplierPartIdentifier;
	}


	/**
	 * Gets the invoiceReferenceItemSupplierPartAuxiliaryIdentifier attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemSupplierPartAuxiliaryIdentifier
	 * 
	 */
	public String getInvoiceReferenceItemSupplierPartAuxiliaryIdentifier() { 
		return invoiceReferenceItemSupplierPartAuxiliaryIdentifier;
	}

	/**
	 * Sets the invoiceReferenceItemSupplierPartAuxiliaryIdentifier attribute.
	 * 
	 * @param - invoiceReferenceItemSupplierPartAuxiliaryIdentifier The invoiceReferenceItemSupplierPartAuxiliaryIdentifier to set.
	 * 
	 */
	public void setInvoiceReferenceItemSupplierPartAuxiliaryIdentifier(String invoiceReferenceItemSupplierPartAuxiliaryIdentifier) {
		this.invoiceReferenceItemSupplierPartAuxiliaryIdentifier = invoiceReferenceItemSupplierPartAuxiliaryIdentifier;
	}


	/**
	 * Gets the invoiceReferenceItemDescription attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemDescription
	 * 
	 */
	public String getInvoiceReferenceItemDescription() { 
		return invoiceReferenceItemDescription;
	}

	/**
	 * Sets the invoiceReferenceItemDescription attribute.
	 * 
	 * @param - invoiceReferenceItemDescription The invoiceReferenceItemDescription to set.
	 * 
	 */
	public void setInvoiceReferenceItemDescription(String invoiceReferenceItemDescription) {
		this.invoiceReferenceItemDescription = invoiceReferenceItemDescription;
	}


	/**
	 * Gets the invoiceReferenceItemManufacturerPartIdentifier attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemManufacturerPartIdentifier
	 * 
	 */
	public String getInvoiceReferenceItemManufacturerPartIdentifier() { 
		return invoiceReferenceItemManufacturerPartIdentifier;
	}

	/**
	 * Sets the invoiceReferenceItemManufacturerPartIdentifier attribute.
	 * 
	 * @param - invoiceReferenceItemManufacturerPartIdentifier The invoiceReferenceItemManufacturerPartIdentifier to set.
	 * 
	 */
	public void setInvoiceReferenceItemManufacturerPartIdentifier(String invoiceReferenceItemManufacturerPartIdentifier) {
		this.invoiceReferenceItemManufacturerPartIdentifier = invoiceReferenceItemManufacturerPartIdentifier;
	}


	/**
	 * Gets the invoiceReferenceItemManufacturerName attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemManufacturerName
	 * 
	 */
	public String getInvoiceReferenceItemManufacturerName() { 
		return invoiceReferenceItemManufacturerName;
	}

	/**
	 * Sets the invoiceReferenceItemManufacturerName attribute.
	 * 
	 * @param - invoiceReferenceItemManufacturerName The invoiceReferenceItemManufacturerName to set.
	 * 
	 */
	public void setInvoiceReferenceItemManufacturerName(String invoiceReferenceItemManufacturerName) {
		this.invoiceReferenceItemManufacturerName = invoiceReferenceItemManufacturerName;
	}


	/**
	 * Gets the invoiceReferenceItemCountryCode attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemCountryCode
	 * 
	 */
	public String getInvoiceReferenceItemCountryCode() { 
		return invoiceReferenceItemCountryCode;
	}

	/**
	 * Sets the invoiceReferenceItemCountryCode attribute.
	 * 
	 * @param - invoiceReferenceItemCountryCode The invoiceReferenceItemCountryCode to set.
	 * 
	 */
	public void setInvoiceReferenceItemCountryCode(String invoiceReferenceItemCountryCode) {
		this.invoiceReferenceItemCountryCode = invoiceReferenceItemCountryCode;
	}


	/**
	 * Gets the invoiceReferenceItemCountryName attribute.
	 * 
	 * @return - Returns the invoiceReferenceItemCountryName
	 * 
	 */
	public String getInvoiceReferenceItemCountryName() { 
		return invoiceReferenceItemCountryName;
	}

	/**
	 * Sets the invoiceReferenceItemCountryName attribute.
	 * 
	 * @param - invoiceReferenceItemCountryName The invoiceReferenceItemCountryName to set.
	 * 
	 */
	public void setInvoiceReferenceItemCountryName(String invoiceReferenceItemCountryName) {
		this.invoiceReferenceItemCountryName = invoiceReferenceItemCountryName;
	}


	/**
	 * Gets the invoiceCatalogNumber attribute.
	 * 
	 * @return - Returns the invoiceCatalogNumber
	 * 
	 */
	public String getInvoiceCatalogNumber() { 
		return invoiceCatalogNumber;
	}

	/**
	 * Sets the invoiceCatalogNumber attribute.
	 * 
	 * @param - invoiceCatalogNumber The invoiceCatalogNumber to set.
	 * 
	 */
	public void setInvoiceCatalogNumber(String invoiceCatalogNumber) {
		this.invoiceCatalogNumber = invoiceCatalogNumber;
	}


	/**
	 * Gets the invoiceHeaderInformation attribute.
	 * 
	 * @return - Returns the invoiceHeaderInformation
	 * 
	 */
	public ElectronicInvoiceHeaderInformation getInvoiceHeaderInformation() { 
		return invoiceHeaderInformation;
	}

	/**
	 * Sets the invoiceHeaderInformation attribute.
	 * 
	 * @param - invoiceHeaderInformation The invoiceHeaderInformation to set.
	 * @deprecated
	 */
	public void setInvoiceHeaderInformation(ElectronicInvoiceHeaderInformation invoiceHeaderInformation) {
		this.invoiceHeaderInformation = invoiceHeaderInformation;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.invoiceItemIdentifier != null) {
            m.put("invoiceItemIdentifier", this.invoiceItemIdentifier.toString());
        }
	    return m;
    }
}
