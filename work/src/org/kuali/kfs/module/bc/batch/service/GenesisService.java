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
package org.kuali.kfs.module.bc.batch.service;

import java.util.Map;

/*
 * this service intializes/updates the budget construction data used by the budget module to build a new budget for the coming
 * fiscal year
 */
public interface GenesisService {
    /*
     * these routines indicate which actions are allowed in genesis
     */
    public boolean BatchPositionSynchAllowed(Integer BaseYear);
    
    public boolean CSFUpdatesAllowed(Integer BaseYear);

    public boolean GLUpdatesAllowed(Integer BaseYear);
    
    public boolean IsBudgetConstructionInUpdateMode(Integer BaseYear);

    // this step clears out the database for genesis
    public void clearDBForGenesis(Integer BaseYear);
    
    /*
     * this step updates budget construction with new data from the sources after genesis has run
     */
   public void bCUpdateStep(Integer baseYear); 
    
    /*
     * this step fetches the base fiscal year based on today's date
     */
    public Integer genesisFiscalYearFromToday();

    /*
     *  this step runs genesis
     */
    public void genesisStep(Integer baseYear);
    /*
     *   look of accounts from the payroll (CSF) or GL that came into budget construction but are *not* in the budget construction accounting table.
     *   this can be due to an oversight on the part of the chart manager, or to problems with the current year's budget control.
     *   such accounts will not appear in the pull-up list, since they can't be added to the reporting hierarchy, which is built from budget construction accounting.
     *   this method is provided for use by a report an institution might want to write.  such accounts will always appear in the log from bCUpdateStep above, whether this method is used or not.
     */
    public Map verifyAccountsAreAccessible(Integer requestFiscalYear);
}
