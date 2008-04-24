/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.budget.service;

import java.util.List;

import org.kuali.module.budget.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.module.budget.document.BudgetConstructionDocument;

/**
 * This provides methods specific to system parameters for the Budget module
 */
public interface BudgetParameterService {
    
    /**
     * Returns a system parameter value as a list. If the parameter doesn't exist,
     * a null list is returned and an info message is written to the log
     *  
     * @param componentClass
     * @param parameterName
     * @return
     */
    public List getParameterValues(Class componentClass, String parameterName);
    
    /**
     * determines if a BudgetConstructionDocument's account is a salary setting only account
     * returns AccountSalarySettingOnlyCause.NONE if not and if both system parameters don't exist
     * 
     * @param bcDoc
     * @return
     */
    public AccountSalarySettingOnlyCause isSalarySettingOnlyAccount(BudgetConstructionDocument bcDoc);

}
