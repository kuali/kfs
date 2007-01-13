/*
 * Copyright 2006 The Kuali Foundation.
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

import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCons;

public class ObjectConsRule extends MaintenanceDocumentRuleBase {
    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        checkObjLevelCode();
        return true;
    }

    /**
     * 
     * This method should be overridden to provide custom rules for processing document routing
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        success &= checkObjLevelCode();
        return success;
    }

    /**
     * 
     * This method checks to see if the Object Consolidation code matches a pre-existing Object Level code that is already entered.
     * If it does it returns false with an error
     * 
     * @param document
     * @return false if Object Level Code already exists
     */
    private boolean checkObjLevelCode() {
        boolean success = true;
        ObjectCons objConsolidation = (ObjectCons) super.getNewBo();
        String chartOfAccountsCode = objConsolidation.getChartOfAccountsCode();
        String financialObjectLevelCode = objConsolidation.getFinConsolidationObjectCode();
        Map primaryKeys = new HashMap();
        primaryKeys.put("chartOfAccountsCode", chartOfAccountsCode);
        primaryKeys.put("financialObjectLevelCode", financialObjectLevelCode);
        ObjLevel objLevel = (ObjLevel) getBoService().findByPrimaryKey(ObjLevel.class, primaryKeys);
        if (objLevel != null) {
            success = false;
            putFieldError("finConsolidationObjectCode", KeyConstants.ERROR_DOCUMENT_OBJCONSMAINT_ALREADY_EXISTS_AS_OBJLEVEL);
        }
        return success;
    }
}
