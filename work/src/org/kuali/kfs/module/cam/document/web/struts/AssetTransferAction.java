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
package org.kuali.module.cams.web.struts.action;

import static org.kuali.module.cams.CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.cams.web.struts.form.AssetTransferForm;

public class AssetTransferAction extends KualiTransactionalDocumentActionBase {
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward docHandlerForward = super.docHandler(mapping, form, request, response);
        AssetTransferForm assetTransferForm = (AssetTransferForm) form;
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) assetTransferForm.getDocument();
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        AssetHeader assetHeader = assetTransferDocument.getAssetHeader();
        Asset asset = assetTransferDocument.getAsset();

        asset = handleRequestFromLookup(request, assetTransferForm, assetTransferDocument, service, asset);

        if (assetTransferDocument.getAsset() != null && (assetTransferDocument.getAssetHeader() == null || assetHeader.getDocumentNumber() == null)) {
            assetHeader = new AssetHeader();
            assetHeader.setDocumentNumber(assetTransferDocument.getDocumentNumber());
            assetHeader.setCapitalAssetNumber(assetTransferDocument.getAsset().getCapitalAssetNumber());
            assetTransferDocument.setAssetHeader(assetHeader);
        }

        asset = handleRequestFromWorkflow(assetTransferForm, assetTransferDocument, service, assetHeader);
        SpringContext.getBean(AssetLocationService.class).setOffCampusLocation(asset);
        SpringContext.getBean(PaymentSummaryService.class).calculateAndSetPaymentSummary(asset);

        return docHandlerForward;
    }

    private Asset handleRequestFromWorkflow(AssetTransferForm assetTransferForm, AssetTransferDocument assetTransferDocument, BusinessObjectService service, AssetHeader assetHeader) {
        Asset newAsset = new Asset();
        if (assetTransferForm.getDocId() != null && assetHeader != null) {
            newAsset.setCapitalAssetNumber(assetHeader.getCapitalAssetNumber());
            newAsset = (Asset) service.retrieve(newAsset);
            assetTransferDocument.setAsset(newAsset);
        }
        return newAsset;
    }

    private Asset handleRequestFromLookup(HttpServletRequest request, AssetTransferForm assetTransferForm, AssetTransferDocument assetTransferDocument, BusinessObjectService service, Asset asset) {
        Asset newAsset = null;
        if (assetTransferForm.getDocId() == null && asset == null) {
            newAsset = new Asset();
            HashMap<String, Object> keys = new HashMap<String, Object>();
            String capitalAssetNumber = request.getParameter(CAPITAL_ASSET_NUMBER);
            keys.put(CAPITAL_ASSET_NUMBER, capitalAssetNumber);
            newAsset = (Asset) service.findByPrimaryKey(Asset.class, keys);
            if (newAsset != null) {
                assetTransferDocument.setAsset(newAsset);
            }
        }
        return newAsset;
    }

}
