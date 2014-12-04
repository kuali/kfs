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
package org.kuali.kfs.sys.batch.service;


/**
 * Runs the fiscal year maker process which creates rows for the next fiscal year based on records for the current fiscal year for
 * configured tables. The base year for the process is given by the system parameter
 * <code>KFS-COA FiscalYearMakerStep SOURCE_FISCAL_YEAR</code>. In addition, the process has an option for how it should handle
 * records that already exist in the target year. If the system parameter
 * <code>KFS-COA FiscalYearMakerStep OVERRIDE_TARGET_YEAR_DATA_IND</code> is set to 'Y', target year records will be overwritten;
 * Otherwise, target year records that already exist will be left alone. Each table that is setup for the fiscal year maker process
 * has a bean configured in Spring. This bean points to an implementation of <code>FiscalYearMaker</code> (either the default
 * implementation or custom) that sets up the <code>Criteria</code> for selecting records to copy, and performs the process of
 * changing the records for the new year. New implementations can be created and configured in Spring to customize the behavior for
 * a table.
 */
public interface FiscalYearMakerService {

    /**
     * Runs the fiscal year maker process for the configured base year
     */
    public void runProcess();
}
