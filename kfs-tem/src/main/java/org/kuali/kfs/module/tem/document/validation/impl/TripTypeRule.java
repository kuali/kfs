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
