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
 * Requisition Item Capital Asset Business Object.
 */
public class RequisitionItemCapitalAsset extends PurchasingItemCapitalAsset {

    private Integer requisitionItemCapitalAssetIdentifier;
    private Integer itemIdentifier;

    private RequisitionItem requisitionItem;

    /**
     * Default constructor.
     */
    public RequisitionItemCapitalAsset() {
    }
    
    /**
     * Constructs a RequisitionItemCapitalAsset.
     * @param capitalAssetNumber
     */
    public RequisitionItemCapitalAsset(Long capitalAssetNumber){
        this.setCapitalAssetNumber(capitalAssetNumber);
    }

    public Integer getRequisitionItemCapitalAssetIdentifier() {
        return requisitionItemCapitalAssetIdentifier;
    }

    public void setRequisitionItemCapitalAssetIdentifier(Integer requisitionItemCapitalAssetIdentifier) {
        this.requisitionItemCapitalAssetIdentifier = requisitionItemCapitalAssetIdentifier;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public RequisitionItem getRequisitionItem() {
        return requisitionItem;
    }

    /**
     * Sets the requisitionItem attribute.
     * 
     * @param requisitionItem The requisitionItem to set.
     * @deprecated
     */
    public void setRequisitionItem(RequisitionItem requisitionItem) {
        this.requisitionItem = requisitionItem;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.requisitionItemCapitalAssetIdentifier != null) {
            m.put("requisitionItemCapitalAssetIdentifier", this.requisitionItemCapitalAssetIdentifier.toString());
        }
        return m;
    }

}
