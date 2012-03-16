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
package org.kuali.kfs.module.cab.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class GeneralLedgerEntry extends PersistableBusinessObjectBase {
    private Long generalLedgerAccountIdentifier;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String universityFiscalPeriodCode;
    private String financialDocumentTypeCode;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private Integer transactionLedgerEntrySequenceNumber;
    private String transactionLedgerEntryDescription;
    private KualiDecimal transactionLedgerEntryAmount;
    private KualiDecimal transactionLedgerSubmitAmount;
    private String organizationReferenceId;
    private String referenceFinancialSystemOriginationCode;
    private String referenceFinancialDocumentNumber;
    private String transactionDebitCreditCode;
    private String organizationDocumentNumber;
    private String projectCode;
    private Date transactionPostingDate;
    private Date transactionDate;
    private Timestamp transactionDateTimeStamp;
    private String activityStatusCode;

    // References
    private Account account;
    private Chart chart;
    private ObjectCode financialObject;
    private SubAccount subAccount;
    private SubObjectCode financialSubObject;
    private ObjectType objectType;
    private DocumentTypeEBO financialSystemDocumentTypeCode;

    private List<GeneralLedgerEntryAsset> generalLedgerEntryAssets;
    private List<PurchasingAccountsPayableLineAssetAccount> purApLineAssetAccounts;
    // non-db fields
    private boolean selected;
    private KualiDecimal amount;
    private boolean active;

    public GeneralLedgerEntry() {
        this.generalLedgerEntryAssets = new ArrayList<GeneralLedgerEntryAsset>();
        this.purApLineAssetAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
    }

    /**
     * Constructs a GeneralLedgerEntry item from GL line entry
     *
     * @param entry GL Lines
     */
    public GeneralLedgerEntry(Entry entry) {
        this.generalLedgerEntryAssets = new ArrayList<GeneralLedgerEntryAsset>();
        this.setUniversityFiscalYear(entry.getUniversityFiscalYear());
        this.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        this.setAccountNumber(entry.getAccountNumber());
        this.setSubAccountNumber(entry.getSubAccountNumber());
        this.setFinancialObjectCode(entry.getFinancialObjectCode());
        this.setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        this.setFinancialBalanceTypeCode(entry.getFinancialBalanceTypeCode());
        this.setFinancialObjectTypeCode(entry.getFinancialObjectTypeCode());
        this.setUniversityFiscalPeriodCode(entry.getUniversityFiscalPeriodCode());
        this.setFinancialDocumentTypeCode(entry.getFinancialDocumentTypeCode());
        this.setFinancialSystemOriginationCode(entry.getFinancialSystemOriginationCode());
        this.setDocumentNumber(entry.getDocumentNumber());
        this.setTransactionLedgerEntrySequenceNumber(entry.getTransactionLedgerEntrySequenceNumber());
        this.setTransactionLedgerEntryDescription(entry.getTransactionLedgerEntryDescription());
        this.setTransactionLedgerEntryAmount(entry.getTransactionLedgerEntryAmount());
        this.setOrganizationReferenceId(entry.getOrganizationReferenceId());
        this.setReferenceFinancialSystemOriginationCode(entry.getReferenceFinancialSystemOriginationCode());
        this.setReferenceFinancialDocumentNumber(entry.getReferenceFinancialDocumentNumber());
        this.setTransactionDebitCreditCode(entry.getTransactionDebitCreditCode());
        this.setOrganizationDocumentNumber(entry.getOrganizationDocumentNumber());
        this.setProjectCode(entry.getProjectCode());
        this.setTransactionDate(entry.getTransactionDate());
        this.setTransactionPostingDate(entry.getTransactionPostingDate());
        this.setTransactionDateTimeStamp(entry.getTransactionDateTimeStamp());
        this.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
    }


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.generalLedgerAccountIdentifier != null) {
            m.put("generalLedgerAccountIdentifier", this.generalLedgerAccountIdentifier.toString());
        }
        m.put("universityFiscalYear", this.universityFiscalYear.toString());
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("financialBalanceTypeCode", this.financialBalanceTypeCode);
        m.put("financialObjectTypeCode", this.financialObjectTypeCode);
        m.put("universityFiscalPeriodCode", this.universityFiscalPeriodCode);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("financialSystemOriginationCode", this.financialSystemOriginationCode);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        return m;
    }

    /**
     * Gets the generalLedgerAccountIdentifier attribute.
     *
     * @return Returns the generalLedgerAccountIdentifier
     */
    public Long getGeneralLedgerAccountIdentifier() {
        return generalLedgerAccountIdentifier;
    }


    /**
     * Sets the generalLedgerAccountIdentifier attribute.
     *
     * @param generalLedgerAccountIdentifier The generalLedgerAccountIdentifier to set.
     */
    public void setGeneralLedgerAccountIdentifier(Long glAccountId) {
        this.generalLedgerAccountIdentifier = glAccountId;
    }

    /**
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     *
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     *
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     *
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     *
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     *
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     *
     * @return Returns the financialBalanceTypeCode
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute.
     *
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     *
     * @return Returns the financialObjectTypeCode
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute.
     *
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the universityFiscalPeriodCode attribute.
     *
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     *
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     *
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     *
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the financialSystemOriginationCode attribute.
     *
     * @return Returns the financialSystemOriginationCode
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute.
     *
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the transactionLedgerEntrySequenceNumber attribute.
     *
     * @return Returns the transactionLedgerEntrySequenceNumber
     */
    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    /**
     * Sets the transactionLedgerEntrySequenceNumber attribute.
     *
     * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
     */
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    /**
     * Gets the transactionLedgerEntryDescription attribute.
     *
     * @return Returns the transactionLedgerEntryDescription
     */
    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    /**
     * Sets the transactionLedgerEntryDescription attribute.
     *
     * @param transactionLedgerEntryDescription The transactionLedgerEntryDescription to set.
     */
    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    /**
     * Gets the transactionLedgerEntryAmount attribute.
     *
     * @return Returns the transactionLedgerEntryAmount
     */
    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    /**
     * Sets the transactionLedgerEntryAmount attribute.
     *
     * @param transactionLedgerEntryAmount The transactionLedgerEntryAmount to set.
     */
    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    /**
     * Gets the organizationReferenceId attribute.
     *
     * @return Returns the organizationReferenceId
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * Sets the organizationReferenceId attribute.
     *
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * Gets the referenceFinancialSystemOriginationCode attribute.
     *
     * @return Returns the referenceFinancialSystemOriginationCode
     */
    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }

    /**
     * Sets the referenceFinancialSystemOriginationCode attribute.
     *
     * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
     */
    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     *
     * @return Returns the referenceFinancialDocumentNumber
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     *
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    /**
     * Gets the transactionDebitCreditCode attribute.
     *
     * @return Returns the transactionDebitCreditCode
     */
    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    /**
     * Sets the transactionDebitCreditCode attribute.
     *
     * @param transactionDebitCreditCode The transactionDebitCreditCode to set.
     */
    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    /**
     * Gets the organizationDocumentNumber attribute.
     *
     * @return Returns the organizationDocumentNumber
     */
    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    /**
     * Sets the organizationDocumentNumber attribute.
     *
     * @param organizationDocumentNumber The organizationDocumentNumber to set.
     */
    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    /**
     * Gets the projectCode attribute.
     *
     * @return Returns the projectCode
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute.
     *
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the transactionPostingDate attribute.
     *
     * @return Returns the transactionPostingDate
     */
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate attribute.
     *
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }

    /**
     * Gets the transactionDateTimeStamp attribute.
     *
     * @return Returns the transactionDateTimeStamp
     */
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp attribute.
     *
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     *
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the subAccount attribute.
     *
     * @return Returns the subAccount
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute.
     *
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }


    /**
     * Gets the chart attribute.
     *
     * @return Returns the chart
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute.
     *
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * Gets the financialObject attribute.
     *
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     *
     * @param financialObject The financialObject to set.
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the financialSubObject attribute.
     *
     * @return Returns the financialSubObject
     */
    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute.
     *
     * @param financialSubObject The financialSubObject to set.
     */
    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the objectType attribute.
     *
     * @return Returns the objectType
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Sets the objectType attribute.
     *
     * @param objectType The objectType to set.
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     *
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentTypeCode() {
        if ( financialSystemDocumentTypeCode == null || !StringUtils.equals(financialSystemDocumentTypeCode.getName(), financialDocumentTypeCode) ) {
            financialSystemDocumentTypeCode = null;
            if ( StringUtils.isNotBlank(financialDocumentTypeCode) ) {
                DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(financialDocumentTypeCode);
                if ( docType != null ) {
                    financialSystemDocumentTypeCode = org.kuali.rice.kew.doctype.bo.DocumentType.from(docType);
                }
            }
        }
        return financialSystemDocumentTypeCode;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */
    public boolean isActive() {
        return CabConstants.ActivityStatusCode.NEW.equalsIgnoreCase(this.getActivityStatusCode()) || CabConstants.ActivityStatusCode.MODIFIED.equalsIgnoreCase(this.getActivityStatusCode());
    }


    /**
     * Gets the activityStatusCode attribute.
     *
     * @return Returns the activityStatusCode.
     */
    public String getActivityStatusCode() {
        return activityStatusCode;
    }

    /**
     * Sets the activityStatusCode attribute value.
     *
     * @param activityStatusCode The activityStatusCode to set.
     */
    public void setActivityStatusCode(String activityStatusCode) {
        this.activityStatusCode = activityStatusCode;
    }

    /**
     * Gets the generalLedgerEntryAssets attribute.
     *
     * @return Returns the generalLedgerEntryAssets
     */

    public List<GeneralLedgerEntryAsset> getGeneralLedgerEntryAssets() {
        return generalLedgerEntryAssets;
    }

    /**
     * Sets the generalLedgerEntryAssets attribute.
     *
     * @param generalLedgerEntryAssets The generalLedgerEntryAssets to set.
     */

    public void setGeneralLedgerEntryAssets(List<GeneralLedgerEntryAsset> generalLedgerEntryAssets) {
        this.generalLedgerEntryAssets = generalLedgerEntryAssets;
    }

    /**
     * Gets the transactionDate attribute.
     *
     * @return Returns the transactionDate
     */

    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transactionDate attribute.
     *
     * @param transactionDate The transactionDate to set.
     */

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the purApLineAssetAccounts attribute.
     *
     * @return Returns the purApLineAssetAccounts.
     */
    public List<PurchasingAccountsPayableLineAssetAccount> getPurApLineAssetAccounts() {
        return purApLineAssetAccounts;
    }

    /**
     * Sets the purApLineAssetAccounts attribute value.
     *
     * @param purApLineAssetAccounts The purApLineAssetAccounts to set.
     */
    public void setPurApLineAssetAccounts(List<PurchasingAccountsPayableLineAssetAccount> purApLineAssetAccounts) {
        this.purApLineAssetAccounts = purApLineAssetAccounts;
    }

    public KualiDecimal computePayment() {
        KualiDecimal absAmount = getTransactionLedgerEntryAmount();
        if (absAmount == null) {
            return null;
        }
        return KFSConstants.GL_CREDIT_CODE.equals(getTransactionDebitCreditCode()) ? absAmount.negated() : absAmount;
    }

    /**
     * Gets the selected attribute.
     *
     * @return Returns the selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the selected attribute value.
     *
     * @param selected The selected to set.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Gets the amount attribute.
     *
     * @return Returns the amount.
     */
    public KualiDecimal getAmount() {
        if (getTransactionLedgerEntryAmount() != null && KFSConstants.GL_CREDIT_CODE.equals(getTransactionDebitCreditCode())) {
            setAmount(getTransactionLedgerEntryAmount().negated());
        }
        else {
            setAmount(getTransactionLedgerEntryAmount());
        }
        return amount;
    }

    /**
     * Sets the amount attribute value.
     *
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal absAmount) {
        this.amount = absAmount;
    }

    /**
     * Gets the transactionLedgerSubmitAmount attribute.
     *
     * @return Returns the transactionLedgerSubmitAmount.
     */
    public KualiDecimal getTransactionLedgerSubmitAmount() {
        return transactionLedgerSubmitAmount;
    }

    /**
     * Sets the transactionLedgerSubmitAmount attribute value.
     *
     * @param transactionLedgerSubmitAmount The transactionLedgerSubmitAmount to set.
     */
    public void setTransactionLedgerSubmitAmount(KualiDecimal transactionLedgerSubmitAmount) {
        this.transactionLedgerSubmitAmount = transactionLedgerSubmitAmount;
    }
}
