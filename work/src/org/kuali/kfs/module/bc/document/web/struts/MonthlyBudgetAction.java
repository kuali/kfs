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
package org.kuali.module.budget.web.struts.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.module.budget.service.BenefitsCalculationService;
import org.kuali.module.budget.web.struts.form.BudgetConstructionForm;
import org.kuali.module.budget.web.struts.form.MonthlyBudgetForm;


public class MonthlyBudgetAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MonthlyBudgetAction.class);

    /**
     * added for testing - remove if not needed
     * 
     * @see org.kuali.module.budget.web.struts.action.BudgetConstructionAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;

        // TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        // since BC sets locks up front, but need to verify this


        // TODO should probably use service locator and call
        // DocumentAuthorizer documentAuthorizer =
        // SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer("<BCDoctype>");
        BudgetConstructionDocumentAuthorizer budgetConstructionDocumentAuthorizer = new BudgetConstructionDocumentAuthorizer();
        monthlyBudgetForm.populateAuthorizationFields(budgetConstructionDocumentAuthorizer);
        /*
         * // TODO from KualiDocumentActionBase remove when ready // populates authorization-related fields in KualiDocumentFormBase
         * instances, which are derived from // information which is contained in the form but which may be unavailable until this
         * point if (form instanceof KualiDocumentFormBase) { KualiDocumentFormBase formBase = (KualiDocumentFormBase) form;
         * Document document = formBase.getDocument(); DocumentAuthorizer documentAuthorizer =
         * SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer(document);
         * formBase.populateAuthorizationFields(documentAuthorizer); // set returnToActionList flag, if needed if
         * ("displayActionListView".equals(formBase.getCommand())) { formBase.setReturnToActionList(true); }
         */
        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {

        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());
        if (!SpringContext.getBean(KualiModuleService.class).isAuthorized(GlobalVariables.getUserSession().getUniversalUser(), bcAuthorizationType)) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName());
            throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), bcAuthorizationType, getKualiModuleService().getResponsibleModule(this.getClass()));
        }
        /*
         * //TODO from KualiAction - remove when ready AuthorizationType defaultAuthorizationType = new
         * AuthorizationType.Default(this.getClass()); if ( !SpringContext.getBean(KualiModuleService.class).isAuthorized(
         * GlobalVariables.getUserSession().getUniversalUser(), defaultAuthorizationType ) ) { LOG.error("User not authorized to use
         * this action: " + this.getClass().getName() ); throw new ModuleAuthorizationException(
         * GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), defaultAuthorizationType,
         * getKualiModuleService().getResponsibleModule(((KualiDocumentFormBase)form).getDocument().getClass()) ); } //TODO from
         * KualiDocumentActionBase - remove when ready AuthorizationType documentAuthorizationType = new
         * AuthorizationType.Document(((KualiDocumentFormBase)form).getDocument().getClass(),
         * ((KualiDocumentFormBase)form).getDocument()); if ( !SpringContext.getBean(KualiModuleService.class).isAuthorized(
         * GlobalVariables.getUserSession().getUniversalUser(), documentAuthorizationType ) ) { LOG.error("User not authorized to
         * use this action: " + ((KualiDocumentFormBase)form).getDocument().getClass().getName() ); throw new
         * ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(),
         * documentAuthorizationType,
         * getKualiModuleService().getResponsibleModule(((KualiDocumentFormBase)form).getDocument().getClass()) ); }
         */
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

        // TODO validate and store monthly changes, for now save using BOService
        SpringContext.getBean(BusinessObjectService.class).save(budgetConstructionMonthly);
        GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);
        
        
        // TODO set the calling form's bcDoc.setBenefitsCalcNeeded(true)
        // this is proof of concept code for now. this should check for a change in any of the monthly fields and
        // eventually store should calc benefits immediately, and replace the benefits accounting lines in session
        // to keep the DB consistent and to keep session in sync with the DB
        
        // if benefits calculation is turned on, check if the line is benefits related and call for calculation after save 
        if (!SpringContext.getBean(BenefitsCalculationService.class).isBenefitsCalculationDisabled()){
            if (budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getPositionObjectBenefit() != null && !budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getPositionObjectBenefit().isEmpty()){

                BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) GlobalVariables.getUserSession().retrieveObject(monthlyBudgetForm.getReturnFormKey());
                budgetConstructionForm.getBudgetConstructionDocument().setMonthlyBenefitsCalcNeeded(true);
                
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly budgetConstructionMonthly = monthlyBudgetForm.getBudgetConstructionMonthly();

        if (!monthlyBudgetForm.getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY) && monthlyBudgetForm.getEditingMode().containsKey(AuthorizationConstants.EditMode.FULL_ENTRY)) {
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
                    // if yes button clicked - save the doc

                    // SpringContext.getBean(DocumentService.class).saveDocument(docForm.getDocument());
                    // TODO for now just do trivial save eventually need to add validation, routelog stuff, etc
                    SpringContext.getBean(BusinessObjectService.class).save(budgetConstructionMonthly);

                }
                // else go to close logic below
            }
        }
        return returnToCaller(monthlyBudgetForm);
    }

    public ActionForward returnToCaller(MonthlyBudgetForm monthlyBudgetForm) throws Exception {

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, monthlyBudgetForm.getReturnFormKey());
        parameters.put(KFSConstants.ANCHOR, monthlyBudgetForm.getReturnAnchor());
        parameters.put(KFSConstants.REFRESH_CALLER, BCConstants.MONTHLY_BUDGET_REFRESH_CALLER);

        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.BC_DOCUMENT_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
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
