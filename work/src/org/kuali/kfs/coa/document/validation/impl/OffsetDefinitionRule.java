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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.OffsetDefinition;

public class OffsetDefinitionRule extends MaintenanceDocumentRuleBase {
    private OffsetDefinition oldDefinition;
    private OffsetDefinition newDefinition;


    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    public void setupConvenienceObjects() {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldDefinition = (OffsetDefinition) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newDefinition = (OffsetDefinition) super.getNewBo();
    }

    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        checkDocTypeAndFinancialObjCode(document);
        checkDocTypeActiveFinancialObjCode(document);
        return true;
    }

    /**
     * This method should be overridden to provide custom rules for processing document routing
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        success &= checkDocTypeAndFinancialObjCode(document);
        success &= checkDocTypeActiveFinancialObjCode(document);
        return true;
    }

    private boolean checkDocTypeAndFinancialObjCode(MaintenanceDocument document) {
        boolean success = true;
        // we need to check to see if the values are in the right range and then
        // see if the ObjectCode is the right value
        if ((ObjectUtils.isNotNull(newDefinition.getFinancialObject()) && StringUtils.isNotEmpty(newDefinition.getFinancialObject().getFinancialObjectSubTypeCode()) && !newDefinition.getFinancialObject().getFinancialObjectSubTypeCode().equalsIgnoreCase("AR")) || StringUtils.isEmpty(newDefinition.getFinancialObjectCode())) {
            if (SpringContext.getBean(ParameterService.class).evaluateConstrainedValue(OffsetDefinition.class, KFSConstants.ChartApcParms.VALID_DOCUMENT_TYPES_BY_OBJECT_SUB_TYPE, newDefinition.getFinancialObject().getFinancialObjectSubTypeCode(), newDefinition.getFinancialDocumentTypeCode())) {
                putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OFFSETDEFMAINT_INVALID_OBJ_CODE_FOR_DOCTYPE, new String[] { newDefinition.getFinancialObjectCode(), SpringContext.getBean(ParameterService.class).getConstrainedValuesString(OffsetDefinition.class, KFSConstants.ChartApcParms.VALID_DOCUMENT_TYPES_BY_OBJECT_SUB_TYPE, newDefinition.getFinancialObject().getFinancialObjectSubTypeCode()) });
                success &= false;
            }
        }

        return success;
    }

    private boolean checkDocTypeActiveFinancialObjCode(MaintenanceDocument document) {
        boolean success = true;
        if (SpringContext.getBean(ParameterService.class).evaluateConstrainedValue(OffsetDefinition.class, KFSConstants.ChartApcParms.DOCTYPE_AND_OBJ_CODE_ACTIVE, newDefinition.getFinancialDocumentTypeCode())) {
            if ((ObjectUtils.isNotNull(newDefinition.getFinancialObject()) && !newDefinition.getFinancialObject().isFinancialObjectActiveCode()) || ObjectUtils.isNull(newDefinition.getFinancialObject())) {
                putFieldError("financialObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OFFSETDEFMAINT_INACTIVE_OBJ_CODE_FOR_DOCTYPE, new String[] { newDefinition.getFinancialObjectCode(), SpringContext.getBean(ParameterService.class).getConstrainedValuesString(OffsetDefinition.class, KFSConstants.ChartApcParms.DOCTYPE_AND_OBJ_CODE_ACTIVE, newDefinition.getFinancialDocumentTypeCode()) });
                success &= false;
            }

        }
        return success;
    }
}
