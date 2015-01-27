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

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsMilestoneReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsMilestoneReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * Implementation of ContractsGrantsReportDataBuilderService to help build the Contracts & Grants Milestone Report
 */
public class ContractsGrantsMilestoneReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
        ContractsGrantsReportDataHolder cgMilestoneReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsMilestoneReportDetailDataHolder> details = cgMilestoneReportDataHolder.getDetails();
        for (ContractsGrantsMilestoneReport cgMilestoneReport : (List<ContractsGrantsMilestoneReport>)displayList) {
            ContractsGrantsMilestoneReportDetailDataHolder reportDetail = new ContractsGrantsMilestoneReportDetailDataHolder();
            // set report data
            setReportDate(cgMilestoneReport, reportDetail);

            reportDetail.setDisplaySubtotal(false);

            details.add(reportDetail);
        }
        return cgMilestoneReportDataHolder;
    }

    /**
     * Returns the class of ContractsGrantsMilestoneReport
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return ContractsGrantsMilestoneReport.class;
    }

    /**
     * @param cgInvoiceReportEntry
     * @param reportDetail
     */
    protected void setReportDate(ContractsGrantsMilestoneReport cgInvoiceReportEntry, ContractsGrantsMilestoneReportDetailDataHolder reportDetail) {
        reportDetail.setProposalNumber(cgInvoiceReportEntry.getProposalNumber());
        reportDetail.setChartOfAccountsCode(cgInvoiceReportEntry.getChartOfAccountsCode());
        reportDetail.setAccountNumber(cgInvoiceReportEntry.getAccountNumber());
        reportDetail.setMilestoneNumber(cgInvoiceReportEntry.getMilestoneNumber());
        reportDetail.setMilestoneExpectedCompletionDate(cgInvoiceReportEntry.getMilestoneExpectedCompletionDate());

        BigDecimal milestoneAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getMilestoneAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getMilestoneAmount().bigDecimalValue();
        reportDetail.setMilestoneAmount(milestoneAmount);
        if (cgInvoiceReportEntry.isBilled()) {
            reportDetail.setBilled(KFSConstants.ParameterValues.YES);
        } else {
            reportDetail.setBilled(KFSConstants.ParameterValues.NO);
        }

        if (cgInvoiceReportEntry.isActive()) {
            reportDetail.setActive(KFSConstants.ParameterValues.YES);
        } else {
            reportDetail.setActive(KFSConstants.ParameterValues.NO);
        }
    }

    @Override
    public ReportInfo getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(ReportInfo reportInfo) {
        this.reportInfo = reportInfo;
    }
}
