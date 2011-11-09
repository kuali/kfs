/*
 * Copyright 2008 The Kuali Foundation
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
