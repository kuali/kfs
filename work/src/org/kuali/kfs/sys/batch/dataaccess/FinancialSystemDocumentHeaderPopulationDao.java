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
