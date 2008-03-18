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
package org.kuali.module.cams.service;

import java.util.Date;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.DateUtils;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetRetirementDocument;
import org.kuali.module.cams.bo.EquipmentLoanOrReturn;
import org.kuali.module.cams.service.impl.RetirementInfoServiceImpl;

public class RetirementInfoServiceTest extends KualiTestBase {
    private static final String DOC_DISAPPROVED = "D";
    private RetirementInfoServiceImpl retirementInfoService;
    private Asset asset;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.retirementInfoService = new RetirementInfoServiceImpl();
        this.asset = new Asset();
    }

    private AssetHeader createApprovedAssetHeader(String docNumber) {
        AssetHeader assetHeader = new AssetHeader();
        AssetRetirementDocument retirementDocument = new AssetRetirementDocument() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
                DocumentHeader header = new DocumentHeader();
                header.setFinancialDocumentStatusCode(CamsConstants.DOC_APPROVED);
                this.setDocumentHeader(header);
            }

        };
        retirementDocument.setDocumentNumber(docNumber);
        assetHeader.setRetirementDocument(retirementDocument);
        return assetHeader;
    }

    private AssetHeader createDisapprovedAssetHeader(String docNumber) {
        AssetHeader assetHeader = new AssetHeader();
        AssetRetirementDocument retirementDocument = new AssetRetirementDocument() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
                DocumentHeader header = new DocumentHeader();
                header.setFinancialDocumentStatusCode(DOC_DISAPPROVED);
                this.setDocumentHeader(header);
            }

        };
        retirementDocument.setDocumentNumber(docNumber);
        assetHeader.setRetirementDocument(retirementDocument);
        return assetHeader;
    }

    public void testRetirementInfoService() throws Exception {
        this.asset.getAssetHeaders().add(createApprovedAssetHeader("123456"));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNotNull(this.asset.getRetirementInfo());
        assertEquals("123456", this.asset.getRetirementInfo().getDocumentNumber());
    }

    public void testRetirementInfoService_Disapproved() throws Exception {
        this.asset.getAssetHeaders().add(createDisapprovedAssetHeader("123456"));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNull(this.asset.getRetirementInfo());
    }
}
