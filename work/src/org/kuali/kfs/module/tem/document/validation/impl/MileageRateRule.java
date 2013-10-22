/*
 * Copyright 2013 The Kuali Foundation.
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
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the mileage rate maintenance document
 */
public class MileageRateRule extends MaintenanceDocumentRuleBase {

    /**
     * Performs extra checks on the rate and expense type but only ever succeeds
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        super.processCustomSaveDocumentBusinessRules(document);

        final MileageRate mileageRate = (MileageRate)document.getNewMaintainableObject().getBusinessObject();
        checkRate(mileageRate);
        checkExpenseType(mileageRate);

        return true;
    }

    /**
     * Performs extra checks on the rate and expense type, which will help determine the result
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        final MileageRate mileageRate = (MileageRate)document.getNewMaintainableObject().getBusinessObject();
        result &= checkRate(mileageRate);
        result &= checkExpenseType(mileageRate);

        return result;
    }

    /**
     * Checks that the rate on the proposed record is positive
     * @param mileageRate the mileage rate to check
     * @return true if the rate rules were passed, false if errors were found
     */
    protected boolean checkRate(MileageRate mileageRate) {
        boolean success = true;
        if (mileageRate.getRate() != null) {
            if (!mileageRate.getRate().isPositive()) {
                putFieldError(TemPropertyConstants.RATE, TemKeyConstants.ERROR_DOCUMENT_MILEAGE_RATE_INVALID_RATE);
                success = false;
            }
        }
        return success;
    }

    /**
     * Checks that the expense type on the proposed record has mileage as the metacategory
     * @param mileageRate the mileage rate to check
     * @return true if the expense type rules were passed, false if errors were found
     */
    protected boolean checkExpenseType(MileageRate mileageRate) {
        boolean success = true;
        if (!StringUtils.isBlank(mileageRate.getExpenseTypeCode())) {
            mileageRate.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE);
            if (!ObjectUtils.isNull(mileageRate.getExpenseType()) && !TemConstants.ExpenseTypeMetaCategory.MILEAGE.getCode().equals(mileageRate.getExpenseType().getExpenseTypeMetaCategoryCode())) {
                putFieldError(TemPropertyConstants.EXPENSE_TYPE_CODE, TemKeyConstants.ERROR_DOCUMENT_MILEAGE_RATE_INVALID_EXPENSE_TYPE, new String[] { mileageRate.getExpenseTypeCode() });
                success = false;
            }
        }
        return success;
    }

}
