/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess;

import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * A surprisingly small DAO interface that declares methods to help Entries interact with the database
 */
public interface EntryDao {

    /**
     * Get the max sequence number currently used for a transaction
     * 
     * @param t the transaction to check
     * @return the max sequence number
     */
    public int getMaxSequenceNumber(Transaction t);

    /**
     * Purge the entry table by chart/year
     * 
     * @param chart the chart of accounts code of entries to purge
     * @param year the university fiscal year of entries to purge
     */
    public void purgeYearByChart(String chart, int year);
}
