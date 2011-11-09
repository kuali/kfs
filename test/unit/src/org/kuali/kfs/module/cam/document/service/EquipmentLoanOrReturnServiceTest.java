/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.document.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class EquipmentLoanOrReturnServiceTest extends KualiTestBase {
    private EquipmentLoanOrReturnService equipmentLoanOrReturnService;
    private DateTimeService dateTimeService;
    private Asset asset;

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        equipmentLoanOrReturnService = SpringContext.getBean(EquipmentLoanOrReturnService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        java.sql.Date date = new java.sql.Date(dateTimeService.getCurrentDate().getTime());
        
        this.asset = new Asset() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
                List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
                AssetLocation loc1 = new AssetLocation();
                loc1.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER);
                assetLocations.add(loc1);

                AssetLocation loc2 = new AssetLocation();
                loc2.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER_STORAGE);
                assetLocations.add(loc2);
                this.setAssetLocations(assetLocations);
            }
        };
        this.asset.setExpectedReturnDate(date);
    }

    public void testSetEquipmentLoanInfo() throws Exception {
        equipmentLoanOrReturnService.setEquipmentLoanInfo(asset);
        assertNotNull(asset.getBorrowerLocation());
        assertNotNull(asset.getBorrowerStorageLocation());
    }

    public void testSetEquipmentLoanInfo_NoLoan() throws Exception {
        this.asset.setExpectedReturnDate(null);
        equipmentLoanOrReturnService.setEquipmentLoanInfo(asset);
        assertNull(asset.getBorrowerLocation());
        assertNull(asset.getBorrowerStorageLocation());
    }

}
