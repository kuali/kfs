/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.financial.service.BudgetAdjustmentLaborBenefitsService;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.LaborObjectBenefit;

/**
 * These service performs methods related to the generation of labor benefit 
 * accounting lines for the budget adjustment document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentLaborBenefitsServiceImpl implements BudgetAdjustmentLaborBenefitsService{
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.financial.service.BudgetAdjustmentLaborBenefitsService#generateLaborBenefitsAccountingLines(org.kuali.module.financial.document.BudgetAdjustmentDocument)
     */
    public void generateLaborBenefitsAccountingLines(BudgetAdjustmentDocument budgetDocument) {
        Integer fiscalYear = budgetDocument.getPostingYear();
      
        List accountingLines = new ArrayList();
        accountingLines.addAll(budgetDocument.getSourceAccountingLines());
        accountingLines.addAll(budgetDocument.getTargetAccountingLines());
        
        /* find lines that have labor object codes, then retrieve the benefit calculation records for the
         * object code. Finally, for each benefit record, create an accounting line with properties set from
         * the orginal line, but substituted with the benefit object code and calculated current and base amount.
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
                    LaborObjectBenefit objectBenefit = (LaborObjectBenefit) iterator.next();
                    BenefitsCalculation benefitsCalculation = objectBenefit.getBenefitsCalculation();
                    
                    // now create and set properties for the benefit line
                    BudgetAdjustmentAccountingLine benefitLine = (BudgetAdjustmentAccountingLine) ObjectUtils.deepCopy(line);
                    benefitLine.setFinancialObjectCode(benefitsCalculation.getPositionFringeBenefitObjectCode());
                    benefitLine.refresh();
                   
                    KualiDecimal benefitCurrentAmount = line.getCurrentBudgetAdjustmentAmount().multiply(benefitsCalculation.getPositionFringeBenefitPercent().toKualiDecimal());
                    benefitLine.setCurrentBudgetAdjustmentAmount(benefitCurrentAmount);

                    KualiInteger benefitBaseAmount = line.getBaseBudgetAdjustmentAmount().multiply(benefitsCalculation.getPositionFringeBenefitPercent().toKualiDecimal());
                    benefitLine.setBaseBudgetAdjustmentAmount(benefitBaseAmount);
                    
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
     * Calls business object service to retrieve LaborObjectBenefit objects for the given fiscal
     * year, and chart, object code from accounting line.
     * @param fiscalYear
     * @param line
     * @return List of LaborObjectBenefit objects or null if one does not exist for parameters
     */
    private Collection retrieveLaborObjectBenefits(Integer fiscalYear, BudgetAdjustmentAccountingLine line) {
        Map searchCriteria = new HashMap();
        
        searchCriteria.put(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(PropertyConstants.CHART_OF_ACCOUNTS_CODE, line.getChartOfAccountsCode());
        searchCriteria.put(PropertyConstants.FINANCIAL_OBJECT_CODE, line.getFinancialObjectCode());
            
        return getBusinessObjectService().findMatching(LaborObjectBenefit.class, searchCriteria);
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }



}
