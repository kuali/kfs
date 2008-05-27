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
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.module.budget.web.struts.form.PositionSalarySettingForm;

public class PositionSalarySettingAction extends DetailSalarySettingAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PositionSalarySettingAction.class);

    /**
     * This action loads the data for the expansion screen based on the passed in url parameters
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;

        // use the passed url parms to get the record from DB
        Map fieldValues = new HashMap();
        fieldValues.put("universityFiscalYear", positionSalarySettingForm.getUniversityFiscalYear());
        fieldValues.put("positionNumber", positionSalarySettingForm.getPositionNumber());

        BudgetConstructionPosition budgetConstructionPosition = (BudgetConstructionPosition) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionPosition.class, fieldValues);
        if (budgetConstructionPosition == null) {
            // TODO this is an RI error need to report it
        }
        positionSalarySettingForm.setBudgetConstructionPosition(budgetConstructionPosition);

        // need to explicitly populate intended incumbent for non-vacant lines
        positionSalarySettingForm.populateBCAFLines();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PositionSalarySettingForm tForm = (PositionSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Save for Salary Setting by Position");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // TODO need prompt to save? added
        PositionSalarySettingForm tForm = (PositionSalarySettingForm) form;

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

        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;
        BudgetConstructionPosition budgetConstructionPosition = positionSalarySettingForm.getBudgetConstructionPosition();

        // TODO validate and store changes

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, positionSalarySettingForm.getReturnFormKey());
        parameters.put(KFSConstants.ANCHOR, positionSalarySettingForm.getReturnAnchor());
        parameters.put(KFSConstants.REFRESH_CALLER, BCConstants.POSITION_SALARY_SETTING_REFRESH_CALLER);

        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.SALARY_SETTING_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;

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
            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "chartOfAccounts", "account", "subAccount", "financialObject", "financialSubObject", "budgetConstructionIntendedIncumbent", "budgetConstructionDuration" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(positionSalarySettingForm.getNewBCAFLine(), REFRESH_FIELDS);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
