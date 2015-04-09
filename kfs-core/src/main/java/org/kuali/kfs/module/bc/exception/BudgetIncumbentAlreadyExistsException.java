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
package org.kuali.kfs.module.bc.exception;

import org.kuali.kfs.module.bc.BCKeyConstants;

/**
 * Thrown when a request to import a new incumbent into budget is called but the incumbent is already in the budget intended incumbent table.
 * 
 * @see org.kuali.kfs.module.bc.service.BudgetConstructionIncumbentService
 */
public class BudgetIncumbentAlreadyExistsException extends RuntimeException {
    private String emplid;

    /**
     * Constructs a BudgetIncumbentAlreadyExistsException.java.
     */
    public BudgetIncumbentAlreadyExistsException(String emplid) {
        super();

        this.emplid = emplid;
    }

    public String getMessageKey() {
        return BCKeyConstants.ERROR_BUDGET_INCUMBENT_ALREADY_EXISTS;
    }

    public String[] getMessageParameters() {
        return new String[] { emplid };
    }
}
