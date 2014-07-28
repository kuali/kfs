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
     * Looks up ContractsGrantsInvoiceDocuments matching the criteria in the given fieldValues
     * @param fieldValues the criteria to find matching ContractsGrantsInvoiceDocument's
     * @return a Map of ContractsGrantsInvoiceDocument which match the given Criteria, keyed by customer number
     */
    public Map<String, List<ContractsGrantsInvoiceDocument>> lookupContractsGrantsInvoiceDocumentsForAging(Map<String, Object> fieldValues);

    /**
     * Given a Map of CustomerGrantsInvoiceDocuments keyed by customer number, comes up with a flat List of just the ContractsGrantsInvoiceDocuments
     * @param cgMapByCustomer the Map to flatten
     * @return the List, severely flatten'd
     */
    public List<ContractsGrantsInvoiceDocument> flattenContrantsGrantsInvoiceDocumentMap(Map<String, List<ContractsGrantsInvoiceDocument>> cgMapByCustomer);
}
