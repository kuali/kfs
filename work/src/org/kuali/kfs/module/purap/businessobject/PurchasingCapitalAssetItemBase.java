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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.cab.businessobject.AssetTransactionType;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.ObjectUtils;

public abstract class PurchasingCapitalAssetItemBase extends PersistableBusinessObjectBase implements PurchasingCapitalAssetItem {

    private Integer capitalAssetItemIdentifier;
    private Integer itemIdentifier;
    private String capitalAssetTransactionTypeCode;
    private Integer capitalAssetSystemIdentifier;
    
    private CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType;
    private CapitalAssetSystem purchasingCapitalAssetSystem;
    private PurchasingDocument purchasingDocument;
    
    public PurchasingCapitalAssetItemBase(){
        super();        
    }
    
    public PurchasingCapitalAssetItemBase(PurchasingDocument pd) {        
        setPurchasingDocument(pd);
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

    public CapitalAssetBuilderAssetTransactionType getCapitalAssetTransactionType() {
        return capitalAssetTransactionType;
    }

    public void setCapitalAssetTransactionType(CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType) {
        this.capitalAssetTransactionType = capitalAssetTransactionType;
    }

    public CapitalAssetSystem getPurchasingCapitalAssetSystem() {
        return purchasingCapitalAssetSystem;
    }

    public void setPurchasingCapitalAssetSystem(CapitalAssetSystem purchasingCapitalAssetSystem) {
        this.purchasingCapitalAssetSystem = purchasingCapitalAssetSystem;
    }

    public PurchasingDocument getPurchasingDocument(){
        if(ObjectUtils.isNull(this.purchasingDocument)){
            this.refreshReferenceObject("purchasingDocument");
        }
        
        return this.purchasingDocument;
    }
    
    public void setPurchasingDocument(PurchasingDocument pd){
        this.purchasingDocument = pd;        
    }
    
    public PurchasingItem getPurchasingItem(){       
        PurchasingDocument pd = this.getPurchasingDocument();
        
        if(( pd != null) && ( this.getItemIdentifier() != null)) {
            return pd.getPurchasingItem(this.getItemIdentifier());
        }
        else {
            return null;
        }
    }
    
    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(capitalAssetTransactionTypeCode) || ! this.getPurchasingCapitalAssetSystem().isEmpty());
    }
 
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("capitalAssetItemIdentifier", this.capitalAssetItemIdentifier);
        return m;
    }

}