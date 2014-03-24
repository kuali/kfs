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
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class TripTypeRule extends MaintenanceDocumentRuleBase {
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        super.processCustomSaveDocumentBusinessRules(document);

        final TripType tripType = (TripType)document.getNewMaintainableObject().getBusinessObject();
        checkBalanceType(tripType);
        checkObjectCode(tripType);
        checkAutoTravelReimbursementLimit(tripType);

        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        final TripType tripType = (TripType)document.getNewMaintainableObject().getBusinessObject();
        result &= checkBalanceType(tripType);
        result &= checkObjectCode(tripType);
        result &= checkAutoTravelReimbursementLimit(tripType);

        return result;
    }

    protected boolean checkBalanceType(TripType tripType) {
        if (tripType.isGenerateEncumbrance() && StringUtils.isEmpty(tripType.getEncumbranceBalanceType())) {
            putFieldError(TemPropertyConstants.TRIP_TYPE_ENCUMBRANCE_BALANCE_TYPE, TemKeyConstants.ERROR_TRIP_TYPE_ENCUMBRANCE_BALANCE_TYPE);
            return false;
        }
        return true;
    }

    protected boolean checkObjectCode(TripType tripType) {
        if (tripType.isGenerateEncumbrance() && StringUtils.isEmpty(tripType.getEncumbranceBalanceType())) {
            putFieldError(TemPropertyConstants.TRIP_TYPE_ENCUMBRANCE_OBJECT_CODE, TemKeyConstants.ERROR_TRIP_TYPE_ENCUMBRANCE_OBJECT_CODE);
            return false;
        }
        return true;
    }

    protected boolean checkAutoTravelReimbursementLimit(TripType tripType) {
        if (ObjectUtils.isNull(tripType.getAutoTravelReimbursementLimit())) {
            tripType.setAutoTravelReimbursementLimit(KualiDecimal.ZERO);
        }
        else if (tripType.getAutoTravelReimbursementLimit().isLessThan(KualiDecimal.ZERO)) {
            putFieldError(TemPropertyConstants.TRIP_TYPE_AUTO_TRAVEL_REIMBURSEMENT_LIMIT, TemKeyConstants.ERROR_TRIP_TYPE_AUTO_TRAVEL_REIMBURSEMENT_LIMIT);
            return false;
        }
        return true;
    }
}
