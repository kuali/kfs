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
package org.kuali.kfs.coa.service;

import org.kuali.kfs.coa.document.AccountDelegateGlobalMaintainableImpl;
import org.kuali.kfs.coa.document.AccountDelegateMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;

/**
 * An interface of services to support account delegate logic
 */
public interface AccountDelegateService {
    
    /**
     * 
     * This method checks for any MaintenanceLocks that would block the creation of this document
     * @param global The AccountDelegateGlobalMaintainableImpl to check against.
     * @param docNumber The document number of the AccountDelegateGlobalMaintainableImpl in question.
     * @return the documentNumber of the locking record or null if none.
     */
    
    public String getLockingDocumentId(AccountDelegateGlobalMaintainableImpl global, String docNumber);
    
    /**
     * 
     * This method checks for any MaintenanceLocks that would block the creation of this document
     * @param delegate The AccountDelegateMaintainableImpl to check against.
     * @param docNumber The document number of the AccountDelegateMaintainableImpl in question.
     * @return the documentNumber of the locking record or null if none.
     */
    public String getLockingDocumentId(AccountDelegateMaintainableImpl delegate, String docNumber);

}
