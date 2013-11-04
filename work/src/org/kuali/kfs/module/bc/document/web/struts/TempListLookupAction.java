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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.LockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService;
import org.kuali.kfs.module.bc.document.service.OrganizationSalarySettingSearchService;
import org.kuali.kfs.module.bc.exception.BudgetIncumbentAlreadyExistsException;
import org.kuali.kfs.module.bc.exception.BudgetPositionAlreadyExistsException;
import org.kuali.kfs.module.bc.exception.IncumbentNotFoundException;
import org.kuali.kfs.module.bc.exception.PositionNotFoundException;
import org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService;
import org.kuali.kfs.module.bc.service.BudgetConstructionPositionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiLookupAction;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Action class to display special budget lookup screens.
 */
public class TempListLookupAction extends KualiLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TempListLookupAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        return super.execute(mapping, form, request, response);
    }

    /**
     * Does not supress actions if in position or incumbent lookup mode in the BC application, otherwise calls on
     * KualiLookupAction.supressActions
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#supressActionsIfNeeded(org.apache.struts.action.ActionForm)
     */
    @Override
    protected void supressActionsIfNeeded(ActionForm form) throws ClassNotFoundException {

        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;
        if ((tempListLookupForm.getTempListLookupMode() == BCConstants.TempListLookupMode.BUDGET_POSITION_LOOKUP) || (tempListLookupForm.getTempListLookupMode() == BCConstants.TempListLookupMode.INTENDED_INCUMBENT)) {

            // we want the actions in actions column to show when in position or incumbent lookup mode
            // regardless of what KIM thinks
            tempListLookupForm.setSuppressActions(false);
        }
        else if (tempListLookupForm.getTempListLookupMode() == BCConstants.TempListLookupMode.LOCK_MONITOR) {
            // check if current user has permission to unlock
            String[] rootOrg = SpringContext.getBean(OrganizationService.class).getRootOrganizationCode();
            Map<String,String> qualification = new HashMap<String,String>();
            qualification.put(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE, rootOrg[0]);
            qualification.put(KfsKimAttributes.ORGANIZATION_CODE, rootOrg[1]);

            boolean canUnlock = SpringContext.getBean(IdentityManagementService.class).isAuthorized(GlobalVariables.getUserSession().getPerson().getPrincipalId(), BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.UNLOCK_PERMISSION_NAME, qualification);
            tempListLookupForm.setSuppressActions(!canUnlock);
            tempListLookupForm.setSupplementalActionsEnabled(canUnlock);
        }
        else {
            super.supressActionsIfNeeded(form);
        }
    }

    /**
     * TempListLookupAction can be called to build and display different lists. This method determines what the requested behavior
     * is and either makes a build call for that list or sets up a message (if the list has already been built). If the request
     * parameter showInitialResults is true, an initial search will be performed before display of the screen.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // determine requested lookup action
        switch (tempListLookupForm.getTempListLookupMode()) {
            case BCConstants.TempListLookupMode.INTENDED_INCUMBENT_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildIntendedIncumbentSelect(tempListLookupForm.getPrincipalId(), tempListLookupForm.getUniversityFiscalYear());
                break;

            case BCConstants.TempListLookupMode.POSITION_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildPositionSelect(tempListLookupForm.getPrincipalId(), tempListLookupForm.getUniversityFiscalYear());
                break;

            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_ABOVE_POV:
                // check report mode for 2PLG or Sync
                // this case is handled after performing the search, see below
                break;

            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_BUDGETED_DOCUMENTS:
                // Show Budgeted Docs (BudgetConstructionAccountSelect) in the point of view subtree for the selected org(s)
                // The table was already built in OrganizationSelectionTreeAction.performShowBudgetDocs
                KNSGlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_ACCOUNT_LIST);
                break;

            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_PULLUP_DOCUMENTS:
                // Show Budgeted Docs (BudgetConstructionAccountSelect) for the selected org(s) below the users point of view
                // The table was already built in OrganizationSelectionTreeAction.performShowPullUpBudgetDocs
                KNSGlobalVariables.getMessageList().add(BCKeyConstants.MSG_ACCOUNT_PULLUP_LIST);
                break;

            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_PUSHDOWN_DOCUMENTS:
                // Show Budgeted Docs (BudgetConstructionAccountSelect) for the selected org(s) at the users point of view
                // The table was already built in OrganizationSelectionTreeAction.performShowPullUpBudgetDocs
                KNSGlobalVariables.getMessageList().add(BCKeyConstants.MSG_ACCOUNT_PUSHDOWN_LIST);
                break;

            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_MANAGER_DELEGATE:
                // Show Budgeted Docs (BudgetConstructionAccountSelect) where the user is a fiscal officer or delegate
                // The table was already built in BudgetConstructionSelectionTreeAction.performMyAccounts
                KNSGlobalVariables.getMessageList().add(BCKeyConstants.MSG_ACCOUNT_MANAGER_DELEGATE_LIST);
                break;
        }

        ActionForward forward = super.start(mapping, form, request, response);
        if (tempListLookupForm.isShowInitialResults()) {
            forward = search(mapping, form, request, response);
        }

        // handling this mode case here instead of inside case test above
        // since it requires knowing if rows exist
        if (tempListLookupForm.getTempListLookupMode() == BCConstants.TempListLookupMode.ACCOUNT_SELECT_ABOVE_POV) {
            if (tempListLookupForm.isForceToAccountListScreen()) {
                ArrayList<ResultRow> resultTable = (ArrayList<ResultRow>) request.getAttribute("reqSearchResults");
                int resultSize = resultTable.size();
                if (resultSize == 0) {
                    KNSGlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_REPORT_EMPTY_ACCOUNT_LIST);
                }
                else {
                    KNSGlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_REPORT_ACCOUNT_LIST);
                }
            }
            else {
                // Show Account above current point of view for user
                // The table was already built in OrganizationSelectionTreeAction.performReport
                KNSGlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_REPORT_ACCOUNT_LIST);
            }
        }
        return forward;
    }

    /**
     * This differs from KualiLookupAction.clearValues in that any atributes marked hidden will not be cleared. This is to support
     * BC temp tables that use principalId to operate on the set of rows associated with the current user.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#clearValues(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!field.getFieldType().equals(Field.RADIO) && !field.getFieldType().equals(Field.HIDDEN)) {
                    field.setPropertyValue(field.getDefaultValue());
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        switch (tempListLookupForm.getTempListLookupMode()) {
            case BCConstants.TempListLookupMode.INTENDED_INCUMBENT_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanIntendedIncumbentSelect(tempListLookupForm.getPrincipalId());
                break;

            case BCConstants.TempListLookupMode.POSITION_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanPositionSelect(tempListLookupForm.getPrincipalId());
                break;

            default:
                SpringContext.getBean(OrganizationBCDocumentSearchService.class).cleanAccountSelectPullList(tempListLookupForm.getPrincipalId(), tempListLookupForm.getUniversityFiscalYear());
        }

        // catch and fix any null docNum so we can use super.cancel without complaints from requestProcessor
        if (tempListLookupForm.getDocNum() == null) {
            tempListLookupForm.setDocNum("");
        }
        return super.cancel(mapping, form, request, response);
    }

    /**
     * Forwards to budget position lookup.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performExtendedPositionSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;
        tempListLookupForm.setMethodToCall("refresh");

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, tempListLookupForm.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        parameters.put(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, BudgetConstructionPosition.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(BCConstants.SHOW_INITIAL_RESULTS, "true");
        parameters.put(BCConstants.TempListLookupMode.TEMP_LIST_LOOKUP_MODE, Integer.toString(BCConstants.TempListLookupMode.BUDGET_POSITION_LOOKUP));

        // pass lookup fields as parameters
        Map fieldValues = tempListLookupForm.getFieldsForLookup();
        parameters.put(BCPropertyConstants.POSITION_NUMBER, fieldValues.get(BCPropertyConstants.POSITION_NUMBER));
        parameters.put(BCPropertyConstants.POSITION_DEPARTMENT_IDENTIFIER, fieldValues.get(BCPropertyConstants.POSITION_DEPARTMENT_IDENTIFIER));
        parameters.put(BCPropertyConstants.IU_POSITION_TYPE, fieldValues.get(BCPropertyConstants.IU_POSITION_TYPE));
        parameters.put(BCPropertyConstants.POSITION_SALARY_PLAN_DEFAULT, fieldValues.get(BCPropertyConstants.SALARY_ADMINISTRATION_PLAN));
        parameters.put(BCPropertyConstants.POSITION_GRADE_DEFAULT, fieldValues.get(BCPropertyConstants.GRADE));

        parameters.put(BCConstants.SHOW_SALARY_BY_POSITION_ACTION, "true");
        parameters.put(BCPropertyConstants.ADD_LINE, "false");

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.ORG_TEMP_LIST_LOOKUP, parameters);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * Validates the get new action for position then calls BudgetPositionService to pull the new position record.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward getNewPosition(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // check bc updates are allowed and updates from payroll are turned on
        if (!tempListLookupForm.isGetNewPositionEnabled()) {
            LOG.error("get new position not enabled.");
            throw new RuntimeException("get new position not enabled");
        }

        // verify a position number to retrieve was given
        String positionNumber = (String) tempListLookupForm.getFieldsForLookup().get(BCPropertyConstants.POSITION_NUMBER);
        if (StringUtils.isBlank(positionNumber)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_REQUIRED_FOR_GET_NEW_POSITION);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // call service to pull new position
        try {
            SpringContext.getBean(BudgetConstructionPositionService.class).pullNewPositionFromExternal(tempListLookupForm.getUniversityFiscalYear(), positionNumber);
        }
        catch (PositionNotFoundException e) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, e.getMessageKey(), e.getMessageParameters());
        }
        catch (BudgetPositionAlreadyExistsException e1) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, e1.getMessageKey(), e1.getMessageParameters());
        }

        // perform search which should return the new budget position
        return this.search(mapping, form, request, response);
    }

    /**
     * Forwards to intended incumbent lookup.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performExtendedIncumbentSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;
        tempListLookupForm.setMethodToCall("refresh");

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, tempListLookupForm.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        parameters.put(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, BudgetConstructionIntendedIncumbent.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(BCConstants.SHOW_INITIAL_RESULTS, "true");
        parameters.put(BCConstants.TempListLookupMode.TEMP_LIST_LOOKUP_MODE, Integer.toString(BCConstants.TempListLookupMode.INTENDED_INCUMBENT));

        // pass lookup fields as parameters
        Map fieldValues = tempListLookupForm.getFieldsForLookup();
        parameters.put(KFSPropertyConstants.EMPLID, fieldValues.get(KFSPropertyConstants.EMPLID));
        parameters.put(KFSPropertyConstants.PERSON_NAME, fieldValues.get(KFSPropertyConstants.PERSON_NAME));

        parameters.put(BCConstants.SHOW_SALARY_BY_INCUMBENT_ACTION, "true");
        parameters.put(BCPropertyConstants.ADD_LINE, "false");

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.ORG_TEMP_LIST_LOOKUP, parameters);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * Validates the get new action for incumbent then calls BudgetPositionService to pull the new incumbent record.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward getNewIncumbent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // check bc updates are allowed and updates from payroll are turned on
        if (!tempListLookupForm.isGetNewIncumbentEnabled()) {
            LOG.error("get new incumbent not enabled.");
            throw new RuntimeException("get new incumbent not enabled");
        }

        // verify an emplid to retrieve was given
        String emplid = (String) tempListLookupForm.getFieldsForLookup().get(KFSPropertyConstants.EMPLID);
        if (StringUtils.isBlank(emplid)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_REQUIRED_FOR_GET_NEW_INCUMBENT);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // call service to pull new incumbent
        try {
            SpringContext.getBean(BudgetConstructionIntendedIncumbentService.class).pullNewIncumbentFromExternal(emplid);
        }
        catch (IncumbentNotFoundException e) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, e.getMessageKey(), e.getMessageParameters());
        }
        catch (BudgetIncumbentAlreadyExistsException e1) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, e1.getMessageKey(), e1.getMessageParameters());
        }

        // perform search which should return the new incumbent
        return this.search(mapping, form, request, response);
    }

    /**
     * Continues the organization report action after viewing the account list.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward submitReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put(BCConstants.RETURN_FORM_KEY, tempListLookupForm.getFormKey());
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + BCConstants.ORG_SEL_TREE_ACTION);
        if (tempListLookupForm.isReportConsolidation()) {
            parameters.put(BCConstants.Report.REPORT_CONSOLIDATION, "true");
        }
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, tempListLookupForm.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        parameters.put(BCConstants.Report.REPORT_MODE, tempListLookupForm.getReportMode());
        parameters.put(BCConstants.CURRENT_POINT_OF_VIEW_KEYCODE, tempListLookupForm.getCurrentPointOfViewKeyCode());

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.ORG_REPORT_SELECTION_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * Unlocks a current budget lock and returns back to lock monitor with refreshed locks.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unlock(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // populate BudgetConstructionLockSummary with the information for the record to unlock (passed on unlock button)
        String unlockKeyString = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isBlank(unlockKeyString)) {
            unlockKeyString = request.getParameter(KRADConstants.METHOD_TO_CALL_PATH);
        }
        BudgetConstructionLockSummary lockSummary = populateLockSummary(unlockKeyString);
        String lockKeyMessage = buildLockKeyMessage(lockSummary.getLockType(), lockSummary.getLockUserId(), lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getPositionNumber());

        // confirm the unlock
        ActionForward forward = doUnlockConfirmation(mapping, form, request, response, lockSummary.getLockType(), lockKeyMessage);
        if (forward != null) {
            return forward;
        }

        // verify lock for user still exists, if not give warning message
        boolean lockExists = SpringContext.getBean(LockService.class).checkLockExists(lockSummary);
        if (!lockExists) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.MSG_LOCK_NOTEXIST, lockSummary.getLockType(), lockKeyMessage);
        }
        else {
            // do the unlock
            LockStatus lockStatus = SpringContext.getBean(LockService.class).doUnlock(lockSummary);
            if (LockStatus.SUCCESS.equals(lockStatus)) {
                String successMessage = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(BCKeyConstants.MSG_UNLOCK_SUCCESSFUL);
                tempListLookupForm.addMessage(MessageFormat.format(successMessage, lockSummary.getLockType(), lockKeyMessage));
            }
            else {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.MSG_UNLOCK_NOTSUCCESSFUL, lockSummary.getLockType(), lockKeyMessage);
            }
        }

        // refresh locks before returning
        return this.search(mapping, form, request, response);
    }


    /**
     * Gives a confirmation first time called. The next time will check the confirmation result. If the returned forward is not
     * null, that indicates we are fowarding to the question or they selected No to the confirmation and we should return to the
     * unlock page.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward doUnlockConfirmation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String lockType, String lockKeyMessage) throws Exception {
        TempListLookupForm lookupForm = (TempListLookupForm) form;

        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question == null) { // question hasn't been asked
            String message = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(BCKeyConstants.MSG_UNLOCK_CONFIRMATION);
            message = MessageFormat.format(message, lockType, lockKeyMessage);

            return this.performQuestionWithoutInput(mapping, form, request, response, BCConstants.UNLOCK_CONFIRMATION_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION, BCConstants.TEMP_LIST_UNLOCK_METHOD, "");
        }
        else {
            // get result of confirmation, if yes return null which will indicate the unlock can continue
            String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((BCConstants.UNLOCK_CONFIRMATION_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                return null;
            }
        }

        // selected no to confirmation so return to lock screen with refreshed results
        return this.search(mapping, form, request, response);
    }

    /**
     * Parses the methodToCall parameter which contains the lock information in a known format. Populates a
     * BudgetConstructionLockSummary that represents the record to unlock.
     *
     * @param methodToCallString - request parameter containing lock information
     * @return lockSummary populated from request parameter
     */
    protected BudgetConstructionLockSummary populateLockSummary(String methodToCallString) {
        BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();

        // parse lock fields from methodToCall parameter
        String lockType = StringUtils.substringBetween(methodToCallString, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        String lockFieldsString = StringUtils.substringBetween(methodToCallString, KFSConstants.METHOD_TO_CALL_PARM9_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM9_RIGHT_DEL);
        String lockUser = StringUtils.substringBetween(methodToCallString, KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);

        // space was replaced by underscore for html
        lockSummary.setLockType(StringUtils.replace(lockType, "_", " "));
        lockSummary.setLockUserId(lockUser);

        // parse key fields
        StrTokenizer strTokenizer = new StrTokenizer(lockFieldsString, BCConstants.LOCK_STRING_DELIMITER);
        strTokenizer.setIgnoreEmptyTokens(false);
        String fiscalYear = strTokenizer.nextToken();
        if (fiscalYear != null) {
            lockSummary.setUniversityFiscalYear(Integer.parseInt(fiscalYear));
        }

        lockSummary.setChartOfAccountsCode(strTokenizer.nextToken());
        lockSummary.setAccountNumber(strTokenizer.nextToken());
        lockSummary.setSubAccountNumber(strTokenizer.nextToken());
        lockSummary.setPositionNumber(strTokenizer.nextToken());

        return lockSummary;
    }

    /**
     * Retrieves the message text for the lock key and fills in message parameters based on the lock type.
     *
     * @return lockKey built from given parameters
     */
    protected String buildLockKeyMessage(String lockType, String lockUserId, String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String positionNumber) {
        ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);

        String lockKeyMessage = "";
        if (BCConstants.LockTypes.POSITION_LOCK.equals(lockType)) {
            lockKeyMessage = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_LOCK_POSITIONKEY);
            lockKeyMessage = MessageFormat.format(lockKeyMessage, lockUserId, fiscalYear.toString(), positionNumber);
        }
        else if (BCConstants.LockTypes.POSITION_FUNDING_LOCK.equals(lockType)) {
            lockKeyMessage = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_LOCK_POSITIONFUNDINGKEY);
            lockKeyMessage = MessageFormat.format(lockKeyMessage, lockUserId, fiscalYear.toString(), positionNumber, chartOfAccountsCode, accountNumber, subAccountNumber);
        }
        else {
            lockKeyMessage = kualiConfigurationService.getPropertyValueAsString(BCKeyConstants.MSG_LOCK_ACCOUNTKEY);
            lockKeyMessage = MessageFormat.format(lockKeyMessage, lockUserId, fiscalYear.toString(), chartOfAccountsCode, accountNumber, subAccountNumber);
        }

        return lockKeyMessage;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;
        tempListLookupForm.setMethodToCall("refresh");
        return super.performLookup(mapping, form, request, response);
    }

}
