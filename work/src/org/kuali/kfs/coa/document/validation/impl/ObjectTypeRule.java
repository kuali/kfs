/*
 * Copyright 2007 The Kuali Foundation.
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
import org.kuali.module.chart.bo.BasicAccountingCategory;
import org.kuali.module.chart.bo.ObjectType;
/**
 * 
 * This class implements the business rules for {@link ObjectType}
 */
public class ObjectTypeRule extends MaintenanceDocumentRuleBase {

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link ObjectTypeRule#checkAccountingCategory(MaintenanceDocument)}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        checkAccountingCategory(document);
        return true; // it's a save, always return true
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link ObjectTypeRule#checkAccountingCategory(MaintenanceDocument)}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        success &= checkAccountingCategory(document);
        return success;
    }

    /**
     * 
     * This checks that the accounting category exists in the system
     * @param document
     * @return false if category does not exist
     */
    protected boolean checkAccountingCategory(MaintenanceDocument document) {
        boolean result = true;
        ObjectType objectType = (ObjectType) document.getNewMaintainableObject().getBusinessObject();
        Map pkMap = new HashMap();
        pkMap.put("code", objectType.getBasicAccountingCategoryCode());
        BasicAccountingCategory basicAccountingCategory = (BasicAccountingCategory) this.getBoService().findByPrimaryKey(BasicAccountingCategory.class, pkMap);
        if (basicAccountingCategory == null) {
            result = false;
            putFieldError("basicAccountingCategoryCode", KFSKeyConstants.ERROR_DOCUMENT_OBJTYPE_INVALID_ACCT_CTGRY, new String[] { objectType.getBasicAccountingCategoryCode() });
        }
        return result;
    }
}
