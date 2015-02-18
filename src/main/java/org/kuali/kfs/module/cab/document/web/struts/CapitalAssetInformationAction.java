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
package org.kuali.kfs.module.cab.document.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.document.service.GlAndPurApHelperService;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Struts action class that handles Capital Asset Information Screen actions
 */
public class CapitalAssetInformationAction extends CabActionBase {

    /**
     * Action "process" from CAB GL Lookup screen is processed by this method
     *
     * @param mapping {@link ActionMapping}
     * @param form {@link ActionForm}
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link ActionForward}
     * @throws Exception
     */
    public ActionForward process(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAssetInformationForm capitalAssetForm = (CapitalAssetInformationForm) form;
        String glAcctId = request.getParameter(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER);
        Long cabGlEntryId = Long.valueOf(glAcctId);
        capitalAssetForm.setGeneralLedgerAccountIdentifier(cabGlEntryId);

        GeneralLedgerEntry entry = findGeneralLedgerEntry(request);
        if (ObjectUtils.isNotNull(entry)) {
            prepareRecordsForDisplay(capitalAssetForm, entry);
        }
      //  if (!entry.isActive()) {
      //      KNSGlobalVariables.getMessageList().add(CabKeyConstants.WARNING_GL_PROCESSED);
      //  }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected void prepareRecordsForDisplay(CapitalAssetInformationForm capitalAssetForm, GeneralLedgerEntry entry) {
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);

        entry.setSelected(true);
        capitalAssetForm.setGeneralLedgerEntry(entry);
        capitalAssetForm.setPrimaryGlAccountId(entry.getGeneralLedgerAccountIdentifier());
        List<CapitalAssetInformation> capitalAssetInformation = glLineService.findCapitalAssetInformationForGLLine(entry);

        // KFSMI-9881
        // For GL Entries without capital asset information (ex: loaded by enterprise feed or Vendor Credit Memo),
        // we need to create that information (at least a shell record) for the Capital Asset Information screen
        // to render and subsequent processing to occur successfully.
        if (capitalAssetInformation.isEmpty()) {
            glLineService.setupCapitalAssetInformation(entry);
            capitalAssetInformation = glLineService.findCapitalAssetInformationForGLLine(entry);
        }

        capitalAssetForm.setCapitalAssetInformation(capitalAssetInformation);
    }

    /**
     * Finds GL entry using the key from request
     *
     * @param request HttpServletRequest
     * @return GeneralLedgerEntry
     */
    protected GeneralLedgerEntry findGeneralLedgerEntry(HttpServletRequest request) {
        String glAcctId = request.getParameter(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER);
        Long cabGlEntryId = Long.valueOf(glAcctId);
        return findGeneralLedgerEntry(cabGlEntryId, false);
    }

    /**
     * Retrieves the CAB General Ledger Entry from DB
     *
     * @param generalLedgerEntryId Entry Id
     * @return GeneralLedgerEntry
     */
    protected GeneralLedgerEntry findGeneralLedgerEntry(Long generalLedgerEntryId, boolean requireNew) {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Object> pkeys = new HashMap<String, Object>();
        pkeys.put(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER, generalLedgerEntryId);
        if (requireNew) {
            pkeys.put(CabPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE, CabConstants.ActivityStatusCode.NEW);
        }
        GeneralLedgerEntry entry = boService.findByPrimaryKey(GeneralLedgerEntry.class, pkeys);
        return entry;
    }

    /**
     * Cancels the action and returns to portal main page
     *
     * @param mapping {@link ActionMapping}
     * @param form {@link ActionForm}
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link ActionForward}
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KRADConstants.MAPPING_PORTAL);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#showAllTabs(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward showAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAssetInformationForm capitalAssetForm = (CapitalAssetInformationForm) form;
        GeneralLedgerEntry generalLedgerEntry = capitalAssetForm.getGeneralLedgerEntry();
        generalLedgerEntry.setSelected(true);

        return super.showAllTabs(mapping, form, request, response);
    }

    /**
     * reloads the capital asset information screen
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAssetInformationForm capitalAssetForm = (CapitalAssetInformationForm) form;

        GeneralLedgerEntry entry = capitalAssetForm.getGeneralLedgerEntry();

     //   GeneralLedgerEntry entry = findGeneralLedgerEntry(request);
     //   if (entry != null) {
            prepareRecordsForDisplay(capitalAssetForm, entry);
      //  }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected GlAndPurApHelperService getGlAndPurApHelperService() {
        return SpringContext.getBean(GlAndPurApHelperService.class);
    }
}
