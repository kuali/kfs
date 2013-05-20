/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.module.ar.businessobject.CustomerNote;

/**
 * This class defines all the service methods for Customer Note.
 */
public interface CustomerNoteService {

    /**
     * This method returns a customer note by primary key.
     * 
     * @param customerNumber
     * @param customerNoteIdentifier
     * @return
     */
    public CustomerNote getByPrimaryKey(String customerNumber, Integer customerNoteIdentifier);

    /**
     * This method gets the next note identifier.
     * 
     * @return
     */
    public Integer getNextCustomerNoteIdentifier();

    /**
     * This method returns true if customer address exists.
     * 
     * @param customerNumber
     * @param customerNoteIdentifier
     * @return
     */
    public boolean customerNoteExists(String customerNumber, Integer customerNoteIdentifier);
}
