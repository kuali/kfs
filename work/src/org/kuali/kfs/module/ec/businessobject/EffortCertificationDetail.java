/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.effort.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.module.cg.bo.AwardAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;

/**
 * Business Object for the Effort Certification Detail Table.
 */
public class EffortCertificationDetail extends PersistableBusinessObjectBase {
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String positionNumber;
    private String financialObjectCode;
    private String sourceChartOfAccountsCode;
    private String sourceAccountNumber;

    private KualiDecimal effortCertificationPayrollAmount;
    private KualiDecimal effortCertificationOriginalPayrollAmount;
    private Integer effortCertificationCalculatedOverallPercent;
    private Integer effortCertificationUpdatedOverallPercent;
    private Integer financialDocumentPostingYear;
    private String costShareSourceSubAccountNumber;

    private KualiDecimal fringeBenefitAmount;
    private KualiDecimal originalFringeBenefitAmount;

    private String overrideCode; // to hold the override code if the associated account is expired
    private boolean newLineIndicator; // to indicate if this detail line has been persisted or not
    private boolean editable; // to indicate if this detail line can be changed or deleted
    private boolean federalOrFederalPassThroughIndicator; // to indicate if this line is associated with a federal or federal pass
    // through account.
    private KualiDecimal persistedPayrollAmount; // holds last saved updated payroll amount so business rule can check if it has
                                                    // been updated at the route level

    private EffortCertificationDocument effortCertificationDocument;
    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private Chart sourceChartOfAccounts;
    private Account sourceAccount;
    private SubAccount subAccount;
    private Options options;

    /**
     * Default constructor.
     */
    public EffortCertificationDetail() {
        super();
    }

    public EffortCertificationDetail(EffortCertificationDetail effortCertificationDetail) {
        super();
        if (effortCertificationDetail != null) {
            this.chartOfAccountsCode = effortCertificationDetail.getChartOfAccountsCode();
            this.accountNumber = effortCertificationDetail.getAccountNumber();
            this.subAccountNumber = effortCertificationDetail.getSubAccountNumber();
            this.positionNumber = effortCertificationDetail.getPositionNumber();
            this.financialObjectCode = effortCertificationDetail.getFinancialObjectCode();
            this.sourceChartOfAccountsCode = effortCertificationDetail.getSourceChartOfAccountsCode();
            this.sourceAccountNumber = effortCertificationDetail.getSourceAccountNumber();
            this.effortCertificationPayrollAmount = effortCertificationDetail.getEffortCertificationPayrollAmount();
            this.effortCertificationCalculatedOverallPercent = effortCertificationDetail.getEffortCertificationCalculatedOverallPercent();
            this.effortCertificationUpdatedOverallPercent = effortCertificationDetail.getEffortCertificationUpdatedOverallPercent();
            this.financialDocumentPostingYear = effortCertificationDetail.getFinancialDocumentPostingYear();
            this.costShareSourceSubAccountNumber = effortCertificationDetail.getCostShareSourceSubAccountNumber();
            this.effortCertificationOriginalPayrollAmount = effortCertificationDetail.getEffortCertificationOriginalPayrollAmount();
            this.fringeBenefitAmount = effortCertificationDetail.getFringeBenefitAmount();
            this.originalFringeBenefitAmount = effortCertificationDetail.getOriginalFringeBenefitAmount();

            this.editable = effortCertificationDetail.isEditable();
        }
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the sourceChartOfAccountsCode attribute.
     * 
     * @return Returns the sourceChartOfAccountsCode.
     */
    public String getSourceChartOfAccountsCode() {
        return sourceChartOfAccountsCode;
    }

    /**
     * Sets the sourceChartOfAccountsCode attribute value.
     * 
     * @param sourceChartOfAccountsCode The sourceChartOfAccountsCode to set.
     */
    public void setSourceChartOfAccountsCode(String sourceChartOfAccountsCode) {
        this.sourceChartOfAccountsCode = sourceChartOfAccountsCode;
    }

    /**
     * Gets the sourceAccountNumber attribute.
     * 
     * @return Returns the sourceAccountNumber.
     */
    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    /**
     * Sets the sourceAccountNumber attribute value.
     * 
     * @param sourceAccountNumber The sourceAccountNumber to set.
     */
    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    /**
     * Gets the effortCertificationPayrollAmount attribute.
     * 
     * @return Returns the effortCertificationPayrollAmount.
     */
    public KualiDecimal getEffortCertificationPayrollAmount() {
        return effortCertificationPayrollAmount;
    }

    /**
     * Sets the effortCertificationPayrollAmount attribute value.
     * 
     * @param effortCertificationPayrollAmount The effortCertificationPayrollAmount to set.
     */
    public void setEffortCertificationPayrollAmount(KualiDecimal effortCertificationPayrollAmount) {
        this.effortCertificationPayrollAmount = effortCertificationPayrollAmount;
    }

    /**
     * Gets the effortCertificationCalculatedOverallPercent attribute.
     * 
     * @return Returns the effortCertificationCalculatedOverallPercent.
     */
    public Integer getEffortCertificationCalculatedOverallPercent() {
        return effortCertificationCalculatedOverallPercent;
    }

    /**
     * Sets the effortCertificationCalculatedOverallPercent attribute value.
     * 
     * @param effortCertificationCalculatedOverallPercent The effortCertificationCalculatedOverallPercent to set.
     */
    public void setEffortCertificationCalculatedOverallPercent(Integer effortCertificationCalculatedOverallPercent) {
        this.effortCertificationCalculatedOverallPercent = effortCertificationCalculatedOverallPercent;
    }

    /**
     * Gets the effortCertificationUpdatedOverallPercent attribute.
     * 
     * @return Returns the effortCertificationUpdatedOverallPercent.
     */
    public Integer getEffortCertificationUpdatedOverallPercent() {
        return effortCertificationUpdatedOverallPercent;
    }

    /**
     * Sets the effortCertificationUpdatedOverallPercent attribute value.
     * 
     * @param effortCertificationUpdatedOverallPercent The effortCertificationUpdatedOverallPercent to set.
     */
    public void setEffortCertificationUpdatedOverallPercent(Integer effortCertificationUpdatedOverallPercent) {
        this.effortCertificationUpdatedOverallPercent = effortCertificationUpdatedOverallPercent;
    }

    /**
     * Gets the financialDocumentPostingYear attribute.
     * 
     * @return Returns the financialDocumentPostingYear.
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }

    /**
     * Sets the financialDocumentPostingYear attribute value.
     * 
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }

    /**
     * Gets the costShareSourceSubAccountNumber attribute.
     * 
     * @return Returns the costShareSourceSubAccountNumber.
     */
    public String getCostShareSourceSubAccountNumber() {
        return costShareSourceSubAccountNumber;
    }

    /**
     * Sets the costShareSourceSubAccountNumber attribute value.
     * 
     * @param costShareSourceSubAccountNumber The costShareSourceSubAccountNumber to set.
     */
    public void setCostShareSourceSubAccountNumber(String costShareSourceSubAccountNumber) {
        this.costShareSourceSubAccountNumber = costShareSourceSubAccountNumber;
    }

    /**
     * Gets the effortCertificationOriginalPayrollAmount attribute.
     * 
     * @return Returns the effortCertificationOriginalPayrollAmount.
     */
    public KualiDecimal getEffortCertificationOriginalPayrollAmount() {
        return effortCertificationOriginalPayrollAmount;
    }

    /**
     * Sets the effortCertificationOriginalPayrollAmount attribute value.
     * 
     * @param effortCertificationOriginalPayrollAmount The effortCertificationOriginalPayrollAmount to set.
     */
    public void setEffortCertificationOriginalPayrollAmount(KualiDecimal effortCertificationOriginalPayrollAmount) {
        this.effortCertificationOriginalPayrollAmount = effortCertificationOriginalPayrollAmount;
    }


    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject.
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute value.
     * 
     * @param financialObject The financialObject to set.
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the effortCertificationDocument attribute.
     * 
     * @return Returns the effortCertificationDocument.
     */
    public EffortCertificationDocument getEffortCertificationDocument() {
        return effortCertificationDocument;
    }

    /**
     * Sets the effortCertificationDocument attribute value.
     * 
     * @param effortCertificationDocument The effortCertificationDocument to set.
     */
    @Deprecated
    public void setEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument) {
        this.effortCertificationDocument = effortCertificationDocument;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * 
     * @param account The account to set.
     */
    @Deprecated
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the sourceChartOfAccounts attribute.
     * 
     * @return Returns the sourceChartOfAccounts.
     */
    public Chart getSourceChartOfAccounts() {
        return sourceChartOfAccounts;
    }

    /**
     * Sets the sourceChartOfAccounts attribute value.
     * 
     * @param sourceChartOfAccounts The sourceChartOfAccounts to set.
     */
    @Deprecated
    public void setSourceChartOfAccounts(Chart sourceChartOfAccounts) {
        this.sourceChartOfAccounts = sourceChartOfAccounts;
    }

    /**
     * Gets the sourceAccount attribute.
     * 
     * @return Returns the sourceAccount.
     */
    public Account getSourceAccount() {
        return sourceAccount;
    }

    /**
     * Sets the sourceAccount attribute value.
     * 
     * @param sourceAccount The sourceAccount to set.
     */
    @Deprecated
    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     */
    @Deprecated
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the options attribute.
     * 
     * @return Returns the options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    @Deprecated
    public void setOptions(Options options) {
        this.options = options;
    }

    /**
     * Gets the newLineIndicator attribute.
     * 
     * @return Returns the newLineIndicator.
     */
    public boolean isNewLineIndicator() {
        return newLineIndicator;
    }

    /**
     * Sets the newLineIndicator attribute value.
     * 
     * @param newLineIndicator The newLineIndicator to set.
     */
    public void setNewLineIndicator(boolean newLineIndicator) {
        this.newLineIndicator = newLineIndicator;
    }

    /**
     * Gets the isEditable attribute. If the account of this detail line is closed, the line cannot be edited.
     * 
     * @return Returns the isEditable.
     */
    public boolean isEditable() {
        if (this.getAccount() != null && this.getAccount().isAccountClosedIndicator()) {
            this.setEditable(false);
        }

        return editable;
    }

    /**
     * Sets the editable attribute value.
     * 
     * @param isEditable The editable to set.
     */
    public void setEditable(boolean isEditable) {
        this.editable = isEditable;
    }

    /**
     * Gets the federalOrFederalPassThroughIndicator attribute. If this line is associated with a valid account, the indicator will
     * be retrieved and updated.
     * 
     * @return Returns the federalOrFederalPassThroughIndicator.
     */
    public boolean isFederalOrFederalPassThroughIndicator() {
        if (this.getAccount() != null) {
            List<String> federalAgencyTypeCodes = EffortCertificationParameterFinder.getFederalAgencyTypeCodes();
            this.setFederalOrFederalPassThroughIndicator(this.getAccount().isAwardedByFederalAcency(federalAgencyTypeCodes));
        }

        return federalOrFederalPassThroughIndicator;
    }

    /**
     * Sets the federalOrFederalPassThroughIndicator attribute value.
     * 
     * @param federalOrFederalPassThroughIndicator The federalOrFederalPassThroughIndicator to set.
     */
    public void setFederalOrFederalPassThroughIndicator(boolean federalOrFederalPassThroughIndicator) {
        this.federalOrFederalPassThroughIndicator = federalOrFederalPassThroughIndicator;
    }

    /**
     * Gets the overrideCode attribute.
     * 
     * @return Returns the overrideCode.
     */
    public String getOverrideCode() {
        return overrideCode;
    }

    /**
     * Sets the overrideCode attribute value.
     * 
     * @param overrideCode The overrideCode to set.
     */
    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }

    /**
     * Gets the fringeBenefitAmount attribute.
     * 
     * @return Returns the fringeBenefitAmount.
     */
    public KualiDecimal getFringeBenefitAmount() {
        return fringeBenefitAmount;
    }

    /**
     * Sets the fringeBenefitAmount attribute value.
     * 
     * @param fringeBenefitAmount The fringeBenefitAmount to set.
     */
    public void setFringeBenefitAmount(KualiDecimal fringeBenefitAmount) {
        this.fringeBenefitAmount = fringeBenefitAmount;
    }

    /**
     * Gets the originalFringeBenefitAmount attribute.
     * 
     * @return Returns the originalFringeBenefitAmount.
     */
    public KualiDecimal getOriginalFringeBenefitAmount() {
        return originalFringeBenefitAmount;
    }

    /**
     * Sets the originalFringeBenefitAmount attribute value.
     * 
     * @param originalFringeBenefitAmount The originalFringeBenefitAmount to set.
     */
    public void setOriginalFringeBenefitAmount(KualiDecimal originalFringeBenefitAmount) {
        this.originalFringeBenefitAmount = originalFringeBenefitAmount;
    }


    /**
     * Gets the persistedPayrollAmount attribute.
     * 
     * @return Returns the persistedPayrollAmount.
     */
    public KualiDecimal getPersistedPayrollAmount() {
        return persistedPayrollAmount;
    }

    /**
     * Sets the persistedPayrollAmount attribute value.
     * 
     * @param persistedPayrollAmount The persistedPayrollAmount to set.
     */
    public void setPersistedPayrollAmount(KualiDecimal persistedPayrollAmount) {
        this.persistedPayrollAmount = persistedPayrollAmount;
    }
    
    /**
     * @return calculated benefits on original payroll amount
     */
    public KualiDecimal getEffortCertificationOriginalBenefitAmount() {
        return new KualiDecimal(0);
    }

    /**
     * @return calculated benefits on updated payroll amount
     */
    public KualiDecimal getEffortCertificationUpdatedBenefitAmount() {
        return new KualiDecimal(0);
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode);
        map.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.accountNumber);
        map.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, this.subAccountNumber);
        map.put(KFSPropertyConstants.POSITION_NUMBER, this.positionNumber);
        map.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.financialObjectCode);
        map.put(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE, this.sourceChartOfAccountsCode);
        map.put(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER, this.sourceAccountNumber);
        map.put(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT, this.effortCertificationPayrollAmount);
        map.put(EffortPropertyConstants.EFFORT_CERTIFICATION_ORIGINAL_PAYROLL_AMOUNT, this.effortCertificationOriginalPayrollAmount);

        return map;
    }

}
