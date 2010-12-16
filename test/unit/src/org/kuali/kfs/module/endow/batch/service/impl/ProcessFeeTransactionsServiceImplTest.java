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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeEndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeTransaction;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.FeeEndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.FeeMethodFixture;
import org.kuali.kfs.module.endow.fixture.FeeTransactionFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.TransactionArchiveFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;

@ConfigureContext(session = kfs)
public class ProcessFeeTransactionsServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessFeeTransactionsServiceImplTest.class);

    private ProcessFeeTransactionsServiceImpl processFeeTransactionsServiceImpl;    
    private BusinessObjectService businessObjectService;
    
    //the properties to hold count, total amounts and fee etc.
    private long totalNumberOfRecords = 0;
    private BigDecimal totalAmountCalculated = BigDecimal.ZERO;
    private BigDecimal feeToBeCharged = BigDecimal.ZERO;    
    private BigDecimal transactionIncomeAmount = BigDecimal.ZERO;
    private BigDecimal transacationPrincipalAmount = BigDecimal.ZERO;
    private BigDecimal totalHoldingUnits = BigDecimal.ZERO;
    
    private FeeMethod feeMethod1;
    private FeeMethod feeMethod2;
    private KEMID kemid;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        processFeeTransactionsServiceImpl = (ProcessFeeTransactionsServiceImpl) TestUtils.getUnproxiedService("mockProcessFeeTransactionsService");

        EndowmentTransactionCode endowmentTransactionCode1 = EndowmentTransactionCodeFixture.EXPENSE_TRANSACTION_CODE.createEndowmentTransactionCode();
        EndowmentTransactionCode endowmentTransactionCode2 = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        EndowmentTransactionCode endowmentTransactionCode3 = EndowmentTransactionCodeFixture.ASSET_TRANSACTION_CODE.createEndowmentTransactionCode();
        
        feeMethod1 = FeeMethodFixture.FEE_METHOD_RECORD1.createFeeMethodRecord();
        feeMethod2 = FeeMethodFixture.FEE_METHOD_RECORD2.createFeeMethodRecord();
        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        
    }
    
    /**
     * method to set totalWaivedFeesThisFiscalYear field to non-zero value so 
     * after calling the method to update the records, we can check for any records
     * matching this non-zero value.  After the update method called, all the records
     * should have 0 in totalWaivedFeesThisFiscalYear field in the table.
     */
    private void setTotalWaivedFeesThisFiscalYearInKemidFee() {
        //setting all the records TotalWaivedFeesThisFiscalYear = 1000 so we can call the update method
        //and then check for this value to make sure that method succeded.
        Collection <KemidFee> kemidRecords = businessObjectService.findAll(KemidFee.class);
        for (KemidFee kemidFee : kemidRecords) {
            kemidFee.setTotalWaivedFeesThisFiscalYear(new KualiDecimal(1000L));
            businessObjectService.save(kemidFee);
        }
    }
    
    /**
     * Update the system parameters to test if updateKemidFeeWaivedYearToDateAmount method will work
     * @param useProcessDate value for setting USE_PROCESS_DATE system parameter
     * @param currentProcessDate value for setting CURRENT_PROCESS_DATE system parameter
     * @param fiscalYearEndMonthDay value for setting FISCAL_YEAR_END_MONTH_AND_DAY system parameter
     */
    private void setSystemParameters(String useProcessDate, String currentProcessDate, String fiscalYearEndMonthDay) {
        //set USE_PROCESS_DATE_IND system parameter
        TestUtils.setSystemParameter(KfsParameterConstants.ENDOWMENT_ALL.class, EndowParameterKeyConstants.USE_PROCESS_DATE, useProcessDate);
        //set USE_PROCESS_DATE_IND system parameter
        TestUtils.setSystemParameter(KfsParameterConstants.ENDOWMENT_ALL.class, EndowParameterKeyConstants.CURRENT_PROCESS_DATE, currentProcessDate);
        // set the FISCAL_YEAR_END_MONTH_AND_DAY parameter
        TestUtils.setSystemParameter(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowParameterKeyConstants.FISCAL_YEAR_END_MONTH_AND_DAY, fiscalYearEndMonthDay);
    }
    
    /**
     * test to validate updateKemidFeeWaivedYearToDateAmount() method in the rule class
     */
    public void testUpdateKemidFeeWaivedYearToDateAmount() {
        LOG.info("method testUpdateKemidFeeWaivedYearToDateAmount() entered.");
        
        setTotalWaivedFeesThisFiscalYearInKemidFee();
        
        //intentionally setting incorrect values to the update will not happen
        setSystemParameters("N", "07/02/2010", "0530");
        boolean updated = processFeeTransactionsServiceImpl.updateKemidFeeWaivedYearToDateAmount();
        
        Map criteria = new HashMap();
        criteria.put("totalWaivedFeesThisFiscalYear", 1000);
        
        Collection <KemidFee> kemidRecords = businessObjectService.findMatching(KemidFee.class, criteria);
        assertFalse("The field totalWaivedFeesThisFiscalYear should not have been set to 0", kemidRecords.size() == 0);

        //setting correct values to the update will not happen
        setSystemParameters("Y", "07/01/2010", "0630");
        updated = processFeeTransactionsServiceImpl.updateKemidFeeWaivedYearToDateAmount();
        kemidRecords = businessObjectService.findMatching(KemidFee.class, criteria);
        assertTrue("The field totalWaivedFeesThisFiscalYear should have been set to 0", kemidRecords.size() == 0);
        
        LOG.info("method testUpdateKemidFeeWaivedYearToDateAmount() exited.");
    }
    
    /**
     * test to validate processTransactionArchivesCountForTransactionsFeeType method
     */
    public void testProcessTransactionArchivesCountForTransactionsFeeType() {
        LOG.info("method testProcessTransactionArchivesCountForTransactionsFeeType() entered.");
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal testTransactionIncomeAmount = BigDecimal.ZERO;
        BigDecimal testTransacationPrincipalAmount = BigDecimal.ZERO;
        
        //add a record into TransactionArchive table and test the methods again..
        TransactionArchive transactionArchive1 = TransactionArchiveFixture.TRANSACTION_ARCHIVE_RECORD_1.createTransactionArchiveRecord();
        TransactionArchive transactionArchive2 = TransactionArchiveFixture.TRANSACTION_ARCHIVE_RECORD_2.createTransactionArchiveRecord();

        // add fee transaction records....
        FeeTransaction feeTransaction1 = FeeTransactionFixture.FEE_TRANSACTION_RECORD_1.createFeeTransactionRecord();
        FeeTransaction feeTransaction2 = FeeTransactionFixture.FEE_TRANSACTION_RECORD_2.createFeeTransactionRecord();
        
        //add feeEndowmentTransacationCode records....
        FeeEndowmentTransactionCode feeEndowmentTransactionCode1 = FeeEndowmentTransactionCodeFixture.FEE_TRANSACTION_RECORD_1.createFeeEndowmentTransactionCodeRecord();
        
        totalAmount = BigDecimal.valueOf(0.0050);
        processFeeTransactionsServiceImpl.processTransactionArchivesCountForTransactionsFeeType(feeMethod1);
        assertTrue("totalAmount should be 0.00500", totalAmount.compareTo(processFeeTransactionsServiceImpl.totalAmountCalculated) == 0);
        
        testTransactionIncomeAmount = BigDecimal.valueOf(20);
        testTransacationPrincipalAmount =  BigDecimal.valueOf(10);
        //changing fee rate definition code to V so we can test getTransactionArchivesIncomeAndPrincipalCashAmountForTransactions method...
        feeMethod1.setFeeRateDefinitionCode(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_VALUE);
        
        processFeeTransactionsServiceImpl.processTransactionArchivesCountForTransactionsFeeType(feeMethod1);
        assertTrue("testTransactionIncomeAmount should be 10.00 and testTransacationPrincipalAmount should be 20.00", (testTransactionIncomeAmount.compareTo(processFeeTransactionsServiceImpl.transactionIncomeAmount) == 0) &&
                    (testTransacationPrincipalAmount.compareTo(processFeeTransactionsServiceImpl.transacationPrincipalAmount) == 0));

        LOG.info("method testProcessTransactionArchivesCountForTransactionsFeeType() exited.");
    }
}
