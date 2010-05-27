/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.document.service.AccountCreateDocumentService;
import org.kuali.kfs.module.external.kc.dto.AccountCreationStatus;
import org.kuali.kfs.module.external.kc.dto.AccountParameters;
import org.kuali.kfs.module.external.kc.service.AccountCreationService;

public class AccountCreationServiceImpl implements AccountCreationService {

    private AccountCreateDocumentService accountCreateDocumentService;
    
    public AccountCreationStatus createAccount(AccountParameters accountParameters) {
        
        //accountCreateDocumentService
        //List<String> statusCodes = new ArrayList<String>(1);
        //statusCodes.add("SUCCESS");

        //AccountCreationStatus accountCreationStatus = new AccountCreationStatus();
        //accountCreationStatus.setSuccess(true);
        
        return accountCreateDocumentService.createAccountForCGMaintenanceDocument(accountParameters);        
    }

    /**
     * Gets the accountCreateDocumentService attribute. 
     * @return Returns the accountCreateDocumentService.
     */
    public AccountCreateDocumentService getAccountCreateDocumentService() {
        return accountCreateDocumentService;
    }

    /**
     * Sets the accountCreateDocumentService attribute value.
     * @param accountCreateDocumentService The accountCreateDocumentService to set.
     */
    public void setAccountCreateDocumentService(AccountCreateDocumentService accountCreateDocumentService) {
        this.accountCreateDocumentService = accountCreateDocumentService;
    }
}
