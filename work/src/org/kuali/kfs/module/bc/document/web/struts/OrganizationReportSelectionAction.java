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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.Guid;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.bo.BudgetConstructionSubFundPick;
import org.kuali.module.budget.service.BudgetConstructionAccountSummaryReportService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationJasperReportsService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetReportsControlListService;
import org.kuali.module.budget.web.struts.form.OrganizationReportSelectionForm;

/**
 * Struts Action Class for the Organization Report Selection Screen.
 */
public class OrganizationReportSelectionAction extends KualiAction {

    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        String backUrl = organizationReportSelectionForm.getBackLocation() + "?methodToCall=refresh&docFormKey=" + organizationReportSelectionForm.getDocFormKey();
        return new ActionForward(backUrl, true);
    }

    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        if (BCConstants.Report.reportModeOnlySubfundCodeSelectionMapping.contains(organizationReportSelectionForm.getReportMode())) {
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
            if (organizationReportSelectionForm.isBuildControlList()) {
                organizationReportSelectionForm.setOperatingModeTitle(BCConstants.Report.SELECTION_OPMODE_TITLE);
                // change flag
                BudgetReportsControlListService budgetReportsControlListService = SpringContext.getBean(BudgetReportsControlListService.class);
                String idForSession = (new Guid()).toString();
                List<BudgetConstructionPullup> selectedOrgs = (List<BudgetConstructionPullup>) GlobalVariables.getUserSession().retrieveObject(BCConstants.Report.SESSION_NAME_SELECTED_ORGS);
                budgetReportsControlListService.changeFlagOrganizationAndChartOfAccountCodeSelection(personUserIdentifier, selectedOrgs);
                budgetReportsControlListService.updateReportsControlList(idForSession, personUserIdentifier, organizationReportSelectionForm.getUniversityFiscalYear(), selectedOrgs);
                budgetReportsControlListService.updateReportsSubFundGroupSelectList(personUserIdentifier);
                organizationReportSelectionForm.setBuildControlList(false);
            }
            Map searchCriteria = new HashMap();
            searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
            organizationReportSelectionForm.setBcSubFunds((List) SpringContext.getBean(BudgetConstructionOrganizationReportsService.class).getBySearchCriteria(BudgetConstructionSubFundPick.class, searchCriteria));
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    
    /**
     * submits user's selection and generate report.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward submit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        organizationReportSelectionForm.setOperatingModeTitle(BCConstants.Report.SELECTION_OPMODE_TITLE);
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        
        String fileName = "";
        if (organizationReportSelectionForm.getReportMode().equals(BCConstants.Report.ACCOUNT_SUMMARY_REPORT)){
            BudgetReportsControlListService budgetReportsControlListService = SpringContext.getBean(BudgetReportsControlListService.class);
            
            //update flags with selected sub-fund group code 
            budgetReportsControlListService.updateReportsSelectedSubFundGroupFlags(personUserIdentifier, organizationReportSelectionForm.getBcSubFunds());
            
            // generate report and returned fileName and system will create a PDF file with the file name. 
            performAccountSummaryReport(organizationReportSelectionForm, personUserIdentifier, request);
            fileName = BCConstants.Report.FILE_NAME_ORG_ACCOUNT_SUMMARY + BCConstants.Report.FILE_EXTENSION_PDF;
        }
        
        //other reports
        
        /*
        // Keep User's selection
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        organizationReportSelectionForm.setBcSubfundList((List) SpringContext.getBean(BudgetConstructionOrganizationReportsService.class).getBySearchCriteria(BudgetConstructionSubFundPick.class, searchCriteria));
        */
        
        // Open PDF file in new window.
        BudgetConstructionOrganizationJasperReportsService budgetConstructionOrganizationJasperReportsService = SpringContext.getBean(BudgetConstructionOrganizationJasperReportsService.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        budgetConstructionOrganizationJasperReportsService.generateOrgAccountSummaryReport(personUserIdentifier, organizationReportSelectionForm.getUniversityFiscalYear(),baos);
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName);

        return null;
    }

    /**
     * selects all sub-fund group code.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectAllSubFundGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm subfundListSelectionForm = (OrganizationReportSelectionForm) form;
        for (BudgetConstructionSubFundPick bcsfp : subfundListSelectionForm.getBcSubFunds()) {
            bcsfp.setReportFlag(new Integer(1));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * un-selects all sub-fund group code.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unSelectAllSubFundGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm subfundListSelectionForm = (OrganizationReportSelectionForm) form;
        for (BudgetConstructionSubFundPick bcsfp : subfundListSelectionForm.getBcSubFunds()) {
            bcsfp.setReportFlag(new Integer(0));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    
    
    
    
    public void performAccountSummaryReport(OrganizationReportSelectionForm organizationReportSelectionForm, String personUserIdentifier, HttpServletRequest request) {
        BudgetConstructionAccountSummaryReportService budgetConstructionAccountSummaryReportService = SpringContext.getBean(BudgetConstructionAccountSummaryReportService.class);
        
        // check consolidation
        if (organizationReportSelectionForm.isReportConsolidation()) {
            budgetConstructionAccountSummaryReportService.updateReportsAccountSummaryTableWithConsolidation(personUserIdentifier);
        }
        else {
            budgetConstructionAccountSummaryReportService.updateReportsAccountSummaryTable(personUserIdentifier);
        }
    }
    
    
    
}
