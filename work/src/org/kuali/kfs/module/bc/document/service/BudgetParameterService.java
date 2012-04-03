/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

import org.kuali.kfs.module.bc.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;

/**
 * This provides methods specific to system parameters for the Budget module
 */
public interface BudgetParameterService {

    /**
     * determines if a BudgetConstructionDocument's account is a salary setting only account returns
     * AccountSalarySettingOnlyCause.NONE if not and if both system parameters don't exist
     *
     * @param bcDoc
     * @return
     */
    public AccountSalarySettingOnlyCause isSalarySettingOnlyAccount(BudgetConstructionDocument bcDoc);

    /**
     * returns a string containing the allowed revenue or expenditure object types setup in the Budget Construction parameter space.
     * this string is typically used in the lookup search criteria
     *
     * @param isRevenue
     * @return
     */
    public String getLookupObjectTypes(boolean isRevenue);
}
