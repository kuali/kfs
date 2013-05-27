/*
 * Copyright 2006-2007 The Kuali Foundation
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

package org.kuali.kfs.module.ec.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.util.EffortCertificationParameterFinder;
import org.kuali.kfs.module.ec.util.PayrollAmountHolder;
import org.kuali.kfs.module.ld.businessobject.PositionData;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

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
    private String costShareSourceSubAccountNumber;

    private Integer universityFiscalYear;

    private KualiDecimal originalFringeBenefitAmount;

    private boolean accountExpiredOverride;
    private boolean accountExpiredOverrideNeeded;
    private String overrideCode = AccountingLineOverride.CODE.NONE;

    private boolean newLineIndicator; // to indicate if this detail line has been persisted or not

    // holds last saved updated payroll amount so business rule can check if it has been updated at the route level
    private KualiDecimal persistedPayrollAmount;
    private Integer persistedEffortPercent;
    private String groupId;

    private EffortCertificationDocument effortCertificationDocument;
    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private Chart sourceChartOfAccounts;
    private Account sourceAccount;
    private SubAccount subAccount;
    private SystemOptions options;

    protected PositionData positionData;
    protected String effectiveDate;
    /**
     * Default constructor.
     */
    public EffortCertificationDetail() {
        super();

        if ( SpringContext.isInitialized() ) {
            subAccountNumber = KFSConstants.getDashSubAccountNumber();
        }

        effortCertificationPayrollAmount = KualiDecimal.ZERO;
        effortCertificationOriginalPayrollAmount = KualiDecimal.ZERO;
        effortCertificationCalculatedOverallPercent = new Integer(0);
        effortCertificationUpdatedOverallPercent = new Integer(0);
        originalFringeBenefitAmount = KualiDecimal.ZERO;
        effectiveDate = KFSConstants.EMPTY_STRING;
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
            this.universityFiscalYear = effortCertificationDetail.getUniversityFiscalYear();
            this.costShareSourceSubAccountNumber = effortCertificationDetail.getCostShareSourceSubAccountNumber();
            this.effortCertificationOriginalPayrollAmount = effortCertificationDetail.getEffortCertificationOriginalPayrollAmount();
            this.originalFringeBenefitAmount = effortCertificationDetail.getOriginalFringeBenefitAmount();
            this.effectiveDate = effortCertificationDetail.getEffectiveDate();


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
        // if accounts can't cross charts, set chart code whenever account number is set
        AccountService accountService = SpringContext.getBean(AccountService.class);
        if (!accountService.accountsCanCrossCharts()) {
            Account account = accountService.getUniqueAccountForAccountNumber(accountNumber);
            if (ObjectUtils.isNotNull(account)) {
                setChartOfAccountsCode(account.getChartOfAccountsCode());
                setChartOfAccounts(account.getChartOfAccounts());

            }


        }
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
     * Gets the effectiveDate attribute.
     *
     * @return Returns the effectiveDate.
     */
    public String getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the effectiveDate attribute value.
     *
     * @param effectiveDate The effectiveDate to set.
     */
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
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
        if (account == null && StringUtils.isNotBlank(this.getChartOfAccountsCode()) && StringUtils.isNotBlank(this.getAccountNumber())) {
            this.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        }

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
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     *
     * @param options The options to set.
     */
    @Deprecated
    public void setOptions(SystemOptions options) {
        this.options = options;
    }

    /**
     * Gets the positionData attribute.
     *
     * @return Returns the positionData.
     */
    public PositionData getPositionData() {
        return positionData;
    }

    /**
     * Sets the positionData attribute value.
     *
     * @param positionData The positionData to set.
     */
    public void setPositionData(PositionData positionData) {
        this.positionData = positionData;
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
     * If the account of this detail line is closed, the line cannot be edited.
     *
     * @return Returns true if line can be edited, false otherwise
     */
    public boolean isEditable() {
        if (this.getAccount() != null && !this.getAccount().isActive()) {
            return false;
        }

        return true;
    }

    /**
     * Gets the federalOrFederalPassThroughIndicator attribute. If this line is associated with a valid account, the indicator will
     * be retrieved and updated.
     *
     * @return Returns the federalOrFederalPassThroughIndicator.
     */
    public boolean isFederalOrFederalPassThroughIndicator() {
        if (this.getAccount() != null) {
            List<String> federalAgencyTypeCodes = new ArrayList<String>(EffortCertificationParameterFinder.getFederalAgencyTypeCodes());
            return SpringContext.getBean(ContractsAndGrantsModuleService.class).isAwardedByFederalAgency(getAccount().getChartOfAccountsCode(), getAccount().getAccountNumber(), federalAgencyTypeCodes);
        }

        return false;
    }

    /**
     * This is a marker method, which does nothing.
     */
    public void setFederalOrFederalPassThroughIndicator(boolean federalOrFederalPassThroughIndicator) {
        return;
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
        KualiDecimal payrollAmount = this.getEffortCertificationPayrollAmount();

        return EffortCertificationDetail.calculateFringeBenefit(this, payrollAmount);
    }

    /**
     * This is a marker method, which does nothing.
     */
    public void setFringeBenefitAmount(KualiDecimal fringeBenefitAmount) {
        return;
    }

    /**
     * Gets the originalFringeBenefitAmount attribute.
     *
     * @return Returns the originalFringeBenefitAmount.
     */
    public KualiDecimal getOriginalFringeBenefitAmount() {
        if (this.originalFringeBenefitAmount == null || originalFringeBenefitAmount.isZero()) {
            this.recalculateOriginalFringeBenefit();
        }
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
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return this.universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
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
     * Gets the accountExpiredOverride attribute.
     *
     * @return Returns the accountExpiredOverride.
     */
    public boolean isAccountExpiredOverride() {
        return accountExpiredOverride;
    }

    /**
     * Sets the accountExpiredOverride attribute value.
     *
     * @param accountExpiredOverride The accountExpiredOverride to set.
     */
    public void setAccountExpiredOverride(boolean accountExpiredOverride) {
        this.accountExpiredOverride = accountExpiredOverride;
    }

    /**
     * Gets the accountExpiredOverrideNeeded attribute.
     *
     * @return Returns the accountExpiredOverrideNeeded.
     */
    public boolean isAccountExpiredOverrideNeeded() {
        return accountExpiredOverrideNeeded;
    }

    /**
     * Sets the accountExpiredOverrideNeeded attribute value.
     *
     * @param accountExpiredOverrideNeeded The accountExpiredOverrideNeeded to set.
     */
    public void setAccountExpiredOverrideNeeded(boolean accountExpiredOverrideNeeded) {
        this.accountExpiredOverrideNeeded = accountExpiredOverrideNeeded;
    }

    /**
     * calculate the total effort percent of the given detail lines
     *
     * @param the given detail lines
     * @return Returns the total effort percent
     */
    public static Integer getTotalEffortPercent(List<EffortCertificationDetail> effortCertificationDetailLines) {
        Integer totalEffortPercent = 0;

        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            totalEffortPercent += detailLine.getEffortCertificationUpdatedOverallPercent();
        }

        return totalEffortPercent;
    }

    /**
     * calculate the total persised effort percent of the given detail lines
     *
     * @param the given detail lines
     * @return Returns the total persisted effort percent
     */
    public static Integer getTotalPersistedEffortPercent(List<EffortCertificationDetail> effortCertificationDetailLines) {
        Integer totalEffortPercent = 0;

        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            totalEffortPercent += detailLine.getPersistedEffortPercent();
        }

        return totalEffortPercent;
    }

    /**
     * calculate the total original effort percent of the given detail lines
     *
     * @param the given detail lines
     * @return Returns the total original effort percent
     */
    public static Integer getTotalOriginalEffortPercent(List<EffortCertificationDetail> effortCertificationDetailLines) {
        Integer totalOriginalEffortPercent = 0;

        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            totalOriginalEffortPercent += detailLine.getEffortCertificationCalculatedOverallPercent();
        }

        return totalOriginalEffortPercent;
    }

    /**
     * calculate the total payroll amount of the given detail lines
     *
     * @param the given detail lines
     * @return Returns the total original payroll amount
     */
    public static KualiDecimal getTotalPayrollAmount(List<EffortCertificationDetail> effortCertificationDetailLines) {
        KualiDecimal totalPayrollAmount = KualiDecimal.ZERO;

        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            totalPayrollAmount = totalPayrollAmount.add(detailLine.getEffortCertificationPayrollAmount());
        }

        return totalPayrollAmount;
    }

    /**
     * calculate the total payroll amount of the given detail lines
     *
     * @param the given detail lines
     * @return Returns the total original payroll amount
     */
    public static KualiDecimal getTotalPersistedPayrollAmount(List<EffortCertificationDetail> effortCertificationDetailLines) {
        KualiDecimal totalPayrollAmount = KualiDecimal.ZERO;

        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            totalPayrollAmount = totalPayrollAmount.add(detailLine.getPersistedPayrollAmount());
        }

        return totalPayrollAmount;
    }

    /**
     * calculate the total original payroll amount of the given detail lines
     *
     * @param the given detail lines
     * @return Returns the total original payroll amount
     */
    public static KualiDecimal getTotalOriginalPayrollAmount(List<EffortCertificationDetail> effortCertificationDetailLines) {
        KualiDecimal totalOriginalPayrollAmount = KualiDecimal.ZERO;

        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            totalOriginalPayrollAmount = totalOriginalPayrollAmount.add(detailLine.getEffortCertificationOriginalPayrollAmount());
        }

        return totalOriginalPayrollAmount;
    }

    /**
     * Gets the totalFringeBenefit attribute.
     *
     * @return Returns the totalFringeBenefit.
     */
    public static KualiDecimal getTotalFringeBenefit(List<EffortCertificationDetail> effortCertificationDetailLines) {
        KualiDecimal totalFringeBenefit = KualiDecimal.ZERO;

        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            totalFringeBenefit = totalFringeBenefit.add(detailLine.getFringeBenefitAmount());
        }

        return totalFringeBenefit;
    }

    /**
     * Gets the totalOriginalFringeBenefit attribute.
     *
     * @return Returns the totalOriginalFringeBenefit.
     */
    public static KualiDecimal getTotalOriginalFringeBenefit(List<EffortCertificationDetail> effortCertificationDetailLines) {
        KualiDecimal totalOriginalFringeBenefit = KualiDecimal.ZERO;

        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            totalOriginalFringeBenefit = totalOriginalFringeBenefit.add(detailLine.getOriginalFringeBenefitAmount());
        }

        return totalOriginalFringeBenefit;
    }

    /**
     * recalculate the payroll amount of the current detail line
     *
     * @param totalPayrollAmount the total payroll amount of the hosting document
     */
    public void recalculatePayrollAmount(KualiDecimal totalPayrollAmount) {
        Integer effortPercent = this.getEffortCertificationUpdatedOverallPercent();
        KualiDecimal payrollAmount = PayrollAmountHolder.recalculatePayrollAmount(totalPayrollAmount, effortPercent);
        this.setEffortCertificationPayrollAmount(payrollAmount);
    }

    /**
     * recalculate the original fringe benefit of the current detail line
     */
    public void recalculateOriginalFringeBenefit() {
        KualiDecimal originalPayrollAmount = this.getEffortCertificationOriginalPayrollAmount();
        KualiDecimal fringeBenefit = EffortCertificationDetail.calculateFringeBenefit(this, originalPayrollAmount);
        this.setOriginalFringeBenefitAmount(fringeBenefit);
    }

    /**
     * recalculate the original fringe benefit of the current detail line
     */
    public static KualiDecimal calculateFringeBenefit(EffortCertificationDetail detailLine, KualiDecimal payrollAmount) {
        LaborModuleService laborModuleService = SpringContext.getBean(LaborModuleService.class);
        Integer fiscalYear = EffortCertificationParameterFinder.getCreateReportFiscalYear();
        String chartOfAccountsCode = detailLine.getChartOfAccountsCode();
        String objectCode = detailLine.getFinancialObjectCode();
        String accountNumber = detailLine.getAccountNumber();
        String subAccountNumber = detailLine.getSubAccountNumber();

        return laborModuleService.calculateFringeBenefit(fiscalYear, chartOfAccountsCode, objectCode, payrollAmount, accountNumber, subAccountNumber);
    }

    /**
     * Gets the persistedEffortPercent attribute.
     * @return Returns the persistedEffortPercent.
     */
    public Integer getPersistedEffortPercent() {
        return persistedEffortPercent;
    }

    /**
     * Sets the persistedEffortPercent attribute value.
     * @param persistedEffortPercent The persistedEffortPercent to set.
     */
    public void setPersistedEffortPercent(Integer persistedEffortPercent) {
        this.persistedEffortPercent = persistedEffortPercent;
    }

    /**
     * Gets the groupId attribute.
     * @return Returns the groupId.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the groupId attribute value.
     * @param groupId The groupId to set.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }





}
