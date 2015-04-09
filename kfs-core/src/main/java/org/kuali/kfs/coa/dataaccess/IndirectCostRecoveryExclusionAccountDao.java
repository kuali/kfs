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
