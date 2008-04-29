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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.DateUtils;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.service.impl.ParameterServiceImpl;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.service.impl.AssetServiceImpl;
import org.kuali.module.cams.service.impl.RetirementInfoServiceImpl;

public class RetirementInfoServiceTest extends KualiTestBase {
    private RetirementInfoServiceImpl retirementInfoService;
    private Asset asset;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.retirementInfoService = new RetirementInfoServiceImpl();
        this.retirementInfoService.setParameterService(createParameterService());
        AssetServiceImpl assetServiceImpl = new AssetServiceImpl();
        assetServiceImpl.setParameterService(createParameterService());
        this.retirementInfoService.setAssetService(assetServiceImpl);
        this.asset = new Asset();
        this.asset.setInventoryStatusCode("R");

    }

    private ParameterServiceImpl createParameterService() {
        return new ParameterServiceImpl() {
            @Override
            public List<String> getParameterValues(Class componentClass, String parameterName) {
                List<String> values = new ArrayList<String>();
                values.add("O");
                values.add("R");
                values.add("E");
                return values;
            }
        };
    }

    private AssetRetirementGlobalDetail createRetirementDetail(String docNumber, int daysToAdd, String docStatus) {
        AssetRetirementGlobalDetail globalDetail = new AssetRetirementGlobalDetail();
        globalDetail.setDocumentNumber(docNumber);
        AssetRetirementGlobal retirementGlobal = new AssetRetirementGlobal() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
            }

        };
        retirementGlobal.setRetirementDate(new java.sql.Date(DateUtils.addDays(new Date(), daysToAdd).getTime()));
        DocumentHeader header = new DocumentHeader();
        header.setFinancialDocumentStatusCode(docStatus);
        retirementGlobal.setDocumentHeader(header);
        globalDetail.setAssetRetirementGlobal(retirementGlobal);
        return globalDetail;
    }

    public void testRetirementInfoService() throws Exception {
        this.asset.getAssetRetirementHistory().add(createRetirementDetail("12345", 0, "A"));
        this.asset.getAssetRetirementHistory().add(createRetirementDetail("123457", 1, "A"));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNotNull(this.asset.getRetirementInfo());
        assertEquals("123457", this.asset.getRetirementInfo().getDocumentNumber());
    }

    public void testRetirementInfoService_Disapproved() throws Exception {
        this.asset.getAssetRetirementHistory().add(createRetirementDetail("12345", 0, "A"));
        this.asset.getAssetRetirementHistory().add(createRetirementDetail("123457", 1, "D"));
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNotNull(this.asset.getRetirementInfo());
        assertEquals("12345", this.asset.getRetirementInfo().getDocumentNumber());
    }

    public void testRetirementInfoService_NotRetired() throws Exception {
        this.asset.setInventoryStatusCode("A");
        this.retirementInfoService.setRetirementInfo(this.asset);
        assertNull(this.asset.getRetirementInfo());
    }
}
