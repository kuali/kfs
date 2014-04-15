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
package org.kuali.kfs.sys.batch.dataaccess;

import java.util.Collection;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;

/**
 * When reading FinancialSystemDocumentHeader records for population by the batch job, we only want to read a limited number at a time.  Therefore,
 * we created a DAO so we specify the begin and end indices on the criteria
 */
public interface FinancialSystemDocumentHeaderPopulationDao {

    /**
     * @return the total number of remaining FinancialSystemDocumentHeader records which need to be processed
     */
    public abstract int countTotalFinancialSystemDocumentHeadersToProcess();

    /**
     * Looks up a set of FinancialSystemDocumentHeader records in the database between the given start and end indices
     * @param batchStartIndex the first record to search for
     * @param batchEndIndex the last record to be part of the current batch
     * @return a batch sized collection of FinancialSystemDocumentHeader records
     */
    public abstract Collection<FinancialSystemDocumentHeader> getFinancialSystemDocumentHeadersForBatch(int batchStartIndex, int batchEndIndex);
}
