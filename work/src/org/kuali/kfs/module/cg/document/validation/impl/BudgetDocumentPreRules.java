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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.businessobject.BudgetTask;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.service.BudgetService;
import org.kuali.kfs.module.cg.document.web.struts.BudgetForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;

public class BudgetDocumentPreRules extends PromptBeforeValidationBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = true;

        BudgetDocument budgetDocument = (BudgetDocument) document;

        // Currently we only want to run one check per event for BudgetDocument.
        // We can look at event.questionContext to determine if a check has run already.
        if (StringUtils.isBlank(event.getQuestionContext()) || question.equals(CGConstants.DELETE_PERIOD_QUESTION_ID)) {
            preRulesOK = confirmDeletePeriod(budgetDocument);
        }
        if (StringUtils.isBlank(event.getQuestionContext()) || question.equals(CGConstants.DELETE_TASK_QUESTION_ID)) {
            preRulesOK = confirmDeleteTask(budgetDocument);
        }
        if (StringUtils.isBlank(event.getQuestionContext()) || question.equals(CGConstants.DELETE_COST_SHARE_QUESTION_ID)) {
            preRulesOK = confirmDeleteCostShare(budgetDocument);
        }

        return preRulesOK;
    }

    public boolean doDeletePeriodRules(Document document) {
        boolean preRulesOK = true;

        BudgetDocument budgetDocument = (BudgetDocument) document;
        preRulesOK &= confirmDeletePeriod(budgetDocument);

        return preRulesOK;
    }

    public boolean doDeleteTaskRules(Document document) {
        boolean preRulesOK = true;

        BudgetDocument budgetDocument = (BudgetDocument) document;
        preRulesOK &= confirmDeleteTask(budgetDocument);

        return preRulesOK;
    }

    /**
     * Confirm that user wants to delete selected BudgetPeriod
     * 
     * @param budgetDocument
     * @return
     */
    private boolean confirmDeletePeriod(BudgetDocument budgetDocument) {

        if (StringUtils.isBlank(event.getQuestionContext())) {
            event.setQuestionContext(budgetDocument.getPeriodToDelete());
        }

        if (!StringUtils.isBlank(event.getQuestionContext()) && Integer.parseInt(event.getQuestionContext()) >= 0) {

            BudgetPeriod periodToDelete = budgetDocument.getBudget().getPeriod(Integer.parseInt(event.getQuestionContext()));

            String questionText = StringUtils.replace(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CGKeyConstants.QUESTION_KRA_DELETE_CONFIRMATION), "{0}", periodToDelete.getBudgetPeriodLabel());

            boolean deletePeriod = super.askOrAnalyzeYesNoQuestion(CGConstants.DELETE_PERIOD_QUESTION_ID, questionText);

            if (deletePeriod) {

                budgetDocument.getBudget().getPeriods().remove(periodToDelete);

                Date defaultNextBeginDate = budgetDocument.getBudget().getDefaultNextPeriodBeginDate();
                if (defaultNextBeginDate != null) {
                    ((BudgetForm) form).getNewPeriod().setBudgetPeriodBeginDate(defaultNextBeginDate);
                }

                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }

    /**
     * Confirm that user wants to delete selected BudgetTask
     * 
     * @param budgetDocument
     * @return
     */
    private boolean confirmDeleteTask(BudgetDocument budgetDocument) {

        if (StringUtils.isBlank(event.getQuestionContext())) {
            event.setQuestionContext(budgetDocument.getTaskToDelete());
        }

        if (!StringUtils.isBlank(event.getQuestionContext()) && Integer.parseInt(event.getQuestionContext()) >= 0) {

            BudgetTask taskToDelete = budgetDocument.getBudget().getTask(Integer.parseInt(event.getQuestionContext()));

            String questionText = StringUtils.replace(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CGKeyConstants.QUESTION_KRA_DELETE_CONFIRMATION), "{0}", taskToDelete.getBudgetTaskName());

            boolean deleteTask = super.askOrAnalyzeYesNoQuestion(CGConstants.DELETE_TASK_QUESTION_ID, questionText);

            if (deleteTask) {
                budgetDocument.getBudget().getTasks().remove(taskToDelete);
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }

    /**
     * Confirm that user wants to delete cost share
     * 
     * @param budgetDocument
     * @return
     */
    private boolean confirmDeleteCostShare(BudgetDocument budgetDocument) {

        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        String costShareRemoved = SpringContext.getBean(BudgetService.class).buildCostShareRemovedCode(budgetDocument);

        if (StringUtils.isBlank(event.getQuestionContext())) {
            event.setQuestionContext(costShareRemoved);
        }

        if (!StringUtils.isBlank(event.getQuestionContext())) {

            // Build our confirmation message with proper context.
            StringBuffer codeText = new StringBuffer();
            if (costShareRemoved.contains(CGConstants.INSTITUTION_COST_SHARE_CODE)) {
                codeText.append(dataDictionaryService.getAttributeLabel(Budget.class, "institutionCostShareIndicator"));
            }
            if (costShareRemoved.contains(CGConstants.THIRD_PARTY_COST_SHARE_CODE)) {
                if (costShareRemoved.indexOf(CGConstants.THIRD_PARTY_COST_SHARE_CODE) != 0) {
                    codeText.append(" and ");
                }
                codeText.append(dataDictionaryService.getAttributeLabel(Budget.class, "budgetThirdPartyCostShareIndicator"));
            }

            String questionText = StringUtils.replace(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(CGKeyConstants.QUESTION_KRA_DELETE_CONFIRMATION), "{0}", codeText.toString());

            boolean deleteCostShare = super.askOrAnalyzeYesNoQuestion(CGConstants.DELETE_COST_SHARE_QUESTION_ID, questionText);

            if (!deleteCostShare) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }
}
