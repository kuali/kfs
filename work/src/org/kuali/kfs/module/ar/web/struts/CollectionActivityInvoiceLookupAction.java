/*
 * Copyright 2012 The Kuali Foundation.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * Action class for Referral To Collections Lookup.
 */
public class CollectionActivityInvoiceLookupAction extends ContractsGrantsMultipleValueLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectionActivityInvoiceLookupAction.class);

    @Override
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.search(mapping, form, request, response);
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;

        this.selectAll(multipleValueLookupForm, getMaxRowsPerPage(multipleValueLookupForm));

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method performs the operations necessary for a multiple value lookup to select all of the results and rerender the page
     *
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    @Override
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.selectAll(multipleValueLookupForm, maxRowsPerPage);
        if (multipleValueLookupForm.getPreviouslySortedColumnIndex() != null) {
            multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        }
        return resultTable;
    }

    /**
     * Grabs the id of every row from the resultTable
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsMultipleValueLookupAction#collectSelectedObjectIds(java.util.List)
     */
    @Override
    protected Map<String, String> collectSelectedObjectIds(List<ResultRow> resultTable) {
        Map<String, String> selectedObjectIds = new HashMap<String, String>();

        for (ResultRow row : resultTable) {
            String objId = row.getObjectId();
            selectedObjectIds.put(objId, objId);
        }
        return selectedObjectIds;
    }

    /**
     * Returns "arContractsGrantsInvoiceSummary.do"
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsMultipleValueLookupAction#getActionUrl()
     */
    @Override
    protected String getActionUrl() {
        return "arContractsGrantsInvoiceSummary.do";
    }
}