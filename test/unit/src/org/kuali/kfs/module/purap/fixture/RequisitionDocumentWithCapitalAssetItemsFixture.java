/*
 * Copyright 2007-2008 The Kuali Foundation
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

import org.kuali.kfs.module.purap.document.RequisitionDocument;

public enum RequisitionDocumentWithCapitalAssetItemsFixture {

    REQ_VALID_IND_NEW_CAPITAL_ASSET_ITEM(
            "IND", // capitalAssetSystemTypeCode
            "NEW", // capitalAssetSystemStateCode
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_VALID_CAPITAL_ASSET} // requisitionItemMultiFixtures
    ),  
    REQ_VALID_ONE_NEW_CAPITAL_ASSET_ITEM(
            "ONE", // capitalAssetSystemTypeCode
            "NEW", // capitalAssetSystemStateCode
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_VALID_CAPITAL_ASSET} // requisitionItemMultiFixtures
    ),  
    ;
    
    private String capitalAssetSystemTypeCode;
    private String capitalAssetSystemStateCode;
    private RequisitionItemFixture[] requisitionItemFixtures;
    
    
    private RequisitionDocumentWithCapitalAssetItemsFixture(
            String capitalAssetSystemTypeCode,
            String capitalAssetSystemStateCode,
            RequisitionItemFixture[] requisitionItemFixtures) {
        this.capitalAssetSystemTypeCode = capitalAssetSystemTypeCode;
        this.capitalAssetSystemStateCode = capitalAssetSystemStateCode;
        this.requisitionItemFixtures = requisitionItemFixtures;
    }
    
    public RequisitionDocument createRequisitionDocument() {
        RequisitionDocument doc = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        doc.getDocumentHeader().setDocumentDescription("Created from RequisitionDocumentWithCapitalAssetItemsFixture");
        doc.setCapitalAssetSystemTypeCode(capitalAssetSystemTypeCode);
        doc.setCapitalAssetSystemStateCode(capitalAssetSystemStateCode);
        //Removes all the existing item from doc, we'll add the appropriate items later.
        doc.getItems().clear();
        for (RequisitionItemFixture requisitionItemFixture : requisitionItemFixtures) { 
            requisitionItemFixture.addTo(doc);
        }
        
        return doc;
    }
}
