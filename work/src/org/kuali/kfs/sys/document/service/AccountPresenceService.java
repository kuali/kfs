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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;

/**
 * This service interface defines methods that an AccountPresenceService implementation must provide.
 * 
 */
public interface AccountPresenceService {

    /**
     * Checks the given account for presence control turned on. If turned on, the object code must have a budget record in the gl
     * balance table, otherwise this method returns false. If presence control is turned off, method returns true.
     * 
     * @param account The account to be checked for presense control.
     * @param objectCode The object code to be looked up in the gl balance table.
     * @return True if presence control is turned on and obj code is in gl balance table, false otherwise.
     */
    public boolean isObjectCodeBudgetedForAccountPresence(Account account, ObjectCode objectCode);
}
