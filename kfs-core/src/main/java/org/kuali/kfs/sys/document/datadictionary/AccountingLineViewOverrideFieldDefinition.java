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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.kfs.sys.document.web.AccountingLineViewOverrideField;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;

/**
 * The definition of an override field associated with an accounting line view field
 */
public class AccountingLineViewOverrideFieldDefinition extends MaintainableFieldDefinition {
    private boolean allowEditDespiteReadOnlyParentWhenAccoutingLineEditable = false;
    
    /**
     * Creates an override field
     * @param parentField the AccountingLineViewField which will own the created OverrideField
     * @param accountingLineClass the class of the AccountingLine being rendered
     * @return a properly created AccountingLineViewOverrideField
     */
    public AccountingLineViewOverrideField getOverrideFieldForDefinition(AccountingLineViewField parentField, Class<? extends AccountingLine> accountingLineClass) {
        return new AccountingLineViewOverrideField(parentField, this, accountingLineClass);
    }

    /**
     * Gets the allowEditDespiteReadOnlyParentWhenAccoutingLineEditable attribute. 
     * @return Returns the allowEditDespiteReadOnlyParentWhenAccoutingLineEditable.
     */
    public boolean isAllowEditDespiteReadOnlyParentWhenAccoutingLineEditable() {
        return allowEditDespiteReadOnlyParentWhenAccoutingLineEditable;
    }

    /**
     * Sets the allowEditDespiteReadOnlyParentWhenAccoutingLineEditable attribute value.
     * @param allowEditDespiteReadOnlyParentWhenAccoutingLineEditable The allowEditDespiteReadOnlyParentWhenAccoutingLineEditable to set.
     */
    public void setAllowEditDespiteReadOnlyParentWhenAccoutingLineEditable(boolean allowOverrideWithReadOnlyParentWhenAccoutingLineEditable) {
        this.allowEditDespiteReadOnlyParentWhenAccoutingLineEditable = allowOverrideWithReadOnlyParentWhenAccoutingLineEditable;
    }
}
