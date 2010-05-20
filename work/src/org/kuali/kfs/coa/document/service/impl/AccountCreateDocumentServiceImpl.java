/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.KCAward;
import org.kuali.kfs.coa.document.service.AccountCreateDocumentService;

/**
 * This class is the default implementation of the AccountCreateDocumentService
 */

public class AccountCreateDocumentServiceImpl implements AccountCreateDocumentService {

    /**
     * This method will use the data from kc award and create a document with the account
     */
    public Account createAccountForCGMaintenanceDocument(KCAward kCAward) {
       Account account = new Account();
       
        return account;
    }
    
    
    /**
     * This method will create a maintenance 
     * @see org.kuali.kfs.coa.document.service.CreateAccountService#createAutomaticCGAccountMaintenanceDocument()
     */
    public void createAutomaticCGAccountMaintenanceDocument(Account account) {
        
    }
}
