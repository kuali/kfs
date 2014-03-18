/*
 * Copyright 2014 The Kuali Foundation.
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
