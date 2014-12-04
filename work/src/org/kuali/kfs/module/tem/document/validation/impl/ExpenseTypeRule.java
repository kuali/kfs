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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Rules for expense type maintenance document
 */
public class ExpenseTypeRule extends MaintenanceDocumentRuleBase {
    protected volatile TravelExpenseService travelExpenseService;

    /**
     * Checks - but does not return the value of - the category and category default status
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = super.processCustomSaveDocumentBusinessRules(document);

        final ExpenseType newExpenseType = (ExpenseType)document.getNewMaintainableObject().getBusinessObject();
        ExpenseType oldExpenseType = null;
        if (document.getOldMaintainableObject() != null && document.getOldMaintainableObject().getBusinessObject() != null) {
            oldExpenseType = (ExpenseType)document.getOldMaintainableObject().getBusinessObject();
        }
        validateCategory(newExpenseType);
        validateCategoryDefault(newExpenseType, oldExpenseType);

        return success;
    }

    /**
     * Checks the category and category default status of the new maintenance object
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = super.processCustomRouteDocumentBusinessRules(document);

        final ExpenseType newExpenseType = (ExpenseType)document.getNewMaintainableObject().getBusinessObject();
        ExpenseType oldExpenseType = null;
        if (document.getOldMaintainableObject() != null && document.getOldMaintainableObject().getBusinessObject() != null) {
            oldExpenseType = (ExpenseType)document.getOldMaintainableObject().getBusinessObject();
        }
        success &= validateCategory(newExpenseType);
        if (success) {
            success &= validateCategoryDefault(newExpenseType, oldExpenseType); // no use in validating default for a non-existing category
        }

        return success;
    }

    /**
     * Validates that the value set as the expense type's meta category code actually exists in the system
     * @param newExpenseType the expense type to validate
     * @return true if the expense type
     */
    protected boolean validateCategory(ExpenseType newExpenseType) {
        if (!StringUtils.isBlank(newExpenseType.getExpenseTypeMetaCategoryCode())) {
            final TemConstants.ExpenseTypeMetaCategory category = TemConstants.ExpenseTypeMetaCategory.forCode(newExpenseType.getExpenseTypeMetaCategoryCode());
            if (category == null) {
                putFieldError(TemPropertyConstants.EXPENSE_TYPE_META_CATEGORY_CODE, TemKeyConstants.ERROR_EXPENSE_TYPE_CATEGORY_NO_EXISTS, newExpenseType.getExpenseTypeMetaCategoryCode());
                return false;
            }
        }
        return true;
    }

    /**
     * Checks that the new expense type - if it wants to - actually is allowed to be the default of the expense type category (ie, there's not another one); and adds warnings about missing expense types
     * @param newExpenseType the expense type as it will be once the document processes
     * @param oldExpenseType the expense type as it is now (or null if the expense type does not exist)
     * @return true if the new expense type handles default for expense type category correctly, false if there's a problem
     */
    protected boolean validateCategoryDefault(ExpenseType newExpenseType, ExpenseType oldExpenseType) {
        if (!StringUtils.isBlank(newExpenseType.getExpenseTypeMetaCategoryCode())) {
            final TemConstants.ExpenseTypeMetaCategory metaCategory = TemConstants.ExpenseTypeMetaCategory.forCode(newExpenseType.getExpenseTypeMetaCategoryCode());

            if (metaCategory != null) {
                if (newExpenseType.isCategoryDefault()) {
                    final ExpenseType expenseType = getTravelExpenseService().getDefaultExpenseTypeForCategory(metaCategory);
                    if (expenseType != null && !StringUtils.equals(expenseType.getCode(), newExpenseType.getCode())) {
                        putFieldError(TemPropertyConstants.EXPENSE_TYPE_META_CATEGORY_CODE, TemKeyConstants.ERROR_EXPENSE_TYPE_CANNOT_BE_DEFAULT, new String[] { metaCategory.getName(), expenseType.getCode() });
                        return false;
                    }
                } else {
                    if (oldExpenseType != null && oldExpenseType.isCategoryDefault() && StringUtils.equals(newExpenseType.getExpenseTypeMetaCategoryCode(), oldExpenseType.getExpenseTypeMetaCategoryCode())) {
                        GlobalVariables.getMessageMap().putWarning(KFSPropertyConstants.DOCUMENT+"."+KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT+"."+TemPropertyConstants.CATEGORY_DEFAULT, TemKeyConstants.WARNING_EXPENSE_TYPE_NO_CATEGORY_DEFAULT_AFTER_DOC, metaCategory.getName());
                    } else {
                        final ExpenseType expenseType = getTravelExpenseService().getDefaultExpenseTypeForCategory(TemConstants.ExpenseTypeMetaCategory.forCode(newExpenseType.getExpenseTypeMetaCategoryCode()));
                        if (expenseType == null) {
                            GlobalVariables.getMessageMap().putWarning(KFSPropertyConstants.DOCUMENT+"."+KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT+"."+TemPropertyConstants.CATEGORY_DEFAULT, TemKeyConstants.WARNING_EXPENSE_TYPE_NO_CATEGORY_DEFAULT, metaCategory.getName());
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return the default implementation of the TravelExpenseService
     */
    public TravelExpenseService getTravelExpenseService() {
        if (travelExpenseService == null) {
            travelExpenseService = SpringContext.getBean(TravelExpenseService.class);
        }
        return travelExpenseService;
    }
}
