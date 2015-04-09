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
