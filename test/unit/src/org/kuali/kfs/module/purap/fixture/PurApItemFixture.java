/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.fixture;

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PurApItemFixture {
    BASIC_QTY_ITEM_1(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description1", // itemDescription
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
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description2", // itemDescription
            new BigDecimal(1), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            new KualiDecimal(1) // itemQuantity
    ), BASIC_QTY_ITEM_NEGATIVE(null, // itemIdentifier
            new Integer(2), // itemLineNumber
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description2", // itemDescription
            new BigDecimal("-123.45"), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal("-123.45"), // extendedPrice
            new KualiDecimal(1) // itemQuantity
    ), APO_QTY_ITEM_1(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
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
            "", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
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
            "", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "freight", // itemDescription
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
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            new BigDecimal(100), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(10000), // extendedPrice
            new KualiDecimal(100) // itemQuantity
    ), BASIC_QTY_ITEM_PERFORMANCE(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "BX", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "Thing", // itemDescription
            new BigDecimal(10.00), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(10.00), // extendedPrice
            new KualiDecimal(10.00) // itemQuantity
    ), REQ_MULTI_ITEM_QUANTITY(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "PCS", // itemUnitOfMeasureCode
            "P10M980", // itemCatalogNumber
            "Copy Paper - 8 1/2 x 11, White, 92, 20lb", // itemDescription
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
            "", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "consulting", // itemDescription
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
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            new BigDecimal(-10), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-100), // extendedPrice
            new KualiDecimal(10) // itemQuantity
    ),
    BASIC_QTY_ITEM_NULL_UNIT_PRICE(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            null, // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            new KualiDecimal(1) // itemQuantity
    ),
    INVALID_QTY_ITEM_NULL_QUANTITY(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description", // itemDescription
            new BigDecimal(100), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),    
    VALID_FREIGHT_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Freight description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),   
    VALID_SHIPPING_AND_HANDLING_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Shipping and Handling description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),    
    POSITIVE_DISC_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "DISC description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),
    POSITIVE_RSTO_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "RSTO description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),
    POSITIVE_MSCR_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "MSCR description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CRDT_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),
    POSITIVE_ORDS_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "ORDS description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),
    POSITIVE_TRDI_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "TRDI description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),
    POSITIVE_FDTX_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "FDTX description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),
    POSITIVE_STTX_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "STTX description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),
    VALID_MISC_CREDIT_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Miscellaneous Credit description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CRDT_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_FREIGHT_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Freight description", // itemDescription
            new BigDecimal(-1), // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_SHIPPING_AND_HANDLING_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Shipping and Handling description", // itemDescription
            new BigDecimal(-1), // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_MIN_ORDER_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Min Order description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_FED_GROSS_CODE_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Fed Gross Code description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FEDERAL_GROSS_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_STATE_GROSS_CODE_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "State Gross Code description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_STATE_GROSS_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_MISC_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Misc description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_DISC_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "DISC description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_RSTO_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "RSTO description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_MSCR_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "MSCR description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CRDT_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_ORDS_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "ORDS description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_TRDI_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "TRDI description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_FDTX_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "FDTX description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    NEGATIVE_STTX_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "STTX description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(-1), // extendedPrice
            null // itemQuantity
    ),
    ZERO_FREIGHT_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Freight description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),  
    ZERO_SHIPPING_AND_HANDLING_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Shipping and Handling description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_MIN_ORDER_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Min Order description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_MISC_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Misc description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_DISC_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Payment Term Disc description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_RSTO_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Restock Fee description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_FED_GROSS_CODE_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Fed Gross Code description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FEDERAL_GROSS_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_STATE_GROSS_CODE_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "State Gross Code description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_STATE_GROSS_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_MSCR_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "MSCR description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CRDT_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_ORDS_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemCatalogNumber
            "ORDS description", // itemDescription
            null, // itemCapitalAssetNoteText
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_TRDI_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "TRDI description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_FDTX_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "FDTX description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    ZERO_STTX_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "STTX description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE,  // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    FREIGHT_ITEM_NO_DESC(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),  
    SHIPPING_AND_HANDLING_ITEM_NO_DESC(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),
    MISC_ITEM_NO_DESC(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(0), // extendedPrice
            null // itemQuantity
    ),  
    VALID_MIN_ORDER_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Minimum Order description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),  
    VALID_MISC_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Miscellaneous description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),  
    VALID_FED_GROSS_CODE_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "Fed Gross description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_FEDERAL_GROSS_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),  
    VALID_STATE_GROSS_CODE_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            null, // itemUnitOfMeasureCode
            null, // itemCatalogNumber
            "State Gross description", // itemDescription
            null, // itemUnitPrice
            PurapConstants.ItemTypeCodes.ITEM_TYPE_STATE_GROSS_CODE, // itemTypeCode
            null, // itemAuxiliaryPartIdentifier
            null, // externalOrganizationB2bProductReferenceNumber
            null, // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            null // itemQuantity
    ),
    ITEM_FOR_THRESHOLD_CHECK(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "PCS", // itemUnitOfMeasureCode
            "", // itemCatalogNumber
            "description1", // itemDescription
            new BigDecimal(10), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(100), // extendedPrice
            new KualiDecimal(10) // itemQuantity
    ),  
    EINVOICE_ITEM(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "BG", // itemUnitOfMeasureCode
            "1212", // itemCatalogNumber
            "description1", // itemDescription
            new BigDecimal(10), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            new KualiDecimal(1) // itemQuantity
    ), 
    BASIC_B2B_QTY_ITEM_1(null, // itemIdentifier
            new Integer(1), // itemLineNumber
            "PCS", // itemUnitOfMeasureCode
            "777", // itemCatalogNumber
            "description1", // itemDescription
            new BigDecimal(1), // itemUnitPrice
            "ITEM", // itemTypeCode
            "", // itemAuxiliaryPartIdentifier
            "", // externalOrganizationB2bProductReferenceNumber
            "ProductSource", // externalOrganizationB2bProductTypeName
            false, // itemAssignedToTradeInIndicator
            new KualiDecimal(1), // extendedPrice
            new KualiDecimal(1) // itemQuantity
    ),
    ;

    private Integer itemIdentifier;
    private Integer itemLineNumber;
    private String itemUnitOfMeasureCode;
    private String itemCatalogNumber;
    private String itemDescription;
    private BigDecimal itemUnitPrice;
    private String itemTypeCode;
    private String itemAuxiliaryPartIdentifier;
    private String externalOrganizationB2bProductReferenceNumber;
    private String externalOrganizationB2bProductTypeName;
    private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal extendedPrice;
    private KualiDecimal itemQuantity;


    private PurApItemFixture(Integer itemIdentifier, Integer itemLineNumber, String itemUnitOfMeasureCode, String itemCatalogNumber, String itemDescription, BigDecimal itemUnitPrice, String itemTypeCode, String itemAuxiliaryPartIdentifier, String externalOrganizationB2bProductReferenceNumber, String externalOrganizationB2bProductTypeName, boolean itemAssignedToTradeInIndicator, KualiDecimal extendedPrice, KualiDecimal itemQuantity) {
        this.itemIdentifier = itemIdentifier;
        this.itemLineNumber = itemLineNumber;
        this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
        this.itemCatalogNumber = itemCatalogNumber;
        this.itemDescription = itemDescription;
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
        item.setItemUnitOfMeasureCode(itemUnitOfMeasureCode);
        item.setItemCatalogNumber(itemCatalogNumber);
        item.setItemDescription(itemDescription);
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
