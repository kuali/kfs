/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.kuali.kfs.module.endow.report.service.TransactionSummaryReportService;
import org.kuali.kfs.module.endow.report.util.EndowmentReportHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportPrint;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;

public class TransactionSummaryAction extends EndowmentReportBaseAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionSummaryAction.class);
    
    private final String REPORT_NAME = "Transaction Summary";
    private final String REPORT_FILE_NAME = "TransactionSummaryReport.pdf";
    
    public TransactionSummaryAction() {
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
        TransactionSummaryForm transactionStatementForm = (TransactionSummaryForm) form;
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
        
        TransactionSummaryReportService transactionSummaryReportService = SpringContext.getBean(TransactionSummaryReportService.class);
        
        // get all the value strings from the form
        TransactionSummaryForm transactionSummaryForm = (TransactionSummaryForm) form;
        String kemids = transactionSummaryForm.getKemid();
        String benefittingOrganziationCampuses = transactionSummaryForm.getBenefittingOrganziationCampus();
        String benefittingOrganziationCharts = transactionSummaryForm.getBenefittingOrganziationChart();
        String benefittingOrganziations = transactionSummaryForm.getBenefittingOrganziation();
        String typeCodes = transactionSummaryForm.getTypeCode();
        String purposeCodes = transactionSummaryForm.getPurposeCode();
        String combineGroupCodes = transactionSummaryForm.getCombineGroupCode();
        String beginningDate = transactionSummaryForm.getBeginningDate();
        String endingDate = transactionSummaryForm.getEndingDate();
        String endowmentOption = transactionSummaryForm.getEndowmentOption();
        String reportOption = transactionSummaryForm.getReportOption();
        String listKemidsOnHeader = transactionSummaryForm.getListKemidsInHeader();
        String summaryTotalsOnly = transactionSummaryForm.getSummaryTotalsOnly();
        String closedIndicator = transactionSummaryForm.getClosedIndicator();
        String message = transactionSummaryForm.getMessage();

        // check to see if the ending date is greater than the beginning date
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        try {
            java.util.Date beginDate = df.parse(beginningDate);
            java.util.Date endDate = df.parse(endingDate);

            if (beginDate.compareTo(endDate) >= 0) {
                transactionSummaryForm.setMessage(ERROR_REPORT_ENDING_DATE_NOT_GREATER_THAN_BEGINNING_DATE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        } catch (ParseException e) {
            transactionSummaryForm.setMessage(e.getMessage());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        List<TransactionSummaryReportDataHolder> transactionSummaryReportList = null;

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
                    transactionSummaryForm.setMessage(ERROR_REPORT_KEMID_WITH_OTHER_CRITERIA);
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                    
                } else {
                    // by kemid only
                    List<String> kemidList = parseValueString(kemids, KEMID_SEPERATOR);                
                    transactionSummaryReportList = transactionSummaryReportService.getTransactionSummaryReportsByKemidByIds(kemidList, beginningDate, endingDate, endowmentOption, closedIndicator, reportOption);
                }
            } else {
                if (( StringUtils.isBlank(benefittingOrganziationCampuses) 
                        && StringUtils.isBlank(benefittingOrganziationCharts)
                        && StringUtils.isBlank(benefittingOrganziations)
                        && StringUtils.isBlank(typeCodes)
                        && StringUtils.isBlank(purposeCodes) 
                        && StringUtils.isBlank(combineGroupCodes) )) {
    
                    // for all kemids
                    transactionSummaryReportList = transactionSummaryReportService.getTransactionSummaryReportForAllKemids(beginningDate, endingDate, endowmentOption, closedIndicator, reportOption);
                    
                } else {
                    // by other criteria
                    transactionSummaryReportList = transactionSummaryReportService.getTransactionSummaryReportsByOtherCriteria(
                        parseValueString(benefittingOrganziationCampuses, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(benefittingOrganziationCharts, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(benefittingOrganziations, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(typeCodes, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(purposeCodes, OTHER_CRITERIA_SEPERATOR), 
                        parseValueString(combineGroupCodes, OTHER_CRITERIA_SEPERATOR), 
                        beginningDate,
                        endingDate,
                        endowmentOption,
                        closedIndicator, reportOption);
                }
            }
        } else {
            transactionSummaryForm.setMessage(ERROR_BOTH_BEGINNING_AND_ENDING_DATE_REQUIRED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
               
        // see if you have something to print        
        if (transactionSummaryReportList != null && !transactionSummaryReportList.isEmpty()) {
            // prepare the header sheet data
            EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder = transactionSummaryReportService.createReportHeaderSheetData(
                    getKemidsSelected(transactionSummaryReportList),
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
            ByteArrayOutputStream pdfStream = new TransactionSummaryReportPrint().printTransactionSummaryReport(reportRequestHeaderDataHolder, transactionSummaryReportList, listKemidsOnHeader, reportOption, summaryTotalsOnly);            
            if (pdfStream != null) {
                transactionSummaryForm.setMessage("Reports Generated");
                WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", pdfStream, REPORT_FILE_NAME);
                return null;
            }
        }       
        
        // No report was generated
        transactionSummaryForm.setMessage("Report was not generated for " + kemids + ".");
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }
    
    /**
     * Retrieves all the kemids used for the report 
     * 
     * @param transactionStatementReportDataHolder
     * @return
     */
    protected List<String> getKemidsSelected(List<TransactionSummaryReportDataHolder> transactionSummaryReportList) {
        
        List<String> kemids = new ArrayList<String>();
        for (TransactionSummaryReportDataHolder dataHolder : transactionSummaryReportList) {
            kemids.add(dataHolder.getKemid());
        }
        
        return kemids;        
    }
    
}
