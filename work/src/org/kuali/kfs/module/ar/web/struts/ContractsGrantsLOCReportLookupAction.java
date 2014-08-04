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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCReport;
import org.kuali.kfs.sys.report.ReportInfoHolder;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Action Class for the Contracts Grants LOC Draw Details Report Lookup.
 */
public class ContractsGrantsLOCReportLookupAction extends ContractsGrantsReportLookupAction {
    /**
     * This report does not have a title
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#generateReportTitle(org.kuali.rice.kns.web.struts.form.LookupForm)
     */
    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        return null;
    }

    /**
     * Returns "contractsGrantsLOCReportBuilderService"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.CONTRACTS_GRANTS_LOC;
    }

    /**
     * Returns the sort field for this report's pdf generation, "ContractsGrantsLOCReport"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getSortFieldName()
     */
    @Override
    protected String getSortFieldName() {
        return ArConstants.CONTRACTS_GRANTS_LOC_REPORT;
    }

    /**
     * Returns the class for ContractsGrantsLOCReport
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getPrintSearchCriteriaClass()
     */
    @Override
    public Class<? extends BusinessObject> getPrintSearchCriteriaClass() {
        return ContractsGrantsLOCReport.class;
    }

    @Override
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String reportTitle = "Letter of Credit Draw Report";
        LookupForm lookupForm = (LookupForm)form;
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        String reportType = (String)lookupFormFields.remove("reportType");

        if (reportType.equals(ArConstants.LOCReportTypeFieldValues.AMOUNTS_NOT_DRAWN)) {
            reportTitle = "Letter of Credit Amounts Not Drawn Report";
        } else if (reportType.equals(ArConstants.LOCReportTypeFieldValues.DRAW_DETAILS) ) {
            reportTitle = "Letter of Credit Detail Report";
        }

        ((ReportInfoHolder)this.getContractsGrantsReportDataBuilderService().getReportInfo()).setReportTitle(reportTitle);

        return super.print(mapping, form, request, response);
    }



}