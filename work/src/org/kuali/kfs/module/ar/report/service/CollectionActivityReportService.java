/*
 * Copyright 2012 The Kuali Foundation.
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

/**
 * This class is used to get the services for PDF generation and other services for CG Aging report.
 */
package org.kuali.kfs.module.ar.report.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;

/**
 * This class is used to get the services for PDF generation and other services for Collection Activity Report
 */
public interface CollectionActivityReportService extends ContractsGrantsReportServiceBase {

    /**
     * This method is used to generate reports for the Colletion Activity
     * 
     * @param reportDataHolder
     * @param baos
     * @return Returns the generated file name.
     */
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos);
    
    /**
     * This method is used to filter the Events for the colletion activity according to criteria
     * 
     * @param fieldValues
     * @return Returns List of Events.
     */
    public List<CollectionActivityReport> filterEventsForColletionActivity(Map fieldValues);


}
