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
