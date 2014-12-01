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
 * Thrown when a request to import a new position into budget is called but the position is already in the budget position table.
 * 
 * @see org.kuali.kfs.module.bc.service.BudgetConstructionPositionService
 */
public class BudgetPositionAlreadyExistsException extends RuntimeException {
    private Integer universityFiscalYear;
    private String positionNumber;

    /**
     * Constructs a BudgetPositionAlreadyExistsException.java.
     */
    public BudgetPositionAlreadyExistsException(Integer universityFiscalYear, String positionNumber) {
        super();

        this.universityFiscalYear = universityFiscalYear;
        this.positionNumber = positionNumber;
    }

    public String getMessageKey() {
        return BCKeyConstants.ERROR_BUDGET_POSITION_ALREADY_EXISTS;
    }

    public String[] getMessageParameters() {
        return new String[] { universityFiscalYear.toString(), positionNumber };
    }
}
