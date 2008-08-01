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
package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.module.purap.document.PurchasingDocument;

/**
 * Item Type Business Object. Defines various types of items.
 */
public class RequisitionCapitalAssetItem extends PurchasingCapitalAssetItemBase {
    
    private Integer purapDocumentIdentifier;        
    
    /**
     * Default constructor.
     */
    public RequisitionCapitalAssetItem(){
        super();
    }
    
    public RequisitionCapitalAssetItem(PurchasingDocument pd) {
        super(pd);
        setPurapDocumentIdentifier(pd.getPurapDocumentIdentifier());        
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }
    
}
