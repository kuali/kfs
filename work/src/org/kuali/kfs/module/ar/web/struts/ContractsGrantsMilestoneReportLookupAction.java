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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsMilestoneReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsMilestoneReportService;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;

/**
 * Action class for Contracts Grants Milestone Report Lookup.
 */
public class ContractsGrantsMilestoneReportLookupAction extends ContractsGrantsReportLookupAction {
    /**
     * implements the print service for Contracts Grants Milestone Report Lookup.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsMilestoneReportLookupForm milestoneReportLookupForm = (ContractsGrantsMilestoneReportLookupForm) form;

        List<ContractsGrantsMilestoneReport> displayList = lookupReportValues(milestoneReportLookupForm, request, true);
        final String sortPropertyName = sortReportValues(displayList, "ContractsGrantsMilestoneReport");

        ContractsGrantsReportDataHolder cgMilestoneReportDataHolder = getContractsGrantsReportDataBuilderService(ContractsGrantsMilestoneReport.class).buildReportDataHolder(displayList, sortPropertyName);
        buildReportForSearchCriteria(cgMilestoneReportDataHolder.getSearchCriteria(), milestoneReportLookupForm.getFieldsForLookup(), ContractsGrantsMilestoneReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = SpringContext.getBean(ContractsGrantsMilestoneReportService.class).generateReport(cgMilestoneReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }
}
