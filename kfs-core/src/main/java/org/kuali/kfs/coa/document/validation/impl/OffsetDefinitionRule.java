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
package org.kuali.kfs.coa.document.validation.impl;

import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * This class implements the business rules for {@link OffsetDefinition}
 */
public class OffsetDefinitionRule extends MaintenanceDocumentRuleBase {
    protected OffsetDefinition oldDefinition;
    protected OffsetDefinition newDefinition;


    /**
     * This method sets the convenience objects like newDefinition and oldDefinition, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return checkDocTypeActiveFinancialObjCode(document);
    }

    /**
     * 
     * This checks that the doctype used for this {@link OffsetDefinition} is valid and active
     * @param document
     * @return false if the {@link org.kuali.rice.core.api.parameter.Parameter} evaluation fails and the financial object code is either null or inactive
     */
    protected boolean checkDocTypeActiveFinancialObjCode(MaintenanceDocument document) {
        boolean success = true;
        ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(OffsetDefinition.class, KFSConstants.ChartApcParms.DOCTYPE_AND_OBJ_CODE_ACTIVE, newDefinition.getFinancialDocumentTypeCode());
        if (!evaluator.evaluationSucceeds()) {
            if ((ObjectUtils.isNotNull(newDefinition.getFinancialObject()) && !newDefinition.getFinancialObject().isFinancialObjectActiveCode()) || ObjectUtils.isNull(newDefinition.getFinancialObject())) {
                putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OFFSETDEFMAINT_INACTIVE_OBJ_CODE_FOR_DOCTYPE, new String[] { newDefinition.getFinancialObjectCode(), evaluator.getParameterValuesForMessage() });
                success &= false;
            }

        }
        return success;
    }
}
