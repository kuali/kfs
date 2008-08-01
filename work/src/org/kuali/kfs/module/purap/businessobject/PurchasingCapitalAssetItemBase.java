/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.businessobject;


import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.module.purap.document.PurchasingDocument;

public abstract class PurchasingCapitalAssetItemBase extends PersistableBusinessObjectBase implements PurchasingCapitalAssetItem {

    private Integer capitalAssetItemIdentifier;
    private Integer itemIdentifier;
    private String capitalAssetTransactionTypeCode;
    private Integer capitalAssetSystemIdentifier;
    
    private CapitalAssetTransactionType capitalAssetTransactionType;
    private PurchasingCapitalAssetSystem purchasingCapitalAssetSystem;
    private PurchasingDocument purchasingDocument;
    
    public PurchasingCapitalAssetItemBase(){
        super();
    }
    
    public PurchasingCapitalAssetItemBase(PurchasingDocument pd) {        
        setDocument(pd);
    }

    public Integer getCapitalAssetItemIdentifier() {
        return capitalAssetItemIdentifier;
    }

    public void setCapitalAssetItemIdentifier(Integer capitalAssetItemIdentifier) {
        this.capitalAssetItemIdentifier = capitalAssetItemIdentifier;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    public CapitalAssetTransactionType getCapitalAssetTransactionType() {
        return capitalAssetTransactionType;
    }

    public void setCapitalAssetTransactionType(CapitalAssetTransactionType capitalAssetTransactionType) {
        this.capitalAssetTransactionType = capitalAssetTransactionType;
    }

    public PurchasingCapitalAssetSystem getPurchasingCapitalAssetSystem() {
        return purchasingCapitalAssetSystem;
    }

    public void setRequisitionCapitalAssetSystem(RequisitionCapitalAssetSystem requisitionCapitalAssetSystem) {
        this.purchasingCapitalAssetSystem = requisitionCapitalAssetSystem;
    }

    public PurchasingDocument getDocument(){
        return this.purchasingDocument;
    }
    
    public void setDocument(PurchasingDocument pd){
        this.purchasingDocument = pd;        
    }
    
    public PurchasingItem getPurchasingItem(Integer itemIdentifier){       
        PurchasingDocument pd = this.getDocument();
        
        return pd.getPurchasingItem(this.getItemIdentifier());
    }
    
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("capitalAssetItemIdentifier", this.capitalAssetItemIdentifier);
        return m;
    }

}