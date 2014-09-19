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
package org.kuali.kfs.module.ar.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsLookupResultRow;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * Action class for Contracts Grants Invoice Lookup.
 */
public class ContractsGrantsInvoiceLookupAction extends ContractsGrantsMultipleValueLookupAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceLookupAction.class);

    /**
     * Collects from the given resultTable - collecting ids from children rows for ContractsGrantsInvoiceResultRows
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsMultipleValueLookupAction#collectSelectedObjectIds(java.util.List)
     */
    @Override
    protected Map<String, String> collectSelectedObjectIds(List<ResultRow> resultTable) {
        Map<String, String> selectedObjectIds = new HashMap<String, String>();

        for (ResultRow row : resultTable) {
            // actual object ids are on sub result rows, not on parent rows
            if (row instanceof ContractsGrantsLookupResultRow) {
                for (ResultRow subResultRow : ((ContractsGrantsLookupResultRow) row).getSubResultRows()) {
                    String objId = subResultRow.getObjectId();
                    selectedObjectIds.put(objId, objId);
                }
            }
            else {
                String objId = row.getObjectId();
                selectedObjectIds.put(objId, objId);
            }
        }

        return selectedObjectIds;
    }

    /**
     * Returns "arContractsGrantsInvoiceSummary.do"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsMultipleValueLookupAction#getActionUrl()
     */
    @Override
    protected String getActionUrl() {
        return ArConstants.MultipleValueReturnActions.CONTRACTS_GRANTS_INVOICES;
    }
}
