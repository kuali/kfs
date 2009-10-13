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
package org.kuali.kfs.coa.dataaccess;

import org.kuali.kfs.coa.businessobject.A21SubAccount;

/**
 * This interface defines what methods of data retrieval should be allowed for {@link org.kuali.kfs.coa.businessobject.A21SubAccount}
 */
public interface A21SubAccountDao {
    /**
     * This method retrieves an A21SubAccount based on it's primary keys
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @return the A21SubAccount that matches this primary key
     */
    public A21SubAccount getByPrimaryKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber);
}
