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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.report.service.TransactionStatementReportService;
import org.kuali.kfs.module.endow.report.util.EndowmentReportHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionStatementReportDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionStatementReportPrint;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;

public class TransactionStatementAction extends EndowmentReportBaseAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionStatementAction.class);
    
    private final String REPORT_NAME = "Transaction Statement";
    private final String REPORT_FILE_NAME = "TransactionStatementReport.pdf";
    
    public TransactionStatementAction() {
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
        TransactionStatementForm transactionStatementForm = (TransactionStatementForm)form;
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
        TransactionStatementForm transactionStatementForm = (TransactionStatementForm) form;
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
        
        TransactionStatementReportService transactionStatementReportService = SpringContext.getBean(TransactionStatementReportService.class);
        
        // get all the value strings from the form
        TransactionStatementForm transactionStatementForm = (TransactionStatementForm) form;
        String kemids = transactionStatementForm.getKemid();
        String benefittingOrganziationCampuses = transactionStatementForm.getBenefittingOrganziationCampus();
        String benefittingOrganziationCharts = transactionStatementForm.getBenefittingOrganziationChart();
        String benefittingOrganziations = transactionStatementForm.getBenefittingOrganziation();
        String typeCodes = transactionStatementForm.getTypeCode();
        String purposeCodes = transactionStatementForm.getPurposeCode();
        String combineGroupCodes = transactionStatementForm.getCombineGroupCode();
        String beginningDate = transactionStatementForm.getBeginningDate();
        String endingDate = transactionStatementForm.getEndingDate();
        String endowmentOption = transactionStatementForm.getEndowmentOption();
        String listKemidsInHeader = transactionStatementForm.getListKemidsInHeader();
        String closedIndicator = transactionStatementForm.getClosedIndicator();
        String message = transactionStatementForm.getMessage();

        List<TransactionStatementReportDataHolder> transactionStatementReportDataHolders = null;
        
        // check to see if the ending date is greater than the beginning date
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        try {
            java.util.Date beginDate = df.parse(beginningDate);
            java.util.Date endDate = df.parse(endingDate);

            if (beginDate.compareTo(endDate) >= 0) {
                transactionStatementForm.setMessage(ERROR_REPORT_ENDING_DATE_NOT_GREATER_THAN_BEGINNING_DATE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        } catch (ParseException e) {
            transactionStatementForm.setMessage(e.getMessage());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        /*
         * Creates the report data based on the selected criteria.
         * The criteria are selected as follows.
         * 1. Kemid and the other criteria cannot be selected at the same time.
         * 2. If none of them are selected, all kemids will be selected.
         * 3. The other criteria other than kemid are "OR" combined.
         * 4. All the criteria in the text input can be multiple by the use of wild card or the separator ('&' for kemid, ',' for the others) 
         * 5. Beginning Date and Ending Date are required.
         */
        
        if (StringUtils.isNotBlank(beginningDate) && StringUtils.isNotBlank(endingDate)) {
            if (StringUtils.isNotBlank(kemids)) {
                
                if (( StringUtils.isNotBlank(benefittingOrganziationCampuses) 
                    || StringUtils.isNotBlank(benefittingOrganziationCharts)
                    || StringUtils.isNotBlank(benefittingOrganziations)
                    || StringUtils.isNotBlank(typeCodes)
                    || StringUtils.isNotBlank(purposeCodes) 
                    || StringUtils.isNotBlank(combineGroupCodes) )) {
                
                    // kemid and the other criteria cannot be selected at the same time 
                    transactionStatementForm.setMessage(ERROR_REPORT_KEMID_WITH_OTHER_CRITERIA);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                    
                } else {
                    // by kemid only
                    List<String> kemidList = parseValueString(kemids, KEMID_SEPERATOR);                
                    transactionStatementReportDataHolders = transactionStatementReportService.getTransactionStatementReportsByKemidByIds(kemidList, beginningDate, endingDate, endowmentOption, closedIndicator);
                }
            } else {
                if (( StringUtils.isBlank(benefittingOrganziationCampuses) 
                        && StringUtils.isBlank(benefittingOrganziationCharts)
                        && StringUtils.isBlank(benefittingOrganziations)
                        && StringUtils.isBlank(typeCodes)
                        && StringUtils.isBlank(purposeCodes) 
                        && StringUtils.isBlank(combineGroupCodes) )) {
    
                    // for all kemids
                    transactionStatementReportDataHolders = transactionStatementReportService.getTransactionStatementReportForAllKemids(beginningDate, endingDate, endowmentOption, closedIndicator);
                    
                } else {
                    // by other criteria
                    transactionStatementReportDataHolders = transactionStatementReportService.getTransactionStatementReportsByOtherCriteria(
                        parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                        beginningDate,
                        endingDate,
                        endowmentOption,
                        closedIndicator);
                }
            }
        } else {
            transactionStatementForm.setMessage(ERROR_BOTH_BEGINNING_AND_ENDING_DATE_REQUIRED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);

        }
               
        // See to see if you have something to print        
        if (transactionStatementReportDataHolders != null && !transactionStatementReportDataHolders.isEmpty()) {
            // prepare the header sheet data
            EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder = transactionStatementReportService.createReportHeaderSheetData(
                    getKemidsSelected(transactionStatementReportDataHolders),
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
            ByteArrayOutputStream pdfStream = new TransactionStatementReportPrint().printTransactionStatementReport(reportRequestHeaderDataHolder, transactionStatementReportDataHolders, listKemidsInHeader);            
            if (pdfStream != null) {
                transactionStatementForm.setMessage("Reports Generated");
                WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", pdfStream, REPORT_FILE_NAME);
                return null;
            }
        }       
        
        // No report was generated
        if (StringUtils.isBlank(kemids)) { 
            transactionStatementForm.setMessage("Report was not generated.");
        } else {
            transactionStatementForm.setMessage("Report was not generated for " + kemids + ".");
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);        
    }
    
    /**
     * Retrieves all the kemids used for the report 
     * 
     * @param transactionStatementReportDataHolder
     * @return
     */
    protected List<String> getKemidsSelected(List<TransactionStatementReportDataHolder> transactionStatementReportDataHolder) {
        
        List<String> kemids = new ArrayList<String>();
        for (TransactionStatementReportDataHolder dataHolder : transactionStatementReportDataHolder) {
            kemids.add(dataHolder.getKemid());
        }
        
        return kemids;        
    }
    
}
