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
import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.document.BudgetAdjustmentDocument;
import org.kuali.module.financial.service.BudgetAdjustmentLaborBenefitsService;
import org.kuali.module.integration.service.LaborModuleService;

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
                if (line.isSourceAccountingLine()) {
                    budgetDocument.getSourceAccountingLines().remove(line);
                }
                else {
                    budgetDocument.getTargetAccountingLines().remove(line);
                }
                continue;
            }
            
            List<BudgetAdjustmentAccountingLine> benefitLines = SpringContext.getBean(LaborModuleService.class).getFringeBenefitLinesForBudjetAdjustmentLine(fiscalYear, line, budgetDocument.getSourceAccountingLineClass(), budgetDocument.getTargetAccountingLineClass());
            
            for (BudgetAdjustmentAccountingLine benefitLine: benefitLines) {
                line.setFringeBenefitIndicator(true);
                
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
     * @param budgetDocument
     * @return 
     * 
     * @see org.kuali.module.financial.service.BudgetAdjustmentLaborBenefitsService#hasLaborObjectCodes(org.kuali.module.financial.document.BudgetAdjustmentDocument)
     */
    public boolean hasLaborObjectCodes(BudgetAdjustmentDocument budgetDocument) {
        boolean hasLaborObjectCodes = false;

        List<AccountingLine> accountingLines = new ArrayList<AccountingLine>();
        accountingLines.addAll(budgetDocument.getSourceAccountingLines());
        accountingLines.addAll(budgetDocument.getTargetAccountingLines());

        Integer fiscalYear = budgetDocument.getPostingYear();
        
        return SpringContext.getBean(LaborModuleService.class).hasFringeBenefitProducingObjectCodes(fiscalYear, accountingLines);
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
