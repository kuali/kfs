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


import static org.kuali.module.cams.fixture.AssetFixture.BASIC_ASSET;
import static org.kuali.module.cams.fixture.AssetPaymentFixture.PAYMENT1;
import static org.kuali.module.cams.fixture.AssetPaymentFixture.PAYMENT2;
import static org.kuali.module.cams.fixture.AssetPaymentFixture.PAYMENT3;
import static org.kuali.module.cams.fixture.AssetPaymentFixture.PAYMENT4;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.service.impl.PaymentSummaryServiceImpl;
import org.kuali.test.util.CSVDataLoader;

public class PaymentSummaryServiceTest extends KualiTestBase {

    private Asset asset;
    private PaymentSummaryServiceImpl paymentSummaryService;

    @Override
    protected void setUp() throws Exception {
        asset = BASIC_ASSET.getAsset();
        List<AssetPayment> assetPayments = new ArrayList<AssetPayment>();
        assetPayments.add(PAYMENT1.getAssetPayment());
        assetPayments.add(PAYMENT2.getAssetPayment());
        assetPayments.add(PAYMENT3.getAssetPayment());
        assetPayments.add(PAYMENT4.getAssetPayment());
        asset.setAssetPayments(assetPayments);
        paymentSummaryService = new PaymentSummaryServiceImpl();
        OptionsService optionsService = new OptionsService() {
            public Options getCurrentYearOptions() {
                Options options = new Options();
                options.setUniversityFiscalYearStartMo("10");
                return options;
            }

            public Options getOptions(Integer universityFiscalYear) {
                return null;
            }
        };
        paymentSummaryService.setOptionsService(optionsService);
    }

    private AssetPayment createAssetPayment(CSVDataLoader loader, int rowPos) {
        AssetPayment assetPayment = new AssetPayment();
        assetPayment.setAccountChargeAmount(loader.getKualiDecimal(rowPos, "ACCT_CHARGE_AMT"));
        assetPayment.setAccumulatedPrimaryDepreciationAmount(loader.getKualiDecimal(rowPos, "AST_ACUM_DEPR1_AMT"));
        assetPayment.setPrimaryDepreciationBaseAmount(loader.getKualiDecimal(rowPos, "AST_DEPR1_BASE_AMT"));
        assetPayment.setPreviousYearPrimaryDepreciationAmount(loader.getKualiDecimal(rowPos, "AST_PRVYRDEPR1_AMT"));
        assetPayment.setPeriod1Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD1_DEPR1_AMT"));
        assetPayment.setPeriod2Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD2_DEPR1_AMT"));
        assetPayment.setPeriod3Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD3_DEPR1_AMT"));
        assetPayment.setPeriod4Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD4_DEPR1_AMT"));
        assetPayment.setPeriod5Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD5_DEPR1_AMT"));
        assetPayment.setPeriod6Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD6_DEPR1_AMT"));
        assetPayment.setPeriod7Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD7_DEPR1_AMT"));
        assetPayment.setPeriod8Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD8_DEPR1_AMT"));
        assetPayment.setPeriod9Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD9_DEPR1_AMT"));
        assetPayment.setPeriod10Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD10_DEPR1_AMT"));
        assetPayment.setPeriod11Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD11_DEPR1_AMT"));
        assetPayment.setPeriod12Depreciation1Amount(loader.getKualiDecimal(rowPos, "AST_PRD12_DEPR1_AMT"));
        return assetPayment;
    }

    private Asset createAsset(CSVDataLoader loader, int rowPos) {
        Asset asset = new Asset();
        asset.setCapitalAssetNumber(Long.valueOf(loader.getString(rowPos, "CPTLAST_NBR")));
        asset.setPrimaryDepreciationMethodCode(loader.getString(rowPos, "CM_AST_DEPR_MTHD1_CD"));
        asset.setSalvageAmount(loader.getKualiDecimal(rowPos, "CPTLAST_SALVAG_AMT"));
        return asset;
    }

    public void testCalculateAndSetPaymentSummary_SL() throws Exception {
        paymentSummaryService.calculateAndSetPaymentSummary(asset);
        assertEquals(new KualiDecimal(50), asset.getPaymentTotalCost());
        assertEquals(new KualiDecimal(100), asset.getAccumulatedDepreciation());
        assertEquals(new KualiDecimal(100.30), asset.getBaseAmount());
        assertEquals(new KualiDecimal(0.30), asset.getBookValue());
        assertEquals(new KualiDecimal(62), asset.getPrevYearDepreciation());
        assertEquals(new KualiDecimal(160.81), asset.getYearToDateDepreciation());
        assertEquals(new KualiDecimal(15), asset.getCurrentMonthDepreciation());
    }

    public void testCalculateAndSetPaymentSummary_SV() throws Exception {
        asset.setPrimaryDepreciationMethodCode(CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE);
        asset.setSalvageAmount(new KualiDecimal(25));
        paymentSummaryService.calculateAndSetPaymentSummary(asset);
        assertEquals(new KualiDecimal(50), asset.getPaymentTotalCost());
        assertEquals(new KualiDecimal(100), asset.getAccumulatedDepreciation());
        assertEquals(new KualiDecimal(100.30), asset.getBaseAmount());
        assertEquals(new KualiDecimal(-24.70), asset.getBookValue());
        assertEquals(new KualiDecimal(62), asset.getPrevYearDepreciation());
        assertEquals(new KualiDecimal(160.81), asset.getYearToDateDepreciation());
        assertEquals(new KualiDecimal(15), asset.getCurrentMonthDepreciation());
    }
}
