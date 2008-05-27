/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.List;

import org.kuali.core.bo.BusinessObject;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;

/**
 * Facilates Budget Construction Import requests
 * 
 */
public interface ImportRequestDao {
    
    /**
     * 
     * @return header record or null if record does not exist.
     */
    public BudgetConstructionHeader getHeaderRecord(BudgetConstructionRequestMove record, Integer budgetYear);
    
    /**
     * find all BudgetConstructionRequestMove records with null error codes
     * 
     * @return List<BudgetConstructionRequestMove>
     */
    public List<BudgetConstructionRequestMove> findAllNonErrorCodeRecords(String personUniversalIdentifier);
    
    /**
     * Save or update business object based on isUpdate
     * 
     * @param businessObject
     * @param isUpadate
     */
    public void save(BusinessObject businessObject, boolean isUpdate);
   
}
