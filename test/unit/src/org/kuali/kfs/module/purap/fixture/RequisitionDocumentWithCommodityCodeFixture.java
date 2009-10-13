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

public enum RequisitionDocumentWithCommodityCodeFixture {

    REQ_VALID_ACTIVE_COMMODITY_CODE(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_NO_APO_BASIC_ACTIVE_COMMODITY_CODE} // requisitionItemMultiFixtures
    ),  
    REQ_VALID_INACTIVE_COMMODITY_CODE(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_NO_APO_BASIC_INACTIVE_COMMODITY_CODE} // requisitionItemMultiFixtures
    ),   
    REQ_NON_EXISTENCE_COMMODITY_CODE(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_NO_APO_NON_EXISTENCE_COMMODITY_CODE} // requisitionItemMultiFixtures
    ),     
    REQ_APO_INACTIVE_COMMODITY_CODE(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_APO_BASIC_INACTIVE_COMMODITY_CODE} // requisitionItemMultiFixtures
    ), 
    REQ_APO_COMMODITY_CODE_WITH_SENSITIVE_DATA(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_ITEM_APO_COMMODITY_CODE_WITH_SENSITIVE_DATA} // requisitionItemMultiFixtures
    ), 
    ;
    
    private RequisitionItemFixture[] requisitionItemFixtures;
    
    
    private RequisitionDocumentWithCommodityCodeFixture(
            RequisitionItemFixture[] requisitionItemFixtures) {
        this.requisitionItemFixtures = requisitionItemFixtures;
    }
    
    public RequisitionDocument createRequisitionDocument() {
        RequisitionDocument doc = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        //Removes all the existing item from doc, we'll add the appropriate items later.
        doc.getItems().clear();
        for (RequisitionItemFixture requisitionItemFixture : requisitionItemFixtures) { 
            requisitionItemFixture.addTo(doc);
        }
        
        return doc;
    }
}
