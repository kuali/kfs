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
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.service.impl.PaymentSummaryServiceImpl;
import org.kuali.kfs.module.cam.fixture.PaymentSummaryFixture;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.UniversityDateServiceImpl;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.impl.parameter.ParameterServiceImpl;

public class PaymentSummaryServiceTest extends KualiTestBase {

    private Asset asset;
    private PaymentSummaryServiceImpl paymentSummaryService;

    @Override
    protected void setUp() throws Exception {
        asset = PaymentSummaryFixture.ASSET.newAsset();
        List<AssetPayment> assetPayments = new ArrayList<AssetPayment>();
        assetPayments.add(PaymentSummaryFixture.PAYMENT1.newAssetPayment());
        assetPayments.add(PaymentSummaryFixture.PAYMENT2.newAssetPayment());
        assetPayments.add(PaymentSummaryFixture.PAYMENT3.newAssetPayment());
        assetPayments.add(PaymentSummaryFixture.PAYMENT4.newAssetPayment());
        asset.setAssetPayments(assetPayments);
        paymentSummaryService = new PaymentSummaryServiceImpl();
        paymentSummaryService.setParameterService(new ParameterServiceImpl() {
            @SuppressWarnings("unchecked")
            @Override
            public List<String> getParameterValuesAsString(Class componentClass, String parameterName) {
                List<String> list = new ArrayList<String>();
                list.add("BF");
                return list;
            }
        });

        UniversityDateService universityDateService = new UniversityDateServiceImpl() {
            @Override
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
        assertEquals(new KualiDecimal(50), asset.getFederalContribution());
        assertEquals(new KualiDecimal(50), asset.getPaymentTotalCost());
        assertEquals(new KualiDecimal(100), asset.getAccumulatedDepreciation());
        assertEquals(new KualiDecimal(100.30), asset.getBaseAmount());
        assertEquals(new KualiDecimal(0.30), asset.getBookValue());
        assertEquals(new KualiDecimal(62), asset.getPrevYearDepreciation());
        assertEquals(new KualiDecimal(160.81), asset.getYearToDateDepreciation());
        assertEquals(new KualiDecimal(15), asset.getCurrentMonthDepreciation());
        assertEquals(new KualiDecimal(392.38), asset.getAssetPayments().get(0).getYearToDate());
        assertEquals(new KualiDecimal(210.92), asset.getAssetPayments().get(1).getYearToDate());
        assertEquals(new KualiDecimal(-392.38), asset.getAssetPayments().get(2).getYearToDate());
        assertEquals(new KualiDecimal(-50.11), asset.getAssetPayments().get(3).getYearToDate());
    }

    public void testCalculateAndSetPaymentSummary_SV() throws Exception {
        asset.setPrimaryDepreciationMethodCode(CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE);
        asset.setSalvageAmount(new KualiDecimal(25));
        paymentSummaryService.calculateAndSetPaymentSummary(asset);
        assertEquals(new KualiDecimal(50), asset.getFederalContribution());
        assertEquals(new KualiDecimal(50), asset.getPaymentTotalCost());
        assertEquals(new KualiDecimal(100), asset.getAccumulatedDepreciation());
        assertEquals(new KualiDecimal(100.30), asset.getBaseAmount());
        assertEquals(new KualiDecimal(-24.70), asset.getBookValue());
        assertEquals(new KualiDecimal(62), asset.getPrevYearDepreciation());
        assertEquals(new KualiDecimal(160.81), asset.getYearToDateDepreciation());
        assertEquals(new KualiDecimal(15), asset.getCurrentMonthDepreciation());
        assertEquals(new KualiDecimal(392.38), asset.getAssetPayments().get(0).getYearToDate());
        assertEquals(new KualiDecimal(210.92), asset.getAssetPayments().get(1).getYearToDate());
        assertEquals(new KualiDecimal(-392.38), asset.getAssetPayments().get(2).getYearToDate());
        assertEquals(new KualiDecimal(-50.11), asset.getAssetPayments().get(3).getYearToDate());
    }
}
