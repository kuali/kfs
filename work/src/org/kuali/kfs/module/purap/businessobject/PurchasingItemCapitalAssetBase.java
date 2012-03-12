/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public abstract class PurchasingItemCapitalAssetBase extends PersistableBusinessObjectBase implements ItemCapitalAsset {

    private Long capitalAssetNumber;
    private Integer capitalAssetSystemIdentifier;
    private Integer itemCapitalAssetIdentifier;
    private CapitalAssetSystem capitalAssetSystem;
    
    /**
     * Default constructor
     */
    public PurchasingItemCapitalAssetBase() {       
        super();
    }
    
    /**
     * Constructs a PurchasingItemCapitalAsset.
     * @param capitalAssetNumber
     */
    public PurchasingItemCapitalAssetBase(Long capitalAssetNumber){
        this.capitalAssetNumber = capitalAssetNumber;
    }
        
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }
    
    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    public Integer getItemCapitalAssetIdentifier() {
        return itemCapitalAssetIdentifier;
    }

    public void setItemCapitalAssetIdentifier(Integer itemCapitalAssetIdentifier) {
        this.itemCapitalAssetIdentifier = itemCapitalAssetIdentifier;
    }

    public CapitalAssetSystem getCapitalAssetSystem() {
        return capitalAssetSystem;
    }

    public void setCapitalAssetSystem(CapitalAssetSystem capitalAssetSystem) {
        this.capitalAssetSystem = capitalAssetSystem;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }

}
