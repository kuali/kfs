/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.coa.dataaccess;


public interface AccountDelegateDao {

    /**
     * Retrieves the Document Number of any AccountDelegate locking this record.
     * 
     * @param lockingRepresentation String representation of the MaintanceLock to check against.
     * @param documentNumber the document number being checked against.
     * 
     * @return the document number of the Document locking this record.
     */
    
    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber);
    
}
