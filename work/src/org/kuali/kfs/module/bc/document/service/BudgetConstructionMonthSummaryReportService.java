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

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgMonthSummaryReport;

/**
 * defines the methods for BudgetConstructionMonthSummaryReports
 */
public interface BudgetConstructionMonthSummaryReportService {

    /**
     * updates month summary table.
     * 
     * @param principalName
     * @param consolidateToObjectCodeLevel
     * @return
     */
    public void updateMonthSummaryReport(String principalName, boolean consolidateToObjectCodeLevel);

    /**
     * 
     * builds BudgetConstructionLevelSummaryReports
     * 
     * @param universityFiscalYear
     * @param principalName
     *  @param consolidateToObjectCodeLevel
     * @return Collection
     */
    public Collection<BudgetConstructionOrgMonthSummaryReport> buildReports(Integer universityFiscalYear, String principalName, boolean consolidateToObjectCodeLevel);
    
}

