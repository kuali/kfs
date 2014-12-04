/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.businessobject.B2BShoppingCartItem;

/**
 * Fixture class for B2BShoppingCartItem.
 */
public enum B2BShoppingCartItemFixture {

    B2B_ITEM_USING_VENDOR_ID (
            "10", // quantity
            "A0125156", // supplierPartId
            "1012273985063\1", // supplierPartAuxiliaryId
            "252.60", // unitPrice
            "USD", // unitPriceCurrency
            "01:00 Kensington Microsaver Laptop Lock - security cable lock", // description
            "EA", // unitOfMeasure
            "64068F", // manufacturerPartID
            "Dell", // manufacturerName
            "4006-0", // externalSupplierId - vendorID
            "Punchout", // productSource
            "", // systemProductID
            //"624007902" // SupplierID-DUNS: value in KFS
            "12829371", // SupplierID-DUNS: value from SciQuest
            "4050097" // SupplierID-SystemSupplierID
    ),     
    
    B2B_ITEM_USING_VENDOR_DUNS (
            "10", // quantity
            "A0125156", // supplierPartId
            "1012273985063\1", // supplierPartAuxiliaryId
            "252.60", // unitPrice
            "USD", // unitPriceCurrency
            "01:00 Kensington Microsaver Laptop Lock - security cable lock", // description
            "EA", // unitOfMeasure
            "64068F", // manufacturerPartID
            "Dell", // manufacturerName
            "", // externalSupplierId - vendorID
            "Punchout", // productSource
            "", // systemProductID
            //"624007902" // SupplierID-DUNS: value in KFS
            "12829371", // SupplierID-DUNS: value from SciQuest
            "4050097" // SupplierID-SystemSupplierID
    ),
    ;

    public String quantity;
    public String supplierPartId;
    public String supplierPartAuxiliaryId;
    public String unitPrice;
    public String unitPriceCurrency;
    public String description;
    public String unitOfMeasure;
    public String manufacturerPartID;
    public String manufacturerName;
    public String externalSupplierId;
    public String productSource;
    public String systemProductID;
    public String duns;
    public String systemSupplierID;
    
    private B2BShoppingCartItemFixture(
            String quantity,
            String supplierPartId,
            String supplierPartAuxiliaryId,
            String unitPrice,
            String unitPriceCurrency,
            String description,
            String unitOfMeasure,
            String manufacturerPartID,
            String manufacturerName,
            String externalSupplierId,
            String productSource,
            String systemProductID,
            String duns,
            String systemSupplierID
    ) {
        this.quantity = quantity;
        this.supplierPartId = supplierPartId;
        this.supplierPartAuxiliaryId = supplierPartAuxiliaryId;
        this.unitPrice = unitPrice;
        this.unitPriceCurrency = unitPriceCurrency;
        this.description = description;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturerPartID = manufacturerPartID;
        this.manufacturerName = manufacturerName;
        this.externalSupplierId = externalSupplierId;
        this.productSource = productSource;
        this.systemProductID = systemProductID;                
        this.duns = duns;
        this.systemSupplierID = systemSupplierID;
    }
    
    /**
     * Creates a B2BShoppingCartItem from this B2BShoppingCartItemFixture.
     */
    public B2BShoppingCartItem createB2BShoppingCartItem() {
        B2BShoppingCartItem item = new B2BShoppingCartItem();
        
        item.setQuantity(quantity);
        item.setSupplierPartId(supplierPartId);
        item.setSupplierPartAuxiliaryId(supplierPartAuxiliaryId);
        
        item.setUnitPrice(unitPrice);
        item.setUnitPriceCurrency(unitPriceCurrency);
        item.setDescription(description);
        item.setUnitOfMeasure(unitOfMeasure);
        item.setManufacturerPartID(manufacturerPartID);
        item.setManufacturerName(manufacturerName);
        
        item.addExtrinsic("ExternalSupplierId", externalSupplierId);
        item.addExtrinsic("Product Source", productSource);
        item.addExtrinsic("SystemProductID", systemProductID);
        
        item.setSupplier("DUNS", duns);
        item.setSupplier("SystemSupplierID", systemSupplierID);
        
        return item;
    }
}
