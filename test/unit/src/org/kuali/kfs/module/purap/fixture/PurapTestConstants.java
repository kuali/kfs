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
package org.kuali.module.purap.fixtures;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.RequisitionDocument;

class PurapTestConstants {
       
    static final Integer FY_2007 = new Integer("2007");
        
    @SuppressWarnings("deprecation")
    static class BeginEndDates {
        static final Date FIRST_DATE = new Date(108,1,1); //2008-01-01
        static final Date LAST_DATE = new Date(109,1,1);  //2009-01-01
        static RequisitionDocument REQ = new RequisitionDocument();
        static PurchaseOrderDocument PO = new PurchaseOrderDocument();
    }
        
    static class RecurringPaymentTypes {
        static final String FIXD = "FIXD";
        static final String FVAR = "FVAR";
        static final String VARV = "VARV";
    }
    
    static class AmountsLimits {
        static final KualiDecimal LARGE_NEGATIVE_AMOUNT = new KualiDecimal(-1024);
        static final KualiDecimal SMALL_NEGATIVE_AMOUNT = new KualiDecimal(-32);
        static final KualiDecimal ZERO = new KualiDecimal(0);
        static final KualiDecimal SMALL_POSITIVE_AMOUNT = new KualiDecimal(32);
        static final KualiDecimal LARGE_POSITIVE_AMOUNT = new KualiDecimal(1024);
    }
    
    static class ItemsAccounts {
        static PurchaseOrderItem PO_ITEM = new PurchaseOrderItem();
        static final String ITEM_DESC = "Test item description";
        static final String ITEM_UOM = "EACH";
        static PurchaseOrderAccount PO_ACCOUNT =  new PurchaseOrderAccount();
        static final String ACCOUNT_NUMBER = "9999999";
        static final BigDecimal UNIT_PRICE = new KualiDecimal(32).bigDecimalValue();
    }
}
