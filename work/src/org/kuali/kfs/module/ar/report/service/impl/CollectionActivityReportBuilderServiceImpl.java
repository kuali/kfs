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

import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.report.CollectionActivityReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;

/**
 * Service which builds a report object for the Collection Activity Report
 */
public class CollectionActivityReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService<CollectionActivityReport> {

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<CollectionActivityReport> displayList, String sortPropertyName) {
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<CollectionActivityReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (CollectionActivityReport collActReportEntry : displayList) {
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
    public Class<CollectionActivityReport> getDetailsClass() {
        return CollectionActivityReport.class;
    }

}
