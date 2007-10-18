/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"),
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

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.RequisitionItem;

public enum PurApItemFixture {
    BASIC_QTY_ITEM_1 (
            new Integer("123456"),  // itemIdentifier
            new Integer(1),         // itemLineNumber
            "",                     // capitalAssetTransactionTypeCode
            "",                     // itemUnitOfMeasureCode
            "",                     // itemCatalogNumber
            "",                     // itemDescription
            "",                     // itemCapitalAssetNoteText
            new BigDecimal(1),      // itemUnitPrice
            "ITEM",                     // itemTypeCode
            "",                     // itemAuxiliaryPartIdentifier
            "",                     // externalOrganizationB2bProductReferenceNumber
            "",                     // externalOrganizationB2bProductTypeName
            false,                  // itemAssignedToTradeInIndicator
            new KualiDecimal(0))    // extendedPrice
    ,
    BASIC_QTY_ITEM_2 (
            new Integer("123457"),  // itemIdentifier
            new Integer(2),         // itemLineNumber
            "",                     // capitalAssetTransactionTypeCode
            "",                     // itemUnitOfMeasureCode
            "",                     // itemCatalogNumber
            "",                     // itemDescription
            "",                     // itemCapitalAssetNoteText
            new BigDecimal(1),      // itemUnitPrice
            "ITEM",                     // itemTypeCode
            "",                     // itemAuxiliaryPartIdentifier
            "",                     // externalOrganizationB2bProductReferenceNumber
            "",                     // externalOrganizationB2bProductTypeName
            false,                  // itemAssignedToTradeInIndicator
            new KualiDecimal(0))    // extendedPrice
    ;
    
    
    private Integer itemIdentifier;
    private Integer itemLineNumber;
    private String capitalAssetTransactionTypeCode;
    private String itemUnitOfMeasureCode;
    private String itemCatalogNumber;
    private String itemDescription;
    private String itemCapitalAssetNoteText;
    private BigDecimal itemUnitPrice;
    private String itemTypeCode;
    private String itemAuxiliaryPartIdentifier;
    private String externalOrganizationB2bProductReferenceNumber;
    private String externalOrganizationB2bProductTypeName;
    private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal extendedPrice;

    
    private PurApItemFixture(  
     Integer itemIdentifier,
     Integer itemLineNumber,
     String capitalAssetTransactionTypeCode,
     String itemUnitOfMeasureCode,
     String itemCatalogNumber,
     String itemDescription,
     String itemCapitalAssetNoteText,
     BigDecimal itemUnitPrice,
     String itemTypeCode,
     String itemAuxiliaryPartIdentifier,
     String externalOrganizationB2bProductReferenceNumber,
     String externalOrganizationB2bProductTypeName,
     boolean itemAssignedToTradeInIndicator,
     KualiDecimal extendedPrice) {
        this.itemIdentifier = itemIdentifier;
        this.itemLineNumber = itemLineNumber;
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
        this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
        this.itemCatalogNumber = itemCatalogNumber;
        this.itemDescription = itemDescription;
        this.itemCapitalAssetNoteText = itemCapitalAssetNoteText;
        this.itemUnitPrice = itemUnitPrice;
        this.itemTypeCode = itemTypeCode;
        this.itemAuxiliaryPartIdentifier = itemAuxiliaryPartIdentifier;
        this.externalOrganizationB2bProductReferenceNumber = externalOrganizationB2bProductReferenceNumber;
        this.externalOrganizationB2bProductTypeName = externalOrganizationB2bProductTypeName;
        this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
        this.extendedPrice = extendedPrice;
        
        //add accounts here
    }
    
    public PurApItem createPurApItem(Class clazz) {
        PurApItem item = null;
        try {
            item = (PurApItem) clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("item creation failed. class = "+clazz);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("item creation failed. class = "+clazz);
        }
        item.setItemIdentifier(itemIdentifier);
        item.setItemLineNumber(itemLineNumber);
        item.setCapitalAssetTransactionTypeCode(capitalAssetTransactionTypeCode);
        item.setItemUnitOfMeasureCode(itemUnitOfMeasureCode);
        item.setItemCatalogNumber(itemCatalogNumber);
        item.setItemDescription(itemDescription);
        item.setItemCapitalAssetNoteText(itemCapitalAssetNoteText);
        item.setItemUnitPrice(itemUnitPrice);
        item.setItemTypeCode(itemTypeCode);
        item.setItemAuxiliaryPartIdentifier(itemAuxiliaryPartIdentifier);
        item.setExternalOrganizationB2bProductReferenceNumber(externalOrganizationB2bProductReferenceNumber);
        item.setExternalOrganizationB2bProductTypeName(externalOrganizationB2bProductTypeName);
        item.setItemAssignedToTradeInIndicator(itemAssignedToTradeInIndicator);
        item.setExtendedPrice(extendedPrice);
        
        item.refreshNonUpdateableReferences();
        return item;
    }
}
