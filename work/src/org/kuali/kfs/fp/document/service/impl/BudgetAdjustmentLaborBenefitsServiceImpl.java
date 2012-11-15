/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentAccountingLine;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.document.service.BudgetAdjustmentLaborBenefitsService;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the default implementation of the methods defined by the BudgetAdjustmentLaborBenefitsService. These service performs
 * methods related to the generation of labor benefit accounting lines for the budget adjustment document.
 */
public class BudgetAdjustmentLaborBenefitsServiceImpl implements BudgetAdjustmentLaborBenefitsService {
    private BusinessObjectService businessObjectService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentLaborBenefitsServiceImpl.class);

    /**
     * This method generated labor benefit accounting lines to be added to the BudgetDocument provided.
     *
     * @param budgetDocument The BudgetDocument to have the new labor benefit accounting lines added to.
     * @see org.kuali.kfs.fp.document.service.BudgetAdjustmentLaborBenefitsService#generateLaborBenefitsAccountingLines(org.kuali.kfs.fp.document.BudgetAdjustmentDocument)
     */
    @Override
    public void generateLaborBenefitsAccountingLines(BudgetAdjustmentDocument budgetDocument) {
        Integer fiscalYear = budgetDocument.getPostingYear();

        List accountingLines = new ArrayList();
        accountingLines.addAll(budgetDocument.getSourceAccountingLines());
        accountingLines.addAll(budgetDocument.getTargetAccountingLines());

        /*
         * find lines that have labor object codes, then retrieve the benefit calculation records for the object code. Finally, for
         * each benefit record, create an accounting line with properties set from the original line, but substituted with the
         * benefit object code and calculated current and base amount.
         */
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();

            // check if the line was previousely generated benefit line, if so delete and skip
            if (line.isFringeBenefitIndicator()) {
                if (line.isSourceAccountingLine()) {
                    budgetDocument.getSourceAccountingLines().remove(line);
                }
                else {
                    budgetDocument.getTargetAccountingLines().remove(line);
                }
                continue;
            }

            List<BudgetAdjustmentAccountingLine> benefitLines = generateBenefitLines(fiscalYear, line, budgetDocument);

            for (BudgetAdjustmentAccountingLine benefitLine : benefitLines) {
                if (benefitLine.isSourceAccountingLine()) {
                    budgetDocument.addSourceAccountingLine((SourceAccountingLine) benefitLine);
                }
                else {
                    budgetDocument.addTargetAccountingLine((TargetAccountingLine) benefitLine);
                }
            }
        }
    }

    /**
     * Given a budget adjustment accounting line, generates appropriate fringe benefit lines for the line
     *
     * @param line a line to generate fringe benefit lines for
     * @return a List of BudgetAdjustmentAccountingLines to add to the document as fringe benefit lines
     */
    protected List<BudgetAdjustmentAccountingLine> generateBenefitLines(Integer fiscalYear, BudgetAdjustmentAccountingLine line, BudgetAdjustmentDocument document) {
        List<BudgetAdjustmentAccountingLine> fringeLines = new ArrayList<BudgetAdjustmentAccountingLine>();
        try {
            Collection<LaborLedgerPositionObjectBenefit> objectBenefits = SpringContext.getBean(LaborModuleService.class).retrieveActiveLaborPositionObjectBenefits(fiscalYear, line.getChartOfAccountsCode(), line.getFinancialObjectCode());
            if (objectBenefits != null) {
                for (LaborLedgerPositionObjectBenefit fringeBenefitInformation : objectBenefits) {
                    // now create and set properties for the benefit line
                    BudgetAdjustmentAccountingLine benefitLine = null;
                    if (line.isSourceAccountingLine()) {
                        benefitLine = (BudgetAdjustmentAccountingLine) document.getSourceAccountingLineClass().newInstance();
                    }
                    else {
                        benefitLine = (BudgetAdjustmentAccountingLine) document.getTargetAccountingLineClass().newInstance();
                    }

                    // create a map to use in the lookup of the account
                    Map<String, Object> fieldValues = new HashMap<String, Object>();
                    fieldValues.put("chartOfAccountsCode", line.getChartOfAccountsCode());
                    fieldValues.put("accountNumber", line.getAccountNumber());
                    // use the budget adjustment accounting line to get the account number that will then be used to lookup the
                    // labor benefit rate category code
                    Account lookupAccount = businessObjectService.findByPrimaryKey(Account.class, fieldValues);
                    LaborLedgerBenefitsCalculation benefitsCalculation = null;
                    String laborBenefitsRateCategoryCode = "";
                    // make sure the parameter exists
                    if (SpringContext.getBean(ParameterService.class).parameterExists(Account.class, KFSParameterKeyConstants.LdParameterConstants.DEFAULT_BENEFIT_RATE_CATEGORY_CODE)) {
                        laborBenefitsRateCategoryCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Account.class, KFSParameterKeyConstants.LdParameterConstants.DEFAULT_BENEFIT_RATE_CATEGORY_CODE);
                    }
                    else {
                        laborBenefitsRateCategoryCode = "";
                    }
                    // make sure the system parameter exists
                    if (SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KFSParameterKeyConstants.LdParameterConstants.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND)) {
                        // check the system param to see if the labor benefit rate category should be filled in
                        String sysParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KFSParameterKeyConstants.LdParameterConstants.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND);
                        LOG.debug("sysParam: " + sysParam);
                        // if sysParam == Y then Labor Benefit Rate Category should be used in the search
                        if (sysParam.equalsIgnoreCase("Y")) {


                            if (StringUtils.isBlank(line.getSubAccount().getSubAccountNumber())) {
                                laborBenefitsRateCategoryCode = lookupAccount.getLaborBenefitRateCategoryCode();
                            }
                            else {
                                laborBenefitsRateCategoryCode = SpringContext.getBean(LaborModuleService.class).getBenefitRateCategoryCode(line.getChartOfAccountsCode(), line.getAccountNumber(), line.getSubAccount().getSubAccountNumber());
                            }

                            // make sure laborBenefitsRateCategoryCode isn't null
                            if (ObjectUtils.isNull(laborBenefitsRateCategoryCode)) {
                                // make sure the parameter exists
                                if (SpringContext.getBean(ParameterService.class).parameterExists(Account.class, KFSParameterKeyConstants.LdParameterConstants.DEFAULT_BENEFIT_RATE_CATEGORY_CODE)) {
                                    laborBenefitsRateCategoryCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Account.class, KFSParameterKeyConstants.LdParameterConstants.DEFAULT_BENEFIT_RATE_CATEGORY_CODE);
                                }
                                else {
                                    laborBenefitsRateCategoryCode = "";
                                }
                            }


                        }
                    }

                    String beneCalc = "{" + fringeBenefitInformation.getUniversityFiscalYear() + "," + fringeBenefitInformation.getChartOfAccountsCode() + "," + fringeBenefitInformation.getFinancialObjectBenefitsTypeCode() + "," + laborBenefitsRateCategoryCode + "}";
                    LOG.info("Looking for a benefits calculation for " + beneCalc);
                    // get the benefits calculation taking the laborBenefitRateCategoryCode into account
                    benefitsCalculation = fringeBenefitInformation.getLaborLedgerBenefitsCalculation(laborBenefitsRateCategoryCode);

                    if (benefitsCalculation != null) {
                        LOG.info("Found benefits calculation for " + beneCalc);
                    }
                    else {
                        LOG.info("Couldn't locate a benefits calculation for " + beneCalc);
                    }
                    if ((benefitsCalculation != null) && benefitsCalculation.isActive()) {
                        benefitLine.copyFrom(line);
                        benefitLine.setFinancialObjectCode(benefitsCalculation.getPositionFringeBenefitObjectCode());
                        benefitLine.refreshNonUpdateableReferences();
                        LaborModuleService laborModuleService = SpringContext.getBean(LaborModuleService.class);
                        if (ObjectUtils.isNotNull(laborModuleService.getCostSharingSourceAccountNumber())) {
                            benefitLine.setAccountNumber(laborModuleService.getCostSharingSourceAccountNumber());
                            benefitLine.setSubAccountNumber(laborModuleService.getCostSharingSourceSubAccountNumber());
                            benefitLine.setChartOfAccountsCode(laborModuleService.getCostSharingSourceChartOfAccountsCode());
                        }

                        benefitLine.refreshNonUpdateableReferences();

                        // convert whole percentage to decimal value (5% to .0500, 18.66% to 0.1866)
                        BigDecimal fringeBenefitPercent = formatPercentageForMultiplication(benefitsCalculation.getPositionFringeBenefitPercent());
                        // compute the benefit current amount with all decimals and then round it to the closest integer by setting the
                        // scale to 0 and using the round half up rounding mode: exp. 1200*0.1866 = 223.92 -> rounded to 224
                        BigDecimal benefitCurrentAmount = line.getCurrentBudgetAdjustmentAmount().bigDecimalValue().multiply(fringeBenefitPercent);
                        benefitCurrentAmount = benefitCurrentAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
                        benefitLine.setCurrentBudgetAdjustmentAmount(new KualiDecimal(benefitCurrentAmount));

                        KualiInteger benefitBaseAmount = line.getBaseBudgetAdjustmentAmount().multiply(fringeBenefitPercent);
                        benefitLine.setBaseBudgetAdjustmentAmount(benefitBaseAmount);

                        // clear monthly lines per KULEDOCS-1606
                        benefitLine.clearFinancialDocumentMonthLineAmounts();

                        // set flag on line so we know it was a generated benefit line and can clear it out later if needed
                        benefitLine.setFringeBenefitIndicator(true);

                        fringeLines.add(benefitLine);
                    }
                }
            }
        }
        catch (InstantiationException ie) {
            // it's doubtful this catch block or the catch block below are ever accessible, as accounting lines should already have
            // been generated
            // for the document. But we can still make it somebody else's problem
            throw new RuntimeException(ie);
        }
        catch (IllegalAccessException iae) {
            // with some luck we'll pass the buck now sez some other dev "This sucks!" Get your Runtime on!
            // but really...we'll never make it this far. I promise.
            throw new RuntimeException(iae);
        }

        return fringeLines;
    }


    /**
     * @param budgetDocument
     * @return
     * @see org.kuali.kfs.fp.document.service.BudgetAdjustmentLaborBenefitsService#hasLaborObjectCodes(org.kuali.kfs.fp.document.BudgetAdjustmentDocument)
     */
    @Override
    public boolean hasLaborObjectCodes(BudgetAdjustmentDocument budgetDocument) {
        boolean hasLaborObjectCodes = false;

        List<AccountingLine> accountingLines = new ArrayList<AccountingLine>();
        accountingLines.addAll(budgetDocument.getSourceAccountingLines());
        accountingLines.addAll(budgetDocument.getTargetAccountingLines());

        Integer fiscalYear = budgetDocument.getPostingYear();
        LaborModuleService laborModuleService = SpringContext.getBean(LaborModuleService.class);

        for (AccountingLine line : accountingLines) {
            if (laborModuleService.hasFringeBenefitProducingObjectCodes(fiscalYear, line.getChartOfAccountsCode(), line.getFinancialObjectCode())) {
                hasLaborObjectCodes = true;
                break;
            }
        }

        return hasLaborObjectCodes;
    }


    /**
     * Formats the stored percentage to be used in multiplication. For example if the percentage is 18.66 it will return 0.1866. The
     * returned number will always have 4 digits.
     *
     * @param percent the stored percent
     * @return percentage formatted for multiplication
     */
    protected BigDecimal formatPercentageForMultiplication(KualiDecimal percent) {

        BigDecimal result = BigDecimal.ZERO;

        if (percent != null) {
            result = percent.bigDecimalValue().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
        }

        return result;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
