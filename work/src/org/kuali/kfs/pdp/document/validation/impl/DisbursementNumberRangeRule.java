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
package org.kuali.kfs.pdp.document.validation.impl;

import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Contains Business Rules for the Effort Certification Report Maintenance Document.
 */
public class DisbursementNumberRangeRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementNumberRangeRule.class);

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("processCustomRouteDocumentBusinessRules() start");
        
        if (GlobalVariables.getMessageMap().hasErrors()) {
            return false;
        }
        
        boolean isValid = true;
        DisbursementNumberRange disbursementNumberRange = (DisbursementNumberRange) document.getNewMaintainableObject().getBusinessObject();
        
        KualiInteger beginNumber = disbursementNumberRange.getBeginDisbursementNbr();
        KualiInteger lastAssigned = disbursementNumberRange.getLastAssignedDisbNbr();
        KualiInteger end = disbursementNumberRange.getEndDisbursementNbr();
        if ( lastAssigned.isLessThan(beginNumber) || lastAssigned.isGreaterThan(end)) {
            putFieldError(PdpPropertyConstants.LAST_ASSIGNED_DISBURSEMENT_NUMBER, PdpKeyConstants.DISBURSEMENT_NUMBER_OUT_OF_RANGE);
            isValid = false;
        }
        
        return isValid;
    }
}
