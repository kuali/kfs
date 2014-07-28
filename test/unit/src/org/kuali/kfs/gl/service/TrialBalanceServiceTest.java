/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.gl.service;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kuali.kfs.gl.businessobject.TrialBalanceReport;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * This class tests the changes made to generate the trial balance report
 * based on fiscal period codes along with pre-existing search parameters.
 *
 * These tests also provide coverage for the related LookupableHelper and
 * DAO's, since if these classes don't function correctly, bad results
 * will manifest here.
 */
@ConfigureContext
public class TrialBalanceServiceTest extends KualiTestBase {

    /*
     * Enum to loop over for testing each period and chart-of-account code combos.
     */
    private enum TrialBalanceInfo {

        /*
         * The thirteen periods as found in tech specs for TrialAccountBalance functionality.
         * These will also be initialized with a common fiscalYear and fiscalCoaCode.
         */
        Period_01("01", "1612056.16"), Period_02("02", "2607203.32"), Period_03("03", "4088938.24"), Period_04("04", "4748510.56"), Period_05("05", "5299554.38"),
        Period_06("06", "5718802.02"), Period_07("07", "6363696.74"), Period_08("08", "6899673.30"), Period_09("09", "7383177.34"), Period_10("10", "7788766.00"),
        Period_11("11", "8260399.64"), Period_12("12", "8908269.48"), Period_13("13", "8908269.48");


        private String fiscalYear;
        private String fiscalPeriodCode;
        private String fiscalCoaCode; // Fiscal Chart-of-Accounts code
        private BigDecimal expectedBalance;


        /*
         * Initial the enum for period and balance specific values. The fiscalYear
         * and fiscalCoaCode are common across records being tested.
         */
        private TrialBalanceInfo(String code, String expectedBalanceString){
            this.fiscalYear = "2014"; // common for all records
            this.fiscalCoaCode = "BA"; // common for majority of records
            this.fiscalPeriodCode = code;
            this.expectedBalance = new BigDecimal(expectedBalanceString).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        }

        public String getFiscalYear(){
            return this.fiscalYear;
        }

        public String getFiscalCoaCode(){
            return this.fiscalCoaCode;
        }

        public String getFiscalPeriodCode(){
            return this.fiscalPeriodCode;
        }

        public BigDecimal getExpectedBalance(){
            return this.expectedBalance;
        }

    }//enum


    // Services
    private TrialBalanceService trialBalanceService;
    private UnitTestSqlDao unitTestSqlDao;

    // DB modification variables
    private static final String DATA_FILE_PATH = "test/unit/src/org/kuali/kfs/gl/batch/fixture/gl_trllblnc.csv";
    private static final int COLUMN_COUNT = 25;
    private static final String DELETE_ALL_ENTRIES_SQL = "DELETE FROM GL_BALANCE_T";

    // Note, the following is "newline'd" every five columns; also, beware of single quotes needed for varchar columns
    private static final String INSERT_SQL_FORMAT = "INSERT INTO GL_BALANCE_T ("
            + "UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, "
            + "FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, "
            + "CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, "
            + "MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, "
            + "MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT, TIMESTAMP)"
            + " VALUES (%s, '%s', '%s', '%s', '%s', "
            + "'%s', '%s', '%s', %s, %s, "
            + "%s, %s, %s, %s, %s, "
            + "%s, %s, %s, %s, %s, "
            + "%s, %s, %s, %s, '%s')";

    // Scale for BigDecimal instances
    private static final int SCALE = 2; // Changed easily if we need to do division/multiplication



    /**
     * This method sets up several services used in testing.
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        trialBalanceService = SpringContext.getBean(TrialBalanceService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }


    /*
     * This method takes a static file, splits it by newline,
     * splits each line by comma into tokens, then formats
     * the tokens into an SQL insert statement, and returns
     * the list of resulting sql insert strings.
     */
    private List<String> getInsertStatements(){
        List<String> lines = null;
        try{
            lines = IOUtils.readLines(new FileReader(new File(DATA_FILE_PATH)));
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        List<String> insertStatements = new ArrayList<String>();

        for(String line : lines){
            String[] tokens = line.split(",", -1);
            String sqlInsert = String.format(INSERT_SQL_FORMAT,
                    tokens[0], tokens[1], tokens[2], tokens[3], tokens[4],
                    tokens[5], tokens[6], tokens[7], tokens[8], tokens[9],
                    tokens[10], tokens[11], tokens[12], tokens[13], tokens[14],
                    tokens[15], tokens[16], tokens[17], tokens[18], tokens[19],
                    tokens[20], tokens[21], tokens[22], tokens[23], tokens[24]);
            insertStatements.add(sqlInsert);
        }

        return insertStatements;
    }


    /*
     * This method clears all records from the GL_BALANCE_T table,
     * pulls records from disk, and inserts the records into DB.
     *
     * In such a way, we know for sure the state of the DB while
     * testing.
     */
    private void initDbFromFile(){
        unitTestSqlDao.sqlCommand(DELETE_ALL_ENTRIES_SQL);
        List<String> insertStatements = getInsertStatements();
        for(String insertStatement : insertStatements){
            unitTestSqlDao.sqlCommand(insertStatement);
        }
    }


    /*
     * This call cascades down the related service, LookupableHelper, and DAO.
     * The passed in TrialBalanceInfo decides which reports are returned.
     */
    private List<TrialBalanceReport> getTrialBalanceReports(TrialBalanceInfo trialBalanceInfo){
        List objects = trialBalanceService.findTrialBalance(trialBalanceInfo.getFiscalYear(),
                                                            trialBalanceInfo.getFiscalCoaCode(),
                                                            trialBalanceInfo.getFiscalPeriodCode());
        List<TrialBalanceReport> trialBalanceReports = new ArrayList<TrialBalanceReport>();
        for(Object o : objects){
            TrialBalanceReport trialBalanceReport = (TrialBalanceReport)o;
            trialBalanceReports.add(trialBalanceReport);
        }
        return trialBalanceReports;
    }


    /*
     * This method adds up all non-null credits from the passed in TrialBalanceReport
     * and returns the total sum.
     */
    private BigDecimal getCreditBalance(List<TrialBalanceReport> trialBalanceReports){
        BigDecimal creditBalance = BigDecimal.ZERO.setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        for(TrialBalanceReport trialBalanceReport : trialBalanceReports){
            KualiDecimal credit = trialBalanceReport.getCreditAmount();
            if(ObjectUtils.isNotNull(credit)){
                creditBalance = creditBalance.add(credit.bigDecimalValue().setScale(SCALE, BigDecimal.ROUND_HALF_UP));
            }
        }
        return creditBalance;
    }


    /*
     * This method adds up all non-null debits from the passed in TrialBalanceReport
     * and returns the total sum.
     */
    private BigDecimal getDebitBalance(List<TrialBalanceReport> trialBalanceReports){
        BigDecimal debitBalance = BigDecimal.ZERO.setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        for(TrialBalanceReport trialBalanceReport : trialBalanceReports){
            KualiDecimal debit = trialBalanceReport.getDebitAmount();
            if(ObjectUtils.isNotNull(debit)){
                debitBalance = debitBalance.add(debit.bigDecimalValue().setScale(SCALE, BigDecimal.ROUND_HALF_UP));
            }
        }
        return debitBalance;
    }


    /**
     * This method tests the service's results for finding a trial balance
     * based on search parameters encapsulated by the TrialBalanceInfo enum.
     */
    public void testFindTrialBalance(){

        // Clear all records from DB, insert controlled ones from file.
        // Doing this here so that each test method is free to mutate
        // records as necessary
        initDbFromFile();

        for(TrialBalanceInfo trialBalanceInfo : TrialBalanceInfo.values()){
            // Perform query through the new code
            List<TrialBalanceReport> trialBalanceReports = getTrialBalanceReports(trialBalanceInfo);

            // Assert the credit balance is correct
            BigDecimal expectedBalance = trialBalanceInfo.getExpectedBalance();
            BigDecimal calculatedCreditBalance = getCreditBalance(trialBalanceReports);
            String msg = String.format("Unexpected balance for period '%s'; expected balance of '%s', but found credit balance of '%s'.", trialBalanceInfo.getFiscalPeriodCode(), expectedBalance, calculatedCreditBalance);
            assertTrue(msg, expectedBalance.compareTo(calculatedCreditBalance) == 0);

            // Assert the debit balance is correct
            BigDecimal calculatedDebitBalance = getDebitBalance(trialBalanceReports);
            msg = String.format("Unexpected balance for period '%s'; expected balance of '%s', but found debit balance of '%s'.", trialBalanceInfo.getFiscalPeriodCode(), expectedBalance, calculatedDebitBalance);
            assertTrue(msg, expectedBalance.compareTo(calculatedDebitBalance) == 0);
        }

    }


    /*
     * Test to ensure report pdf is generated on the file system
     */
    public void testGenerateReportForExtractProcess(){

        // Clear all records from DB, insert controlled ones from file.
        // Doing this here so that each test method is free to mutate
        // records as necessary
        initDbFromFile();

        File reportFile = null;
        try{

            Collection dataSource = getTrialBalanceReports(TrialBalanceInfo.Period_13);
            String reportFilePath = trialBalanceService.generateReportForExtractProcess(dataSource, TrialBalanceInfo.Period_13.getFiscalYear(), TrialBalanceInfo.Period_13.getFiscalPeriodCode());
            reportFile = new File(reportFilePath);

            // Assert file exists
            String msg = String.format("Expected report file does not exist, should be found at '%s'.",  reportFilePath);
            assertTrue(msg, reportFile.exists());

            // Assert file is non-zero
            long fileSize = reportFile.length();
            msg = String.format("Expected '%s' to a non-zero size.", fileSize);
            assertTrue(msg, fileSize > 0);

        } finally {
            // If anything goes wrong, we still need to clean up,
            // not done in tearDown(), since we'd potentially have to save
            // state between tests
            FileUtils.deleteQuietly(reportFile);
        }
    }

}
