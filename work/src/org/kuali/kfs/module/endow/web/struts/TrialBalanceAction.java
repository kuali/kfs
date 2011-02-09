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
    private final char KEMID_SEPERATOR = '&';
    private final char OTHER_CRITERIA_SEPERATOR = ',';
    
    public TrialBalanceAction() {
        super();
    }

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm)form;
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm) form;
        trialBalanceForm.clear();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        TrialBalanceReportService trialBalanceReportService = SpringContext.getBean(TrialBalanceReportService.class);
        
        String basePath = getApplicationBaseUrl();
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm) form;
        String kemids = trialBalanceForm.getKemid();
        String benefittingOrganziationCampuses = trialBalanceForm.getBenefittingOrganziationCampus();
        String benefittingOrganziationCharts = trialBalanceForm.getBenefittingOrganziationChart();
        String benefittingOrganziations = trialBalanceForm.getBenefittingOrganziation();
        String typeCodes = trialBalanceForm.getTypeCode();
        String purposeCodes = trialBalanceForm.getPurposeCode();
        String combineGroupCodes = trialBalanceForm.getCombineGroupCode();
        //String asOfDate = trialBalanceForm.getAsOfDate();
        String endowmnetOption = trialBalanceForm.getEndowmentOption();
        String message = trialBalanceForm.getMessage();

        List<TrialBalanceReportDataHolder> trialBalanceReports = null;
        
        if (StringUtils.isNotBlank(kemids)) {
            
            if (( StringUtils.isNotBlank(benefittingOrganziationCampuses) 
                || StringUtils.isNotBlank(benefittingOrganziationCharts)
                || StringUtils.isNotBlank(benefittingOrganziations)
                || StringUtils.isNotBlank(typeCodes)
                || StringUtils.isNotBlank(purposeCodes) 
                || StringUtils.isNotBlank(combineGroupCodes) )) {
            
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
               
        // print reports
        
        if (trialBalanceReports != null) {
            ReportRequestHeaderDataHolder reportRequestHeaderDataHolder = new ReportRequestHeaderDataHolder();
    
            reportRequestHeaderDataHolder.setInstitutionName(trialBalanceReportService.getInstitutionName());
            reportRequestHeaderDataHolder.setReportRequested(REPORT_NAME);
            reportRequestHeaderDataHolder.setRequestedBy(trialBalanceReportService.getReportRequestor());
            String endowmentOptionDesc = "";
            if ("Y".equalsIgnoreCase(endowmnetOption)) endowmentOptionDesc = "Endowmnet";
            else if ("N".equalsIgnoreCase(endowmnetOption)) endowmentOptionDesc = "Non-Endowed";
            else if ("B".equalsIgnoreCase(endowmnetOption)) endowmentOptionDesc = "Both";
            reportRequestHeaderDataHolder.setEndowmentOption(endowmentOptionDesc);
            reportRequestHeaderDataHolder.setReportOption("Detail");  // check with Norm
            
            reportRequestHeaderDataHolder.setBenefittingCampus(trialBalanceReportService.getBenefittingCampuses(parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setBenefittingChart(trialBalanceReportService.getBenefittingCharts(parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setBenefittingOrganization(trialBalanceReportService.getBenefittingOrganizations(parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setKemidTypeCode(trialBalanceReportService.getKemidTypeCodes(parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setKemidPurposeCode(trialBalanceReportService.getKemidPurposeCodes(parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR)));
            reportRequestHeaderDataHolder.setCombineGroupCode(trialBalanceReportService.getCombineGroupCodes(parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR)));
            
            List<String> kemidsSelected = getKemidsSelected(trialBalanceReports);
            reportRequestHeaderDataHolder.setKemidsSelected(kemidsSelected);
    
            reportRequestHeaderDataHolder.setKemidsWithMultipleBenefittingOrganizationsDataHolders(trialBalanceReportService.getKemidsWithMultipleBenefittingOrganizations(kemidsSelected));
            
            if (new TrialBalanceReportPrint().printTrialBalanceReport(reportRequestHeaderDataHolder, trialBalanceReports, response)) {
                // succeeded
                return null;
            }
        }
        
        // No report was generated
        trialBalanceForm.setMessage("Report was not generated");
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }
    
    protected List<String> getKemidsSelected(List<TrialBalanceReportDataHolder> trialBalanceReports) {
        
        List<String> kemids = new ArrayList<String>();
        for (TrialBalanceReportDataHolder dataHolder : trialBalanceReports) {
            kemids.add(dataHolder.getKemid());
        }
        
        return kemids;        
    }
    
    protected List<String> parseValueString(String valueString, char separater) {        
        
        List<String> values = new ArrayList<String>();
        
        if (StringUtils.isNotBlank(valueString)) {
            values = Arrays.asList(StringUtils.split(valueString.trim(), separater));
        }
        
        return values;
    }

}
