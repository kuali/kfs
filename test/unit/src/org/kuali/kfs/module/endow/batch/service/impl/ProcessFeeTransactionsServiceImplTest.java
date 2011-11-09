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
import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeEndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.FeeTransaction;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.document.validation.impl.CashDecreaseDocumentRuleValidationsForBatchProcess;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.CurrentTaxLotBalanceFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.FeeClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.FeeEndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.FeeMethodFixture;
import org.kuali.kfs.module.endow.fixture.FeeSecurityFixture;
import org.kuali.kfs.module.endow.fixture.FeeTransactionFixture;
import org.kuali.kfs.module.endow.fixture.GLLinkFixture;
import org.kuali.kfs.module.endow.fixture.HoldingHistoryFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFeeFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.KemidGeneralLedgerAccountFixture;
import org.kuali.kfs.module.endow.fixture.MonthEndDateFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.module.endow.fixture.TransactionArchiveFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;


@ConfigureContext(session = kfs)
public class ProcessFeeTransactionsServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessFeeTransactionsServiceImplTest.class);

    private ProcessFeeTransactionsServiceImpl processFeeTransactionsServiceImpl;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;

    // the properties to hold count, total amounts and fee etc.
    private long totalNumberOfRecords = 0;
    private BigDecimal totalAmountCalculated = BigDecimal.ZERO;
    private BigDecimal feeToBeCharged = BigDecimal.ZERO;
    private BigDecimal transactionIncomeAmount = BigDecimal.ZERO;
    private BigDecimal transacationPrincipalAmount = BigDecimal.ZERO;
    private BigDecimal totalHoldingUnits = BigDecimal.ZERO;

    private FeeMethod feeMethod1;
    private FeeMethod feeMethod2;
    private KEMID kemid;
    private EndowmentTransactionCode endowmentTransactionCode1;
    private EndowmentTransactionCode endowmentTransactionCode2;
    private KemidGeneralLedgerAccount kemidGeneralLedgerAccount;
    private GLLink gLLink;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        processFeeTransactionsServiceImpl = (ProcessFeeTransactionsServiceImpl) TestUtils.getUnproxiedService("mockProcessFeeTransactionsService");

        endowmentTransactionCode1 = EndowmentTransactionCodeFixture.EXPENSE_TRANSACTION_CODE.createEndowmentTransactionCode();
        endowmentTransactionCode2 = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        gLLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();

        SecurityReportingGroup securityReportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        ClassCode classCode = ClassCodeFixture.HOLDING_HISTORY_VALUE_ADJUSTMENT_CLASS_CODE_2.createClassCodeRecord();

        feeMethod1 = FeeMethodFixture.FEE_METHOD_RECORD1.createFeeMethodRecord();
        feeMethod2 = FeeMethodFixture.FEE_METHOD_RECORD2.createFeeMethodRecord();

        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
        kemidGeneralLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT.createKemidGeneralLedgerAccount();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * method to set totalWaivedFeesThisFiscalYear field to non-zero value so after calling the method to update the records, we can
     * check for any records matching this non-zero value. After the update method called, all the records should have 0 in
     * totalWaivedFeesThisFiscalYear field in the table.
     */
    private void setTotalWaivedFeesThisFiscalYearInKemidFee() {
        // setting all the records TotalWaivedFeesThisFiscalYear = 1000 so we can call the update method
        // and then check for this value to make sure that method succeded.
        Collection<KemidFee> kemidRecords = businessObjectService.findAll(KemidFee.class);
        for (KemidFee kemidFee : kemidRecords) {
            kemidFee.setTotalWaivedFeesThisFiscalYear(new KualiDecimal(1000L));
            businessObjectService.save(kemidFee);
        }
    }

    /**
     * Update the system parameters to test if updateKemidFeeWaivedYearToDateAmount method will work
     * 
     * @param useProcessDate value for setting USE_PROCESS_DATE system parameter
     * @param currentProcessDate value for setting CURRENT_PROCESS_DATE system parameter
     * @param fiscalYearEndMonthDay value for setting FISCAL_YEAR_END_MONTH_AND_DAY system parameter
     */
    private void setSystemParameters(String useProcessDate, String currentProcessDate, String fiscalYearEndMonthDay) {
        // set USE_PROCESS_DATE_IND system parameter
        TestUtils.setSystemParameter(KfsParameterConstants.ENDOWMENT_ALL.class, EndowParameterKeyConstants.USE_PROCESS_DATE, useProcessDate);
        // set USE_PROCESS_DATE_IND system parameter
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

        // prepare a generic date string without year. The year is calculated from the current system date.
        Calendar calendar = Calendar.getInstance();
        String testDate = ("0701");
        testDate = testDate.substring(0, 2).concat("/").concat(testDate.substring(2, 4)).concat("/") + calendar.get(Calendar.YEAR);

        Map criteria = new HashMap();
        criteria.put("totalWaivedFeesThisFiscalYear", 1000);

        // setting correct values to the update will not happen
        setSystemParameters("Y", testDate, "0630");
        boolean updated = processFeeTransactionsServiceImpl.updateKemidFeeWaivedYearToDateAmount();
        Collection<KemidFee> kemidRecords = businessObjectService.findMatching(KemidFee.class, criteria);

        assertTrue("There should not be any records that has WAIVED_FEE_YTD <> 0.", kemidRecords.size() == 0);

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

        // add a record into TransactionArchive table and test the methods again..
        TransactionArchive transactionArchive1 = TransactionArchiveFixture.TRANSACTION_ARCHIVE_RECORD_1.createTransactionArchiveRecord();
        TransactionArchive transactionArchive2 = TransactionArchiveFixture.TRANSACTION_ARCHIVE_RECORD_2.createTransactionArchiveRecord();

        // add fee transaction records....
        FeeTransaction feeTransaction1 = FeeTransactionFixture.FEE_TRANSACTION_RECORD_1.createFeeTransactionRecord();
        FeeTransaction feeTransaction2 = FeeTransactionFixture.FEE_TRANSACTION_RECORD_2.createFeeTransactionRecord();

        // add feeEndowmentTransacationCode records....
        FeeEndowmentTransactionCode feeEndowmentTransactionCode1 = FeeEndowmentTransactionCodeFixture.FEE_TRANSACTION_RECORD_1.createFeeEndowmentTransactionCodeRecord();

        totalAmount = BigDecimal.valueOf(0.0050);
        processFeeTransactionsServiceImpl.processTransactionArchivesCountForTransactionsFeeType(feeMethod1);
        assertTrue("totalAmount should be 0.00500", totalAmount.compareTo(processFeeTransactionsServiceImpl.totalAmountCalculated) == 0);

        testTransactionIncomeAmount = BigDecimal.valueOf(20);
        testTransacationPrincipalAmount = BigDecimal.valueOf(10);
        // changing fee rate definition code to V so we can test getTransactionArchivesIncomeAndPrincipalCashAmountForTransactions
        // method...
        feeMethod1.setFeeRateDefinitionCode(EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_VALUE);

        processFeeTransactionsServiceImpl.processTransactionArchivesCountForTransactionsFeeType(feeMethod1);
        assertTrue("testTransactionIncomeAmount should be 10.00 and testTransacationPrincipalAmount should be 20.00", (testTransactionIncomeAmount.compareTo(processFeeTransactionsServiceImpl.transactionIncomeAmount) == 0) && (testTransacationPrincipalAmount.compareTo(processFeeTransactionsServiceImpl.transacationPrincipalAmount) == 0));

        LOG.info("method testProcessTransactionArchivesCountForTransactionsFeeType() exited.");
    }

    /**
     * test to validate method performFeeRateDefintionForCountCalculations() fee rate defintion code is C when FEE_BAL_TYP_CD = AU
     * OR CU then total END_HLDG_HIST_T:HLDG_UNITS column
     */
    public void testPerformFeeRateDefintionForCountCalculations() {
        LOG.info("method testPerformFeeRateDefintionForCountCalculations() entered.");

        BigDecimal totalHoldingUnits = BigDecimal.ZERO;
        BigDecimal totalAmountCalculated = BigDecimal.ZERO;

        feeMethod1.setFeeBySecurityCode(true);
        feeMethod1.setFeeByClassCode(true);
        feeMethod1.setFeeLastProcessDate(Date.valueOf("2010-04-25"));

        Security security = SecurityFixture.LIABILITY_INCREASE_ACTIVE_SECURITY.createSecurityRecord();

        MonthEndDate monthEndDate = MonthEndDateFixture.MONTH_END_DATE_TEST_RECORD.createMonthEndDate();

        FeeClassCode feeClassCode1 = FeeClassCodeFixture.FEE_CLASS_CODE_RECORD_1.createFeeClassCodeRecord();
        FeeSecurity feeSecurity1 = FeeSecurityFixture.FEE_SECURITY_RECORD_1.createFeeSecurityRecord();
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD1_FOR_PROCESS_FEE_TRANSACTIONS.createRegistrationCode();
        HoldingHistory holdingHistory1 = HoldingHistoryFixture.HOLDING_HISTORY_RECORD1_FOR_PROCESS_FEE_TRANSACTIONS.createHoldingHistoryRecord();

        // when FEE_BAL_TYP_CD = AU OR MU then total END_HLDG_HIST_T:HLDG_UNITS column
        processFeeTransactionsServiceImpl.performFeeRateDefintionForCountCalculations(feeMethod1);
        totalHoldingUnits = BigDecimal.valueOf(200.00000);
        // totalHoldingUnits should be 200.00000
        assertTrue("totalHoldingUnits should be 200.00000", (processFeeTransactionsServiceImpl.totalHoldingUnits.compareTo(totalHoldingUnits) == 0));
        // totalAmountCalculated should be 1.00
        totalAmountCalculated = BigDecimal.valueOf(1.00);
        assertTrue("totalAmountCalculated should be 1.00", (processFeeTransactionsServiceImpl.totalAmountCalculated.compareTo(totalAmountCalculated) == 0));

        // when FEE_BAL_TYP_CD = CU, totalHoldingUnits is calculated using records from current tax lot balance..
        feeMethod1.setFeeBalanceTypeCode(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_UNITS);
        CurrentTaxLotBalance currentTaxLotBalance = CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD_FOR_FEE_PROCESSING.createCurrentTaxLotBalanceRecord();
        processFeeTransactionsServiceImpl.performFeeRateDefintionForCountCalculations(feeMethod1);

        // totalHoldingUnits should be 10000.00
        totalHoldingUnits = BigDecimal.valueOf(10000.00);
        assertTrue("totalHoldingUnits should be 10000.00", (processFeeTransactionsServiceImpl.totalHoldingUnits.compareTo(totalHoldingUnits) == 0));
        // totalAmountCalculated should be 50.00
        totalAmountCalculated = BigDecimal.valueOf(50.00);
        assertTrue("totalAmountCalculated should be 50.00", (processFeeTransactionsServiceImpl.totalAmountCalculated.compareTo(totalAmountCalculated) == 0));

        LOG.info("method testPerformFeeRateDefintionForCountCalculations() exited.");
    }

    /**
     * test to validate performFeeRateDefintionForValueCalculations() method in the impl class fee rate defintion code is V when
     * FEE_BAL_TYP_CD = AU OR CU then total END_HLDG_HIST_T:HLDG_UNITS column
     */
    public void testPerformFeeRateDefintionForValueCalculations() {
        LOG.info("method testPerformFeeRateDefintionForValueCalculations() entered.");

        BigDecimal totalHoldingUnits = BigDecimal.ZERO;
        BigDecimal totalAmountCalculated = BigDecimal.ZERO;

        feeMethod1.setFeeBySecurityCode(true);
        feeMethod1.setFeeByClassCode(true);
        feeMethod1.setFeeLastProcessDate(Date.valueOf("2010-04-25"));

        Security security = SecurityFixture.LIABILITY_INCREASE_ACTIVE_SECURITY.createSecurityRecord();
        MonthEndDate monthEndDate = MonthEndDateFixture.MONTH_END_DATE_TEST_RECORD.createMonthEndDate();
        FeeClassCode feeClassCode1 = FeeClassCodeFixture.FEE_CLASS_CODE_RECORD_1.createFeeClassCodeRecord();
        FeeSecurity feeSecurity1 = FeeSecurityFixture.FEE_SECURITY_RECORD_1.createFeeSecurityRecord();
        RegistrationCode registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD1_FOR_PROCESS_FEE_TRANSACTIONS.createRegistrationCode();
        HoldingHistory holdingHistory1 = HoldingHistoryFixture.HOLDING_HISTORY_RECORD1_FOR_PROCESS_FEE_TRANSACTIONS.createHoldingHistoryRecord();
        feeMethod1.setFeeBalanceTypeCode(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE);

        // when FEE_BAL_TYP_CD = AMV OR MMV then total END_HLDG_HIST_T:HLDG_UNITS column
        processFeeTransactionsServiceImpl.performFeeRateDefintionForValueCalculations(feeMethod1);
        totalHoldingUnits = BigDecimal.valueOf(1.00000);
        // totalHoldingUnits should be 200.00000
        assertTrue("totalHoldingUnits should be 1.00000", (processFeeTransactionsServiceImpl.totalHoldingUnits.compareTo(totalHoldingUnits) == 0));
        // totalAmountCalculated should be 1.00
        totalAmountCalculated = BigDecimal.valueOf(0.01);
        assertTrue("totalAmountCalculated should be 0.01", (processFeeTransactionsServiceImpl.totalAmountCalculated.compareTo(totalAmountCalculated) == 0));

        // when FEE_BAL_TYP_CD = CMV, totalHoldingUnits is calculated using records from current tax lot balance..
        feeMethod1.setFeeBalanceTypeCode(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_MARKET_VALUE);
        CurrentTaxLotBalance currentTaxLotBalance = CurrentTaxLotBalanceFixture.CURRENT_TAX_LOT_BALANCE_RECORD_FOR_FEE_PROCESSING.createCurrentTaxLotBalanceRecord();
        processFeeTransactionsServiceImpl.performFeeRateDefintionForValueCalculations(feeMethod1);

        // totalHoldingUnits should be 10000.00
        totalHoldingUnits = BigDecimal.valueOf(10000.00);
        assertTrue("totalHoldingUnits should be 10000.00", (processFeeTransactionsServiceImpl.totalHoldingUnits.compareTo(totalHoldingUnits) == 0));
        // totalAmountCalculated should be 50.00
        totalAmountCalculated = BigDecimal.valueOf(50.00);
        assertTrue("totalAmountCalculated should be 50.00", (processFeeTransactionsServiceImpl.totalAmountCalculated.compareTo(totalAmountCalculated) == 0));

        LOG.info("method testPerformFeeRateDefintionForCountCalculations() exited.");
    }

    /**
     * test to validate performCalculationsAgainstTotalAmountCalculated() method in the impl class
     */
    public void testPerformCalculationsAgainstTotalAmountCalculated() {
        LOG.info("method testPerformCalculationsAgainstTotalAmountCalculated() entered.");

        processFeeTransactionsServiceImpl.totalAmountCalculated = BigDecimal.valueOf(50.00);
        BigDecimal feeToBeCharged = BigDecimal.valueOf(0.25);

        processFeeTransactionsServiceImpl.performCalculationsAgainstTotalAmountCalculated(feeMethod1);
        assertTrue("totalAmountCalculated is not less than First Fee Break Point value", processFeeTransactionsServiceImpl.feeToBeCharged.compareTo(feeToBeCharged) == 0);

        feeMethod2.setFirstFeeBreakpoint(new KualiDecimal(25.00));
        feeToBeCharged = BigDecimal.valueOf(2.50);
        processFeeTransactionsServiceImpl.feeToBeCharged = BigDecimal.ZERO;

        // it should hit second if condition in the called method....
        processFeeTransactionsServiceImpl.performCalculationsAgainstTotalAmountCalculated(feeMethod2);
        assertTrue("totalAmountCalculated is not less than Second Fee Break Point value", processFeeTransactionsServiceImpl.feeToBeCharged.compareTo(feeToBeCharged) == 0);

        feeMethod2.setSecondFeeBreakpoint(new KualiDecimal(25.00));
        feeMethod2.setThirdFeeRate(BigDecimal.ONE);
        feeToBeCharged = BigDecimal.valueOf(50.00);
        processFeeTransactionsServiceImpl.feeToBeCharged = BigDecimal.ZERO;

        // it should hit third if condition in the called method....
        processFeeTransactionsServiceImpl.performCalculationsAgainstTotalAmountCalculated(feeMethod2);
        assertTrue("totalAmountCalculated is not less than Third Fee Break Point value", processFeeTransactionsServiceImpl.feeToBeCharged.compareTo(feeToBeCharged) == 0);

        LOG.info("method testPerformCalculationsAgainstTotalAmountCalculated() exited.");
    }

    /**
     * test to validate calculateMinumumFeeAmount() method in the impl class.
     */
    public void testCalculateMinumumFeeAmount() {
        LOG.info("method testcalculateMinumumFeeAmount() entered.");

        processFeeTransactionsServiceImpl.feeToBeCharged = BigDecimal.ZERO;
        processFeeTransactionsServiceImpl.totalAmountCalculated = BigDecimal.valueOf(50.00);
        BigDecimal feeToBeCharged = BigDecimal.ZERO;
        // not less than minimum fee so should equal to 0....
        processFeeTransactionsServiceImpl.calculateMinumumFeeAmount(feeMethod1);
        assertTrue("feeToBeCharged is calculated.  It should not be.", processFeeTransactionsServiceImpl.feeToBeCharged.compareTo(feeToBeCharged) == 0);

        processFeeTransactionsServiceImpl.totalAmountCalculated = BigDecimal.valueOf(0.50);
        feeToBeCharged = BigDecimal.ONE;
        // less than minimum fee so should equal to 1.00....
        processFeeTransactionsServiceImpl.calculateMinumumFeeAmount(feeMethod1);
        assertTrue("feeToBeCharged is not calculated.  It should be 1.00.", processFeeTransactionsServiceImpl.feeToBeCharged.compareTo(feeToBeCharged) == 0);

        LOG.info("method testcalculateMinumumFeeAmount() exited.");
    }

    /**
     * test to validate checkForMinimumThresholdAmount() method in the impl class
     */
    public void testCheckForMinimumThresholdAmount() {
        LOG.info("method testCheckForMinimumThresholdAmount() entered.");

        KemidFee kemidFee1 = KemIdFeeFixture.KEMID_FEE_RECORD1.createKemidFeeRecord();

        feeMethod1.setMinimumFeeThreshold(new KualiDecimal(2.00));
        processFeeTransactionsServiceImpl.feeToBeCharged = BigDecimal.valueOf(1.00);
        // feeToBeCharged less than minimum threshold fee so should return false...
        processFeeTransactionsServiceImpl.calculateMinumumFeeAmount(feeMethod1);
        assertFalse("feeToBeCharged is not less than minimum fee threshold.", processFeeTransactionsServiceImpl.checkForMinimumThresholdAmount(feeMethod1, kemidFee1));

        feeMethod1.setMinimumFeeThreshold(new KualiDecimal(0.00));
        assertTrue("feeToBeCharged is less than minimum fee threshold.", processFeeTransactionsServiceImpl.checkForMinimumThresholdAmount(feeMethod1, kemidFee1));

        LOG.info("method testCheckForMinimumThresholdAmount() exited.");
    }

    /**
     * private method to get the kemidFee record using businessObjectService
     */
    private KemidFee getKemidFeeRecord(String kemid, String feeMethodCode, KualiInteger feeMethodSeq) {
        Map criteria = new HashMap();
        criteria.put("kemid", kemid);
        criteria.put("feeMethodCode", feeMethodCode);
        criteria.put("feeMethodSeq", feeMethodSeq);

        return (KemidFee) businessObjectService.findByPrimaryKey(KemidFee.class, criteria);
    }

    /**
     * test to validate processFeeAccrual() method in the impl class..
     */
    public void testProcessFeeAccrual() {
        LOG.info("method testProcessFeeAccrual() entered.");

        KemidFee kemidFee1 = KemIdFeeFixture.KEMID_FEE_RECORD1.createKemidFeeRecord();
        BigDecimal totalAccruedFees = BigDecimal.valueOf(110.00);

        processFeeTransactionsServiceImpl.feeToBeCharged = BigDecimal.valueOf(100.00);

        // total accrued fees should be 10+100 = 110.00 after the method called.
        processFeeTransactionsServiceImpl.processFeeAccrual(feeMethod1, kemidFee1);

        KemidFee kemidFee = getKemidFeeRecord(kemidFee1.getKemid(), kemidFee1.getFeeMethodCode(), kemidFee1.getFeeMethodSeq());

        assertTrue("Total Accrued Fees Should be equal to 110.00", (kemidFee.getTotalAccruedFees().bigDecimalValue().compareTo(totalAccruedFees) == 0));

        LOG.info("method testProcessFeeAccrual() exited.");
    }

    /**
     * test to validate processFeeWaiver() method in the impl class..
     */
    public void testProcessFeeWaiver() {
        LOG.info("method testProcessFeeWaiver() entered.");

        KemidFee kemidFee1 = KemIdFeeFixture.KEMID_FEE_RECORD1.createKemidFeeRecord();
        BigDecimal totalWaivedFeesThisFiscalYear = BigDecimal.valueOf(110.00);
        BigDecimal totalWaivedFees = BigDecimal.valueOf(110.00);

        processFeeTransactionsServiceImpl.feeToBeCharged = BigDecimal.valueOf(100.00);

        // total accrued fees should be 10+100 = 110.00 after the method called.
        processFeeTransactionsServiceImpl.processFeeWaiver(feeMethod1, kemidFee1);

        KemidFee kemidFee = getKemidFeeRecord(kemidFee1.getKemid(), kemidFee1.getFeeMethodCode(), kemidFee1.getFeeMethodSeq());

        assertTrue("Total waived Fees this fiscal year should be equal to 110.00", (kemidFee.getTotalWaivedFeesThisFiscalYear().bigDecimalValue().compareTo(totalWaivedFeesThisFiscalYear) == 0));
        assertTrue("Total waived Fees should be equal to 110.00", (kemidFee.getTotalWaivedFees().bigDecimalValue().compareTo(totalWaivedFees) == 0));

        LOG.info("method testProcessFeeAccrual() exited.");
    }

    /**
     * create a CashDecreaseDocument
     * 
     * @return doc
     */
    @ConfigureContext(session = kfs, shouldCommitTransactions = true)
    protected CashDecreaseDocument createCashDecreaseDocumentDocument() throws WorkflowException {
        LOG.info("createCashDecreaseDocumentDocument() entered.");

        CashDecreaseDocument doc = (CashDecreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentTransactionDocument(CashDecreaseDocument.class);
        doc.getDocumentHeader().setDocumentDescription("Testing Cash Decrease document.");
        return doc;
    }

    /**
     * test to validate method setDocumentOverviewAndDetails() in the impl class
     */
    public void testSetDocumentOverviewAndDetails() throws Exception {
        LOG.info("testSetDocumentOverviewAndDetails() entered.");

        CashDecreaseDocument cashDecreaseDocument = createCashDecreaseDocumentDocument();
        processFeeTransactionsServiceImpl.setDocumentOverviewAndDetails(cashDecreaseDocument, feeMethod1.getName());

        assertTrue(cashDecreaseDocument.getTransactionSourceTypeCode().equalsIgnoreCase(EndowConstants.TransactionSourceTypeCode.AUTOMATED) && cashDecreaseDocument.getTransactionSubTypeCode().equalsIgnoreCase(EndowConstants.TransactionSubTypeCode.CASH));

        LOG.info("testSetDocumentOverviewAndDetails() exited.");
    }

    /**
     * test to validate addTransactionLineToDocument method in the impl class.
     * 
     * @see org.kuali.kfs.module.endow.batch.service.impl.ProcessFeeTransactionsServiceImpl#addTransactionLineToDocument(CashDecreaseDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine, int, String)
     */
    public void testAddTransactionLineToDocument() throws Exception {
        LOG.info("testAddTransactionLineToDocument() entered.");

        CashDecreaseDocument cashDecreaseDocument = createCashDecreaseDocumentDocument();
        processFeeTransactionsServiceImpl.setDocumentOverviewAndDetails(cashDecreaseDocument, feeMethod1.getName());

        EndowmentSourceTransactionLine line = (EndowmentSourceTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_ECDD_WITH_ETRAN_CD.createEndowmentTransactionLine(true);
        line.setDocumentNumber(cashDecreaseDocument.getDocumentNumber());

        assertTrue("Source Transaction Line not added.", checkvalidateCashTransactionLine(cashDecreaseDocument, line));

        LOG.info("testAddTransactionLineToDocument() exited.");
    }

    /*
     * The static method uses the methods in CashDecreaseDocumentRuleValidatiionsForBatchProcess which basically calls individual
     * methods in cash document base rule class to validate validateCashTransactionLine method. The reason we are doing this way so
     * that the method validateChartMatch() can be ignored. This method will always fail in unit tests since END_KEMID_T and
     * END_KEMID_GL_LNK_T tables have an inverse key KEMID and collection records for a given kemid in END_KEMID_GL_LNK_T will not
     * be visible during validateChartMatch method so instead of calling validateCashTransactionLine, we validate the individual
     * methods to make sure validateCashTransactionLine passes.
     */
    private static boolean checkvalidateCashTransactionLine(CashDecreaseDocument cashDecreaseDocument, EndowmentSourceTransactionLine line) throws Exception {
        boolean isValid = true;

        CashDecreaseDocumentRuleValidationsForBatchProcess ruleTest = new CashDecreaseDocumentRuleValidationsForBatchProcess();

        return ruleTest.checkValidateCashTransactionLine(cashDecreaseDocument, line, 0);
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the documentService attribute value.
     */
    protected DocumentService getDocumentService() {
        return documentService;
    }

}
