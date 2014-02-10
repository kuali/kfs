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
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsInvoiceResultRow;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Action class for Contracts Grants Invoice Lookup.
 */
public class ContractsGrantsInvoiceLookupAction extends KualiMultipleValueLookupAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceLookupAction.class);

    /**
     * Uses the initiate permission for the CINV doc to check if authorized
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, ArConstants.CGIN_DOCUMENT_TYPE);

        if (!SpringContext.getBean(IdentityManagementService.class).isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, null)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, ArConstants.CGIN_DOCUMENT_TYPE);
        }
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
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }

        Map<String, String> selectedObjectIds = new HashMap<String, String>();

        for (ResultRow row : resultTable) {

            // actual object ids are on sub result rows, not on parent rows
            if (row instanceof ContractsGrantsInvoiceResultRow) {
                for (ResultRow subResultRow : ((ContractsGrantsInvoiceResultRow) row).getSubResultRows()) {
                    String objId = subResultRow.getObjectId();
                    selectedObjectIds.put(objId, objId);
                }
            }
            else {
                String objId = row.getObjectId();
                selectedObjectIds.put(objId, objId);
            }
        }

        multipleValueLookupForm.jumpToPage(multipleValueLookupForm.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        multipleValueLookupForm.setCompositeObjectIdMap(selectedObjectIds);

        return resultTable;
    }

    /**
     * This method does the processing necessary to return selected results and sends a redirect back to the lookup caller
     *
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward prepareToReturnSelectedResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        if (StringUtils.isBlank(multipleValueLookupForm.getLookupResultsSequenceNumber())) {
            // no search was executed
            return prepareToReturnNone(mapping, form, request, response);
        }

        Map<String, String> compositeObjectIdMap = LookupUtils.generateCompositeSelectedObjectIds(multipleValueLookupForm.getPreviouslySelectedObjectIdSet(), multipleValueLookupForm.getDisplayedObjectIdSet(), multipleValueLookupForm.getSelectedObjectIdSet());
        Set<String> compositeObjectIds = compositeObjectIdMap.keySet();

        // The results need to be validated to check if there is at-least one value selected

        boolean success = false;
        if (!compositeObjectIds.isEmpty()) {
            success = true;
        }


        if (success) {

            prepareToReturnSelectedResultBOs(multipleValueLookupForm);

            // build the parameters for the refresh url
            Properties parameters = new Properties();
            parameters.put(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, multipleValueLookupForm.getLookupResultsSequenceNumber());
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "viewSummary");

            String contractsGrantsInvoiceSummaryUrl = UrlFactory.parameterizeUrl("arContractsGrantsInvoiceSummary.do", parameters);
            return new ActionForward(contractsGrantsInvoiceSummaryUrl, true);

        }
        else {
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
    }
}
