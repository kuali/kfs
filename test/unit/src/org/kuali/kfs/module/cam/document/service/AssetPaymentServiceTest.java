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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.cams.fixture.AssetPaymentServiceFixture;
import org.kuali.test.ConfigureContext;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.RicePropertyConstants;

// @ConfigureContext(session = KHUNTLEY)
@ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
public class AssetPaymentServiceTest extends KualiTestBase {

    private AssetPaymentService assetPaymentService;
    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;

    @Override
    // @ConfigureContext(session = UserNameFixture.KHUNTLEY)
    protected void setUp() throws Exception {
        super.setUp();
        assetPaymentService = SpringContext.getBean(AssetPaymentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
    }

    public void testAdjustPaymentAmounts_params_false_false() throws Exception {
        AssetPayment assetPayment = AssetPaymentServiceFixture.PAYMENT1.newAssetPayment();
        this.assetPaymentService.adjustPaymentAmounts(assetPayment, false, false);
        assertTrue(assetPayment.getAccountChargeAmount().isPositive());
        assertTrue(assetPayment.getAccumulatedPrimaryDepreciationAmount().isPositive());
        assertNotNull(assetPayment.getPeriod1Depreciation1Amount());
        assertNotNull(assetPayment.getPeriod11Depreciation1Amount());

        assertTrue(assetPayment.getPeriod11Depreciation1Amount() instanceof KualiDecimal);
    }

    public void testAdjustPaymentAmounts_params_true_true() throws Exception {
        AssetPayment assetPayment = AssetPaymentServiceFixture.PAYMENT1.newAssetPayment();
        this.assetPaymentService.adjustPaymentAmounts(assetPayment, true, true);
        assertTrue(assetPayment.getAccountChargeAmount().isNegative());
        assertTrue(assetPayment.getAccumulatedPrimaryDepreciationAmount().isNegative());
        assertNull(assetPayment.getPeriod1Depreciation1Amount());
        assertNull(assetPayment.getPeriod11Depreciation1Amount());

        assertFalse(assetPayment.getPeriod11Depreciation1Amount() instanceof KualiDecimal);
    }

    public void testAdjustPaymentAmounts_false_true() throws Exception {
        AssetPayment assetPayment = AssetPaymentServiceFixture.PAYMENT1.newAssetPayment();
        this.assetPaymentService.adjustPaymentAmounts(assetPayment, false, true);
        assertTrue(assetPayment.getAccountChargeAmount().isPositive());
        assertTrue(assetPayment.getAccumulatedPrimaryDepreciationAmount().isPositive());
        assertNull(assetPayment.getPeriod1Depreciation1Amount());
        assertNull(assetPayment.getPeriod2Depreciation1Amount());
        assertNull(assetPayment.getPeriod3Depreciation1Amount());
        assertNull(assetPayment.getPeriod4Depreciation1Amount());
        assertNull(assetPayment.getPeriod5Depreciation1Amount());
        assertNull(assetPayment.getPeriod6Depreciation1Amount());
        assertNull(assetPayment.getPeriod7Depreciation1Amount());
        assertNull(assetPayment.getPeriod8Depreciation1Amount());
        assertNull(assetPayment.getPeriod9Depreciation1Amount());
        assertNull(assetPayment.getPeriod10Depreciation1Amount());
        assertNull(assetPayment.getPeriod11Depreciation1Amount());
        assertNull(assetPayment.getPeriod12Depreciation1Amount());
    }


    public void testProcessApprovedAssetPayment() throws Exception {
        // Creating document
        AssetPaymentDocument document = AssetPaymentServiceFixture.PAYMENT1.newAssetPaymentDocument();
        document.setDocumentHeader(getDocumentHeader());
        document.setSourceAccountingLines(document.getAssetPaymentDetail());

        Map key = new HashMap();
        key.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, document.getAsset().getCapitalAssetNumber());
        
        Asset asset = (Asset) businessObjectService.findByPrimaryKey(Asset.class, key);
        document.setAsset(asset);
        document.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        document.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());
        document.setPreviousTotalCostAmount(new KualiDecimal(0));

        // Saving document **************************
        this.businessObjectService.save(document);
        // ******************************************

        // Getting current total cost before update
        KualiDecimal previousAssetTotalCost = asset.getTotalCostAmount();
        KualiDecimal newAssetTotalCost = asset.getTotalCostAmount();
        KualiDecimal documentTotal = new KualiDecimal(0);
        for (AssetPaymentDetail assetPaymentDetail : document.getAssetPaymentDetail()) {
            newAssetTotalCost = newAssetTotalCost.add(assetPaymentDetail.getAmount());
            documentTotal = documentTotal.add(assetPaymentDetail.getAmount());
        }
        // *********Saving asset payment, only. *********************
        this.assetPaymentService.processApprovedAssetPayment(document);
        // ***********************************************************

        // ********** Testing data **********************
        key = new HashMap();
        key.put(RicePropertyConstants.DOCUMENT_NUMBER,document.getDocumentNumber());

        // Checking that total cost was updated in the asset table
        document = (AssetPaymentDocument) businessObjectService.findByPrimaryKey(AssetPaymentDocument.class, key);
        assertEquals(newAssetTotalCost, document.getAsset().getTotalCostAmount());

        //Checking the previous cost is updated in the asset document table
       assertEquals(document.getPreviousTotalCostAmount(),previousAssetTotalCost);
       
        key = new HashMap();
        key.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, document.getAsset().getCapitalAssetNumber());
        key.put(RicePropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        List<AssetPayment> assetPayments = (List<AssetPayment>) businessObjectService.findMatching(AssetPayment.class, key);

        // Checking that all rows were saved
        assertEquals(assetPayments.size(), document.getAssetPaymentDetail().size());

        // Checking fields were saved
        for (int i = 0; i < document.getAssetPaymentDetail().size(); i++) {
            AssetPaymentDetail assetPaymentDetail = document.getAssetPaymentDetail().get(i);
            AssetPayment assetPayment = assetPayments.get(i);

            assertEquals(assetPaymentDetail.getAccountNumber(), assetPayment.getAccountNumber());
            assertEquals(assetPaymentDetail.getChartOfAccountsCode(), assetPayment.getChartOfAccountsCode());
            assertEquals(assetPaymentDetail.getFinancialObjectCode(), assetPayment.getFinancialObjectCode());
            assertEquals(assetPaymentDetail.getAmount(), assetPayment.getAccountChargeAmount());
            assertEquals(assetPaymentDetail.getFinancialDocumentPostingYear(), assetPayment.getFinancialDocumentPostingYear());
            assertEquals(assetPaymentDetail.getFinancialDocumentPostingPeriodCode(), assetPayment.getFinancialDocumentPostingPeriodCode());
            assertEquals(assetPaymentDetail.getPaymentApplicationDate(), assetPayment.getFinancialDocumentPostingDate());
        }
    }

    public DocumentHeader getDocumentHeader() throws Exception {
        KualiWorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument("AssetPaymentDocument", GlobalVariables.getUserSession().getUniversalUser());
        DocumentHeader documentHeader = new DocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getRouteHeaderId().toString());
        documentHeader.setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        documentHeader.setExplanation("New asset payment");
        documentHeader.setFinancialDocumentDescription("New asset payment");
        documentHeader.setFinancialDocumentTotalAmount(new KualiDecimal(0));
        return documentHeader;
    }
}
