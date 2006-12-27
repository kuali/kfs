/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/unit/src/org/kuali/kfs/module/purap/fixture/PurapTestConstants.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License")
;
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

import java.sql.Date;

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
}
