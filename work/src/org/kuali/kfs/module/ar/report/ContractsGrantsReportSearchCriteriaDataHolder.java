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

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Defines a data holder object for report services for Contracts Grants Report Search Criteria.
 */
public class ContractsGrantsReportSearchCriteriaDataHolder {

    private String searchFieldLabel;
    private String searchFieldValue;

    /**
     * @return searchFieldLabel
     */
    public String getSearchFieldLabel() {
        return searchFieldLabel;
    }

    /**
     * @param searchFieldLabel
     */
    public void setSearchFieldLabel(String searchFieldLabel) {
        this.searchFieldLabel = searchFieldLabel;
    }

    /**
     * @return searchFieldValue
     */
    public String getSearchFieldValue() {
        return searchFieldValue;
    }

    /**
     * @param searchFieldValue
     */
    public void setSearchFieldValue(String searchFieldValue) {
        this.searchFieldValue = searchFieldValue;
    }


}
