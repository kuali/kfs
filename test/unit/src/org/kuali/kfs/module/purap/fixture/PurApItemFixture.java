/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.PurApItem;

public enum PurApItemFixture {
    BASIC_QTY_ITEM_1(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // capitalAssetTransactionTypeCode
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(1), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            new KualiDecimal(1) // itemQuantity
    ), BASIC_QTY_ITEM_2(null, // itemIdentifier
            new Integer(2), // itemLineNumber
            null, // capitalAssetTransactionTypeCode
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(1), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            new KualiDecimal(1) // itemQuantity
    ), APO_QTY_ITEM_1(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // capitalAssetTransactionTypeCode
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(1.99), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(995.00), // extendedPrice
            new KualiDecimal(500) // itemQuantity
    ), APO_SERVICE_ITEM_1(null, // itemIdentifier
            new Integer(2), // itemLineNumber
            null, // capitalAssetTransactionTypeCode
            "", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(239.99), // itemUnitPrice
            "SRVC", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(239.99), // extendedPrice
            null // itemQuantity
    ), APO_FREIGHT_ITEM_1(null, // itemIdentifier
            new Integer(3), // itemLineNumber
            null, // capitalAssetTransactionTypeCode
            "", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "freight", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(12.49), // itemUnitPrice
            "FRHT", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(12.49), // extendedPrice
            null // itemQuantity
    ), BASIC_QTY_ITEM_NO_APO(null, // itemIdentifier
            new Integer(2), // itemLineNumber
            null, // capitalAssetTransactionTypeCode
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(100), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(10000), // extendedPrice
            new KualiDecimal(100) // itemQuantity
    ), REQ_MULTI_ITEM_QUANTITY(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // capitalAssetTransactionTypeCode
            "PCS", // itemUnitOfMeasureCode
            "P10M980", // itemCatalogNumber
            "Copy Paper - 8 1/2 x 11, White, 92, 20lb", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(30.20), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(604), // extendedPrice
            new KualiDecimal(20) // itemQuantity
    ), REQ_MULTI_ITEM_NON_QUANTITY(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // capitalAssetTransactionTypeCode
            "", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "consulting", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(5000), // itemUnitPrice
            "SRVC", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(5000), // extendedPrice
            null // itemQuantity
    ),
    BASIC_QTY_ITEM_NO_APO_TOTAL_NOT_GREATER_THAN_ZERO(null, // itemIdentifier
            new Integer(2), // itemLineNumber
            "", // capitalAssetTransactionTypeCode
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            "", // itemCapitalAssetNoteText
            new BigDecimal(-10), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-100), // extendedPrice
            new KualiDecimal(10) // itemQuantity
    );

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
    private KualiDecimal itemQuantity;


    private PurApItemFixture(Integer itemIdentifier, Integer itemLineNumber, String capitalAssetTransactionTypeCode, String itemUnitOfMeasureCode, String itemCatalogNumber, String itemDescription, String itemCapitalAssetNoteText, BigDecimal itemUnitPrice, String itemTypeCode, String itemAuxiliaryPartIdentifier, String externalOrganizationB2bProductReferenceNumber, String externalOrganizationB2bProductTypeName, boolean itemAssignedToTradeInIndicator, KualiDecimal extendedPrice, KualiDecimal itemQuantity) {
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
        this.itemQuantity = itemQuantity;

        // add accounts here
    }

    public PurApItem createPurApItem(Class clazz) {
        PurApItem item = null;
        try {
            item = (PurApItem) clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("item creation failed. class = " + clazz);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("item creation failed. class = " + clazz);
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
        item.setItemQuantity(itemQuantity);

        item.refreshNonUpdateableReferences();
        return item;
    }
}
