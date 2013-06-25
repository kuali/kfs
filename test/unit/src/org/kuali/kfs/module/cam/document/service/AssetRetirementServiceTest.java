/*
 * Copyright 2009 The Kuali Foundation
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

import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.gl.AssetRetirementGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.fixture.AssetRetirementGlobalMaintainableFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.DocumentService;

public class AssetRetirementServiceTest extends KualiTestBase {
    private AssetRetirementService assetRetirementService;
    private UniversityDateService universityDateService;

    @Override
    @ConfigureContext(session = UserNameFixture.bomiddle, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        assetRetirementService = SpringContext.getBean(AssetRetirementService.class);
        universityDateService = SpringContext.getBean(UniversityDateService.class);
    }


    /**
     * Test capital asset with active payments
     * 
     * @throws Exception
     */
    @ConfigureContext(session = UserNameFixture.bomiddle, shouldCommitTransactions = false)
    public void testCreateGLPostables_Success() throws Exception {
        MaintenanceDocument document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(CamsConstants.DocumentTypeName.ASSET_RETIREMENT_GLOBAL);
        AssetRetirementGlobal assetRetirementGlobal = AssetRetirementGlobalMaintainableFixture.RETIREMENT1.newAssetRetirement();
        // create poster
        AssetRetirementGeneralLedgerPendingEntrySource assetRetirementGlPoster = new AssetRetirementGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) document.getDocumentHeader());
        assetRetirementService.createGLPostables(assetRetirementGlobal, assetRetirementGlPoster);
        List<GeneralLedgerPendingEntrySourceDetail> postables = assetRetirementGlPoster.getPostables();

        assertFalse(postables.isEmpty());
        assertEquals(10, postables.size());

        int i = 0;
        for (AssetRetirementGlobalDetail detail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            Asset asset = detail.getAsset();
            // assert gl postable for first payment
            AssetPayment payment1 = asset.getAssetPayments().get(0);
            assertGLPostable(postables.get(i++), asset.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccountChargeAmount(), "9520004", "Asset retirement cost reversal entry", "8610");
            assertGLPostable(postables.get(i++), asset.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccumulatedPrimaryDepreciationAmount(), "9520004", "Asset retirement depreciation reversal", "8910");
            assertGLPostable(postables.get(i++), asset.getOrganizationOwnerChartOfAccountsCode(), payment1.getAccountChargeAmount().subtract(payment1.getAccumulatedPrimaryDepreciationAmount()), "9520004", "Asset retirement fund balance adjustment", "4998");

            AssetPayment payment2 = asset.getAssetPayments().get(1);
            assertGLPostable(postables.get(i++), asset.getOrganizationOwnerChartOfAccountsCode(), payment2.getAccountChargeAmount(), "9520004", "Asset retirement cost reversal entry", "8610");
            assertGLPostable(postables.get(i++), asset.getOrganizationOwnerChartOfAccountsCode(), payment2.getAccumulatedPrimaryDepreciationAmount(), "9520004", "Asset retirement depreciation reversal", "8910");
        }
    }

    /**
     * Assert GL postable entry
     * 
     * @param glPostable
     * @param chartOfAccountsCode
     * @param amount
     * @param plantAccount
     * @param financialLineDesc
     * @param financialObjectCode
     */
    private void assertGLPostable(GeneralLedgerPendingEntrySourceDetail glPostable, String chartOfAccountsCode, KualiDecimal amount, String plantAccount, String financialLineDesc, String financialObjectCode) {
        assertEquals(plantAccount, glPostable.getAccountNumber());
        assertEquals(amount, glPostable.getAmount());
        assertEquals(CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC, glPostable.getBalanceTypeCode());
        assertEquals(chartOfAccountsCode, glPostable.getChartOfAccountsCode());
        assertEquals(financialLineDesc, glPostable.getFinancialDocumentLineDescription());
        assertEquals(financialObjectCode, glPostable.getFinancialObjectCode());
        assertEquals(this.universityDateService.getCurrentFiscalYear(), glPostable.getPostingYear());
        assertNull(glPostable.getOrganizationReferenceId());
        assertNull(glPostable.getProjectCode());
        assertNull(glPostable.getReferenceNumber());
        assertNull(glPostable.getReferenceOriginCode());
        assertNull(glPostable.getReferenceTypeCode());
    }
}

