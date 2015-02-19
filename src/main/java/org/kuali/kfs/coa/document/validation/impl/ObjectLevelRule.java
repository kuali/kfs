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
