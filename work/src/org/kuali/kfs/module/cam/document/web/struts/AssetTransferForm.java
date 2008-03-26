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
package org.kuali.module.cams.web.struts.form;

import static org.kuali.module.cams.CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetOrganization;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.service.AssetLocationService;
import org.kuali.module.cams.service.PaymentSummaryService;

public class AssetTransferForm extends KualiAccountingDocumentFormBase {

    private AssetTransferDocument assetTransferDocument;

    public AssetTransferForm() {
        super();
        this.assetTransferDocument = new AssetTransferDocument();
        setDocument(this.assetTransferDocument);
    }

    public AssetTransferDocument getAssetTransferDocument() {
        return this.assetTransferDocument;
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        AssetTransferDocument transferDocument = getAssetTransferDocument();
        initializeDoc(request, transferDocument);
        initializeAssetHeader(transferDocument);
    }

    private void initializeDoc(HttpServletRequest request, AssetTransferDocument transferDocument) {
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        if (transferDocument.getAsset() == null && transferDocument.getAssetHeader() == null) {
            HashMap<String, Object> keys = new HashMap<String, Object>();
            String capitalAssetNumber = request.getParameter(CAPITAL_ASSET_NUMBER);
            keys.put(CAPITAL_ASSET_NUMBER, capitalAssetNumber);
            Asset asset = (Asset) service.findByPrimaryKey(Asset.class, keys);
            if (asset != null) {
                transferDocument.setAsset(asset);
            }
            else {
                throw new RuntimeException("Asset is not found for capital asset number " + capitalAssetNumber);
            }
        }
        else {
            transferDocument.setAsset((Asset) service.retrieve(transferDocument.getAsset()));
            Asset currAsset = transferDocument.getAsset();
            SpringContext.getBean(AssetLocationService.class).setOffCampusLocation(currAsset);
            SpringContext.getBean(PaymentSummaryService.class).calculateAndSetPaymentSummary(currAsset);
        }
    }

    private void initializeAssetHeader(AssetTransferDocument transferDocument) {
        if (transferDocument.getAsset() != null && transferDocument.getDocumentNumber() != null) {
            AssetHeader assetHeader = new AssetHeader();
            assetHeader.setDocumentNumber(transferDocument.getDocumentNumber());
            assetHeader.setCapitalAssetNumber(transferDocument.getAsset().getCapitalAssetNumber());
            transferDocument.setAssetHeader(assetHeader);
        }
    }
}
