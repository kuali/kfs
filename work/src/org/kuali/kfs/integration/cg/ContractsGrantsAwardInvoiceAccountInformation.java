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
