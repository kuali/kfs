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

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectCons;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.ObjectLevelService;
/**
 * 
 * This class implements the business rules for {@link ObjectCons}
 */
public class ObjectConsRule extends MaintenanceDocumentRuleBase {

    private static ChartService chartService;
    private static ObjectLevelService objectLevelService;
    private static ObjectCodeService objectCodeService;
    
    /**
     * 
     * Constructs a {@link ObjectConsRule}
     * Pseudo-injects some services
     */
    public ObjectConsRule() {
        if (chartService == null) {
            objectLevelService = SpringContext.getBean(ObjectLevelService.class);
            objectCodeService = SpringContext.getBean(ObjectCodeService.class);
            chartService = SpringContext.getBean(ChartService.class);
        }
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link ObjectConsRule#checkObjLevelCode(ObjectCons)}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        ObjectCons objConsolidation = (ObjectCons) getNewBo();

        checkObjLevelCode(objConsolidation);
        return true;
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link ObjectConsRule#checkObjLevelCode(ObjectCons)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        ObjectCons objConsolidation = (ObjectCons) getNewBo();

        success &= checkObjLevelCode(objConsolidation);
        return success;
    }

    /**
     * This method checks to see if the Object Consolidation code matches a pre-existing Object Level code that is already entered.
     * If it does it returns false with an error
     * 
     * @param document
     * @return false if Object Level Code already exists
     */
    private boolean checkObjLevelCode(ObjectCons objConsolidation) {
        boolean success = true;

        ObjLevel objLevel = objectLevelService.getByPrimaryId(objConsolidation.getChartOfAccountsCode(), objConsolidation.getFinConsolidationObjectCode());
        if (objLevel != null) {
            success = false;
            putFieldError("finConsolidationObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCONSMAINT_ALREADY_EXISTS_AS_OBJLEVEL);
        }
        return success;
    }
}
