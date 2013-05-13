/*
 * Copyright 2011 The Kuali Foundation.
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