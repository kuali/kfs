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
package org.kuali.kfs.module.ar.batch.service;

import org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO;
import org.kuali.rice.kim.api.identity.Person;

/**
 * 
 * Accepts a write-off batch VO object, populated with a list of invoices to write off,
 * and creates the correct XML file and drops it in the batch system for processing.
 * 
 * Actual processing of this happens later, and asynchronously.
 * 
 * @return Returns a true or false, depending on whether the call succeeded or not.
 */
public interface CustomerInvoiceWriteoffBatchService {

    public boolean loadFiles();
    
    /**
     * 
     * Accepts a batch VO full of invoiceNumbers to be written off in batch.  Creates an XML file and 
     * drops it in the batch service staging area, to be processed next time the job runs.
     * 
     * @param user KIM person who is credited with creating the batch file.
     * @param writeoffBatchVO Populated batch VO full of invoice numbers.
     * @return True if everything worked, False if it failed.
     */
    public String createBatchDrop(Person user, CustomerInvoiceWriteoffBatchVO writeoffBatchVO);
    
}
