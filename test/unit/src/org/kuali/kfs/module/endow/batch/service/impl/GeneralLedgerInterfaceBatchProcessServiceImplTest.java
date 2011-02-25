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
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.BusinessObjectService;


@ConfigureContext(session = kfs)
public class GeneralLedgerInterfaceBatchProcessServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerInterfaceBatchProcessServiceImplTest.class);

    private GeneralLedgerInterfaceBatchProcessServiceImpl generalLedgerInterfaceBatchProcessServiceImpl;    
    private BusinessObjectService businessObjectService;
    
    private static final BigDecimal DEBIT_AMOUNT = BigDecimal.valueOf(123.45);
    private static final BigDecimal CREDIT_AMOUNT = BigDecimal.valueOf(543.21);
    private static final int NUMBER_OF_RECORDS = 12;
    
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
    public void testGetTransactionDebitCreditCode() {
        LOG.info("testGetTransactionDebitCreditCode() entered.");

        //positive amount....
        BigDecimal transactionAmount = CREDIT_AMOUNT.add(DEBIT_AMOUNT);
        assertTrue("Credit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCode(transactionAmount, EndowConstants.TransactionSubTypeCode.CASH).equals(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE));
        //NON-CASH...
        assertTrue("Debit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCode(transactionAmount, EndowConstants.TransactionSubTypeCode.NON_CASH).equals(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE));
        
        //negative transaction amount...
        transactionAmount = transactionAmount.negate();
        assertTrue("Debit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCode(transactionAmount, EndowConstants.TransactionSubTypeCode.CASH).equals(EndowConstants.KemToGLInterfaceBatchProcess.DEBIT_CODE));
        //NON-CASH...
        assertTrue("Debit Code is not returned.", generalLedgerInterfaceBatchProcessServiceImpl.getTransactionDebitCreditCode(transactionAmount, EndowConstants.TransactionSubTypeCode.NON_CASH).equals(EndowConstants.KemToGLInterfaceBatchProcess.CREDIT_CODE));
        
        LOG.info("testGetTransactionDebitCreditCode() exited.");
    }
    
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
}
