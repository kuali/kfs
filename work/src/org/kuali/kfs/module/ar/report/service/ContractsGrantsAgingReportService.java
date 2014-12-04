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
     * @param fieldValues
     * @param startDate
     * @param endDate
     * @return Returns the Map of Customer and customer related invoice CGDocs.
     * @throws ParseException if dates cannot be properly parsed
     */
    public Map<String, List<ContractsGrantsInvoiceDocument>> filterContractsGrantsAgingReport(Map fieldValues, java.sql.Date startDate, java.sql.Date endDate) throws ParseException;

    /**
     * Looks up ContractsGrantsInvoiceDocuments matching the criteria in the given fieldValues
     * @param fieldValues the criteria to find matching ContractsGrantsInvoiceDocument's
     * @return a Map of ContractsGrantsInvoiceDocument which match the given Criteria, keyed by customer number
     */
    public List<ContractsGrantsInvoiceDocument> lookupContractsGrantsInvoiceDocumentsForAging(Map<String, Object> fieldValues);
}
