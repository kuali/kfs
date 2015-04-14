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
