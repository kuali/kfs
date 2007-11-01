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
public class RequisitionItemCapitalAsset extends PersistableBusinessObjectBase {

    private Integer requisitionItemCapitalAssetIdentifier;
    private Integer requisitionItemIdentifier;
    private Long capitalAssetNumber;
    private String addCapitalAssetNumber;

    private RequisitionItem requisitionItem;

    /**
     * Default constructor.
     */
    public RequisitionItemCapitalAsset() {

    }

    public Integer getRequisitionItemCapitalAssetIdentifier() {
        return requisitionItemCapitalAssetIdentifier;
    }

    public void setRequisitionItemCapitalAssetIdentifier(Integer requisitionItemCapitalAssetIdentifier) {
        this.requisitionItemCapitalAssetIdentifier = requisitionItemCapitalAssetIdentifier;
    }

    public Integer getRequisitionItemIdentifier() {
        return requisitionItemIdentifier;
    }

    public void setRequisitionItemIdentifier(Integer requisitionItemIdentifier) {
        this.requisitionItemIdentifier = requisitionItemIdentifier;
    }

    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public RequisitionItem getRequisitionItem() {
        return requisitionItem;
    }

    public String getAddCapitalAssetNumber() {
        return addCapitalAssetNumber;
    }

    public void setAddCapitalAssetNumber(String addCapitalAssetNumber) {
        this.addCapitalAssetNumber = addCapitalAssetNumber;
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
