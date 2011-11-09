/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * 
 * This class implements the business rules for {@link ObjLevel}
 */
public class ObjectLevelRule extends MaintenanceDocumentRuleBase {
    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link ObjectLevelRule#checkObjConsCode()}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        checkObjConsCode();
        return true;
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link ObjectLevelRule#checkObjConsCode()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        success &= checkObjConsCode();
        return success;
    }

    /**
     * This method checks to see if the Object Consolidation code matches a pre-existing Object Level code that is already entered.
     * If it does it returns false with an error
     * 
     * @param document
     * @return false if Object Level Code already exists
     */
    protected boolean checkObjConsCode() {
        boolean success = true;
        ObjectLevel objLevel = (ObjectLevel) super.getNewBo();
        String chartOfAccountsCode = objLevel.getChartOfAccountsCode();
        String finConsolidationObjectCode = objLevel.getFinancialObjectLevelCode();
        Map primaryKeys = new HashMap();
        primaryKeys.put("chartOfAccountsCode", chartOfAccountsCode);
        primaryKeys.put("finConsolidationObjectCode", finConsolidationObjectCode);
        ObjectConsolidation objCons = (ObjectConsolidation) getBoService().findByPrimaryKey(ObjectConsolidation.class, primaryKeys);
        if (objCons != null) {
            success = false;
            putFieldError("financialObjectLevelCode", KFSKeyConstants.ERROR_DOCUMENT_OBJLEVELMAINT_ALREADY_EXISTS_AS_OBJCONS);
        }
        return success;
    }
}
