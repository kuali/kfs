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
import org.kuali.kfs.module.endow.report.util.ReportRequestHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionStatementReportDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionStatementReportPrint;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;

public class TransactionSummaryAction extends EndowmentReportBaseAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionSummaryAction.class);
    
    private final String REPORT_NAME = "Transaction Summary";
    
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
        String message = transactionSummaryForm.getMessage();

        
        // No report was generated
        transactionSummaryForm.setMessage("Report was not generated");
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }
    
}
