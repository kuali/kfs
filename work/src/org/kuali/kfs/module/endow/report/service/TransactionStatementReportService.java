/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report.service;

import java.util.List;

import org.kuali.kfs.module.endow.report.util.TransactionStatementReportDataHolder;

public interface TransactionStatementReportService extends EndowmentReportService {

    /**
     * Gets the trial balance data using selected kemids
     * 
     * @param kemids
     * @param beginningDate
     * @param endigDate
     * @param endownmentOption
     * @param closedIndicator
     * @return
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportsByKemidByIds(List<String> kemids, String beginningDate, String endigDate, String endownmentOption, String closedIndicator);
    
    /**
     * Gets the trial balance data for all kemids
     * 
     * @param beginningDate
     * @param endigDate
     * @param endownmentOption
     * @param closedIndicator
     * @return
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportForAllKemids(String beginningDate, String endigDate, String endownmentOption, String closedIndicator);
        
    /**
     * Gets the trial balance data using selected criteria 
     * 
     * @param benefittingOrganziationCampuses
     * @param benefittingOrganziationCharts
     * @param benefittingOrganziations
     * @param typeCodes
     * @param purposeCodes
     * @param combineGroupCodes
     * @param beginningDate
     * @param endigDate
     * @param endowmnetOption
     * @param closedIndicator
     * @return
     */
    public List<TransactionStatementReportDataHolder> getTransactionStatementReportsByOtherCriteria( 
            List<String> benefittingOrganziationCampuses, 
            List<String> benefittingOrganziationCharts,
            List<String> benefittingOrganziations, 
            List<String> typeCodes, 
            List<String> purposeCodes, 
            List<String> combineGroupCodes, 
            String beginningDate, 
            String endigDate,
            String endowmnetOption,
            String closedIndicator);
}
