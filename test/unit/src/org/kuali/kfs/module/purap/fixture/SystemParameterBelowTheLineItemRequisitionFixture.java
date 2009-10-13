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

public enum SystemParameterBelowTheLineItemRequisitionFixture {

    REQ_VALID_BELOW_LINE_ITEMS(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_VALID_FREIGHT_ITEM, RequisitionItemFixture.REQ_VALID_SHIPPING_AND_HANDLING_ITEM } // requisitionItemMultiFixtures
    ),
    
    REQ_INVALID_BELOW_LINE_ITEMS(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_WITH_MISC_CREDIT_ITEM } // requisitionItemMultiFixtures
    ),
    REQ_WITH_NEGATIVE_BELOW_LINE_ITEMS(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_WITH_NEGATIVE_FREIGHT_ITEM, RequisitionItemFixture.REQ_WITH_NEGATIVE_SHIPPING_AND_HANDLING_ITEM } // requisitionItemMultiFixtures
    ),    
    REQ_WITH_ZERO_BELOW_LINE_ITEMS(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_WITH_ZERO_FREIGHT_ITEM, RequisitionItemFixture.REQ_WITH_ZERO_SHIPPING_AND_HANDLING_ITEM } // requisitionItemMultiFixtures
    ),    
    REQ_WITH_BELOW_LINE_ITEMS_WITHOUT_DESCRIPTION(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_WITH_MISC_ITEM_NO_DESC } // requisitionItemMultiFixtures
    ),
    
    REQ_WITH_BELOW_LINE_ITEMS_WITH_DESCRIPTION(
            new RequisitionItemFixture[] { RequisitionItemFixture.REQ_VALID_FREIGHT_ITEM, RequisitionItemFixture.REQ_VALID_SHIPPING_AND_HANDLING_ITEM } // requisitionItemMultiFixtures
    )    
    ;
    private RequisitionItemFixture[] requisitionItemFixtures;
    
    
    private SystemParameterBelowTheLineItemRequisitionFixture(
            RequisitionItemFixture[] requisitionItemFixtures) {
        this.requisitionItemFixtures = requisitionItemFixtures;
    }
    
    public RequisitionDocument createRequisitionDocument() {
        RequisitionDocument doc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        //Removes all the existing item from doc, we'll add the appropriate items later.
        doc.getItems().clear();
        for (RequisitionItemFixture requisitionItemFixture : requisitionItemFixtures) { 
            requisitionItemFixture.addTo(doc);
        }
        
        return doc;
    }
}
