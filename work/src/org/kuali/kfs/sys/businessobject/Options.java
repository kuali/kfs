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
package org.kuali.kfs.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * 
 */
public class Options extends PersistableBusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public Options() {

    }

    private Integer universityFiscalYear;
    private String actualFinancialBalanceTypeCd;
    private String budgetCheckingBalanceTypeCd;
    private boolean budgetCheckingOptionsCode;
    private Integer universityFiscalYearStartYr;
    private String universityFiscalYearStartMo;
    private String finObjectTypeIncomecashCode;
    private String finObjTypeExpenditureexpCd;
    private String finObjTypeExpendNotExpCode;
    private String finObjTypeExpNotExpendCode;
    private String financialObjectTypeAssetsCd;
    private String finObjectTypeLiabilitiesCode;
    private String finObjectTypeFundBalanceCd;
    private String extrnlEncumFinBalanceTypCd;
    private String intrnlEncumFinBalanceTypCd;
    private String preencumbranceFinBalTypeCd;
    private String eliminationsFinBalanceTypeCd;
    private String finObjTypeIncomeNotCashCd;
    private String finObjTypeCshNotIncomeCd;
    private String universityFiscalYearName;
    private boolean financialBeginBalanceLoadInd;
    private String universityFinChartOfAcctCd;
    private String costShareEncumbranceBalanceTypeCode;
    private String baseBudgetFinancialBalanceTypeCode;
    private String monthlyBudgetFinancialBalanceTypeCode;
    private String financialObjectTypeTransferIncomeCode;
    private String financialObjectTypeTransferExpenseCode;
    private String nominalFinancialBalanceTypeCode;

    private Chart universityFinChartOfAcct;
    private ObjectType objectType;
    private ObjectType finObjTypeExpenditureexp;
    private ObjectType finObjTypeExpendNotExp;
    private ObjectType finObjTypeExpNotExpend;
    private ObjectType financialObjectTypeAssets;
    private ObjectType finObjectTypeLiabilities;
    private ObjectType finObjectTypeFundBalance;
    private ObjectType finObjTypeIncomeNotCash;
    private ObjectType finObjTypeCshNotIncome;
    private ObjectType financialObjectTypeTransferIncome;
    private ObjectType financialObjectTypeTransferExpense;
    private BalanceTyp actualFinancialBalanceType;
    private BalanceTyp budgetCheckingBalanceType;
    private BalanceTyp extrnlEncumFinBalanceTyp;
    private BalanceTyp intrnlEncumFinBalanceTyp;
    private BalanceTyp preencumbranceFinBalType;
    private BalanceTyp eliminationsFinBalanceType;
    private BalanceTyp costShareEncumbranceBalanceType;
    private BalanceTyp baseBudgetFinancialBalanceType;
    private BalanceTyp monthlyBudgetFinancialBalanceType;
    private BalanceTyp nominalFinancialBalanceType;

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     * 
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     * 
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the actualFinancialBalanceTypeCd attribute.
     * 
     * @return Returns the actualFinancialBalanceTypeCd
     * 
     */
    public String getActualFinancialBalanceTypeCd() {
        return actualFinancialBalanceTypeCd;
    }

    /**
     * Sets the actualFinancialBalanceTypeCd attribute.
     * 
     * @param actualFinancialBalanceTypeCd The actualFinancialBalanceTypeCd to set.
     * 
     */
    public void setActualFinancialBalanceTypeCd(String actualFinancialBalanceTypeCd) {
        this.actualFinancialBalanceTypeCd = actualFinancialBalanceTypeCd;
    }

    /**
     * Gets the budgetCheckingBalanceTypeCd attribute.
     * 
     * @return Returns the budgetCheckingBalanceTypeCd
     * 
     */
    public String getBudgetCheckingBalanceTypeCd() {
        return budgetCheckingBalanceTypeCd;
    }

    /**
     * Sets the budgetCheckingBalanceTypeCd attribute.
     * 
     * @param budgetCheckingBalanceTypeCd The budgetCheckingBalanceTypeCd to set.
     * 
     */
    public void setBudgetCheckingBalanceTypeCd(String budgetCheckingBalanceTypeCd) {
        this.budgetCheckingBalanceTypeCd = budgetCheckingBalanceTypeCd;
    }

    /**
     * Gets the budgetCheckingOptionsCode attribute.
     * 
     * @return Returns the budgetCheckingOptionsCode
     * 
     */
    public boolean isBudgetCheckingOptionsCode() {
        return budgetCheckingOptionsCode;
    }

    /**
     * Sets the budgetCheckingOptionsCode attribute.
     * 
     * @param budgetCheckingOptionsCode The budgetCheckingOptionsCode to set.
     * 
     */
    public void setBudgetCheckingOptionsCode(boolean budgetCheckingOptionsCode) {
        this.budgetCheckingOptionsCode = budgetCheckingOptionsCode;
    }

    /**
     * Gets the universityFiscalYearStartYr attribute.
     * 
     * @return Returns the universityFiscalYearStartYr
     * 
     */
    public Integer getUniversityFiscalYearStartYr() {
        return universityFiscalYearStartYr;
    }

    /**
     * Sets the universityFiscalYearStartYr attribute.
     * 
     * @param universityFiscalYearStartYr The universityFiscalYearStartYr to set.
     * 
     */
    public void setUniversityFiscalYearStartYr(Integer universityFiscalYearStartYr) {
        this.universityFiscalYearStartYr = universityFiscalYearStartYr;
    }

    /**
     * Gets the universityFiscalYearStartMo attribute.
     * 
     * @return Returns the universityFiscalYearStartMo
     * 
     */
    public String getUniversityFiscalYearStartMo() {
        return universityFiscalYearStartMo;
    }

    /**
     * Sets the universityFiscalYearStartMo attribute.
     * 
     * @param universityFiscalYearStartMo The universityFiscalYearStartMo to set.
     * 
     */
    public void setUniversityFiscalYearStartMo(String universityFiscalYearStartMo) {
        this.universityFiscalYearStartMo = universityFiscalYearStartMo;
    }

    /**
     * Gets the finObjectTypeIncomecashCode attribute.
     * 
     * @return Returns the finObjectTypeIncomecashCode
     * 
     */
    public String getFinObjectTypeIncomecashCode() {
        return finObjectTypeIncomecashCode;
    }

    /**
     * Sets the finObjectTypeIncomecashCode attribute.
     * 
     * @param finObjectTypeIncomecashCode The finObjectTypeIncomecashCode to set.
     * 
     */
    public void setFinObjectTypeIncomecashCode(String finObjectTypeIncomecashCode) {
        this.finObjectTypeIncomecashCode = finObjectTypeIncomecashCode;
    }

    /**
     * Gets the finObjTypeExpenditureexpCd attribute.
     * 
     * @return Returns the finObjTypeExpenditureexpCd
     * 
     */
    public String getFinObjTypeExpenditureexpCd() {
        return finObjTypeExpenditureexpCd;
    }

    /**
     * Sets the finObjTypeExpenditureexpCd attribute.
     * 
     * @param finObjTypeExpenditureexpCd The finObjTypeExpenditureexpCd to set.
     * 
     */
    public void setFinObjTypeExpenditureexpCd(String finObjTypeExpenditureexpCd) {
        this.finObjTypeExpenditureexpCd = finObjTypeExpenditureexpCd;
    }

    /**
     * Gets the finObjTypeExpendNotExpCode attribute.
     * 
     * @return Returns the finObjTypeExpendNotExpCode
     * 
     */
    public String getFinObjTypeExpendNotExpCode() {
        return finObjTypeExpendNotExpCode;
    }

    /**
     * Sets the finObjTypeExpendNotExpCode attribute.
     * 
     * @param finObjTypeExpendNotExpCode The finObjTypeExpendNotExpCode to set.
     * 
     */
    public void setFinObjTypeExpendNotExpCode(String finObjTypeExpendNotExpCode) {
        this.finObjTypeExpendNotExpCode = finObjTypeExpendNotExpCode;
    }

    /**
     * Gets the finObjTypeExpNotExpendCode attribute.
     * 
     * @return Returns the finObjTypeExpNotExpendCode
     * 
     */
    public String getFinObjTypeExpNotExpendCode() {
        return finObjTypeExpNotExpendCode;
    }

    /**
     * Sets the finObjTypeExpNotExpendCode attribute.
     * 
     * @param finObjTypeExpNotExpendCode The finObjTypeExpNotExpendCode to set.
     * 
     */
    public void setFinObjTypeExpNotExpendCode(String finObjTypeExpNotExpendCode) {
        this.finObjTypeExpNotExpendCode = finObjTypeExpNotExpendCode;
    }

    /**
     * Gets the financialObjectTypeAssetsCd attribute.
     * 
     * @return Returns the financialObjectTypeAssetsCd
     * 
     */
    public String getFinancialObjectTypeAssetsCd() {
        return financialObjectTypeAssetsCd;
    }

    /**
     * Sets the financialObjectTypeAssetsCd attribute.
     * 
     * @param financialObjectTypeAssetsCd The financialObjectTypeAssetsCd to set.
     * 
     */
    public void setFinancialObjectTypeAssetsCd(String financialObjectTypeAssetsCd) {
        this.financialObjectTypeAssetsCd = financialObjectTypeAssetsCd;
    }

    /**
     * Gets the finObjectTypeLiabilitiesCode attribute.
     * 
     * @return Returns the finObjectTypeLiabilitiesCode
     * 
     */
    public String getFinObjectTypeLiabilitiesCode() {
        return finObjectTypeLiabilitiesCode;
    }

    /**
     * Sets the finObjectTypeLiabilitiesCode attribute.
     * 
     * @param finObjectTypeLiabilitiesCode The finObjectTypeLiabilitiesCode to set.
     * 
     */
    public void setFinObjectTypeLiabilitiesCode(String finObjectTypeLiabilitiesCode) {
        this.finObjectTypeLiabilitiesCode = finObjectTypeLiabilitiesCode;
    }

    /**
     * Gets the finObjectTypeFundBalanceCd attribute.
     * 
     * @return Returns the finObjectTypeFundBalanceCd
     * 
     */
    public String getFinObjectTypeFundBalanceCd() {
        return finObjectTypeFundBalanceCd;
    }

    /**
     * Sets the finObjectTypeFundBalanceCd attribute.
     * 
     * @param finObjectTypeFundBalanceCd The finObjectTypeFundBalanceCd to set.
     * 
     */
    public void setFinObjectTypeFundBalanceCd(String finObjectTypeFundBalanceCd) {
        this.finObjectTypeFundBalanceCd = finObjectTypeFundBalanceCd;
    }

    /**
     * Gets the extrnlEncumFinBalanceTypCd attribute.
     * 
     * @return Returns the extrnlEncumFinBalanceTypCd
     * 
     */
    public String getExtrnlEncumFinBalanceTypCd() {
        return extrnlEncumFinBalanceTypCd;
    }

    /**
     * Sets the extrnlEncumFinBalanceTypCd attribute.
     * 
     * @param extrnlEncumFinBalanceTypCd The extrnlEncumFinBalanceTypCd to set.
     * 
     */
    public void setExtrnlEncumFinBalanceTypCd(String extrnlEncumFinBalanceTypCd) {
        this.extrnlEncumFinBalanceTypCd = extrnlEncumFinBalanceTypCd;
    }

    /**
     * Gets the intrnlEncumFinBalanceTypCd attribute.
     * 
     * @return Returns the intrnlEncumFinBalanceTypCd
     * 
     */
    public String getIntrnlEncumFinBalanceTypCd() {
        return intrnlEncumFinBalanceTypCd;
    }

    /**
     * Sets the intrnlEncumFinBalanceTypCd attribute.
     * 
     * @param intrnlEncumFinBalanceTypCd The intrnlEncumFinBalanceTypCd to set.
     * 
     */
    public void setIntrnlEncumFinBalanceTypCd(String intrnlEncumFinBalanceTypCd) {
        this.intrnlEncumFinBalanceTypCd = intrnlEncumFinBalanceTypCd;
    }

    /**
     * Gets the preencumbranceFinBalTypeCd attribute.
     * 
     * @return Returns the preencumbranceFinBalTypeCd
     * 
     */
    public String getPreencumbranceFinBalTypeCd() {
        return preencumbranceFinBalTypeCd;
    }

    /**
     * Sets the preencumbranceFinBalTypeCd attribute.
     * 
     * @param preencumbranceFinBalTypeCd The preencumbranceFinBalTypeCd to set.
     * 
     */
    public void setPreencumbranceFinBalTypeCd(String preencumbranceFinBalTypeCd) {
        this.preencumbranceFinBalTypeCd = preencumbranceFinBalTypeCd;
    }

    /**
     * Gets the eliminationsFinBalanceTypeCd attribute.
     * 
     * @return Returns the eliminationsFinBalanceTypeCd
     * 
     */
    public String getEliminationsFinBalanceTypeCd() {
        return eliminationsFinBalanceTypeCd;
    }

    /**
     * Sets the eliminationsFinBalanceTypeCd attribute.
     * 
     * @param eliminationsFinBalanceTypeCd The eliminationsFinBalanceTypeCd to set.
     * 
     */
    public void setEliminationsFinBalanceTypeCd(String eliminationsFinBalanceTypeCd) {
        this.eliminationsFinBalanceTypeCd = eliminationsFinBalanceTypeCd;
    }

    /**
     * Gets the finObjTypeIncomeNotCashCd attribute.
     * 
     * @return Returns the finObjTypeIncomeNotCashCd
     * 
     */
    public String getFinObjTypeIncomeNotCashCd() {
        return finObjTypeIncomeNotCashCd;
    }

    /**
     * Sets the finObjTypeIncomeNotCashCd attribute.
     * 
     * @param finObjTypeIncomeNotCashCd The finObjTypeIncomeNotCashCd to set.
     * 
     */
    public void setFinObjTypeIncomeNotCashCd(String finObjTypeIncomeNotCashCd) {
        this.finObjTypeIncomeNotCashCd = finObjTypeIncomeNotCashCd;
    }

    /**
     * Gets the finObjTypeCshNotIncomeCd attribute.
     * 
     * @return Returns the finObjTypeCshNotIncomeCd
     * 
     */
    public String getFinObjTypeCshNotIncomeCd() {
        return finObjTypeCshNotIncomeCd;
    }

    /**
     * Sets the finObjTypeCshNotIncomeCd attribute.
     * 
     * @param finObjTypeCshNotIncomeCd The finObjTypeCshNotIncomeCd to set.
     * 
     */
    public void setFinObjTypeCshNotIncomeCd(String finObjTypeCshNotIncomeCd) {
        this.finObjTypeCshNotIncomeCd = finObjTypeCshNotIncomeCd;
    }

    /**
     * Gets the universityFiscalYearName attribute.
     * 
     * @return Returns the universityFiscalYearName
     * 
     */
    public String getUniversityFiscalYearName() {
        return universityFiscalYearName;
    }

    /**
     * Sets the universityFiscalYearName attribute.
     * 
     * @param universityFiscalYearName The universityFiscalYearName to set.
     * 
     */
    public void setUniversityFiscalYearName(String universityFiscalYearName) {
        this.universityFiscalYearName = universityFiscalYearName;
    }

    /**
     * Gets the financialBeginBalanceLoadInd attribute.
     * 
     * @return Returns the financialBeginBalanceLoadInd
     * 
     */
    public boolean isFinancialBeginBalanceLoadInd() {
        return financialBeginBalanceLoadInd;
    }

    /**
     * Sets the financialBeginBalanceLoadInd attribute.
     * 
     * @param financialBeginBalanceLoadInd The financialBeginBalanceLoadInd to set.
     * 
     */
    public void setFinancialBeginBalanceLoadInd(boolean financialBeginBalanceLoadInd) {
        this.financialBeginBalanceLoadInd = financialBeginBalanceLoadInd;
    }

    /**
     * Gets the universityFinChartOfAcct attribute.
     * 
     * @return Returns the universityFinChartOfAcct
     * 
     */
    public Chart getUniversityFinChartOfAcct() {
        return universityFinChartOfAcct;
    }

    /**
     * Sets the universityFinChartOfAcct attribute.
     * 
     * @param universityFinChartOfAcct The universityFinChartOfAcct to set.
     * @deprecated
     */
    public void setUniversityFinChartOfAcct(Chart universityFinChartOfAcct) {
        this.universityFinChartOfAcct = universityFinChartOfAcct;
    }

    /**
     * @return Returns the universityFinChartOfAcctCd.
     */
    public String getUniversityFinChartOfAcctCd() {
        return universityFinChartOfAcctCd;
    }

    /**
     * @param universityFinChartOfAcctCd The universityFinChartOfAcctCd to set.
     */
    public void setUniversityFinChartOfAcctCd(String universityFinChartOfAcctCd) {
        this.universityFinChartOfAcctCd = universityFinChartOfAcctCd;
    }

    /**
     * @return Returns the actualFinancialBalanceType.
     */
    public BalanceTyp getActualFinancialBalanceType() {
        return actualFinancialBalanceType;
    }

    /**
     * @param actualFinancialBalanceType The actualFinancialBalanceType to set.
     */
    public void setActualFinancialBalanceType(BalanceTyp actualFinancialBalanceType) {
        this.actualFinancialBalanceType = actualFinancialBalanceType;
    }

    /**
     * @return Returns the budgetCheckingBalanceType.
     */
    public BalanceTyp getBudgetCheckingBalanceType() {
        return budgetCheckingBalanceType;
    }

    /**
     * @param budgetCheckingBalanceType The budgetCheckingBalanceType to set.
     */
    public void setBudgetCheckingBalanceType(BalanceTyp budgetCheckingBalanceType) {
        this.budgetCheckingBalanceType = budgetCheckingBalanceType;
    }

    /**
     * @return Returns the eliminationsFinBalanceType.
     */
    public BalanceTyp getEliminationsFinBalanceType() {
        return eliminationsFinBalanceType;
    }

    /**
     * @param eliminationsFinBalanceType The eliminationsFinBalanceType to set.
     */
    public void setEliminationsFinBalanceType(BalanceTyp eliminationsFinBalanceType) {
        this.eliminationsFinBalanceType = eliminationsFinBalanceType;
    }

    /**
     * @return Returns the extrnlEncumFinBalanceTyp.
     */
    public BalanceTyp getExtrnlEncumFinBalanceTyp() {
        return extrnlEncumFinBalanceTyp;
    }

    /**
     * @param extrnlEncumFinBalanceTyp The extrnlEncumFinBalanceTyp to set.
     */
    public void setExtrnlEncumFinBalanceTyp(BalanceTyp extrnlEncumFinBalanceTyp) {
        this.extrnlEncumFinBalanceTyp = extrnlEncumFinBalanceTyp;
    }

    /**
     * @return Returns the financialObjectTypeAssets.
     */
    public ObjectType getFinancialObjectTypeAssets() {
        return financialObjectTypeAssets;
    }

    /**
     * @param financialObjectTypeAssets The financialObjectTypeAssets to set.
     */
    public void setFinancialObjectTypeAssets(ObjectType financialObjectTypeAssets) {
        this.financialObjectTypeAssets = financialObjectTypeAssets;
    }

    /**
     * @return Returns the finObjectTypeFundBalance.
     */
    public ObjectType getFinObjectTypeFundBalance() {
        return finObjectTypeFundBalance;
    }

    /**
     * @param finObjectTypeFundBalance The finObjectTypeFundBalance to set.
     */
    public void setFinObjectTypeFundBalance(ObjectType finObjectTypeFundBalance) {
        this.finObjectTypeFundBalance = finObjectTypeFundBalance;
    }

    /**
     * @return Returns the finObjectTypeLiabilities.
     */
    public ObjectType getFinObjectTypeLiabilities() {
        return finObjectTypeLiabilities;
    }

    /**
     * @param finObjectTypeLiabilities The finObjectTypeLiabilities to set.
     */
    public void setFinObjectTypeLiabilities(ObjectType finObjectTypeLiabilities) {
        this.finObjectTypeLiabilities = finObjectTypeLiabilities;
    }

    /**
     * @return Returns the finObjTypeCshNotIncome.
     */
    public ObjectType getFinObjTypeCshNotIncome() {
        return finObjTypeCshNotIncome;
    }

    /**
     * @param finObjTypeCshNotIncome The finObjTypeCshNotIncome to set.
     */
    public void setFinObjTypeCshNotIncome(ObjectType finObjTypeCshNotIncome) {
        this.finObjTypeCshNotIncome = finObjTypeCshNotIncome;
    }

    /**
     * @return Returns the finObjTypeExpenditureexp.
     */
    public ObjectType getFinObjTypeExpenditureexp() {
        return finObjTypeExpenditureexp;
    }

    /**
     * @param finObjTypeExpenditureexp The finObjTypeExpenditureexp to set.
     */
    public void setFinObjTypeExpenditureexp(ObjectType finObjTypeExpenditureexp) {
        this.finObjTypeExpenditureexp = finObjTypeExpenditureexp;
    }

    /**
     * @return Returns the finObjTypeExpendNotExp.
     */
    public ObjectType getFinObjTypeExpendNotExp() {
        return finObjTypeExpendNotExp;
    }

    /**
     * @param finObjTypeExpendNotExp The finObjTypeExpendNotExp to set.
     */
    public void setFinObjTypeExpendNotExp(ObjectType finObjTypeExpendNotExp) {
        this.finObjTypeExpendNotExp = finObjTypeExpendNotExp;
    }

    /**
     * @return Returns the finObjTypeExpNotExpend.
     */
    public ObjectType getFinObjTypeExpNotExpend() {
        return finObjTypeExpNotExpend;
    }

    /**
     * @param finObjTypeExpNotExpend The finObjTypeExpNotExpend to set.
     */
    public void setFinObjTypeExpNotExpend(ObjectType finObjTypeExpNotExpend) {
        this.finObjTypeExpNotExpend = finObjTypeExpNotExpend;
    }

    /**
     * @return Returns the finObjTypeIncomeNotCash.
     */
    public ObjectType getFinObjTypeIncomeNotCash() {
        return finObjTypeIncomeNotCash;
    }

    /**
     * @param finObjTypeIncomeNotCash The finObjTypeIncomeNotCash to set.
     */
    public void setFinObjTypeIncomeNotCash(ObjectType finObjTypeIncomeNotCash) {
        this.finObjTypeIncomeNotCash = finObjTypeIncomeNotCash;
    }

    /**
     * @return Returns the intrnlEncumFinBalanceTyp.
     */
    public BalanceTyp getIntrnlEncumFinBalanceTyp() {
        return intrnlEncumFinBalanceTyp;
    }

    /**
     * @param intrnlEncumFinBalanceTyp The intrnlEncumFinBalanceTyp to set.
     */
    public void setIntrnlEncumFinBalanceTyp(BalanceTyp intrnlEncumFinBalanceTyp) {
        this.intrnlEncumFinBalanceTyp = intrnlEncumFinBalanceTyp;
    }

    /**
     * @return Returns the objectType.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * @param objectType The objectType to set.
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    /**
     * @return Returns the preencumbranceFinBalType.
     */
    public BalanceTyp getPreencumbranceFinBalType() {
        return preencumbranceFinBalType;
    }

    /**
     * @param preencumbranceFinBalType The preencumbranceFinBalType to set.
     */
    public void setPreencumbranceFinBalType(BalanceTyp preencumbranceFinBalType) {
        this.preencumbranceFinBalType = preencumbranceFinBalType;
    }

    /**
     * Gets the costShareEncumbranceBalanceTypeCode attribute.
     * 
     * @return Returns the costShareEncumbranceBalanceTypeCode.
     */
    public String getCostShareEncumbranceBalanceTypeCode() {
        return costShareEncumbranceBalanceTypeCode;
    }

    /**
     * Sets the costShareEncumbranceBalanceTypeCode attribute value.
     * 
     * @param costShareEncumbranceBalanceTypeCode The costShareEncumbranceBalanceTypeCode to set.
     */
    public void setCostShareEncumbranceBalanceTypeCode(String costShareEncumbranceBalanceTypeCode) {
        this.costShareEncumbranceBalanceTypeCode = costShareEncumbranceBalanceTypeCode;
    }

    /**
     * Gets the costShareEncumbranceBalanceType attribute.
     * 
     * @return Returns the costShareEncumbranceBalanceType.
     */
    public BalanceTyp getCostShareEncumbranceBalanceType() {
        return costShareEncumbranceBalanceType;
    }

    /**
     * Sets the costShareEncumbranceBalanceType attribute value.
     * 
     * @param costShareEncumbranceBalanceType The costShareEncumbranceBalanceType to set.
     * @deprecated
     */
    public void setCostShareEncumbranceBalanceType(BalanceTyp costShareEncumbranceBalanceType) {
        this.costShareEncumbranceBalanceType = costShareEncumbranceBalanceType;
    }

    /**
     * Gets the baseBudgetFinancialBalanceTypeCode attribute.
     * 
     * @return Returns the baseBudgetFinancialBalanceTypeCode.
     */
    public String getBaseBudgetFinancialBalanceTypeCode() {
        return baseBudgetFinancialBalanceTypeCode;
    }

    /**
     * Sets the baseBudgetFinancialBalanceTypeCode attribute value.
     * 
     * @param baseBudgetFinancialBalanceTypeCode The baseBudgetFinancialBalanceTypeCode to set.
     */
    public void setBaseBudgetFinancialBalanceTypeCode(String baseBudgetFinancialBalanceTypeCode) {
        this.baseBudgetFinancialBalanceTypeCode = baseBudgetFinancialBalanceTypeCode;
    }

    /**
     * Gets the monthlyBudgetFinancialBalanceTypeCode attribute.
     * 
     * @return Returns the monthlyBudgetFinancialBalanceTypeCode.
     */
    public String getMonthlyBudgetFinancialBalanceTypeCode() {
        return monthlyBudgetFinancialBalanceTypeCode;
    }

    /**
     * Sets the monthlyBudgetFinancialBalanceTypeCode attribute value.
     * 
     * @param monthlyBudgetFinancialBalanceTypeCode The monthlyBudgetFinancialBalanceTypeCode to set.
     */
    public void setMonthlyBudgetFinancialBalanceTypeCode(String monthlyBudgetFinancialBalanceTypeCode) {
        this.monthlyBudgetFinancialBalanceTypeCode = monthlyBudgetFinancialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeTransferIncomeCode attribute.
     * 
     * @return Returns the financialObjectTypeTransferIncomeCode.
     */
    public String getFinancialObjectTypeTransferIncomeCode() {
        return financialObjectTypeTransferIncomeCode;
    }

    /**
     * Sets the financialObjectTypeTransferIncomeCode attribute value.
     * 
     * @param financialObjectTypeTransferIncomeCode The financialObjectTypeTransferIncomeCode to set.
     */
    public void setFinancialObjectTypeTransferIncomeCode(String financialObjectTypeTransferIncomeCode) {
        this.financialObjectTypeTransferIncomeCode = financialObjectTypeTransferIncomeCode;
    }

    /**
     * Gets the financialObjectTypeTransferExpenseCode attribute.
     * 
     * @return Returns the financialObjectTypeTransferExpenseCode.
     */
    public String getFinancialObjectTypeTransferExpenseCode() {
        return financialObjectTypeTransferExpenseCode;
    }

    /**
     * Sets the financialObjectTypeTransferExpenseCode attribute value.
     * 
     * @param financialObjectTypeTransferExpenseCode The financialObjectTypeTransferExpenseCode to set.
     */
    public void setFinancialObjectTypeTransferExpenseCode(String financialObjectTypeTransferExpenseCode) {
        this.financialObjectTypeTransferExpenseCode = financialObjectTypeTransferExpenseCode;
    }

    /**
     * Gets the financialObjectTypeTransferIncome attribute.
     * 
     * @return Returns the financialObjectTypeTransferIncome.
     */
    public ObjectType getFinancialObjectTypeTransferIncome() {
        return financialObjectTypeTransferIncome;
    }

    /**
     * Sets the financialObjectTypeTransferIncome attribute value.
     * 
     * @param financialObjectTypeTransferIncome The financialObjectTypeTransferIncome to set.
     * @deprecated
     */
    public void setFinancialObjectTypeTransferIncome(ObjectType financialObjectTypeTransferIncome) {
        this.financialObjectTypeTransferIncome = financialObjectTypeTransferIncome;
    }

    /**
     * Gets the financialObjectTypeTransferExpense attribute.
     * 
     * @return Returns the financialObjectTypeTransferExpense.
     */
    public ObjectType getFinancialObjectTypeTransferExpense() {
        return financialObjectTypeTransferExpense;
    }

    /**
     * Sets the financialObjectTypeTransferExpense attribute value.
     * 
     * @param financialObjectTypeTransferExpense The financialObjectTypeTransferExpense to set.
     * @deprecated
     */
    public void setFinancialObjectTypeTransferExpense(ObjectType financialObjectTypeTransferExpense) {
        this.financialObjectTypeTransferExpense = financialObjectTypeTransferExpense;
    }

    /**
     * Gets the baseBudgetFinancialBalanceType attribute.
     * 
     * @return Returns the baseBudgetFinancialBalanceType.
     */
    public BalanceTyp getBaseBudgetFinancialBalanceType() {
        return baseBudgetFinancialBalanceType;
    }

    /**
     * Sets the baseBudgetFinancialBalanceType attribute value.
     * 
     * @param baseBudgetFinancialBalanceType The baseBudgetFinancialBalanceType to set.
     * @deprecated
     */
    public void setBaseBudgetFinancialBalanceType(BalanceTyp baseBudgetFinancialBalanceType) {
        this.baseBudgetFinancialBalanceType = baseBudgetFinancialBalanceType;
    }

    /**
     * Gets the monthlyBudgetFinancialBalanceType attribute.
     * 
     * @return Returns the monthlyBudgetFinancialBalanceType.
     */
    public BalanceTyp getMonthlyBudgetFinancialBalanceType() {
        return monthlyBudgetFinancialBalanceType;
    }

    /**
     * Sets the monthlyBudgetFinancialBalanceType attribute value.
     * 
     * @param monthlyBudgetFinancialBalanceType The monthlyBudgetFinancialBalanceType to set.
     * @deprecated
     */
    public void setMonthlyBudgetFinancialBalanceType(BalanceTyp monthlyBudgetFinancialBalanceType) {
        this.monthlyBudgetFinancialBalanceType = monthlyBudgetFinancialBalanceType;
    }

    /**
     * Gets the nominalFinancialBalanceTypeCode attribute.
     * 
     * @return Returns the nominalFinancialBalanceTypeCode.
     */
    public String getNominalFinancialBalanceTypeCode() {
        return nominalFinancialBalanceTypeCode;
    }

    /**
     * Sets the nominalFinancialBalanceTypeCode attribute value.
     * 
     * @param nominalFinancialBalanceTypeCode The nominalFinancialBalanceTypeCode to set.
     */
    public void setNominalFinancialBalanceTypeCode(String nominalFinancialBalanceTypeCode) {
        this.nominalFinancialBalanceTypeCode = nominalFinancialBalanceTypeCode;
    }

    /**
     * Gets the nominalFinancialBalanceType attribute.
     * 
     * @return Returns the nominalFinancialBalanceType.
     */
    public BalanceTyp getNominalFinancialBalanceType() {
        return nominalFinancialBalanceType;
    }

    /**
     * Sets the nominalFinancialBalanceType attribute value.
     * 
     * @param nominalFinancialBalanceType The nominalFinancialBalanceType to set.
     * @deprecated
     */
    public void setNominalFinancialBalanceType(BalanceTyp nominalFinancialBalanceType) {
        this.nominalFinancialBalanceType = nominalFinancialBalanceType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("universityFiscalYear", getUniversityFiscalYear());
        return map;
    }

}
