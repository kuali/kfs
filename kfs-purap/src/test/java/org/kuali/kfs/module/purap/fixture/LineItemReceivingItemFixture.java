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

import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for Purchase Order Item.
 */
public enum LineItemReceivingItemFixture {

    NORMAL_ITEM_1(
            new Integer(0), //receivingLineItemIdentifier;
            "", //documentNumber;
            new Integer(0), //purchaseOrderIdentifier;
            new Integer(1), //itemLineNumber;
            "ITEM", //itemTypeCode;
            "BX", //itemUnitOfMeasureCode;
            new KualiDecimal(30), //itemOrderedQuantity;
            "", //itemCatalogNumber;
            "red staplers", //itemDescription;
            new KualiDecimal(30), //itemReceivedTotalQuantity;
            new KualiDecimal(0), //itemReturnedTotalQuantity;
            new KualiDecimal(0), //itemDamagedTotalQuantity;
            "", //itemReasonAddedCode;
            new KualiDecimal(0), //itemReceivedPriorQuantity;
            new KualiDecimal(30) //itemReceivedToBeQuantity;
    )
    ;

    
    public Integer receivingLineItemIdentifier;
    public String documentNumber;
    public Integer purchaseOrderIdentifier;
    public Integer itemLineNumber;
    public String itemTypeCode;
    public String itemUnitOfMeasureCode;
    public KualiDecimal itemOrderedQuantity;
    public String itemCatalogNumber;
    public String itemDescription;
    public KualiDecimal itemReceivedTotalQuantity;
    public KualiDecimal itemReturnedTotalQuantity;
    public KualiDecimal itemDamagedTotalQuantity;
    public String itemReasonAddedCode;
    public KualiDecimal itemReceivedPriorQuantity;
    public KualiDecimal itemReceivedToBeQuantity;
    
    /**
     * Private Constructor.
     */
    private LineItemReceivingItemFixture(
            Integer receivingLineItemIdentifier, String documentNumber, Integer purchaseOrderIdentifier, Integer itemLineNumber,
            String itemTypeCode, String itemUnitOfMeasureCode, KualiDecimal itemOrderedQuantity, String itemCatalogNumber,
            String itemDescription, KualiDecimal itemReceivedTotalQuantity, KualiDecimal itemReturnedTotalQuantity, 
            KualiDecimal itemDamagedTotalQuantity, String itemReasonAddedCode, KualiDecimal itemReceivedPriorQuantity, KualiDecimal itemReceivedToBeQuantity) {

        this.receivingLineItemIdentifier = receivingLineItemIdentifier;
        this.documentNumber = documentNumber;
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
        this.itemLineNumber = itemLineNumber;
        this.itemTypeCode = itemTypeCode;
        this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
        this.itemOrderedQuantity = itemOrderedQuantity;
        this.itemCatalogNumber = itemCatalogNumber;
        this.itemDescription = itemDescription;
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
        this.itemReturnedTotalQuantity = itemReturnedTotalQuantity;
        this.itemDamagedTotalQuantity = itemDamagedTotalQuantity;
        this.itemReasonAddedCode = itemReasonAddedCode; 
        this.itemReceivedPriorQuantity = itemReceivedPriorQuantity;
        this.itemReceivedToBeQuantity = itemReceivedToBeQuantity;

    }
    
    /**
     * Creates a Receiving Line Item from this fixture and adds the item to the specified Receiving Line Document.
     * 
     * @param receivingLineDocument the specified Receiving Line Document.
     */
    public void addTo(LineItemReceivingDocument lineItemReceivingDocument) {
        LineItemReceivingItem item = null;
        item = (LineItemReceivingItem) this.createLineItemReceivingItem();
        lineItemReceivingDocument.addItem(item);
    }

    /**
     * Creates a Receiving Line Item.
     * 
     * @param purApItemFixture the specified PurAp Item Fixture.
     * @return the created Purchase Order Item.
     */
    public LineItemReceivingItem createLineItemReceivingItem() {
        LineItemReceivingItem item = new LineItemReceivingItem();

        item.setReceivingItemIdentifier(this.receivingLineItemIdentifier);
        item.setDocumentNumber(this.documentNumber);
        item.setPurchaseOrderIdentifier(this.purchaseOrderIdentifier);
        item.setItemLineNumber(this.itemLineNumber);
        item.setItemTypeCode(this.itemTypeCode);
        item.setItemUnitOfMeasureCode(this.itemUnitOfMeasureCode);
        item.setItemOrderedQuantity(this.itemOrderedQuantity);
        item.setItemCatalogNumber(this.itemCatalogNumber);
        item.setItemDescription(this.itemDescription);
        item.setItemReceivedTotalQuantity(this.itemReceivedTotalQuantity);
        item.setItemReturnedTotalQuantity(this.itemReturnedTotalQuantity);
        item.setItemDamagedTotalQuantity(this.itemDamagedTotalQuantity);
        item.setItemReasonAddedCode(this.itemReasonAddedCode);
        item.setItemReceivedPriorQuantity(this.itemReceivedPriorQuantity);
        item.setItemReceivedToBeQuantity(this.itemReceivedToBeQuantity);

        return item;
    }

}
