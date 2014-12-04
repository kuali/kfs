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
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.coa.businessobject.ObjectCode;

/**
 * An interface that represents the logic of a category associated with the Organization Reversion Process
 */
public interface OrganizationReversionCategoryLogic {
    
    /**
     * Given an object code, determins if balances with the object code should be added to the bucket
     * for this category or not
     * 
     * @param oc the object code of a balance
     * @return true if object code indicates that the balance should be added to this category, false if not
     */
    public boolean containsObjectCode(ObjectCode oc);

    /**
     * Returns the code of the organization reversion category this logic is associated with
     * 
     * @return the category code of this organization reversion category
     */
    public String getCode();

    /**
     * Returns the name of the organization reversion category this logic is associated with?
     * 
     * @return the name of this organization reversion category
     */
    public String getName();

    /**
     * Does this category represent an expense?
     * 
     * @return true if it represents an expense, false if not
     */
    public boolean isExpense();
}
