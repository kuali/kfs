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

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgList2PLGReport;

/**
 * defines the methods for BudgetConstructionAccountFundingDetailReports
 */
public interface BudgetConstructionList2PLGReportService {

    /**
     * updates account FundingDetail table.
     * 
     * @param principalName
     */
    public void updateList2PLGReport(String principalName, Integer universityFiscalYear);

    
    /**
     * 
     * builds BudgetConstructionAccountFundingDetailReports
     * 
     * @param universityFiscalYear
     * @param accountFundingDetailList
     */
    public Collection<BudgetConstructionOrgList2PLGReport> buildReports(Integer universityFiscalYear, String principalName);
    
}

