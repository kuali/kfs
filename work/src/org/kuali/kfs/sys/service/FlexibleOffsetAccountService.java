/*
 * Copyright 2006 The Kuali Foundation.
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

import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.gl.bo.OriginEntry;

/**
 * This interface defines methods that a FlexibleOffsetAccount Service must provide.
 * 
 * 
 */
public interface FlexibleOffsetAccountService {

    /**
     * Retrieves the OffsetAccount by its composite primary key (all passed in as parameters) if the SYSTEM parameter
     * FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialOffsetObjectCode
     * @return An OffsetAccount object instance. Returns null if there is none with the given key, or if the SYSTEM parameter
     *         FLEXIBLE_OFFSET_ENABLED_FLAG is false.
     */
    public OffsetAccount getByPrimaryIdIfEnabled(String chartOfAccountsCode, String accountNumber, String financialOffsetObjectCode);

    /**
     * Retrieves whether the SYSTEM parameter FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     * 
     * @return whether the SYSTEM parameter FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     */
    public boolean getEnabled();

    /**
     * This method will apply the flexible offset account if necessary. It will only change the chart, account, sub account and sub
     * object on the transaction. If the flexible offset isn't enabled or valid for this transaction, it will be unchanged.
     * 
     * It throws a FlexibleOffset
     * 
     * @param transaction
     * @return true if transaction was changed, false if not
     */
    public boolean updateOffset(OriginEntry transaction);
}
