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

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgSalarySummaryReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReportThresholdSettings;


/**
 * defines the methods for BudgetConstructionAccountFundingDetailReports
 */
public interface BudgetConstructionSalarySummaryReportService {

    public void updateSalarySummaryReport(String principalName, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings);
    
    
    
    public Collection<BudgetConstructionOrgSalarySummaryReport> buildReports(Integer universityFiscalYear, String principalName, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings);
    
    
}

