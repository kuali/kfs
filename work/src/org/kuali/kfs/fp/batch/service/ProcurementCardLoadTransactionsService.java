/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service;


/**
 * Provides methods to load batch files for the procurement card batch job.
 */
public interface ProcurementCardLoadTransactionsService {

    /**
     * Validates and parses the file identified by the given files name. If successful, parsed entries are stored.
     * 
     * @param fileNaem - name of file to process
     * @return boolean indicating if the load was successful
     */
    public boolean loadProcurementCardFile(String fileName);

    /**
     * Clears out temporary transaction table.
     */
    public void cleanTransactionsTable();
}