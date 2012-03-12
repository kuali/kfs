/*
 * Copyright 2005-2009 The Kuali Foundation
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
import java.util.LinkedHashMap;

import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


public class ElectronicInvoiceRejectItem extends PersistableBusinessObjectBase {

    // NOT NULL FIELDS
    private Integer invoiceRejectItemIdentifier;
    private Integer purapDocumentIdentifier;

    private Integer invoiceItemLineNumber;
    private BigDecimal invoiceItemQuantity;
    private String invoiceItemUnitOfMeasureCode;
    private String invoiceItemCatalogNumber;

    private String invoiceItemUnitPriceCurrencyCode;
    private String invoiceItemSubTotalCurrencyCode;
    private String invoiceItemSpecialHandlingCurrencyCode;
    private String invoiceItemShippingCurrencyCode;
    private String invoiceItemShippingDescription;
    private String invoiceItemTaxCurrencyCode;
    private String invoiceItemTaxDescription;
    private String invoiceItemGrossCurrencyCode;
    private String invoiceItemDiscountCurrencyCode;
    private String invoiceItemNetCurrencyCode;

    private BigDecimal invoiceItemUnitPrice;
    private BigDecimal invoiceItemSubTotalAmount;
    private BigDecimal invoiceItemSpecialHandlingAmount;
    private BigDecimal invoiceItemShippingAmount;
    private BigDecimal invoiceItemTaxAmount;
    private BigDecimal invoiceItemGrossAmount;
    private BigDecimal invoiceItemDiscountAmount;
    private BigDecimal invoiceItemNetAmount;

    private Integer invoiceReferenceItemLineNumber;
    private String invoiceReferenceItemSerialNumber;
    private String invoiceReferenceItemSupplierPartIdentifier;
    private String invoiceReferenceItemSupplierPartAuxiliaryIdentifier;
    private String invoiceReferenceItemDescription;
    private String invoiceReferenceItemManufacturerPartIdentifier;
    private String invoiceReferenceItemManufacturerName;
    private String invoiceReferenceItemCountryCode;
    private String invoiceReferenceItemCountryName;

    private boolean unitOfMeasureAcceptIndicator = false;
    private boolean catalogNumberAcceptIndicator = false;

    private ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument;


    /**
   * 
   */
    public ElectronicInvoiceRejectItem() {
        super();
    }

    /**
   * 
   */
    public ElectronicInvoiceRejectItem(ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument, ElectronicInvoiceItem eii) {
        super();

        this.electronicInvoiceRejectDocument = electronicInvoiceRejectDocument;
        try {
            this.invoiceItemLineNumber = new Integer(Integer.parseInt(eii.getInvoiceLineNumber()));
        }
        catch (NumberFormatException n) {
            this.invoiceItemLineNumber = null;
        }
        this.invoiceItemCatalogNumber = eii.getCatalogNumber();
        this.invoiceItemQuantity = eii.getInvoiceLineQuantityBigDecimal();
        this.invoiceItemUnitOfMeasureCode = eii.getUnitOfMeasure();
        this.invoiceReferenceItemLineNumber = eii.getReferenceLineNumberInteger();
        this.invoiceReferenceItemSerialNumber = eii.getReferenceSerialNumber();
        this.invoiceReferenceItemSupplierPartIdentifier = eii.getReferenceItemIDSupplierPartID();
        this.invoiceReferenceItemSupplierPartAuxiliaryIdentifier = eii.getReferenceItemIDSupplierPartAuxID();
        this.invoiceReferenceItemDescription = eii.getReferenceDescription();
        this.invoiceReferenceItemManufacturerPartIdentifier = eii.getReferenceManufacturerPartID();
        this.invoiceReferenceItemManufacturerName = eii.getReferenceManufacturerName();
        this.invoiceReferenceItemCountryCode = eii.getReferenceCountryCode();
        this.invoiceReferenceItemCountryName = eii.getReferenceCountryName();

        this.invoiceItemUnitPriceCurrencyCode = eii.getUnitPriceCurrency();
        this.invoiceItemSubTotalCurrencyCode = eii.getSubTotalAmountCurrency();
        this.invoiceItemSpecialHandlingCurrencyCode = eii.getInvoiceLineSpecialHandlingAmountCurrency();
        this.invoiceItemShippingCurrencyCode = eii.getInvoiceLineShippingAmountCurrency();
        this.invoiceItemShippingDescription = eii.getInvoiceLineShippingDescription();
        this.invoiceItemTaxCurrencyCode = eii.getTaxAmountCurrency();
        this.invoiceItemTaxDescription = eii.getTaxDescription();
        this.invoiceItemGrossCurrencyCode = eii.getInvoiceLineGrossAmountCurrency();
        this.invoiceItemDiscountCurrencyCode = eii.getInvoiceLineDiscountAmountCurrency();
        this.invoiceItemNetCurrencyCode = eii.getInvoiceLineNetAmountCurrency();

        this.invoiceItemUnitPrice = eii.getInvoiceLineUnitCostBigDecimal();
        this.invoiceItemSubTotalAmount = eii.getInvoiceLineSubTotalAmountBigDecimal();
        this.invoiceItemSpecialHandlingAmount = eii.getInvoiceLineSpecialHandlingAmountBigDecimal();
        this.invoiceItemShippingAmount = eii.getInvoiceLineShippingAmountBigDecimal();
        this.invoiceItemTaxAmount = eii.getInvoiceLineTaxAmountBigDecimal();
        this.invoiceItemGrossAmount = eii.getInvoiceLineGrossAmountBigDecimal();
        this.invoiceItemDiscountAmount = eii.getInvoiceLineDiscountAmountBigDecimal();
        this.invoiceItemNetAmount = eii.getInvoiceLineNetAmountBigDecimal();

        // setup the sub total amount so that the reject prints to the files correctly
        if (((eii.getSubTotalAmount() == null) || ("".equals(eii.getSubTotalAmount())))) {
            // the sub total amount of this electronic invoice item was not given
            if (((this.invoiceItemQuantity != null) && ((BigDecimal.ZERO.compareTo(this.invoiceItemQuantity)) != 0)) && ((this.invoiceItemUnitPrice != null) && ((BigDecimal.ZERO.compareTo(this.invoiceItemUnitPrice)) != 0))) {
                // unit price and quantity are valid... calculate subtotal
                this.invoiceItemSubTotalAmount = this.invoiceItemQuantity.multiply(this.invoiceItemUnitPrice);
            }
            else if (((this.invoiceItemQuantity == null) || ("".equals(this.invoiceItemQuantity))) && ((this.invoiceItemUnitPrice != null) && ((BigDecimal.ZERO.compareTo(this.invoiceItemUnitPrice)) != 0))) {
                // quantity is empty but unit cost exists... use it
                this.invoiceItemSubTotalAmount = this.invoiceItemUnitPrice;
            }
            else {
                this.invoiceItemSubTotalAmount = null;
            }
        }
        else {
            this.invoiceItemSubTotalAmount = eii.getInvoiceLineSubTotalAmountBigDecimal();
        }
    }


    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    /**
     * @return Returns the electronicInvoiceRejectDocument.
     */
    public ElectronicInvoiceRejectDocument getElectronicInvoiceRejectDocument() {
        return electronicInvoiceRejectDocument;
    }

    /**
     * @param electronicInvoiceRejectDocument The electronicInvoiceRejectDocument to set.
     */
    public void setElectronicInvoiceRejectDocument(ElectronicInvoiceRejectDocument electronicInvoiceRejectDocument) {
        this.electronicInvoiceRejectDocument = electronicInvoiceRejectDocument;
    }

    /**
     * @return Returns the invoiceRejectItemIdentifier.
     */
    public Integer getInvoiceRejectItemIdentifier() {
        return invoiceRejectItemIdentifier;
    }

    /**
     * @param invoiceRejectItemIdentifier The invoiceRejectItemIdentifier to set.
     */
    public void setInvoiceRejectItemIdentifier(Integer id) {
        this.invoiceRejectItemIdentifier = id;
    }

    /**
     * @return Returns the invoiceItemCatalogNumber.
     */
    public String getInvoiceItemCatalogNumber() {
        return invoiceItemCatalogNumber;
    }

    /**
     * @param invoiceItemCatalogNumber The invoiceItemCatalogNumber to set.
     */
    public void setInvoiceItemCatalogNumber(String invoiceCatalogNumber) {
        this.invoiceItemCatalogNumber = invoiceCatalogNumber;
    }

    /**
     * @return Returns the invoiceItemDiscountAmount.
     */
    public BigDecimal getInvoiceItemDiscountAmount() {
        return invoiceItemDiscountAmount;
    }

    /**
     * @param invoiceItemDiscountAmount The invoiceItemDiscountAmount to set.
     */
    public void setInvoiceItemDiscountAmount(BigDecimal invoiceDiscountAmount) {
        this.invoiceItemDiscountAmount = invoiceDiscountAmount;
    }

    /**
     * @return Returns the invoiceItemDiscountCurrencyCode.
     */
    public String getInvoiceItemDiscountCurrencyCode() {
        return invoiceItemDiscountCurrencyCode;
    }

    /**
     * @param invoiceItemDiscountCurrencyCode The invoiceItemDiscountCurrencyCode to set.
     */
    public void setInvoiceItemDiscountCurrencyCode(String invoiceDiscountCurrencyCode) {
        this.invoiceItemDiscountCurrencyCode = invoiceDiscountCurrencyCode;
    }

    /**
     * @return Returns the invoiceItemGrossAmount.
     */
    public BigDecimal getInvoiceItemGrossAmount() {
        return invoiceItemGrossAmount;
    }

    /**
     * @param invoiceItemGrossAmount The invoiceItemGrossAmount to set.
     */
    public void setInvoiceItemGrossAmount(BigDecimal invoiceGrossAmount) {
        this.invoiceItemGrossAmount = invoiceGrossAmount;
    }

    /**
     * @return Returns the invoiceItemGrossCurrencyCode.
     */
    public String getInvoiceItemGrossCurrencyCode() {
        return invoiceItemGrossCurrencyCode;
    }

    /**
     * @param invoiceItemGrossCurrencyCode The invoiceItemGrossCurrencyCode to set.
     */
    public void setInvoiceItemGrossCurrencyCode(String invoiceGrossCurrencyCode) {
        this.invoiceItemGrossCurrencyCode = invoiceGrossCurrencyCode;
    }

    /**
     * @return Returns the invoiceItemQuantity.
     */
    public BigDecimal getInvoiceItemQuantity() {
        return invoiceItemQuantity;
    }

    /**
     * @param invoiceItemQuantity The invoiceItemQuantity to set.
     */
    public void setInvoiceItemQuantity(BigDecimal invoiceItemQuantity) {
        this.invoiceItemQuantity = invoiceItemQuantity;
    }

    /**
     * @return Returns the invoiceItemUnitOfMeasureCode.
     */
    public String getInvoiceItemUnitOfMeasureCode() {
        return invoiceItemUnitOfMeasureCode;
    }

    /**
     * @param invoiceItemUnitOfMeasureCode The invoiceItemUnitOfMeasureCode to set.
     */
    public void setInvoiceItemUnitOfMeasureCode(String invoiceItemUnitOfMeasureCode) {
        this.invoiceItemUnitOfMeasureCode = invoiceItemUnitOfMeasureCode;
    }

    /**
     * @return Returns the invoiceItemNetAmount.
     */
    public BigDecimal getInvoiceItemNetAmount() {
        BigDecimal returnValue = this.getInvoiceItemSubTotalAmount();

        if (returnValue != null) {
            if (this.getInvoiceItemShippingAmount() != null) {
                returnValue = returnValue.add(this.getInvoiceItemShippingAmount());
            }

            if (this.getInvoiceItemSpecialHandlingAmount() != null) {
                returnValue = returnValue.add(this.getInvoiceItemSpecialHandlingAmount());
            }

            if (this.getInvoiceItemTaxAmount() != null) {
                returnValue = returnValue.add(this.getInvoiceItemTaxAmount());
            }

            this.invoiceItemGrossAmount = returnValue;

            if (this.getInvoiceItemDiscountAmount() != null) {
                returnValue = returnValue.subtract(this.getInvoiceItemDiscountAmount());
            }
            returnValue = returnValue.setScale(4, BigDecimal.ROUND_HALF_UP);

        }
        else {
            returnValue = null;
        }

        this.invoiceItemNetAmount = returnValue;

        return this.invoiceItemNetAmount;
    }

    /**
     * @param invoiceItemNetAmount The invoiceItemNetAmount to set.
     */
    public void setInvoiceItemNetAmount(BigDecimal invoiceNetAmount) {
        this.invoiceItemNetAmount = invoiceNetAmount;
    }

    /**
     * @return Returns the invoiceItemNetCurrencyCode.
     */
    public String getInvoiceItemNetCurrencyCode() {
        return invoiceItemNetCurrencyCode;
    }

    /**
     * @param invoiceItemNetCurrencyCode The invoiceItemNetCurrencyCode to set.
     */
    public void setInvoiceItemNetCurrencyCode(String invoiceNetCurrencyCode) {
        this.invoiceItemNetCurrencyCode = invoiceNetCurrencyCode;
    }

    /**
     * @return Returns the invoiceItemShippingAmount.
     */
    public BigDecimal getInvoiceItemShippingAmount() {
        return invoiceItemShippingAmount;
    }

    /**
     * @param invoiceItemShippingAmount The invoiceItemShippingAmount to set.
     */
    public void setInvoiceItemShippingAmount(BigDecimal invoiceShippingAmount) {
        this.invoiceItemShippingAmount = invoiceShippingAmount;
    }

    /**
     * @return Returns the invoiceItemShippingCurrencyCode.
     */
    public String getInvoiceItemShippingCurrencyCode() {
        return invoiceItemShippingCurrencyCode;
    }

    /**
     * @param invoiceItemShippingCurrencyCode The invoiceItemShippingCurrencyCode to set.
     */
    public void setInvoiceItemShippingCurrencyCode(String invoiceShippingCurrencyCode) {
        this.invoiceItemShippingCurrencyCode = invoiceShippingCurrencyCode;
    }

    /**
     * @return Returns the invoiceItemShippingDescription.
     */
    public String getInvoiceItemShippingDescription() {
        return invoiceItemShippingDescription;
    }

    /**
     * @param invoiceItemShippingDescription The invoiceItemShippingDescription to set.
     */
    public void setInvoiceItemShippingDescription(String invoiceShippingDescription) {
        this.invoiceItemShippingDescription = invoiceShippingDescription;
    }

    /**
     * @return Returns the invoiceItemSpecialHandlingAmount.
     */
    public BigDecimal getInvoiceItemSpecialHandlingAmount() {
        return invoiceItemSpecialHandlingAmount;
    }

    /**
     * @param invoiceItemSpecialHandlingAmount The invoiceItemSpecialHandlingAmount to set.
     */
    public void setInvoiceItemSpecialHandlingAmount(BigDecimal invoiceSpecialHandlingAmount) {
        this.invoiceItemSpecialHandlingAmount = invoiceSpecialHandlingAmount;
    }

    /**
     * @return Returns the invoiceItemSpecialHandlingCurrencyCode.
     */
    public String getInvoiceItemSpecialHandlingCurrencyCode() {
        return invoiceItemSpecialHandlingCurrencyCode;
    }

    /**
     * @param invoiceItemSpecialHandlingCurrencyCode The invoiceItemSpecialHandlingCurrencyCode to set.
     */
    public void setInvoiceItemSpecialHandlingCurrencyCode(String invoiceSpecialHandlingCurrencyCode) {
        this.invoiceItemSpecialHandlingCurrencyCode = invoiceSpecialHandlingCurrencyCode;
    }

    /**
     * @return Returns the invoiceItemSubTotalAmount.
     */
    public BigDecimal getInvoiceItemSubTotalAmount() {
        // this needs to be calculated when read
        BigDecimal returnValue;
        if (((this.invoiceItemQuantity != null) && ((BigDecimal.ZERO.compareTo(this.invoiceItemQuantity)) != 0)) && ((this.invoiceItemUnitPrice != null) && ((BigDecimal.ZERO.compareTo(this.invoiceItemUnitPrice)) != 0))) {
            // unit price and quantity are valid... calculate subtotal
            returnValue = this.invoiceItemQuantity.multiply(this.invoiceItemUnitPrice);
        }
        else if (((this.invoiceItemQuantity == null) || ("".equals(this.invoiceItemQuantity))) && ((this.invoiceItemUnitPrice != null) && ((BigDecimal.ZERO.compareTo(this.invoiceItemUnitPrice)) != 0))) {
            // quantity is empty but unit cost exists... use it
            returnValue = this.invoiceItemUnitPrice;
        }
        else {
            returnValue = null;
        }

        if (returnValue != null) {
            invoiceItemSubTotalAmount = returnValue.setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        else {
            invoiceItemSubTotalAmount = null;
        }

        return invoiceItemSubTotalAmount;
    }

    /**
     * @param invoiceItemSubTotalAmount The invoiceItemSubTotalAmount to set.
     */
    public void setInvoiceItemSubTotalAmount(BigDecimal invoiceSubTotalAmount) {
        this.invoiceItemSubTotalAmount = invoiceSubTotalAmount;
    }

    /**
     * @return Returns the invoiceItemSubTotalCurrencyCode.
     */
    public String getInvoiceItemSubTotalCurrencyCode() {
        return invoiceItemSubTotalCurrencyCode;
    }

    /**
     * @param invoiceItemSubTotalCurrencyCode The invoiceItemSubTotalCurrencyCode to set.
     */
    public void setInvoiceItemSubTotalCurrencyCode(String invoiceSubTotalCurrencyCode) {
        this.invoiceItemSubTotalCurrencyCode = invoiceSubTotalCurrencyCode;
    }

    /**
     * @return Returns the invoiceItemTaxAmount.
     */
    public BigDecimal getInvoiceItemTaxAmount() {
        return invoiceItemTaxAmount;
    }

    /**
     * @param invoiceItemTaxAmount The invoiceItemTaxAmount to set.
     */
    public void setInvoiceItemTaxAmount(BigDecimal invoiceTaxAmount) {
        this.invoiceItemTaxAmount = invoiceTaxAmount;
    }

    /**
     * @return Returns the invoiceItemTaxCurrencyCode.
     */
    public String getInvoiceItemTaxCurrencyCode() {
        return invoiceItemTaxCurrencyCode;
    }

    /**
     * @param invoiceItemTaxCurrencyCode The invoiceItemTaxCurrencyCode to set.
     */
    public void setInvoiceItemTaxCurrencyCode(String invoiceTaxCurrencyCode) {
        this.invoiceItemTaxCurrencyCode = invoiceTaxCurrencyCode;
    }

    /**
     * @return Returns the invoiceItemTaxDescription.
     */
    public String getInvoiceItemTaxDescription() {
        return invoiceItemTaxDescription;
    }

    /**
     * @param invoiceItemTaxDescription The invoiceItemTaxDescription to set.
     */
    public void setInvoiceItemTaxDescription(String invoiceTaxDescription) {
        this.invoiceItemTaxDescription = invoiceTaxDescription;
    }

    /**
     * @return Returns the invoiceItemUnitPrice.
     */
    public BigDecimal getInvoiceItemUnitPrice() {
        return invoiceItemUnitPrice;
    }

    /**
     * @param invoiceItemUnitPrice The invoiceItemUnitPrice to set.
     */
    public void setInvoiceItemUnitPrice(BigDecimal invoiceUnitPrice) {
        this.invoiceItemUnitPrice = invoiceUnitPrice;
    }

    /**
     * @return Returns the invoiceItemUnitPriceCurrencyCode.
     */
    public String getInvoiceItemUnitPriceCurrencyCode() {
        return invoiceItemUnitPriceCurrencyCode;
    }

    /**
     * @param invoiceItemUnitPriceCurrencyCode The invoiceItemUnitPriceCurrencyCode to set.
     */
    public void setInvoiceItemUnitPriceCurrencyCode(String invoiceUnitPriceCurrency) {
        this.invoiceItemUnitPriceCurrencyCode = invoiceUnitPriceCurrency;
    }

    /**
     * @return Returns the invoiceItemLineNumber.
     */
    public Integer getInvoiceItemLineNumber() {
        return invoiceItemLineNumber;
    }

    /**
     * @param invoiceItemLineNumber The invoiceItemLineNumber to set.
     */
    public void setInvoiceItemLineNumber(Integer itemLineNumber) {
        this.invoiceItemLineNumber = itemLineNumber;
    }

    /**
     * @return Returns the invoiceReferenceItemCountryCode.
     */
    public String getInvoiceReferenceItemCountryCode() {
        return invoiceReferenceItemCountryCode;
    }

    /**
     * @param invoiceReferenceItemCountryCode The invoiceReferenceItemCountryCode to set.
     */
    public void setInvoiceReferenceItemCountryCode(String itemReferenceCountryCode) {
        this.invoiceReferenceItemCountryCode = itemReferenceCountryCode;
    }

    /**
     * @return Returns the invoiceReferenceItemCountryName.
     */
    public String getInvoiceReferenceItemCountryName() {
        return invoiceReferenceItemCountryName;
    }

    /**
     * @param invoiceReferenceItemCountryName The invoiceReferenceItemCountryName to set.
     */
    public void setInvoiceReferenceItemCountryName(String itemReferenceCountryName) {
        this.invoiceReferenceItemCountryName = itemReferenceCountryName;
    }

    /**
     * @return Returns the invoiceReferenceItemDescription.
     */
    public String getInvoiceReferenceItemDescription() {
        return invoiceReferenceItemDescription;
    }

    /**
     * @param invoiceReferenceItemDescription The invoiceReferenceItemDescription to set.
     */
    public void setInvoiceReferenceItemDescription(String itemReferenceDescription) {
        this.invoiceReferenceItemDescription = itemReferenceDescription;
    }

    /**
     * @return Returns the invoiceReferenceItemLineNumber.
     */
    public Integer getInvoiceReferenceItemLineNumber() {
        return invoiceReferenceItemLineNumber;
    }

    /**
     * @param invoiceReferenceItemLineNumber The invoiceReferenceItemLineNumber to set.
     */
    public void setInvoiceReferenceItemLineNumber(Integer itemReferenceLineNumber) {
        this.invoiceReferenceItemLineNumber = itemReferenceLineNumber;
    }

    /**
     * @return Returns the invoiceReferenceItemManufacturerName.
     */
    public String getInvoiceReferenceItemManufacturerName() {
        return invoiceReferenceItemManufacturerName;
    }

    /**
     * @param invoiceReferenceItemManufacturerName The invoiceReferenceItemManufacturerName to set.
     */
    public void setInvoiceReferenceItemManufacturerName(String itemReferenceManufacturerName) {
        this.invoiceReferenceItemManufacturerName = itemReferenceManufacturerName;
    }

    /**
     * @return Returns the invoiceReferenceItemManufacturerPartIdentifier.
     */
    public String getInvoiceReferenceItemManufacturerPartIdentifier() {
        return invoiceReferenceItemManufacturerPartIdentifier;
    }

    /**
     * @param invoiceReferenceItemManufacturerPartIdentifier The invoiceReferenceItemManufacturerPartIdentifier to set.
     */
    public void setInvoiceReferenceItemManufacturerPartIdentifier(String itemReferenceManufacturerPartId) {
        this.invoiceReferenceItemManufacturerPartIdentifier = itemReferenceManufacturerPartId;
    }

    /**
     * @return Returns the invoiceReferenceItemSerialNumber.
     */
    public String getInvoiceReferenceItemSerialNumber() {
        return invoiceReferenceItemSerialNumber;
    }

    /**
     * @param invoiceReferenceItemSerialNumber The invoiceReferenceItemSerialNumber to set.
     */
    public void setInvoiceReferenceItemSerialNumber(String itemReferenceSerialNumber) {
        this.invoiceReferenceItemSerialNumber = itemReferenceSerialNumber;
    }

    /**
     * @return Returns the invoiceReferenceItemSupplierPartAuxiliaryIdentifier.
     */
    public String getInvoiceReferenceItemSupplierPartAuxiliaryIdentifier() {
        return invoiceReferenceItemSupplierPartAuxiliaryIdentifier;
    }

    /**
     * @param invoiceReferenceItemSupplierPartAuxiliaryIdentifier The invoiceReferenceItemSupplierPartAuxiliaryIdentifier to set.
     */
    public void setInvoiceReferenceItemSupplierPartAuxiliaryIdentifier(String itemReferenceSupplierPartAuxId) {
        this.invoiceReferenceItemSupplierPartAuxiliaryIdentifier = itemReferenceSupplierPartAuxId;
    }

    /**
     * @return Returns the invoiceReferenceItemSupplierPartIdentifier.
     */
    public String getInvoiceReferenceItemSupplierPartIdentifier() {
        return invoiceReferenceItemSupplierPartIdentifier;
    }

    /**
     * @param invoiceReferenceItemSupplierPartIdentifier The invoiceReferenceItemSupplierPartIdentifier to set.
     */
    public void setInvoiceReferenceItemSupplierPartIdentifier(String itemReferenceSupplierPartId) {
        this.invoiceReferenceItemSupplierPartIdentifier = itemReferenceSupplierPartId;
    }

    public boolean isUnitOfMeasureAcceptIndicator() {
        return unitOfMeasureAcceptIndicator;
    }

    public void setUnitOfMeasureAcceptIndicator(boolean unitOfMeasureAcceptIndicator) {
        this.unitOfMeasureAcceptIndicator = unitOfMeasureAcceptIndicator;
    }

    public boolean isCatalogNumberAcceptIndicator() {
        return catalogNumberAcceptIndicator;
    }

    public void setCatalogNumberAcceptIndicator(boolean catalogNumberAcceptIndicator) {
        this.catalogNumberAcceptIndicator = catalogNumberAcceptIndicator;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("invoiceRejectItemIdentifier", this.invoiceRejectItemIdentifier);
        return m;
    }
}
