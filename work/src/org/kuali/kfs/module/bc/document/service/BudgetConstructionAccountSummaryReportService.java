/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

import java.util.Collection;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgAccountSummaryReport;

/**
 * This interface defines the methods for BudgetConstructionAccountSummaryReports
 */
public interface BudgetConstructionAccountSummaryReportService {

    /**
     * updates account summary table.
     * 
     * @param principalName
     */
    public void updateReportsAccountSummaryTable(String principalName);

    /**
     * updates account summary table when users choose consolidation.
     * 
     * @param principalName
     */
    public void updateReportsAccountSummaryTableWithConsolidation(String principalName);
    
    /**
     * updates account summary table.
     * 
     * @param principalName - user requesting the report
     * @param consolidated - whether to produce a consolidate report
     */
    public void updateReportsAccountSummaryTable(String principalName, boolean consolidated);

    /**
     * 
     * builds BudgetConstructionAccountSummaryReports
     * 
     * @param universityFiscalYear
     * @param accountSummaryList
     */
    public Collection<BudgetConstructionOrgAccountSummaryReport> buildReports(Integer universityFiscalYear, String principalName, boolean consolidated);
    
}

