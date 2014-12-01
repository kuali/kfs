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
package org.kuali.kfs.fp.document.service;

import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;

/**
 * This service interface defines methods that a BudgetAdjustmentLaborBenefitService implementation must provide.
 * 
 */
public interface BudgetAdjustmentLaborBenefitsService {

    /**
     * Checks the object codes from the document accounting lines against the labor object code table.
     * 
     * @param budgetDocument The budget document to be analyzed.
     * @return True if any labor object codes were found
     */
    public boolean hasLaborObjectCodes(BudgetAdjustmentDocument budgetDocument);

    /**
     * Generates labor benefit accounting lines for the budget document.
     * 
     * @param budgetDocument The budget document used to generate the labor benefit accounting lines.
     */
    public void generateLaborBenefitsAccountingLines(BudgetAdjustmentDocument budgetDocument);

}
