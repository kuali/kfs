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
import java.util.List;
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
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.OrgSelOpMode;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.service.BudgetDocumentService;
import org.kuali.module.budget.web.struts.form.BudgetConstructionSelectionForm;


/**
 * This class...
 */
public class BudgetConstructionSelectionAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSelectionAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;  

        //TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        //since BC sets locks up front, but need to verify this


//TODO will eventually need to setup some sort of authorization for typical user versus BC root approver
//root approvers have more controls present on the page
        //TODO should probably use service locator and call
        //DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer("<BCDoctype>");
//        BudgetConstructionDocumentAuthorizer budgetConstructionDocumentAuthorizer = new BudgetConstructionDocumentAuthorizer();
//        budgetConstructionSelectionForm.populateAuthorizationFields(budgetConstructionDocumentAuthorizer);

        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
 
        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());
        if ( !SpringContext.getBean(KualiModuleService.class).isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), bcAuthorizationType ) ){
            LOG.error("User not authorized to use this action: " + this.getClass().getName() );
            throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), bcAuthorizationType, getKualiModuleService().getResponsibleModule(this.getClass()) );
        }
    }

    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;

//TODO this needs to call the bc fiscal year function service
        budgetConstructionSelectionForm.setUniversityFiscalYear(new Integer(2008));

        budgetConstructionSelectionForm.getBudgetConstructionHeader().setUniversityFiscalYear(budgetConstructionSelectionForm.getUniversityFiscalYear());

//TODO need to make a call here to clear out all Objects(forms) stored in GlobalVariables.UserSession
//to help prevent memory leaks if the user fails to use application control flow

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performBCDocumentOpen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
//      TODO do lookup of header and call open if found, otherwise create blank doc and account hierarchy, then open if no error
//      TODO for now just return an error if the doc does not exist
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        BudgetConstructionHeader bcHeader = budgetConstructionSelectionForm.getBudgetConstructionHeader();

        Integer universityFiscalYear = bcHeader.getUniversityFiscalYear();
        String chartOfAccountsCode = bcHeader.getChartOfAccountsCode();
        String accountNumber = bcHeader.getAccountNumber() ;
        String subAccountNumber;
        if (StringUtils.isBlank(bcHeader.getSubAccountNumber())){
            subAccountNumber = KFSConstants.getDashSubAccountNumber();
        } else {
            subAccountNumber = bcHeader.getSubAccountNumber();
        }

        BudgetConstructionHeader tHeader = (BudgetConstructionHeader) SpringContext.getBean(BudgetDocumentService.class).getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        if (tHeader == null){
            //error ERROR_EXISTENCE
          GlobalVariables.getErrorMap().putError("budgetConstructionHeader",KFSKeyConstants.ERROR_EXISTENCE, "BC Document");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {
            // TODO abyrne changed this to reference config property, but not sure where this is being used
            String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_METHOD);
            parameters.put("universityFiscalYear", tHeader.getUniversityFiscalYear().toString());
            parameters.put("chartOfAccountsCode", tHeader.getChartOfAccountsCode());
            parameters.put("accountNumber", tHeader.getAccountNumber());
            parameters.put("subAccountNumber", tHeader.getSubAccountNumber());

            // anchor, if it exists
            if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
                parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
            }

            // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
            parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObject(form));

            String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.BC_DOCUMENT_ACTION, parameters);
            return new ActionForward(lookupUrl, true);
        }
    }


    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

//      TODO need a better way to detect return from lookups
//      returning from account lookup sets refreshcaller to accountLookupable, due to setting in account.xml
//              if (refreshCaller != null && refreshCaller.equalsIgnoreCase(KFSConstants.KUALI_LOOKUPABLE_IMPL)){
        if (refreshCaller != null && (refreshCaller.endsWith("Lookupable") || (refreshCaller.endsWith("LOOKUPABLE")))){
            final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] {"chartOfAccounts", "account", "subAccount", "budgetConstructionAccountReports"}));
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
     * This method sets up to forward to the BC Organization Selection screen using a specific operating mode. The various
     * operating modes include PULLUP, PUSHDOWN, REPORTS, SALSET, ACCOUNT. 
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
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.ORG_SEL_TREE_METHOD);
        parameters.put("operatingMode", opMode.toString());
        parameters.put("universityFiscalYear", budgetConstructionSelectionForm.getUniversityFiscalYear().toString());
        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObject(form));

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.ORG_SEL_TREE_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    public ActionForward performReportDump(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
 
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;  
        
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.REPORTS, mapping, form, request, response);
        
        return forward;
    }

    public ActionForward performRequestImport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
 
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;  
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES,KFSKeyConstants.ERROR_UNIMPLEMENTED, "Request Import");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }

    public ActionForward performOrgPullup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
 
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;  
        
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.PULLUP, mapping, form, request, response);
        
        return forward;
    }

    public ActionForward performOrgPushdown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
 
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;  
        
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.PUSHDOWN, mapping, form, request, response);
        
        return forward;
    }

    public ActionForward performMyAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
 
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;  
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES,KFSKeyConstants.ERROR_UNIMPLEMENTED, "Find My Budgeted Accounts");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }

    public ActionForward performMyOrganization(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
 
        BudgetConstructionSelectionForm budgetConstructionSelectionForm = (BudgetConstructionSelectionForm) form;  
        
        ActionForward forward = performOrgSelectionTree(OrgSelOpMode.ACCOUNT, mapping, form, request, response);
        
        return forward;
    }
}
