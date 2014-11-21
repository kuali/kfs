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

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.kfs.vnd.fixture.CommodityCodeFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for Purchase Order Item.
 */
public enum PurchaseOrderItemFixture {

    PO_QTY_UNRESTRICTED_ITEM_1(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_QTY_UNRESTRICTED_ITEM_2(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.BASIC_QTY_ITEM_2, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_VALID_FREIGHT_ITEM(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.VALID_FREIGHT_ITEM, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_VALID_SHIPPING_AND_HANDLING_ITEM(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.VALID_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_WITH_MISC_CREDIT_ITEM(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.VALID_MISC_CREDIT_ITEM, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_WITH_NEGATIVE_FREIGHT_ITEM(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.NEGATIVE_FREIGHT_ITEM, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_WITH_NEGATIVE_SHIPPING_AND_HANDLING_ITEM(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.NEGATIVE_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_WITH_ZERO_FREIGHT_ITEM(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.ZERO_FREIGHT_ITEM, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_WITH_ZERO_SHIPPING_AND_HANDLING_ITEM(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.ZERO_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_WITH_MISC_ITEM_NO_DESC(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.MISC_ITEM_NO_DESC, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    PO_QTY_APO_ITEM_1(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.PO_APO_ACCOUNT_1 } // requisitionAccountMultiFixtures
    ),
    PO_ITEM_BASIC_ACTIVE_COMMODITY_CODE(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 }, // purchaseOrderAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_BASIC_ACTIVE  //commodityCodeFixture
    ),
    PO_ITEM_BASIC_INACTIVE_COMMODITY_CODE(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 }, // purchaseOrderAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_BASIC_INACTIVE  //commodityCodeFixture
    ),
    PO_ITEM_NON_EXISTENCE_COMMODITY_CODE(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 }, // purchaseOrderAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_NON_EXISTENCE //commodityCodeFixture
    ),
    PO_ITEM_THRESHOLD_CHECK(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.ITEM_FOR_THRESHOLD_CHECK, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 }, // purchaseOrderAccountMultiFixtures
            CommodityCodeFixture.COMMODITY_CODE_BASIC_ACTIVE //commodityCodeFixture 
    ),
    EINVOICE_PO_ITEM(null, // documentNumber,
            null, // itemInvoicedTotalQuantity,
            null, // itemInvoicedTotalAmount,
            null, // itemReceivedTotalQuantity,
            null, // itemReturnedTotalQuantity,
            null, // itemOutstandingEncumberedQuantity,
            null, // itemOutstandingEncumberedAmount,
            true, // itemActiveIndicator,
            PurApItemFixture.EINVOICE_ITEM, // purApItemFixture
            new PurchaseOrderAccountingLineFixture[] { PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1 } // purchaseOrderAccountMultiFixtures
    ),
    ;

    
    private String documentNumber;
    private KualiDecimal itemInvoicedTotalQuantity;
    private KualiDecimal itemInvoicedTotalAmount;
    private KualiDecimal itemReceivedTotalQuantity;
    private KualiDecimal itemReturnedTotalQuantity;
    private KualiDecimal itemOutstandingEncumberedQuantity;
    private KualiDecimal itemOutstandingEncumberedAmount;
    private boolean itemActiveIndicator = true;

    private PurApItemFixture purApItemFixture;
    private PurchaseOrderAccountingLineFixture[] purchaseOrderAccountingLineFixtures;
    private CommodityCodeFixture commodityCodeFixture;
    
    /**
     * Private Constructor.
     */
    private PurchaseOrderItemFixture(String documentNumber, KualiDecimal itemInvoicedTotalQuantity, KualiDecimal itemInvoicedTotalAmount, KualiDecimal itemReceivedTotalQuantity, KualiDecimal itemReturnedTotalQuantity, KualiDecimal itemOutstandingEncumberedQuantity, KualiDecimal itemOutstandingEncumberedAmount, boolean itemActiveIndicator, PurApItemFixture purApItemFixture, PurchaseOrderAccountingLineFixture[] purchaseOrderAccountingLineFixtures) {
        this.documentNumber = documentNumber;
        this.itemInvoicedTotalQuantity = itemInvoicedTotalQuantity;
        this.itemInvoicedTotalAmount = itemInvoicedTotalAmount;
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
        this.itemReturnedTotalQuantity = itemReturnedTotalQuantity;
        this.itemOutstandingEncumberedQuantity = itemOutstandingEncumberedQuantity;
        this.itemOutstandingEncumberedAmount = itemOutstandingEncumberedAmount;
        this.itemActiveIndicator = itemActiveIndicator;
        this.purApItemFixture = purApItemFixture;
        this.purchaseOrderAccountingLineFixtures = purchaseOrderAccountingLineFixtures;
    }

    private PurchaseOrderItemFixture(String documentNumber, KualiDecimal itemInvoicedTotalQuantity, KualiDecimal itemInvoicedTotalAmount, KualiDecimal itemReceivedTotalQuantity, KualiDecimal itemReturnedTotalQuantity, KualiDecimal itemOutstandingEncumberedQuantity, KualiDecimal itemOutstandingEncumberedAmount, boolean itemActiveIndicator, PurApItemFixture purApItemFixture, PurchaseOrderAccountingLineFixture[] purchaseOrderAccountingLineFixtures, CommodityCodeFixture commodityCodeFixture) {
        this.documentNumber = documentNumber;
        this.itemInvoicedTotalQuantity = itemInvoicedTotalQuantity;
        this.itemInvoicedTotalAmount = itemInvoicedTotalAmount;
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
        this.itemReturnedTotalQuantity = itemReturnedTotalQuantity;
        this.itemOutstandingEncumberedQuantity = itemOutstandingEncumberedQuantity;
        this.itemOutstandingEncumberedAmount = itemOutstandingEncumberedAmount;
        this.itemActiveIndicator = itemActiveIndicator;
        this.purApItemFixture = purApItemFixture;
        this.purchaseOrderAccountingLineFixtures = purchaseOrderAccountingLineFixtures;
        this.commodityCodeFixture = commodityCodeFixture;
    }
    
    /**
     * Creates a Purchase Order Item from this fixture and adds the item to the specified Purchase Order Document.
     * 
     * @param purchaseOrderDocument the specified Purchase Order Document.
     */
    public void addTo(PurchaseOrderDocument purchaseOrderDocument) {
        PurchaseOrderItem item = null;
        item = (PurchaseOrderItem) this.createPurchaseOrderItem(purApItemFixture);
        purchaseOrderDocument.addItem(item);
        // iterate over the accounts
        for (PurchaseOrderAccountingLineFixture purchaseOrderAccountMultiFixture : purchaseOrderAccountingLineFixtures) {
            purchaseOrderAccountMultiFixture.addTo(item);
            //add latest one to source accounting lines
            //purchaseOrderDocument.getSourceAccountingLines().add(item.getSourceAccountingLines().get(item.getSourceAccountingLines().size()-1));
        }
    }

    /**
     * Creates a Purchase Order Item using the specified PurAp Item Fixture.
     * 
     * @param purApItemFixture the specified PurAp Item Fixture.
     * @return the created Purchase Order Item.
     */
    public PurApItem createPurchaseOrderItem(PurApItemFixture purApItemFixture) {
        PurchaseOrderItem item = (PurchaseOrderItem) purApItemFixture.createPurApItem(PurchaseOrderItem.class);
        item.setDocumentNumber(documentNumber);
        item.setItemInvoicedTotalQuantity(itemInvoicedTotalQuantity);
        item.setItemInvoicedTotalAmount(itemInvoicedTotalAmount);
        item.setItemReceivedTotalQuantity(itemReceivedTotalQuantity);
//        item.setItemReturnedTotalQuantity(itemReturnedTotalQuantity);
        item.setItemOutstandingEncumberedQuantity(itemOutstandingEncumberedQuantity);
        item.setItemOutstandingEncumberedAmount(itemOutstandingEncumberedAmount);
        item.setItemActiveIndicator(itemActiveIndicator);

        if (commodityCodeFixture != null) {
            CommodityCode commodityCode = commodityCodeFixture.createCommodityCode();
            item.setCommodityCode(commodityCode);
            item.setPurchasingCommodityCode(commodityCode.getPurchasingCommodityCode());
        }
        
        return item;
    }

}
