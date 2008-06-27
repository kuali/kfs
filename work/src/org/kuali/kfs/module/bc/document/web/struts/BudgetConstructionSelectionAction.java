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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCConstants.OrgSelOpMode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSelect;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPositionSelect;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.module.bc.report.ReportControlListBuildHelper;
import org.kuali.kfs.module.bc.util.BudgetUrlUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;


/**
 * This class...
 */
public class BudgetConstructionSelectionAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSelectionAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
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

        // TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        // since BC sets locks up front, but need to verify this


        // TODO will eventually need to setup some sort of authorization for typical user versus BC root approver
        // root approvers have more controls present on the page
        // TODO should probably use service locator and call
        // DocumentAuthorizer documentAuthorizer =
        // SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer("<BCDoctype>");
        // BudgetConstructionDocumentAuthorizer budgetConstructionDocumentAuthorizer = new BudgetConstructionDocumentAuthorizer();
        // budgetConstructionSelectionForm.populateAuthorizationFields(budgetConstructionDocumentAuthorizer);

        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {

        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());
        if (!SpringContext.getBean(KualiModuleService.class).isAuthorized(GlobalVariables.getUserSession().getFinancialSystemUser(), bcAuthorizationType)) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName());
            throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUserIdentifier(), bcAuthorizationType, getKualiModuleService().getResponsibleModule(this.getClass()));
        }
    }

    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        // TODO this needs to call the bc fiscal year function service
        budgetConstructionSelectionForm.setUniversityFiscalYear(new Integer(2008));

        budgetConstructionSelectionForm.getBudgetConstructionHeader().setUniversityFiscalYear(budgetConstructionSelectionForm.getUniversityFiscalYear());

        // clear out all BC related Objects(forms) stored in GlobalVariables.UserSession
        // to help prevent memory leaks if the user fails to use application control flow
        GlobalVariables.getUserSession().removeObjectsByPrefix(BCConstants.FORMKEY_PREFIX);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performBCDocumentOpen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // TODO do lookup of header and call open if found, otherwise create blank doc and account hierarchy, then open if no error
        // TODO for now just return an error if the doc does not exist
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

        BudgetConstructionHeader tHeader = (BudgetConstructionHeader) SpringContext.getBean(BudgetDocumentService.class).getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        if (tHeader == null) {
            // error ERROR_EXISTENCE
            GlobalVariables.getErrorMap().putError("budgetConstructionHeader", KFSKeyConstants.ERROR_EXISTENCE, "BC Document");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_METHOD);
            parameters.put("universityFiscalYear", tHeader.getUniversityFiscalYear().toString());
            parameters.put("chartOfAccountsCode", tHeader.getChartOfAccountsCode());
            parameters.put("accountNumber", tHeader.getAccountNumber());
            parameters.put("subAccountNumber", tHeader.getSubAccountNumber());
            parameters.put("pickListMode", "false");
            
            String lookupUrl = BudgetUrlUtil.buildBudgetUrl(mapping, budgetConstructionSelectionForm, BCConstants.BC_DOCUMENT_ACTION, parameters);
            
            return new ActionForward(lookupUrl, true);
        }
    }


    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

        // TODO need a better way to detect return from lookups
        // returning from account lookup sets refreshcaller to accountLookupable, due to setting in account.xml
        // if (refreshCaller != null && refreshCaller.equalsIgnoreCase(KFSConstants.KUALI_LOOKUPABLE_IMPL)){
        if (refreshCaller != null && (refreshCaller.endsWith("Lookupable") || (refreshCaller.endsWith("LOOKUPABLE")))) {
            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "chartOfAccounts", "account", "subAccount", "budgetConstructionAccountReports" }));
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetConstructionSelectionForm.getBudgetConstructionHeader(), REFRESH_FIELDS);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

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

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.ORG_SEL_TREE_METHOD);
        parameters.put("operatingMode", opMode.toString());
        parameters.put("universityFiscalYear", budgetConstructionSelectionForm.getUniversityFiscalYear().toString());
        
        String lookupUrl = BudgetUrlUtil.buildBudgetUrl(mapping, budgetConstructionSelectionForm, BCConstants.ORG_SEL_TREE_ACTION, parameters);
        
        return new ActionForward(lookupUrl, true);
    }

    public ActionForward performReportDump(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.REPORTS, mapping, form, request, response);

        return forward;
    }

    public ActionForward performRequestImport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        String lookupUrl = BudgetUrlUtil.buildBudgetUrl(mapping, budgetConstructionSelectionForm, BCConstants.REQUEST_IMPORT_ACTION, null);
        
        return new ActionForward(lookupUrl, true);
    }
    
    public ActionForward performPayrateImportExport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        String lookupUrl = BudgetUrlUtil.buildBudgetUrl(mapping, budgetConstructionSelectionForm, BCConstants.PAYRATE_IMPORT_EXPORT_ACTION, null);
    
        return new ActionForward(lookupUrl, true);
    }
    
    /**
     * Builds forward URL to lock monitor page, following expansion screen pattern. Also checks if the user has permission for the unlock action and sets the
     * show action column property accordingly.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performLockMonitor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        Map<String, String> urlParms = new HashMap<String, String>();

        // check if current user is an budget approver at the top level (root) org
        String[] chartOrg = SpringContext.getBean(OrganizationService.class).getRootOrganizationCode();
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier();
        boolean isRootApprover = SpringContext.getBean(PermissionService.class).isOrgReviewApprover(personUserIdentifier, chartOrg[0], chartOrg[1]);
        urlParms.put(KFSConstants.SUPPRESS_ACTIONS, Boolean.toString(!isRootApprover));

        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, budgetConstructionSelectionForm, BCConstants.TempListLookupMode.LOCK_MONITOR, BudgetConstructionLockSummary.class.getName(), urlParms);

        return new ActionForward(url, true);
    }
    
    public ActionForward performOrgPullup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.PULLUP, mapping, form, request, response);

        return forward;
    }

    public ActionForward performOrgPushdown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.PUSHDOWN, mapping, form, request, response);

        return forward;
    }

    /**
     * Calls service to build the account list for which the user is a manager and delegate. Then forwards to temp list action to display the results.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performMyAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

        // call service to build account list and give message if empty
        int rowCount = SpringContext.getBean(OrganizationBCDocumentSearchService.class).buildAccountManagerDelegateList(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier(), budgetConstructionSelectionForm.getUniversityFiscalYear());
        if (rowCount == 0) {
            GlobalVariables.getMessageList().add(BCKeyConstants.ERROR_NO_RECORDS_MY_ACCOUNTS);
            
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, budgetConstructionSelectionForm, BCConstants.TempListLookupMode.ACCOUNT_SELECT_MANAGER_DELEGATE, BudgetConstructionAccountSelect.class.getName(), null);

        return new ActionForward(url, true);
    }

    public ActionForward performMyOrganization(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.ACCOUNT, mapping, form, request, response);

        return forward;
    }
}
