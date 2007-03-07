/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.bo;

import java.math.BigDecimal;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;


public interface PurApAccountingLine {

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public abstract Chart getChartOfAccounts();

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public abstract void setChartOfAccounts(Chart chartOfAccounts);

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     * 
     */
    public abstract Account getAccount();

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public abstract void setAccount(Account account);

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public abstract SubAccount getSubAccount();

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public abstract void setSubAccount(SubAccount subAccount);

    public abstract Integer getAccountIdentifier();

    public abstract void setAccountIdentifier(Integer requisitionAccountIdentifier);

    public abstract Integer getItemIdentifier();

    public abstract void setItemIdentifier(Integer requisitionItemIdentifier);

    public abstract String getChartOfAccountsCode();

    public abstract void setChartOfAccountsCode(String chartOfAccountsCode);

    public abstract String getAccountNumber();

    public abstract void setAccountNumber(String accountNumber);

    public abstract String getSubAccountNumber();

    public abstract void setSubAccountNumber(String subAccountNumber);

    public abstract String getFinancialObjectCode();

    public abstract void setFinancialObjectCode(String financialObjectCode);

    public abstract String getFinancialSubObjectCode();

    public abstract void setFinancialSubObjectCode(String financialSubObjectCode);

    public abstract String getProjectCode();

    public abstract void setProjectCode(String projectCode);

    public abstract String getOrganizationReferenceId();

    public abstract void setOrganizationReferenceId(String organizationReferenceId);

    public abstract BigDecimal getAccountLinePercent();

    public abstract void setAccountLinePercent(BigDecimal accountLinePercent);

}