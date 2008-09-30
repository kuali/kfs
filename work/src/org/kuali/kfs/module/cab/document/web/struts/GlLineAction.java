/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cab.document.web.struts;

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
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class GlLineAction extends KualiAction {

    // TODO this can be removed later since we removed direct link from lookup screen
    public ActionForward createAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Find out the GL Entry
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);
        GeneralLedgerEntry entry = findGeneralLedgerEntry(request);
        Document maintDoc = glLineService.createAssetGlobalDocument(entry);
        return new ActionForward(prepareDocHandlerUrl(maintDoc, CabConstants.ASSET_GLOBAL_MAINTENANCE_DOCUMENT), true);
    }

    /**
     * Action "process" from CAB GL Lookup screen is processed by this method
     * 
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward process(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);
        GeneralLedgerEntry entry = findGeneralLedgerEntry(request);
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("documentNumber", entry.getDocumentNumber());
        fieldValues.put("active", true);
        Collection<GeneralLedgerEntry> entries = boService.findMatchingOrderBy(GeneralLedgerEntry.class, fieldValues, "generalLedgerAccountIdentifier", true);
        List<GeneralLedgerEntry> list = new ArrayList<GeneralLedgerEntry>();
        list.addAll(entries);
        for (GeneralLedgerEntry generalLedgerEntry : list) {
            if (generalLedgerEntry.getGeneralLedgerAccountIdentifier().equals(entry.getGeneralLedgerAccountIdentifier())) {
                generalLedgerEntry.setSelected(true);
            }
        }
        GlLineForm glLineForm = (GlLineForm) form;
        glLineForm.setRelatedGlEntries(list);
        glLineForm.setPrimaryGlAccountId(entry.getGeneralLedgerAccountIdentifier());
        glLineForm.setListSize(list.size());
        CapitalAssetInformation capitalAssetInformation = glLineService.findCapitalAssetInformation(entry);
        glLineForm.setCapitalAssetInformation(capitalAssetInformation);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected GeneralLedgerEntry findGeneralLedgerEntry(HttpServletRequest request) {
        String glAcctId = request.getParameter(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER);
        Long cabGlEntryId = Long.valueOf(glAcctId);
        return findGeneralLedgerEntry(cabGlEntryId);
    }

    protected String prepareDocHandlerUrl(Document maintDoc, String docTypeName) {
        DocumentTypeService documentTypeService = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
        DocumentType documentType = documentTypeService.findByName(docTypeName);
        String docHandler = documentType.getDocHandlerUrl();
        if (docHandler.indexOf("?") == -1) {
            docHandler += "?";
        }
        else {
            docHandler += "&";
        }
        docHandler += "docId=" + maintDoc.getDocumentNumber() + "&command=displayDocSearchView";
        return docHandler;
    }

    // TODO this can be removed because we dont directly create payment from CAB GL lookup screen
    public ActionForward createPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Find out the GL Entry
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);
        GeneralLedgerEntry entry = findGeneralLedgerEntry(request);
        // initiate a new document
        Document document = glLineService.createAssetPaymentDocument(entry);
        return new ActionForward(prepareDocHandlerUrl(document, CabConstants.ASSET_PAYMENT_DOCUMENT), true);
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
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);
        GlLineForm glLineForm = (GlLineForm) form;
        List<GeneralLedgerEntry> submitList = new ArrayList<GeneralLedgerEntry>();
        // add the default one
        GeneralLedgerEntry defaultGeneralLedgerEntry = findGeneralLedgerEntry(glLineForm.getPrimaryGlAccountId());
        defaultGeneralLedgerEntry.setSelected(true);
        submitList.add(defaultGeneralLedgerEntry);
        List<GeneralLedgerEntry> relatedGlEntries = glLineForm.getRelatedGlEntries();

        for (GeneralLedgerEntry generalLedgerEntry : relatedGlEntries) {
            if (generalLedgerEntry.isSelected()) {
                GeneralLedgerEntry entry = findGeneralLedgerEntry(generalLedgerEntry.getGeneralLedgerAccountIdentifier());
                if (entry != null && entry.isActive()) {
                    submitList.add(entry);
                }
            }
        }
        if (submitList.isEmpty()) {
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
        Document maintDoc = glLineService.createAssetGlobalDocument(submitList, defaultGeneralLedgerEntry);
        return new ActionForward(prepareDocHandlerUrl(maintDoc, CabConstants.ASSET_GLOBAL_MAINTENANCE_DOCUMENT), true);
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
        List<GeneralLedgerEntry> submitList = new ArrayList<GeneralLedgerEntry>();
        // add the default one
        GeneralLedgerEntry defaultGeneralLedgerEntry = findGeneralLedgerEntry(glLineForm.getPrimaryGlAccountId());
        defaultGeneralLedgerEntry.setSelected(true);
        submitList.add(defaultGeneralLedgerEntry);
        List<GeneralLedgerEntry> relatedGlEntries = glLineForm.getRelatedGlEntries();

        for (GeneralLedgerEntry generalLedgerEntry : relatedGlEntries) {
            if (generalLedgerEntry.isSelected()) {
                GeneralLedgerEntry entry = findGeneralLedgerEntry(generalLedgerEntry.getGeneralLedgerAccountIdentifier());
                if (entry != null && entry.isActive()) {
                    submitList.add(entry);
                }
            }
        }
        if (submitList.isEmpty()) {
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
        Document document = glLineService.createAssetPaymentDocument(submitList, defaultGeneralLedgerEntry);
        return new ActionForward(prepareDocHandlerUrl(document, CabConstants.ASSET_PAYMENT_DOCUMENT), true);
    }

    /**
     * This method will process the view document request by clicking on a specific document.
     * 
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ActionForward
     * @throws Exception
     */
    // TODO this can be removed later if there is a core Document Search/View service that does the same
    public ActionForward viewDoc(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String documentId = request.getParameter("documentNumber");
        DocumentTypeService documentTypeService = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
        DocumentType documentType = documentTypeService.findByDocumentId(Long.valueOf(documentId));
        String docHandler = documentType.getDocHandlerUrl();
        if (docHandler.indexOf("?") == -1) {
            docHandler += "?";
        }
        else {
            docHandler += "&";
        }
        docHandler += "docId=" + documentId + "&command=displayDocSearchView";
        return new ActionForward(docHandler, true);
    }

    /**
     * Retrieves the CAB General Ledger Entry from DB
     * 
     * @param generalLedgerEntryId Entry Id
     * @return GeneralLedgerEntry
     */
    protected GeneralLedgerEntry findGeneralLedgerEntry(Long generalLedgerEntryId) {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Object> pkeys = new HashMap<String, Object>();
        pkeys.put(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER, generalLedgerEntryId);
        pkeys.put(CabPropertyConstants.GeneralLedgerEntry.ACTIVE, true);
        GeneralLedgerEntry entry = (GeneralLedgerEntry) boService.findByPrimaryKey(GeneralLedgerEntry.class, pkeys);
        return entry;
    }

    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KNSConstants.MAPPING_PORTAL);
    }

    @Override
    public ActionForward showAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlLineForm glLineForm = (GlLineForm) form;
        List<GeneralLedgerEntry> relatedGlEntries = glLineForm.getRelatedGlEntries();
        for (GeneralLedgerEntry generalLedgerEntry : relatedGlEntries) {
            if (generalLedgerEntry.getGeneralLedgerAccountIdentifier().equals(glLineForm.getPrimaryGlAccountId())) {
                generalLedgerEntry.setSelected(true);
            }
        }
        return super.showAllTabs(mapping, form, request, response);
    }
}
