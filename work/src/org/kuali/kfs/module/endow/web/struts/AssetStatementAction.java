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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.report.util.AssetStatementReportDataHolder;
import org.kuali.kfs.module.endow.report.util.ReportRequestHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.TrialBalanceReportPrint;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;

public class AssetStatementAction extends EndowmentReportBaseAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetStatementAction.class);
    
    private final String ENDOWMENT_REPORT_NAME = "Endowment Asset Statement";
    private final String NON_ENDOWED_REPORT_NAME = "Non-Endowed Asset Statement";
    
    public AssetStatementAction() {
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
        AssetStatementForm transactionStatementForm = (AssetStatementForm) form;
        transactionStatementForm.clear();
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
     * Generates Transaction Statement in the PDF form
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // get all the value strings from the form
        AssetStatementForm assetStatementForm = (AssetStatementForm) form;
        String kemids = assetStatementForm.getKemid();
        String benefittingOrganziationCampuses = assetStatementForm.getBenefittingOrganziationCampus();
        String benefittingOrganziationCharts = assetStatementForm.getBenefittingOrganziationChart();
        String benefittingOrganziations = assetStatementForm.getBenefittingOrganziation();
        String typeCodes = assetStatementForm.getTypeCode();
        String purposeCodes = assetStatementForm.getPurposeCode();
        String combineGroupCodes = assetStatementForm.getCombineGroupCode();
        String monthEndDate = assetStatementForm.getMonthEndDate();
        String endowmentOption = assetStatementForm.getEndowmentOption();
        String reportOption = assetStatementForm.getReportOption();
        String listKemidsOnHeader = assetStatementForm.getListKemidsInHeader();
        String message = assetStatementForm.getMessage();

        List<AssetStatementReportDataHolder> endowmentAsetStatementReportDataHolders = null;
        List<AssetStatementReportDataHolder> nonEndowedAssetStatementReportDataHolders = null;
        
        /*
         * Creates the report data based on the selected criteria.
         * The criteria are selected as follows.
         * 1. Kemid and the other criteria cannot be selected at the same time.
         * 2. If none of them are selected, all kemids will be selected.
         * 3. The other criteria other than kemid are "OR" combined.
         * 4. All the criteria in the text input can be multiple by the use of wild card or the separator ('&' for kemid, ',' for the others) 
         * 5. Beginning Date and Ending Date are required.
         */

        /*
        AssetStatementReportService assetStatementReportService = SpringContext.getBean(AssetStatementReportService.class);
        
        if (StringUtils.isNotBlank(kemids)) {
            
            if (( StringUtils.isNotBlank(benefittingOrganziationCampuses) 
                || StringUtils.isNotBlank(benefittingOrganziationCharts)
                || StringUtils.isNotBlank(benefittingOrganziations)
                || StringUtils.isNotBlank(typeCodes)
                || StringUtils.isNotBlank(purposeCodes) 
                || StringUtils.isNotBlank(combineGroupCodes) )) {
            
                // kemid and the other criteria cannot be selected at the same time 
                assetStatementForm.setMessage(ERROR_REPORT_KEMID_WITH_OTHER_CRITERIA);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
                
            } else {
                // by kemid only
                List<String> kemidList = parseValueString(kemids, KEMID_SEPERATOR);                
                if ("Y".equalsIgnoreCase(endowmentOption)) {
                    endowmentAsetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByKemidByIds(kemidList, monthEndDate, endowmentOption);
                } else if ("N".equalsIgnoreCase(endowmentOption)) {
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByKemidByIds(kemidList, monthEndDate, endowmentOption);
                } else {
                    endowmentAsetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByKemidByIds(kemidList, monthEndDate, endowmentOption);
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByKemidByIds(kemidList, monthEndDate, endowmentOption);
                }
            }
        } else {
            if (( StringUtils.isBlank(benefittingOrganziationCampuses) 
                    && StringUtils.isBlank(benefittingOrganziationCharts)
                    && StringUtils.isBlank(benefittingOrganziations)
                    && StringUtils.isBlank(typeCodes)
                    && StringUtils.isBlank(purposeCodes) 
                    && StringUtils.isBlank(combineGroupCodes) )) {

                // for all kemids
                if ("Y".equalsIgnoreCase(endowmentOption)) {
                    endowmentAsetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportForAllKemids(monthEndDate, endowmentOption);
                } else if ("N".equalsIgnoreCase(endowmentOption)) {
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportForAllKemids(monthEndDate, endowmentOption);
                } else {
                    endowmentAsetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportForAllKemids(monthEndDate, endowmentOption);
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportForAllKemids(monthEndDate, endowmentOption);
                }
                
            } else {
                // by other criteria
                if ("Y".equalsIgnoreCase(endowmentOption)) {
                    endowmentAsetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByOtherCriteria(
                        parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                        monthEndDate,
                        endowmentOption);
                } else if ("N".equalsIgnoreCase(endowmentOption)) {
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByOtherCriteria(
                            parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                            monthEndDate,
                            endowmentOption);
                } else {
                    endowmentAsetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByOtherCriteria(
                            parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                            monthEndDate,
                            endowmentOption);
                    
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByOtherCriteria(
                            parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                            monthEndDate,
                            endowmentOption);
                }
                
            }
        }
            
        // See to see if you have something to print
        if (endowmentAsetStatementReportDataHolders != null || nonEndowedAssetStatementReportDataHolders != null) {
            
            ServletOutputStream outStream = response.getOutputStream();
            response.setContentType("application/pdf");
            double streamSize = 0;
        
            if (endowmentAsetStatementReportDataHolders != null) {
                
                // prepare the header sheet data
                ReportRequestHeaderDataHolder reportRequestHeaderDataHolder = assetStatementReportService.createReportHeaderSheetData(
                        getKemidsSelected(endowmentAsetStatementReportDataHolders),
                        parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR),
                        ENDOWMENT_REPORT_NAME,
                        endowmentOption);
                
                // generate the report in PDF 
                ByteArrayOutputStream pdfStream = new AsetStatementReportPrint().printAssetStatementReport(reportRequestHeaderDataHolder, endowmentAsetStatementReportDataHolders);                
                if (pdfStream != null) {
                    streamSize = pdfStream.size();
                    pdfStream.writeTo(outStream);
                }
            }   
            if (nonEndowedAssetStatementReportDataHolders != null) {
                
                // prepare the header sheet data
                ReportRequestHeaderDataHolder reportRequestHeaderDataHolder = assetStatementReportService.createReportHeaderSheetData(
                        getKemidsSelected(endowmentAsetStatementReportDataHolders),
                        parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR),
                        NON_ENDOWED_REPORT_NAME,
                        endowmentOption);
                
                // generate the report in PDF 
                ByteArrayOutputStream pdfStream = new AsetStatementReportPrint().printAssetStatementReport(reportRequestHeaderDataHolder, nonEndowedAssetStatementReportDataHolders);                
                if (pdfStream != null) {
                    streamSize = pdfStream.size();
                    pdfStream.writeTo(outStream);
                }
            }  
            outStream.flush();
            return null;
        }       
*/        
        // No report was generated
        assetStatementForm.setMessage("Report was not generated");
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }
    
    /**
     * Retrieves all the kemids used for the report 
     * 
     * @param transactionStatementReportDataHolder
     * @return
     */
    protected List<String> getKemidsSelected(List<AssetStatementReportDataHolder> transactionStatementReportDataHolder) {
        
        List<String> kemids = new ArrayList<String>();
        for (AssetStatementReportDataHolder dataHolder : transactionStatementReportDataHolder) {
            kemids.add(dataHolder.getKemid());
        }
        
        return kemids;        
    }
}
