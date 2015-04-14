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
package org.kuali.kfs.module.ld.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerEntryGLSummary;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Action which will perform lookup and presentation of LD entries related to a GL entry
 */
public class GLLaborEntrySummarizationInquiryAction extends KualiAction {
    protected volatile static DataDictionaryService dataDictionaryService;

    /**
     * Uses the inquire permission for the labor ledger entry to check if authorized
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (!SpringContext.getBean(IdentityManagementService.class).isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, getNamespaceAndComponentSimpleName(LedgerEntry.class), null)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), "inquire", LedgerEntry.class.getSimpleName());
        }
    }
    
    protected Map<String,String> getNamespaceAndComponentSimpleName( Class<? extends Object> clazz) {
        Map<String,String> attributeSet = new HashMap<String,String>();
        attributeSet.put(KimConstants.AttributeConstants.NAMESPACE_CODE, getKualiModuleService().getNamespaceCode(clazz));
        attributeSet.put(KimConstants.AttributeConstants.COMPONENT_NAME, getKualiModuleService().getComponentCode(clazz));
        return attributeSet;
    }

    /**
     * Method which performs search for matching LaborEntry records
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final BusinessObjectEntry entry = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(LedgerEntryGLSummary.class.getSimpleName());
        final String lookupId = entry.getLookupDefinition().getLookupableID();
        Lookupable lookupable = (Lookupable)SpringContext.getService(lookupId);
        lookupable.setBusinessObjectClass(LedgerEntryGLSummary.class);

        Collection displayList = new ArrayList();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();
        displayList = lookupable.performLookup((LookupForm)form, resultTable, true);

        ((GLLaborEntrySummarizationInquiryForm)form).setEntries(resultTable);
        ((GLLaborEntrySummarizationInquiryForm)form).buildInquiryUrls(lookupable.getLookupableHelperService());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @return the configured default implementation of the DataDictionaryService
     */
    public DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }
}
