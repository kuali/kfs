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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchaseOrderCapitalAssetItem extends PurchasingCapitalAssetItemBase {

    private String documentNumber;        
    
    public PurchaseOrderCapitalAssetItem(){
        super();
    }
    
    public PurchaseOrderCapitalAssetItem(RequisitionCapitalAssetItem reqAssetItem, Integer itemIdentifier) {
        this.setItemIdentifier(itemIdentifier);
        this.setCapitalAssetTransactionTypeCode(reqAssetItem.getCapitalAssetTransactionTypeCode());
        if (ObjectUtils.isNotNull(reqAssetItem.getPurchasingCapitalAssetSystem())) {
            this.setPurchasingCapitalAssetSystem(new PurchaseOrderCapitalAssetSystem((RequisitionCapitalAssetSystem)reqAssetItem.getPurchasingCapitalAssetSystem()));
        }
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    
    public void setPurchasingDocument(PurchasingDocument pd){
        super.setPurchasingDocument(pd);
        
        PurchaseOrderDocument po = (PurchaseOrderDocument)pd;
        if(po != null){
            setDocumentNumber( po.getDocumentNumber() );
        }
    }

    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if( this.getCapitalAssetItemIdentifier() != null) {
            m.put("capitalAssetItemIdentifier", this.getCapitalAssetItemIdentifier().toString());
        }
        if( this.getItemIdentifier() != null ) {
            m.put("itemIdentifier", this.getItemIdentifier().toString());
        }
        if( this.documentNumber != null) {
            m.put("documentNumber", this.documentNumber);
        }
        return m;
    }

    @Override
    public ItemCapitalAsset setupNewPurchasingItemCapitalAssetLine() {
        ItemCapitalAsset asset = new PurchaseOrderItemCapitalAsset();
        return asset;
    }
}
