/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;

/**
 * Defines a data holder class for all Contracts and Grants Reports. *
 */
public class ContractsGrantsReportDataHolder {

    private List details;
    private Map<String, Object> reportData;
    private List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria;
    private String reportTitle;

    public final static String KEY_OF_DETAILS_ENTRY = "details";
    public final static String KEY_OF_SEARCH_CRITERIA = "searchCriteria";

    /**
     * Default Constructor.
     */
    public ContractsGrantsReportDataHolder() {
        this.reportData = new HashMap<String, Object>();
        this.details = new ArrayList();
        this.searchCriteria = new ArrayList<ContractsGrantsReportSearchCriteriaDataHolder>();
    }


    /**
     * Sets the details attribute value.
     * 
     * @param details The details to set.
     */
    public void setDetails(List details) {
        this.details = details;
    }

    /**
     * Gets the details attribute.
     * 
     * @return Returns the details.
     */
    public List getDetails() {
        return details;
    }

    /**
     * Sets the reportData attribute value.
     * 
     * @param reportData The reportData to set.
     */
    public void setReportData(Map<String, Object> reportData) {
        this.reportData = reportData;
    }

    /**
     * Gets the reportData attribute.
     * 
     * @return Returns the reportData.
     */
    public Map<String, Object> getReportData() {

        reportData.put(KEY_OF_DETAILS_ENTRY, details);
        reportData.put(KEY_OF_SEARCH_CRITERIA, searchCriteria);
        reportData.put(KFSConstants.REPORT_TITLE, reportTitle);

        return reportData;
    }

    /**
     * Gets the searchCriteria attribute.
     * 
     * @return Returns the searchCriteria.
     */
    public List<ContractsGrantsReportSearchCriteriaDataHolder> getSearchCriteria() {
        return searchCriteria;
    }

    /**
     * Sets the searchCriteria attribute value.
     * 
     * @param searchCriteria The searchCriteria to set.
     */
    public void setSearchCriteria(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /**
     * Gets the reportTitle attribute.
     * 
     * @return Returns the reportTitle.
     */
    public String getReportTitle() {
        return reportTitle;
    }

    /**
     * Sets the reportTitle attribute value.
     * 
     * @param reportTitle The reportTitle to set.
     */
    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

}
