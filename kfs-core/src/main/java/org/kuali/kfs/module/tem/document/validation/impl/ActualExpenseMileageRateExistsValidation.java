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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.MileageRateService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Determines that an actual expense - if it has a mileage rate expense object code - has a valid mileage rate for the document-effective
 * date of the expense
 */
public class ActualExpenseMileageRateExistsValidation extends GenericValidation {
    protected ActualExpense actualExpenseForValidation;
    protected MileageRateService mileageRateService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        if (!StringUtils.isBlank(getActualExpenseForValidation().getExpenseTypeCode())) {
            if (ObjectUtils.isNull(getActualExpenseForValidation().getExpenseType()) || !StringUtils.equals(getActualExpenseForValidation().getExpenseType().getCode(), getActualExpenseForValidation().getExpenseTypeCode())) {
                getActualExpenseForValidation().refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE);
            }
            if (getActualExpenseForValidation().isMileage()) {
                final TravelDocument doc = (TravelDocument)event.getDocument();
                final java.sql.Date effectiveDate = doc.getEffectiveDateForMileageRate(getActualExpenseForValidation());
                final MileageRate rate = getMileageRateService().findMileageRateByExpenseTypeCodeAndDate(getActualExpenseForValidation().getExpenseTypeCode(), effectiveDate);
                if (ObjectUtils.isNull(rate)) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.EXPENSE_TYPE_CODE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MISSING_MILEAGE_RATE, new String[] { getActualExpenseForValidation().getExpenseType().getName() });
                    return false;
                }
            }
        }
        return true;
    }

    public MileageRateService getMileageRateService() {
        return mileageRateService;
    }

    public void setMileageRateService(MileageRateService mileageRateService) {
        this.mileageRateService = mileageRateService;
    }

    public ActualExpense getActualExpenseForValidation() {
        return actualExpenseForValidation;
    }

    public void setActualExpenseForValidation(ActualExpense actualExpenseForValidation) {
        this.actualExpenseForValidation = actualExpenseForValidation;
    }
}
