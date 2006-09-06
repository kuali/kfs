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
package org.kuali.module.kra.budget.rules.budget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraKeyConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetIndirectCost;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.bo.BudgetModularPeriod;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.document.ResearchDocument;
import org.kuali.module.kra.budget.rules.ResearchDocumentRuleBase;
import org.kuali.module.kra.budget.util.AuditCluster;
import org.kuali.module.kra.budget.util.AuditError;

/**
 * This class...
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetDocumentRule extends ResearchDocumentRuleBase {
    
    BudgetPersonnelRule budgetPersonnelRule = new BudgetPersonnelRule();
    BudgetParametersRule budgetParametersRule = new BudgetParametersRule();
    BudgetCostShareRule budgetCostShareRule = new BudgetCostShareRule();
    BudgetAuditRule budgetAuditRule = new BudgetAuditRule();
    BudgetModularRule budgetModularRule = new BudgetModularRule();
    BudgetIndirectCostRule budgetIndirectCostRule = new BudgetIndirectCostRule();
    
    /**
     * Checks business rules related to adding a Period. This method exists so that these checks can be run on update manually.
     * 
     * @param Document document
     * @return boolean True if the document is valid, false otherwise.
     */
    public boolean processUpdateNonpersonnelBusinessRules(Document document, List nonpersonnelItems) {
        if (!(document instanceof BudgetDocument)) {
            return false;
        }

        boolean valid = true;

        BudgetDocument budgetDocument = (BudgetDocument) document;
        Budget budget = budgetDocument.getBudget();

        int i = 0;

        for (Iterator iter = nonpersonnelItems.iterator(); iter.hasNext(); i++) {
            BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) iter.next();

            if (StringUtils.isEmpty(budgetNonpersonnel.getBudgetNonpersonnelSubCategoryCode())) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].budgetNonpersonnelSubCategoryCode", KeyConstants.ERROR_REQUIRED, "Sub-category");
                valid = false;
            }
            if (budgetNonpersonnel.getBudgetNonpersonnelCategoryCode().equals("SC") && budgetNonpersonnel.getBudgetNonpersonnelDescription() == null) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].budgetNonpersonnelDescription", KeyConstants.ERROR_REQUIRED, "Description");
                valid = false;
            }
            if (budgetNonpersonnel.getAgencyRequestAmount() == null) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].agencyRequestAmount", KeyConstants.ERROR_REQUIRED, "Agency Amount Requested");
                valid = false;
            }
            if (budget.isUniversityCostShareIndicator() && budgetNonpersonnel.getBudgetUniversityCostShareAmount() == null) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].budgetUniversityCostShareAmount", KeyConstants.ERROR_REQUIRED, "Institution Cost Share");
                valid = false;
            }
            if (budget.isBudgetThirdPartyCostShareIndicator() && budgetNonpersonnel.getBudgetThirdPartyCostShareAmount() == null) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].budgetThirdPartyCostShareAmount", KeyConstants.ERROR_REQUIRED, "3rd Party Cost Share");
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Checks business rules related to saving a ResearchDocument.
     * 
     * @param ResearchDocument researchDocument
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(ResearchDocument researchDocument) {
        if (!(researchDocument instanceof BudgetDocument)) {
            return false;
        }

        boolean valid = true;

        BudgetDocument budgetDocument = (BudgetDocument) researchDocument;

        budgetDocument.getBudget().setAllUserAppointmentTasks(new ArrayList());
        budgetDocument.getBudget().setAllUserAppointmentTaskPeriods(new ArrayList());

        SpringServiceLocator.getDictionaryValidationService().validateDocumentRecursively(budgetDocument, 1);

        valid &= GlobalVariables.getErrorMap().isEmpty();
        valid &= budgetParametersRule.isParametersValid(budgetDocument);
        valid &= budgetPersonnelRule.isPersonnelListValid(budgetDocument.getBudget());
        valid &= budgetCostShareRule.isCostShareValid(budgetDocument.getBudget());

        budgetDocument.getBudget().setAllUserAppointmentTasks(null);
        budgetDocument.getBudget().setAllUserAppointmentTaskPeriods(null);

        return valid;
    }
    
    public boolean processRunAuditBusinessRules(Document document) {
        return budgetAuditRule.processRunAuditBusinessRules(document);
    }
    
    public boolean processEnterModularBusinessRules(Document document) {
        return budgetModularRule.processEnterModularBusinessRules(document);
    }

    public boolean processSaveModularBusinessRules(Document document) {
        return budgetModularRule.processSaveModularBusinessRules(document);
    }

    public boolean processRecalculateIndirectCostBusinessRules(Document document) {
        return budgetIndirectCostRule.processRecalculateIndirectCostBusinessRules(document);
    }

    public boolean processSaveIndirectCostBusinessRules(BudgetIndirectCost idc) {
        return budgetIndirectCostRule.processSaveIndirectCostBusinessRules(idc);
    }

    public boolean processInsertPersonnelBusinessRules(Document document, BudgetUser newBudgetUser) {
        return budgetPersonnelRule.processInsertPersonnelBusinessRules(((BudgetDocument)document).getBudget().getPersonnel(), newBudgetUser);
    }

    public boolean processSavePersonnelBusinessRules(List personnel) {
        return budgetPersonnelRule.processSavePersonnelBusinessRules(personnel);
    }

    public boolean processUpdatePersonnelBusinessRules(Document document) {
        return budgetPersonnelRule.processUpdatePersonnelBusinessRules(document);
    }

    public boolean processAddPeriodBusinessRules(Document document, BudgetPeriod budgetPeriod) {
        return budgetParametersRule.processAddPeriodBusinessRules(document, budgetPeriod);
    }

    public boolean isPeriodListValid(List periodList, boolean isModularBudget) {
        return budgetParametersRule.isPeriodListValid(periodList, isModularBudget);
    }

    public boolean isPeriodValid(BudgetPeriod budgetPeriod, String periodLabel, Integer periodNumber) {
        return budgetParametersRule.isPeriodValid(budgetPeriod, periodLabel, periodNumber);
    }

    public boolean isTaskListValid(List tasks) {
        return budgetParametersRule.isTaskListValid(tasks);
    }
    
    public boolean verifyPersonnelRequiredFields(Document document) {
        return budgetPersonnelRule.verifyRequiredFields(document);
    }
}
