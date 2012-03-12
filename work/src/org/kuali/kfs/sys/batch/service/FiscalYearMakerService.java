/*
 * Copyright 2008-2009 The Kuali Foundation
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
