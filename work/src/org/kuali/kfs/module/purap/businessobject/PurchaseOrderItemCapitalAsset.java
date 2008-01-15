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

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Item Capital Asset Business Object.
 */
public class PurchaseOrderItemCapitalAsset extends PurchasingItemCapitalAsset {

    private String documentNumber;
    private Integer purchaseOrderItemCapitalAssetIdentifier;
    private Integer purchaseOrderItemIdentifier;

    private PurchaseOrderItem purchaseOrderItem;

    /**
     * Default constructor.
     */
    public PurchaseOrderItemCapitalAsset() {
    }
    
    /**
     * Constructs a PurchaseOrderItemCapitalAsset.
     * @param capitalAssetNumber
     */
    public PurchaseOrderItemCapitalAsset(Long capitalAssetNumber){
        this.setCapitalAssetNumber(capitalAssetNumber);
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getPurchaseOrderItemCapitalAssetIdentifier() {
        return purchaseOrderItemCapitalAssetIdentifier;
    }

    public void setPurchaseOrderItemCapitalAssetIdentifier(Integer purchaseOrderItemCapitalAssetIdentifier) {
        this.purchaseOrderItemCapitalAssetIdentifier = purchaseOrderItemCapitalAssetIdentifier;
    }

    public Integer getPurchaseOrderItemIdentifier() {
        return purchaseOrderItemIdentifier;
    }

    public void setPurchaseOrderItemIdentifier(Integer purchaseOrderItemIdentifier) {
        this.purchaseOrderItemIdentifier = purchaseOrderItemIdentifier;
    }

    public PurchaseOrderItem getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    /**
     * Sets the purchaseOrderItem attribute.
     * 
     * @param purchaseOrderItem The purchaseOrderItem to set.
     * @deprecated
     */
    public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        this.purchaseOrderItem = purchaseOrderItem;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.purchaseOrderItemCapitalAssetIdentifier != null) {
            m.put("purchaseOrderItemCapitalAssetIdentifier", this.purchaseOrderItemCapitalAssetIdentifier.toString());
        }
        return m;
    }

}
