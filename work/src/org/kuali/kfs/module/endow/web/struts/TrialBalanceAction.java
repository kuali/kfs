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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.report.service.TrialBalanceReportService;
import org.kuali.kfs.module.endow.report.util.EndowmentReportHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.TrialBalanceReportDataHolder;
import org.kuali.kfs.module.endow.report.util.TrialBalanceReportPrint;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;

public class TrialBalanceAction extends EndowmentReportBaseAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceAction.class);
    
    private final String REPORT_NAME = "Trial Balance";
    private final String REPORT_FILE_NAME = "TrialBalanceReport.pdf";
    
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
        
        // get all the value strings from the form
        TrialBalanceForm trialBalanceForm = (TrialBalanceForm) form;
        String kemids = trialBalanceForm.getKemid();
        String benefittingOrganziationCampuses = trialBalanceForm.getBenefittingOrganziationCampus();
        String benefittingOrganziationCharts = trialBalanceForm.getBenefittingOrganziationChart();
        String benefittingOrganziations = trialBalanceForm.getBenefittingOrganziation();
        String typeCodes = trialBalanceForm.getTypeCode();
        String purposeCodes = trialBalanceForm.getPurposeCode();
        String combineGroupCodes = trialBalanceForm.getCombineGroupCode();
        String asOfDate = trialBalanceForm.getAsOfDate();
        String endowmentOption = trialBalanceForm.getEndowmentOption();
        String listKemidsInHeader = trialBalanceForm.getListKemidsInHeader();
        String closedIndicator = trialBalanceForm.getClosedIndicator();
        String message = trialBalanceForm.getMessage();

        List<TrialBalanceReportDataHolder> trialBalanceReportDataHolders = null;
        
        /*
         * Creates the report data based on the selected criteria.
         * The criteria are selected as follows.
         * 1. Kemid and the other criteria cannot be selected at the same time.
         * 2. If none of them are selected, all kemids will be selected.
         * 3. The other criteria other than kemid are AND-combined.
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
                trialBalanceForm.setMessage(ERROR_REPORT_KEMID_WITH_OTHER_CRITERIA);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
                
            } else {
                // by kemid only
                List<String> kemidList = parseValueString(kemids, KEMID_SEPERATOR);                
                trialBalanceReportDataHolders = trialBalanceReportService.getTrialBalanceReportsByKemidByIds(kemidList, endowmentOption, closedIndicator);
            }
        } else {
            if (( StringUtils.isBlank(benefittingOrganziationCampuses) 
                    && StringUtils.isBlank(benefittingOrganziationCharts)
                    && StringUtils.isBlank(benefittingOrganziations)
                    && StringUtils.isBlank(typeCodes)
                    && StringUtils.isBlank(purposeCodes) 
                    && StringUtils.isBlank(combineGroupCodes) )) {

                // for all kemids
                trialBalanceReportDataHolders = trialBalanceReportService.getTrialBalanceReportForAllKemids(endowmentOption, closedIndicator);
                
            } else {
                // by other criteria
                trialBalanceReportDataHolders = trialBalanceReportService.getTrialBalanceReportsByOtherCriteria(
                    parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                    parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                    endowmentOption,
                    closedIndicator);
            }
        }
               
        // Check to see if you have something to print        
        if (trialBalanceReportDataHolders != null && !trialBalanceReportDataHolders.isEmpty()) {
  
            // prepare the header sheet data
            EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder = trialBalanceReportService.createReportHeaderSheetData(
                    getKemidsSelected(trialBalanceReportDataHolders),
                    parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR),
                    parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR),
                    parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR),
                    parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR),
                    parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR),
                    parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR),
                    REPORT_NAME,
                    endowmentOption,
                    null);
            
            // generate the report in PDF 
            ByteArrayOutputStream pdfStream = new TrialBalanceReportPrint().printTrialBalanceReport(reportRequestHeaderDataHolder, trialBalanceReportDataHolders, listKemidsInHeader);            
            if (pdfStream != null) {
                trialBalanceForm.setMessage("Reports Generated");
                WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", pdfStream, REPORT_FILE_NAME);    
                return null;
            }
        }
        
        // No report was generated
        if (StringUtils.isBlank(kemids)) { 
            trialBalanceForm.setMessage("Report was not generated.");
        } else {
            trialBalanceForm.setMessage("Report was not generated for " + kemids + ".");
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Retrieves all the kemids used for the report 
     * 
     * @param trialBalanceReportDataHolder
     * @return
     */
    protected List<String> getKemidsSelected(List<TrialBalanceReportDataHolder> trialBalanceReportDataHolder) {        
        List<String> kemids = new ArrayList<String>();
        for (TrialBalanceReportDataHolder dataHolder : trialBalanceReportDataHolder) {
            kemids.add(dataHolder.getKemid());
        }        
        return kemids;        
    }

}
