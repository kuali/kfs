/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.chart.dao;

import org.kuali.module.chart.bo.PriorYearAccount;

/**
 * This interface defines data access methods for {@link PriorYearAccount}
 * 
 * 
 */
public interface PriorYearAccountDao {
    /**
     * 
     * Retrieves an Account object based on primary key.
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @return {@link PriorYearAccount} based on primary key
     */
    public PriorYearAccount getByPrimaryId(String chartOfAccountsCode, String accountNumber);
}
