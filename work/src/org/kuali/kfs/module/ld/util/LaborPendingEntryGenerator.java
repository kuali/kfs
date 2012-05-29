/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.BenefitsCalculation;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.service.LaborPositionObjectBenefitService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is used to help generating pending entries for the given labor documents
 */
public class LaborPendingEntryGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborPendingEntryGenerator.class);

    /**
     * generate the expense pending entries based on the given document and accouting line
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @return a set of expense pending entries
     */
    public static List<LaborLedgerPendingEntry> generateExpensePendingEntries(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        List<LaborLedgerPendingEntry> expensePendingEntries = new ArrayList<LaborLedgerPendingEntry>();
        LaborLedgerPendingEntry expensePendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getExpensePendingEntry(document, accountingLine, sequenceHelper);
        expensePendingEntries.add(expensePendingEntry);

        // if the AL's pay FY and period do not match the University fiscal year and period need to create a reversal entry for
        // current period
        if (!isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(document, accountingLine)) {
            LaborLedgerPendingEntry expenseA21PendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getExpenseA21PendingEntry(document, accountingLine, sequenceHelper);
            expensePendingEntries.add(expenseA21PendingEntry);

            LaborLedgerPendingEntry expenseA21ReversalPendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getExpenseA21ReversalPendingEntry(document, accountingLine, sequenceHelper);
            expensePendingEntries.add(expenseA21ReversalPendingEntry);
        }

        //refresh nonupdateable references for financial object...
        refreshObjectCodeNonUpdateableReferences(expensePendingEntries);
        
        return expensePendingEntries;
    }

    /**
     * generate the benefit pending entries based on the given document and accounting line
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @return a set of benefit pending entries
     */
    public static List<LaborLedgerPendingEntry> generateBenefitPendingEntries(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.LABOR_OBJECT);
        if (ObjectUtils.isNull(accountingLine.getLaborObject())) {
            return null;
        }

        String FringeOrSalaryCode = accountingLine.getLaborObject().getFinancialObjectFringeOrSalaryCode();
        if (!LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE.equals(FringeOrSalaryCode)) {
            return null;
        }

        Integer payrollFiscalyear = accountingLine.getPayrollEndDateFiscalYear();
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String objectCode = accountingLine.getFinancialObjectCode();
        Collection<PositionObjectBenefit> positionObjectBenefits = SpringContext.getBean(LaborPositionObjectBenefitService.class).getPositionObjectBenefits(payrollFiscalyear, chartOfAccountsCode, objectCode);

        List<LaborLedgerPendingEntry> benefitPendingEntries = new ArrayList<LaborLedgerPendingEntry>();
        for (PositionObjectBenefit positionObjectBenefit : positionObjectBenefits) {
            positionObjectBenefit.setLaborBenefitRateCategoryCode(accountingLine.getAccount().getLaborBenefitRateCategoryCode());
            String fringeBenefitObjectCode = positionObjectBenefit.getBenefitsCalculation().getPositionFringeBenefitObjectCode();

            KualiDecimal benefitAmount = SpringContext.getBean(LaborBenefitsCalculationService.class).calculateFringeBenefit(positionObjectBenefit, accountingLine.getAmount(), accountingLine.getAccountNumber(), accountingLine.getSubAccountNumber());
            if (benefitAmount.isNonZero()) {
                
                //make sure the fringebenefitobjectcode is not null
                if(ObjectUtils.isNull(fringeBenefitObjectCode)){
                    String laborBenefitRateCategoryCode = "";
                    
                    //make sure the system parameter exists
                    if (SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND")) {
                        //parameter exists, get the benefit rate based off of the university fiscal year, chart of account code, labor benefit type code and labor benefit rate category code 
                        String sysParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, "ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND");
                        LOG.debug("sysParam: " + sysParam);
                        //if sysParam == Y then use the Labor Benefit Rate Category Code to help determine the fringe benefit rate
                        if (sysParam.equalsIgnoreCase("Y")) {
                            laborBenefitRateCategoryCode = positionObjectBenefit.getLaborBenefitRateCategoryCode();
                        }else{
                         // make sure the parameter exists
                            if (SpringContext.getBean(ParameterService.class).parameterExists(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE")) {
                                laborBenefitRateCategoryCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE");
                            }
                            else {
                                laborBenefitRateCategoryCode = "";
                            }
                        }
                    }
                    
                    if(StringUtils.isBlank(laborBenefitRateCategoryCode)){
                        laborBenefitRateCategoryCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Account.class, "DEFAULT_BENEFIT_RATE_CATEGORY_CODE");
                    }
                    
                    //create a  map for the search criteria to lookup the fringe benefit percentage 
                    Map<String, Object> fieldValues = new HashMap<String, Object>();
                    fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, positionObjectBenefit.getUniversityFiscalYear());
                    fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, positionObjectBenefit.getChartOfAccountsCode());
                    fieldValues.put(LaborPropertyConstants.POSITION_BENEFIT_TYPE_CODE, positionObjectBenefit.getFinancialObjectBenefitsTypeCode());
                    fieldValues.put("laborBenefitRateCategoryCode",laborBenefitRateCategoryCode);
                    BenefitsCalculation bc = (BenefitsCalculation) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BenefitsCalculation.class, fieldValues);
                    
                    fringeBenefitObjectCode = bc.getPositionFringeBenefitObjectCode();
                }
                
                List<LaborLedgerPendingEntry> pendingEntries = generateBenefitPendingEntries(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
                benefitPendingEntries.addAll(pendingEntries);
            }
        }

        return benefitPendingEntries;
    }

    /**
     * generate the benefit pending entries with the given benefit amount and finge benefit object code based on the given document
     * and accouting line
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @param benefitAmount the given benefit amount
     * @param fringeBenefitObjectCode the given finge benefit object code
     * @return a set of benefit pending entries with the given benefit amount and finge benefit object code
     */
    public static List<LaborLedgerPendingEntry> generateBenefitPendingEntries(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        List<LaborLedgerPendingEntry> benefitPendingEntries = new ArrayList<LaborLedgerPendingEntry>();
        LaborLedgerPendingEntry benefitPendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getBenefitPendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
        benefitPendingEntries.add(benefitPendingEntry);

        // if the AL's pay FY and period do not match the University fiscal year and period
        if (!isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(document, accountingLine)) {
            LaborLedgerPendingEntry benefitA21PendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getBenefitA21PendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
            benefitPendingEntries.add(benefitA21PendingEntry);

            LaborLedgerPendingEntry benefitA21ReversalPendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getBenefitA21ReversalPendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
            benefitPendingEntries.add(benefitA21ReversalPendingEntry);
        }

        //refresh nonupdateable references for financial object...
        refreshObjectCodeNonUpdateableReferences(benefitPendingEntries);   
        
        return benefitPendingEntries;
    }

    /**
     * generate the benefit clearing pending entries with the given benefit amount and fringe benefit object code based on the given
     * document and accouting line
     * 
     * @param document the given accounting document
     * @param sequenceHelper the given squence helper
     * @param accountNumber the given clearing account number
     * @param chartOfAccountsCode the given clearing chart of accounts code
     * @return a set of benefit clearing pending entries
     */
    public static List<LaborLedgerPendingEntry> generateBenefitClearingPendingEntries(LaborLedgerPostingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String accountNumber, String chartOfAccountsCode) {
        List<LaborLedgerPendingEntry> benefitClearingPendingEntries = new ArrayList<LaborLedgerPendingEntry>();

        Map<String, KualiDecimal> sourceLineBenefitAmountSum = new HashMap<String, KualiDecimal>();
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = document.getSourceAccountingLines();
        for (ExpenseTransferSourceAccountingLine accountingLine : sourceAccountingLines) {
            updateBenefitAmountSum(sourceLineBenefitAmountSum, accountingLine);
        }

        Map<String, KualiDecimal> targetLineBenefitAmountSum = new HashMap<String, KualiDecimal>();
        List<ExpenseTransferTargetAccountingLine> targetAccountingLines = document.getTargetAccountingLines();
        for (ExpenseTransferTargetAccountingLine accountingLine : targetAccountingLines) {
            updateBenefitAmountSum(targetLineBenefitAmountSum, accountingLine);
        }

        Set<String> benefitTypeCodes = new HashSet<String>();
        for (String key : targetLineBenefitAmountSum.keySet()) {
            benefitTypeCodes.add(key);
        }

        for (String key : sourceLineBenefitAmountSum.keySet()) {
            benefitTypeCodes.add(key);
        }

        for (String benefitTypeCode : benefitTypeCodes) {
            KualiDecimal targetAmount = KualiDecimal.ZERO;
            if (targetLineBenefitAmountSum.containsKey(benefitTypeCode)) {
                targetAmount = targetLineBenefitAmountSum.get(benefitTypeCode);
            }

            KualiDecimal sourceAmount = KualiDecimal.ZERO;
            if (sourceLineBenefitAmountSum.containsKey(benefitTypeCode)) {
                sourceAmount = sourceLineBenefitAmountSum.get(benefitTypeCode);
            }

            KualiDecimal clearingAmount = sourceAmount.subtract(targetAmount);
            if (clearingAmount.isNonZero()) {
                benefitClearingPendingEntries.add(SpringContext.getBean(LaborPendingEntryConverterService.class).getBenefitClearingPendingEntry(document, sequenceHelper, accountNumber, chartOfAccountsCode, benefitTypeCode, clearingAmount));
            }
        }

        //refresh nonupdateable references for financial object...
        refreshObjectCodeNonUpdateableReferences(benefitClearingPendingEntries);   
        
        return benefitClearingPendingEntries;
    }

    /**
     * update the benefit amount summary map based on the given accounting line
     * 
     * @param benefitAmountSumByBenefitType the given benefit amount summary map
     * @param accountingLine the given accounting line
     */
    protected static void updateBenefitAmountSum(Map<String, KualiDecimal> benefitAmountSumByBenefitType, ExpenseTransferAccountingLine accountingLine) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.LABOR_OBJECT);
        if (ObjectUtils.isNull(accountingLine.getLaborObject())) {
            return;
        }

        String FringeOrSalaryCode = accountingLine.getLaborObject().getFinancialObjectFringeOrSalaryCode();
        if (!LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE.equals(FringeOrSalaryCode)) {
            return;
        }

        Integer payrollFiscalyear = accountingLine.getPayrollEndDateFiscalYear();
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String objectCode = accountingLine.getFinancialObjectCode();

        Collection<PositionObjectBenefit> positionObjectBenefits = SpringContext.getBean(LaborPositionObjectBenefitService.class).getPositionObjectBenefits(payrollFiscalyear, chartOfAccountsCode, objectCode);
        for (PositionObjectBenefit positionObjectBenefit : positionObjectBenefits) {
            positionObjectBenefit.setLaborBenefitRateCategoryCode(accountingLine.getAccount().getLaborBenefitRateCategoryCode());
            String benefitTypeCode = positionObjectBenefit.getBenefitsCalculation().getPositionBenefitTypeCode();

            KualiDecimal benefitAmount = SpringContext.getBean(LaborBenefitsCalculationService.class).calculateFringeBenefit(positionObjectBenefit, accountingLine.getAmount(), accountingLine.getAccountNumber(), accountingLine.getSubAccountNumber());
            if (benefitAmountSumByBenefitType.containsKey(benefitTypeCode)) {
                benefitAmount = benefitAmount.add(benefitAmountSumByBenefitType.get(benefitTypeCode));
            }
            benefitAmountSumByBenefitType.put(benefitTypeCode, benefitAmount);
        }
    }

    /**
     * determine if the pay fiscal year and period from the accounting line match with its university fiscal year and period.
     * 
     * @param document the given document
     * @param accountingLine the given accounting line of the document
     * @return true if the pay fiscal year and period from the accounting line match with its university fiscal year and period;
     *         otherwise, false
     */
    protected static boolean isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine) {
        Integer fiscalYear = document.getPostingYear();
        Integer payFiscalYear = accountingLine.getPayrollEndDateFiscalYear();
        if (!fiscalYear.equals(payFiscalYear)) {
            return false;
        }

        String periodCode = document.getPostingPeriodCode();
        String payPeriodCode = accountingLine.getPayrollEndDateFiscalPeriodCode();
        if (!StringUtils.equals(periodCode, payPeriodCode)) {
            return false;
        }

        return true;
    }
    
    /**
     * refreshes labor ledger pending entry's object codes.
     * 
     * @param llpes
     */
    protected static void refreshObjectCodeNonUpdateableReferences(List<LaborLedgerPendingEntry> llpes) {
        BusinessObjectService bos = SpringContext.getBean(BusinessObjectService.class);

        //refresh nonupdateable references for financial object...
        Map<String, String> primaryKeys = new HashMap<String,String>();
        
        for (LaborLedgerPendingEntry llpe : llpes) {
            primaryKeys.put("financialObjectCode", llpe.getFinancialObjectCode());
            ObjectCode objectCode = bos.findByPrimaryKey(ObjectCode.class, primaryKeys);
            llpe.setFinancialObject(objectCode);
        }
    }
}
