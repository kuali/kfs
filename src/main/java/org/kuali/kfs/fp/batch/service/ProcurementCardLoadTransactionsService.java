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
