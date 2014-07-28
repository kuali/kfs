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
     * Enum to loop over for testing each period/chart-of-accounts code combo
     */
    private enum TrialBalanceInfo {

        /*
         * The thirteen periods as found in tech specs for TrialAccountBalance functionality.
         * These will also be initialized with a common fiscalYear and fiscalCoaCode.
         */
        Period_01("01", "1612056.16"), Period_02("02", "2607203.32"), Period_03("03", "4088938.24"), Period_04("04", "4748510.56"), Period_05("05", "5299554.38"),
        Period_06("06", "5718802.02"), Period_07("07", "6363696.74"), Period_08("08", "6899673.30"), Period_09("09", "7383177.34"), Period_10("10", "7788766.00"),
        Period_11("11", "8260399.64"), Period_12("12", "8908269.48"), Period_13("13", "8908269.48");

        // Magic Strings, common across relevant records in TrialBalanceServiceTest.DATA_FILE_PATH
        private static final String FISCAL_YEAR = "2014";
        private static final String FISCAL_CHART_OF_ACCOUNTS_CODE = "BA";

        // Instance Variables
        private String fiscalPeriodCode;
        private BigDecimal expectedBalance;


        /*
         * Initialize the enum for period and balance specific values.
         */
        private TrialBalanceInfo(final String code, final String expectedBalanceString){
            this.fiscalPeriodCode = code;
            this.expectedBalance = new BigDecimal(expectedBalanceString).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        }

        public String getFiscalYear(){
            return FISCAL_YEAR;
        }

        public String getFiscalCoaCode(){
            return FISCAL_CHART_OF_ACCOUNTS_CODE;
        }

        public String getFiscalPeriodCode(){
            return this.fiscalPeriodCode;
        }

        public BigDecimal getExpectedBalance(){
            return this.expectedBalance;
        }

    }//enum


    /*
     * Enum used for switching of adding up credit OR debit fields
     * for a TrialBalanceReport; enables code re-use of getBalance(...)
     */
    private enum BalanceType{
        CREDIT, DEBIT
    }//enum


    // Magic value variables
    private static final String DATA_FILE_PATH = "test/unit/src/org/kuali/kfs/gl/batch/fixture/gl_trllblnc.csv";
    private static final String DATA_FILE_DELIM = ",";
    private static final int COLUMN_COUNT = 25;
    private static final String DELETE_ALL_ENTRIES_SQL = "DELETE FROM GL_BALANCE_T";
    private static final String TIMESTAMP_FORMAT = "DD/MM/YYYY HH12:MI:SS PM"; // UnitTestSqlDao is hard coded to this value
    private static final int SCALE = 2; // Scale for BigDecimal instances

    // Note, the following is "newline'd" every five columns; also, be aware of the single quotes needed for varchar columns
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
            + "%s, %s, %s, %s, %s)";

    // Services
    private TrialBalanceService trialBalanceService;
    private UnitTestSqlDao unitTestSqlDao;


    /**
     * This method sets up several services and intializes
     * the DB to only contain records from file.
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        trialBalanceService = SpringContext.getBean(TrialBalanceService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        initDbFromFile();
    }


    /**
     * This method tests the service's results for finding a trial balance
     * based on search parameters encapsulated by the TrialBalanceInfo enum.
     */
    public void testFindTrialBalance(){
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


    /**
     * Test to ensure report pdf is generated on the file system
     */
    public void testGenerateReportForExtractProcess(){
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
            msg = String.format("Expected '%s' to be a non-zero size.", fileSize);
            assertTrue(msg, fileSize > 0);

        } finally {
            // If anything goes wrong, we still need to clean up, not done in
            // tearDown(), since this is the only test that creates a file
            FileUtils.deleteQuietly(reportFile);
        }
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
            String[] tokens = line.split(DATA_FILE_DELIM, -1);

            // Be DB agnostic with different timestamp functions
            String timeStampString = tokens[24];
            String timeStampSql = getTimestampSql(timeStampString);

            String sqlInsert = String.format(INSERT_SQL_FORMAT,
                    tokens[0], tokens[1], tokens[2], tokens[3], tokens[4],
                    tokens[5], tokens[6], tokens[7], tokens[8], tokens[9],
                    tokens[10], tokens[11], tokens[12], tokens[13], tokens[14],
                    tokens[15], tokens[16], tokens[17], tokens[18], tokens[19],
                    tokens[20], tokens[21], tokens[22], tokens[23], timeStampSql);
            insertStatements.add(sqlInsert);
        }

        return insertStatements;
    }


    /*
     * Build DB platform-specific date function sql, e.g.:
     *     MySql  -> STR_TO_DATE('07/05/2014 07:49:19 AM', '%d/%m/%Y %r')
     *     Oracle -> TO_DATE('07/05/2014 07:49:19 AM', 'DD/MM/YYYY HH12:MI:SS PM')
     */
    private String getTimestampSql(String timeStampString){
        StringBuffer sb = new StringBuffer();
        sb.append(unitTestSqlDao.getDbPlatform().getStrToDateFunction());
        sb.append("('");
        sb.append(timeStampString);
        sb.append("', ");
        sb.append(unitTestSqlDao.getDbPlatform().getDateFormatString(TIMESTAMP_FORMAT));
        sb.append(")");

        return sb.toString();
    }


    /*
     * This call cascades down the related service, LookupableHelper, and DAO.
     * The passed in TrialBalanceInfo determines which reports are returned.
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
        return getBalance(trialBalanceReports, BalanceType.CREDIT);
    }


    /*
     * This method adds up all non-null debits from the passed in TrialBalanceReport
     * and returns the total sum.
     */
    private BigDecimal getDebitBalance(List<TrialBalanceReport> trialBalanceReports){
        return getBalance(trialBalanceReports, BalanceType.DEBIT);
    }


    /*
     * Helper method that adds up debit/credit acroos collection of TrialBalanceReport
     */
    private BigDecimal getBalance(List<TrialBalanceReport> trialBalanceReports, BalanceType balanceType){
        BigDecimal balance = BigDecimal.ZERO.setScale(SCALE, BigDecimal.ROUND_HALF_UP);

        for(TrialBalanceReport trialBalanceReport : trialBalanceReports){
            KualiDecimal testBalance;
            if (balanceType == BalanceType.CREDIT) {
                testBalance = trialBalanceReport.getCreditAmount();
            }else {
                // balanceType == BalanceType.DEBIT
                testBalance = trialBalanceReport.getDebitAmount();
            }

            if(ObjectUtils.isNotNull(testBalance)){
                balance = balance.add(testBalance.bigDecimalValue().setScale(SCALE, BigDecimal.ROUND_HALF_UP));
            }
        }//for

        return balance;
    }

}
