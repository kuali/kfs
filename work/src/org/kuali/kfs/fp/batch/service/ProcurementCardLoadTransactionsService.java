/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.batch.service;

import org.kuali.kfs.sys.batch.InitiateDirectory;
import org.kuali.kfs.sys.service.ReportWriterService;

/**
 * 
 * This service interface defines the methods that a ProcurementCardLoadTransactionsService implementation must provide.
 * 
 * Provides methods to load batch files for the procurement card batch job.
 */
public interface ProcurementCardLoadTransactionsService extends InitiateDirectory{

    /**
     * Validates and parses the file identified by the given files name. If successful, parsed entries are stored.
     * 
     * @param fileNaem Name of file to be uploaded and processed.
     * @param reportWriterService report writing service.
     * @return True if the file load and store was successful, false otherwise.
     */
    public boolean loadProcurementCardFile(String fileName, ReportWriterService reportWriterService);
    
    /**
     * Clears out the temporary transaction table.
     */
    public void cleanTransactionsTable();
}
