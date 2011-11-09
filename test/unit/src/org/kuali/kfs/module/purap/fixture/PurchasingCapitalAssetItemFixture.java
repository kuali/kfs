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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;

public enum PurchasingCapitalAssetItemFixture {
  
    ASSET_ITEM_BASIC ( 
            new Integer(1), //itemIdentifier
            "CO", //capitalAssetTransactionTypeCode
            null  //capitalAssetSystemIdentifier
            ),                       
            ;
    
    private Integer itemIdentifier;
    private String capitalAssetTransactionTypeCode;
    private Integer capitalAssetSystemIdentifier;
    
    private PurchasingCapitalAssetItemFixture (Integer itemIdentifier, String capitalAssetTransactionTypeCode, Integer capitalAssetSystemIdentifier) {
        this.itemIdentifier = itemIdentifier;
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }
    
    public PurchasingCapitalAssetItem createPurchasingCapitalAssetItem(Class clazz) {
        PurchasingCapitalAssetItem assetItem = null;
        try {
            assetItem = (PurchasingCapitalAssetItem) clazz.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("asset item creation failed. class = " + clazz);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("asset item creation failed. class = " + clazz);
        }

        assetItem.setItemIdentifier(itemIdentifier);
        assetItem.setCapitalAssetTransactionTypeCode(capitalAssetTransactionTypeCode);
        assetItem.setCapitalAssetSystemIdentifier(capitalAssetSystemIdentifier);
        return assetItem;
    }
}
