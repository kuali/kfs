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
        super.setUp();
        CSVDataLoader loader = new CSVDataLoader();
        loader.loadData("org/kuali/module/cams/service/testdata/cm_ast_389220.txt", true);
        asset = createAsset(loader, 0);
        loader.reset();
        loader.loadData("org/kuali/module/cams/service/testdata/cm_ast_payment_389220.txt", true);
        // create payments
        List<AssetPayment> assetPayments = new ArrayList<AssetPayment>();
        assetPayments.add(createAssetPayment(loader, 0));
        assetPayments.add(createAssetPayment(loader, 1));
        assetPayments.add(createAssetPayment(loader, 2));
        assetPayments.add(createAssetPayment(loader, 3));
        // set payments
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
        assetPayment.setAccountChargeAmount(createKualiDecimal(loader, rowPos, "ACCT_CHARGE_AMT"));
        assetPayment.setAccumulatedPrimaryDepreciationAmount(createKualiDecimal(loader, rowPos, "AST_ACUM_DEPR1_AMT"));
        assetPayment.setPrimaryDepreciationBaseAmount(createKualiDecimal(loader, rowPos, "AST_DEPR1_BASE_AMT"));
        assetPayment.setPreviousYearPrimaryDepreciationAmount(createKualiDecimal(loader, rowPos, "AST_PRVYRDEPR1_AMT"));
        assetPayment.setPeriod1Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD1_DEPR1_AMT"));
        assetPayment.setPeriod2Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD2_DEPR1_AMT"));
        assetPayment.setPeriod3Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD3_DEPR1_AMT"));
        assetPayment.setPeriod4Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD4_DEPR1_AMT"));
        assetPayment.setPeriod5Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD5_DEPR1_AMT"));
        assetPayment.setPeriod6Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD6_DEPR1_AMT"));
        assetPayment.setPeriod7Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD7_DEPR1_AMT"));
        assetPayment.setPeriod8Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD8_DEPR1_AMT"));
        assetPayment.setPeriod9Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD9_DEPR1_AMT"));
        assetPayment.setPeriod10Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD10_DEPR1_AMT"));
        assetPayment.setPeriod11Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD11_DEPR1_AMT"));
        assetPayment.setPeriod12Depreciation1Amount(createKualiDecimal(loader, rowPos, "AST_PRD12_DEPR1_AMT"));
        return assetPayment;
    }

    private Asset createAsset(CSVDataLoader loader, int rowPos) {
        Asset asset = new Asset();
        asset.setCapitalAssetNumber(Long.valueOf(loader.getColumnData(rowPos, "CPTLAST_NBR")));
        asset.setPrimaryDepreciationMethodCode(loader.getColumnData(rowPos, "CM_AST_DEPR_MTHD1_CD"));
        asset.setSalvageAmount(createKualiDecimal(loader, rowPos, "CPTLAST_SALVAG_AMT"));
        return asset;
    }

    private KualiDecimal createKualiDecimal(CSVDataLoader loader, int pos, String column) {
        String columnData = loader.getColumnData(pos, column);
        if (columnData == null || columnData.trim().length() == 0) {
            columnData = "0";
        }
        return new KualiDecimal(columnData);
    }

    public void testCalculateAndSetPaymentSummary_SL() throws Exception {
        paymentSummaryService.calculateAndSetPaymentSummary(asset);
        assertEquals(new KualiDecimal(50), asset.getPaymentTotalCost());
        assertEquals(new KualiDecimal(100), asset.getAccumulatedDepreciation());
        assertEquals(new KualiDecimal(100.30), asset.getBaseAmount());
        assertEquals(new KualiDecimal(0.30), asset.getBookValue());
        assertEquals(new KualiDecimal(62), asset.getPrevYearDepreciation());
        assertEquals(new KualiDecimal(109.33), asset.getYearToDateDepreciation());
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
        assertEquals(new KualiDecimal(109.33), asset.getYearToDateDepreciation());
        assertEquals(new KualiDecimal(15), asset.getCurrentMonthDepreciation());
    }
}
