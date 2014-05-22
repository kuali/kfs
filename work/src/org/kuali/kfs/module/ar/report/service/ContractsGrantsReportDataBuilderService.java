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
package org.kuali.kfs.module.ar.report.service;

import java.util.List;

import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Interface for services which want to build AR Contracts & Grants Billing reports
 */
public interface ContractsGrantsReportDataBuilderService {
    /**
     * Builds a new ContractsGrantsReportDataHolder object for a set of details
     * @param displayList the set of looked-up data to build a report from
     * @param sortPropertyName the name of the property to sort everything by
     * @return a ContractsGrantsReportDataHolder for a class
     */
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName);

    /**
     * @return the class of the details that the given implementation builds report details out of
     */
    public Class<? extends BusinessObject> getDetailsClass();

    /**
     * @return the ReportInfo object associated with this report
     */
    public ReportInfo getReportInfo();
}