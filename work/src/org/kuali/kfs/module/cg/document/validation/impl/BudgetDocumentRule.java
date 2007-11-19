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
package org.kuali.module.kra.budget.rules.budget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetIndirectCost;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.rules.ResearchDocumentRuleBase;
import org.kuali.module.kra.document.ResearchDocument;

/**
 * This class...
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

            DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

            if (StringUtils.isEmpty(budgetNonpersonnel.getBudgetNonpersonnelSubCategoryCode())) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].budgetNonpersonnelSubCategoryCode", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeErrorLabel(budgetNonpersonnel.getClass(), "budgetNonpersonnelSubCategoryCode"));
                valid = false;
            }
            if (budgetNonpersonnel.getBudgetNonpersonnelCategoryCode().equals("SC") && budgetNonpersonnel.getBudgetNonpersonnelDescription() == null) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].budgetNonpersonnelDescription", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeErrorLabel(budgetNonpersonnel.getClass(), "budgetNonpersonnelDescription"));
                valid = false;
            }
            if (budgetNonpersonnel.getAgencyRequestAmount() == null) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].agencyRequestAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeErrorLabel(budgetNonpersonnel.getClass(), "agencyRequestAmount"));
                valid = false;
            }
            if (budget.isInstitutionCostShareIndicator() && budgetNonpersonnel.getBudgetInstitutionCostShareAmount() == null) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].budgetInstitutionCostShareAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeErrorLabel(budgetNonpersonnel.getClass(), "budgetInstitutionCostShareAmount"));
                valid = false;
            }
            if (budget.isBudgetThirdPartyCostShareIndicator() && budgetNonpersonnel.getBudgetThirdPartyCostShareAmount() == null) {
                GlobalVariables.getErrorMap().putError("document.budget.nonpersonnelItem[" + i + "].budgetThirdPartyCostShareAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeErrorLabel(budgetNonpersonnel.getClass(), "budgetThirdPartyCostShareAmount"));
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

        SpringContext.getBean(DictionaryValidationService.class).validateDocumentRecursively(budgetDocument, 0);

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

    public boolean processInsertPersonnelBusinessRules(Document document, BudgetUser newBudgetUser, boolean isToBeNamed) {
        return budgetPersonnelRule.processInsertPersonnelBusinessRules(((BudgetDocument) document).getBudget().getPersonnel(), newBudgetUser, isToBeNamed);
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

    public boolean isPeriodListValid(List periodList, boolean isModularBudget, boolean validatePeriodDatesExist) {
        return budgetParametersRule.isPeriodListValid(periodList, isModularBudget, validatePeriodDatesExist);
    }

    public boolean isPeriodValid(BudgetPeriod budgetPeriod, String periodLabel, Integer periodNumber, boolean validatePeriodDatesExist) {
        return budgetParametersRule.isPeriodValid(budgetPeriod, periodLabel, periodNumber, validatePeriodDatesExist);
    }

    public boolean isTaskListValid(List tasks) {
        return budgetParametersRule.isTaskListValid(tasks);
    }

    public boolean verifyPersonnelRequiredFields(Document document) {
        return budgetPersonnelRule.verifyRequiredFields(document);
    }
}
