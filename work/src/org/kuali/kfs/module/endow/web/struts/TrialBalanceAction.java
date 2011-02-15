/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.web.struts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.report.service.TrialBalanceReportService;
import org.kuali.kfs.module.endow.report.util.ReportRequestHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.TrialBalanceReportDataHolder;
import org.kuali.kfs.module.endow.report.util.TrialBalanceReportPrint;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class TrialBalanceAction extends KualiAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceAction.class);
    
    private final String REPORT_NAME = "Trial Balance";
    private final String ONLY_ENDOWMENT = "Endowmnet";
    private final String NON_ENDOWED = "Non-Endowed";
    private final String BOTH_ENDOWMENT_OPTION = "Both";
    
    private final char KEMID_SEPERATOR = '&';
    private final char OTHER_CRITERIA_SEPERATOR = ',';
    
    public TrialBalanceAction() {
        super();
    }

    /**
     * Directs to the start page
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm)form;
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears the form when the "clear" button is pressed
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm) form;
        trialBalanceForm.clear();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Cancels the current page and goes to the start page
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Generates Trial Balance in the PDF form
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        TrialBalanceReportService trialBalanceReportService = SpringContext.getBean(TrialBalanceReportService.class);
        
        // get all the values from the form
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm) form;
        String kemids = trialBalanceForm.getKemid();
        String benefittingOrganziationCampuses = trialBalanceForm.getBenefittingOrganziationCampus();
        String benefittingOrganziationCharts = trialBalanceForm.getBenefittingOrganziationChart();
        String benefittingOrganziations = trialBalanceForm.getBenefittingOrganziation();
        String typeCodes = trialBalanceForm.getTypeCode();
        String purposeCodes = trialBalanceForm.getPurposeCode();
        String combineGroupCodes = trialBalanceForm.getCombineGroupCode();
        String asOfDate = trialBalanceForm.getAsOfDate();
        String endowmnetOption = trialBalanceForm.getEndowmentOption();
        String message = trialBalanceForm.getMessage();

        List<TrialBalanceReportDataHolder> trialBalanceReports = null;
        
        /*
         * Creates the report data based on the selected criteria.
         * The criteria are selected as follows.
         * 1. Kemid and the other criteria cannot be selected at the same time.
         * 2. If none of them are selected, all kemids will be selected.
         * 3. The other criteria other than kemid are "OR" combined.
         * 4. All the criteria in the text input can be multiple by the use of wild card or the separator ('&' for kemid, ',' for the others) 
         */
        if (StringUtils.isNotBlank(kemids)) {
            
            if (( StringUtils.isNotBlank(benefittingOrganziationCampuses) 
                || StringUtils.isNotBlank(benefittingOrganziationCharts)
                || StringUtils.isNotBlank(benefittingOrganziations)
                || StringUtils.isNotBlank(typeCodes)
                || StringUtils.isNotBlank(purposeCodes) 
                || StringUtils.isNotBlank(combineGroupCodes) )) {
            
                // kemid and the other criteria cannot be selected at the same time 
                trialBalanceForm.setMessage("The use of the KEMID as a selection criterion casnnot be used in combination with any orther selection criteria");
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
                
            } else {
                // by kemid only
                List<String> kemidList = parseValueString(kemids, KEMID_SEPERATOR);                
                trialBalanceReports = trialBalanceReportService.getTrialBalanceReportsByKemidByIds(kemidList, endowmnetOption);
            }
        } else {
            if (( StringUtils.isBlank(benefittingOrganziationCampuses) 
                    && StringUtils.isBlank(benefittingOrganziationCharts)
                    && StringUtils.isBlank(benefittingOrganziations)
                    && StringUtils.isBlank(typeCodes)
                    && StringUtils.isBlank(purposeCodes) 
                    && StringUtils.isBlank(combineGroupCodes) )) {

                // for all kemids
                trialBalanceReports = trialBalanceReportService.getTrialBalanceReportForAllKemids(endowmnetOption);
                
            } else {
                // by other criteria
                trialBalanceReports = trialBalanceReportService.getTrialBalanceReportsByOtherCriteria(
                    parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                    endowmnetOption);
            }
        }
               
        // See if you have something to print        
        if (trialBalanceReports != null) {
            
            // There are something to print so prepare the header sheet data
            
            // get request report
            ReportRequestHeaderDataHolder reportRequestHeaderDataHolder = new ReportRequestHeaderDataHolder();    
            reportRequestHeaderDataHolder.setInstitutionName(trialBalanceReportService.getInstitutionName());
            reportRequestHeaderDataHolder.setReportRequested(REPORT_NAME);
            reportRequestHeaderDataHolder.setRequestedBy(trialBalanceReportService.getReportRequestor());
            String endowmentOptionDesc = "";
            if ("B".equalsIgnoreCase(endowmnetOption)) {
                endowmentOptionDesc = BOTH_ENDOWMENT_OPTION;
            } else {
                endowmentOptionDesc = "Y".equalsIgnoreCase(endowmnetOption) ? ONLY_ENDOWMENT : NON_ENDOWED;
            }
            reportRequestHeaderDataHolder.setEndowmentOption(endowmentOptionDesc);
            reportRequestHeaderDataHolder.setReportOption(""); 
            
            // get criteria
            reportRequestHeaderDataHolder.setBenefittingCampus(trialBalanceReportService.getBenefittingCampuses(parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setBenefittingChart(trialBalanceReportService.getBenefittingCharts(parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setBenefittingOrganization(trialBalanceReportService.getBenefittingOrganizations(parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setKemidTypeCode(trialBalanceReportService.getKemidTypeCodes(parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setKemidPurposeCode(trialBalanceReportService.getKemidPurposeCodes(parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setCombineGroupCode(trialBalanceReportService.getCombineGroupCodes(parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR)));
            
            // get kemids selected
            List<String> kemidsSelected = getKemidsSelected(trialBalanceReports);
            reportRequestHeaderDataHolder.setKemidsSelected(kemidsSelected);
    
            // get kemdis with multiple benefittting organizations
            reportRequestHeaderDataHolder.setKemidsWithMultipleBenefittingOrganizationsDataHolders(trialBalanceReportService.getKemidsWithMultipleBenefittingOrganizations(kemidsSelected));
            
            // generate the report in PDF 
            if (new TrialBalanceReportPrint().printTrialBalanceReport(reportRequestHeaderDataHolder, trialBalanceReports, response)) {
                // succeeded
                return null;
            }
        }
        
        // No report was generated
        trialBalanceForm.setMessage("Report was not generated");
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }
    
    /**
     * Retrieves all the kemids used for the report 
     * 
     * @param trialBalanceReports
     * @return
     */
    protected List<String> getKemidsSelected(List<TrialBalanceReportDataHolder> trialBalanceReports) {
        
        List<String> kemids = new ArrayList<String>();
        for (TrialBalanceReportDataHolder dataHolder : trialBalanceReports) {
            kemids.add(dataHolder.getKemid());
        }
        
        return kemids;        
    }
    
    /**
     * Parses the string value, which can include wild cards or separators
     * 
     * @param valueString
     * @param separater
     * @return
     */
    protected List<String> parseValueString(String valueString, char separater) {        
        
        List<String> values = null;        
        if (StringUtils.isNotBlank(valueString)) {
            values = Arrays.asList(StringUtils.split(valueString.trim(), separater));
        }        
        return values;
    }

}
