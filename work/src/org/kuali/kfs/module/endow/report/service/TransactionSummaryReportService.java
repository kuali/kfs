/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
