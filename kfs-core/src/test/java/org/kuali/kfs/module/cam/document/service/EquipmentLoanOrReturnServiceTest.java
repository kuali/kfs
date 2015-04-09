/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
