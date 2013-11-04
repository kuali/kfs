/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TmExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface TmExpenseService {

    public Map<String, AccountingDistribution> getAccountingDistribution(TravelDocument document);
    
    /**
     * Calculate distribution total
     * 
     * @param document
     * @param distributionMap
     * @param expenses
     */
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TmExpense> expenses);
    
    /**
     * Get the appropriate expense detail from the document
     * 
     * @param document
     * @return
     */
    public List<? extends TmExpense> getExpenseDetails(TravelDocument document);
    
    /**
     * Validation for expense calculation
     * 
     * @param expenses
     */
    public boolean validateExpenseCalculation(TmExpense expense);
    
    /**
     * 
     * @param document
     * @param includeNonReimbursable
     * @return
     */
    public KualiDecimal getAllExpenseTotal(TravelDocument document, boolean includeNonReimbursable);
    
    /**
     * 
     * @param document
     * @return
     */
    public KualiDecimal getNonReimbursableExpenseTotal(TravelDocument document);
    
    /**
     * 
     * @param travelDocument
     */
    public void processExpense(TravelDocument travelDocument);
    
    /**
     * 
     * @param travelDocument
     */
    public void updateExpense(TravelDocument travelDocument);
}
