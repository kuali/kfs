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
package org.kuali.kfs.module.bc.document.validation.event;

import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.rice.krad.document.Document;

/**
 * Base class for salary setting events. 
 */
public abstract class SalarySettingBaseEvent extends BudgetExpansionEvent {
    /**
     * Constructs a SalarySettingBaseEvent.java.
     * @param description the given description
     * @param errorPathPrefix the given error path prefix
     * @param document the specified document
     */ 
    public SalarySettingBaseEvent(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent#getExpansionRuleInterfaceClass()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getExpansionRuleInterfaceClass() {
        return SalarySettingRule.class;
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent#getRuleInterfaceClass()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return this.getExpansionRuleInterfaceClass();
    }
}
