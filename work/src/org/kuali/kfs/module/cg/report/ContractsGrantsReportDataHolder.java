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
