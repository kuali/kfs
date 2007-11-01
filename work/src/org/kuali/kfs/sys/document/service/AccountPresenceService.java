/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * Provides methods for checking the budgeting of attributes associated with an account that has presence control turned on.
 */
public interface AccountPresenceService {

    /**
     * Checks the given account for presence control turned on. If turned on, the object code must have a budget record in the gl
     * balance table, otherwise this method returns false. If presence control is turned of, method returns true.
     * 
     * @param account
     * @param objectCode
     * @return
     */
    public boolean isObjectCodeBudgetedForAccountPresence(Account account, ObjectCode objectCode);
}
