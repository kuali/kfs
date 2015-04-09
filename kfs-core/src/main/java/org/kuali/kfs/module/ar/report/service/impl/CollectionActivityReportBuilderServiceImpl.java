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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.report.CollectionActivityReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Service which builds a report object for the Collection Activity Report
 */
public class CollectionActivityReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<CollectionActivityReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (CollectionActivityReport collActReportEntry : (List<CollectionActivityReport>)displayList) {
            CollectionActivityReportDetailDataHolder reportDetail = new CollectionActivityReportDetailDataHolder();
            // set report data
            reportDetail = new CollectionActivityReportDetailDataHolder(collActReportEntry);
            details.add(reportDetail);
        }

        cgInvoiceReportDataHolder.setDetails(details);
        return cgInvoiceReportDataHolder;
    }

    /**
     * Return CollectionActivityReport's class
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return CollectionActivityReport.class;
    }

    @Override
    public ReportInfo getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(ReportInfo reportInfo) {
        this.reportInfo = reportInfo;
    }
}
