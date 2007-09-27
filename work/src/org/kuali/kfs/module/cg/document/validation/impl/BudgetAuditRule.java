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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetTaskPeriodIndirectCost;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.service.BudgetIndirectCostService;
import org.kuali.module.kra.budget.web.struts.form.BudgetCostShareFormHelper;
import org.kuali.module.kra.util.AuditCluster;
import org.kuali.module.kra.util.AuditError;

public class BudgetAuditRule {
    
    BudgetPersonnelRule budgetPersonnelRule = new BudgetPersonnelRule();
    
    protected BudgetAuditRule() {}
    
    /**
     * Checks business rules to be flagged as audit errors.
     * 
     * @param Document document
     * @return boolean True if the document is valid, false otherwise.
     */
    protected boolean processRunAuditBusinessRules(Document document) {
        if (!(document instanceof BudgetDocument)) {
            return false;
        }

        BudgetDocument budgetDocument = (BudgetDocument) document;
        Budget budget = budgetDocument.getBudget();

        boolean valid = true;

        valid &= runCostShareAuditErrors(budget);
        valid &= runNonpersonnelAuditErrors(budget.getNonpersonnelItems());
        valid &= runParametersSoftAuditErrors(budgetDocument);
        valid &= budgetPersonnelRule.runPersonnelAuditErrors(budget.getPersonnel());

        if (budget.isAgencyModularIndicator() && budget.getModularBudget() != null) {
            valid &= runModularSoftAuditErrors(budget.getModularBudget());
        }

        return valid;
    }
    
    private boolean runParametersSoftAuditErrors(BudgetDocument budgetDocument) {
        // Need to setup IDC values
        BudgetIndirectCostService idcService = SpringContext.getBean(BudgetIndirectCostService.class);
        idcService.refreshIndirectCost(budgetDocument);
        
        if (ObjectUtils.isNotNull(budgetDocument.getBudget().getIndirectCost())) {
            List<AuditError> parametersSoftAuditErrors = new ArrayList<AuditError>();
            List<BudgetTaskPeriodIndirectCost> taskPeriodIndirectCostItems = budgetDocument.getBudget().getIndirectCost().getBudgetTaskPeriodIndirectCostItems();
        
            for (BudgetTaskPeriodIndirectCost taskPeriodIndirectCost : taskPeriodIndirectCostItems) {
                if (taskPeriodIndirectCost.getCalculatedIndirectCost().isNegative()) {
                    parametersSoftAuditErrors.add(new AuditError("document.budget.audit.parameters.tasks.negativeIdc" + taskPeriodIndirectCost.getBudgetTaskSequenceNumber().toString(),
                            KraKeyConstants.AUDIT_PARAMETERS_NEGATIVE_IDC,
                            "parameters", new String[] {taskPeriodIndirectCost.getBudgetTaskSequenceNumber().toString()}));
                }
            }
        
            if (!parametersSoftAuditErrors.isEmpty()) {
                GlobalVariables.getAuditErrorMap().put("parametersSoftAuditErrors", new AuditCluster("Parameters", parametersSoftAuditErrors, true));
                return false;
            }
        }
        return true;
    }

    private boolean runCostShareAuditErrors(Budget budget) {
        List<AuditError> costShareAuditErrors = new ArrayList<AuditError>();
        BudgetCostShareFormHelper budgetCostShareFormHelper = new BudgetCostShareFormHelper(budget.getPeriods(), budget.getPersonnel(), budget.getNonpersonnelItems(), budget.getInstitutionCostSharePersonnelItems(), budget.getInstitutionCostShareItems(), budget.getThirdPartyCostShareItems());

        if (!budgetCostShareFormHelper.getInstitutionDirect().getTotalBalanceToBeDistributed().equals(new KualiInteger(0))) {
            costShareAuditErrors.add(new AuditError("document.budget.audit.costShare.institution.distributed",
            KraKeyConstants.AUDIT_COST_SHARE_INSTITUTION_DISTRIBUTED, "costshare"));
        }
        if (!budgetCostShareFormHelper.getThirdPartyDirect().getTotalBalanceToBeDistributed().equals(new KualiInteger(0))) {
            costShareAuditErrors.add(new AuditError("document.budget.audit.costShare.3rdParty.distributed",
            KraKeyConstants.AUDIT_COST_SHARE_3P_DISTRIBUTED, "costshare"));
        }
        
        if (!costShareAuditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("costShareAuditErrors", new AuditCluster("Cost Share", costShareAuditErrors));
            return false;
        }
        return true;
    }

    private boolean runNonpersonnelAuditErrors(List<BudgetNonpersonnel> nonpersonnelItems) {
        List<AuditError> nonpersonnelAuditErrors = new ArrayList<AuditError>();
        List<String> first25kSubcategoryCodes = SpringContext.getBean(KualiConfigurationService.class).getParameterValuesAsList(KFSConstants.KRA_NAMESPACE, KFSConstants.Components.DOCUMENT, KraConstants.FIRST25K_SUBCATEGORY_CODES);
        
        HashMap<String, BudgetNonpersonnel> hashMap = new HashMap();
        
        // Go over all nonpersonnel items, and pick subcontractor less then 25K items. Finally aggregate the ones that
        // have same subcategoryCode and subcontractor number.
        for(BudgetNonpersonnel budgetNonpersonnel : nonpersonnelItems) {
            if (KraConstants.SUBCONTRACTOR_CATEGORY_CODE.equals(budgetNonpersonnel.getBudgetNonpersonnelCategoryCode()) &&
                    first25kSubcategoryCodes.contains(budgetNonpersonnel.getBudgetNonpersonnelSubCategoryCode())) {
                String key = budgetNonpersonnel.getBudgetNonpersonnelSubCategoryCode() + "-" + budgetNonpersonnel.getSubcontractorNumber();
                
                if(hashMap.containsKey(key)) {
                    BudgetNonpersonnel matchedBudgetNonpersonnel = hashMap.get(key);
                    
                    matchedBudgetNonpersonnel.setAgencyRequestAmount(matchedBudgetNonpersonnel.getAgencyRequestAmount().add(budgetNonpersonnel.getAgencyRequestAmount()));
                    matchedBudgetNonpersonnel.setBudgetInstitutionCostShareAmount(matchedBudgetNonpersonnel.getBudgetInstitutionCostShareAmount().add(budgetNonpersonnel.getBudgetInstitutionCostShareAmount()));
                    matchedBudgetNonpersonnel.setBudgetThirdPartyCostShareAmount(matchedBudgetNonpersonnel.getBudgetThirdPartyCostShareAmount().add(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount()));
                } else {
                    // create a new one so to avoid messing with the interface values
                    hashMap.put(key, new BudgetNonpersonnel(budgetNonpersonnel));
                }
            }
        }
        
        // Second step is to look over the aggregated items and decide if they need audit errors (sum(amounts) > 25K).
        for (BudgetNonpersonnel budgetNonpersonnel : hashMap.values()) {
            if (budgetNonpersonnel.getAgencyRequestAmount().add(budgetNonpersonnel.getBudgetInstitutionCostShareAmount().add(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount())).isGreaterThan(new KualiInteger(25000))) {
                // Necessary because the nonpersonnel page doesn't have all the required hidden variables for this. This should only happen
                // for the first one found that has null values, after that it will be set for all others.
                if (budgetNonpersonnel.getNonpersonnelObjectCode() == null) {
                    budgetNonpersonnel.refreshReferenceObject("nonpersonnelObjectCode");
                }
                
                nonpersonnelAuditErrors.add(new AuditError("document.budget.audit.nonpersonnelItem.category." + budgetNonpersonnel.getBudgetNonpersonnelCategoryCode(), KraKeyConstants.AUDIT_NONPERSONNEL_SUBCONTRACTOR_EXCESS_AMOUNT,  "nonpersonnel", new String[] { budgetNonpersonnel.getNonpersonnelObjectCode().getNonpersonnelSubCategory().getName(), budgetNonpersonnel.getBudgetNonpersonnelDescription()}));
            }
        }
        
        if (!nonpersonnelAuditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("nonpersonnelAuditErrors", new AuditCluster("Nonpersonnel", nonpersonnelAuditErrors, true));
            return false;
        }
        return true;
    }
    
    private boolean runModularSoftAuditErrors(BudgetModular modularBudget) {
        List<AuditError> modularSoftAuditErrors = new ArrayList<AuditError>();
        if (StringUtils.isBlank(modularBudget.getBudgetModularConsortiumDescription())) {
            modularSoftAuditErrors.add(new AuditError("document.budget.audit.modular.consortium", KraKeyConstants.AUDIT_MODULAR_CONSORTIUM, "modular"));
        }
        if (!modularSoftAuditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("modularSoftAuditErrors", new AuditCluster("Modular", modularSoftAuditErrors, true));
            return false;
        }
        return true;
    }
}
