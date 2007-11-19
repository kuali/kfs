/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.chart.rules;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCons;

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
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
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
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
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
    private boolean checkObjConsCode() {
        boolean success = true;
        ObjLevel objLevel = (ObjLevel) super.getNewBo();
        String chartOfAccountsCode = objLevel.getChartOfAccountsCode();
        String finConsolidationObjectCode = objLevel.getFinancialObjectLevelCode();
        Map primaryKeys = new HashMap();
        primaryKeys.put("chartOfAccountsCode", chartOfAccountsCode);
        primaryKeys.put("finConsolidationObjectCode", finConsolidationObjectCode);
        ObjectCons objCons = (ObjectCons) getBoService().findByPrimaryKey(ObjectCons.class, primaryKeys);
        if (objCons != null) {
            success = false;
            putFieldError("financialObjectLevelCode", KFSKeyConstants.ERROR_DOCUMENT_OBJLEVELMAINT_ALREADY_EXISTS_AS_OBJCONS);
        }
        return success;
    }
}
