/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.batch.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.report.Summary;
import org.kuali.kfs.sys.Message;

/**
 * This defines a set of reporting generation facilities
 */
public interface LaborReportService {

    /**
     * Generate statistics report with the given information
     * 
     * @param reportSummary a list of report <code>Summary<code> objects
     * @param errors the tansactions with problems and the descriptions of the problems
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateStatisticsReport(List<Summary> reportSummary, Map<Transaction, List<Message>> errors, String reportsDirectory, Date runDate);
   
}
