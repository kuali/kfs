/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.budget.dao;
import java.util.*;
import org.kuali.module.budget.bo.*;

public interface GenesisDao {

    /*
     * return a map of values for the budget construction control flags
     * for the fiscal year (flag name, flag value)
     */
   public Map<String,String> getBudgetConstructionControlFlags 
                             (Integer universityFiscalYear);
   /*
    *  check the value of a specific budget construction control flag
    *  (on = true, off = false)
    */
   public boolean getBudgetConstructionControlFlag (Integer universityFiscalYear,
               String FlagID);
   /*
    *  get the initiator ID for budget construction
    */
   public String getBudgetConstructionInitiatorID();
   
   // clear locks in headers
   public void clearHangingBCLocks (Integer currentFiscalYear);

   // control flags
   public void setControlFlagsAtTheStartOfGenesis(Integer currentFiscalYear);
   public void setControlFlagsAtTheEndOfGenesis(Integer currentFiscalYear);
   
   // chart and organization hierarchy
   public void createChartForNextBudgetCycle();
   public void rebuildOrganizationHierarchy(Integer currentFiscalYear);
   
   // intialization for genesis
   public void clearDBForGenesis(Integer BaseYear);
   
   // pending budget construction general ledger
   public void initialLoadToPBGL(Integer currentFiscalYear);
   public void updateToPBGL(Integer currentFiscalYear);
   
   // document creation
   public void createNewBCDocuments(Integer currentFiscalYear);
   public void primeNewBCHeadersDocumentCreation(Integer currentFiscalYear);
}
