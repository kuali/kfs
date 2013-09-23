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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.sys.document.validation.Validation;

/**
 * Contract for validations which validate an expense detail line
 */
public interface ActualExpenseDetailValidation extends Validation {
    /**
     * Sets the actual expense to validate
     * @param actualExpense the actual expense to validate
     */
    public abstract void setActualExpenseForValidation(ActualExpense actualExpense);
    
    /**
     * Sets the actual expense detail (which should be a detail of the set actual expense) to validate
     * @param actualExpenseDetail the actual expense detail to validate
     */
    public abstract void setActualExpenseDetailForValidation(ActualExpense actualExpenseDetail);
}
