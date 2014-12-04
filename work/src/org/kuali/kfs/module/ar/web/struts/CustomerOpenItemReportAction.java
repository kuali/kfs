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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerOpenItemReportLookupableHelperServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;


/**
 * This class handles Actions for lookup flow
 */

public class CustomerOpenItemReportAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerOpenItemReportAction.class);
    private static final String TOTALS_TABLE_KEY = "totalsTable";

    /**
     * Search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     *
     * KRAD Conversion: Lookupable performs customization of the results.
     *
     * Fields are in data dictionary.
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerOpenItemReportForm lookupForm = (CustomerOpenItemReportForm) form;

        Lookupable lookupable = lookupForm.getLookupable();

        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        try {
            displayList = lookupable.performLookup(lookupForm, resultTable, true);
            Long totalSize = ((CollectionIncomplete) displayList).getActualSizeIfTruncated();

            request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, totalSize);
            request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, resultTable);

            if (request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY) != null) {
                GlobalVariables.getUserSession().removeObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
            }

            request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(resultTable));
        } catch (NumberFormatException e) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * View results from balance inquiry action
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(KFSConstants.SEARCH_LIST_REQUEST_KEY, request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, GlobalVariables.getUserSession().retrieveObject(request.getParameter(KFSConstants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, request.getParameter(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE));

        // TODO: use inheritance instead of this if statement
        if (((CustomerOpenItemReportForm) form).getLookupable().getLookupableHelperService() instanceof CustomerOpenItemReportLookupableHelperServiceImpl) {
            Object totalsTable = GlobalVariables.getUserSession().retrieveObject(TOTALS_TABLE_KEY);
            request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Overridden to add profilePrincipalId; some day, hopefully TEM can do this itself
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#getRoleQualification(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected Map<String, String> getRoleQualification(ActionForm form, String methodToCall) {
        Map<String, String> qualification =  super.getRoleQualification(form, methodToCall);
        final String currentUserPrincipalId = GlobalVariables.getUserSession().getPrincipalId();
        qualification.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, currentUserPrincipalId);
        qualification.put(KimConstants.AttributeConstants.PRINCIPAL_ID, currentUserPrincipalId);
        return qualification;
    }
}
