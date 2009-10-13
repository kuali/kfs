/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionAccount;

/**
 * This interface defines the data access methods for {@link IndirectCostRecoveryExclusionAccount}
 */
public interface IndirectCostRecoveryExclusionAccountDao {
    /**
     * This method retrieves a given {@link IndirectCostRecoveryExclusionAccount}
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param objectChartOfAccountsCode
     * @param objectCode
     * @return the {@link IndirectCostRecoveryExclusionAccount} matching this criteria
     */
    public IndirectCostRecoveryExclusionAccount getByPrimaryKey(String chartOfAccountsCode, String accountNumber, String objectChartOfAccountsCode, String objectCode);

    /**
     * This method determines whether a given {@link IndirectCostRecoveryExclusionAccount} exists from a specific {@link Account}
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return true if it does exist
     */
    public boolean existByAccount(String chartOfAccountsCode, String accountNumber);
}
