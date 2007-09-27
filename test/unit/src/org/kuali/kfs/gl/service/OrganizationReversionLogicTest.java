/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl.orgreversion;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.service.OrgReversionUnitOfWorkService;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.kuali.module.gl.service.OrganizationReversionSelection;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.impl.OrganizationReversionMockService;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class OrganizationReversionLogicTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionLogicTest.class);
    
    public static final String DEFAULT_BALANCE_CHART = "BL";
    public static final String DEFAULT_BALANCE_ACCOUNT_NBR = "1031400";
    
    private OrganizationReversionService organizationReversionService;
    private BalanceService balanceService;
    private OrganizationReversionSelection organizationReversionSelection;
    private OrganizationReversionCategoryLogic cashOrganizationReversionCategoryLogic;
    private PriorYearAccountService priorYearAccountService;
    private ReportService reportService;
    private OrgReversionUnitOfWorkService orgReversionUnitOfWorkService;
    private OrganizationReversionProcess orgRevProcess;
    private Integer currentFiscalYear;
    private Integer previousFiscalYear;
    
    /*
     * These fixtures come from the spreadsheet org+reversion.xls that Bill Overman posted
     * as an attachment to JIRA KULRNE-5652
     */
    enum BALANCE_FIXTURE{
        SCENARIO1_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(5000)),
        SCENARIO1_ACTUAL_BALANCE("AC", new KualiDecimal(3000)),
        
        SCENARIO2_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(4000)),
        SCENARIO2_ACTUAL_BALANCE("AC", new KualiDecimal(7000)),
        
        SCENARIO3_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(2000)),
        SCENARIO3_ACTUAL_BALANCE("AC", new KualiDecimal(2000)),
        
        SCENARIO4_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(5000)),
        SCENARIO4_ACTUAL_BALANCE("AC", new KualiDecimal(3000)),
        SCENARIO4_ENCUMBRANCE_BALANCE("IE", new KualiDecimal(500)),
        
        SCENARIO5_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(5000)),
        SCENARIO5_ACTUAL_BALANCE("AC", new KualiDecimal(3000)),
        SCENARIO5_ENCUMBRANCE_BALANCE("IE", new KualiDecimal(2000)),
        
        SCENARIO6_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(5000)),
        SCENARIO6_ACTUAL_BALANCE("AC", new KualiDecimal(3000)),
        SCENARIO6_ENCUMBRANCE_BALANCE("IE", new KualiDecimal(2500)),
        
        SCENARIO7_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(4000)),
        SCENARIO7_ACTUAL_BALANCE("AC", new KualiDecimal(7000)),
        SCENARIO7_ENCUMBRANCE_BALANCE("IE", new KualiDecimal(500));
        
        private String objectCode;
        private String balanceTypeCode;
        private String objectTypeCode;
        private KualiDecimal amount;
        private Date timestamp;
        private static final String DATE_FORMAT = "yyyy-MM-dd";
        private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BALANCE_FIXTURE.class);
        
        private BALANCE_FIXTURE(String balanceTypeCode, KualiDecimal amount) {
            this.balanceTypeCode = balanceTypeCode;
            this.amount = amount;
        }
        
        public void setObjectCode(String objectCode) {
            this.objectCode = objectCode;
        }
        
        
        public Balance convertToBalance() {
            Balance balance = new Balance();
            balance.setUniversityFiscalYear(new Integer((SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear()).intValue() - 1));
            balance.setChartOfAccountsCode(DEFAULT_BALANCE_CHART);
            balance.setAccountNumber(DEFAULT_BALANCE_ACCOUNT_NBR);
            balance.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            balance.setObjectCode(objectCode);
            balance.setSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            balance.setBalanceTypeCode(balanceTypeCode);
            balance.setObjectTypeCode("EX");
            balance.setAccountLineAnnualBalanceAmount(amount);
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
                java.util.Date jud = sdf.parse(SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, GLConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM));
                balance.setTimestamp(new java.sql.Date(jud.getTime()));
            }
            catch (ParseException e) {
                LOG.debug("Parse date exception while parsing transaction date");
            }
            balance.refresh();
            return balance;
        }
    };
    
    enum OBJECT_CODE_FIXTURE {
        C01_ORG_WAGES_CODE("3000"), // C01 = A logic
        C02_SALARY_FRINGES_CODE("2400"), // C02 = C1 logic
        C03_FINANCIAL_AID_CODE("5880"), // C03 = C2 logic
        C04_CAPITAL_EQUIP_CODE("7200"), // C04 = N1 logic
        C05_RESERVE_CODE("7990"), // C05 = N2 logic
        C08_TRAVEL_CODE("6000"), // C08 = R1 logic
        C09_OTHER_EXPENSE_CODE("4100"); // C09 = R2 logic
        
        private String code;
        
        private OBJECT_CODE_FIXTURE(String code) {
            this.code = code;
        }
        
        public String getCode() {
            return this.code;
        }
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        
        Map<String, OrganizationReversionService> orgRevServiceBeans = SpringContext.getBeansOfType(OrganizationReversionService.class);
        organizationReversionService = orgRevServiceBeans.get("glOrganizationReversionMockService");
        DateTimeService dtService = SpringContext.getBean(DateTimeService.class);
        balanceService = SpringContext.getBean(BalanceService.class);
        organizationReversionSelection = SpringContext.getBean(OrganizationReversionSelection.class);
        cashOrganizationReversionCategoryLogic = SpringContext.getBean(CashOrganizationReversionCategoryLogic.class);
        priorYearAccountService = SpringContext.getBean(PriorYearAccountService.class);
        reportService = SpringContext.getBean(ReportService.class);
        orgReversionUnitOfWorkService = SpringContext.getBean(OrgReversionUnitOfWorkService.class);
        
        currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        previousFiscalYear = new Integer(currentFiscalYear.intValue() - 1);
        
        orgRevProcess = new OrganizationReversionProcess(true, organizationReversionService, kualiConfigurationService, balanceService, organizationReversionSelection, originEntryGroupService, originEntryService, persistenceService, dtService, cashOrganizationReversionCategoryLogic, priorYearAccountService, reportService, orgReversionUnitOfWorkService);
        orgRevProcess.initializeProcess();
    }
    
    protected void tearDown() throws Exception {
        orgRevProcess = null;
    }
    
    protected OriginEntryGroup runOrganizationReversionProcess(List<Balance> balancesToTestAgainst) {
        clearGlBalanceTable();
        clearOriginEntryTables();
        persistenceService.clearCache();
        for (Balance bal: balancesToTestAgainst) {
            balanceService.save(bal);
        }
        orgRevProcess.setHoldGeneratedOriginEntries(true);
        orgRevProcess.organizationReversionProcess();
        
        // ye olde sanity check
        assertEquals("Balances Read", new Integer(orgRevProcess.getBalancesRead()), new Integer(balancesToTestAgainst.size()));
        
        // make sure this resulted in one Org Rev origin entry group
        Collection groups = originEntryGroupService.getAllOriginEntryGroup();
        assertEquals("Origin Entries Group Size", new Integer(groups.size()), new Integer(1));
        
        OriginEntryGroup group = (OriginEntryGroup)groups.iterator().next();
        assertEquals("Origin Entry Group Source Code", group.getSourceCode(), OriginEntrySource.YEAR_END_ORG_REVERSION);
        return group;
    }
    
    private void assertOriginEntry(OriginEntry originEntry, Integer fiscalYear, String periodCode, String chart, String account, String objectCode, String balanceType, String objectType, KualiDecimal amount) {
        assertEquals("Origin Entry "+originEntry.toString()+" Fiscal Year", fiscalYear, originEntry.getUniversityFiscalYear());
        assertEquals("Origin Entry "+originEntry.toString()+" Fiscal Period", periodCode, originEntry.getUniversityFiscalPeriodCode());
        assertEquals("Origin Entry "+originEntry.toString()+" Chart of Accounts", chart, originEntry.getChartOfAccountsCode());
        assertEquals("Origin Entry "+originEntry.toString()+" Account Number", account, originEntry.getAccountNumber());
        assertEquals("Origin Entry "+originEntry.toString()+" Object Code", objectCode, originEntry.getFinancialObjectCode());
        assertEquals("Origin Entry "+originEntry.toString()+" Balance Type", balanceType, originEntry.getFinancialBalanceTypeCode());
        assertEquals("Origin Entry "+originEntry.toString()+" Object Type", objectType, originEntry.getFinancialObjectTypeCode());
        assertEquals("Origin Entry "+originEntry.toString()+" Amount", amount, originEntry.getTransactionLedgerEntryAmount());
    }
    
    private List<OriginEntryFull> putOriginEntriesInList(Iterator<OriginEntryFull> originEntriesIterator) {
        List<OriginEntryFull> entries = new ArrayList<OriginEntryFull>();
        while (originEntriesIterator.hasNext()) {
            entries.add(originEntriesIterator.next());
        }
        return entries;
    }
    
    private void logAllEntries(List<OriginEntryFull> originEntries) {
        for (OriginEntryFull entry: originEntries) {
            LOG.info(entry.getLine());
        }
    }
    
    /*
     * ************** SCENARIO 1 *****************
     */
    public void testBudgetGreaterThanActualNoEncumbrancesLogicA() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualNoEncumbrancesLogicC1() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualNoEncumbrancesLogicC2() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
   public void testBudgetGreaterThanActualNoEncumbrancesLogicN1() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualNoEncumbrancesLogicN2() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualNoEncumbrancesLogicR1() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualNoEncumbrancesLogicR2() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        //      add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    /*
     * ************** SCENARIO 2 *****************
     */
    public void testBudgetLessThanActualNoEncumbrancesALogic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }
    
    public void testBudgetLessThanActualNoEncumbrancesC1Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    public void testBudgetLessThanActualNoEncumbrancesC2Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    public void testBudgetLessThanActualNoEncumbrancesN1Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, "1031400", "5000", "CB", "EX", new KualiDecimal(-3000));
    }

    public void testBudgetLessThanActualNoEncumbrancesN2Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }

    public void testBudgetLessThanActualNoEncumbrancesR1Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    public void testBudgetLessThanActualNoEncumbrancesR2Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }
    
    /*
     * ************** SCENARIO 3 *****************
     */
    
    public void testBudgetEqualsActualNoEncumbrancesALogic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(0));
    }
    
    public void testBudgetEqualsActualNoEncumbrancesC1Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(0));
    }

    public void testBudgetEqualsActualNoEncumbrancesC2Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(0));
    }
    
    public void testBudgetEqualsActualNoEncumbrancesN1Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(0));
    }
    
    public void testBudgetEqualsActualNoEncumbrancesN2Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(0));
    }
    
    public void testBudgetEqualsActualNoEncumbrancesR1Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
     
        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(0));
    }
    
    public void testBudgetEqualsActualNoEncumbrancesR2Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(0));
    }
    
    /*
     * ************** SCENARIO 4 *****************
     */
    
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicA() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicC1() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicC2() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicN1() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(4));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-1500));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(1500));
        assertOriginEntry(originEntries.get(2), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(500));
        assertOriginEntry(originEntries.get(3), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(500));
    }
    
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicN2() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicR1() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(4));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-1500));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(1500));
        assertOriginEntry(originEntries.get(2), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(500));
        assertOriginEntry(originEntries.get(3), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(500));
    }
    
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicR2() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    /*
     * ************** SCENARIO 5 *****************
     */
    
   public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicA() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicC1() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicC2() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicN1() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicN2() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicR1() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicR2() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    /*
     * ************** SCENARIO 6 *****************
     */
    
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicA() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicC1() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicC2() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicN1() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicN2() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicR1() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }
    
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicR2() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }
    
    /*
     * ************** SCENARIO 7 *****************
     */
    
    public void testActualGreaterThanBudgetWithEncumbrancesLogicA() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }
    
    public void testActualGreaterThanBudgetWithEncumbrancesLogicC1() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }
    
    public void testActualGreaterThanBudgetWithEncumbrancesLogicC2() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
     
        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }
    
    public void testActualGreaterThanBudgetWithEncumbrancesLogicN1() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }
    
    public void testActualGreaterThanBudgetWithEncumbrancesLogicN2() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(originEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }
    
    public void testActualGreaterThanBudgetWithEncumbrancesLogicR1() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }
    
    public void testActualGreaterThanBudgetWithEncumbrancesLogicR2() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        
        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);
        
        OriginEntryGroup orgRevGroup = runOrganizationReversionProcess(balancesToCheck);
        
        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));
        
        List<OriginEntryFull> originEntries = orgRevProcess.getGeneratedOriginEntries();
        logAllEntries(originEntries);
        
        assertEquals("correct number of origin entries returned? ", new Integer(originEntries.size()), new Integer(2));
        
        // check the origin entries
        assertOriginEntry(originEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(originEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockService.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }
}
