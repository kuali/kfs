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
package org.kuali.kfs.module.cg.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;

/**
 * Contracts Grants Report Data Holder object.
 */
public class ContractsGrantsReportDataHolder {
    private List details;

    private Map<String, Object> reportData;
    private List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria;
    private String reportTitle;

    public final static String KEY_OF_DETAILS_ENTRY = "details";
    public final static String KEY_OF_SEARCH_CRITERIA = "searchCriteria";

    public ContractsGrantsReportDataHolder() {
        this.reportData = new HashMap<String, Object>();
        this.details = new ArrayList();
        this.searchCriteria = new ArrayList<ContractsGrantsReportSearchCriteriaDataHolder>();
    }

    public List getDetails() {
        return details;
    }

    public void setDetails(List details) {
        this.details = details;
    }

    public Map<String, Object> getReportData() {

        reportData.put(KEY_OF_DETAILS_ENTRY, details);
        reportData.put(KEY_OF_SEARCH_CRITERIA, searchCriteria);
        reportData.put(KFSConstants.REPORT_TITLE, reportTitle);

        return reportData;
    }

    public void setReportData(Map<String, Object> reportData) {
        this.reportData = reportData;
    }

    public List<ContractsGrantsReportSearchCriteriaDataHolder> getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }
}
