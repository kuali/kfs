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
