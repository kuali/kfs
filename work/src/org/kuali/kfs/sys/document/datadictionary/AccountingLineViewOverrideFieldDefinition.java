/*
 * Copyright 2008 The Kuali Foundation
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
