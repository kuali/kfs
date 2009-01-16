/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAuthorizationStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.validation.event.SaveMonthlyBudgetEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiInteger;


public class MonthlyBudgetAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MonthlyBudgetAction.class);

    /**
     * added for testing - remove if not needed
     * 
     * @see org.kuali.kfs.module.bc.document.web.struts.BudgetConstructionAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        populateAuthorizationFields(monthlyBudgetForm);
        
        // set the readOnly status on initial load of the form
        if (monthlyBudgetForm.getMethodToCall().equals(BCConstants.MONTHLY_BUDGET_METHOD)) {
            BudgetConstructionMonthly bcMonthly = monthlyBudgetForm.getBudgetConstructionMonthly();
            PendingBudgetConstructionGeneralLedger pbgl = bcMonthly.getPendingBudgetConstructionGeneralLedger();

            boolean tmpReadOnly = monthlyBudgetForm.isSystemViewOnly() || !monthlyBudgetForm.isEditAllowed();
            tmpReadOnly |= bcMonthly.getFinancialObjectCode().equalsIgnoreCase(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG);
            tmpReadOnly |= (!monthlyBudgetForm.isBenefitsCalculationDisabled() && ((pbgl.getLaborObject() != null) && pbgl.getLaborObject().getFinancialObjectFringeOrSalaryCode().equalsIgnoreCase(BCConstants.LABOR_OBJECT_FRINGE_CODE)));

            monthlyBudgetForm.setBudgetableDocument(SpringContext.getBean(BudgetDocumentService.class).isBudgetableDocumentNoWagesCheck(pbgl.getBudgetConstructionHeader()));
            // tmpReadOnly |= !monthlyBudgetForm.isBudgetableDocument();
            // TODO handle not budgetable in rules like the BC document so we can display the delete monthly button only when we are
            // not readonly
            monthlyBudgetForm.setMonthlyReadOnly(tmpReadOnly);

        }

        return forward;
    }
    
    protected void populateAuthorizationFields(MonthlyBudgetForm monthlyBudgetForm) {
        BudgetConstructionAuthorizationStatus authorizationStatus = (BudgetConstructionAuthorizationStatus) GlobalVariables.getUserSession().retrieveObject(BCConstants.BC_DOC_AUTHORIZATION_STATUS_SESSIONKEY);

        if (authorizationStatus == null) {
            // TODO: handle session timeout
            return;
        }

        monthlyBudgetForm.setDocumentActions(authorizationStatus.getDocumentActions());
        monthlyBudgetForm.setEditingMode(authorizationStatus.getEditingMode());
    }

    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;

        // use the passed url parms to get the record from DB
        Map fieldValues = new HashMap();
        fieldValues.put("documentNumber", monthlyBudgetForm.getDocumentNumber());
        fieldValues.put("universityFiscalYear", monthlyBudgetForm.getUniversityFiscalYear());
        fieldValues.put("chartOfAccountsCode", monthlyBudgetForm.getChartOfAccountsCode());
        fieldValues.put("accountNumber", monthlyBudgetForm.getAccountNumber());
        fieldValues.put("subAccountNumber", monthlyBudgetForm.getSubAccountNumber());
        fieldValues.put("financialObjectCode", monthlyBudgetForm.getFinancialObjectCode());
        fieldValues.put("financialSubObjectCode", monthlyBudgetForm.getFinancialSubObjectCode());
        fieldValues.put("financialBalanceTypeCode", monthlyBudgetForm.getFinancialBalanceTypeCode());
        fieldValues.put("financialObjectTypeCode", monthlyBudgetForm.getFinancialObjectTypeCode());
        BudgetConstructionMonthly budgetConstructionMonthly = (BudgetConstructionMonthly) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionMonthly.class, fieldValues);
        if (budgetConstructionMonthly == null) {
            budgetConstructionMonthly = new BudgetConstructionMonthly();
            budgetConstructionMonthly.setDocumentNumber(monthlyBudgetForm.getDocumentNumber());
            budgetConstructionMonthly.setUniversityFiscalYear(monthlyBudgetForm.getUniversityFiscalYear());
            budgetConstructionMonthly.setChartOfAccountsCode(monthlyBudgetForm.getChartOfAccountsCode());
            budgetConstructionMonthly.setAccountNumber(monthlyBudgetForm.getAccountNumber());
            budgetConstructionMonthly.setSubAccountNumber(monthlyBudgetForm.getSubAccountNumber());
            budgetConstructionMonthly.setFinancialObjectCode(monthlyBudgetForm.getFinancialObjectCode());
            budgetConstructionMonthly.setFinancialSubObjectCode(monthlyBudgetForm.getFinancialSubObjectCode());
            budgetConstructionMonthly.setFinancialBalanceTypeCode(monthlyBudgetForm.getFinancialBalanceTypeCode());
            budgetConstructionMonthly.setFinancialObjectTypeCode(monthlyBudgetForm.getFinancialObjectTypeCode());
            budgetConstructionMonthly.refreshReferenceObject("pendingBudgetConstructionGeneralLedger");
            monthlyBudgetForm.setMonthlyPersisted(false);
        }
        else {
            monthlyBudgetForm.setMonthlyPersisted(true);
        }
        monthlyBudgetForm.setBudgetConstructionMonthly(budgetConstructionMonthly);


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This saves the data and redisplays
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly budgetConstructionMonthly = monthlyBudgetForm.getBudgetConstructionMonthly();

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) GlobalVariables.getUserSession().retrieveObject(monthlyBudgetForm.getReturnFormKey());
        BudgetConstructionDocument bcDoc = budgetConstructionForm.getBudgetConstructionDocument();
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new SaveMonthlyBudgetEvent(BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY, bcDoc, budgetConstructionMonthly));
        
        if (rulePassed){
            SpringContext.getBean(BudgetDocumentService.class).saveMonthlyBudget(monthlyBudgetForm, budgetConstructionMonthly);
            GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);
            monthlyBudgetForm.setMonthlyPersisted(true);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly budgetConstructionMonthly = monthlyBudgetForm.getBudgetConstructionMonthly();

        if (!monthlyBudgetForm.isMonthlyReadOnly()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

            // TODO create generic question text without reference to saving a document
            // logic for close question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyString(KFSKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if ((KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {

                    // yes button clicked - validate and save the row
                    BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) GlobalVariables.getUserSession().retrieveObject(monthlyBudgetForm.getReturnFormKey());
                    BudgetConstructionDocument bcDoc = budgetConstructionForm.getBudgetConstructionDocument();
                    boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new SaveMonthlyBudgetEvent(BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY, bcDoc, budgetConstructionMonthly));

                    if (rulePassed){
                        SpringContext.getBean(BudgetDocumentService.class).saveMonthlyBudget(monthlyBudgetForm, budgetConstructionMonthly);

                        // drop to close logic below
                    }
                    else {
                        return mapping.findForward(KFSConstants.MAPPING_BASIC);
                    }
                }
                // else go to close logic below
            }
        }
        
        this.cleanupAnySessionForm(mapping, request);
        return returnToCaller(mapping, form, request, response);
    }

    public ActionForward performMonthlySpread(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm tForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly bcMth = tForm.getBudgetConstructionMonthly();
        KualiInteger requestAmt = bcMth.getPendingBudgetConstructionGeneralLedger().getAccountLineAnnualBalanceAmount();
        if (requestAmt != null){
            bcMth.setFinancialDocumentMonth2LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth3LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth4LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth5LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth6LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth7LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth8LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth9LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth10LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth11LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));
            bcMth.setFinancialDocumentMonth12LineAmount(new KualiInteger(requestAmt.divide(new KualiInteger(12))));

            KualiInteger totSoFar = bcMth.getFinancialDocumentMonth2LineAmount();
            totSoFar = totSoFar.add(bcMth.getFinancialDocumentMonth3LineAmount().add(bcMth.getFinancialDocumentMonth4LineAmount()));
            totSoFar = totSoFar.add(bcMth.getFinancialDocumentMonth5LineAmount().add(bcMth.getFinancialDocumentMonth6LineAmount()));
            totSoFar = totSoFar.add(bcMth.getFinancialDocumentMonth7LineAmount().add(bcMth.getFinancialDocumentMonth8LineAmount()));
            totSoFar = totSoFar.add(bcMth.getFinancialDocumentMonth9LineAmount().add(bcMth.getFinancialDocumentMonth10LineAmount()));
            totSoFar = totSoFar.add(bcMth.getFinancialDocumentMonth11LineAmount().add(bcMth.getFinancialDocumentMonth12LineAmount()));

            bcMth.setFinancialDocumentMonth1LineAmount(requestAmt.subtract(totSoFar));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performMonthlyZero(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm tForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly bcMth = tForm.getBudgetConstructionMonthly();

        bcMth.setFinancialDocumentMonth1LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth2LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth3LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth4LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth5LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth6LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth7LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth8LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth9LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth10LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth11LineAmount(new KualiInteger(0));
        bcMth.setFinancialDocumentMonth12LineAmount(new KualiInteger(0));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performMonthlyDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly budgetConstructionMonthly = monthlyBudgetForm.getBudgetConstructionMonthly();

        // don't really need this test since the delete button isn't displayed in readOnly mode
        if (!monthlyBudgetForm.isSystemViewOnly() && monthlyBudgetForm.isEditAllowed()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

            // logic for delete question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_DELETE_QUESTION, kualiConfiguration.getPropertyString(BCKeyConstants.QUESTION_DELETE), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if ((KFSConstants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {

                    // if yes button clicked - delete and close
                    SpringContext.getBean(BusinessObjectService.class).delete(budgetConstructionMonthly);

                    // if benefits calculation is turned on, check if the line is benefits related and call for calculation after save
                    BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) GlobalVariables.getUserSession().retrieveObject(monthlyBudgetForm.getReturnFormKey());
                    BudgetConstructionDocument bcDoc = budgetConstructionForm.getBudgetConstructionDocument();
                    SpringContext.getBean(BudgetDocumentService.class).callForBenefitsCalcIfNeeded(bcDoc, budgetConstructionMonthly, KualiInteger.ZERO);

                    this.cleanupAnySessionForm(mapping, request);
                    return returnToCaller(mapping, form, request, response);
                }
                // else go to close logic below
            }
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action changes the value of the hide field in the user interface so that when the page is rendered, the UI knows to show
     * all of the descriptions and labels for each of the pbgl line values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MonthlyBudgetForm tForm = (MonthlyBudgetForm) form;
        tForm.setHideDetails(false);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action toggles the value of the hide field in the user interface to "hide" so that when the page is rendered, the UI
     * displays values without all of the descriptions and labels for each of the pbgl lines.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MonthlyBudgetForm tForm = (MonthlyBudgetForm) form;
        tForm.setHideDetails(true);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}

