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

import org.kuali.kfs.module.endow.report.util.EndowmentReportHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.KemidsWithMultipleBenefittingOrganizationsDataHolder;

public interface EndowmentReportService {
    
    /**
     * Gets the institution name, using the KFS parameter
     * See 8 Report Design in the specs
     * 
     * @return
     */
    public String getInstitutionName();
    
    /**
     * Gets the report name "Trial Balance"
     * 
     * @return
     */
    public String getReportRequestor();
    
    /**
     * Gets the campus names of selected campuses 
     * 
     * @param campuses
     * @return
     */
    public String getBenefittingCampuses(List<String> campuses);
    
    /**
     * Gets the chart names of selected charts
     * 
     * @param charts
     * @return
     */
    public String getBenefittingCharts(List<String> charts);
    
    /**
     * Gets the organization names of selected organizations
     * 
     * @param organizations
     * @return
     */
    public String getBenefittingOrganizations(List<String> organizations);
    
    /**
     * Gets the selected type codes   
     * 
     * @param typeCodes
     * @return
     */
    public String getKemidTypeCodes(List<String> typeCodes);
    
    /**
     * Gets the selected purpose codes
     * 
     * @param purposes
     * @return
     */
    public String getKemidPurposeCodes(List<String> purposes);
    
    /**
     * Gets the selected group codes
     * 
     * @param groupCodes
     * @return
     */
    public String getCombineGroupCodes(List<String> groupCodes);
    
    /**
     * Gets the KEMIDs with multiple benefitting organizations
     * 
     * @param kemids
     * @return
     */
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizations(List<String> kemids);
    
    /**
     * Creates the report header data
     * 
     * @param kemidsSelected
     * @param reportName
     * @param endowmnetOption
     * @param benefittingOrganziationCampuses
     * @param benefittingOrganziationCharts
     * @param benefittingOrganziations
     * @param typeCodes
     * @param purposeCodes
     * @param combineGroupCodes
     * @return
     */
    public EndowmentReportHeaderDataHolder createReportHeaderSheetData(
            List<String> kemidsSelected,             
            List<String> benefittingOrganziationCampuses,
            List<String> benefittingOrganziationCharts,
            List<String> benefittingOrganziations,
            List<String> typeCodes,
            List<String> purposeCodes,
            List<String> combineGroupCodes,
            String reportName, 
            String endowmnetOption,
            String reportOption);
    
    /**
     * method to pickup all kemids from the list of kemids where for each kemid, if
     * there is record in END_HIST_CSH_T table
     * 
     * @param kemids
     * @param beginningDate
     * @param endingDate
     * @return List<String> newKemids
     */
    public List<String> getKemidsInHistoryCash(List<String> kemids, String beginningDate, String endingDate);

    /**
     * method to pickup all kemids based on user selection of endowmentOption and closed indicator
     * 
     * @param kemids
     * @param endowmentOption
     * @param endingDate
     * @return List<String> newKemids
     */
    public List<String> getKemidsBasedOnUserSelection(List<String> kemids, String endowmentOption, String closedIndicator);    
}
