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
package org.kuali.kfs.module.bc.document.validation;

import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;

/**
 * Defines a rule for adding (creating) a new Budget Construction Document.
 * This rule is invoked from the Budget Construction Selection screen when the
 * user tries to load a BC document that does not exist. 
 */
public interface AddBudgetConstructionDocumentRule<D extends BudgetConstructionDocument> {
    
    /**
     * Processes all the rules for this event and returns true if all rules passed
     * 
     * @param budgetConstructionDocument
     * @return
     */
    public boolean processAddBudgetConstructionDocumentRules(D budgetConstructionDocument);

}
