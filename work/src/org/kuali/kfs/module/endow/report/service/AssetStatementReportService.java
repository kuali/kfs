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

import org.kuali.kfs.module.endow.report.util.AssetStatementReportDataHolder;

public interface AssetStatementReportService extends EndowmentReportService {

        /**
         * Gets the trial balance data using selected kemids
         * 
         * @param kemids
         * @param monthEndDate
         * @param endownmentOption
         * @param reportOption
         * @param closedIndicator
         * @return
         */
        public List<AssetStatementReportDataHolder> getAssetStatementReportsByKemidByIds(List<String> kemids, String monthEndDate, String endownmentOption, String reportOption, String closedIndicator);
        
        /**
         * Gets the trial balance data for all kemids
         * 
         * @param endownmentOption
         * @return
         */
        public List<AssetStatementReportDataHolder> getAssetStatementReportForAllKemids(String monthEndDate, String endownmentOption, String reportOption, String closedIndicator);
            
        /**
         * Gets the trial balance data using selected criteria 
         * 
         * @param benefittingOrganziationCampuses
         * @param benefittingOrganziationCharts
         * @param benefittingOrganziations
         * @param typeCodes
         * @param purposeCodes
         * @param combineGroupCodes
         * @param monthEndDate
         * @param endowmnetOption
         * @param reportOption
         * @param closedIndicator
         * @return
         */
        public List<AssetStatementReportDataHolder> getAssetStatementReportsByOtherCriteria( 
                List<String> benefittingOrganziationCampuses, 
                List<String> benefittingOrganziationCharts,
                List<String> benefittingOrganziations, 
                List<String> typeCodes, 
                List<String> purposeCodes, 
                List<String> combineGroupCodes, 
                String monthEndDate, 
                String endowmnetOption,
                String reportOption, 
                String closedIndicator);
}
