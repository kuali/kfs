/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCodeCurrent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * Provides an interface for the accounting lines used in the Transfer of Funds documents in the Endowment module.
 */
public interface EndowmentAccountingLine extends PersistableBusinessObject {

    /**
     * Gets the account.
     * 
     * @return account
     */
    public Account getAccount();

    /**
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account);

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public Chart getChart();

    /**
     * @param chart The chartOfAccountsCode to set.
     * @deprecated
     */
    public void setChart(Chart chart);

    /**
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount);

    /**
     * @return Returns the amount.
     */
    public KualiDecimal getAmount();

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    public void setObjectCode(ObjectCodeCurrent objectCode);

    /**
     * Gets the object code.
     * 
     * @return objectCode
     */
    public ObjectCodeCurrent getObjectCode();

    /**
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId);

    /**
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode);

    /**
     * Gets the projectCode.
     * 
     * @return projectCode
     */
    public String getProjectCode();

    /**
     * @return Returns the accountingLineNumber.
     */
    public Integer getAccountingLineNumber();

    /**
     * @param accountingLineNumber The accountingLineNumber to set.
     */
    public void setAccountingLineNumber(Integer accountingLineNumber);

    /**
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount();

    /**
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount);

    /**
     * @return Returns the subObjectCode.
     */
    public SubObjectCodeCurrent getSubObjectCode();

    /**
     * @param subObjectCode The subObjectCode to set.
     * @deprecated
     */
    public void setSubObjectCode(SubObjectCodeCurrent subObjectCode);

    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber);

    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber);

    /**
     * Gets the accountNumber.
     * 
     * @return accountNumber
     */
    public String getAccountNumber();

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * Gets the chartOfAccountsCode.
     * 
     * @return chartOfAccountsCode
     */
    public String getChartOfAccountsCode();

    /**
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode);

    /**
     * Gets the financialObjectCode.
     * 
     * @return financialObjectCode
     */
    public String getFinancialObjectCode();

    /**
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode);

    /**
     * Gets the financialSubObjectCode.
     * 
     * @return financialSubObjectCode
     */
    public String getFinancialSubObjectCode();

    /**
     * @return Returns the financialDocumentLineTypeCode.
     */
    public String getFinancialDocumentLineTypeCode();

    /**
     * @param financialDocumentLineTypeCode The financialDocumentLineTypeCode to set.
     */
    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode);

    /**
     * @return Returns the project.
     */
    public ProjectCode getProject();

    /**
     * @param project The project to set.
     * @deprecated
     */
    public void setProject(ProjectCode project);

    /**
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber);

    /**
     * Gets the subAccountNumber.
     * 
     * @return subAccountNumber
     */
    public String getSubAccountNumber();

    /**
     * @see org.kuali.rice.krad.bo.AccountingLine#isSourceAccountingLine()
     */
    public boolean isSourceAccountingLine();

    /**
     * @see org.kuali.rice.krad.bo.AccountingLine#isTargetAccountingLine()
     */
    public boolean isTargetAccountingLine();

}
