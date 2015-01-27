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
package org.kuali.kfs.module.ar.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsLookupResultRow;

/**
 * Action class for Contracts & Grants Invoice Lookup.
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
