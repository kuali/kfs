/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.ui;

import java.util.List;

import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * This class defines the list that contains the Contracts Grants Invoice On Demand Results.
 */
public class ContractsGrantsInvoiceOnDemandResultRow extends ResultRow {

    private List<ResultRow> subResultRows;

    /**
     * @param columns
     * @param subResultRows
     * @param returnUrl
     * @param actionUrls
     */
    public ContractsGrantsInvoiceOnDemandResultRow(List<Column> columns, List<ResultRow> subResultRows, String returnUrl, String actionUrls) {
        super(columns, returnUrl, actionUrls);
        this.subResultRows = subResultRows;
    }

    /**
     * @return subResultRows
     */
    public List<ResultRow> getSubResultRows() {
        return subResultRows;
    }

    /**
     * @param subResultRows
     */
    public void setSubResultRows(List<ResultRow> subResultRows) {
        this.subResultRows = subResultRows;
    }
}