/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.bo;

import java.util.Map;

import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * An AccountLine contains all the accounting items typically necessary to create a pending entry to the G/L. All transaction
 * documents will use this business object inherently. Specific accounting line business rules should exist not in this
 * implementation, but rather in the document business object that uses it.
 * 
 * 
 */
public interface AccountingLine extends PersistableBusinessObject {

    /**
     * @return Returns the account.
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
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber();

    /**
     * @return Returns the amount.
     */
    public KualiDecimal getAmount();

    /**
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount);

    /**
     * @return Returns the balanceTyp.
     */
    public BalanceTyp getBalanceTyp();

    /**
     * @param balanceTyp The balanceTyp to set.
     * @deprecated
     */
    public void setBalanceTyp(BalanceTyp balanceTyp);

    /**
     * @return Returns the objectCode.
     */
    public ObjectCode getObjectCode();

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    public void setObjectCode(ObjectCode objectCode);

    /**
     * @return Returns the referenceOriginCode.
     */
    public String getReferenceOriginCode();

    /**
     * @param originCode The referenceOriginCode to set.
     */
    public void setReferenceOriginCode(String originCode);

    /**
     * This method returns the object related to referenceOriginCode
     * 
     * @return referenceOrigin
     */
    public OriginationCode getReferenceOrigin();

    /**
     * This method sets the referenceOrigin object, this is only to be used by OJB
     * 
     * @param referenceOrigin
     * @deprecated
     */
    public void setReferenceOrigin(OriginationCode referenceOrigin);

    /**
     * This method returns the referenceType associated with the referenceTypeCode
     * 
     * @return referenceType
     */
    public DocumentType getReferenceType();

    /**
     * This method sets the referenceType, this is only to be used by OJB
     * 
     * @param referenceType
     * @deprecated
     */
    public void setReferenceType(DocumentType referenceType);

    /**
     * @return Returns the organizationReferenceId.
     */
    public String getOrganizationReferenceId();

    /**
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId);

    /**
     * @return Returns the overrideCode.
     */
    public String getOverrideCode();

    /**
     * @param overrideCode The overrideCode to set.
     */
    public void setOverrideCode(String overrideCode);

    /**
     * @return Returns the postingYear.
     */
    public Integer getPostingYear();

    /**
     * @param postingYear The postingYear to set.
     */
    public void setPostingYear(Integer postingYear);

    /**
     * @return Returns the projectCode.
     */
    public String getProjectCode();

    /**
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode);

    /**
     * @return Returns the referenceNumber.
     */
    public String getReferenceNumber();

    /**
     * @param referenceNumber The referenceNumber to set.
     */
    public void setReferenceNumber(String referenceNumber);

    /**
     * @return Returns the referenceTypeCode.
     */
    public String getReferenceTypeCode();

    /**
     * @param referenceTypeCode The referenceTypeCode to set.
     */
    public void setReferenceTypeCode(String referenceTypeCode);

    /**
     * @return Returns the sequenceNumber.
     */
    public Integer getSequenceNumber();

    /**
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(Integer sequenceNumber);

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
    public SubObjCd getSubObjectCode();

    /**
     * @param subObjectCode The subObjectCode to set.
     * @deprecated
     */
    public void setSubObjectCode(SubObjCd subObjectCode);

    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber);

    /**
     * This method retrieves the debit/credit code for the accounting line. This method will only return a not null value for a
     * Journal Voucher document.
     * 
     * @return A String code.
     */
    public String getDebitCreditCode();

    /**
     * This method sets the debit/credit code for the accounting line. This method should only be used for a Journal Voucher
     * document.
     * 
     * @param debitCreditCode
     */
    public void setDebitCreditCode(String debitCreditCode);

    /**
     * This method retrieves the encumbrance update code for the accounting line. This method will only return a not null value for
     * a Journal Voucher document.
     * 
     * @return A String code.
     */
    public String getEncumbranceUpdateCode();

    /**
     * This method sets the debit/credit code for the accounting line. This method should only be used for a Journal Voucher
     * document.
     * 
     * @param encumbranceUpdateCode
     */
    public void setEncumbranceUpdateCode(String encumbranceUpdateCode);

    /**
     * This method retrieves the ObjectType for the accounting line. This method will only return a not null value for a Journal
     * Voucher document.
     * 
     * @return An ObjectType instance.
     */
    public ObjectType getObjectType();

    /**
     * This method sets the ObjectType for the accounting line. This method should only be used for a Journal Voucher document.
     * 
     * @param objectType
     * @deprecated
     */
    public void setObjectType(ObjectType objectType);

    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber();

    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber);

    /**
     * @return Returns the balanceTypeCode.
     */
    public String getBalanceTypeCode();

    /**
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    public void setBalanceTypeCode(String balanceTypeCode);

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode();

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode();

    /**
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode);

    /**
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode();

    /**
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode);

    /**
     * @return Returns the objectTypeCode.
     */
    public String getObjectTypeCode();

    /**
     * @param objectTypeCode The objectTypeCode to set.
     */
    public void setObjectTypeCode(String objectTypeCode);

    /**
     * @return Returns the ojbConcreteClass.
     */
    public String getOjbConcreteClass();

    /**
     * @param ojbConcreteClass The ojbConcreteClass to set.
     */
    public void setOjbConcreteClass(String ojbConcreteClass);

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
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber();

    /**
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber);

    /**
     * @return Returns the financialDocumentLineDescription.
     */
    public abstract String getFinancialDocumentLineDescription();

    /**
     * @param financialDocumentLineDescription The financialDocumentLineDescription to set.
     */
    public abstract void setFinancialDocumentLineDescription(String financialDocumentLineDescription);

    /**
     * @see org.kuali.core.bo.AccountingLine#isSourceAccountingLine()
     */
    public boolean isSourceAccountingLine();

    /**
     * @see org.kuali.core.bo.AccountingLine#isTargetAccountingLine()
     */
    public boolean isTargetAccountingLine();

    /**
     * @return Returns the budgetYear.
     */
    public String getBudgetYear();

    /**
     * @param budgetYear The budgetYear to set.
     */
    public void setBudgetYear(String budgetYear);


    /**
     * @param other
     * @return true if this AccountingLine has the same primitive field values as the given one
     */
    public boolean isLike(AccountingLine other);

    /**
     * Overwrites the fields of this AccountingLine with the values of the corresponding fields of the given AccountingLine.
     * Reference field values are in this instance are overwritten with deepCopies of the reference fields of the given
     * AccountingLine.
     * 
     * @param other
     */
    public void copyFrom(AccountingLine other);

    /**
     * Convenience method to make the primitive account fields from this AccountingLine easier to compare to the account fields of
     * another AccountingLine or of an Account
     * 
     * @return String representing the account associated with this AccountingLine
     */
    public String getAccountKey();

    /**
     * This indicates the account expired component of this AccountingLine's overrideCode. It provides a DataDictionary attribute
     * for the UI, but is not persisted itself.
     * 
     * @return accountExpiredOverride
     */
    boolean getAccountExpiredOverride();

    /**
     * @see #getAccountExpiredOverride()
     * @param b the accountExpiredOverride to set
     */
    public void setAccountExpiredOverride(boolean b);

    /**
     * This indicates the account expired override is needed (because this AccountingLine's Account is expired). It tells the UI to
     * display the accountExpiredOverride attribute, but is not persisted itself.
     * 
     * @return accountExpiredOverride
     */
    public boolean getAccountExpiredOverrideNeeded();

    /**
     * @see #getAccountExpiredOverrideNeeded()
     * @param b the accountExpiredOverrideNeeded to set
     */
    public void setAccountExpiredOverrideNeeded(boolean b);

    /**
     * This indicates the object budget component of this AccountingLine's overrideCode. It provides a DataDictionary attribute for
     * the UI, but is not persisted itself.
     * 
     * @return objectBudgetOverride
     */
    public boolean isObjectBudgetOverride();

    /**
     * @see #isObjectBudgetOverride()
     * @param b the objectBudgetOverride to set
     */
    public void setObjectBudgetOverride(boolean b);

    /**
     * This indicates the object budget override is needed (because this AccountingLine's Account is expired). It tells the UI to
     * display the objectBudgetOverride attribute, but is not persisted itself.
     * 
     * @return boolean
     */
    public boolean isObjectBudgetOverrideNeeded();

    /**
     * @see #isObjectBudgetOverrideNeeded()
     * @param b the objectBudgetOverride to set
     */
    public void setObjectBudgetOverrideNeeded(boolean b);

    /**
     * Returns a Map with the accounting line primitive field names as the key of the map and the primitive values as the value.
     * 
     * @return Map
     */
    public Map getValuesMap();
}
