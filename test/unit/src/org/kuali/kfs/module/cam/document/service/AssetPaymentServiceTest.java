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

import static org.kuali.kfs.sys.fixture.UserNameFixture.bomiddle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.fixture.AssetPaymentServiceFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;

@ConfigureContext(session = bomiddle)
public class AssetPaymentServiceTest extends KualiTestBase {
    private static Logger LOG = Logger.getLogger(AssetPaymentServiceTest.class);

    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    private AssetPaymentService assetPaymentService;
    private BusinessObjectService businessObjectService;

    @Override
    // @ConfigureContext(session = UserNameFixture.khuntley)
    protected void setUp() throws Exception {
        super.setUp();
        assetPaymentService = SpringContext.getBean(AssetPaymentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        universityDateService   = SpringContext.getBean(UniversityDateService.class);
        dateTimeService  = SpringContext.getBean(DateTimeService.class);
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
        int detailRows=0;
        int assetRows=0;

        // Creating document
        AssetPaymentDocument document = AssetPaymentServiceFixture.PAYMENT1.newAssetPaymentDocument();
        document.setDocumentHeader(getDocumentHeader());
        document.setAssetPaymentAllocationTypeCode(CamsPropertyConstants.AssetPaymentAllocation.ASSET_DISTRIBUTION_BY_TOTAL_COST_CODE);

        KualiDecimal totalDocument = new KualiDecimal(0);
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = document.getAssetPaymentAssetDetail();
        List<AssetPaymentDetail> assetPaymentDetails = document.getSourceAccountingLines();


        for(AssetPaymentDetail assetPaymentDetail:assetPaymentDetails){
            detailRows++;
            totalDocument = totalDocument.add(assetPaymentDetail.getAmount());
        }

        Double totalHistoricalAmount = new Double(0);
        HashMap<Long,Double>assets = new HashMap();
        HashMap<Long,KualiDecimal>assetsNewCost = new HashMap();

        Map key;
        for(AssetPaymentAssetDetail assetPaymentAssetDetail:assetPaymentAssetDetails) {
            assetRows++;
            assetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentAssetDetail.ASSET);
            assets.put(assetPaymentAssetDetail.getCapitalAssetNumber(), new Double(assetPaymentAssetDetail.getAsset().getTotalCostAmount().toString()));

            assetPaymentAssetDetail.setPreviousTotalCostAmount(assetPaymentAssetDetail.getAsset().getTotalCostAmount());
            totalHistoricalAmount = totalHistoricalAmount +  new Double(assetPaymentAssetDetail.getAsset().getTotalCostAmount().toString());

            LOG.info("***Asset:"+assetPaymentAssetDetail.getCapitalAssetNumber().toString()+" Previous Cost:"+assetPaymentAssetDetail.getAsset().getTotalCostAmount());
        }


        LOG.info("***Saving Document:"+document.getDocumentHeader().getDocumentNumber());

        // Saving document **************************
        this.businessObjectService.save(document);
        // ******************************************

        LOG.info("***Processing Document:"+document.getDocumentNumber());
        // *********Saving asset payment, only. *********************
        this.assetPaymentService.processApprovedAssetPayment(document);
        // ***********************************************************

        // ********** Testing data **********************
        key = new HashMap();
        key.put(KRADPropertyConstants.DOCUMENT_NUMBER,document.getDocumentNumber());

        LOG.info("***Retrieving Document:"+document.getDocumentNumber());
        // Checking that total cost was updated in the asset table
        document = (AssetPaymentDocument) businessObjectService.findByPrimaryKey(AssetPaymentDocument.class, key);
        document.setAssetPaymentAllocationTypeCode(CamsPropertyConstants.AssetPaymentAllocation.ASSET_DISTRIBUTION_BY_TOTAL_COST_CODE);
        KualiDecimal calculatedAssetNewCost;
        KualiDecimal assetOldCost;


        // Getting the number of records in the asset payment
        key = new HashMap();
        key.put(KRADPropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        List<AssetPayment> assetPayments = (List<AssetPayment>) businessObjectService.findMatching(AssetPayment.class, key);

        // Checking that all rows were saved
        assertEquals(assetPayments.size(), (assetRows*detailRows));


        //Comparing records by record
        for (int x = 0; x < document.getAssetPaymentAssetDetail().size(); x++) {
            AssetPaymentAssetDetail assetPaymentAssetDetail =document.getAssetPaymentAssetDetail().get(x);
            assetPaymentAssetDetail.refreshReferenceObject("asset");
            Long capitalAssetNumber =  assetPaymentAssetDetail.getAsset().getCapitalAssetNumber();

            key = new HashMap();
            key.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, capitalAssetNumber);
            key.put(KRADPropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
            assetPayments = (List<AssetPayment>) businessObjectService.findMatching(AssetPayment.class, key);

            calculatedAssetNewCost = new KualiDecimal(assets.get(capitalAssetNumber));
            Double previousTotalCostAmount = assets.get(capitalAssetNumber);
            Double percentage = (previousTotalCostAmount/totalHistoricalAmount);

            for (int i = 0; i < document.getSourceAccountingLines().size(); i++) {
                AssetPaymentDetail assetPaymentDetail =(AssetPaymentDetail)document.getSourceAccountingLines().get(i);
                Double paymentAmount = new Double(assetPaymentDetail.getAmount().toString());
                KualiDecimal amount = new KualiDecimal(paymentAmount.doubleValue() * percentage.doubleValue());
                calculatedAssetNewCost = calculatedAssetNewCost.add(amount);

                //                LOG.info("*****************************************************************************");
                //                LOG.info("***Asset: "+capitalAssetNumber);
                //                LOG.info("***Previous Cost:"+previousTotalCostAmount);
                //                LOG.info("***New      Cost:"+assetPaymentAssetDetail.getAsset().getTotalCostAmount());
                //                LOG.info("*** % :"+ percentage + " - Total Historical Cost:"+totalHistoricalAmount);
                //                LOG.info("***Payment amount:"+paymentAmount);
                //                LOG.info("***Calculated Amount:"+amount);
                //                LOG.info("***Calculated new cost:"+calculatedAssetNewCost);
                //                LOG.info("*****************************************************************************");


                // Checking fields were saved in the asset payment table
                AssetPayment assetPayment = assetPayments.get(i);

                assertEquals(assetPaymentDetail.getAccountNumber(), assetPayment.getAccountNumber());
                assertEquals(assetPaymentDetail.getChartOfAccountsCode(), assetPayment.getChartOfAccountsCode());
                assertEquals(assetPaymentDetail.getFinancialObjectCode(), assetPayment.getFinancialObjectCode());
                assertEquals(amount, assetPayment.getAccountChargeAmount());
                assertEquals(assetPaymentDetail.getPostingYear(), assetPayment.getFinancialDocumentPostingYear());
                assertEquals(assetPaymentDetail.getPostingPeriodCode(), assetPayment.getFinancialDocumentPostingPeriodCode());
            }
            assetsNewCost.put(capitalAssetNumber, calculatedAssetNewCost);
        }

        for (int x = 0; x < document.getAssetPaymentAssetDetail().size(); x++) {
            AssetPaymentAssetDetail assetPaymentAssetDetail =document.getAssetPaymentAssetDetail().get(x);
            assetPaymentAssetDetail.refreshReferenceObject("asset");
            Long capitalAssetNumber =  assetPaymentAssetDetail.getAsset().getCapitalAssetNumber();

            calculatedAssetNewCost  = assetsNewCost.get(capitalAssetNumber);
            //assetOldCost            = new KualiDecimal(assets.get(capitalAssetNumber));
            assertEquals(calculatedAssetNewCost, assetPaymentAssetDetail.getAsset().getTotalCostAmount());
        }
    }

    public DocumentHeader getDocumentHeader() throws Exception {
        WorkflowDocument workflowDocument = SpringContext.getBean(org.kuali.rice.krad.workflow.service.WorkflowDocumentService.class).createWorkflowDocument(SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(AssetPaymentDocument.class), GlobalVariables.getUserSession().getPerson());
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getDocumentId());
        documentHeader.setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        documentHeader.setExplanation("New asset payment");
        documentHeader.setDocumentDescription("New asset payment");
        documentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        return documentHeader;
    }


    public void testExtractPostedDatePeriod() throws Exception {
        Calendar currentDate = Calendar.getInstance();

        java.sql.Date jsqlD;

        List<AssetPaymentDetail> assetPaymentDetails = new ArrayList<AssetPaymentDetail>();
        AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int currentYear = universityDateService.getCurrentFiscalYear();
        int testYear = universityDateService.getCurrentFiscalYear() + 1000;

        currentDate.setTime(dateFormat.parse(currentYear+"-01-01"));
        jsqlD = new java.sql.Date(currentDate.getTime().getTime());

        assetPaymentDetail.setExpenditureFinancialDocumentPostedDate(jsqlD);
        assertEquals(assetPaymentService.extractPostedDatePeriod(assetPaymentDetail),true);



        currentDate.setTime(dateFormat.parse(testYear+"-01-01"));
        jsqlD = new java.sql.Date(currentDate.getTime().getTime());

        assetPaymentDetail.setExpenditureFinancialDocumentPostedDate(jsqlD);
        assertEquals(assetPaymentService.extractPostedDatePeriod(assetPaymentDetail),false);
    }
}

