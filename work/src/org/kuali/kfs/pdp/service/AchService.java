/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.pdp.service;

import java.util.List;

import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;

/**
 * Provides service methods for retrieving ACH information
 */
public interface AchService {

    /**
     * Retrieves a Payee's ACH record
     * 
     * @param idType type of payee
     * @param payeeId unique identifier for payee (based on type)
     * @param achTransactionType ach transaction type for record
     * @return PayeeAchAccount
     */
    public PayeeACHAccount getAchInformation(String idType, String payeeId, String achTransactionType);
    
    /**   
     * Gets all active PayeeAchAccounts.
     * @return all active PayeeAchAccounts.
     */
    public List<PayeeACHAccount> getActiveAchAccounts();
    
}
