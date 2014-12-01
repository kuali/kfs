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
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.service.impl.PaymentSummaryServiceImpl;
import org.kuali.kfs.module.cam.fixture.PaymentSummaryFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.UniversityDateServiceImpl;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.impl.parameter.ParameterServiceImpl;

public class PaymentSummaryServiceTest extends KualiTestBase {

    private Asset asset;
    private PaymentSummaryServiceImpl paymentSummaryService;

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
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
