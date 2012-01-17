/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.GeneralLedgerInterfaceBatchProcessStep;
import org.kuali.kfs.module.endow.businessobject.GlInterfaceBatchProcessKemLine;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;


@ConfigureContext(session = kfs)
public class GeneralLedgerInterfaceBatchProcessServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerInterfaceBatchProcessServiceImplTest.class);

    private GeneralLedgerInterfaceBatchProcessServiceImpl generalLedgerInterfaceBatchProcessServiceImpl;    
    private BusinessObjectService businessObjectService;
    
    private static final BigDecimal DEBIT_AMOUNT = BigDecimal.valueOf(123.45);
    private static final BigDecimal CREDIT_AMOUNT = BigDecimal.valueOf(543.21);
    private static final BigDecimal SHORT_TERM_GAIN_LOSS_AMOUNT = BigDecimal.valueOf(123.45);
    private static final BigDecimal LONG_TERM_GAIN_LOSS_AMOUNT = BigDecimal.valueOf(543.21);
    private static final int NUMBER_OF_RECORDS = 12;
    private static final String TEST_OBJECT_CODE = "1890";
    private static final String TEST_CHART_CODE = "BL";
    private static final String TEST_DIFFERENT_CHART_CODE = "UA";
    private static final String TEST_DOCUMENT_TYPE_CODE = "EAD";
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        generalLedgerInterfaceBatchProcessServiceImpl = (GeneralLedgerInterfaceBatchProcessServiceImpl) TestUtils.getUnproxiedService("mockGeneralLedgerInterfaceBatchProcessService");
        
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Update the system parameters to test if r method will work
     * @param parameterValue
     */
    private void setSystemParameters(String parameterValue) {
        //set COMBINE_ENDOWMENT_GL_ENTRIES_IND system parameter
        TestUtils.setSystemParameter(GeneralLedgerInterfaceBatchProcessStep.class, EndowParameterKeyConstants.GLInterfaceBatchProcess.COMBINE_ENDOWMENT_GL_ENTRIES_IND, parameterValue);
    }
    
    /**
     * test to check adding subtotals at the object code level to the chart level subtotals.
     */
    public void testaddTotalsToChartTotals() {
        LOG.info("testaddTotalsToChartTotals() entered.");
        
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectDebitAmountSubTotal = DEBIT_AMOUNT;
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectCreditAmountSubTotal = CREDIT_AMOUNT;
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectNumberOfRecordsSubTotal = NUMBER_OF_RECORDS;
        
        generalLedgerInterfaceBatchProcessServiceImpl.addTotalsToChartTotals();
        
        assertTrue("Adding object code subtotals to chart subtotals failed.", 
                generalLedgerInterfaceBatchProcessServiceImpl.chartDebitAmountSubTotal.equals(DEBIT_AMOUNT) &&
                generalLedgerInterfaceBatchProcessServiceImpl.chartCreditAmountSubTotal.equals(CREDIT_AMOUNT) && 
                generalLedgerInterfaceBatchProcessServiceImpl.chartNumberOfRecordsSubTotal == NUMBER_OF_RECORDS);
        
        LOG.info("testaddTotalsToChartTotals() exited.");
    }
    
    /**
     * add chart level totals to document type totals and reset the chart/object level totals.
     * @see org.kuali.kfs.module.endow.batch.service.impl.GeneralLedgerInterfaceBatchProcessServiceImpl#addChartTotalsToDocumentTypeTotals()
     */
    public void testAddChartTotalsToDocumentTypeTotals() {
        LOG.info("testAddChartTotalsToDocumentTypeTotals() entered.");
        
        generalLedgerInterfaceBatchProcessServiceImpl.chartDebitAmountSubTotal = DEBIT_AMOUNT;
        generalLedgerInterfaceBatchProcessServiceImpl.chartCreditAmountSubTotal = CREDIT_AMOUNT;
        generalLedgerInterfaceBatchProcessServiceImpl.chartNumberOfRecordsSubTotal = NUMBER_OF_RECORDS;
        
        generalLedgerInterfaceBatchProcessServiceImpl.addChartTotalsToDocumentTypeTotals();
        
        assertTrue("Adding chart subtotals to document type subtotals failed.", 
                generalLedgerInterfaceBatchProcessServiceImpl.documentTypeDebitAmountSubTotal.equals(DEBIT_AMOUNT) &&
                generalLedgerInterfaceBatchProcessServiceImpl.documentTypeCreditAmountSubTotal.equals(CREDIT_AMOUNT) && 
                generalLedgerInterfaceBatchProcessServiceImpl.documentTypeNumberOfRecordsSubTotal == 12);

        LOG.info("testAddChartTotalsToDocumentTypeTotals() exited.");
    }
    
    /**
     * add totals to grand totals and reset document level totals...
     */
    protected void addDocumentTypeTotalsToGrandTotals() {
        LOG.info("addDocumentTypeTotalsToGrandTotals() entered.");
        
        generalLedgerInterfaceBatchProcessServiceImpl.documentTypeDebitAmountSubTotal = DEBIT_AMOUNT;
        generalLedgerInterfaceBatchProcessServiceImpl.documentTypeCreditAmountSubTotal = CREDIT_AMOUNT;
        generalLedgerInterfaceBatchProcessServiceImpl.documentTypeNumberOfRecordsSubTotal = NUMBER_OF_RECORDS;
        
        generalLedgerInterfaceBatchProcessServiceImpl.addDocumentTypeTotalsToGrandTotals();
        
        assertTrue("Adding document type subtotals to grand totals failed.", 
                generalLedgerInterfaceBatchProcessServiceImpl.documentTypeDebitAmountGrandTotal.equals(DEBIT_AMOUNT) &&
                generalLedgerInterfaceBatchProcessServiceImpl.documentTypeCreditAmountGrandTotal.equals(CREDIT_AMOUNT) && 
                generalLedgerInterfaceBatchProcessServiceImpl.documentTypeNumberOfRecordsGrandTotal == 12);

        LOG.info("addDocumentTypeTotalsToGrandTotals() exited.");
    }

    /**
     * test method to get the transaction amount
     */
    public void testGetTransactionAmount() {
        LOG.info("testGetTransactionAmount() entered.");
        
        BigDecimal transactionAmount = BigDecimal.ZERO;
        
        GlInterfaceBatchProcessKemLine transactionArchive = new GlInterfaceBatchProcessKemLine();

        transactionArchive.setTypeCode(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE);
        transactionArchive.setSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
        transactionArchive.setTransactionArchiveIncomeAmount(DEBIT_AMOUNT);
        transactionArchive.setTransactionArchivePrincipalAmount(CREDIT_AMOUNT);
        transactionArchive.setHoldingCost(CREDIT_AMOUNT);
        //subtype = CASH so the transaction amount should add income and principal amounts.
        transactionAmount = generalLedgerInterfaceBatchProcessServiceImpl.getTransactionAmount(transactionArchive);
        
        assertTrue("Income and Principal amounts do not equal to transaction amount", transactionAmount.equals(CREDIT_AMOUNT.add(DEBIT_AMOUNT)));
        
        transactionArchive.setSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        //subtype = NON-CASH so the transaction amount should get holding cost.
        transactionAmount = generalLedgerInterfaceBatchProcessServiceImpl.getTransactionAmount(transactionArchive);
        assertTrue("Holding cost does not equal to transaction amount", transactionAmount.equals(CREDIT_AMOUNT));
        
        LOG.info("testGetTransactionAmount() exited.");        
    }
     
    /**
     * method test if correct description is returning.
     */
    public void testGetTransactionDescription() {
        LOG.info("testGetTransactionDescription() entered.");
        
        GlInterfaceBatchProcessKemLine transactionArchive = new GlInterfaceBatchProcessKemLine();
        
        //CASH description
        java.util.Date postedDate = generalLedgerInterfaceBatchProcessServiceImpl.kemService.getCurrentDate(); 
        transactionArchive.setSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
        assertTrue("Incorrect description for CASH subtypecode", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDescription(transactionArchive, postedDate).contains(EndowConstants.KemToGLInterfaceBatchProcess.SUB_TYPE_CASH));

        //NON-CASH description
        transactionArchive.setSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        assertTrue("Incorrect description for CASH subtypecode", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDescription(transactionArchive, postedDate).contains(EndowConstants.KemToGLInterfaceBatchProcess.SUB_TYPE_NON_CASH));
        
        LOG.info("testGetTransactionDescription() exited.");        
    }
    
    /**
     * test method to get credit or debit code based on the amount value.
     */
 //   public void testGetTransactionDebitCreditCode() {
 //       LOG.info("testGetTransactionDebitCreditCode() entered.");

  //      //positive amount....
 //       BigDecimal transactionAmount = CREDIT_AMOUNT.add(DEBIT_AMOUNT);
 //       assertTrue("Credit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCode(transactionAmount, EndowConstants.TransactionSubTypeCode.CASH).equals(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE));
 //       //CASH...
 //       assertTrue("Debit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCode(transactionAmount, EndowConstants.TransactionSubTypeCode.NON_CASH).equals(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE));
 //       
 //       //negative transaction amount...
 //       transactionAmount = transactionAmount.negate();
  //      assertTrue("Debit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCode(transactionAmount, EndowConstants.TransactionSubTypeCode.CASH).equals(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE));
 //       //NON-CASH...
 //       assertTrue("Debit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCode(transactionAmount, EndowConstants.TransactionSubTypeCode.NON_CASH).equals(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE));
 //       
//        LOG.info("testGetTransactionDebitCreditCode() exited.");
//    }
    
    /**
     * test method to get credit or debit code based on the amount for offset entries...
     */
    public void testGetTransactionDebitCreditCodeForOffSetEntry() {
        LOG.info("testGetTransactionDebitCreditCodeForOffSetEntry() entered.");

        //positive amount....
        BigDecimal transactionAmount = CREDIT_AMOUNT.add(DEBIT_AMOUNT);
        assertTrue("Credit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCodeForOffSetEntry(transactionAmount).equals(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE));
        //negative transaction amount...
        transactionAmount = transactionAmount.negate();
        assertTrue("Debit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCodeForOffSetEntry(transactionAmount).equals(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE));
        
        LOG.info("testGetTransactionDebitCreditCodeForOffSetEntry() exited.");
    }
    
    /**
     * test method to check updateTotals
     */
    public void testUpdateTotals() {
        LOG.info("testUpdateTotals() entered.");
        
        GlInterfaceBatchProcessKemLine transactionArchive = new GlInterfaceBatchProcessKemLine();
        
        //CASH description
        transactionArchive.setTypeCode(EndowConstants.DocumentTypeNames.ENDOWMENT_ASSET_INCREASE);
        transactionArchive.setSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
        transactionArchive.setObjectCode(TEST_OBJECT_CODE);
        transactionArchive.setTransactionArchiveIncomeAmount(DEBIT_AMOUNT);
        transactionArchive.setTransactionArchivePrincipalAmount(CREDIT_AMOUNT);
        transactionArchive.setHoldingCost(CREDIT_AMOUNT);
        
        BigDecimal transactionAmount = generalLedgerInterfaceBatchProcessServiceImpl.getTransactionAmount(transactionArchive);
        generalLedgerInterfaceBatchProcessServiceImpl.updateTotals(transactionArchive);        
        assertTrue("Update Totals when Cash is subtypecode has failed.", transactionAmount.equals(generalLedgerInterfaceBatchProcessServiceImpl.chartObjectCreditAmountSubTotal));
        //rest the values...
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectCreditAmountSubTotal = BigDecimal.ZERO;
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectDebitAmountSubTotal = BigDecimal.ZERO;
        
        transactionArchive.setTransactionArchiveIncomeAmount(DEBIT_AMOUNT.negate());
        transactionArchive.setTransactionArchivePrincipalAmount(CREDIT_AMOUNT.negate());
        transactionArchive.setHoldingCost(CREDIT_AMOUNT.negate());
        transactionAmount = transactionAmount.negate();
        generalLedgerInterfaceBatchProcessServiceImpl.updateTotals(transactionArchive);        
        assertTrue("Update Totals when Cash is subtypecode has failed.", transactionAmount.equals(generalLedgerInterfaceBatchProcessServiceImpl.chartObjectDebitAmountSubTotal));
        //rest the values...
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectCreditAmountSubTotal = BigDecimal.ZERO;
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectDebitAmountSubTotal = BigDecimal.ZERO;
        
        //non-cash type....
        transactionArchive.setSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        //subtype = NON-CASH so the transaction amount should get holding cost.
        transactionAmount = SHORT_TERM_GAIN_LOSS_AMOUNT.add(LONG_TERM_GAIN_LOSS_AMOUNT);
        transactionArchive.setObjectCode(SpringContext.getBean(ParameterService.class).getParameterValueAsString(GeneralLedgerInterfaceBatchProcessStep.class, EndowParameterKeyConstants.GLInterfaceBatchProcess.CASH_SALE_GAIN_LOSS_OBJECT_CODE));
        transactionArchive.setShortTermGainLoss(SHORT_TERM_GAIN_LOSS_AMOUNT);
        transactionArchive.setLongTermGainLoss(LONG_TERM_GAIN_LOSS_AMOUNT);
        generalLedgerInterfaceBatchProcessServiceImpl.updateTotals(transactionArchive);        
        assertTrue("Update Totals when Non-Cash is subtypecode has failed.", transactionAmount.equals(generalLedgerInterfaceBatchProcessServiceImpl.chartObjectCreditAmountSubTotal));
        //rest the values...
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectCreditAmountSubTotal = BigDecimal.ZERO;
        generalLedgerInterfaceBatchProcessServiceImpl.chartObjectDebitAmountSubTotal = BigDecimal.ZERO;
        
        transactionAmount = transactionAmount.negate();
        transactionArchive.setShortTermGainLoss(SHORT_TERM_GAIN_LOSS_AMOUNT.negate());
        transactionArchive.setLongTermGainLoss(LONG_TERM_GAIN_LOSS_AMOUNT.negate());
        generalLedgerInterfaceBatchProcessServiceImpl.updateTotals(transactionArchive);        
        assertTrue("Update Totals when Non-Cash is subtypecode has failed.", transactionAmount.equals(generalLedgerInterfaceBatchProcessServiceImpl.chartObjectDebitAmountSubTotal));
        
        LOG.info("testUpdateTotals() exited.");        
    }
    
    /**
     * test method to check the code in the method updateTotalsProcessed()
     */
    public void testUpdateTotalsProcessed_SameChartAndObject() {
        LOG.info("testUpdateTotals() entered.");
        
        GlInterfaceBatchProcessKemLine transactionArchive = getGlInterfaceBatchProcessKemLine();
        //set previous chart, object, and account same as the transaction archive...so update can happen...
        generalLedgerInterfaceBatchProcessServiceImpl.previousChartCode = TEST_CHART_CODE;
        generalLedgerInterfaceBatchProcessServiceImpl.previousDocumentTypeCode = TEST_DOCUMENT_TYPE_CODE;
        generalLedgerInterfaceBatchProcessServiceImpl.previousObjectCode = TEST_OBJECT_CODE;
        
        BigDecimal transactionAmount = generalLedgerInterfaceBatchProcessServiceImpl.getTransactionAmount(transactionArchive);
        generalLedgerInterfaceBatchProcessServiceImpl.updateTotalsProcessed(transactionArchive);        
      //  assertTrue("Update Totals when Cash is subtypecode has failed.", transactionAmount.equals(generalLedgerInterfaceBatchProcessServiceImpl.chartObjectCreditAmountSubTotal));
        
        generalLedgerInterfaceBatchProcessServiceImpl.documentTypeCreditAmountSubTotal = BigDecimal.ZERO;
        generalLedgerInterfaceBatchProcessServiceImpl.documentTypeDebitAmountSubTotal  = BigDecimal.ZERO;
    }
    
    /**
     * test method to check the code in the method updateTotalsProcessed()
     */
    public void testUpdateTotalsProcessed_DifferentChart() {
        LOG.info("testUpdateTotals() entered.");
        
        generalLedgerInterfaceBatchProcessServiceImpl.initializeChartObjectTotals();
        generalLedgerInterfaceBatchProcessServiceImpl.chartDebitAmountSubTotal = BigDecimal.ZERO;
        generalLedgerInterfaceBatchProcessServiceImpl.chartCreditAmountSubTotal = BigDecimal.ZERO;
        generalLedgerInterfaceBatchProcessServiceImpl.documentTypeDebitAmountSubTotal = BigDecimal.ZERO;
        generalLedgerInterfaceBatchProcessServiceImpl.documentTypeCreditAmountSubTotal = BigDecimal.ZERO;
        
        GlInterfaceBatchProcessKemLine transactionArchive = getGlInterfaceBatchProcessKemLine();
        //set previous chart, object, and account same as the transaction archive...so update can happen...
        generalLedgerInterfaceBatchProcessServiceImpl.previousChartCode = TEST_DIFFERENT_CHART_CODE;
        generalLedgerInterfaceBatchProcessServiceImpl.previousDocumentTypeCode = TEST_DOCUMENT_TYPE_CODE;
        generalLedgerInterfaceBatchProcessServiceImpl.previousObjectCode = TEST_OBJECT_CODE;
        
        BigDecimal transactionAmount = generalLedgerInterfaceBatchProcessServiceImpl.getTransactionAmount(transactionArchive);
        generalLedgerInterfaceBatchProcessServiceImpl.updateTotals(transactionArchive);        
        generalLedgerInterfaceBatchProcessServiceImpl.updateTotalsProcessed(transactionArchive);        
      //  assertTrue("Update Totals when Cash is subtypecode has failed.", transactionAmount.equals(generalLedgerInterfaceBatchProcessServiceImpl.documentTypeCreditAmountSubTotal));
    }
    
    /**
     * Helper method to create the transient business object GlInterfaceBatchProcessKemLine
     * for testing purposes.
     */
    private GlInterfaceBatchProcessKemLine getGlInterfaceBatchProcessKemLine() {
        GlInterfaceBatchProcessKemLine transactionArchive = new GlInterfaceBatchProcessKemLine();
        
        transactionArchive.setChartCode(TEST_CHART_CODE);
        transactionArchive.setSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
        transactionArchive.setObjectCode(TEST_OBJECT_CODE);
        transactionArchive.setTransactionArchiveIncomeAmount(DEBIT_AMOUNT);
        transactionArchive.setTransactionArchivePrincipalAmount(CREDIT_AMOUNT);
        transactionArchive.setHoldingCost(CREDIT_AMOUNT);
        transactionArchive.setShortTermGainLoss(SHORT_TERM_GAIN_LOSS_AMOUNT);
        transactionArchive.setLongTermGainLoss(LONG_TERM_GAIN_LOSS_AMOUNT);
        transactionArchive.setTypeCode(TEST_DOCUMENT_TYPE_CODE);
        
        return transactionArchive;
    }
}
