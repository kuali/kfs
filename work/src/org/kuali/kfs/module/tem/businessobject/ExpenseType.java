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
package org.kuali.kfs.module.tem.businessobject;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * A record encapsulating information about a category of expense - airfare or lodging for instance
 */
public class ExpenseType extends KualiCodeBase implements MutableInactivatable {
    private boolean groupTravel;
    private boolean expenseDetailRequired;
    private boolean prepaidExpense;
    private boolean hosted;
    private String expenseTypeMetaCategoryCode;
    private boolean categoryDefault;

    public boolean isGroupTravel() {
        return groupTravel;
    }
    public void setGroupTravel(boolean groupTravelIndicator) {
        this.groupTravel = groupTravelIndicator;
    }
    public boolean isExpenseDetailRequired() {
        return expenseDetailRequired;
    }
    public void setExpenseDetailRequired(boolean expenseDetailRequired) {
        this.expenseDetailRequired = expenseDetailRequired;
    }
    public boolean isPrepaidExpense() {
        return prepaidExpense;
    }
    public void setPrepaidExpense(boolean prepaidExpenseIndicator) {
        this.prepaidExpense = prepaidExpenseIndicator;
    }
    public boolean isHosted() {
        return hosted;
    }
    public void setHosted(boolean hosted) {
        this.hosted = hosted;
    }
    public String getExpenseTypeMetaCategoryCode() {
        return expenseTypeMetaCategoryCode;
    }
    public void setExpenseTypeMetaCategoryCode(String expenseTypeMetaCategoryCode) {
        this.expenseTypeMetaCategoryCode = expenseTypeMetaCategoryCode;
    }
    public boolean isCategoryDefault() {
        return categoryDefault;
    }
    public void setCategoryDefault(boolean categoryDefault) {
        this.categoryDefault = categoryDefault;
    }
}
