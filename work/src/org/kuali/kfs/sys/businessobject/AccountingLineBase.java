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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.PropertyConstants;

/**
 * This is the generic class which contains all the elements on a typical line of accounting elements. These are all the accounting
 * items necessary to create a pending entry to the G/L. All transaction documents will use this business object inherently.
 * 
 * 
 */
public abstract class AccountingLineBase extends PersistableBusinessObjectBase implements Serializable, AccountingLine {
    private static Logger LOG = Logger.getLogger(AccountingLineBase.class);

    //
    // Note: if you add any new instance fields here, you must add handling for them in the copyFrom and isLike methods
    //

    private String documentNumber;
    private Integer sequenceNumber; // relative to the grouping of acctng lines
    private Integer postingYear;
    private String budgetYear;
    private KualiDecimal amount;
    private String referenceOriginCode;
    private String referenceNumber;
    private String referenceTypeCode;
    private String overrideCode = AccountingLineOverride.CODE.NONE;
    private boolean accountExpiredOverride; // for the UI, persisted in overrideCode
    private boolean accountExpiredOverrideNeeded; // for the UI, not persisted
    private boolean objectBudgetOverride;
    private boolean objectBudgetOverrideNeeded;
    private String organizationReferenceId;
    private String debitCreditCode; // should only be set by the Journal Voucher or Auxiliary Voucher document
    private String encumbranceUpdateCode; // should only be set by the Journal Voucher document
    protected String ojbConcreteClass; // attribute needed for OJB polymorphism - do not alter!
    protected String financialDocumentLineDescription;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String subAccountNumber;
    private String financialSubObjectCode;
    private String projectCode;
    private String balanceTypeCode;
    private String objectTypeCode;

    // bo references
    private Chart chart;
    private Account account;
    private ObjectCode objectCode;
    private SubAccount subAccount;
    private SubObjCd subObjectCode;
    private ProjectCode project;
    private BalanceTyp balanceTyp;
    private ObjectType objectType; // should only be set by the Journal Voucher document
    private OriginationCode referenceOrigin;
    private DocumentType referenceType;

    /**
     * This constructor sets up empty instances for the dependent objects.
     */
    public AccountingLineBase() {
        setAmount(new KualiDecimal(0));
        chart = new Chart();
        account = new Account();
        objectCode = new ObjectCode();
        subAccount = new SubAccount();
        subObjectCode = new SubObjCd();
        project = new ProjectCode();
        postingYear = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
        objectCode.setUniversityFiscalYear(postingYear);
        // all Financial Transaction Processing accounting lines (those extending from this) should use a balance type
        // of Actual, except for JV which allows a choice and PE which uses "PE"
        balanceTyp = SpringServiceLocator.getBalanceTypService().getActualBalanceTyp();
        objectType = new ObjectType();
    }


    /**
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * @param chart The chartOfAccountsCode to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @return Returns the amount.
     */
    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return Returns the balanceTyp.
     */
    public BalanceTyp getBalanceTyp() {
        return balanceTyp;
    }

    /**
     * @param balanceTyp The balanceTyp to set.
     * @deprecated
     */
    public void setBalanceTyp(BalanceTyp balanceTyp) {
        this.balanceTyp = balanceTyp;
    }

    /**
     * @return Returns the objectCode.
     */
    public ObjectCode getObjectCode() {
        return objectCode;
    }

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * @return Returns the referenceOriginCode.
     */
    public String getReferenceOriginCode() {
        return referenceOriginCode;
    }

    /**
     * @param originCode The referenceOriginCode to set.
     */
    public void setReferenceOriginCode(String originCode) {
        this.referenceOriginCode = originCode;
    }

    /**
     * This method returns the object related to referenceOriginCode
     * 
     * @return referenceOrigin
     */
    public OriginationCode getReferenceOrigin() {
        return referenceOrigin;
    }

    /**
     * This method sets the referenceOrigin object, this is only to be used by OJB
     * 
     * @param referenceOrigin
     * @deprecated
     */
    public void setReferenceOrigin(OriginationCode referenceOrigin) {
        this.referenceOrigin = referenceOrigin;
    }

    /**
     * This method returns the referenceType associated with the referenceTypeCode
     * 
     * @return referenceType
     */
    public DocumentType getReferenceType() {
        return referenceType;
    }

    /**
     * This method sets the referenceType, this is only to be used by OJB
     * 
     * @param referenceType
     * @deprecated
     */
    public void setReferenceType(DocumentType referenceType) {
        this.referenceType = referenceType;
    }


    /**
     * @return Returns the organizationReferenceId.
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * @return Returns the overrideCode.
     */
    public String getOverrideCode() {
        return overrideCode;
    }

    /**
     * @param overrideCode The overrideCode to set.
     */
    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }

    /**
     * @return Returns the postingYear.
     */
    public Integer getPostingYear() {
        return postingYear;
    }

    /**
     * @param postingYear The postingYear to set.
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * @return Returns the projectCode.
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * @return Returns the referenceNumber.
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * @param referenceNumber The referenceNumber to set.
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return Returns the referenceTypeCode.
     */
    public String getReferenceTypeCode() {
        return referenceTypeCode;
    }

    /**
     * @param referenceTypeCode The referenceTypeCode to set.
     */
    public void setReferenceTypeCode(String referenceTypeCode) {
        this.referenceTypeCode = referenceTypeCode;
    }

    /**
     * @return Returns the sequenceNumber.
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @return Returns the subObjectCode.
     */
    public SubObjCd getSubObjectCode() {
        return subObjectCode;
    }

    /**
     * @param subObjectCode The subObjectCode to set.
     * @deprecated
     */
    public void setSubObjectCode(SubObjCd subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * This method retrieves the debit/credit code for the accounting line. This method will only return a not null value for a
     * Journal Voucher document.
     * 
     * @return A String code.
     */
    public String getDebitCreditCode() {
        return debitCreditCode;
    }

    /**
     * This method sets the debit/credit code for the accounting line. This method should only be used for a Journal Voucher
     * document.
     * 
     * @param debitCreditCode
     */
    public void setDebitCreditCode(String debitCreditCode) {
        this.debitCreditCode = debitCreditCode;
    }

    /**
     * This method retrieves the encumbrance update code for the accounting line. This method will only return a not null value for
     * a Journal Voucher document.
     * 
     * @return A String code.
     */
    public String getEncumbranceUpdateCode() {
        return encumbranceUpdateCode;
    }

    /**
     * This method sets the debit/credit code for the accounting line. This method should only be used for a Journal Voucher
     * document.
     * 
     * @param encumbranceUpdateCode
     */
    public void setEncumbranceUpdateCode(String encumbranceUpdateCode) {
        this.encumbranceUpdateCode = encumbranceUpdateCode;
    }

    /**
     * This method retrieves the ObjectType for the accounting line. This method will only return a not null value for a Journal
     * Voucher document.
     * 
     * @return An ObjectType instance.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * This method sets the ObjectType for the accounting line. This method should only be used for a Journal Voucher document.
     * 
     * @param objectType
     * @deprecated
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }


    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @return Returns the balanceTypeCode.
     */
    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }

    /**
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * @return Returns the objectTypeCode.
     */
    public String getObjectTypeCode() {
        return objectTypeCode;
    }

    /**
     * @param objectTypeCode The objectTypeCode to set.
     */
    public void setObjectTypeCode(String objectTypeCode) {
        this.objectTypeCode = objectTypeCode;
    }

    /**
     * @return Returns the ojbConcreteClass.
     */
    public String getOjbConcreteClass() {
        return ojbConcreteClass;
    }

    /**
     * @param ojbConcreteClass The ojbConcreteClass to set.
     */
    public void setOjbConcreteClass(String ojbConcreteClass) {
        this.ojbConcreteClass = ojbConcreteClass;
    }

    /**
     * @return Returns the project.
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * @param project The project to set.
     * @deprecated
     */
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @return Returns the financialDocumentLineDescription.
     */
    public String getFinancialDocumentLineDescription() {
        return financialDocumentLineDescription;
    }

    /**
     * @param financialDocumentLineDescription The financialDocumentLineDescription to set.
     */
    public void setFinancialDocumentLineDescription(String financialDocumentLineDescription) {
        this.financialDocumentLineDescription = financialDocumentLineDescription;
    }

    /**
     * @return Returns the budgetYear.
     */
    public String getBudgetYear() {
        return budgetYear;
    }

    /**
     * @param budgetYear The budgetYear to set.
     */
    public void setBudgetYear(String budgetYear) {
        this.budgetYear = budgetYear;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(PropertyConstants.DOCUMENT_NUMBER, documentNumber);

        m.put("sequenceNumber", sequenceNumber);
        m.put("postingYear", postingYear);
        m.put("amount", amount);
        m.put("debitCreditCode", debitCreditCode);
        m.put("encumbranceUpdateCode", encumbranceUpdateCode);
        m.put("financialDocumentLineDescription", financialDocumentLineDescription);

        m.put("chart", getChartOfAccountsCode());
        m.put("account", getAccountNumber());
        m.put("objectCode", getFinancialObjectCode());
        m.put("subAccount", getSubAccountNumber());
        m.put("subObjectCode", getFinancialSubObjectCode());
        m.put("projectCode", getProjectCode());
        m.put("balanceTyp", getBalanceTypeCode());
        m.put("objectType", getObjectTypeCode());

        m.put("orgRefId", getOrganizationReferenceId());

        return m;
    }

    /**
     * @see org.kuali.core.bo.AccountingLine#isSourceAccountingLine()
     */
    public boolean isSourceAccountingLine() {
        return (this instanceof SourceAccountingLine);
    }

    /**
     * @see org.kuali.core.bo.AccountingLine#isTargetAccountingLine()
     */
    public boolean isTargetAccountingLine() {
        return (this instanceof TargetAccountingLine);
    }


    /**
     * @see org.kuali.core.bo.AccountingLine#getAccountKey()
     */
    public String getAccountKey() {
        String key = getChartOfAccountsCode() + ":" + getAccountNumber();
        return key;
    }


    /**
     * @see org.kuali.core.bo.AccountingLine#copyFrom(org.kuali.core.bo.AccountingLine)
     */
    public void copyFrom(AccountingLine other) {
        if (other == null) {
            throw new IllegalArgumentException("invalid (null) other");
        }

        if (this != other) {
            // primitive fields
            setSequenceNumber(other.getSequenceNumber());
            setDocumentNumber(other.getDocumentNumber());
            setPostingYear(other.getPostingYear());
            setAmount(other.getAmount());
            setReferenceOriginCode(other.getReferenceOriginCode());
            setReferenceNumber(other.getReferenceNumber());
            setReferenceTypeCode(other.getReferenceTypeCode());
            setOverrideCode(other.getOverrideCode());
            setOrganizationReferenceId(other.getOrganizationReferenceId());
            setDebitCreditCode(other.getDebitCreditCode());
            setEncumbranceUpdateCode(other.getEncumbranceUpdateCode());
            setOjbConcreteClass(other.getOjbConcreteClass());
            setFinancialDocumentLineDescription(other.getFinancialDocumentLineDescription());
            setAccountExpiredOverride(other.getAccountExpiredOverride());
            setAccountExpiredOverrideNeeded(other.getAccountExpiredOverrideNeeded());
            setObjectBudgetOverride(other.isObjectBudgetOverride());
            setObjectBudgetOverrideNeeded(other.isObjectBudgetOverrideNeeded());

            // foreign keys
            setChartOfAccountsCode(other.getChartOfAccountsCode());
            setAccountNumber(other.getAccountNumber());
            setFinancialObjectCode(other.getFinancialObjectCode());
            setSubAccountNumber(other.getSubAccountNumber());
            setFinancialSubObjectCode(other.getFinancialSubObjectCode());
            setProjectCode(other.getProjectCode());
            setBalanceTypeCode(other.getBalanceTypeCode());
            setObjectTypeCode(other.getObjectTypeCode());

            // object references
            setChart((Chart) ObjectUtils.deepCopy(other.getChart()));
            setAccount((Account) ObjectUtils.deepCopy(other.getAccount()));
            setObjectCode((ObjectCode) ObjectUtils.deepCopy(other.getObjectCode()));
            setSubAccount((SubAccount) ObjectUtils.deepCopy(other.getSubAccount()));
            setSubObjectCode((SubObjCd) ObjectUtils.deepCopy(other.getSubObjectCode()));
            setProject((ProjectCode) ObjectUtils.deepCopy(other.getProject()));
            setBalanceTyp((BalanceTyp) ObjectUtils.deepCopy(other.getBalanceTyp()));
            setObjectType((ObjectType) ObjectUtils.deepCopy(other.getObjectType()));
        }
    }


    /**
     * @see org.kuali.core.bo.AccountingLine#isLike(org.kuali.core.bo.AccountingLine)
     */
    public boolean isLike(AccountingLine other) {
        boolean isLike = false;

        if (other != null) {
            if (other == this) {
                isLike = true;
            }
            else {
                Map thisValues = this.getValuesMap();
                Map otherValues = other.getValuesMap();

                isLike = thisValues.equals(otherValues);

                if (!isLike && LOG.isDebugEnabled()) {
                    StringBuffer inequalities = new StringBuffer();
                    boolean first = true;

                    for (Iterator i = thisValues.keySet().iterator(); i.hasNext();) {
                        String key = (String) i.next();

                        Object thisValue = thisValues.get(key);
                        Object otherValue = otherValues.get(key);
                        if (!org.apache.commons.lang.ObjectUtils.equals(thisValue, otherValue)) {
                            inequalities.append(key + "(" + thisValue + " != " + otherValue + ")");

                            if (first) {
                                first = false;
                            }
                            else {
                                inequalities.append(",");
                            }
                        }
                    }

                    LOG.debug("inequalities: " + inequalities);
                }
            }
        }

        return isLike;
    }

    /**
     * @see AccountingLine#getAccountExpiredOverride()
     */
    public boolean getAccountExpiredOverride() {
        return accountExpiredOverride;
    }

    /**
     * @see AccountingLine#setAccountExpiredOverride(boolean)
     */
    public void setAccountExpiredOverride(boolean b) {
        accountExpiredOverride = b;
    }

    /**
     * @see AccountingLine#getAccountExpiredOverrideNeeded()
     */
    public boolean getAccountExpiredOverrideNeeded() {
        return accountExpiredOverrideNeeded;
    }

    /**
     * @see AccountingLine#setAccountExpiredOverrideNeeded(boolean)
     */
    public void setAccountExpiredOverrideNeeded(boolean b) {
        accountExpiredOverrideNeeded = b;
    }

    /**
     * @return Returns the objectBudgetOverride.
     */
    public boolean isObjectBudgetOverride() {
        return objectBudgetOverride;
    }

    /**
     * @param objectBudgetOverride The objectBudgetOverride to set.
     */
    public void setObjectBudgetOverride(boolean objectBudgetOverride) {
        this.objectBudgetOverride = objectBudgetOverride;
    }

    /**
     * @return Returns the objectBudgetOverrideNeeded.
     */
    public boolean isObjectBudgetOverrideNeeded() {
        return objectBudgetOverrideNeeded;
    }

    /**
     * @param objectBudgetOverrideNeeded The objectBudgetOverrideNeeded to set.
     */
    public void setObjectBudgetOverrideNeeded(boolean objectBudgetOverrideNeeded) {
        this.objectBudgetOverrideNeeded = objectBudgetOverrideNeeded;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map
     */
    public Map getValuesMap() {
        Map simpleValues = new HashMap();

        simpleValues.put("sequenceNumber", getSequenceNumber());
        simpleValues.put(PropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        simpleValues.put("postingYear", getPostingYear());
        simpleValues.put("amount", getAmount());
        simpleValues.put("referenceOriginCode", getReferenceOriginCode());
        simpleValues.put("referenceNumber", getReferenceNumber());
        simpleValues.put("referenceTypeCode", getReferenceTypeCode());
        simpleValues.put("overrideCode", getOverrideCode());
        // The override booleans are not in the map because they should not cause isLike() to fail and generate update events.
        simpleValues.put("organizationReferenceId", getOrganizationReferenceId());
        simpleValues.put("debitCreditCode", getDebitCreditCode());
        simpleValues.put("encumbranceUpdateCode", getEncumbranceUpdateCode());
        simpleValues.put("ojbConcreteClass", getOjbConcreteClass());
        simpleValues.put("financialDocumentLineDescription", getFinancialDocumentLineDescription());

        simpleValues.put("chartOfAccountsCode", getChartOfAccountsCode());
        simpleValues.put("accountNumber", getAccountNumber());
        simpleValues.put("financialObjectCode", getFinancialObjectCode());
        simpleValues.put("subAccountNumber", getSubAccountNumber());
        simpleValues.put("financialSubObjectCode", getFinancialSubObjectCode());
        simpleValues.put("projectCode", getProjectCode());
        simpleValues.put("balanceTypeCode", getBalanceTypeCode());
        simpleValues.put("objectTypeCode", getObjectTypeCode());

        return simpleValues;
    }
}
