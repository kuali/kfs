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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.BasicAccountingCategory;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
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
