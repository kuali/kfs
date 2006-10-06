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

import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class BudgetDocumentPreRules extends PreRulesContinuationBase {

    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.MaintenanceDocument)
     */
    public boolean doRules(Document document) {
        boolean preRulesOK = true;

        BudgetDocument budgetDocument = (BudgetDocument) document;
        
        // Currently we only want to run one check per event for BudgetDocument.
        // We can look at event.questionContext to determine if a check has run already.
        if (StringUtils.isBlank(event.getQuestionContext()) || question.equals(KraConstants.DELETE_PERIOD_QUESTION_ID)) {
            preRulesOK = confirmDeletePeriod(budgetDocument);
        }
        if (StringUtils.isBlank(event.getQuestionContext()) || question.equals(KraConstants.DELETE_TASK_QUESTION_ID)) {
            preRulesOK = confirmDeleteTask(budgetDocument);
        }
        if (StringUtils.isBlank(event.getQuestionContext()) || question.equals(KraConstants.DELETE_COST_SHARE_QUESTION_ID)) {
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
            
            String questionText = StringUtils.replace(SpringServiceLocator.getKualiConfigurationService().getPropertyString(
                    KeyConstants.QUESTION_BUDGET_DELETE_CONFIRMATION), "{0}", periodToDelete.getBudgetPeriodLabel());
            
            boolean deletePeriod = super.askOrAnalyzeYesNoQuestion(KraConstants.DELETE_PERIOD_QUESTION_ID, questionText);

            if (deletePeriod) {

                budgetDocument.getBudget().getPeriods().remove(periodToDelete);

                Date defaultNextBeginDate = budgetDocument.getBudget().getDefaultNextPeriodBeginDate();
                if (defaultNextBeginDate != null) {
                    ((BudgetForm) form).getNewPeriod().setBudgetPeriodBeginDate(defaultNextBeginDate);
                }

                event.setActionForwardName(Constants.MAPPING_BASIC);
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
            
            String questionText = StringUtils.replace(SpringServiceLocator.getKualiConfigurationService().getPropertyString(
                    KeyConstants.QUESTION_BUDGET_DELETE_CONFIRMATION), "{0}", taskToDelete.getBudgetTaskName());
            
            boolean deleteTask = super.askOrAnalyzeYesNoQuestion(KraConstants.DELETE_TASK_QUESTION_ID, questionText);

            if (deleteTask) {
                budgetDocument.getBudget().getTasks().remove(taskToDelete);
                event.setActionForwardName(Constants.MAPPING_BASIC);
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
        
        String costShareRemoved = SpringServiceLocator.getBudgetService().buildCostShareRemovedCode(budgetDocument);
        
        if (StringUtils.isBlank(event.getQuestionContext())) {
            event.setQuestionContext(costShareRemoved);
        }

        if (!StringUtils.isBlank(event.getQuestionContext())) {
            
          //Build our confirmation message with proper context.
          StringBuffer codeText = new StringBuffer();
          if (costShareRemoved.contains(KraConstants.INSTITUTION_COST_SHARE_CODE)) {
              codeText.append("Institution Cost Share");
          }
          if (costShareRemoved.contains(KraConstants.THIRD_PARTY_COST_SHARE_CODE)) {
              if (costShareRemoved.indexOf(KraConstants.THIRD_PARTY_COST_SHARE_CODE) != 0) {
                  codeText.append(" and ");
              }
              codeText.append("Third Party Cost Share");
          }
            
            String questionText = StringUtils.replace(SpringServiceLocator.getKualiConfigurationService().getPropertyString(
                    KeyConstants.QUESTION_BUDGET_DELETE_CONFIRMATION), "{0}", codeText.toString());
            
            boolean deleteCostShare = super.askOrAnalyzeYesNoQuestion(KraConstants.DELETE_COST_SHARE_QUESTION_ID, questionText);

            if (!deleteCostShare) {
                event.setActionForwardName(Constants.MAPPING_BASIC);
                return false;
            }
        }

        return true;
    }
}
