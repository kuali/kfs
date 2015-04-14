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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Date;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingPaymentInfoValidation extends GenericValidation {

    DateTimeService dateTimeService;
    UniversityDateService universityDateService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingDocument purDocument = (PurchasingDocument)event.getDocument();
        
        GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.PAYMENT_INFO_ERRORS);
        valid &= checkBeginDateBeforeEndDate(purDocument);
        if (valid && (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) || ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate()))) {
            if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNull(purDocument.getPurchaseOrderEndDate())) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE);
                valid &= false;
            }
            else {
                if (ObjectUtils.isNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE);
                    valid &= false;
                }
            }
        }
        if (valid && ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
            if (ObjectUtils.isNull(purDocument.getRecurringPaymentTypeCode())) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.RECURRING_PAYMENT_TYPE_CODE, PurapKeyConstants.ERROR_RECURRING_DATE_NO_TYPE);

                valid &= false;
            }
        }
        else if (valid && ObjectUtils.isNotNull(purDocument.getRecurringPaymentTypeCode())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_RECURRING_TYPE_NO_DATE);
            valid &= false;
        }

        //checks for when FY is set to next FY
        if (purDocument.isPostingYearNext()) {
            Integer currentFY = universityDateService.getCurrentFiscalYear();
            Date closingDate = universityDateService.getLastDateOfFiscalYear(currentFY);

            //if recurring payment begin dates entered, begin date must be > closing date
            if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) &&
                   (purDocument.getPurchaseOrderBeginDate().before(closingDate) ||
                    purDocument.getPurchaseOrderBeginDate().equals(closingDate))) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_NEXT_FY_BEGIN_DATE_INVALID);
                valid &= false;
            }
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(PurapConstants.PAYMENT_INFO_ERRORS);
        return valid;
    }

    /**
     * Implementation of the rule that if a document has a recurring payment begin date and end date, the begin date should come
     * before the end date. We needed to play around with this order if the fiscal year is the next fiscal year, since we
     * were dealing just with month and day, but we don't need to do that here; we're dealing with the whole Date object.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the begin date is not before the end date.
     */
    protected boolean checkBeginDateBeforeEndDate(PurchasingDocument purDocument) {
        boolean valid = true;

        Date beginDate = purDocument.getPurchaseOrderBeginDate();
        Date endDate = purDocument.getPurchaseOrderEndDate();
        if (ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if (dateTimeService.dateDiff(beginDate, endDate, false) <= 0) {
                valid &= false;
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END);
            }
        }

        return valid;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

}
