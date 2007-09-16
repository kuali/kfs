/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.service;

import java.util.HashMap;

import org.kuali.module.purap.bo.PurApAccountingLineBase;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;

/**
 * This class contains logic for use by the individual AccountsPayable docs
 */
public interface AccountsPayableService {

    /**
     * This method generates a list of continuation accounts for expired or closed accounts as well as
     * a list of expired or closed accounts with no continuation accounts.
     * 
     * @param document
     * @return
     */
    public HashMap<String, ExpiredOrClosedAccountEntry> getExpiredOrClosedAccountList(AccountsPayableDocument document);
    
    /**
     * This method generates a note of where continuation accounts were used and adds them as a note to the document.
     * 
     * @param document
     * @param expiredOrClosedAccountList
     */
    public void generateExpiredOrClosedAccountNote(AccountsPayableDocument document, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList);
    
    /**
     * This method adds a warning message to the message list if expired or closed accounts have been used on the document.
     * 
     * @param document
     */
    public void generateExpiredOrClosedAccountWarning(AccountsPayableDocument document);        
    
    /**
     * This method performs the replacement of an expired/closed account with a continuation account.
     * 
     * @param acctLineBase
     * @param expiredOrClosedAccountList
     */
    public void processExpiredOrClosedAccount(PurApAccountingLineBase acctLineBase, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList);
    
    public void cancelAccountsPayableDocument(AccountsPayableDocument apDocument, String currentNodeName);
}
