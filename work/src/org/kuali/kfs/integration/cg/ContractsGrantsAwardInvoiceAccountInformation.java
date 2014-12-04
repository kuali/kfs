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
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * Integration interface for AwardInvoiceAccount
 */
public interface ContractsGrantsAwardInvoiceAccountInformation extends ExternalizableBusinessObject, Inactivatable {

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
     * Gets the objectCode attribute.
     *
     * @return Returns the objectCode
     */
    public String getObjectCode();

    /**
     * Gets the subObjectCode attribute.
     *
     * @return Returns the subObjectCode
     */
    public String getSubObjectCode();

    /**
     * Gets the subAccountNumber attribute.
     *
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber();


    /**
     * Gets the object attribute.
     *
     * @return Returns the object
     */
    public ObjectCode getObject();

    /**
     * Gets the subAccount attribute.
     *
     * @return Returns the subAccount
     */
    public SubAccount getSubAccount();

    /**
     * Gets the subObject attribute.
     *
     * @return Returns the subObject
     */
    public SubObjectCode getSubObject();

    /**
     * Gets the project attribute.
     *
     * @return Returns the project
     */
    public ProjectCode getProject();


    /**
     * Gets the accountType attribute.
     *
     * @return Returns the accountType.
     */
    public String getAccountType();
}
