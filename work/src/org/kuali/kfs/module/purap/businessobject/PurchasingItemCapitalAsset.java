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
package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

public class PurchasingItemCapitalAsset extends PersistableBusinessObjectBase {

    private Long capitalAssetNumber;
    
    /**
     * Default constructor
     */
    public PurchasingItemCapitalAsset() {        
    }
    
    /**
     * Constructs a PurchasingItemCapitalAsset.
     * @param capitalAssetNumber
     */
    public PurchasingItemCapitalAsset(Long capitalAssetNumber){
        this.capitalAssetNumber = capitalAssetNumber;
    }
        
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }
}
