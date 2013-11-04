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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.OrgSelOpMode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSelect;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService;
import org.kuali.kfs.module.bc.document.validation.event.AddBudgetConstructionDocumentEvent;
import org.kuali.kfs.module.bc.report.ReportControlListBuildHelper;
import org.kuali.kfs.module.bc.util.BudgetUrlUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * This class...
 */
public class BudgetConstructionSelectionAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSelectionAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        // set force rebuild on report build helper so each time we go out of report screen and come back the list will be rebuilt
        ReportControlListBuildHelper buildHelper = (ReportControlListBuildHelper) GlobalVariables.getUserSession().retrieveObject(BCConstants.Report.CONTROL_BUILD_HELPER_SESSION_NAME);
        if (buildHelper == null) {
            buildHelper = new ReportControlListBuildHelper();
        }
        buildHelper.setForceRebuild(true);
        GlobalVariables.getUserSession().addObject(BCConstants.Report.CONTROL_BUILD_HELPER_SESSION_NAME, buildHelper);

        return forward;
    }

    /**
     * Performs the initial load of the selection screen. Checks for the active BC fiscal year and initializes the fiscal year to be
     * budgeted and used for all other operations throughout the system
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        Boolean isBCInProgress = (Boolean) GlobalVariables.getUserSession().retrieveObject(BCConstants.BC_IN_PROGRESS_SESSIONFLAG);
        if (isBCInProgress != null && isBCInProgress) {

            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {
                // ask question if not already asked
                return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_DELETE_QUESTION, kualiConfiguration.getPropertyValueAsString(BCKeyConstants.QUESTION_CONFIRM_CLEANUP), KFSConstants.CONFIRMATION_QUESTION, "loadExpansionScreen", "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if ((KFSConstants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                    // clear out all BC related Objects(forms) stored in GlobalVariables.UserSession
                    // to help prevent memory leaks if the user fails to use application control flow
                    GlobalVariables.getUserSession().removeObjectsByPrefix(BCConstants.FORMKEY_PREFIX);

                    // clear out any session object form attribute
                    HttpSession sess = request.getSession(Boolean.FALSE);
                    sess.removeAttribute(BCConstants.MAPPING_ATTRIBUTE_KUALI_FORM);

                }
                else {
                    budgetConstructionSelectionForm.setSessionInProgressDetected(true);
                    KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_PREVIOUS_SESSION_NOTCLEANED);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }
        } else {
            // cleanup anyway to handle back door lost session case
            GlobalVariables.getUserSession().removeObjectsByPrefix(BCConstants.FORMKEY_PREFIX);

            // clear out any session object form attribute
            HttpSession sess = request.getSession(Boolean.FALSE);
            sess.removeAttribute(BCConstants.MAPPING_ATTRIBUTE_KUALI_FORM);
        }


        // get active BC year and complain when anything other than one year active for now
        List<Integer> activeBCYears = fiscalYearFunctionControlService.getActiveBudgetYear();
        if (activeBCYears.size() != 1) {
            budgetConstructionSelectionForm.setUniversityFiscalYear(null);
            if (activeBCYears.size() < 1) {
                KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SYSTEM_NOT_ACTIVE);
            }
            else {
                KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SYSTEM_MULTIPLE_ACTIVE);
            }
        }
        else {
            budgetConstructionSelectionForm.setUniversityFiscalYear(activeBCYears.get(0));
        }

        budgetConstructionSelectionForm.getBudgetConstructionHeader().setUniversityFiscalYear(budgetConstructionSelectionForm.getUniversityFiscalYear());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Opens a Budget Construction document. Creates a new (blank) BC document, if one does not exist. The new BC document is
     * created at level zero and the associated account organization hierarchy is built.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBCDocumentOpen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // do lookup of header and call open if found, otherwise create blank doc and account hierarchy, then open if no error
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        BudgetConstructionHeader bcHeader = budgetConstructionSelectionForm.getBudgetConstructionHeader();

        Integer universityFiscalYear = bcHeader.getUniversityFiscalYear();
        String chartOfAccountsCode = bcHeader.getChartOfAccountsCode();
        String accountNumber = bcHeader.getAccountNumber();
        String subAccountNumber;
        if (StringUtils.isBlank(bcHeader.getSubAccountNumber())) {
            subAccountNumber = KFSConstants.getDashSubAccountNumber();
        }
        else {
            subAccountNumber = bcHeader.getSubAccountNumber();
        }

        BudgetConstructionHeader tHeader = SpringContext.getBean(BudgetDocumentService.class).getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        if (tHeader == null) {

            // get a bare bones BC document to run the rule engine against
            // if rulesPassed, use the document to instantiate to the DB
            BudgetConstructionDocument budgetConstructionDocument = (BudgetConstructionDocument) SpringContext.getBean(DocumentService.class).getNewDocument(BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
            budgetConstructionDocument.setUniversityFiscalYear(universityFiscalYear);
            budgetConstructionDocument.setChartOfAccountsCode(chartOfAccountsCode);
            budgetConstructionDocument.setAccountNumber(accountNumber);
            budgetConstructionDocument.setSubAccountNumber(subAccountNumber);
            List refreshFields = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.ACCOUNT, KFSPropertyConstants.SUB_ACCOUNT }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionDocument, refreshFields);

            boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddBudgetConstructionDocumentEvent(BCPropertyConstants.BUDGET_CONSTRUCTION_HEADER, budgetConstructionDocument));
            if (rulePassed) {
                List<BudgetConstructionAccountOrganizationHierarchy> newAccountOrganizationHierarchy = SpringContext.getBean(BudgetDocumentService.class).retrieveOrBuildAccountOrganizationHierarchy(universityFiscalYear, chartOfAccountsCode, accountNumber);
                if (newAccountOrganizationHierarchy == null || newAccountOrganizationHierarchy.isEmpty()) {
                    GlobalVariables.getMessageMap().putError("budgetConstructionHeader", BCKeyConstants.ERROR_BUDGET_ACCOUNT_ORGANIZATION_HIERARCHY, chartOfAccountsCode + "-" + accountNumber);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }

                // hierarchy created - attempt to create BC document
                // SpringContext.getBean(BudgetDocumentService.class).instantiateNewBudgetConstructionDocument(universityFiscalYear,
                // chartOfAccountsCode, accountNumber, subAccountNumber);
                SpringContext.getBean(BudgetDocumentService.class).instantiateNewBudgetConstructionDocument(budgetConstructionDocument);
                tHeader = SpringContext.getBean(BudgetDocumentService.class).getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
                if (tHeader == null) {

                    GlobalVariables.getMessageMap().putError("budgetConstructionHeader", KFSKeyConstants.ERROR_EXISTENCE, "BC Document");
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                else {
                    // drop to open the newly created document
                }
            }
            else {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        this.flagBCInProgress();

        // open the existing or newly created BC document
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_METHOD);
        parameters.put("universityFiscalYear", tHeader.getUniversityFiscalYear().toString());
        parameters.put("chartOfAccountsCode", tHeader.getChartOfAccountsCode());
        parameters.put("accountNumber", tHeader.getAccountNumber());
        parameters.put("subAccountNumber", tHeader.getSubAccountNumber());
        parameters.put("pickListMode", "false");
        parameters.put(BCPropertyConstants.MAIN_WINDOW, "true");

        String lookupUrl = BudgetUrlUtil.buildBudgetUrl(mapping, budgetConstructionSelectionForm, BCConstants.BC_DOCUMENT_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }


    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

        // returning from account lookup sets refreshCaller to accountLookupable, due to setting in account.xml
        if (refreshCaller != null && (refreshCaller.toUpperCase().endsWith(KFSConstants.LOOKUPABLE_SUFFIX.toUpperCase()))) {
            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "chartOfAccounts", "account", "subAccount", "budgetConstructionAccountReports" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionSelectionForm.getBudgetConstructionHeader(), REFRESH_FIELDS);
        }

        // clearout any BC inprogress semaphore, regardless of where we are returning from
        // this handles the lock monitor no refreshCaller return case
        GlobalVariables.getUserSession().removeObject(BCConstants.BC_IN_PROGRESS_SESSIONFLAG);

        // clear out any session object form attribute
        HttpSession sess = request.getSession(Boolean.FALSE);
        sess.removeAttribute(BCConstants.MAPPING_ATTRIBUTE_KUALI_FORM);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        return mapping.findForward(KFSConstants.MAPPING_PORTAL);
    }

    public ActionForward performOrgSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.SALSET, mapping, form, request, response);

        return forward;
    }

    /**
     * This method sets up to forward to the BC Organization Selection screen using a specific operating mode. The various operating
     * modes include PULLUP, PUSHDOWN, REPORTS, SALSET, ACCOUNT.
     *
     * @param opMode
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performOrgSelectionTree(OrgSelOpMode opMode, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        this.flagBCInProgress();

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.ORG_SEL_TREE_METHOD);
        parameters.put("operatingMode", opMode.toString());
        parameters.put("universityFiscalYear", budgetConstructionSelectionForm.getUniversityFiscalYear().toString());

        String lookupUrl = BudgetUrlUtil.buildBudgetUrl(mapping, budgetConstructionSelectionForm, BCConstants.ORG_SEL_TREE_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * Passes control to the Organization Selection to run the organization reports subsystem
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performReportDump(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.REPORTS, mapping, form, request, response);

        return forward;
    }

    /**
     * Passes control to the request import subsystem
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performRequestImport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        this.flagBCInProgress();

        String lookupUrl = BudgetUrlUtil.buildBudgetUrl(mapping, budgetConstructionSelectionForm, BCConstants.REQUEST_IMPORT_ACTION, null);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * Passes control to the Pay Rate import/export subsystem
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performPayrateImportExport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        this.flagBCInProgress();

        String lookupUrl = BudgetUrlUtil.buildBudgetUrl(mapping, budgetConstructionSelectionForm, BCConstants.PAYRATE_IMPORT_EXPORT_ACTION, null);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * Builds forward URL to lock monitor page, following expansion screen pattern. Also checks if the user has permission for the
     * unlock action and sets the show action column property accordingly.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performLockMonitor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        this.flagBCInProgress();

        Map<String, String> urlParms = new HashMap<String, String>();

        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, budgetConstructionSelectionForm, BCConstants.TempListLookupMode.LOCK_MONITOR, BudgetConstructionLockSummary.class.getName(), urlParms);

        return new ActionForward(url, true);
    }

    /**
     * Passes control to the Organization Selection to run the organization pullup subsystem
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performOrgPullup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.PULLUP, mapping, form, request, response);

        return forward;
    }

    /**
     * Passes control to the Organization Selection to run the organization pushdown subsystem
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performOrgPushdown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.PUSHDOWN, mapping, form, request, response);

        return forward;
    }

    /**
     * Calls service to build the account list for which the user is a manager and delegate. Then forwards to temp list action to
     * display the results.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performMyAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        // call service to build account list and give message if empty
        int rowCount = SpringContext.getBean(OrganizationBCDocumentSearchService.class).buildAccountManagerDelegateList(GlobalVariables.getUserSession().getPerson().getPrincipalId(), budgetConstructionSelectionForm.getUniversityFiscalYear());
        if (rowCount == 0) {
            KNSGlobalVariables.getMessageList().add(BCKeyConstants.ERROR_NO_RECORDS_MY_ACCOUNTS);

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        this.flagBCInProgress();

        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, budgetConstructionSelectionForm, BCConstants.TempListLookupMode.ACCOUNT_SELECT_MANAGER_DELEGATE, BudgetConstructionAccountSelect.class.getName(), null);

        return new ActionForward(url, true);
    }

    /**
     * Passes control to the Organization Selection to run the organization budgeted account list subsystem
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performMyOrganization(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.ACCOUNT, mapping, form, request, response);

        return forward;
    }

    public void flagBCInProgress() {

        // Overwrite or add the BC in progress flag
        // This is used as a semaphore to control cleanup of session BC Temp objects
        GlobalVariables.getUserSession().addObject(BCConstants.BC_IN_PROGRESS_SESSIONFLAG, Boolean.TRUE);
    }
}
