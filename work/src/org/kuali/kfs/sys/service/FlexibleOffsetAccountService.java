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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.fp.businessobject.OffsetAccount;
import org.kuali.kfs.gl.businessobject.FlexibleAccountUpdateable;

/**
 * 
 * This interface defines methods that a FlexibleOffsetAccount Service must provide.
 * 
 */
public interface FlexibleOffsetAccountService {

    /**
     * Retrieves the OffsetAccount by its composite primary key (all passed in as parameters) if the SYSTEM parameter
     * FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     * 
     * @param chartOfAccountsCode The chart code of the account to be retrieved.
     * @param accountNumber The account number of the account to be retrieved.
     * @param financialOffsetObjectCode Offset object code used to retrieve the OffsetAccount.
     * @return An OffsetAccount object instance. Returns null if there is none with the given key, or if the SYSTEM parameter
     *         FLEXIBLE_OFFSET_ENABLED_FLAG is false.
     */
    public OffsetAccount getByPrimaryIdIfEnabled(String chartOfAccountsCode, String accountNumber, String financialOffsetObjectCode);

    /**
     * Retrieves whether the SYSTEM parameter FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     * 
     * @return Whether the SYSTEM parameter FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     */
    public boolean getEnabled();

    /**
     * This method will apply the flexible offset account if necessary. It will only change the chart, account, sub account and sub
     * object on the transaction. If the flexible offset isn't enabled or valid for this transaction, it will be unchanged.
     * 
     * It throws an InvalidFlexibleOffsetException if the flexible offset account associated with the transaction
     * is invalid, closed or expired or if the object code is invalid for the flexible offset.
     * 
     * @param transaction The OriginEntryFull object to be updated.
     * @return True if transaction was changed, false if not.
     */
    public boolean updateOffset(FlexibleAccountUpdateable transaction);
}
