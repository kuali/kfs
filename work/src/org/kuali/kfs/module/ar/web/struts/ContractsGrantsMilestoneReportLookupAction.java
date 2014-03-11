/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsMilestoneReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsMilestoneReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsMilestoneReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action class for Contracts Grants Milestone Report Lookup.
 */
public class ContractsGrantsMilestoneReportLookupAction extends ContractsGrantsReportLookupAction {

    /**
     * implements the print service for Contracts Grants Milestone Report Lookup.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContractsGrantsMilestoneReportLookupForm milestoneReportLookupForm = (ContractsGrantsMilestoneReportLookupForm) form;

        String methodToCall = findMethodToCall(form, request);
        if (methodToCall.equalsIgnoreCase("search")) {
            GlobalVariables.getUserSession().removeObjectsByPrefix(KRADConstants.SEARCH_METHOD);
        }

        Lookupable kualiLookupable = milestoneReportLookupForm.getLookupable();
        if (kualiLookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        List<ContractsGrantsMilestoneReport> displayList = new ArrayList<ContractsGrantsMilestoneReport>();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(milestoneReportLookupForm.getFields());

        // this is for 200 limit. turn it off for report.
        boolean bounded = false;

        displayList = (List<ContractsGrantsMilestoneReport>) kualiLookupable.performLookup(milestoneReportLookupForm, resultTable, bounded);

        Object sortIndexObject = GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY);

        if (ObjectUtils.isNull(sortIndexObject) || sortIndexObject.toString() == "0") {
            sortIndexObject = "0";
        }
        // get sort property
        String sortPropertyName = getFieldNameForSorting(Integer.parseInt(sortIndexObject.toString()), "ContractsGrantsMilestoneReport");

        // sort list
        sortReport(displayList, sortPropertyName);

        // build report
        ContractsGrantsReportDataHolder cgMilestoneReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsMilestoneReportDetailDataHolder> details = cgMilestoneReportDataHolder.getDetails();
        for (ContractsGrantsMilestoneReport cgMilestoneReport : displayList) {


            ContractsGrantsMilestoneReportDetailDataHolder reportDetail = new ContractsGrantsMilestoneReportDetailDataHolder();
            // set report data
            setReportDate(cgMilestoneReport, reportDetail);

            reportDetail.setDisplaySubtotalInd(false);

            details.add(reportDetail);
        }
        buildReportForSearchCriteia(cgMilestoneReportDataHolder.getSearchCriteria(), milestoneReportLookupForm.getFieldsForLookup(), ContractsGrantsMilestoneReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsMilestoneReportService.class).generateReport(cgMilestoneReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * @param cgInvoiceReportEntry
     * @param reportDetail
     */
    private void setReportDate(ContractsGrantsMilestoneReport cgInvoiceReportEntry, ContractsGrantsMilestoneReportDetailDataHolder reportDetail) {
        reportDetail.setProposalNumber(cgInvoiceReportEntry.getProposalNumber());
        reportDetail.setAccountNumber(cgInvoiceReportEntry.getAccountNumber());
        reportDetail.setMilestoneNumber(cgInvoiceReportEntry.getMilestoneNumber());
        reportDetail.setMilestoneExpectedCompletionDate(cgInvoiceReportEntry.getMilestoneExpectedCompletionDate());

        BigDecimal milestoneAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getMilestoneAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getMilestoneAmount().bigDecimalValue();
        reportDetail.setMilestoneAmount(milestoneAmount);
        reportDetail.setIsItBilled(cgInvoiceReportEntry.getIsItBilled());
        if (cgInvoiceReportEntry.isActive()) {
            reportDetail.setActive(KFSConstants.ParameterValues.YES);
        } else {
            reportDetail.setActive(KFSConstants.ParameterValues.NO);
        }

    }
}
