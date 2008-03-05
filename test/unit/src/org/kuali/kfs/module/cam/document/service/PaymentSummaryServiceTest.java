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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.service.impl.PaymentSummaryServiceImpl;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.financial.service.impl.UniversityDateServiceImpl;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.test.util.CSVDataLoader;

import sun.util.calendar.Gregorian;

public class PaymentSummaryServiceTest extends KualiTestBase {

    private Asset asset;
    private PaymentSummaryServiceImpl paymentSummaryService;

    @Override
    protected void setUp() throws Exception {
        asset = BASIC_ASSET.newAsset();
        List<AssetPayment> assetPayments = new ArrayList<AssetPayment>();
        assetPayments.add(PAYMENT1.newAssetPayment());
        assetPayments.add(PAYMENT2.newAssetPayment());
        assetPayments.add(PAYMENT3.newAssetPayment());
        assetPayments.add(PAYMENT4.newAssetPayment());
        asset.setAssetPayments(assetPayments);
        paymentSummaryService = new PaymentSummaryServiceImpl();

        UniversityDateService universityDateService = new UniversityDateServiceImpl() {
            public UniversityDate getCurrentUniversityDate() {
                return new UniversityDate() {
                    @Override
                    public String getUniversityFiscalAccountingPeriod() {
                        return "5";
                    }

                };
            }
        };
        paymentSummaryService.setUniversityDateService(universityDateService);
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
