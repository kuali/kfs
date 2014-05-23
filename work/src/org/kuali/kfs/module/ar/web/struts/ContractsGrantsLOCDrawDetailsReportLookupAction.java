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
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCDrawDetailsReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * Action Class for the Contracts Grants LOC Draw Details Report Lookup.
 */
public class ContractsGrantsLOCDrawDetailsReportLookupAction extends ContractsGrantsReportLookupAction {
    /**
     * This method implements the print functionality for the Contracts Grants LOC Draw Details Report Lookup.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsLOCDrawDetailsReportLookupForm cgLOCDrawDetailsReportLookupForm = (ContractsGrantsLOCDrawDetailsReportLookupForm) form;

        List<ContractsGrantsLOCDrawDetailsReport> displayList = lookupReportValues(cgLOCDrawDetailsReportLookupForm, request, true);
        final String sortPropertyName = sortReportValues(displayList, "ContractsGrantsLOCDrawDetailsReport");

        ContractsGrantsReportDataHolder cgLOCDrawDetailsReportDataHolder = getContractsGrantsReportDataBuilderService().buildReportDataHolder(displayList, sortPropertyName);

        // build search criteria for report
        buildReportForSearchCriteria(cgLOCDrawDetailsReportDataHolder.getSearchCriteria(), cgLOCDrawDetailsReportLookupForm.getFieldsForLookup(), ContractsGrantsLOCDrawDetailsReport.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String reportFileName = generateReportPdf(cgLOCDrawDetailsReportDataHolder, baos);
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.PDF_MIME_TYPE, baos, reportFileName + ReportGeneration.PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * This report does not have a title
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#generateReportTitle(org.kuali.rice.kns.web.struts.form.LookupForm)
     */
    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        return null;
    }

    /**
     * Returns "contractsGrantsLOCDrawDetailsReportBuilderService"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.CONTRACTS_GRANTS_LOC_DRAW_DETAILS;
    }
}
