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
package org.kuali.kfs.sys.businessobject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.fp.businessobject.SalesTax;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OriginationCodeService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the generic class which contains all the elements on a typical line of accounting elements. These are all the accounting
 * items necessary to create a pending entry to the G/L. All transaction documents will use this business object inherently.
 */
public abstract class AccountingLineBase extends PersistableBusinessObjectBase implements Serializable, AccountingLine, GeneralLedgerPendingEntrySourceDetail {
    private static final Logger LOG = Logger.getLogger(AccountingLineBase.class);

    protected String documentNumber;
    protected Integer sequenceNumber; // relative to the grouping of acctng lines
    protected Integer postingYear;
    protected KualiDecimal amount;
    protected String referenceOriginCode;
    protected String referenceNumber;
    protected String referenceTypeCode;
    protected String overrideCode = AccountingLineOverride.CODE.NONE;
    protected boolean accountExpiredOverride; // for the UI, persisted in overrideCode
    protected boolean accountExpiredOverrideNeeded; // for the UI, not persisted
    protected boolean nonFringeAccountOverride; // for the UI, persisted in overrideCode
    protected boolean nonFringeAccountOverrideNeeded; // for the UI, not persisted
    protected boolean objectBudgetOverride;
    protected boolean objectBudgetOverrideNeeded;
    protected String organizationReferenceId;
    protected String debitCreditCode; // should only be set by the Journal Voucher or Auxiliary Voucher document
    protected String encumbranceUpdateCode; // should only be set by the Journal Voucher document
    protected String financialDocumentLineTypeCode;
    protected String financialDocumentLineDescription;
    protected boolean salesTaxRequired;

    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String financialObjectCode;
    protected String subAccountNumber;
    protected String financialSubObjectCode;
    protected String projectCode;
    protected String balanceTypeCode;

    // bo references
    protected Chart chart;
    protected Account account;
    protected ObjectCode objectCode;
    protected SubAccount subAccount;
    protected SubObjectCode subObjectCode;
    protected ProjectCode project;
    protected BalanceType balanceTyp;
    protected OriginationCode referenceOrigin;
    protected DocumentTypeEBO referenceFinancialSystemDocumentTypeCode;
    protected SalesTax salesTax;

    /**
     * This constructor sets up empty instances for the dependent objects.
     */
    public AccountingLineBase() {
        setAmount(KualiDecimal.ZERO);
        chart = new Chart();
        account = new Account();
        objectCode = new ObjectCode();
        subAccount = new SubAccount();
        subObjectCode = new SubObjectCode();
        project = new ProjectCode();

        balanceTyp = new BalanceType();
        // salesTax = new SalesTax();
        salesTaxRequired = false;
    }


    /**
     * @return Returns the account.
     */
    @Override
    public Account getAccount() {
        if ( StringUtils.isBlank(getChartOfAccountsCode()) || StringUtils.isBlank(getAccountNumber())) {
            account = new Account();
        } else if ( account == null
                || !StringUtils.equals(account.getChartOfAccountsCode(), getChartOfAccountsCode())
                || !StringUtils.equals(account.getAccountNumber(), getAccountNumber()) ) {
            account = SpringContext.getBean(AccountService.class).getByPrimaryIdWithCaching(getChartOfAccountsCode(), getAccountNumber());
        }
        return account;
    }

    /**
     * @param account The account to set.
     * @deprecated
     */
    @Deprecated
    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    @Override
    public Chart getChart() {
        if ( StringUtils.isBlank(getChartOfAccountsCode()) ) {
            chart = new Chart();
        } else if ( chart == null || !StringUtils.equals(chart.getChartOfAccountsCode(), getChartOfAccountsCode()) ) {
            chart = SpringContext.getBean(ChartService.class).getByPrimaryId(getChartOfAccountsCode());
        }
        return chart;
    }

    /**
     * @param chart The chartOfAccountsCode to set.
     * @deprecated
     */
    @Deprecated
    @Override
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @return Returns the documentNumber.
     */
    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @return Returns the amount.
     */
    @Override
    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount The amount to set.
     */
    @Override
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return Returns the balanceTyp.
     */
    @Override
    public BalanceType getBalanceTyp() {
        if ( StringUtils.isBlank(getBalanceTypeCode()) ) {
            balanceTyp = new BalanceType();
        } else if ( balanceTyp == null || !StringUtils.equals(balanceTyp.getCode(), getBalanceTypeCode()) ) {
            balanceTyp = SpringContext.getBean(BalanceTypeService.class).getBalanceTypeByCode(getBalanceTypeCode());
        }
        return balanceTyp;
    }

    /**
     * @param balanceTyp The balanceTyp to set.
     * @deprecated
     */
    @Deprecated
    @Override
    public void setBalanceTyp(BalanceType balanceTyp) {
        this.balanceTyp = balanceTyp;
    }

    /**
     * @return Returns the objectCode.
     */
    @Override
    public ObjectCode getObjectCode() {
        if ( StringUtils.isBlank(getFinancialObjectCode()) || getPostingYear() == null || StringUtils.isBlank(getChartOfAccountsCode()) ) {
            objectCode = new ObjectCode();
        } else if ( objectCode == null
                || !StringUtils.equals(objectCode.getFinancialObjectCode(), getFinancialObjectCode())
                || !StringUtils.equals(objectCode.getChartOfAccountsCode(), getChartOfAccountsCode())
                || getPostingYear().equals(objectCode.getUniversityFiscalYear()) ) {
            objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryIdWithCaching( getPostingYear(), getChartOfAccountsCode(), getFinancialObjectCode());
            if ( objectCode == null ) {
                objectCode = new ObjectCode();
            }
        }

        return objectCode;
    }

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    @Deprecated
    @Override
    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * @return Returns the referenceOriginCode.
     */
    @Override
    public String getReferenceOriginCode() {
        return referenceOriginCode;
    }

    /**
     * @param originCode The referenceOriginCode to set.
     */
    @Override
    public void setReferenceOriginCode(String originCode) {
        this.referenceOriginCode = originCode;
    }

    /**
     * This method returns the object related to referenceOriginCode
     *
     * @return referenceOrigin
     */
    @Override
    public OriginationCode getReferenceOrigin() {
        if ( StringUtils.isBlank(getReferenceOriginCode()) ) {
            referenceOrigin = new OriginationCode();
        } else if ( referenceOrigin == null || !StringUtils.equals(referenceOrigin.getFinancialSystemOriginationCode(), getReferenceOriginCode()) ) {
            referenceOrigin = SpringContext.getBean(OriginationCodeService.class).getByPrimaryKey(getReferenceOriginCode());
        }
        return referenceOrigin;
    }

    /**
     * This method sets the referenceOrigin object, this is only to be used by OJB
     *
     * @param referenceOrigin
     * @deprecated
     */
    @Deprecated
    @Override
    public void setReferenceOrigin(OriginationCode referenceOrigin) {
        this.referenceOrigin = referenceOrigin;
    }

    /**
     * Gets the referenceFinancialSystemDocumentTypeCode attribute.
     * @return Returns the referenceFinancialSystemDocumentTypeCode.
     */
    @Override
    public DocumentTypeEBO getReferenceFinancialSystemDocumentTypeCode() {
        if ( StringUtils.isBlank( referenceTypeCode ) ) {
            referenceFinancialSystemDocumentTypeCode = null;
        } else {
            if ( referenceFinancialSystemDocumentTypeCode == null || !StringUtils.equals(referenceTypeCode, referenceFinancialSystemDocumentTypeCode.getName() ) ) {
                org.kuali.rice.kew.api.doctype.DocumentType temp = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(referenceTypeCode);
                if ( temp != null ) {
                    referenceFinancialSystemDocumentTypeCode = DocumentType.from( temp );
                } else {
                    referenceFinancialSystemDocumentTypeCode = null;
                }
            }
        }
        return referenceFinancialSystemDocumentTypeCode;
    }

    /**
     * @return Returns the organizationReferenceId.
     */
    @Override
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    @Override
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * @return Returns the overrideCode.
     */
    @Override
    public String getOverrideCode() {
        return overrideCode;
    }

    /**
     * @param overrideCode The overrideCode to set.
     */
    @Override
    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }

    /**
     * @return Returns the postingYear.
     */
    @Override
    public Integer getPostingYear() {
        if (postingYear == null) {
            postingYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        }
        return postingYear;
    }

    /**
     * @param postingYear The postingYear to set.
     */
    @Override
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * @return Returns the projectCode.
     */
    @Override
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @param projectCode The projectCode to set.
     */
    @Override
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * @return Returns the referenceNumber.
     */
    @Override
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * @param referenceNumber The referenceNumber to set.
     */
    @Override
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return Returns the referenceTypeCode.
     */
    @Override
    public String getReferenceTypeCode() {
        return referenceTypeCode;
    }

    /**
     * @param referenceTypeCode The referenceTypeCode to set.
     */
    @Override
    public void setReferenceTypeCode(String referenceTypeCode) {
        this.referenceTypeCode = referenceTypeCode;
    }

    /**
     * @return Returns the sequenceNumber.
     */
    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @param sequenceNumber The sequenceNumber to set.
     */
    @Override
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @return Returns the subAccount.
     */
    @Override
    public SubAccount getSubAccount() {
        if ( StringUtils.isBlank(getChartOfAccountsCode()) || StringUtils.isBlank(getAccountNumber()) || StringUtils.isBlank(getSubAccountNumber()) ) {
            subAccount = new SubAccount();
        } else if ( subAccount == null
                || !StringUtils.equals(subAccount.getChartOfAccountsCode(), getChartOfAccountsCode())
                || !StringUtils.equals(subAccount.getAccountNumber(), getAccountNumber())
                || !StringUtils.equals(subAccount.getSubAccountNumber(), getSubAccountNumber())) {
            subAccount = SpringContext.getBean(SubAccountService.class).getByPrimaryIdWithCaching(getChartOfAccountsCode(), getAccountNumber(), getSubAccountNumber() );
        }
        return subAccount;
    }

    /**
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    @Deprecated
    @Override
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @return Returns the subObjectCode.
     */
    @Override
    public SubObjectCode getSubObjectCode() {
        if ( StringUtils.isBlank(getFinancialObjectCode()) || getPostingYear() == null || StringUtils.isBlank(getChartOfAccountsCode()) || StringUtils.isBlank(getAccountNumber()) || StringUtils.isBlank(getFinancialSubObjectCode()) ) {
            subObjectCode = new SubObjectCode();
        } else if ( subObjectCode == null
                || !StringUtils.equals(subObjectCode.getFinancialSubObjectCode(), getFinancialSubObjectCode())
                || !StringUtils.equals(subObjectCode.getFinancialObjectCode(), getFinancialObjectCode())
                || !StringUtils.equals(subObjectCode.getAccountNumber(), getAccountNumber())
                || !StringUtils.equals(subObjectCode.getChartOfAccountsCode(), getChartOfAccountsCode())
                || getPostingYear().equals(subObjectCode.getUniversityFiscalYear()) ) {
            subObjectCode = SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId( getPostingYear(), getChartOfAccountsCode(), getAccountNumber(), getFinancialObjectCode(), getFinancialSubObjectCode() );
        }
        return subObjectCode;
    }

    /**
     * @param subObjectCode The subObjectCode to set.
     * @deprecated
     */
    @Deprecated
    @Override
    public void setSubObjectCode(SubObjectCode subObjectCode) {
        this.subObjectCode = subObjectCode;
    }


    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#getSalesTax()
     */
    @Override
    public SalesTax getSalesTax() {
        return salesTax;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#setSalesTax(org.kuali.kfs.fp.businessobject.SalesTax)
     * @deprecated
     */
    @Deprecated
    @Override
    public void setSalesTax(SalesTax salesTax) {
        this.salesTax = salesTax;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#isSalesTaxRequired()
     */
    @Override
    public boolean isSalesTaxRequired() {
        return salesTaxRequired;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#setSalesTaxRequired(boolean)
     */
    @Override
    public void setSalesTaxRequired(boolean salesTaxRequired) {
        this.salesTaxRequired = salesTaxRequired;
    }


    /**
     * @param documentNumber The documentNumber to set.
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * This method retrieves the debit/credit code for the accounting line. This method will only return a not null value for a
     * Journal Voucher document.
     *
     * @return A String code.
     */
    @Override
    public String getDebitCreditCode() {
        return debitCreditCode;
    }

    /**
     * This method sets the debit/credit code for the accounting line. This method should only be used for a Journal Voucher
     * document.
     *
     * @param debitCreditCode
     */
    @Override
    public void setDebitCreditCode(String debitCreditCode) {
        this.debitCreditCode = debitCreditCode;
    }

    /**
     * This method retrieves the encumbrance update code for the accounting line. This method will only return a not null value for
     * a Journal Voucher document.
     *
     * @return A String code.
     */
    @Override
    public String getEncumbranceUpdateCode() {
        return encumbranceUpdateCode;
    }

    /**
     * This method sets the debit/credit code for the accounting line. This method should only be used for a Journal Voucher
     * document.
     *
     * @param encumbranceUpdateCode
     */
    @Override
    public void setEncumbranceUpdateCode(String encumbranceUpdateCode) {
        this.encumbranceUpdateCode = encumbranceUpdateCode;
    }

    /**
     * This method retrieves the ObjectType for the accounting line. This method will only return a not null value for a Journal
     * Voucher document.
     *
     * @return An ObjectType instance.
     */
    @Override
    public ObjectType getObjectType() {
        if ( StringUtils.isNotBlank(getObjectTypeCode()) ) {
            return SpringContext.getBean(ObjectTypeService.class).getByPrimaryKey(getObjectTypeCode());
        }
        return null;
    }

    /**
     * @return Returns the accountNumber.
     */
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber The accountNumber to set.
     */
    @Override
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        // if accounts can't cross charts, set chart code whenever account number is set
        SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(this);
    }

    /**
     * @return Returns the balanceTypeCode.
     */
    @Override
    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }

    /**
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    @Override
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    @Override
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    @Override
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the financialObjectCode.
     */
    @Override
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * @param financialObjectCode The financialObjectCode to set.
     */
    @Override
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * @return Returns the financialSubObjectCode.
     */
    @Override
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    @Override
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * @return Returns the objectTypeCode.
     */
    @Override
    public String getObjectTypeCode() {
        return getObjectCode().getFinancialObjectTypeCode();
        }

    public void setObjectTypeCode( String objectTypeCode ) {
        // do nothing - just here to shut up the PojoFormBase about a missing setter
        }

    /**
     * @return Returns the financialDocumentLineTypeCode.
     */
    @Override
    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }

    /**
     * @param financialDocumentLineTypeCode The financialDocumentLineTypeCode to set.
     */
    @Override
    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }

    /**
     * @return Returns the project.
     */
    @Override
    public ProjectCode getProject() {
        if ( StringUtils.isBlank(getProjectCode()) ) {
            project = new ProjectCode();
        } else if ( project == null || !StringUtils.equals(project.getCode(), getProjectCode()) ) {
            project = SpringContext.getBean(ProjectCodeService.class).getByPrimaryId(getProjectCode());
        }
        return project;
    }

    /**
     * @param project The project to set.
     * @deprecated
     */
    @Deprecated
    @Override
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * @return Returns the subAccountNumber.
     */
    @Override
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber The subAccountNumber to set.
     */
    @Override
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @return Returns the financialDocumentLineDescription.
     */
    @Override
    public String getFinancialDocumentLineDescription() {
        return financialDocumentLineDescription;
    }

    /**
     * @param financialDocumentLineDescription The financialDocumentLineDescription to set.
     */
    @Override
    public void setFinancialDocumentLineDescription(String financialDocumentLineDescription) {
        this.financialDocumentLineDescription = financialDocumentLineDescription;
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLine#isSourceAccountingLine()
     */
    @Override
    public boolean isSourceAccountingLine() {
        return (this instanceof SourceAccountingLine);
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLine#isTargetAccountingLine()
     */
    @Override
    public boolean isTargetAccountingLine() {
        return (this instanceof TargetAccountingLine);
    }


    /**
     * @see org.kuali.rice.krad.bo.AccountingLine#getAccountKey()
     */
    @Override
    public String getAccountKey() {
        String key = getChartOfAccountsCode() + ":" + getAccountNumber();
        return key;
    }


    /**
     * @see org.kuali.rice.krad.bo.AccountingLine#copyFrom(org.kuali.rice.krad.bo.AccountingLine)
     */
    @Override
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
            setFinancialDocumentLineTypeCode(other.getFinancialDocumentLineTypeCode());
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

            // sales tax
            if (ObjectUtils.isNotNull(other.getSalesTax())) {
                SalesTax salesTax = getSalesTax();
                SalesTax origSalesTax = other.getSalesTax();
                if (salesTax != null) {
                    salesTax.setAccountNumber(origSalesTax.getAccountNumber());
                    salesTax.setChartOfAccountsCode(origSalesTax.getChartOfAccountsCode());
                    salesTax.setFinancialDocumentGrossSalesAmount(origSalesTax.getFinancialDocumentGrossSalesAmount());
                    salesTax.setFinancialDocumentTaxableSalesAmount(origSalesTax.getFinancialDocumentTaxableSalesAmount());
                    salesTax.setFinancialDocumentSaleDate(origSalesTax.getFinancialDocumentSaleDate());

                    // primary keys
                    salesTax.setDocumentNumber(other.getDocumentNumber());
                    salesTax.setFinancialDocumentLineNumber(other.getSequenceNumber());
                    salesTax.setFinancialDocumentLineTypeCode(other.getFinancialDocumentLineTypeCode());
                }
                else {
                    salesTax = origSalesTax;
                }
            }

            // object references
            setChart(other.getChart());
            setAccount(other.getAccount());
            setObjectCode(other.getObjectCode());
            setSubAccount(other.getSubAccount());
            setSubObjectCode(other.getSubObjectCode());
            setProject(other.getProject());
            setBalanceTyp(other.getBalanceTyp());
        }
    }


    /**
     * @see org.kuali.rice.krad.bo.AccountingLine#isLike(org.kuali.rice.krad.bo.AccountingLine)
     */
    @Override
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
                    StringBuilder inequalities = new StringBuilder();
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
    @Override
    public boolean getAccountExpiredOverride() {
        return accountExpiredOverride;
    }

    /**
     * @see AccountingLine#setAccountExpiredOverride(boolean)
     */
    @Override
    public void setAccountExpiredOverride(boolean b) {
        accountExpiredOverride = b;
    }

    /**
     * @see AccountingLine#getAccountExpiredOverrideNeeded()
     */
    @Override
    public boolean getAccountExpiredOverrideNeeded() {
        return accountExpiredOverrideNeeded;
    }

    /**
     * @see AccountingLine#setAccountExpiredOverrideNeeded(boolean)
     */
    @Override
    public void setAccountExpiredOverrideNeeded(boolean b) {
        accountExpiredOverrideNeeded = b;
    }

    /**
     * @return Returns the objectBudgetOverride.
     */
    @Override
    public boolean isObjectBudgetOverride() {
        return objectBudgetOverride;
    }

    /**
     * @param objectBudgetOverride The objectBudgetOverride to set.
     */
    @Override
    public void setObjectBudgetOverride(boolean objectBudgetOverride) {
        this.objectBudgetOverride = objectBudgetOverride;
    }

    /**
     * @return Returns the objectBudgetOverrideNeeded.
     */
    @Override
    public boolean isObjectBudgetOverrideNeeded() {
        return objectBudgetOverrideNeeded;
    }

    /**
     * @param objectBudgetOverrideNeeded The objectBudgetOverrideNeeded to set.
     */
    @Override
    public void setObjectBudgetOverrideNeeded(boolean objectBudgetOverrideNeeded) {
        this.objectBudgetOverrideNeeded = objectBudgetOverrideNeeded;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#isNonFringeAccountOverride()
     */
    @Override
    public boolean getNonFringeAccountOverride() {
        return nonFringeAccountOverride;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#setNonFringeAccountOverride(boolean)
     */
    @Override
    public void setNonFringeAccountOverride(boolean nonFringeAccountOverride) {
        this.nonFringeAccountOverride = nonFringeAccountOverride;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#isNonFringeAccountOverrideNeeded()
     */
    @Override
    public boolean getNonFringeAccountOverrideNeeded() {
        return nonFringeAccountOverrideNeeded;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#setNonFringeAccountOverrideNeeded(boolean)
     */
    @Override
    public void setNonFringeAccountOverrideNeeded(boolean nonFringeAccountOverrideNeeded) {
        this.nonFringeAccountOverrideNeeded = nonFringeAccountOverrideNeeded;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     *
     * @return Map
     */
    @Override
    public Map getValuesMap() {
        Map simpleValues = new HashMap();

        simpleValues.put("sequenceNumber", getSequenceNumber());
        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        simpleValues.put("postingYear", getPostingYear());
        simpleValues.put("amount", getAmount());
        simpleValues.put("referenceOriginCode", getReferenceOriginCode());
        simpleValues.put("referenceNumber", getReferenceNumber());
        simpleValues.put("referenceTypeCode", getReferenceTypeCode());
        // The override booleans are not in the map because they should not cause isLike() to fail and generate update events.
        simpleValues.put("organizationReferenceId", getOrganizationReferenceId());
        simpleValues.put("debitCreditCode", getDebitCreditCode());
        simpleValues.put("encumbranceUpdateCode", getEncumbranceUpdateCode());
        simpleValues.put("financialDocumentLineTypeCode", getFinancialDocumentLineTypeCode());
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

    /**
     * Override needed for PURAP GL entry creation (hjs) - please do not add "amount" to this method
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AccountingLine)) {
            return false;
        }
        AccountingLine accountingLine = (AccountingLine) obj;
        return new EqualsBuilder().append(this.chartOfAccountsCode, accountingLine.getChartOfAccountsCode()).append(this.accountNumber, accountingLine.getAccountNumber()).append(this.subAccountNumber, accountingLine.getSubAccountNumber()).append(this.financialObjectCode, accountingLine.getFinancialObjectCode()).append(this.financialSubObjectCode, accountingLine.getFinancialSubObjectCode()).append(this.projectCode, accountingLine.getProjectCode()).append(this.organizationReferenceId, accountingLine.getOrganizationReferenceId()).isEquals();
    }

    /**
     * Override needed for PURAP GL entry creation (hjs) - please do not add "amount" to this method
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 41).append(this.chartOfAccountsCode).append(this.accountNumber).append(this.subAccountNumber).append(this.financialObjectCode).append(this.financialSubObjectCode).append(this.projectCode).append(this.organizationReferenceId).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccountingLineBase [");
        if (documentNumber != null) {
            builder.append("documentNumber=").append(documentNumber).append(", ");
        }
        if (sequenceNumber != null) {
            builder.append("sequenceNumber=").append(sequenceNumber).append(", ");
        }
        if (postingYear != null) {
            builder.append("postingYear=").append(postingYear).append(", ");
        }
        if (amount != null) {
            builder.append("amount=").append(amount).append(", ");
        }
        if (debitCreditCode != null) {
            builder.append("debitCreditCode=").append(debitCreditCode).append(", ");
        }
        if (chartOfAccountsCode != null) {
            builder.append("chartOfAccountsCode=").append(chartOfAccountsCode).append(", ");
        }
        if (accountNumber != null) {
            builder.append("accountNumber=").append(accountNumber).append(", ");
        }
        if (subAccountNumber != null) {
            builder.append("subAccountNumber=").append(subAccountNumber).append(", ");
        }
        if (financialObjectCode != null) {
            builder.append("financialObjectCode=").append(financialObjectCode).append(", ");
        }
        if (financialSubObjectCode != null) {
            builder.append("financialSubObjectCode=").append(financialSubObjectCode).append(", ");
        }
        if (projectCode != null) {
            builder.append("projectCode=").append(projectCode).append(", ");
        }
        if (balanceTypeCode != null) {
            builder.append("balanceTypeCode=").append(balanceTypeCode);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void refreshReferenceObject(String referenceName) {
        if ( referenceName.equals("chart") || referenceName.equals("account") || referenceName.equals("objectCode")
                || referenceName.equals("subAccount") || referenceName.equals("subObjectCode") || referenceName.equals("protect")
                || referenceName.equals("referenceOrigin") ) {
            return; // do nothing - not OJB objects
        }
        super.refreshReferenceObject(referenceName);
    }

}
