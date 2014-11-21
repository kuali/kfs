/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
     * @param icrCostTypes a list of cost types - system parameter INDIRECT_COST_TYPES
     * @param expenseObjectTypes a list of expense object types
     * @param costShareSubAccountType the cost share sub-account type     *
     * @param fw the file writer
     */
    public void buildIcrEncumbranceFeed(Integer fiscalYear, String fiscalPeriod, String icrEncumbOriginCode, Collection<String> icrEncumbBalanceTypes, Collection<String> icrCostTypes, String[] expenseObjectTypes, String costShareSubAccountType, Writer fw) throws IOException;

}
