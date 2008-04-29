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

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.Date;

import org.kuali.core.util.DateUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.fixture.AssetPaymentServiceFixture;
import org.kuali.module.financial.service.UniversityDateService;
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

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = false)
    public void testCreateOffsetPayment() throws Exception {
        AssetPayment assetPayment = AssetPaymentServiceFixture.PAYMENT1.newAssetPayment();
        String documentNumber = "21346";
        String documentTypeCode = "AT";
        AssetPayment offsetPayment = this.assetPaymentService.createOffsetPayment(assetPayment, documentNumber, documentTypeCode);
        assertNotNull(offsetPayment);
        assertEquals(assetPayment.getAccountChargeAmount(), offsetPayment.getAccountChargeAmount().multiply(new KualiDecimal(-1)));
        assertEquals(assetPayment.getAccumulatedPrimaryDepreciationAmount(), offsetPayment.getAccumulatedPrimaryDepreciationAmount().multiply(new KualiDecimal(-1)));
        assertNull(offsetPayment.getPeriod1Depreciation1Amount());
        assertNull(offsetPayment.getPeriod2Depreciation1Amount());
        assertNull(offsetPayment.getPeriod3Depreciation1Amount());
        assertNull(offsetPayment.getPeriod4Depreciation1Amount());
        assertNull(offsetPayment.getPeriod5Depreciation1Amount());
        assertNull(offsetPayment.getPeriod6Depreciation1Amount());
        assertNull(offsetPayment.getPeriod7Depreciation1Amount());
        assertNull(offsetPayment.getPeriod8Depreciation1Amount());
        assertNull(offsetPayment.getPeriod9Depreciation1Amount());
        assertNull(offsetPayment.getPeriod10Depreciation1Amount());
        assertNull(offsetPayment.getPeriod11Depreciation1Amount());
        assertNull(offsetPayment.getPeriod12Depreciation1Amount());
        assertNull(offsetPayment.getTransferPaymentCode());
        assertEquals(documentNumber, offsetPayment.getDocumentNumber());
        assertEquals(documentTypeCode, offsetPayment.getFinancialDocumentTypeCode());
        assertTrue(DateUtils.isSameDay(DateUtils.convertToSqlDate(new Date()), offsetPayment.getFinancialDocumentPostingDate()));
        //TODO
        //assertEquals(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalAccountingPeriod(), offsetPayment.getFinancialDocumentPostingPeriod());
        assertEquals(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear(), offsetPayment.getFinancialDocumentPostingYear());
    }

    private UniversityDateService getUniversityDateService() {
        return SpringContext.getBean(UniversityDateService.class);
    }

    public void testAdjustPaymentAmounts_test1() throws Exception {
        AssetPayment assetPayment = AssetPaymentServiceFixture.PAYMENT1.newAssetPayment();
        this.assetPaymentService.adjustPaymentAmounts(assetPayment, false, false);
        assertTrue(assetPayment.getAccountChargeAmount().isPositive());
        assertTrue(assetPayment.getAccumulatedPrimaryDepreciationAmount().isPositive());
        assertNotNull(assetPayment.getPeriod1Depreciation1Amount());
        assertNotNull(assetPayment.getPeriod11Depreciation1Amount());
    }

    public void testAdjustPaymentAmounts_test2() throws Exception {
        AssetPayment assetPayment = AssetPaymentServiceFixture.PAYMENT1.newAssetPayment();
        this.assetPaymentService.adjustPaymentAmounts(assetPayment, true, true);
        assertTrue(assetPayment.getAccountChargeAmount().isNegative());
        assertTrue(assetPayment.getAccumulatedPrimaryDepreciationAmount().isNegative());
        assertNull(assetPayment.getPeriod1Depreciation1Amount());
        assertNull(assetPayment.getPeriod11Depreciation1Amount());
    }
}
