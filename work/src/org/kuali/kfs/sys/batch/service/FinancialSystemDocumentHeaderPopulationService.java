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
package org.kuali.kfs.sys.batch.service;

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;

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
     */
    public abstract void populateFinancialSystemDocumentHeadersFromKew(int batchSize, Integer jobRunSize);

    /**
     * Handles a single batch of document headers; exposed on the interface so that Spring can proxy it properly
     * @param documentHeaders the Map of document headers, keyed by document number, that make up the batch
     */
    public void handleBatch(Map<String, FinancialSystemDocumentHeader> documentHeaders);

    /**
     * Reads in a batch of FinancialSystemDocumentHeader records which exist within the given start index and end index
     * @param startIndex the start index for the first record in the batch
     * @param endIndex the end index for the last record in the batch
     * @return the batch of records
     */
    public Collection<FinancialSystemDocumentHeader> readBatchOfFinancialSystemDocumentHeaders(int startIndex, int endIndex);
}
