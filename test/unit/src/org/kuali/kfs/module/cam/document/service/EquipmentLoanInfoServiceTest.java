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
import org.kuali.module.cams.bo.EquipmentLoanOrReturn;
import org.kuali.module.cams.service.impl.AssetHeaderServiceImpl;
import org.kuali.module.cams.service.impl.EquipmentLoanInfoServiceImpl;

public class EquipmentLoanInfoServiceTest extends KualiTestBase {

    private static final String DOC_DISAPPROVED = "D";
    private static final int THREE_DAYS_LATER = 3;
    private static final int TWO_DAYS_LATER = 2;
    private EquipmentLoanInfoServiceImpl equipmentLoanInfoService;
    private Asset asset;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        equipmentLoanInfoService = new EquipmentLoanInfoServiceImpl();
        this.equipmentLoanInfoService.setAssetHeaderService(new AssetHeaderServiceImpl() {
            public boolean isDocumentApproved(AssetHeader assetHeader) {
                return true;
            }

        });
        this.asset = new Asset();
    }

    private AssetHeader createAssetHeader(String docNumber, int daysToAdd) {
        AssetHeader assetHeader = new AssetHeader();
        EquipmentLoanOrReturn loanOrReturn = new EquipmentLoanOrReturn();
        loanOrReturn.setDocumentNumber(docNumber);
        loanOrReturn.setLoanDate(new java.sql.Date(DateUtils.addDays(new Date(), daysToAdd).getTime()));
        assetHeader.setEquipmentLoanOrReturn(loanOrReturn);
        return assetHeader;
    }


    public void testEmptyHeaderList() throws Exception {
        this.asset.getAssetHeaders().clear();
        try {
            equipmentLoanInfoService.setEquipmentLoanInfo(this.asset);
            assertNull(this.asset.getLoanOrReturnInfo());
        }
        catch (Exception e) {
            fail("Should not have thrown exception");
        }
    }

    public void testSetEquipmentLoanInfo() throws Exception {
        this.asset.getAssetHeaders().add(createAssetHeader("first", TWO_DAYS_LATER));
        this.asset.getAssetHeaders().add(createAssetHeader("second", THREE_DAYS_LATER));
        equipmentLoanInfoService.setEquipmentLoanInfo(this.asset);
        assertNotNull(this.asset.getLoanOrReturnInfo());
        assertEquals("second", this.asset.getLoanOrReturnInfo().getDocumentNumber());
    }

    public void testSetEquipmentLoanInfo_Disapproved() throws Exception {
        this.equipmentLoanInfoService.setAssetHeaderService(new AssetHeaderServiceImpl() {
            public boolean isDocumentApproved(AssetHeader assetHeader) {
                return false;
            }
        });
        this.asset.getAssetHeaders().add(createAssetHeader("first", TWO_DAYS_LATER));
        // create a disapproved header so that it is ignored by the program
        this.asset.getAssetHeaders().add(createAssetHeader("second", THREE_DAYS_LATER));
        equipmentLoanInfoService.setEquipmentLoanInfo(this.asset);
        assertNull(this.asset.getLoanOrReturnInfo());
    }

    public void testSetEquipmentLoanInfo_Reverse() throws Exception {
        this.asset.getAssetHeaders().add(createAssetHeader("first", THREE_DAYS_LATER));
        this.asset.getAssetHeaders().add(createAssetHeader("second", TWO_DAYS_LATER));
        equipmentLoanInfoService.setEquipmentLoanInfo(this.asset);
        assertNotNull(this.asset.getLoanOrReturnInfo());
        assertEquals("first", this.asset.getLoanOrReturnInfo().getDocumentNumber());
    }
}
