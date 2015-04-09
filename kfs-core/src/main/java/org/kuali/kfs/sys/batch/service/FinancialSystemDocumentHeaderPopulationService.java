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
package org.kuali.kfs.sys.batch.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.kew.api.document.DocumentStatus;

/**
 * Business object which supports the populateFinancialSystemDocumentHeadersFromKewJob, which populate fields on the FinancialSystemDocumentHeader
 * from KEW document headers
 */
public interface FinancialSystemDocumentHeaderPopulationService {
    /**
     * In groups of size batchSize at a time, reads FinancialSystemDocumentHeaders, finds their matching KEW document headers,
     * and then updates the changes in the database
     * @param batchSize the size of the batch to process at once
     * @param jobRunSize the total number of records to process in this run; if the value is null or negative, then all records will be run
     * @param documentStatusesToPopulate if the given Set has any members, only documents in the given statuses will have their FinancialSystemDocumentHeader records populated
     */
    public abstract void populateFinancialSystemDocumentHeadersFromKew(int batchSize, Integer jobRunSize, Set<DocumentStatus> documentStatusesToPopulate);

    /**
     * Handles a single batch of document headers; exposed on the interface so that Spring can proxy it properly
     * @param documentHeaders the Map of document headers, keyed by document number, that make up the batch
     * @param documentStatusesToPopulate if the given Set has any members, only documents in the given statuses will have their FinancialSystemDocumentHeader records populated
     */
    public void handleBatch(Map<String, FinancialSystemDocumentHeader> documentHeaders, Set<DocumentStatus> documentStatusesToPopulate);

    /**
     * Reads in a batch of FinancialSystemDocumentHeader records which exist within the given start index and end index
     * @param startIndex the start index for the first record in the batch
     * @param endIndex the end index for the last record in the batch
     * @return the batch of records
     */
    public Collection<FinancialSystemDocumentHeader> readBatchOfFinancialSystemDocumentHeaders(int startIndex, int endIndex);

    /**
     * @return the total count of yet unpopulated financial system document header records
     */
    public int getFinancialSystemDocumentHeaderCount();
}
