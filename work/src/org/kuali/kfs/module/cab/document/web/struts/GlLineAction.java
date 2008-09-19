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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class GlLineAction extends KualiAction {
    /**
     * Action method that will create an AssetGlobal document and send to Cams module for asset creation
     * 
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ActionForward
     * @throws Exception
     * @see KualiAction#execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    public ActionForward createAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Find out the GL Entry
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);

        GeneralLedgerEntry entry = findGeneralLedgerEntry(request, boService);
        Document maintDoc = glLineService.createAssetGlobalDocument(entry);
        return new ActionForward(prepareDocHandlerUrl(maintDoc, CabConstants.ASSET_GLOBAL_MAINTENANCE_DOCUMENT), true);
    }

    private GeneralLedgerEntry findGeneralLedgerEntry(HttpServletRequest request, BusinessObjectService boService) {
        String glAcctId = request.getParameter(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER);
        Long cabGlEntryId = Long.valueOf(glAcctId);
        Map<String, Long> pkeys = new HashMap<String, Long>();
        pkeys.put(CabPropertyConstants.GeneralLedgerEntry.GENERAL_LEDGER_ACCOUNT_IDENTIFIER, cabGlEntryId);
        GeneralLedgerEntry entry = (GeneralLedgerEntry) boService.findByPrimaryKey(GeneralLedgerEntry.class, pkeys);
        return entry;
    }

    private String prepareDocHandlerUrl(Document maintDoc, String docTypeName) {
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


    public ActionForward createPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Find out the GL Entry
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        GlLineService glLineService = SpringContext.getBean(GlLineService.class);
        GeneralLedgerEntry entry = findGeneralLedgerEntry(request, boService);
        // initiate a new document
        Document document = glLineService.createAssetPaymentDocument(entry);
        return new ActionForward(prepareDocHandlerUrl(document, "AssetPaymentDocument"), true);
    }
}
