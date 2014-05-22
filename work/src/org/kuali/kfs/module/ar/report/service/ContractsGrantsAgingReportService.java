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

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class is used to get the services for PDF generation and other services for Contracts And Grants Aging Report
 */
public interface ContractsGrantsAgingReportService {
    /**
     * This method is used to filter the contracts and grants invoice docs according to criteria
     *
     * @param fieldValues
     * @param orgCode
     * @param startDate
     * @param endDate
     * @return Returns the Map of Customer and customer related invoice CGDocs.
     */
    public Map<String, List<ContractsGrantsInvoiceDocument>> filterContractsGrantsAgingReport(Map fieldValues, java.sql.Date startDate, java.sql.Date endDate) throws ParseException;


    /**
     * This method is used to generate xls reports for the Contracts Grants Aging report.
     *
     * @param details
     * @param totalDataHolder
     * @return Returns the generated file in byte array.
     */
    public byte[] generateCSVToExport(ContractsGrantsReportDataHolder cgInvoiceReportDataHolder, List<ContractsGrantsInvoiceDocument> displayList, String sortPropertyName);

    /**
     * Generates the Map of sub totals from the C&G Invoice documents
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    public Map<String, List<KualiDecimal>> buildSubTotalMap(List<ContractsGrantsInvoiceDocument> displayList, String sortPropertyName);
}
