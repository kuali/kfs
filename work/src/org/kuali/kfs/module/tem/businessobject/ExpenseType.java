/*
 * Copyright 2013 The Kuali Foundation.
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
