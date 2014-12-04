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

        String reportType = (String)lookupFormFields.get("reportType");

        if (reportType.equals(ArConstants.LOCReportTypeFieldValues.AMOUNTS_NOT_DRAWN)) {
            reportTitle = "Letter of Credit Amounts Not Drawn Report";
        } else if (reportType.equals(ArConstants.LOCReportTypeFieldValues.DRAW_DETAILS) ) {
            reportTitle = "Letter of Credit Detail Report";
        }

        ((ReportInfoHolder)getContractsGrantsReportDataBuilderService().getReportInfo()).setReportTitle(reportTitle);

        return super.print(mapping, form, request, response);
    }



}
