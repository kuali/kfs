/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbent;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.module.budget.web.struts.form.DetailSalarySettingForm;
import org.kuali.module.budget.web.struts.form.IncumbentSalarySettingForm;
import org.kuali.module.budget.web.struts.form.PositionSalarySettingForm;

/**
 * the base struts action for the salary setting
 */
public abstract class DetailSalarySettingAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetailSalarySettingAction.class);
    
    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;

        // TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        // since BC sets locks up front, but need to verify this

        // TODO form.populateAuthorizationFields(budgetConstructionDocumentAuthorizer) would normally happen here
        // but each line needs to be authorized, so need to figure out how to implement this

        // TODO should probably use service locator and call
        // DocumentAuthorizer documentAuthorizer =
        // SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer("<BCDoctype>");
        BudgetConstructionDocumentAuthorizer budgetConstructionDocumentAuthorizer = new BudgetConstructionDocumentAuthorizer();
        salarySettingForm.populateAuthorizationFields(budgetConstructionDocumentAuthorizer);

        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());
        
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();
        if (!SpringContext.getBean(KualiModuleService.class).isAuthorized(currentUser, bcAuthorizationType)) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName());
            
            throw new ModuleAuthorizationException(currentUser.getPersonUserIdentifier(), bcAuthorizationType, getKualiModuleService().getResponsibleModule(this.getClass()));
        }
    }

    /**
     * This action loads the data for the expansion screen based on the passed in url parameters
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncumbentSalarySettingForm incumbentSalarySettingForm = (IncumbentSalarySettingForm) form;
        // use the passed url parms to get the record from DB
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("emplid", incumbentSalarySettingForm.getEmplid());

        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = (BudgetConstructionIntendedIncumbent) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, fieldValues);
        if (budgetConstructionIntendedIncumbent == null) {
            // TODO this is an RI error need to report it
        }
        incumbentSalarySettingForm.setBudgetConstructionIntendedIncumbent(budgetConstructionIntendedIncumbent);
        incumbentSalarySettingForm.populateBCAFLines();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncumbentSalarySettingForm tForm = (IncumbentSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Save For Salary Setting by Incumbent");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // TODO need prompt to save? added
        IncumbentSalarySettingForm tForm = (IncumbentSalarySettingForm) form;

        if (tForm.isBudgetByAccountMode()) {
            return returnToCaller(mapping, form, request, response);
        }
        else {
            GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
            tForm.setOrgSalSetClose(true);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO this will eventually be spit out into close and save methods, with close calling this method

        IncumbentSalarySettingForm incumbentSalarySettingForm = (IncumbentSalarySettingForm) form;
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = incumbentSalarySettingForm.getBudgetConstructionIntendedIncumbent();

        // TODO validate and store changes

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, incumbentSalarySettingForm.getReturnFormKey());
        parameters.put(KFSConstants.ANCHOR, incumbentSalarySettingForm.getReturnAnchor());
        parameters.put(KFSConstants.REFRESH_CALLER, BCConstants.INCUMBENT_SALARY_SETTING_REFRESH_CALLER);

        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.SALARY_SETTING_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncumbentSalarySettingForm incumbentSalarySettingForm = (IncumbentSalarySettingForm) form;
        // Do specific refresh stuff here based on refreshCaller parameter
        // typical refresh callers would be lookupable or reasoncode??
        // need to look at optmistic locking problems since we will be storing the values in the form before hand
        // this locking problem may workout if we store first then put the form in session
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

        // TODO may need to check for reason code called refresh here

        // TODO this should figure out if user is returning to a rev or exp line and refresh just that
        // TODO this should also keep original values of obj, sobj to compare and null out dependencies when needed
        // TODO need a better way to detect return from lookups
        // returning from account lookup sets refreshcaller to accountLookupable, due to setting in account.xml
        // if (refreshCaller != null && refreshCaller.equalsIgnoreCase(KFSConstants.KUALI_LOOKUPABLE_IMPL)){
        if (refreshCaller != null && (refreshCaller.endsWith("Lookupable") || (refreshCaller.endsWith("LOOKUPABLE")))) {
            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "chartOfAccounts", "account", "subAccount", "financialObject", "financialSubObject", "budgetConstructionPosition", "budgetConstructionDuration" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(incumbentSalarySettingForm.getNewBCAFLine(), REFRESH_FIELDS);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action changes the value of the hide field in the user interface so that when the page is rendered, the UI knows to show
     * all of the descriptions and labels for each of the pbgl line values.
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        salarySettingForm.setHideDetails(false);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action toggles the value of the hide field in the user interface to "hide" so that when the page is rendered, the UI
     * displays values without all of the descriptions and labels for each of the pbgl lines.
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        salarySettingForm.setHideDetails(true);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action adds an appointment funding line to the set of existing funding lines
     */
    public ActionForward insertBCAFLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;        
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Add Salary Setting Line");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This action creates a new funding line based on the selected line and sets the emplid to vacant then marks the selected line
     * delete.
     */
    public ActionForward performVacateSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PositionSalarySettingForm tForm = (PositionSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Vacate Salary Setting Line");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action sets the request amount using the CSF amount adjusted by a percent or flat rate
     */
    public ActionForward performPercentAdjustmentSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Percent Adjustment For Salary Setting Line");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This displays the reason code screen for the selected funding line
     */
    public ActionForward performReasonAnnotation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Reason Annotation For Salary Setting Line");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
