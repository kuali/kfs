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
