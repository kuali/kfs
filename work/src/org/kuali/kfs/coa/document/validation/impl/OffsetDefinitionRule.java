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
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.OffsetDefinition;

/**
 * 
 * This class implements the business rules for {@link OffsetDefinition}
 */
public class OffsetDefinitionRule extends MaintenanceDocumentRuleBase {
    private OffsetDefinition oldDefinition;
    private OffsetDefinition newDefinition;


    /**
     * This method sets the convenience objects like newDefinition and oldDefinition, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldDefinition = (OffsetDefinition) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newDefinition = (OffsetDefinition) super.getNewBo();
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link OffsetDefinitionRule#checkDocTypeActiveFinancialObjCode(MaintenanceDocument)}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        return checkDocTypeActiveFinancialObjCode(document);
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link OffsetDefinitionRule#checkDocTypeActiveFinancialObjCode(MaintenanceDocument)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return checkDocTypeActiveFinancialObjCode(document);
    }

    /**
     * 
     * This checks that the doctype used for this {@link OffsetDefinition} is valid and active
     * @param document
     * @return false if the {@link org.kuali.core.bo.Parameter} evaluation fails and the financial object code is either null or inactive
     */
    private boolean checkDocTypeActiveFinancialObjCode(MaintenanceDocument document) {
        boolean success = true;
        ParameterEvaluator evaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(OffsetDefinition.class, KFSConstants.ChartApcParms.DOCTYPE_AND_OBJ_CODE_ACTIVE, newDefinition.getFinancialDocumentTypeCode());
        if (!evaluator.evaluationSucceeds()) {
            if ((ObjectUtils.isNotNull(newDefinition.getFinancialObject()) && !newDefinition.getFinancialObject().isFinancialObjectActiveCode()) || ObjectUtils.isNull(newDefinition.getFinancialObject())) {
                putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OFFSETDEFMAINT_INACTIVE_OBJ_CODE_FOR_DOCTYPE, new String[] { newDefinition.getFinancialObjectCode(), evaluator.getParameterValuesForMessage() });
                success &= false;
            }

        }
        return success;
    }
}
