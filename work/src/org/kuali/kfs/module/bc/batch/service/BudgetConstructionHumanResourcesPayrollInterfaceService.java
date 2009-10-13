/*
 * Copyright 2008 The Kuali Foundation
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

public interface BudgetConstructionHumanResourcesPayrollInterfaceService {

    /**
     * 
     * read from the HR/Payroll systems and create/update position rows in Budget Construction for both the base and request years
     * @param BaseYear
     * @param PositionSynchOK control flag which indicates whether position data in budget construction is updateable or frozen
     * @param CSFUpdateOK control flag which indicates whether current payroll information can flow into budget construction
     */
    public void refreshBudgetConstructionPosition (Integer BaseYear, boolean PositionSynchOK, boolean CSFUpdateOK);
    
    /**
     * 
     * read from the HR/Payroll systems and create person-related information (ranks, titles) for display in budget construction
     * @param BaseYear
     * @param PositionSynchOK control flag which indicates whether position data in budget construction is updateable or frozen
     * @param CSFUpdateOK control flag which indicates whether current payroll information can flow into budget construction
     */
    public void refreshBudgetConstructionIntendedIncumbent (Integer BaseYear, boolean PositionSynchOK, boolean CSFUpdateOK, boolean BCUpdatesAllowed);
}
