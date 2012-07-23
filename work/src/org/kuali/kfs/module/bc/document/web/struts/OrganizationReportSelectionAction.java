/*
 * Copyright 2008 The Kuali Foundation
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRParameter;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCConstants.Report.ReportSelectMode;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BudgetConstructionReportMode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectPick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReasonCodePick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSubFundPick;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountFundingDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountObjectDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionAccountSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionLevelSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionList2PLGReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionMonthSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionObjectSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionPositionFundingDetailReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonStatisticsReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReasonSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionSalaryStatisticsReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionSalarySummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionSubFundSummaryReportService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionSynchronizationProblemsReportService;
import org.kuali.kfs.module.bc.document.service.BudgetReportsControlListService;
import org.kuali.kfs.module.bc.report.ReportControlListBuildHelper;
import org.kuali.kfs.module.bc.util.BudgetUrlUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Struts Action Class for the Organization Report Selection Screen.
 */
public class OrganizationReportSelectionAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReportSelectionAction.class);

    /**
     * Called from org select or account listing. Checks for needed control list build, makes call to build control list if
     * necessary, and forwards to subfund or object code select page.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        String principalName = GlobalVariables.getUserSession().getPerson().getPrincipalId();

        // retrieve report mode to determine how control list should be built and what select screen should be rendered
        BudgetConstructionReportMode reportMode = BudgetConstructionReportMode.getBudgetConstructionReportModeByName(organizationReportSelectionForm.getReportMode());
        if (reportMode == null) {
            LOG.error("Invalid report mode passed to report select action: " + organizationReportSelectionForm.getReportMode());
            throw new RuntimeException("Invalid report mode passed to report select action: " + organizationReportSelectionForm.getReportMode());
        }

        // retrieve build helper to determine if a control list rebuild is needed
        ReportControlListBuildHelper buildHelper = (ReportControlListBuildHelper) GlobalVariables.getUserSession().retrieveObject(BCConstants.Report.CONTROL_BUILD_HELPER_SESSION_NAME);
        if (buildHelper == null) {
            // session timeout, need to rebuild build request
            buildHelper = new ReportControlListBuildHelper();

            Collection<BudgetConstructionPullup> selectedOrganizations = SpringContext.getBean(BudgetReportsControlListService.class).retrieveSelectedOrganziations(principalName);
            buildHelper.addBuildRequest(organizationReportSelectionForm.getCurrentPointOfViewKeyCode(), selectedOrganizations, reportMode.reportBuildMode);
            GlobalVariables.getUserSession().addObject(BCConstants.Report.CONTROL_BUILD_HELPER_SESSION_NAME, buildHelper);
        }

        // do list builds
        buildControlLists(principalName, organizationReportSelectionForm.getUniversityFiscalYear(), buildHelper, reportMode.reportSelectMode);

        // a few reports go just against the account control table, therefore we are ready to run the report
        if (ReportSelectMode.ACCOUNT.equals(reportMode.reportSelectMode)) {
            // fixed null point exception of operationgModeTitle.
            organizationReportSelectionForm.setOperatingModeTitle(BCConstants.Report.NONE_SELECTION_TITLE);
            return performReport(mapping, form, request, response);
        }

        // setup action form
        if (ReportSelectMode.SUBFUND.equals(reportMode.reportSelectMode)) {
            organizationReportSelectionForm.setSubFundPickList((List) SpringContext.getBean(BudgetReportsControlListService.class).retrieveSubFundList(principalName));
            organizationReportSelectionForm.setOperatingModeTitle(BCConstants.Report.SUB_FUND_SELECTION_TITLE);
        }
        else if (ReportSelectMode.OBJECT_CODE.equals(reportMode.reportSelectMode) || ReportSelectMode.REASON.equals(reportMode.reportSelectMode)) {
            organizationReportSelectionForm.setObjectCodePickList((List) SpringContext.getBean(BudgetReportsControlListService.class).retrieveObjectCodeList(principalName));
            organizationReportSelectionForm.setOperatingModeTitle(BCConstants.Report.OBJECT_CODE_SELECTION_TITLE);
            organizationReportSelectionForm.getBudgetConstructionReportThresholdSettings().setLockThreshold(reportMode.lockThreshold);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Makes service calls to rebuild the report control and sub-fund or object code select lists if needed.
     * 
     * @param principalName - current user requesting the report
     * @param buildHelper - contains the current and requested build states
     * @param reportSelectMode - indicates whether the report takes a sub-fund or object code select list
     */
    protected void buildControlLists(String principalName, Integer universityFiscalYear, ReportControlListBuildHelper buildHelper, ReportSelectMode reportSelectMode) {
        BudgetReportsControlListService budgetReportsControlListService = SpringContext.getBean(BudgetReportsControlListService.class);

        if (buildHelper.isBuildNeeded()) {
            String[] pointOfViewFields = buildHelper.getRequestedState().getPointOfView().split("[-]");
            budgetReportsControlListService.updateReportsControlList(principalName, universityFiscalYear, pointOfViewFields[0], pointOfViewFields[1], buildHelper.getRequestedState().getBuildMode());

            if (ReportSelectMode.SUBFUND.equals(reportSelectMode)) {
                budgetReportsControlListService.updateReportSubFundGroupSelectList(principalName);
            }
            else if (ReportSelectMode.OBJECT_CODE.equals(reportSelectMode) || ReportSelectMode.REASON.equals(reportSelectMode)) {
                budgetReportsControlListService.updateReportObjectCodeSelectList(principalName);
            }

            buildHelper.requestBuildComplete();
            GlobalVariables.getUserSession().addObject(BCConstants.Report.CONTROL_BUILD_HELPER_SESSION_NAME, buildHelper);
        }
    }

    /**
     * Generates the Budget Report and returns pdf.
     */
    public ActionForward performReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();

        BudgetConstructionReportMode reportMode = BudgetConstructionReportMode.getBudgetConstructionReportModeByName(organizationReportSelectionForm.getReportMode());
        if (!storeCodeSelections(organizationReportSelectionForm, reportMode, principalId)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        // validate threshold settings if needed
        if (reportMode == BudgetConstructionReportMode.REASON_STATISTICS_REPORT || reportMode == BudgetConstructionReportMode.REASON_SUMMARY_REPORT || reportMode == BudgetConstructionReportMode.SALARY_SUMMARY_REPORT){
            if (!this.validThresholdSettings(organizationReportSelectionForm.getBudgetConstructionReportThresholdSettings())){
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        // for report exports foward to export action to display formatting screen
        if (reportMode.export) {
            String exportUrl = this.buildReportExportForwardURL(organizationReportSelectionForm, mapping);
            return new ActionForward(exportUrl, true);
        }

        // build report data and populate report objects for rendering
        Collection reportSet = buildReportData(reportMode, organizationReportSelectionForm.getUniversityFiscalYear(), principalId, organizationReportSelectionForm.isReportConsolidation(), organizationReportSelectionForm.getBudgetConstructionReportThresholdSettings());

        // build pdf and stream back
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // output the report or a message if empty
        if (reportSet.isEmpty()) {
            List<String> messageList = new ArrayList<String>();
            messageList.add(BCConstants.Report.MSG_REPORT_NO_DATA);
            SpringContext.getBean(BudgetConstructionReportsServiceHelper.class).generatePdf(messageList, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportMode.jasperFileName + ReportGeneration.PDF_FILE_EXTENSION);
        }
        else {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(BCConstants.Report.REPORT_MESSAGES_CLASSPATH, Locale.getDefault());
            Map<String, Object> reportData = new HashMap<String, Object>();
            reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);

            SpringContext.getBean(ReportGenerationService.class).generateReportToOutputStream(reportData, reportSet, BCConstants.Report.REPORT_TEMPLATE_CLASSPATH + reportMode.jasperFileName, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportMode.jasperFileName + ReportGeneration.PDF_FILE_EXTENSION);
        }
        return null;
    }


    /**
     * Checks and stores sub-fund, object code, or reason code list depenending on the report mode and which screen we are on.
     * 
     * @param organizationReportSelectionForm - OrganizationReportSelectionForm containing the select lists
     * @param reportMode - BudgetConstructionReportMode for the report being ran
     * @return - true if a selection was found and the list was stored or if we need to show reason code select screen, false
     *         otherwise
     */
    protected boolean storeCodeSelections(OrganizationReportSelectionForm organizationReportSelectionForm, BudgetConstructionReportMode reportMode, String principalName) {
        boolean codeSelected = true;

        if (ReportSelectMode.SUBFUND.equals(reportMode.reportSelectMode)) {
            // came from sub fund select screen so need to store sub fund selection settings
            if (!storedSelectedSubFunds(organizationReportSelectionForm.getSubFundPickList())) {
                codeSelected = false;
            }
        }
        else if (organizationReportSelectionForm.getOperatingModeTitle().equals(BCConstants.Report.OBJECT_CODE_SELECTION_TITLE)) {
            // came from object code select screen so need to store object code selection settings
            if (!storedSelectedObjectCodes(organizationReportSelectionForm.getObjectCodePickList())) {
                codeSelected = false;
            }

            // determine if we need to setup reason code select
            if (ReportSelectMode.REASON.equals(reportMode.reportSelectMode) && !organizationReportSelectionForm.getBudgetConstructionReportThresholdSettings().isUseThreshold()) {
                BudgetReportsControlListService budgetReportsControlListService = SpringContext.getBean(BudgetReportsControlListService.class);

                // rebuild reason code control list
                budgetReportsControlListService.updateReportReasonCodeSelectList(principalName);

                // setup form
                organizationReportSelectionForm.setReasonCodePickList((List) budgetReportsControlListService.retrieveReasonCodeList(principalName));
                organizationReportSelectionForm.setOperatingModeTitle(BCConstants.Report.REASON_CODE_SELECTION_TITLE);
                codeSelected = false;
            }
        }
        else if (organizationReportSelectionForm.getOperatingModeTitle().equals(BCConstants.Report.REASON_CODE_SELECTION_TITLE)) {
            // came from reason code select screen so need to store reason code selection settings
            if (!storedSelectedReasonCodes(organizationReportSelectionForm.getReasonCodePickList())) {
                codeSelected = false;
            }
        }

        return codeSelected;
    }

    /**
     * Calls the report service for the given reportMode to build the report data in the db then populate the reports objects for
     * rendering to pdf.
     * 
     * @param reportMode - BudgetConstructionReportMode indicates which report we are running
     * @param universityFiscalYear - budget fiscal year
     * @param principalId - user running report
     * @param runConsolidated - indicates whether the report should be ran consolidated (if it has a consolidated option)
     * @param budgetConstructionReportThresholdSettings - contains threshold setting options
     * @return Collection - Reports objects that contain built data
     */
    protected Collection buildReportData(BudgetConstructionReportMode reportMode, Integer universityFiscalYear, String principalId, boolean runConsolidated, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        Collection reportData = new ArrayList();

        switch (reportMode) {
            case ACCOUNT_SUMMARY_REPORT:
                SpringContext.getBean(BudgetConstructionAccountSummaryReportService.class).updateReportsAccountSummaryTable(principalId, runConsolidated);
                reportData = SpringContext.getBean(BudgetConstructionAccountSummaryReportService.class).buildReports(universityFiscalYear, principalId, runConsolidated);
                break;
            case SUBFUND_SUMMARY_REPORT:
                SpringContext.getBean(BudgetConstructionSubFundSummaryReportService.class).updateSubFundSummaryReport(principalId);
                reportData = SpringContext.getBean(BudgetConstructionSubFundSummaryReportService.class).buildReports(universityFiscalYear, principalId);
                break;
            case LEVEL_SUMMARY_REPORT:
                SpringContext.getBean(BudgetConstructionLevelSummaryReportService.class).updateLevelSummaryReport(principalId);
                reportData = SpringContext.getBean(BudgetConstructionLevelSummaryReportService.class).buildReports(universityFiscalYear, principalId);
                break;
            case OBJECT_SUMMARY_REPORT:
                SpringContext.getBean(BudgetConstructionObjectSummaryReportService.class).updateObjectSummaryReport(principalId);
                reportData = SpringContext.getBean(BudgetConstructionObjectSummaryReportService.class).buildReports(universityFiscalYear, principalId);
                break;
            case ACCOUNT_OBJECT_DETAIL_REPORT:
                SpringContext.getBean(BudgetConstructionAccountObjectDetailReportService.class).updateAccountObjectDetailReport(principalId, runConsolidated);
                reportData = SpringContext.getBean(BudgetConstructionAccountObjectDetailReportService.class).buildReports(universityFiscalYear, principalId, runConsolidated);
                break;
            case ACCOUNT_FUNDING_DETAIL_REPORT:
                SpringContext.getBean(BudgetConstructionAccountFundingDetailReportService.class).updateAccountFundingDetailTable(principalId);
                reportData = SpringContext.getBean(BudgetConstructionAccountFundingDetailReportService.class).buildReports(universityFiscalYear, principalId);
                break;
            case MONTH_SUMMARY_REPORT:
                SpringContext.getBean(BudgetConstructionMonthSummaryReportService.class).updateMonthSummaryReport(principalId, runConsolidated);
                reportData = SpringContext.getBean(BudgetConstructionMonthSummaryReportService.class).buildReports(universityFiscalYear, principalId, runConsolidated);
                break;
            case POSITION_FUNDING_DETAIL_REPORT:
                SpringContext.getBean(BudgetConstructionPositionFundingDetailReportService.class).updatePositionFundingDetailReport(principalId, budgetConstructionReportThresholdSettings);
                reportData = SpringContext.getBean(BudgetConstructionPositionFundingDetailReportService.class).buildReports(universityFiscalYear, principalId);
                break;
            case SALARY_SUMMARY_REPORT:
                SpringContext.getBean(BudgetConstructionSalarySummaryReportService.class).updateSalarySummaryReport(principalId, universityFiscalYear, budgetConstructionReportThresholdSettings);
                reportData = SpringContext.getBean(BudgetConstructionSalarySummaryReportService.class).buildReports(universityFiscalYear, principalId, budgetConstructionReportThresholdSettings);
                break;
            case SALARY_STATISTICS_REPORT:
                SpringContext.getBean(BudgetConstructionSalaryStatisticsReportService.class).updateSalaryStatisticsReport(principalId, universityFiscalYear);
                reportData = SpringContext.getBean(BudgetConstructionSalaryStatisticsReportService.class).buildReports(universityFiscalYear, principalId);
                break;
            case REASON_SUMMARY_REPORT:
                SpringContext.getBean(BudgetConstructionReasonSummaryReportService.class).updateReasonSummaryReport(principalId, universityFiscalYear, budgetConstructionReportThresholdSettings);
                reportData = SpringContext.getBean(BudgetConstructionReasonSummaryReportService.class).buildReports(universityFiscalYear, principalId, budgetConstructionReportThresholdSettings);
                break;
            case REASON_STATISTICS_REPORT:
                SpringContext.getBean(BudgetConstructionReasonStatisticsReportService.class).updateReasonStatisticsReport(principalId, universityFiscalYear, budgetConstructionReportThresholdSettings);
                reportData = SpringContext.getBean(BudgetConstructionReasonStatisticsReportService.class).buildReports(universityFiscalYear, principalId, budgetConstructionReportThresholdSettings);
                break;

            case TWOPLG_LIST_REPORT:
                SpringContext.getBean(BudgetConstructionList2PLGReportService.class).updateList2PLGReport(principalId, universityFiscalYear);
                reportData = SpringContext.getBean(BudgetConstructionList2PLGReportService.class).buildReports(universityFiscalYear, principalId);
                break;

            case SYNCHRONIZATION_PROBLEMS_REPORT:
                SpringContext.getBean(BudgetConstructionSynchronizationProblemsReportService.class).updateSynchronizationProblemsReport(principalId);
                reportData = SpringContext.getBean(BudgetConstructionSynchronizationProblemsReportService.class).buildReports(universityFiscalYear, principalId);
                break;

        }

        return reportData;
    }

    /**
     * Builds URL for the report dump url.
     */
    protected String buildReportExportForwardURL(OrganizationReportSelectionForm organizationReportSelectionForm, ActionMapping mapping) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(BCConstants.Report.REPORT_MODE, organizationReportSelectionForm.getReportMode());
        parameters.put(BCConstants.IS_ORG_REPORT_REQUEST_PARAMETER, "true");

        return BudgetUrlUtil.buildBudgetUrl(mapping, organizationReportSelectionForm, BCConstants.REPORT_EXPORT_ACTION, parameters);
    }

    /**
     * Selects all sub-fund codes.
     */
    public ActionForward selectAllSubFunds(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        for (BudgetConstructionSubFundPick budgetConstructionSubFundPick : organizationReportSelectionForm.getSubFundPickList()) {
            budgetConstructionSubFundPick.setReportFlag(new Integer(1));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Selects all object codes.
     */
    public ActionForward selectAllObjectCodes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        for (BudgetConstructionObjectPick budgetConstructionObjectPick : organizationReportSelectionForm.getObjectCodePickList()) {
            budgetConstructionObjectPick.setSelectFlag(new Integer(1));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Selects all reason codes.
     */
    public ActionForward selectAllReasonCodes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        for (BudgetConstructionReasonCodePick budgetConstructionReasonCodePick : organizationReportSelectionForm.getReasonCodePickList()) {
            budgetConstructionReasonCodePick.setSelectFlag(new Integer(1));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Unselects all sub-fund codes.
     */
    public ActionForward unselectAllSubFunds(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        for (BudgetConstructionSubFundPick budgetConstructionSubFundPick : organizationReportSelectionForm.getSubFundPickList()) {
            budgetConstructionSubFundPick.setReportFlag(new Integer(0));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * unselects all object codes.
     */
    public ActionForward unselectAllObjectCodes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        for (BudgetConstructionObjectPick budgetConstructionObjectPick : organizationReportSelectionForm.getObjectCodePickList()) {
            budgetConstructionObjectPick.setSelectFlag(new Integer(0));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Unselects all reason codes.
     */
    public ActionForward unselectAllReasonCodes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationReportSelectionForm organizationReportSelectionForm = (OrganizationReportSelectionForm) form;
        for (BudgetConstructionReasonCodePick budgetConstructionReasonCodePick : organizationReportSelectionForm.getReasonCodePickList()) {
            budgetConstructionReasonCodePick.setSelectFlag(new Integer(0));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Checks that at least one sub fund is selected and stores the selection settings. If no sub fund is selected, an error message
     * is displayed to the user.
     * 
     * @param subFundPickList - List of BudgetConstructionSubFundPick objects to check
     * @return boolean - true if there was a selection and the list was saved, otherwise false
     */
    protected boolean storedSelectedSubFunds(List<BudgetConstructionSubFundPick> subFundPickList) {
        boolean foundSelected = false;

        // check to see if at least one pullflag is set and store the reportflag settings for currently displayed set of sub-funds
        for (BudgetConstructionSubFundPick budgetConstructionSubFundPick : subFundPickList) {
            if (budgetConstructionSubFundPick.getReportFlag() > 0) {
                foundSelected = true;
            }
        }

        // if selection was found, save the sub-fund selections, otherwise build error message
        if (foundSelected) {
            SpringContext.getBean(BudgetReportsControlListService.class).updateSubFundSelectFlags(subFundPickList);
        }
        else {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_SUBFUND_NOT_SELECTED);
        }

        return foundSelected;
    }

    /**
     * Checks that at least one object code is selected and stores the selection settings. If no object code is selected, an error
     * message is displayed to the user.
     * 
     * @param objectCodePickList - List of BudgetConstructionObjectPick objects to check
     * @return boolean - true if there was a selection and the list was saved, otherwise false
     */
    protected boolean storedSelectedObjectCodes(List<BudgetConstructionObjectPick> objectCodePickList) {
        boolean foundSelected = false;

        // check to see if at least one pullflag is set and store the selectFlag settings for currently displayed set of object
        // codes
        for (BudgetConstructionObjectPick budgetConstructionObjectPick : objectCodePickList) {
            if (budgetConstructionObjectPick.getSelectFlag() > 0) {
                foundSelected = true;
            }
        }

        // if selection was found, save the object code selections, otherwise build error message
        if (foundSelected) {
            SpringContext.getBean(BudgetReportsControlListService.class).updateObjectCodeSelectFlags(objectCodePickList);
        }
        else {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_OBJECT_CODE_NOT_SELECTED);
        }

        return foundSelected;
    }

    /**
     * Checks that at least one reason code is selected and stores the selection settings. If no reason code is selected, an error
     * message is displayed to the user.
     * 
     * @param reasonCodePickList - List of BudgetConstructionReasonCodePick objects to check
     * @return boolean - true if there was a selection and the list was saved, otherwise false
     */
    protected boolean storedSelectedReasonCodes(List<BudgetConstructionReasonCodePick> reasonCodePickList) {
        boolean foundSelected = false;

        // check to see if at least one pullflag is set and store the selectFlag settings for currently displayed set of reason
        // codes
        for (BudgetConstructionReasonCodePick budgetConstructionReasonCodePick : reasonCodePickList) {
            if (budgetConstructionReasonCodePick.getSelectFlag() > 0) {
                foundSelected = true;
            }
        }

        // if selection was found, save the reason code selections, otherwise build error message
        if (foundSelected) {
            SpringContext.getBean(BudgetReportsControlListService.class).updateReasonCodeSelectFlags(reasonCodePickList);
        }
        else {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_REASON_CODE_NOT_SELECTED);
        }

        return foundSelected;
    }

    /**
     * When apply threshold is checked, and displays error if no percent change threshold value is set.  
     * 
     * @param thresholdSettings
     * @return
     */
    protected boolean validThresholdSettings(BudgetConstructionReportThresholdSettings thresholdSettings){
        Boolean thresholdSettingsValid = true;
        if (thresholdSettings.isUseThreshold()){
            if (thresholdSettings.getThresholdPercent() == null){
                thresholdSettingsValid = false;
                GlobalVariables.getMessageMap().putError(BCPropertyConstants.BUDGET_CONSTRUCTION_REPORT_THRESHOLD_SETTINGS+"."+BCPropertyConstants.THRESHOLD_PERCENT, BCKeyConstants.ERROR_BUDGET_THRESHOLD_PERCENT_NEEDED);
            }
        }
        return thresholdSettingsValid;
    }
}
