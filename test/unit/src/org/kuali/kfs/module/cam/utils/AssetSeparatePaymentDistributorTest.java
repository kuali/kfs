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
package org.kuali.kfs.module.cam.utils;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.util.AssetSeparatePaymentDistributor;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@ConfigureContext
public class AssetSeparatePaymentDistributorTest extends KualiTestBase {
    public void testDistributeByAsset() throws Exception {
        Asset sourceAsset = new Asset();
        sourceAsset.getAssetPayments().add(createPayment("0", "DUMMY1"));
        sourceAsset.getAssetPayments().add(createPayment("13", "DUMMY2"));
        sourceAsset.getAssetPayments().add(createPayment("13", "DUMMY3"));
        sourceAsset.getAssetPayments().add(createPayment("13", "DUMMY4"));
        sourceAsset.getAssetPayments().add(createPayment("0", "DUMMY5"));


        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setTotalCostAmount(new KualiDecimal("39"));
        assetGlobal.setSeparateSourceTotalAmount(new KualiDecimal("39"));
        assetGlobal.setSeparateSourceRemainingAmount(new KualiDecimal("13"));
        assetGlobal.getAssetGlobalDetails().add(createDetail(1L, new KualiDecimal("22")));
        assetGlobal.getAssetGlobalDetails().add(createDetail(2L, new KualiDecimal("17")));

        List<Asset> newAssets = new ArrayList<Asset>();
        newAssets.add(createAsset(1L));
        newAssets.add(createAsset(2L));

        AssetSeparatePaymentDistributor distributor = new AssetSeparatePaymentDistributor(sourceAsset, sourceAsset.getAssetPayments(), 5, assetGlobal, newAssets);
        distributor.distribute();

        // make sure payments are added to new asset
        Asset asset = newAssets.get(0);
        assertEquals(3, asset.getAssetPayments().size());
        assertEquals(new KualiDecimal("7.33"), asset.getAssetPayments().get(0).getAccountChargeAmount());
        assertEquals("DUMMY2", asset.getAssetPayments().get(0).getAccountNumber());
        assertEquals(new KualiDecimal("7.33"), asset.getAssetPayments().get(1).getAccountChargeAmount());
        assertEquals("DUMMY3", asset.getAssetPayments().get(1).getAccountNumber());
        assertEquals(new KualiDecimal("7.34"), asset.getAssetPayments().get(2).getAccountChargeAmount());
        assertEquals("DUMMY4", asset.getAssetPayments().get(2).getAccountNumber());

        assertEquals(new KualiDecimal("7.33"), asset.getAssetPayments().get(0).getPrimaryDepreciationBaseAmount());
        assertEquals(new KualiDecimal("7.33"), asset.getAssetPayments().get(1).getPrimaryDepreciationBaseAmount());
        assertEquals(new KualiDecimal("7.34"), asset.getAssetPayments().get(2).getPrimaryDepreciationBaseAmount());

        assertEquals(new KualiDecimal("3.89"), asset.getAssetPayments().get(0).getAccumulatedPrimaryDepreciationAmount());
        assertEquals(new KualiDecimal("3.89"), asset.getAssetPayments().get(1).getAccumulatedPrimaryDepreciationAmount());
        assertEquals(new KualiDecimal("3.89"), asset.getAssetPayments().get(2).getAccumulatedPrimaryDepreciationAmount());

        asset = newAssets.get(1);
        assertEquals(3, asset.getAssetPayments().size());
        assertEquals(new KualiDecimal("5.67"), asset.getAssetPayments().get(0).getAccountChargeAmount());
        assertEquals("DUMMY2", asset.getAssetPayments().get(0).getAccountNumber());
        assertEquals(new KualiDecimal("5.67"), asset.getAssetPayments().get(1).getAccountChargeAmount());
        assertEquals("DUMMY3", asset.getAssetPayments().get(1).getAccountNumber());
        assertEquals(new KualiDecimal("5.66"), asset.getAssetPayments().get(2).getAccountChargeAmount());
        assertEquals("DUMMY4", asset.getAssetPayments().get(2).getAccountNumber());

        assertEquals(new KualiDecimal("5.67"), asset.getAssetPayments().get(0).getPrimaryDepreciationBaseAmount());
        assertEquals(new KualiDecimal("5.67"), asset.getAssetPayments().get(1).getPrimaryDepreciationBaseAmount());
        assertEquals(new KualiDecimal("5.66"), asset.getAssetPayments().get(2).getPrimaryDepreciationBaseAmount());

        assertEquals(new KualiDecimal("3.01"), asset.getAssetPayments().get(0).getAccumulatedPrimaryDepreciationAmount());
        assertEquals(new KualiDecimal("3.01"), asset.getAssetPayments().get(1).getAccumulatedPrimaryDepreciationAmount());
        assertEquals(new KualiDecimal("3.01"), asset.getAssetPayments().get(2).getAccumulatedPrimaryDepreciationAmount());

        // make sure offset payments are added to source asset
        assertEquals(8, sourceAsset.getAssetPayments().size());
        assertEquals(new KualiDecimal("-13"), sourceAsset.getAssetPayments().get(5).getAccountChargeAmount());
        assertEquals("DUMMY2", sourceAsset.getAssetPayments().get(5).getAccountNumber());
        assertEquals(new KualiDecimal("-13"), sourceAsset.getAssetPayments().get(6).getAccountChargeAmount());
        assertEquals("DUMMY3", sourceAsset.getAssetPayments().get(6).getAccountNumber());
        assertEquals(new KualiDecimal("-13"), sourceAsset.getAssetPayments().get(7).getAccountChargeAmount());
        assertEquals("DUMMY4", sourceAsset.getAssetPayments().get(7).getAccountNumber());

        assertEquals(new KualiDecimal("-13"), sourceAsset.getAssetPayments().get(5).getPrimaryDepreciationBaseAmount());
        assertEquals(new KualiDecimal("-13"), sourceAsset.getAssetPayments().get(6).getPrimaryDepreciationBaseAmount());
        assertEquals(new KualiDecimal("-13"), sourceAsset.getAssetPayments().get(7).getPrimaryDepreciationBaseAmount());

        assertEquals(new KualiDecimal("-6.9"), sourceAsset.getAssetPayments().get(5).getAccumulatedPrimaryDepreciationAmount());
        assertEquals(new KualiDecimal("-6.9"), sourceAsset.getAssetPayments().get(6).getAccumulatedPrimaryDepreciationAmount());
        assertEquals(new KualiDecimal("-6.9"), sourceAsset.getAssetPayments().get(7).getAccumulatedPrimaryDepreciationAmount());

        // sum and see if dollar amount sums up correctly
        assertEquals(new KualiDecimal("13"), newAssets.get(0).getAssetPayments().get(0).getAccountChargeAmount().add(newAssets.get(1).getAssetPayments().get(0).getAccountChargeAmount()));
        assertEquals(new KualiDecimal("13"), newAssets.get(0).getAssetPayments().get(1).getAccountChargeAmount().add(newAssets.get(1).getAssetPayments().get(1).getAccountChargeAmount()));
        assertEquals(new KualiDecimal("13"), newAssets.get(0).getAssetPayments().get(2).getAccountChargeAmount().add(newAssets.get(1).getAssetPayments().get(2).getAccountChargeAmount()));
    }

    private Asset createAsset(long assetNumber) {
        Asset asset = new Asset();
        asset.setCapitalAssetNumber(assetNumber);
        return asset;
    }

    private AssetGlobalDetail createDetail(long assetNumber, KualiDecimal separateAmount) {
        AssetGlobalDetail detail = new AssetGlobalDetail();
        detail.setCapitalAssetNumber(assetNumber);
        detail.setSeparateSourceAmount(separateAmount);
        return detail;
    }

    private AssetPayment createPayment(String amount, String acctNo) {
        AssetPayment payment = new AssetPayment();
        payment.setAccountNumber(acctNo);
        payment.setAccountChargeAmount(new KualiDecimal(amount));
        payment.setPrimaryDepreciationBaseAmount(new KualiDecimal(amount));
        payment.setPreviousYearPrimaryDepreciationAmount(new KualiDecimal("2.3"));
        payment.setAccumulatedPrimaryDepreciationAmount(new KualiDecimal("6.9"));
        payment.setPeriod2Depreciation1Amount(new KualiDecimal("2.3"));
        payment.setPeriod3Depreciation1Amount(new KualiDecimal("2.3"));
        payment.setPeriod4Depreciation1Amount(KualiDecimal.ZERO);
        payment.setPeriod5Depreciation1Amount(null);
        return payment;
    }
}
