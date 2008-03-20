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

import org.kuali.core.util.DateUtils;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetRetirementDocument;
import org.kuali.module.cams.service.impl.RetirementInfoServiceImpl;

public class RetirementInfoServiceTest extends KualiTestBase {
    private static final String DOC_DISAPPROVED = "D";
    private RetirementInfoServiceImpl retirementInfoService;
    private Asset asset;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.retirementInfoService = new RetirementInfoServiceImpl();
        this.retirementInfoService.setAssetHeaderService(new AssetHeaderService() {

            public boolean isDocumentApproved(AssetHeader assetHeader) {
                return true;
            }
        });
        this.asset = new Asset();
        this.asset.setInventoryStatusCode("R");
    }

    private AssetHeader createAssetHeader(String docNumber, int daysToAdd) {
        AssetHeader assetHeader = new AssetHeader();
        AssetRetirementDocument retirementDocument = new AssetRetirementDocument();
        retirementDocument.setDocumentNumber(docNumber);
        retirementDocument.setRetirementDate(new java.sql.Date(DateUtils.addDays(new Date(), daysToAdd).getTime()));
        assetHeader.setRetirementDocument(retirementDocument);
        return assetHeader;
    }

    public void testRetirementInfoService() throws Exception {
        this.asset.getAssetHeaders().add(createAssetHeader("123456", 0));
        this.asset.getAssetHeaders().add(createAssetHeader("123457", 1));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNotNull(this.asset.getRetirementInfo());
        assertEquals("123457", this.asset.getRetirementInfo().getDocumentNumber());
    }

    public void testRetirementInfoService_Disapproved() throws Exception {
        this.retirementInfoService.setAssetHeaderService(new AssetHeaderService() {
            public boolean isDocumentApproved(AssetHeader assetHeader) {
                return false;
            }
        });
        this.asset.getAssetHeaders().add(createAssetHeader("123456", 0));
        this.asset.getAssetHeaders().add(createAssetHeader("123457", 1));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNull(this.asset.getRetirementInfo());
    }

    public void testRetirementInfoService_NotRetired() throws Exception {
        this.asset.setInventoryStatusCode("A");
        this.asset.getAssetHeaders().add(createAssetHeader("123456", 0));
        this.asset.getAssetHeaders().add(createAssetHeader("123457", 1));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNull(this.asset.getRetirementInfo());
    }
}
