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

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;

public class PurchaseOrderCapitalAssetItem extends PurchasingCapitalAssetItemBase {

    private String documentNumber;        
    
    public PurchaseOrderCapitalAssetItem(){
        super();
    }
    
    public PurchaseOrderCapitalAssetItem(PurchasingDocument pd){
        super(pd);
        setDocumentNumber(documentNumber);
        this.setPurchasingCapitalAssetSystem(new PurchaseOrderCapitalAssetSystem());
    }
    
    //FIXME (from hjs) change parameter to be RequisitionCapitalAssetItem
    public PurchaseOrderCapitalAssetItem (PurchasingCapitalAssetItem reqAssetItem) {
        //this.setCapitalAssetItemIdentifier(reqAssetItem.getCapitalAssetItemIdentifier());
        //this.setItemIdentifier(reqAssetItem.getItemIdentifier());
        this.setCapitalAssetTransactionTypeCode(reqAssetItem.getCapitalAssetTransactionTypeCode());
        //this.setCapitalAssetSystemIdentifier(reqAssetItem.getCapitalAssetSystemIdentifier());
        this.setCapitalAssetTransactionType(reqAssetItem.getCapitalAssetTransactionType());
        //this.setPurchasingCapitalAssetSystem(reqAssetItem.getPurchasingCapitalAssetSystem());
        
        //FIXME (from hjs) copy the system
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Override
    public void setPurchasingDocument(PurchasingDocument pd){
        super.setPurchasingDocument(pd);
        
        PurchaseOrderDocument po = (PurchaseOrderDocument)pd;
        if(po != null){
            setDocumentNumber( po.getDocumentNumber() );
        }
    }

    @Override
    protected LinkedHashMap toStringMapper() {
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

}
