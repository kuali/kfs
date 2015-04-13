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
package org.kuali.kfs.module.ar.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;

/**
 * Defines a data holder class for all Contracts & Grants Reports. *
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
