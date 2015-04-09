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
package org.kuali.kfs.module.bc.batch.dataaccess;

import java.util.Map;

public interface GenesisDao {

    /*
     * return a map of values for the budget construction control flags for the fiscal year (flag name, flag value)
     */
    public Map<String, String> getBudgetConstructionControlFlags(Integer universityFiscalYear);

    /*
     * fetch the fiscal year corresponding to today's date
     */
    public Integer fiscalYearFromToday();

    /*
     * check the value of a specific budget construction control flag (on = true, off = false)
     */
    public boolean getBudgetConstructionControlFlag(Integer universityFiscalYear, String FlagID);

    // clear locks in headers
    public void clearHangingBCLocks(Integer currentFiscalYear);

    // control flags
    public void setControlFlagsAtTheStartOfGenesis(Integer currentFiscalYear);

    public void setControlFlagsAtTheEndOfGenesis(Integer currentFiscalYear);

    // chart and organization hierarchy
    public void createChartForNextBudgetCycle();

    public void rebuildOrganizationHierarchy(Integer currentFiscalYear);

    // intialization for genesis
    public void clearDBForGenesis(Integer BaseYear);

    // pending budget construction general ledger
    public void ensureObjectClassRIForBudget(Integer BaseYear);

    public void initialLoadToPBGL(Integer currentFiscalYear);

    public void updateToPBGL(Integer currentFiscalYear);

    public Map verifyAccountsAreAccessible(Integer requestFiscalYear);

    // document creation
    // create document with embedded workflow
    public void createNewBCDocumentsFromGLCSF(Integer BaseYear, boolean GLUpdatesAllowed, boolean CSFUpdatesAllowed);

    // budget construction CSF and budget construction appointment funding
    public void buildAppointmentFundingAndBCSF(Integer BaseYear);

}
