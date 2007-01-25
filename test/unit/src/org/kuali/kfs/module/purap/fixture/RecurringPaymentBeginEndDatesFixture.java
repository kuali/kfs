/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/unit/src/org/kuali/kfs/module/purap/fixture/RecurringPaymentBeginEndDatesFixture.java,v $
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
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.document.PurchasingDocument;

import org.kuali.module.purap.fixtures.PurapTestConstants;
import org.kuali.module.purap.fixtures.PurapTestConstants.BeginEndDates;

public enum RecurringPaymentBeginEndDatesFixture {
    
    REQ_RIGHT_ORDER( BeginEndDates.REQ, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE ),
    REQ_WRONG_ORDER( BeginEndDates.REQ, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE ),
    REQ_SEQUENTIAL_NEXT_FY( BeginEndDates.REQ, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, 
            PurapTestConstants.FY_2007 ),
    REQ_NON_SEQUENTIAL_NEXT_FY( BeginEndDates.REQ, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE,
            PurapTestConstants.FY_2007 ),
    PO_RIGHT_ORDER( BeginEndDates.PO, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE ),
    PO_WRONG_ORDER( BeginEndDates.PO, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE ),
    PO_SEQUENTIAL_NEXT_FY( BeginEndDates.PO, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, 
            PurapTestConstants.FY_2007 ),
    PO_NON_SEQUENTIAL_NEXT_FY( BeginEndDates.PO, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE,
            PurapTestConstants.FY_2007 );
    
    PurchasingDocument document;
    Date beginDate;
    Date endDate;
    Integer currentFiscalYear;
    
    private RecurringPaymentBeginEndDatesFixture( PurchasingDocument document, Date date1, Date date2 ){
        this.document = document;
        this.beginDate = date1;
        this.endDate = date2;
    }
    
    private RecurringPaymentBeginEndDatesFixture( PurchasingDocument document, Date date1, Date date2, 
            Integer currentFY ){
        this.document = document;
        this.beginDate = date1;
        this.endDate = date2;
        this.currentFiscalYear = currentFY;
    }

    public PurchasingDocument populateDocument() {
        this.document.setPurchaseOrderBeginDate( beginDate ); 
        this.document.setPurchaseOrderEndDate( endDate );
        if( ObjectUtils.isNotNull( this.currentFiscalYear ) ) {
            this.document.setPostingYear( new Integer( this.currentFiscalYear + 1 ) );
        }
        return document;
    }

}
