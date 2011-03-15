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

import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder;

public interface TransactionSummaryReportService extends EndowmentReportService {

    /**
     * Gets the transaction summary data using selected kemids

     * @param kemids
     * @param beginningDate
     * @param endigDate
     * @param endownmentOption
     * @param closedIndicator
     * @param reportOption
     * @return List<TransactionSummaryReportDataHolder>
     */
    public List<TransactionSummaryReportDataHolder> getTransactionSummaryReportsByKemidByIds(List<String> kemids, String beginningDate, String endigDate, String endownmentOption, String closedIndicator, String reportOption);
    
    /**
     * Gets the transaction summary data for all kemids
     * 
     * @param beginningDate
     * @param endigDate
     * @param endownmentOption
     * @param closedIndicator
     * @param reportOption
     * @return List<TransactionSummaryReportDataHolder>
     */
    public List<TransactionSummaryReportDataHolder> getTransactionSummaryReportForAllKemids(String beginningDate, String endigDate, String endownmentOption, String closedIndicator, String reportOption);
        
    /**
     * Gets the transaction summary data using selected criteria 
     * 
     * @param benefittingOrganziationCampuses
     * @param benefittingOrganziationCharts
     * @param benefittingOrganziations
     * @param typeCodes
     * @param purposeCodes
     * @param combineGroupCodes
     * @param endowmnetOption
     * @param closedIndicator
     * @param reportOption
     * @return List<TransactionSummaryReportDataHolder>
     */
    public List<TransactionSummaryReportDataHolder> getTransactionSummaryReportsByOtherCriteria( 
            List<String> benefittingOrganziationCampuses, 
            List<String> benefittingOrganziationCharts,
            List<String> benefittingOrganziations, 
            List<String> typeCodes, 
            List<String> purposeCodes, 
            List<String> combineGroupCodes, 
            String beginningDate, 
            String endigDate,
            String endowmnetOption,
            String closedIndicator, String reportOption);
}
