/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;


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
            monthlyBudgetForm.setMonthlyReadOnly(tmpReadOnly);

        }

        return forward;
    }

    protected void populateAuthorizationFields(MonthlyBudgetForm monthlyBudgetForm) {
        BudgetConstructionAuthorizationStatus authorizationStatus = (BudgetConstructionAuthorizationStatus) GlobalVariables.getUserSession().retrieveObject(BCConstants.BC_DOC_AUTHORIZATION_STATUS_SESSIONKEY);

        if (authorizationStatus == null) {
            // just return, BudgetExpansionAction.execute() will see the session time out
            // and redirect back to BudgetConstructionSelection
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
        BudgetConstructionMonthly budgetConstructionMonthly = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionMonthly.class, fieldValues);
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

        this.replaceMonthlyNullWithZero(budgetConstructionMonthly);

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) GlobalVariables.getUserSession().retrieveObject(monthlyBudgetForm.getReturnFormKey());
        BudgetConstructionDocument bcDoc = budgetConstructionForm.getBudgetConstructionDocument();
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new SaveMonthlyBudgetEvent(BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY, bcDoc, budgetConstructionMonthly));

        if (rulePassed) {

            // getting here means salary detail line monthly totals equal annual
            // or this is a non-salary detail line and overriding any difference needs to be confirmed
            KualiInteger monthTotalAmount = budgetConstructionMonthly.getFinancialDocumentMonthTotalLineAmount();
            KualiInteger pbglRequestAmount = budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getAccountLineAnnualBalanceAmount();
            if (!monthTotalAmount.equals(pbglRequestAmount)){

                // total is different than annual
                Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
                ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
                if (question == null || !(KFSConstants.CONFIRMATION_QUESTION.equals(question))){
                    // ask question if not already asked
                    String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(BCKeyConstants.QUESTION_CONFIRM_MONTHLY_OVERRIDE);
                    questionText = StringUtils.replace(questionText, "{0}", pbglRequestAmount.toString());
                    questionText = StringUtils.replace(questionText, "{1}", monthTotalAmount.toString());

                    return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.CONFIRMATION_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, BCConstants.MAPPING_SAVE, "");
                }
                else {
                    Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                    if ((KFSConstants.CONFIRMATION_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {

                        // yes do the override for non-salary line and save
                        SpringContext.getBean(BudgetDocumentService.class).saveMonthlyBudget(monthlyBudgetForm, budgetConstructionMonthly);
                        KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_MONTHLY_ANNUAL_OVERRIDE_SAVED);
                        monthlyBudgetForm.setMonthlyPersisted(true);
                    }
                }
            }
            else {
                // total is same as annual do the save with no confirmation
                SpringContext.getBean(BudgetDocumentService.class).saveMonthlyBudget(monthlyBudgetForm, budgetConstructionMonthly);
                KNSGlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);
                monthlyBudgetForm.setMonthlyPersisted(true);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly budgetConstructionMonthly = monthlyBudgetForm.getBudgetConstructionMonthly();

        if (!monthlyBudgetForm.isMonthlyReadOnly()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

            // logic for close question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyValueAsString(KFSKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if ((KFSConstants.CONFIRMATION_QUESTION.equals(question)) || ((KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked))) {

                    // yes button clicked - validate and save the row
                    return this.save(mapping, form, request, response);
                }
                // else go to close logic below
            }
        }

        return returnToCaller(mapping, form, request, response);
    }

    public ActionForward performMonthlySpread(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm tForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly bcMth = tForm.getBudgetConstructionMonthly();
        KualiInteger requestAmt = bcMth.getPendingBudgetConstructionGeneralLedger().getAccountLineAnnualBalanceAmount();
        if (requestAmt != null) {
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
            ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

            // logic for delete question
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_DELETE_QUESTION, kualiConfiguration.getPropertyValueAsString(BCKeyConstants.QUESTION_DELETE), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if ((KFSConstants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {

                    // if yes button clicked - delete and close
                    SpringContext.getBean(BusinessObjectService.class).delete(budgetConstructionMonthly);

                    // if benefits calculation is turned on, check if the line is benefits related and call for calculation after
                    // save
                    BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) GlobalVariables.getUserSession().retrieveObject(monthlyBudgetForm.getReturnFormKey());
                    BudgetConstructionDocument bcDoc = budgetConstructionForm.getBudgetConstructionDocument();
                    SpringContext.getBean(BudgetDocumentService.class).callForBenefitsCalcIfNeeded(bcDoc, budgetConstructionMonthly, KualiInteger.ZERO);

                    return returnToCaller(mapping, form, request, response);
                }
                // else go to close logic below
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * checks monthly object for nulls in the amounts and replaces with zeros
     *
     * @param bcMth
     */
    public void replaceMonthlyNullWithZero(BudgetConstructionMonthly bcMth) {

        if (bcMth.getFinancialDocumentMonth1LineAmount() == null) {
            bcMth.setFinancialDocumentMonth1LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth2LineAmount() == null) {
            bcMth.setFinancialDocumentMonth2LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth3LineAmount() == null) {
            bcMth.setFinancialDocumentMonth3LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth4LineAmount() == null) {
            bcMth.setFinancialDocumentMonth4LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth5LineAmount() == null) {
            bcMth.setFinancialDocumentMonth5LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth6LineAmount() == null) {
            bcMth.setFinancialDocumentMonth6LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth7LineAmount() == null) {
            bcMth.setFinancialDocumentMonth7LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth8LineAmount() == null) {
            bcMth.setFinancialDocumentMonth8LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth9LineAmount() == null) {
            bcMth.setFinancialDocumentMonth9LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth10LineAmount() == null) {
            bcMth.setFinancialDocumentMonth10LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth11LineAmount() == null) {
            bcMth.setFinancialDocumentMonth11LineAmount(new KualiInteger(0));
        }
        if (bcMth.getFinancialDocumentMonth12LineAmount() == null) {
            bcMth.setFinancialDocumentMonth12LineAmount(new KualiInteger(0));
        }
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
