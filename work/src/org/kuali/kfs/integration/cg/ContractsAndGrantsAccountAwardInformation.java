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
package org.kuali.kfs.integration.cg;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * Information about an award associated with an account.
 */
public interface ContractsAndGrantsAccountAwardInformation extends ExternalizableBusinessObject, Inactivatable {

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber();

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode();

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber
     */
    public String getAccountNumber();

    /**
     * Gets the principalId attribute.
     *
     * @return Returns the principalId
     */
    public String getPrincipalId();

    /**
     * Gets the account attribute.
     *
     * @return Returns the account
     */
    public Account getAccount();

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts();

    /**
     * Gets the name of the project director for the associated award.
     * @return the name of the project director
     */
    public String getProjectDirectorName();
}

