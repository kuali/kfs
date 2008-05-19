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

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.fixture.AssetPaymentServiceFixture;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.UserNameFixture;

public class AssetPaymentServiceTest extends KualiTestBase {

    private AssetPaymentService assetPaymentService;

    @Override
    @ConfigureContext(session = UserNameFixture.KHUNTLEY)
    protected void setUp() throws Exception {
        super.setUp();
        assetPaymentService = SpringContext.getBean(AssetPaymentService.class);
    }


    public void testAdjustPaymentAmounts_params_false_false() throws Exception {
        AssetPayment assetPayment = AssetPaymentServiceFixture.PAYMENT1.newAssetPayment();
        this.assetPaymentService.adjustPaymentAmounts(assetPayment, false, false);
        assertTrue(assetPayment.getAccountChargeAmount().isPositive());
        assertTrue(assetPayment.getAccumulatedPrimaryDepreciationAmount().isPositive());
        assertNotNull(assetPayment.getPeriod1Depreciation1Amount());
        assertNotNull(assetPayment.getPeriod11Depreciation1Amount());
    }

    public void testAdjustPaymentAmounts_params_true_true() throws Exception {
        AssetPayment assetPayment = AssetPaymentServiceFixture.PAYMENT1.newAssetPayment();
        this.assetPaymentService.adjustPaymentAmounts(assetPayment, true, true);
        assertTrue(assetPayment.getAccountChargeAmount().isNegative());
        assertTrue(assetPayment.getAccumulatedPrimaryDepreciationAmount().isNegative());
        assertNull(assetPayment.getPeriod1Depreciation1Amount());
        assertNull(assetPayment.getPeriod11Depreciation1Amount());
    }
}
