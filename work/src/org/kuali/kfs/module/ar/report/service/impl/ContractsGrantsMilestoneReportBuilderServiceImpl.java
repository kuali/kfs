/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsMilestoneReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsMilestoneReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of ContractsGrantsReportDataBuilderService to help build the Contracts Grants Milestone Report
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

            reportDetail.setDisplaySubtotalInd(false);

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
        reportDetail.setAccountNumber(cgInvoiceReportEntry.getAccountNumber());
        reportDetail.setMilestoneNumber(cgInvoiceReportEntry.getMilestoneNumber());
        reportDetail.setMilestoneExpectedCompletionDate(cgInvoiceReportEntry.getMilestoneExpectedCompletionDate());

        BigDecimal milestoneAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getMilestoneAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getMilestoneAmount().bigDecimalValue();
        reportDetail.setMilestoneAmount(milestoneAmount);
        reportDetail.setIsItBilled(cgInvoiceReportEntry.getBilledIndicator());
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