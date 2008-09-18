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
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetGlobalMaintainableImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class GlLineAction extends KualiAction {
    public ActionForward createAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String glAcctId = request.getParameter("generalLedgerAccountIdentifier");
        Long cabGlEntryId = Long.valueOf(glAcctId);
        BusinessObjectService bosService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Long> pkeys = new HashMap<String, Long>();
        pkeys.put("generalLedgerAccountIdentifier", cabGlEntryId);
        GeneralLedgerEntry entry = (GeneralLedgerEntry) bosService.findByPrimaryKey(GeneralLedgerEntry.class, pkeys);
        DocumentService documentService = KNSServiceLocator.getDocumentService();
        MaintenanceDocument maintDoc = (MaintenanceDocument) documentService.getNewDocument("AssetGlobalMaintenanceDocument");
        maintDoc.getNewMaintainableObject().setMaintenanceAction("New");
        maintDoc.getOldMaintainableObject().setMaintenanceAction("New");
        maintDoc.getDocumentHeader().setDocumentDescription("CAB Submitted transaction for GL Id " + glAcctId);
        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setOrganizationOwnerChartOfAccountsCode(entry.getChartOfAccountsCode());
        assetGlobal.setOrganizationOwnerAccountNumber(entry.getAccountNumber());
        assetGlobal.setDocumentNumber(maintDoc.getDocumentNumber());
        AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail();
        // populate AssetPaymentDetail with AssetPayment data
        assetPaymentDetail.setSequenceNumber(1);
        assetPaymentDetail.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        assetPaymentDetail.setAccountNumber(entry.getAccountNumber());
        assetPaymentDetail.setSubAccountNumber(entry.getSubAccountNumber());
        assetPaymentDetail.setFinancialObjectCode(entry.getFinancialObjectCode());
        assetPaymentDetail.setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        assetPaymentDetail.setProjectCode(entry.getProjectCode());
        assetPaymentDetail.setOrganizationReferenceId(entry.getOrganizationReferenceId());
        assetPaymentDetail.setExpenditureFinancialDocumentNumber(entry.getDocumentNumber());
        assetPaymentDetail.setExpenditureFinancialDocumentPostedDate(entry.getTransactionPostingDate());
        assetPaymentDetail.setPostingYear(entry.getUniversityFiscalYear());
        assetPaymentDetail.setPostingPeriodCode(entry.getUniversityFiscalPeriodCode());
        assetPaymentDetail.setAmount(KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) ? entry.getTransactionLedgerEntryAmount().multiply(new KualiDecimal(-1)) : entry.getTransactionLedgerEntryAmount());
        assetGlobal.getAssetPaymentDetails().add(assetPaymentDetail);
        maintDoc.getOldMaintainableObject().setBusinessObject((PersistableBusinessObject) ObjectUtils.deepCopy(assetGlobal));
        maintDoc.getOldMaintainableObject().setBoClass(assetGlobal.getClass());
        maintDoc.getNewMaintainableObject().setBusinessObject(assetGlobal);
        maintDoc.getNewMaintainableObject().setBoClass(assetGlobal.getClass());
        documentService.saveDocument(maintDoc);

        DocumentTypeService documentTypeService = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
        DocumentType documentType = documentTypeService.findByName("AssetGlobalMaintenanceDocument");
        String docHandler = documentType.getDocHandlerUrl();
        if (docHandler.indexOf("?") == -1) {
            docHandler += "?";
        }

        docHandler += "&docId=" + maintDoc.getDocumentNumber() + "&command=displayDocSearchView";
        return new ActionForward(docHandler, true);
    }

    public ActionForward createPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("portal");
    }
}
