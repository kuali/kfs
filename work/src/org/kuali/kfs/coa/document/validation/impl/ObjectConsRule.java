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

import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
/**
 * 
 * This class implements the business rules for {@link ObjectCons}
 */
public class ObjectConsRule extends MaintenanceDocumentRuleBase {

    protected static ChartService chartService;
    protected static ObjectLevelService objectLevelService;
    protected static ObjectCodeService objectCodeService;
    
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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        ObjectConsolidation objConsolidation = (ObjectConsolidation) getNewBo();

        checkObjLevelCode(objConsolidation);
        return true;
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link ObjectConsRule#checkObjLevelCode(ObjectCons)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        ObjectConsolidation objConsolidation = (ObjectConsolidation) getNewBo();

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
    protected boolean checkObjLevelCode(ObjectConsolidation objConsolidation) {
        boolean success = true;

        ObjectLevel objLevel = objectLevelService.getByPrimaryId(objConsolidation.getChartOfAccountsCode(), objConsolidation.getFinConsolidationObjectCode());
        if (objLevel != null) {
            success = false;
            putFieldError("finConsolidationObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCONSMAINT_ALREADY_EXISTS_AS_OBJLEVEL);
        }
        return success;
    }
}
