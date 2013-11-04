/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"),
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
