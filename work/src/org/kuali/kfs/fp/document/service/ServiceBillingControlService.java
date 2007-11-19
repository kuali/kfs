/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import org.kuali.module.financial.bo.ServiceBillingControl;

/**
 * This service interface defines methods that a ServiceBillingControlService implementation must provide.
 * 
 */
public interface ServiceBillingControlService {

    /**
     * Retrieves the ServiceBillingControl by its composite primary key (all passed in as parameters).
     * 
     * @param chartOfAccountsCode Chart of accounts code used to perform lookup of ServiceBillingControl object.
     * @param accountNumber Account number used to perform lookup of ServiceBillingControl object.
     * @return A ServiceBillingControl object instance. Returns null if there is none with the given key.
     */
    public ServiceBillingControl getByPrimaryId(String chartOfAccountsCode, String accountNumber);

    /**
     * Retrieves all the ServiceBillingControls in the database.
     * 
     * @return All the ServiceBillingControls in the database, or an empty array (not null) if there are none.
     */
    public ServiceBillingControl[] getAll();
}
