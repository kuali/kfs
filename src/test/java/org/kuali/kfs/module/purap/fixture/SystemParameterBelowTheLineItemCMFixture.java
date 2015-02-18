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

import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;

public enum SystemParameterBelowTheLineItemCMFixture {
    
    CM_VALID_BELOW_LINE_ITEMS(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_VALID_RSTO_ITEM, CreditMemoItemFixture.CM_VALID_MSCR_ITEM} // creditMemoItemMultiFixtures
            ),
            
    CM_INVALID_BELOW_LINE_ITEMS(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_VALID_MISC_ITEM} // creditMemoItemMultiFixtures
            ),
            
    CM_VALID_NEGATIVE_BELOW_LINE_ITEMS(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_VALID_MSCR_ITEM} // creditMemoItemMultiFixtures
            ),
            
    CM_INVALID_NEGATIVE_BELOW_LINE_ITEMS(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_NEGATIVE_FREIGHT_ITEM,
                                         CreditMemoItemFixture.CM_NEGATIVE_SHIPPING_AND_HANDLING_ITEM,
                                         CreditMemoItemFixture.CM_NEGATIVE_MIN_ORDER_ITEM,
                                         CreditMemoItemFixture.CM_NEGATIVE_FED_GROSS_CODE_ITEM,
                                         CreditMemoItemFixture.CM_NEGATIVE_STATE_GROSS_CODE_ITEM} // creditMemoItemMultiFixtures
            ),   
            
    CM_VALID_POSITIVE_BELOW_LINE_ITEMS(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_POSITIVE_MSCR_ITEM} // creditMemoItemMultiFixtures
            ),    
            
    CM_INVALID_POSITIVE_BELOW_LINE_ITEMS(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_POSITIVE_DISC_ITEM,
                                         CreditMemoItemFixture.CM_POSITIVE_ORDS_ITEM,
                                         CreditMemoItemFixture.CM_POSITIVE_TRDI_ITEM,
                                         CreditMemoItemFixture.CM_POSITIVE_FDTX_ITEM,
                                         CreditMemoItemFixture.CM_POSITIVE_STTX_ITEM} // creditMemoItemMultiFixtures
            ),  
            
    CM_VALID_ZERO_BELOW_LINE_ITEMS(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_ZERO_RSTO_ITEM,
                                         CreditMemoItemFixture.CM_ZERO_MSCR_ITEM} // creditMemoItemMultiFixtures
                    ),    
                    
    CM_INVALID_ZERO_BELOW_LINE_ITEMS(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_ZERO_FED_GROSS_CODE_ITEM,
                                         CreditMemoItemFixture.CM_ZERO_STATE_GROSS_CODE_ITEM,
                                         CreditMemoItemFixture.CM_ZERO_ORDS_ITEM,
                                         CreditMemoItemFixture.CM_ZERO_TRDI_ITEM,
                                         CreditMemoItemFixture.CM_ZERO_FDTX_ITEM,
                                         CreditMemoItemFixture.CM_ZERO_STTX_ITEM} // creditMemoItemMultiFixtures
            ),      
            
    CM_WITH_BELOW_LINE_ITEMS_WITHOUT_DESCRIPTION(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_WITH_FREIGHT_ITEM_NO_DESC} // creditMemoItemMultiFixtures
            ),
            
    CM_WITH_BELOW_LINE_ITEMS_WITH_DESCRIPTION(
            new CreditMemoItemFixture[] {CreditMemoItemFixture.CM_WITH_MISC_CREDIT_ITEM_WITH_DESC,
                                         CreditMemoItemFixture.CM_WITH_MISC_ITEM_WITH_DESC} // creditMemoItemMultiFixtures
            ),            
            ;   
    private CreditMemoItemFixture[] creditMemoItemFixtures;
    
    
    private SystemParameterBelowTheLineItemCMFixture(
            CreditMemoItemFixture[] creditMemoItemFixtures) {
        this.creditMemoItemFixtures = creditMemoItemFixtures;
    }
    
    public VendorCreditMemoDocument createCreditMemoDocument() {
        VendorCreditMemoDocument doc = CreditMemoDocumentFixture.CM_ONLY_REQUIRED_FIELDS.createCreditMemoDocument();
        //Removes all the existing item from doc, we'll add the appropriate items later.
        doc.getItems().clear();         
        for (CreditMemoItemFixture creditMemoItemFixture : creditMemoItemFixtures) { 
            creditMemoItemFixture.addTo(doc);
        }
        
        return doc;
    }
    
}
