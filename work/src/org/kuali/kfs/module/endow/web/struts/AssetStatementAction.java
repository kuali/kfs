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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.report.service.AssetStatementReportService;
import org.kuali.kfs.module.endow.report.util.AssetStatementReportDataHolder;
import org.kuali.kfs.module.endow.report.util.AssetStatementReportPrint;
import org.kuali.kfs.module.endow.report.util.EndowmentReportHeaderDataHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;

public class AssetStatementAction extends EndowmentReportBaseAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetStatementAction.class);
    
    private final String ENDOWMENT_REPORT_NAME = "Endowment Asset Statement";
    private final String NON_ENDOWED_REPORT_NAME = "Non-Endowed Asset Statement";
    private final String REPORT_FILE_NAME = "AssetStatementReport.pdf";
    
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
        String listKemidsInHeader = assetStatementForm.getListKemidsInHeader();
        String closedIndicator = assetStatementForm.getClosedIndicator();
        String printFileOption =  assetStatementForm.getPrintFileOption();   
        String message = assetStatementForm.getMessage();

        List<AssetStatementReportDataHolder> endowmentAssetStatementReportDataHolders = null;
        List<AssetStatementReportDataHolder> nonEndowedAssetStatementReportDataHolders = null;
        
        /*
         * Creates the report data based on the selected criteria.
         * The criteria are selected as follows.
         * 1. Kemid and the other criteria cannot be selected at the same time.
         * 2. If none of them are selected, all kemids will be selected.
         * 3. The other criteria other than kemid are "OR" combined.
         * 4. All the criteria in the text input can be multiple by the use of wild card or the separator ('&' for kemid, ',' for the others) 
         */

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
                    endowmentAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByKemidByIds(kemidList, monthEndDate, endowmentOption, reportOption, closedIndicator);
                } 
                else if ("N".equalsIgnoreCase(endowmentOption)) {
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByKemidByIds(kemidList, monthEndDate, endowmentOption, reportOption, closedIndicator);
                } 
                else {
                    endowmentAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByKemidByIds(kemidList, monthEndDate, "Y", reportOption, closedIndicator);
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByKemidByIds(kemidList, monthEndDate, "N", reportOption, closedIndicator);
                }
            }
        } 
        else {
            if (( StringUtils.isBlank(benefittingOrganziationCampuses) 
                    && StringUtils.isBlank(benefittingOrganziationCharts)
                    && StringUtils.isBlank(benefittingOrganziations)
                    && StringUtils.isBlank(typeCodes)
                    && StringUtils.isBlank(purposeCodes) 
                    && StringUtils.isBlank(combineGroupCodes) )) {

                // for all kemids
                if ("Y".equalsIgnoreCase(endowmentOption)) {
                    endowmentAssetStatementReportDataHolders =  assetStatementReportService.getAssetStatementReportForAllKemids(monthEndDate, endowmentOption, reportOption, closedIndicator);
                } else if ("N".equalsIgnoreCase(endowmentOption)) {
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportForAllKemids(monthEndDate, endowmentOption, reportOption, closedIndicator);
                } else {
                    endowmentAssetStatementReportDataHolders =  assetStatementReportService.getAssetStatementReportForAllKemids(monthEndDate, "Y", reportOption, closedIndicator);
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportForAllKemids(monthEndDate, "N", reportOption, closedIndicator);
                }
                
            } 
            else {
                // by other criteria
                if ("B".equalsIgnoreCase(endowmentOption)) {
                    endowmentAssetStatementReportDataHolders =  assetStatementReportService.getAssetStatementReportsByOtherCriteria(
                            parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                            monthEndDate,
                            "Y",
                            reportOption,
                            closedIndicator);
                    
                    nonEndowedAssetStatementReportDataHolders = assetStatementReportService.getAssetStatementReportsByOtherCriteria(
                            parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                            monthEndDate,
                            "N",
                            reportOption,
                            closedIndicator);
                } 
                else if ("Y".equalsIgnoreCase(endowmentOption)) {
                    // either endowment or non-endowed
                    endowmentAssetStatementReportDataHolders =  assetStatementReportService.getAssetStatementReportsByOtherCriteria(
                            parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                            monthEndDate,
                            endowmentOption,
                            reportOption, 
                            closedIndicator);
                }
                else if ("N".equalsIgnoreCase(endowmentOption)) {
                    // either endowment or non-endowed
                    nonEndowedAssetStatementReportDataHolders =  assetStatementReportService.getAssetStatementReportsByOtherCriteria(
                            parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                            parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                            monthEndDate,
                            endowmentOption,
                            reportOption, 
                            closedIndicator);
                }          
            }
        }
            
        // See to see if you have something to print
        if (endowmentAssetStatementReportDataHolders != null || nonEndowedAssetStatementReportDataHolders != null) {
            EndowmentReportHeaderDataHolder reportHeaderDataHolderForEndowment = null;
            EndowmentReportHeaderDataHolder reportHeaderDataHolderForNonEndowed = null;
            
            if (endowmentAssetStatementReportDataHolders != null) {
    
                // prepare the header sheet data          
                reportHeaderDataHolderForEndowment = assetStatementReportService.createReportHeaderSheetData(
                        getKemidsSelected(endowmentAssetStatementReportDataHolders),
                        parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR),
                        ENDOWMENT_REPORT_NAME,
                        endowmentOption,
                        reportOption);
            }
            if (nonEndowedAssetStatementReportDataHolders != null) {
    
                // prepare the header sheet data          
                reportHeaderDataHolderForNonEndowed = assetStatementReportService.createReportHeaderSheetData(
                        getKemidsSelected(nonEndowedAssetStatementReportDataHolders),
                        parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR),
                        parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR),
                        ENDOWMENT_REPORT_NAME,
                        endowmentOption,
                        reportOption);
            }
            // generate the report in one PDF file
            if (printFileOption.equalsIgnoreCase("Y")) {
                // consolidate all reports into one
                ByteArrayOutputStream pdfStream = null;
                pdfStream = new AssetStatementReportPrint().printAssetStatementReport(reportHeaderDataHolderForEndowment, reportHeaderDataHolderForNonEndowed, endowmentAssetStatementReportDataHolders, nonEndowedAssetStatementReportDataHolders, endowmentOption, reportOption, listKemidsInHeader);
                if (pdfStream != null) {
                    assetStatementForm.setMessage("Reports Generated");
                    WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", pdfStream, REPORT_FILE_NAME);
                }
            } 
            else {
                // zip all report files
            }
            
            return null;

        }       
        
        // No report was generated
        if (StringUtils.isBlank(kemids)) { 
            assetStatementForm.setMessage("Report was not generated.");
        } else {
            assetStatementForm.setMessage("Report was not generated for " + kemids + ".");
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);        
    }
    
    /**
     * Zip and output files that are not of type text/plain or text/html.
     * 
     * @param response
     * @param contentType
     * @param byteArrayOutputStreams
     * @param fileNames
     * @param zipFileName
     * @throws IOException
     */
    public static void saveMimeZipOutputStreamAsFile(HttpServletResponse response, String contentType, List<ByteArrayOutputStream> byteArrayOutputStreams, List<String> fileNames, String zipFileName) throws IOException {
    
        response.setContentType("application/zip");
        response.setHeader("Content-disposition", "attachment; filename=" + zipFileName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
    
        ZipOutputStream zout = new ZipOutputStream(response.getOutputStream());
        int i = 0;
        int totalSize = 0;
        for (ByteArrayOutputStream byteArrayOutputStream : byteArrayOutputStreams) {
            totalSize += byteArrayOutputStream.size();
            zout.putNextEntry(new ZipEntry(fileNames.get(i++)));
            zout.write(byteArrayOutputStream.toByteArray());
            zout.closeEntry();
        }
        response.setContentLength(totalSize);
        zout.flush();
        zout.close();        
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
