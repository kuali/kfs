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
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiBalanceInquiryReportMenuForm;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.OrgSelOpMode;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.bo.BudgetConstructionSubFundPick;
import org.kuali.module.budget.service.BudgetConstructionOrganizationJasperReportsService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetReportsControlListService;
import org.kuali.module.budget.web.struts.form.BudgetConstructionSelectionForm;
import org.kuali.module.budget.web.struts.form.OrganizationReportSelectionForm;

/**
 * Struts Action Class for the Organization Report Selection Screen.
 */
public class OrganizationReportSelectionAction extends KualiAction {

    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm subFundListSelectionForm = (OrganizationReportSelectionForm) form;
        String backUrl = subFundListSelectionForm.getBackLocation() + "?methodToCall=refresh&docFormKey=" + subFundListSelectionForm.getDocFormKey();
        return new ActionForward(backUrl, true);
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
        OrganizationReportSelectionForm subfundListSelectionForm = (OrganizationReportSelectionForm) form;
        subfundListSelectionForm.setOperatingModeTitle(BCConstants.Report.SELECTION_OPMODE_TITLE);
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        List<BudgetConstructionPullup> selectionSubTreeOrgs = (List<BudgetConstructionPullup>) GlobalVariables.getUserSession().retrieveObject(BCConstants.Report.SESSION_NAME_SELECTED_ORGS);
        BudgetReportsControlListService budgetReportsControlListService = SpringContext.getBean(BudgetReportsControlListService.class);
        BudgetConstructionOrganizationJasperReportsService budgetConstructionOrganizationJasperReportsService = SpringContext.getBean(BudgetConstructionOrganizationJasperReportsService.class);
        
        Iterator iter = subfundListSelectionForm.getBcSubfundList().iterator();
        while (iter.hasNext()) {
            BudgetConstructionSubFundPick bcsfp = (BudgetConstructionSubFundPick) iter.next();
            if (request.getParameter(bcsfp.getSubFundGroupCode()) != null) {
                subfundListSelectionForm.getSelectedSubfundGroupCode().add(request.getParameter(bcsfp.getSubFundGroupCode()));
            }
        }

        budgetReportsControlListService.updateReportsSelectedSubFundGroupFlags(personUserIdentifier, subfundListSelectionForm.getSelectedSubfundGroupCode());
        budgetReportsControlListService.cleanReportsAccountSummaryTable(personUserIdentifier);

        // check consolidation
        if (subfundListSelectionForm.getAccSumConsolidation() != null) {
            budgetReportsControlListService.updateRepotsAccountSummaryTableWithConsolidation(personUserIdentifier);
        }
        else {
            budgetReportsControlListService.updateRepotsAccountSummaryTable(personUserIdentifier);
        }

        // Keep User's selection
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        subfundListSelectionForm.setBcSubfundList((List) SpringContext.getBean(BudgetConstructionOrganizationReportsService.class).getBySearchCriteria(BudgetConstructionSubFundPick.class, searchCriteria));

        // Open PDF file in new window.
        String filename = BCConstants.Report.FILE_NAME_ORG_ACCOUNT_SUMMARY + BCConstants.Report.FILE_EXTENSION_PDF;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        budgetConstructionOrganizationJasperReportsService.generageOrgAccountSummaryReport(personUserIdentifier, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, filename);

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
        for (BudgetConstructionSubFundPick bcsfp : subfundListSelectionForm.getBcSubfundList()) {
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
        for (BudgetConstructionSubFundPick bcsfp : subfundListSelectionForm.getBcSubfundList()) {
            bcsfp.setReportFlag(new Integer(0));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
