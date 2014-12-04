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
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Struts action class that handles GL Line Processing Screen actions
 */
public class GlLineAction extends CabActionBase {

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
        GlLineForm glLineForm = (GlLineForm) form;
        String glAcctId = request.getParameter(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER);
        Long cabGlEntryId = Long.valueOf(glAcctId);

        GeneralLedgerEntry entry = findGeneralLedgerEntry(request);


        String assetLineNumber = request.getParameter(CabPropertyConstants.CapitalAssetInformation.ASSET_LINE_NUMBER);
        Integer capitalAssetLineNumber = Integer.valueOf(assetLineNumber);
        glLineForm.setCapitalAssetLineNumber(capitalAssetLineNumber);

        if (ObjectUtils.isNotNull(entry)) {
            prepareRecordsForDisplay(glLineForm, entry, capitalAssetLineNumber);
        }

        glLineForm.setGeneralLedgerEntry(entry);
     //   if (!entry.isActive()) {
     //       KNSGlobalVariables.getMessageList().add(CabKeyConstants.WARNING_GL_PROCESSED);
     //   }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    private void prepareRecordsForDisplay(GlLineForm glLineForm, GeneralLedgerEntry entry, Integer capitalAssetLineNumber) {
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);
        entry.setSelected(true);
        glLineForm.setGeneralLedgerEntry(entry);
        glLineForm.setPrimaryGlAccountId(entry.getGeneralLedgerAccountIdentifier());

        CapitalAssetInformation capitalAssetInformation = glLineService.findCapitalAssetInformation(entry.getDocumentNumber(), capitalAssetLineNumber);
        glLineForm.setCapitalAssetInformation(capitalAssetInformation);
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
     * Action "Create Assets" from CAB GL Detail Selection screen is processed by this method. This will initiate an asset global
     * document and redirect the user to document edit page.
     *
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward submitAssetGlobal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineForm glLineForm = (GlLineForm) form;
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);
        GeneralLedgerEntry defaultGeneralLedgerEntry = findGeneralLedgerEntry(glLineForm.getPrimaryGlAccountId(), true);
        defaultGeneralLedgerEntry.setSelected(true);

        Integer capitalAssetLineNumber = glLineForm.getCapitalAssetLineNumber();

        // set the default as the first entry in the list if it's null
        if (ObjectUtils.isNull(defaultGeneralLedgerEntry)) {
            form.reset(mapping, request);
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }

        Document maintDoc = glLineService.createAssetGlobalDocument(defaultGeneralLedgerEntry, capitalAssetLineNumber);

        return new ActionForward(getGlAndPurApHelperService().getDocHandlerUrl(maintDoc.getDocumentNumber(), DocumentTypeName.ASSET_ADD_GLOBAL), true);
    }

    /**
     * Action "Create Payments" from CAB GL Detail Selection screen is processed by this method. This will initiate an asset payment
     * global document and redirect the user to document edit page.
     *
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward submitPaymentGlobal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);
        GlLineForm glLineForm = (GlLineForm) form;
        GeneralLedgerEntry defaultGeneralLedgerEntry = findGeneralLedgerEntry(glLineForm.getPrimaryGlAccountId(), true);

        // set the default as the first entry in the list if it's null
        if (ObjectUtils.isNull(defaultGeneralLedgerEntry)) {
            form.reset(mapping, request);
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }

        Integer capitalAssetLineNumber = glLineForm.getCapitalAssetLineNumber();
        Document document = glLineService.createAssetPaymentDocument(defaultGeneralLedgerEntry, capitalAssetLineNumber);

        return new ActionForward(getGlAndPurApHelperService().getDocHandlerUrl(document.getDocumentNumber(), DocumentTypeName.ASSET_PAYMENT), true);
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
        GlLineForm glLineForm = (GlLineForm) form;
        GeneralLedgerEntry generalLedgerEntry = glLineForm.getGeneralLedgerEntry();
        generalLedgerEntry.setSelected(true);

        return super.showAllTabs(mapping, form, request, response);
    }

    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineForm glLineForm = (GlLineForm) form;

        Integer capitalAssetLineNumber = glLineForm.getCapitalAssetLineNumber();

        GeneralLedgerEntry entry = glLineForm.getGeneralLedgerEntry();

        if (entry != null) {
            prepareRecordsForDisplay(glLineForm, entry, capitalAssetLineNumber);
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected GlAndPurApHelperService getGlAndPurApHelperService() {
        return SpringContext.getBean(GlAndPurApHelperService.class);
    }
}
