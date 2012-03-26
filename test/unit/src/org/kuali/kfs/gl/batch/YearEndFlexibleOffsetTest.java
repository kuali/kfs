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
package org.kuali.kfs.gl.batch;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.A21SubAccountService;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.fp.businessobject.OffsetAccount;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.EncumbranceClosingOriginEntryGenerationService;
import org.kuali.kfs.gl.batch.service.OrganizationReversionProcess;
import org.kuali.kfs.gl.batch.service.OrganizationReversionProcessService;
import org.kuali.kfs.gl.batch.service.OrganizationReversionUnitOfWorkService;
import org.kuali.kfs.gl.batch.service.impl.CashOrganizationReversionCategoryLogic;
import org.kuali.kfs.gl.batch.service.impl.OrganizationReversionMockServiceImpl;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryOffsetPair;
import org.kuali.kfs.gl.batch.service.impl.exception.FatalErrorException;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

/*
 * Unit tests to verify that flexible offsets are being added to year end origin entries correctly
 */
@ConfigureContext
public class YearEndFlexibleOffsetTest extends OriginEntryTestBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(YearEndFlexibleOffsetTest.class);
    
    public static final String DEFAULT_FLEXIBLE_BALANCE_CHART = "BL";
    public static final String DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR = "1031400";
    public static final String DEFAULT_NO_FLEXIBLE_BALANCE_CHART = "BL";
    public static final String DEFAULT_NO_FLEXIBLE_BALANCE_ACCOUNT_NBR = "1031420";
    public static final String DEFAULT_FLEXIBLE_ENCUMBRANCE_CHART = "BL";
    public static final String DEFAULT_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR = "4531402";
    public static String DEFAULT_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR;
    public static final String DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_CHART = "BL";
    public static final String DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR = "4531403";
    public static String DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR;
    public static final String CS_FLEXIBLE_ENCUMBRANCE_CHART = "BL";
    public static final String CS_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR = "4031418";
    public static final String CS_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR = "CS001";
    public static final String CS_NO_FLEXIBLE_ENCUMBRANCE_CHART = "BL";
    public static final String CS_NO_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR = "4431423";
    public static final String CS_NO_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR = "CS001";
    public static final KualiDecimal DEFAULT_FIXTURE_AMOUNT = new KualiDecimal(3000);
    public static final String DEFAULT_EXPENSE_OBJECT_CODE = "4680";
    public static final String DEFAULT_NOMINAL_ACTIVITY_OFFSET_OBJECT_CODE = "9899";
    public static final String DEFAULT_ENCUMBRANCE_OFFSET_OBJECT_CODE = "9892";
    public static final String DEFAULT_COST_SHARE_ENCUMBRANCE_OFFSET_OBJECT_CODE = "9893";
    public static final String DEFAULT_ENCUMBRANCE_BALANCE_TYPE_CODE = "EX";
    public static final String DEFAULT_OFFSET_CHART = "BL";
    public static final String DEFAULT_OFFSET_ACCOUNT_NBR = "0211201";
    public static final String ORG_REVERSION_CASH_OBJECT_CODE = "8000";
    
    private BusinessObjectService boService;
    private ParameterService parameterService;
    private Integer fiscalYear;
    private Date transactionDate;
    
    enum NOMINAL_ACTIVITY_BALANCE_FIXTURE {
        FLEXIBLE_NOMINAL_ACTIVITY_BALANCE(DEFAULT_FLEXIBLE_BALANCE_CHART, DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR),
        INFLEXIBLE_NOMINAL_ACTIVITY_BALANCE(DEFAULT_NO_FLEXIBLE_BALANCE_CHART, DEFAULT_NO_FLEXIBLE_BALANCE_ACCOUNT_NBR);

        private String chartCode;
        private String accountNumber;
        private KualiDecimal amount;
        private Date timestamp;
        private static final String DATE_FORMAT = "yyyy-MM-dd";
        private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NOMINAL_ACTIVITY_BALANCE_FIXTURE.class);
        private SystemOptions fsOptions = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();

        private NOMINAL_ACTIVITY_BALANCE_FIXTURE(String chartCode, String accountNumber) {
            this.chartCode = chartCode;
            this.accountNumber = accountNumber;
        }

        /**
         * Converts the fixture into a balance to test
         * 
         * @return a balance represented by this fixture
         */
        public Balance convertToBalance() {
            Balance balance = new Balance();
            balance.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting().intValue());
            balance.setChartOfAccountsCode(chartCode);
            balance.setAccountNumber(accountNumber);
            balance.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            balance.setObjectCode(DEFAULT_EXPENSE_OBJECT_CODE);
            balance.setSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            balance.setBalanceTypeCode(fsOptions.getActualFinancialBalanceTypeCd());
            balance.setObjectTypeCode(fsOptions.getFinObjTypeCshNotIncomeCd());
            balance.setAccountLineAnnualBalanceAmount(DEFAULT_FIXTURE_AMOUNT);
            balance.setBeginningBalanceLineAmount(KualiDecimal.ZERO);
            balance.setContractsGrantsBeginningBalanceAmount(KualiDecimal.ZERO);
            balance.setMonth1Amount(amount);
            balance.setMonth2Amount(KualiDecimal.ZERO);
            balance.setMonth3Amount(KualiDecimal.ZERO);
            balance.setMonth4Amount(KualiDecimal.ZERO);
            balance.setMonth5Amount(KualiDecimal.ZERO);
            balance.setMonth6Amount(KualiDecimal.ZERO);
            balance.setMonth7Amount(KualiDecimal.ZERO);
            balance.setMonth8Amount(KualiDecimal.ZERO);
            balance.setMonth9Amount(KualiDecimal.ZERO);
            balance.setMonth10Amount(KualiDecimal.ZERO);
            balance.setMonth11Amount(KualiDecimal.ZERO);
            balance.setMonth12Amount(KualiDecimal.ZERO);
            balance.setMonth13Amount(KualiDecimal.ZERO);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                java.util.Date jud = sdf.parse(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM));
                balance.setTimestamp(new java.sql.Date(jud.getTime()));
            }
            catch (ParseException e) {
                LOG.debug("Parse date exception while parsing transaction date");
            }
            balance.refresh();
            return balance;
        }
    };
    
    enum ENCUMBRANCE_FORWARD_FIXTURE {
        FLEXIBLE_ENCUMBRANCE(DEFAULT_FLEXIBLE_ENCUMBRANCE_CHART, DEFAULT_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR, DEFAULT_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR),
        INFLEXIBLE_ENCUMBRANCE(DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_CHART, DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR, DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR),
        FLEXIBLE_COST_SHARE_ENCUMBRANCE(CS_FLEXIBLE_ENCUMBRANCE_CHART, CS_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR, CS_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR),
        INFLEXIBLE_COST_SHARE_ENCUMBRANCE(CS_NO_FLEXIBLE_ENCUMBRANCE_CHART, CS_NO_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR, CS_NO_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR);
        
        private ENCUMBRANCE_FORWARD_FIXTURE(String chart, String account, String subAccount) {
            this.chartOfAccountsCode = chart;
            this.accountNumber = account;
            this.subAccountNumber = subAccount;
        }
        
        private String chartOfAccountsCode;
        private String accountNumber;
        private String subAccountNumber;
        
        public Encumbrance convertToEncumbrance() {
            Encumbrance encumbrance = new Encumbrance();
            encumbrance.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting().intValue() - 1);
            encumbrance.setChartOfAccountsCode(this.chartOfAccountsCode);
            encumbrance.setAccountNumber(this.accountNumber);
            encumbrance.setSubAccountNumber(this.subAccountNumber);
            encumbrance.setObjectCode(DEFAULT_EXPENSE_OBJECT_CODE);
            encumbrance.setSubObjectCode(subAccountNumber);
            encumbrance.setBalanceTypeCode(DEFAULT_ENCUMBRANCE_BALANCE_TYPE_CODE);
            encumbrance.setDocumentTypeCode("EXEN");
            encumbrance.setOriginCode("02");
            encumbrance.setDocumentNumber("200200");
            encumbrance.setTransactionEncumbranceDescription("Test Encumbrance");
            encumbrance.setTransactionEncumbranceDate(getEncumbranceDate());
            encumbrance.setAccountLineEncumbranceAmount(DEFAULT_FIXTURE_AMOUNT);
            encumbrance.setAccountLineEncumbranceClosedAmount(KualiDecimal.ZERO);
            encumbrance.setAccountLineEncumbrancePurgeCode(" ");
            encumbrance.setTimestamp(new Timestamp(getEncumbranceDate().getTime()));
            return encumbrance;
        }
        
        private Date getEncumbranceDate() {
            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.YEAR, ((SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear()).intValue() - 1));
            return new Date(cal.getTimeInMillis());
        }
    }
    
    enum ORG_REVERSION_BALANCE_FIXTURE {
        FLEXIBLE_ORG_REVERSION_BALANCE(DEFAULT_FLEXIBLE_BALANCE_CHART, DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR),
        INFLEXIBLE_ORG_REVERSION_BALANCE(DEFAULT_NO_FLEXIBLE_BALANCE_CHART, DEFAULT_NO_FLEXIBLE_BALANCE_ACCOUNT_NBR);

        private String chartCode;
        private String accountNumber;
        private KualiDecimal amount;
        private Date timestamp;
        private static final String DATE_FORMAT = "yyyy-MM-dd";
        private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NOMINAL_ACTIVITY_BALANCE_FIXTURE.class);
        private SystemOptions fsOptions = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();

        private ORG_REVERSION_BALANCE_FIXTURE(String chartCode, String accountNumber) {
            this.chartCode = chartCode;
            this.accountNumber = accountNumber;
        }

        /**
         * Converts the fixture into a balance to test
         * 
         * @return a balance represented by this fixture
         */
        public Balance convertToBalance() {
            Balance balance = new Balance();
            balance.setChartOfAccountsCode(chartCode);
            balance.setAccountNumber(accountNumber);
            balance.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            balance.setObjectCode(ORG_REVERSION_CASH_OBJECT_CODE);
            balance.setSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            balance.setBalanceTypeCode(fsOptions.getActualFinancialBalanceTypeCd());
            balance.setObjectTypeCode(fsOptions.getFinObjTypeCshNotIncomeCd());
            balance.setAccountLineAnnualBalanceAmount(DEFAULT_FIXTURE_AMOUNT);
            balance.setBeginningBalanceLineAmount(KualiDecimal.ZERO);
            balance.setContractsGrantsBeginningBalanceAmount(KualiDecimal.ZERO);
            balance.setMonth1Amount(amount);
            balance.setMonth2Amount(KualiDecimal.ZERO);
            balance.setMonth3Amount(KualiDecimal.ZERO);
            balance.setMonth4Amount(KualiDecimal.ZERO);
            balance.setMonth5Amount(KualiDecimal.ZERO);
            balance.setMonth6Amount(KualiDecimal.ZERO);
            balance.setMonth7Amount(KualiDecimal.ZERO);
            balance.setMonth8Amount(KualiDecimal.ZERO);
            balance.setMonth9Amount(KualiDecimal.ZERO);
            balance.setMonth10Amount(KualiDecimal.ZERO);
            balance.setMonth11Amount(KualiDecimal.ZERO);
            balance.setMonth12Amount(KualiDecimal.ZERO);
            balance.setMonth13Amount(KualiDecimal.ZERO);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                java.util.Date jud = sdf.parse(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM));
                balance.setTimestamp(new java.sql.Date(jud.getTime()));
            }
            catch (ParseException e) {
                LOG.debug("Parse date exception while parsing transaction date");
            }
            balance.refresh();
            return balance;
        }
    };
    
    public enum FLEXIBLE_OFFSET_ACCOUNT_FIXTURE {
        FLEXIBLE_ACTIVITY_CLOSING_OFFSET_ACCOUNT(DEFAULT_FLEXIBLE_BALANCE_CHART, DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR, DEFAULT_NOMINAL_ACTIVITY_OFFSET_OBJECT_CODE),
        FLEXIBLE_ENCUMBRANCE_FORWARD_OFFSET_ACCOUNT(DEFAULT_FLEXIBLE_ENCUMBRANCE_CHART, DEFAULT_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR, DEFAULT_ENCUMBRANCE_OFFSET_OBJECT_CODE),
        FLEXIBLE_CS_ENCUMBRANCE_FORWARD_OFFSET_ACCOUNT(DEFAULT_FLEXIBLE_BALANCE_CHART, DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR, DEFAULT_COST_SHARE_ENCUMBRANCE_OFFSET_OBJECT_CODE),
        CASH_REVERSION_FORWARD_OFFSET_ACCOUNT(OrganizationReversionMockServiceImpl.DEFAULT_CASH_REVERSION_CHART, OrganizationReversionMockServiceImpl.DEFAULT_CASH_REVERSION_ACCOUNT, DEFAULT_NOMINAL_ACTIVITY_OFFSET_OBJECT_CODE);
        
        private String chartCode;
        private String accountNumber;
        private String objectCode;
        
        private FLEXIBLE_OFFSET_ACCOUNT_FIXTURE(String chartCode, String accountNumber, String objectCode) {
            this.chartCode = chartCode;
            this.accountNumber = accountNumber;
            this.objectCode = objectCode;
        }
        
        public OffsetAccount convertToOffsetAccount() {
            OffsetAccount offset = new OffsetAccount();
            offset.setChartOfAccountsCode(this.chartCode);
            offset.setAccountNumber(this.accountNumber);
            offset.setFinancialOffsetObjectCode(this.objectCode);
            offset.setFinancialOffsetChartOfAccountCode(DEFAULT_OFFSET_CHART);
            offset.setFinancialOffsetAccountNumber(DEFAULT_OFFSET_ACCOUNT_NBR);
            return offset;
        }
    }
    
    /**
     * Initialize defaults for each test.
     * @see org.kuali.kfs.gl.businessobject.OriginEntryTestBase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
        
        this.boService = SpringContext.getBean(BusinessObjectService.class);
        this.fiscalYear = new Integer((SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear()).intValue() - 1);
        this.transactionDate = new java.sql.Date(new java.util.Date().getTime());
        this.parameterService = SpringContext.getBean(ParameterService.class);
        
        DEFAULT_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR = KFSConstants.getDashSubAccountNumber();
        DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR = KFSConstants.getDashSubAccountNumber();
        
        createFlexibleOffsetAccounts();
    }
    
    /**
     * Test that:
     * <ol>
     *  <li>when flexible offsets are turned on, nominal activity offsets that should get flexible offsets get them</li>
     *  <li>when flexible offsets are turned on, nominal activity offsets that should not get flexible offsets don't get them</li>
     *  <li>when flexible offsets are turned on, nominal activity entries do not get flexible offsets</li>
     * </ol>
     */
    public void testNominalActivityFlexibleOffsetsWhenOffsetsOn() {
        try {
            NominalActivityClosingHelper closingHelper = new NominalActivityClosingHelper(fiscalYear, transactionDate, parameterService, kualiConfigurationService);
            OriginEntryInformation entry;
            
            // 1. flexible offsets on, flexible offset should be updated
            toggleFlexibleOffsets(true);
            entry = closingHelper.generateOffset(NOMINAL_ACTIVITY_BALANCE_FIXTURE.FLEXIBLE_NOMINAL_ACTIVITY_BALANCE.convertToBalance(), 1);
            assertChartAndAccount(entry, DEFAULT_OFFSET_CHART, DEFAULT_OFFSET_ACCOUNT_NBR);
            
            // 2. flexible offsets on, but balances without matching offsets should not be updated
            entry = closingHelper.generateOffset(NOMINAL_ACTIVITY_BALANCE_FIXTURE.INFLEXIBLE_NOMINAL_ACTIVITY_BALANCE.convertToBalance(), 1);
            assertChartAndAccount(entry, DEFAULT_NO_FLEXIBLE_BALANCE_CHART, DEFAULT_NO_FLEXIBLE_BALANCE_ACCOUNT_NBR);
            
            // 3. flexible offsets on, but activity offsets stay the same!
            entry = closingHelper.generateActivityEntry(NOMINAL_ACTIVITY_BALANCE_FIXTURE.FLEXIBLE_NOMINAL_ACTIVITY_BALANCE.convertToBalance(), 1);
            assertChartAndAccount(entry, DEFAULT_FLEXIBLE_BALANCE_CHART, DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR);
                        
        } catch (FatalErrorException fee) {
            throw new RuntimeException(fee);
        }
    }
    
    /**
     * Test that:
     * <ol>
     *  <li>when flexible offsets are turned off, nominal activity offsets that should get flexible offsets don't get them</li>
     * </ol>
     */
    public void testNominalActivityFlexibleOffsetsWhenOffsetsOff() {
        try {
            NominalActivityClosingHelper closingHelper = new NominalActivityClosingHelper(fiscalYear, transactionDate, parameterService, kualiConfigurationService);
            OriginEntryInformation entry;
                     
            // 1. flexible offsets off, flexible offset should not be updated
            toggleFlexibleOffsets(false);
            entry = closingHelper.generateOffset(NOMINAL_ACTIVITY_BALANCE_FIXTURE.FLEXIBLE_NOMINAL_ACTIVITY_BALANCE.convertToBalance(), 1);
            assertChartAndAccount(entry, DEFAULT_FLEXIBLE_BALANCE_CHART, DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR);
            
        } catch (FatalErrorException fee) {
            throw new RuntimeException(fee);
        }
    }
    
    /**
     * Test that:
     * <ol>
     *  <li>when flexible offsets are turned on, encumbrance forward offsets that should get flexible offsets get them</li>
     *  <li>when flexible offsets are turned on, encumbrance forward offsets that should not get flexible offsets don't get them</li>
     *  <li>when flexible offsets are turned on, encumbrance forward entries do not get flexible offsets</li>
     * </ol>
     */
    public void testEncumbranceForwardFlexibleOffsetsWhenFlexibleOffsetsOn() {
        OriginEntryOffsetPair entryPair;
        toggleFlexibleOffsets(true);
        
        final EncumbranceClosingOriginEntryGenerationService encumbranceClosingOriginEntryGenerationSerivce = SpringContext.getBean(EncumbranceClosingOriginEntryGenerationService.class);
        
        // 1. when flexible offsets are turned on, encumbrance forward offsets that should get flexible offsets get them
        entryPair = encumbranceClosingOriginEntryGenerationSerivce.createBeginningBalanceEntryOffsetPair(ENCUMBRANCE_FORWARD_FIXTURE.FLEXIBLE_ENCUMBRANCE.convertToEncumbrance(), fiscalYear, transactionDate);
        assertChartAndAccount(entryPair.getOffset(), DEFAULT_OFFSET_CHART, DEFAULT_OFFSET_ACCOUNT_NBR);
        // 2. when flexible offsets are turned on, encumbrance forward entries do not get flexible offsets
        assertChartAndAccount(entryPair.getEntry(), DEFAULT_FLEXIBLE_ENCUMBRANCE_CHART, DEFAULT_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR);
        
        // 3. when flexible offsets are turned on, encumbrance forward offsets that should not get flexible offsets don't get them
        entryPair = encumbranceClosingOriginEntryGenerationSerivce.createBeginningBalanceEntryOffsetPair(ENCUMBRANCE_FORWARD_FIXTURE.INFLEXIBLE_ENCUMBRANCE.convertToEncumbrance(), fiscalYear, transactionDate);
        assertChartAndAccount(entryPair.getOffset(), DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_CHART, DEFAULT_NO_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR);
    }
    
    /**
     * Test that:
     * <ol>
     *  <li>when flexible offsets are turned off, encumbrance forward offsets that should get flexible offsets don't get them</li>
     * </ol>
     */
    public void testEncumbranceForwardFlexibleOffsetsWhenFlexibleOffsetsOff() {
        OriginEntryOffsetPair entryPair;
        toggleFlexibleOffsets(false);
        
        final EncumbranceClosingOriginEntryGenerationService encumbranceClosingOriginEntryGenerationSerivce = SpringContext.getBean(EncumbranceClosingOriginEntryGenerationService.class);
        
        // 1. when flexible offsets are turned off, encumbrance forward offsets that should get flexible offsets do not get them
        entryPair = encumbranceClosingOriginEntryGenerationSerivce.createBeginningBalanceEntryOffsetPair(ENCUMBRANCE_FORWARD_FIXTURE.FLEXIBLE_ENCUMBRANCE.convertToEncumbrance(), fiscalYear, transactionDate);
        assertChartAndAccount(entryPair.getEntry(), DEFAULT_FLEXIBLE_ENCUMBRANCE_CHART, DEFAULT_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR);
    }
    
    /**
     * Test that:
     * <ul>
     *  <li>when flexible offsets are turned on, encumbrance forward cost share offsets that should get flexible offsets get them</li>
     *  <li>when flexible offsets are turned on, encumbrance forward cost share offsets that should not get flexible offsets don't get them</li>
     *  <li>when flexible offsets are turned on, encumbrance forward cost share entries do not get flexible offsets</li>
     * </ul>
     */
    public void testEncumbranceForwardCostShareFlexibleOffsetsWhenFlexibleOffsetsOn() {
        OriginEntryOffsetPair entryPair;
        A21SubAccount a21SubAccount;
        toggleFlexibleOffsets(true);
        
        final EncumbranceClosingOriginEntryGenerationService encumbranceClosingOriginEntryGenerationSerivce = SpringContext.getBean(EncumbranceClosingOriginEntryGenerationService.class);
                
        // 1. when flexible offsets are turned on, encumbrance forward cost share offsets that should get flexible offsets get them
        a21SubAccount = SpringContext.getBean(A21SubAccountService.class).getByPrimaryKey(CS_FLEXIBLE_ENCUMBRANCE_CHART, CS_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR, CS_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR);
        entryPair = encumbranceClosingOriginEntryGenerationSerivce.createCostShareBeginningBalanceEntryOffsetPair(ENCUMBRANCE_FORWARD_FIXTURE.FLEXIBLE_COST_SHARE_ENCUMBRANCE.convertToEncumbrance(), transactionDate);
        assertChartAndAccount(entryPair.getOffset(), DEFAULT_OFFSET_CHART, DEFAULT_OFFSET_ACCOUNT_NBR);
        // 2. when flexible offsets are turned on, encumbrance forward cost share entries do not get flexible offsets
        assertChartAndAccount(entryPair.getEntry(), a21SubAccount.getCostShareChartOfAccountCode(), a21SubAccount.getCostShareSourceAccountNumber());
        
        // 3. when flexible offsets are turned on, encumbrance forward cost share offsets that should not get flexible offsets don't get them
        a21SubAccount = SpringContext.getBean(A21SubAccountService.class).getByPrimaryKey(CS_NO_FLEXIBLE_ENCUMBRANCE_CHART, CS_NO_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR, CS_NO_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR);
        entryPair = encumbranceClosingOriginEntryGenerationSerivce.createCostShareBeginningBalanceEntryOffsetPair(ENCUMBRANCE_FORWARD_FIXTURE.INFLEXIBLE_COST_SHARE_ENCUMBRANCE.convertToEncumbrance(), transactionDate);
        assertChartAndAccount(entryPair.getOffset(), a21SubAccount.getCostShareChartOfAccountCode(), a21SubAccount.getCostShareSourceAccountNumber());
    }
    
    /**
     * Test that:
     * <ul>
     *  <li>when flexible offsets are turned off, encumbrance forward cost share offsets that should get flexible offsets don't get them</li>
     * </ul>
     */
    public void testEncumbranceForwardCostShareFlexibleOffsetsWhenFlexibleOffsetsOff() {
        OriginEntryOffsetPair entryPair;
        A21SubAccount a21SubAccount;
        toggleFlexibleOffsets(false);
        
        final EncumbranceClosingOriginEntryGenerationService encumbranceClosingOriginEntryGenerationSerivce = SpringContext.getBean(EncumbranceClosingOriginEntryGenerationService.class);
                
        // 1. when flexible offsets are turned off, encumbrance forward cost share offsets that should get flexible offsets don't get them
        a21SubAccount = SpringContext.getBean(A21SubAccountService.class).getByPrimaryKey(CS_FLEXIBLE_ENCUMBRANCE_CHART, CS_FLEXIBLE_ENCUMBRANCE_ACCOUNT_NBR, CS_FLEXIBLE_ENCUMBRANCE_SUB_ACCT_NBR);
        entryPair = encumbranceClosingOriginEntryGenerationSerivce.createCostShareBeginningBalanceEntryOffsetPair(ENCUMBRANCE_FORWARD_FIXTURE.FLEXIBLE_COST_SHARE_ENCUMBRANCE.convertToEncumbrance(), transactionDate);
        assertChartAndAccount(entryPair.getEntry(), a21SubAccount.getCostShareChartOfAccountCode(), a21SubAccount.getCostShareSourceAccountNumber());
    }
    
    /**
     * Test that:
     * <ul>
     *  <li>when flexible offsets are turned on, cash reversion offsets that should get flexible offsets get them</li>
     *  <li>when flexible offsets are turned on, cash reversion activity offsets that should not get flexible offsets don't get them</li>
     *  <li>when flexible offsets are turned on, cash reversion activity entries do not get flexible offsets</li>
     * </ul>
     */
    public void testOrganizationReversionCashFlexibleOffsetsWhenFlexibleOffsetsOn() {
//        toggleFlexibleOffsets(true);
//        List<Balance> flexibleBalances = new ArrayList<Balance>();
//        flexibleBalances.add(ORG_REVERSION_BALANCE_FIXTURE.FLEXIBLE_ORG_REVERSION_BALANCE.convertToBalance());
//        flexibleBalances.add(ORG_REVERSION_BALANCE_FIXTURE.INFLEXIBLE_ORG_REVERSION_BALANCE.convertToBalance());
//        
//        List<OriginEntryFull> resultingEntries = runOrganizationReversion(flexibleBalances);
//        assertEquals("Number of generated OriginEntries ", new Integer(8), new Integer(resultingEntries.size()));
//        // 1. when flexible offsets are turned on, cash reversion activity entries do not get flexible offsets
//        assertChartAndAccount(resultingEntries.get(0), DEFAULT_FLEXIBLE_BALANCE_CHART, DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR);
//        assertChartAndAccount(resultingEntries.get(2), OrganizationReversionMockServiceImpl.DEFAULT_CASH_REVERSION_CHART, OrganizationReversionMockServiceImpl.DEFAULT_CASH_REVERSION_ACCOUNT);
//        assertChartAndAccount(resultingEntries.get(4), DEFAULT_NO_FLEXIBLE_BALANCE_CHART, DEFAULT_NO_FLEXIBLE_BALANCE_ACCOUNT_NBR);
//        assertChartAndAccount(resultingEntries.get(6), OrganizationReversionMockServiceImpl.DEFAULT_CASH_REVERSION_CHART, OrganizationReversionMockServiceImpl.DEFAULT_CASH_REVERSION_ACCOUNT);
//        // 2. when flexible offsets are turned on, cash reversion offsets that should get flexible offsets get them
//        assertChartAndAccount(resultingEntries.get(1), DEFAULT_OFFSET_CHART, DEFAULT_OFFSET_ACCOUNT_NBR);
//        assertChartAndAccount(resultingEntries.get(3), DEFAULT_OFFSET_CHART, DEFAULT_OFFSET_ACCOUNT_NBR);
//        assertChartAndAccount(resultingEntries.get(7), DEFAULT_OFFSET_CHART, DEFAULT_OFFSET_ACCOUNT_NBR);
//        // 3. when flexible offsets are turned on, cash reversion offsets that should not get flexible offsets don't get them
//        assertChartAndAccount(resultingEntries.get(5), DEFAULT_NO_FLEXIBLE_BALANCE_CHART, DEFAULT_NO_FLEXIBLE_BALANCE_ACCOUNT_NBR);
    }
    
    /**
     * Test that:
     * <ul>
     *  <li>when flexible offsets are turned off, cash reversion activity offsets that should get flexible offsets don't get them</li>
     * </ul>
     */
    public void testOrganizationReversionCashFlexibleOffsetsWhenFlexibleOffsetsOff() {
//        toggleFlexibleOffsets(false);
//        List<Balance> flexibleBalances = new ArrayList<Balance>();
//        flexibleBalances.add(ORG_REVERSION_BALANCE_FIXTURE.FLEXIBLE_ORG_REVERSION_BALANCE.convertToBalance());
//        
//        List<OriginEntryFull> resultingEntries = runOrganizationReversion(flexibleBalances);
//        assertEquals("Number of generated OriginEntries ", new Integer(4), new Integer(resultingEntries.size()));
//        assertChartAndAccount(resultingEntries.get(1), DEFAULT_FLEXIBLE_BALANCE_CHART, DEFAULT_FLEXIBLE_BALANCE_ACCOUNT_NBR);
//        assertChartAndAccount(resultingEntries.get(3), OrganizationReversionMockServiceImpl.DEFAULT_CASH_REVERSION_CHART, OrganizationReversionMockServiceImpl.DEFAULT_CASH_REVERSION_ACCOUNT);
    }
    
    /**
     * Runs the organization service against a given set of balances.
     * @param balancesToTest a List of balances to test the organization reversion process against
     * @return the list of origin entries generated by the organization reversion process
     */
    private List<OriginEntryFull> runOrganizationReversion(List<Balance> balancesToTest) {
        OrganizationReversionService organizationReversionService = SpringContext.getBean(OrganizationReversionService.class,"glOrganizationReversionMockService");
        DateTimeService dtService = SpringContext.getBean(DateTimeService.class);
        BalanceService balanceService = SpringContext.getBean(BalanceService.class);
        CashOrganizationReversionCategoryLogic cashOrganizationReversionCategoryLogic = SpringContext.getBean(CashOrganizationReversionCategoryLogic.class);
        PriorYearAccountService priorYearAccountService = SpringContext.getBean(PriorYearAccountService.class);
        OrganizationReversionUnitOfWorkService orgReversionUnitOfWorkService = SpringContext.getBean(OrganizationReversionUnitOfWorkService.class);
        OrganizationReversionProcessService organizationReversionProcessService = SpringContext.getBean(OrganizationReversionProcessService.class);

        Map jobParameters = organizationReversionProcessService.getJobParameters();
        Integer currentFiscalYear = new Integer(((Number)jobParameters.get(KFSConstants.UNIV_FISCAL_YR)).intValue() + 1);
        Integer previousFiscalYear = new Integer(((Number)jobParameters.get(KFSConstants.UNIV_FISCAL_YR)).intValue());
        Map<String, Integer> organizationReversionCounts = new HashMap<String, Integer>();

        OrganizationReversionProcess orgRevProcess = SpringContext.getBean(OrganizationReversionProcess.class,"glOrganizationReversionTestProcess");
        
        clearGlBalanceTable();
        clearBatchFiles();
        //we do not need to call clearCache() since no dao and jdbc calls mixted in this method.
        //refer to KFSMI-7637
      //  persistenceService.clearCache();
        for (Balance bal : balancesToTest) {
            bal.setUniversityFiscalYear(previousFiscalYear);
            SpringContext.getBean(BusinessObjectService.class).save(bal);
        }
        //TODO:- commented out
        //OriginEntryGroup outputGroup = organizationReversionProcessService.createOrganizationReversionProcessOriginEntryGroup();
        
        //TODO:- fix 
        //orgRevProcess.setOutputGroup(outputGroup);
        orgRevProcess.setHoldGeneratedOriginEntries(true);
        orgRevProcess.organizationReversionProcess(jobParameters, organizationReversionCounts);

        // ye olde sanity check
        assertEquals("Balances Read", new Integer(balancesToTest.size()), new Integer(orgRevProcess.getBalancesRead()));

        // make sure this resulted in one Org Rev origin entry group
        //TODO:- commented out
//        Collection groups = originEntryGroupService.getAllOriginEntryGroup();
//        assertEquals("Origin Entries Group Size", new Integer(1), new Integer(groups.size()));
//
//        OriginEntryGroup group = (OriginEntryGroup) groups.iterator().next();
//        assertEquals("Origin Entry Group Source Code", OriginEntrySource.YEAR_END_ORG_REVERSION, group.getSourceCode());
        return orgRevProcess.getGeneratedOriginEntries();
    }
    
    /**
     * Asserts that certain fields in the given origin entry equal given parameters
     * @param originEntry the actual origin entry
     * @param fiscalYear the expected fiscal year
     * @param periodCode the expected period code
     * @param chart the expected chart
     * @param account the expected account
     * @param objectCode the expected object code
     * @param balanceType the expected balance type
     * @param objectType the expected object type
     * @param amount the expected amount
     */
    private void assertChartAndAccount(OriginEntryInformation originEntry, String chart, String account) {
        assertEquals("Origin Entry " + originEntry.toString() + " Chart of Accounts", chart, originEntry.getChartOfAccountsCode());
        assertEquals("Origin Entry " + originEntry.toString() + " Account Number", account, originEntry.getAccountNumber());
    }
    
    /**
     * Turns the flexible offset option on or off
     * @param flexibleOffsetsOn whether we should turn the flexible offsets on or off
     */
    private void toggleFlexibleOffsets(boolean flexibleOffsetsOn) {
        try {
            TestUtils.setSystemParameter(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG, (flexibleOffsetsOn ? "Y" : "N"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void createFlexibleOffsetAccounts() {
        // clear the flexible offsets table
        unitTestSqlDao.sqlCommand("delete from FP_OFST_ACCT_T");
        // save our offsets
        boService.save(FLEXIBLE_OFFSET_ACCOUNT_FIXTURE.FLEXIBLE_ACTIVITY_CLOSING_OFFSET_ACCOUNT.convertToOffsetAccount());
        boService.save(FLEXIBLE_OFFSET_ACCOUNT_FIXTURE.FLEXIBLE_ENCUMBRANCE_FORWARD_OFFSET_ACCOUNT.convertToOffsetAccount());
        boService.save(FLEXIBLE_OFFSET_ACCOUNT_FIXTURE.FLEXIBLE_CS_ENCUMBRANCE_FORWARD_OFFSET_ACCOUNT.convertToOffsetAccount());
        boService.save(FLEXIBLE_OFFSET_ACCOUNT_FIXTURE.CASH_REVERSION_FORWARD_OFFSET_ACCOUNT.convertToOffsetAccount());
    }
}
