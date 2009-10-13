/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess;


public interface AccountDelegateGlobalDao {

    /**
     * 
     * This method returns the document number of any locking records.
     * @param lockingRepresentation String representation of the MaintenanceLock created by the AccountDelegateGlobal
     * @param documentNumber the document number of the Document being checked against.
     * @return The document number of the locking record, or null if none.
     */
    
    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber);
    
}
