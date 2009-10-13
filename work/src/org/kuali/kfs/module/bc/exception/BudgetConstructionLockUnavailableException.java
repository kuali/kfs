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
package org.kuali.kfs.module.bc.exception;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;

/**
 * Thrown when an account lock is unavailable for the account
 * 
 * @see org.kuali.kfs.module.bc..service.HumanResourcesPayrollService
 */
public class BudgetConstructionLockUnavailableException extends RuntimeException {
    private BudgetConstructionLockStatus lockStatus;
    
    /**
     * Constructs a BudgetConstructionLockUnavailableException.java.
     */
    public BudgetConstructionLockUnavailableException(BudgetConstructionLockStatus lockStatus) {
        super();

        this.lockStatus = lockStatus;
    }
    
    /**
     * Return the BudgetConstructionLockStatus
     * 
     * @return
     */
    public BudgetConstructionLockStatus getLockStatus() {
        return lockStatus;
    }
    
}
