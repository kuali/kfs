/*
 * Copyright 2007 The Kuali Foundation
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

import java.sql.Date;

import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.BeginEndDates;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.RecurringPaymentTypes;
import org.kuali.rice.krad.util.ObjectUtils;

public enum RecurringPaymentBeginEndDatesFixture {

    REQ_RIGHT_ORDER(BeginEndDates.REQ, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, RecurringPaymentTypes.FIXD), REQ_WRONG_ORDER(BeginEndDates.REQ, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE, RecurringPaymentTypes.FIXD), REQ_SEQUENTIAL_NEXT_FY(BeginEndDates.REQ, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, PurapTestConstants.FY_2007, RecurringPaymentTypes.FIXD), REQ_NON_SEQUENTIAL_NEXT_FY(BeginEndDates.REQ, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE, PurapTestConstants.FY_2007, RecurringPaymentTypes.FIXD), PO_RIGHT_ORDER(BeginEndDates.PO, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, RecurringPaymentTypes.FIXD), PO_WRONG_ORDER(BeginEndDates.PO, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE, RecurringPaymentTypes.FIXD), PO_SEQUENTIAL_NEXT_FY(BeginEndDates.PO, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, PurapTestConstants.FY_2007, RecurringPaymentTypes.FIXD), PO_NON_SEQUENTIAL_NEXT_FY(BeginEndDates.PO, BeginEndDates.LAST_DATE,
            BeginEndDates.FIRST_DATE, PurapTestConstants.FY_2007, RecurringPaymentTypes.FIXD);

    PurchasingDocument document;
    Date beginDate;
    Date endDate;
    Integer currentFiscalYear;
    String recurringPaymentType;

    private RecurringPaymentBeginEndDatesFixture(PurchasingDocument document, Date date1, Date date2, String recurringPaymentType) {
        this.document = document;
        this.beginDate = date1;
        this.endDate = date2;
        this.recurringPaymentType = recurringPaymentType;
    }

    private RecurringPaymentBeginEndDatesFixture(PurchasingDocument document, Date date1, Date date2, Integer currentFY, String recurringPaymentType) {
        this.document = document;
        this.beginDate = date1;
        this.endDate = date2;
        this.currentFiscalYear = currentFY;
        this.recurringPaymentType = recurringPaymentType;
    }

    public PurchasingDocument populateDocument() {
        this.document.setPurchaseOrderBeginDate(beginDate);
        this.document.setPurchaseOrderEndDate(endDate);
        this.document.setRecurringPaymentTypeCode(recurringPaymentType);
        if (ObjectUtils.isNotNull(this.currentFiscalYear)) {
            this.document.setPostingYear(new Integer(this.currentFiscalYear + 1));
        }
        return document;
    }

}
