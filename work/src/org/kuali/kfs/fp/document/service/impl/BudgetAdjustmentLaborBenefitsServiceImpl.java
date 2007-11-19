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
package org.kuali.module.financial.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.financial.service.BudgetAdjustmentLaborBenefitsService;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.PositionObjectBenefit;

/**
 * 
 * This is the default implementation of the methods defined by the BudgetAdjustmentLaborBenefitsService.
 * 
 * These service performs methods related to the generation of labor benefit accounting lines for the budget 
 * adjustment document.
 * 
 */
public class BudgetAdjustmentLaborBenefitsServiceImpl implements BudgetAdjustmentLaborBenefitsService {
    private BusinessObjectService businessObjectService;

    /**
     * This method generated labor benefit accounting lines to be added to the BudgetDocument provided.
     * 
     * @param budgetDocument The BudgetDocument to have the new labor benefit accounting lines added to.
     * 
     * @see org.kuali.module.financial.service.BudgetAdjustmentLaborBenefitsService#generateLaborBenefitsAccountingLines(org.kuali.module.financial.document.BudgetAdjustmentDocument)
     */
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
                if (line instanceof BudgetAdjustmentSourceAccountingLine) {
                    budgetDocument.getSourceAccountingLines().remove(line);
                }
                else {
                    budgetDocument.getTargetAccountingLines().remove(line);
                }
                continue;
            }

            Collection objectBenefits = retrieveLaborObjectBenefits(fiscalYear, line);
            if (objectBenefits != null) {
                for (Iterator iterator = objectBenefits.iterator(); iterator.hasNext();) {
                    PositionObjectBenefit objectBenefit = (PositionObjectBenefit) iterator.next();
                    BenefitsCalculation benefitsCalculation = objectBenefit.getBenefitsCalculation();

                    // now create and set properties for the benefit line
                    BudgetAdjustmentAccountingLine benefitLine = (BudgetAdjustmentAccountingLine) ObjectUtils.deepCopy(line);
                    benefitLine.setFinancialObjectCode(benefitsCalculation.getPositionFringeBenefitObjectCode());
                    benefitLine.refresh();

                    KualiDecimal benefitCurrentAmount = line.getCurrentBudgetAdjustmentAmount().multiply(benefitsCalculation.getPositionFringeBenefitPercent().toKualiDecimal());
                    benefitLine.setCurrentBudgetAdjustmentAmount(benefitCurrentAmount);

                    KualiInteger benefitBaseAmount = line.getBaseBudgetAdjustmentAmount().multiply(benefitsCalculation.getPositionFringeBenefitPercent().toKualiDecimal());
                    benefitLine.setBaseBudgetAdjustmentAmount(benefitBaseAmount);

                    // clear monthly lines per KULEDOCS-1606
                    benefitLine.clearFinancialDocumentMonthLineAmounts();
                    
                    // set flag on line so we know it was a generated benefit line and can clear it out later if needed
                    benefitLine.setFringeBenefitIndicator(true);

                    if (benefitLine instanceof BudgetAdjustmentSourceAccountingLine) {
                        budgetDocument.addSourceAccountingLine((SourceAccountingLine) benefitLine);
                    }
                    else {
                        budgetDocument.addTargetAccountingLine((TargetAccountingLine) benefitLine);
                    }
                }
            }
        }
    }


    /**
     * @param budgetDocument
     * @return 
     * 
     * @see org.kuali.module.financial.service.BudgetAdjustmentLaborBenefitsService#hasLaborObjectCodes(org.kuali.module.financial.document.BudgetAdjustmentDocument)
     */
    public boolean hasLaborObjectCodes(BudgetAdjustmentDocument budgetDocument) {
        boolean hasLaborObjectCodes = false;

        List accountingLines = new ArrayList();
        accountingLines.addAll(budgetDocument.getSourceAccountingLines());
        accountingLines.addAll(budgetDocument.getTargetAccountingLines());

        Integer fiscalYear = budgetDocument.getPostingYear();
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            Collection objectBenefits = retrieveLaborObjectBenefits(fiscalYear, line);
            if (objectBenefits != null && !objectBenefits.isEmpty()) {
                hasLaborObjectCodes = true;
                break;
            }
        }

        return hasLaborObjectCodes;
    }

    /**
     * Calls business object service to retrieve LaborObjectBenefit objects for the given fiscal year, and chart, 
     * object code from accounting line.
     * 
     * @param fiscalYear The fiscal year to be used as search criteria for looking up the labor object benefits.
     * @param line The account line the benefits are being retrieved for.
     * @return List of LaborObjectBenefit objects or null if one does not exist for parameters
     */
    private Collection retrieveLaborObjectBenefits(Integer fiscalYear, BudgetAdjustmentAccountingLine line) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, line.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, line.getFinancialObjectCode());

        return getBusinessObjectService().findMatching(PositionObjectBenefit.class, searchCriteria);
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
