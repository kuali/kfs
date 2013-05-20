/*
 * Copyright 2006 The Kuali Foundation
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
