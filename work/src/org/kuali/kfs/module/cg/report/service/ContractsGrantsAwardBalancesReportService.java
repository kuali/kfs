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
package org.kuali.kfs.module.cg.report.service;

import java.io.ByteArrayOutputStream;

import org.kuali.kfs.module.cg.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * This class defines the methods for Contracts Grants Award Balances Reports.
 */
public interface ContractsGrantsAwardBalancesReportService {

    /**
     * This method generates the report.
     * 
     * @param reportDataHolder
     * @param reportInfo
     * @param baos
     * @return
     */
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos);

    /**
     * This method generates the report onto the byteArrayOutputStream provided.
     * 
     * @param reportDataHolder
     * @param reportInfo
     * @param baos
     * @return
     */
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ReportInfo reportInfo, ByteArrayOutputStream baos);
}
