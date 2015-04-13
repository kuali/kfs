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

import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum CreditMemoItemFixture {

    CM_QTY_UNRESTRICTED_ITEM_1(
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1)  // poInvoicedTotalQuantity
    ),

    CM_ITEM_NO_APO(
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),      

    /*
     * CM_QTY_APO_ITEM_1 ( false, // itemRestrictedIndicator PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture new
     * CreditMemoAccountingLineFixture[] {CreditMemoAccountingLineFixture.APO_CM_ACCOUNT_1} // creditMemoAccountMultiFixtures ),
     * CM_SERVICE_APO_ITEM_1 ( false, // itemRestrictedIndicator PurApItemFixture.APO_SERVICE_ITEM_1, // purApItemFixture new
     * RequisitionAccountingLineFixture[] {CreditMemoAccountingLineFixture.APO_CM_ACCOUNT_2,
     * CreditMemoAccountingLineFixture.APO_CM_ACCOUNT_3} // creditMemoAccountMultiFixtures ), CM_FREIGHT_ITEM_1 ( false, //
     * itemRestrictedIndicator PurApItemFixture.APO_FREIGHT_ITEM_1, // purApItemFixture new CreditMemoAccountingLineFixture[]
     * {RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_4} // creditMemoAccountMultiFixtures ),
     */

    CM_VALID_RSTO_ITEM(
            PurApItemFixture.NEGATIVE_RSTO_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),    
    CM_VALID_MSCR_ITEM(
            PurApItemFixture.NEGATIVE_MSCR_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),  
    CM_VALID_MISC_ITEM(
            PurApItemFixture.VALID_MISC_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),  
    CM_NEGATIVE_DISC_ITEM(
            PurApItemFixture.NEGATIVE_DISC_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_ORDS_ITEM(
            PurApItemFixture.NEGATIVE_ORDS_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_TRDI_ITEM(
            PurApItemFixture.NEGATIVE_TRDI_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_FDTX_ITEM(
            PurApItemFixture.NEGATIVE_FDTX_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_STTX_ITEM(
            PurApItemFixture.NEGATIVE_STTX_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_MISC_ITEM(
            PurApItemFixture.NEGATIVE_MISC_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_FREIGHT_ITEM(
            PurApItemFixture.NEGATIVE_FREIGHT_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_SHIPPING_AND_HANDLING_ITEM(
            PurApItemFixture.NEGATIVE_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_FED_GROSS_CODE_ITEM(
            PurApItemFixture.NEGATIVE_FED_GROSS_CODE_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_STATE_GROSS_CODE_ITEM(
            PurApItemFixture.NEGATIVE_STATE_GROSS_CODE_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_NEGATIVE_MIN_ORDER_ITEM(
            PurApItemFixture.NEGATIVE_MIN_ORDER_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_DISC_ITEM(
            PurApItemFixture.POSITIVE_DISC_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_ORDS_ITEM(
            PurApItemFixture.POSITIVE_ORDS_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_TRDI_ITEM(
            PurApItemFixture.POSITIVE_TRDI_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_FDTX_ITEM(
            PurApItemFixture.POSITIVE_FDTX_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_STTX_ITEM(
            PurApItemFixture.POSITIVE_STTX_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_FREIGHT_ITEM(
            PurApItemFixture.VALID_FREIGHT_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_SHIPPING_AND_HANDLING_ITEM(
            PurApItemFixture.VALID_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_FED_GROSS_CODE_ITEM(
            PurApItemFixture.VALID_FED_GROSS_CODE_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_STATE_GROSS_CODE_ITEM(
            PurApItemFixture.VALID_STATE_GROSS_CODE_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_MIN_ORDER_ITEM(
            PurApItemFixture.NEGATIVE_MIN_ORDER_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_MSCR_ITEM(
            PurApItemFixture.POSITIVE_MSCR_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_POSITIVE_RSTO_ITEM(
            PurApItemFixture.POSITIVE_RSTO_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),    
    CM_ZERO_FREIGHT_ITEM(
            PurApItemFixture.ZERO_FREIGHT_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_SHIPPING_AND_HANDLING_ITEM(
            PurApItemFixture.ZERO_SHIPPING_AND_HANDLING_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_FED_GROSS_CODE_ITEM(
            PurApItemFixture.ZERO_FED_GROSS_CODE_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_STATE_GROSS_CODE_ITEM(
            PurApItemFixture.ZERO_STATE_GROSS_CODE_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_MIN_ORDER_ITEM(
            PurApItemFixture.ZERO_MIN_ORDER_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),     
    CM_ZERO_MSCR_ITEM(
            PurApItemFixture.ZERO_MSCR_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_RSTO_ITEM(
            PurApItemFixture.ZERO_RSTO_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),   
    CM_ZERO_DISC_ITEM(
            PurApItemFixture.ZERO_DISC_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_ORDS_ITEM(
            PurApItemFixture.ZERO_ORDS_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_TRDI_ITEM(
            PurApItemFixture.ZERO_TRDI_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_FDTX_ITEM(
            PurApItemFixture.ZERO_FDTX_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_STTX_ITEM(
            PurApItemFixture.ZERO_STTX_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_ZERO_MISC_ITEM(
            PurApItemFixture.ZERO_MISC_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ), 
    CM_WITH_FREIGHT_ITEM_NO_DESC(
            PurApItemFixture.FREIGHT_ITEM_NO_DESC, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),
    CM_WITH_MISC_CREDIT_ITEM_WITH_DESC(
            PurApItemFixture.VALID_MISC_CREDIT_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),
    CM_WITH_MISC_ITEM_WITH_DESC(
            PurApItemFixture.VALID_MISC_ITEM, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 }, // creditMemoAccountMultiFixtures
            new KualiDecimal(1) // poInvoicedTotalQuantity
    ),
    ;


    //private boolean itemRestrictedIndicator;
    private PurApItemFixture purApItemFixture;
    private CreditMemoAccountingLineFixture[] creditMemoAccountingLineFixtures;
    private KualiDecimal poInvoicedTotalQuantity;

    private CreditMemoItemFixture(PurApItemFixture purApItemFixture, CreditMemoAccountingLineFixture[] creditMemoAccountingLineFixtures,
            KualiDecimal poInvoicedTotalQuantity) {
        this.purApItemFixture = purApItemFixture;
        this.creditMemoAccountingLineFixtures = creditMemoAccountingLineFixtures;
        this.poInvoicedTotalQuantity = poInvoicedTotalQuantity;
    }

    public void addTo(VendorCreditMemoDocument creditMemoDocument) {
        CreditMemoItem item = null;
        item = (CreditMemoItem) this.createCreditMemoItem();
        creditMemoDocument.addItem(item);
        // iterate over the accounts
        for (CreditMemoAccountingLineFixture creditMemoAccountMultiFixture : creditMemoAccountingLineFixtures) {
            creditMemoAccountMultiFixture.addTo(item);
        }
    }

   public AccountsPayableItem createCreditMemoItem() {
        CreditMemoItem item = (CreditMemoItem) purApItemFixture.createPurApItem(CreditMemoItem.class);
        return item;
    }
}
