/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.io.File;
import java.util.Date;

import org.kuali.kfs.module.ar.report.util.CustomerStatementReportDataHolder;

/**
 * Customer Invoice Report generation related method.
 */
public interface CustomerStatementReportService {

    /**
     * This method generates a Customer Statement report PDF with the given report data and runDate
     *
     * @param reportDataHolder the given report data holder
     * @param runDate the datetime of the repor generation
     * @param statementFormat format (detailed or summary) for the report
     * @return report PDF
     */
    public File generateReport(CustomerStatementReportDataHolder reportDataHolder, Date runDate, String statementFormat);
}
