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

import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;

public class RequisitionCapitalAssetItem extends PurchasingCapitalAssetItemBase {
    
    private Integer purapDocumentIdentifier;        
    
    public RequisitionCapitalAssetItem(){
        super();
        this.setPurchasingCapitalAssetSystem(new RequisitionCapitalAssetSystem());
    }
    
    public RequisitionCapitalAssetItem(PurchasingDocument pd) {
        super(pd);
        setPurapDocumentIdentifier(pd.getPurapDocumentIdentifier());
        this.setPurchasingCapitalAssetSystem(new RequisitionCapitalAssetSystem());
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    @Override
    public void setPurchasingDocument(PurchasingDocument pd){
        super.setPurchasingDocument(pd);

        RequisitionDocument req = (RequisitionDocument)pd;
        if(req != null){
            setPurapDocumentIdentifier( req.getPurapDocumentIdentifier() );
        }
    }
    
    @Override
    public ItemCapitalAsset setupNewPurchasingItemCapitalAssetLine() {
        ItemCapitalAsset asset = new RequisitionItemCapitalAsset();
        return asset;
    }

}
