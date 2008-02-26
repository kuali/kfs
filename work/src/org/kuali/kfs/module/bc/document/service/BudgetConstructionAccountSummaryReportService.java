/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.service;

import java.util.Collection;

import org.kuali.module.budget.bo.BudgetConstructionAccountSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountSummaryReport;

/**
 * This interface defines the methods for BudgetConstructionAccountSummaryReports
 */
public interface BudgetConstructionAccountSummaryReportService {

    /**
     * updates account summary table.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateReportsAccountSummaryTable(String personUserIdentifier);

    /**
     * updates account summary table when users choose consolidation.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateReportsAccountSummaryTableWithConsolidation(String personUserIdentifier);

    /**
     * 
     * builds BudgetConstructionAccountSummaryReports
     * 
     * @param universityFiscalYear
     * @param accountSummaryList
     * @return Collection
     */
    public Collection<BudgetConstructionOrgAccountSummaryReport> buildReports(Integer universityFiscalYear, Collection<BudgetConstructionAccountSummary> accountSummaryList);
    
}
