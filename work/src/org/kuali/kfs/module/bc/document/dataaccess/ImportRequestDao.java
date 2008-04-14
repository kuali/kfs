/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.dao;

import org.kuali.module.budget.bo.BudgetConstructionRequestMove;

/**
 * Facilates Budget Construction Import requests
 * 
 */
public interface ImportRequestDao {
    
    /**
     * checks if record has a non-budgeted account
     * 
     * @return true if account is non-budgeted.
     */
    public boolean isNonBudgetedAccount(BudgetConstructionRequestMove record);
    
    /**
     * if record's account closed account is flagged with 'CLAC' error code
     * 
     * @return true if account was flagged. false if not flagged
     */
    //public void isClosedAccount(BudgetConstructionRequestMove record);
    
    /**
     * Flags all expired account records with 'EXAC' error code
     * 
     */
    //public void flagExpiredAccounts();
    
    /**
     * Flags all inactive subaccount records with 'INSA' error code
     * 
     */
    //public void flagInactiveSubAccounts();
    
    /**
     * Sets subaccount type code for each budget construction request record
     */
    //public void setObjectTypeCodes();
    
    /**
     * Flags all invalid object due to null object type with 'NOOB' error code
     * 
     */
    //public void flagInvalidObjectCodesDueToNullObjectType();
    
    /**
     * Flags all invalid object due to bad object type with 'NOOB' error code
     * 
     */
    //public void flagInvalidObjectCodesDueToBadObjectType();
    
    /**
     * Flags all invalid object codes that are not due to bad or null object types with 'INOB' error codes
     * 
     */
    //public void flagOtherInvalidObjectCodes();
    
    /**
     * flags all compensation object codes with 'COMP' error codes
     * 
     */
    //public void flagCompensationObjectCodes();
    
    /**
     * flag all no wage accounts with 'CMPA' error codes
     * 
     */
    //public void flagNoWageAccounts();
    
    /**
     * flag invalid sub-object codes with 'NOSO' error codes 
     * 
     */
    //public void flagInvalidSubObjectCodes();
    
    /**
     * flag all inactive sub-object codes with 'INSO' error codes
     * 
     */
    //public void flagInactiveSubObjectCodes();
}
