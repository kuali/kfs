/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.gl.dataaccess;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public interface IcrEncumbranceDao {
    /**
     * Builds a file of ICR Encumbrance Entries for posting to the General Ledger
     *
     * @param fiscalYear the current fiscal year
     * @param fiscalPeriod the current fiscal period
     * @param icrEncumbOriginCode the ICR origin code - system parameter INDIRECT_COST_RECOVERY_ENCUMBRANCE_ORIGINATION
     * @param icrEncumbBalanceTypes a list of balance types - system parameter INDIRECT_COST_RECOVERY_ENCUMBRANCE_BALANCE_TYPES
     * @param expenseObjectTypes a list of expense object types
     * @param costShareSubAccountType the cost share sub-account type
     * @param fw the file writer
     */
    public void buildIcrEncumbranceFeed(Integer fiscalYear, String fiscalPeriod, String icrEncumbOriginCode, Collection<String> icrEncumbBalanceTypes, String[] expenseObjectTypes, String costShareSubAccountType, Writer fw) throws IOException;

}