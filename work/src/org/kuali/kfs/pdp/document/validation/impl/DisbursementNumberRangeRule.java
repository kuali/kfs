/*
 * Copyright 2007-2008 The Kuali Foundation
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
