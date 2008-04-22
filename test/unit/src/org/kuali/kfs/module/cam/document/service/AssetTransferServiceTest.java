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

import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetRetirementReason;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.fixture.AssetTransferFixture;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.UserNameFixture;

public class AssetTransferServiceTest extends KualiTestBase {

    private AssetTransferService assetTransferService;

    @Override
    @ConfigureContext(session = UserNameFixture.KHUNTLEY)
    protected void setUp() throws Exception {
        super.setUp();
        assetTransferService = SpringContext.getBean(AssetTransferService.class);
    }

    @SuppressWarnings("deprecation")
    public void testIsTransferable_RetiredAsset() throws Exception {
        assertNotNull(assetTransferService);
        AssetTransferDocument document = new AssetTransferDocument();
        Asset newAsset = AssetTransferFixture.RETIRED_ASSET.newAsset();
        AssetRetirementReason reason = new AssetRetirementReason();
        reason.setRetirementReasonCode(newAsset.getRetirementReasonCode());
        reason.setRetirementReasonName("Test Retired");
        newAsset.setRetirementReason(reason);
        document.setAsset(newAsset);
        assertFalse(this.assetTransferService.isTransferable(document));
    }

    public void testIsTransferable_Success() throws Exception {
        assertNotNull(assetTransferService);
        AssetTransferDocument document = AssetTransferFixture.ASSET_TRANSFER.newAssetTransferDocument();
        document.setAsset(AssetTransferFixture.ACTIVE_ASSET.newAsset());
        assertTrue(this.assetTransferService.isTransferable(document));
    }

    public void testIsTransferable_LockedAsset() throws Exception {
        // TODO this is pending implementation
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = false)
    public void testCreateGLPostables_Success() throws Exception {
        // set up the data
        AssetTransferDocument document = buildTransferDocument();
        this.assetTransferService.createGLPostables(document);
        // assert gl postables
        List<GeneralLedgerPendingEntrySourceDetail> generalLedgerPostables = document.getGeneralLedgerPostables();
        assertFalse(generalLedgerPostables.isEmpty());
        assertEquals(12, generalLedgerPostables.size());
        Asset asset = document.getAsset();
        // assert source gl postable for first payment
        AssetPayment payment1 = asset.getAssetPayments().get(0);
        assertGLPostable(generalLedgerPostables.get(0), asset.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccountChargeAmount(), "9520004", "Reverse asset cost", "8610");
        assertGLPostable(generalLedgerPostables.get(1), asset.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccumulatedPrimaryDepreciationAmount(), "9520004", "Reverse accumulated depreciation", "8910");
        assertGLPostable(generalLedgerPostables.get(2), asset.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccountChargeAmount().subtract(payment1.getAccumulatedPrimaryDepreciationAmount()), "9520004", "Reverse offset amount", "9899");

        // assert source gl postable for second payment
        AssetPayment payment2 = asset.getAssetPayments().get(1);
        assertGLPostable(generalLedgerPostables.get(3), asset.getOrganizationOwnerChartOfAccountsCode(), payment2.getAccountChargeAmount(), "9520004", "Reverse asset cost", "8610");
        assertGLPostable(generalLedgerPostables.get(4), asset.getOrganizationOwnerChartOfAccountsCode(), payment2.getAccumulatedPrimaryDepreciationAmount(), "9520004", "Reverse accumulated depreciation", "8910");
        assertGLPostable(generalLedgerPostables.get(5), asset.getOrganizationOwnerChartOfAccountsCode(), payment2.getAccountChargeAmount().subtract(payment2.getAccumulatedPrimaryDepreciationAmount()), "9520004", "Reverse offset amount", "9899");

        // assert target gl postable for first payment
        assertGLPostable(generalLedgerPostables.get(6), document.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccountChargeAmount(), "9567077", "Transfer asset cost", "8610");
        assertGLPostable(generalLedgerPostables.get(7), document.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccumulatedPrimaryDepreciationAmount(), "9567077", "Transfer accumulated depreciation", "8910");
        assertGLPostable(generalLedgerPostables.get(8), document.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccountChargeAmount().subtract(payment1.getAccumulatedPrimaryDepreciationAmount()), "9567077", "Transfer offset amount", "9899");

        // assert target gl postable for second payment
        assertGLPostable(generalLedgerPostables.get(9), document.getOrganizationOwnerChartOfAccountsCode(), payment2.getAccountChargeAmount(), "9567077", "Transfer asset cost", "8610");
        assertGLPostable(generalLedgerPostables.get(10), document.getOrganizationOwnerChartOfAccountsCode(), payment2.getAccumulatedPrimaryDepreciationAmount(), "9567077", "Transfer accumulated depreciation", "8910");
        assertGLPostable(generalLedgerPostables.get(11), document.getOrganizationOwnerChartOfAccountsCode(), payment2.getAccountChargeAmount().subtract(payment2.getAccumulatedPrimaryDepreciationAmount()), "9567077", "Transfer offset amount", "9899");


    }

    private void assertGLPostable(GeneralLedgerPendingEntrySourceDetail glPostable, String chartOfAccountsCode, KualiDecimal amount, String plantAccount, String financialLineDesc, String financialObjectCode) {
        assertEquals(plantAccount, glPostable.getAccountNumber());
        assertEquals(amount, glPostable.getAmount());
        assertEquals(CamsConstants.GL_BALANCE_TYPE_CDE_AC, glPostable.getBalanceTypeCode());
        assertEquals(chartOfAccountsCode, glPostable.getChartOfAccountsCode());
        assertEquals(financialLineDesc, glPostable.getFinancialDocumentLineDescription());
        assertEquals(financialObjectCode, glPostable.getFinancialObjectCode());
        assertEquals(Integer.valueOf(2008), glPostable.getPostingYear());
        assertNull(glPostable.getOrganizationReferenceId());
        assertNull(glPostable.getProjectCode());
        assertNull(glPostable.getReferenceNumber());
        assertNull(glPostable.getReferenceOriginCode());
        assertNull(glPostable.getReferenceTypeCode());
    }

    private AssetTransferDocument buildTransferDocument() {
        AssetTransferDocument document = AssetTransferFixture.ASSET_TRANSFER.newAssetTransferDocument();
        Asset asset = AssetTransferFixture.ACTIVE_ASSET.newAsset();
        asset.setCapitalAssetNumber(null);
        asset.setCapitalAssetTypeCode("665");
        AssetPayment payment1 = AssetTransferFixture.PAYMENT1.newAssetPayment();
        AssetPayment payment2 = AssetTransferFixture.PAYMENT2.newAssetPayment();
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        boService.save(asset);
        payment1.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        payment1.setPaymentSequenceNumber(1);
        payment2.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        payment2.setPaymentSequenceNumber(2);
        boService.save(payment1);
        boService.save(payment2);
        payment1.refresh();
        payment2.refresh();
        asset.getAssetPayments().add(payment1);
        asset.getAssetPayments().add(payment2);
        document.setAsset(asset);
        return document;
    }
}
