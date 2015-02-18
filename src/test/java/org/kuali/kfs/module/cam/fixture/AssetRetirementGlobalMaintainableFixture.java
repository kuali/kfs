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
package org.kuali.kfs.module.cam.fixture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.sys.context.SpringContext;

public enum AssetRetirementGlobalMaintainableFixture {
    RETIREMENT1(1);

    private int testDataPos;

    private static Properties properties;
    private static final AccountService acctService;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/asset_retirement.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
	acctService = SpringContext.getBean(AccountService.class);
    }

    private AssetRetirementGlobalMaintainableFixture(int dataPos) {
        this.testDataPos = dataPos;
    }

    @SuppressWarnings("deprecation")
    public AssetRetirementGlobal newAssetRetirement() {
        String propertyKey = "assetRetirement.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetRetirement.fieldNames");
        AssetRetirementGlobal       assetRetirementGlobal       = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetRetirementGlobal.class, properties, propertyKey, fieldNames, deliminator);

        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetail = newAssetRetirementDetail();
        assetRetirementGlobal.setAssetRetirementGlobalDetails(assetRetirementGlobalDetail);
        return assetRetirementGlobal;
    }

    @SuppressWarnings("deprecation")
    private List<AssetRetirementGlobalDetail> newAssetRetirementDetail() {
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = new ArrayList<AssetRetirementGlobalDetail>();
        List<Asset> assets = newAssets();
        for (Asset asset : assets) {
            AssetRetirementGlobalDetail assetRetirementDetail = new AssetRetirementGlobalDetail();
            assetRetirementDetail.setAsset(asset);
            assetRetirementGlobalDetails.add(assetRetirementDetail);
        }
        return assetRetirementGlobalDetails;
    }

    @SuppressWarnings("deprecation")
    private List<Asset> newAssets() {
        List<Asset> assets = new ArrayList<Asset>();
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("asset.fieldNames");
        Integer dataRows = new Integer(properties.getProperty("asset.numOfData"));
        testDataPos=1;
        for (int i=1; i<= dataRows.intValue(); i++) {
            String propertyKey = "asset.testData" +i;
            Asset asset = CamsFixture.DATA_POPULATOR.buildTestDataObject(Asset.class, properties, propertyKey, fieldNames, deliminator);
            List<AssetPayment> assetPayments = newAssetPayments();
            asset.setAssetPayments(assetPayments);
            for (AssetPayment assetPayment : assetPayments) {
                assetPayment.setAsset(asset);
            }
            assets.add(asset);
            testDataPos += 2;
        }
        return assets;
    }

    private List<AssetPayment> newAssetPayments() {
        List<AssetPayment> assetPayments = new ArrayList<AssetPayment>();
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetPayment.fieldNames");
        Integer dataRows = new Integer(properties.getProperty("assetPayment.numOfData"));
        for (int i=testDataPos;i<=dataRows.intValue() && i<testDataPos+2;i++) {
            String propertyKey = "assetPayment.testData" + i;
            AssetPayment assetPayment = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);

	    /*part of the Asset Retirement Service depends on an Account being present in the AssetPayment*/
	    String[] data = properties.getProperty(propertyKey).split(",");
	    String[] fields = fieldNames.split(",");
	    String coa = null;
	    String acctnum = null;
	    boolean gotit = false;
	    for (int j = 0; j < fields.length; ++j){
		    if ("accountnumber".equalsIgnoreCase(fields[j])) {
			    acctnum = data[j];
			} else if ("chartofaccountscode".equalsIgnoreCase(fields[j])) {
				coa = data[j];
			}
			if (acctnum != null && coa != null){
				Account act = acctService.getByPrimaryId(coa, acctnum);
				assetPayment.setAccount(act);
				gotit = true;
				break;
			}
		}
		if (!gotit) {
			throw new RuntimeException("Unable to find account number of asset payment.");
		}
			
            assetPayments.add(assetPayment);
        }
        return assetPayments;
    }
}
