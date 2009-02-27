/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.service.BalanceTypService;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.BalanceForwardRuleHelper;
import org.kuali.kfs.gl.batch.EncumbranceClosingOriginEntryFactory;
import org.kuali.kfs.gl.batch.EncumbranceClosingRuleHelper;
import org.kuali.kfs.gl.batch.NominalActivityClosingHelper;
import org.kuali.kfs.gl.batch.dataaccess.YearEndDao;
import org.kuali.kfs.gl.batch.service.YearEndService;
import org.kuali.kfs.gl.batch.service.impl.exception.FatalErrorException;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.dataaccess.EncumbranceDao;
import org.kuali.kfs.gl.report.Summary;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.ReportService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the logic to perform year end tasks.
 */
@Transactional
public class YearEndServiceImpl implements YearEndService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(YearEndServiceImpl.class);

    private EncumbranceDao encumbranceDao;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private BalanceService balanceService;
    private BalanceTypService balanceTypeService;
    private ObjectTypeService objectTypeService;
    private ParameterService parameterService;
    private KualiConfigurationService configurationService;
    private PriorYearAccountService priorYearAccountService;
    private SubFundGroupService subFundGroupService;
    private EncumbranceClosingRuleHelper encumbranceClosingRuleHelper;
    private ReportService reportService;
    private PersistenceService persistenceService;
    private YearEndDao yearEndDao;
    private String batchFileDirectoryName;

    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * Constructs a YearEndServiceImpl, and that's about it.
     */
    public YearEndServiceImpl() {
        super();
    }

    /**
     * This class actually generates all the origin entries for nominal activity closing and saves them to the proper origin entry
     * group. Note: Much (but no longer all) of the original COBOL program this code is based off of is within the comments.
     * 
     * @param nominalClosingOriginEntryGroup the origin entry group to save the generated nominal closing entries to
     * @param nominalClosingJobParameters a map of parameters for the job:
     * @param nominalClosingCounts various statistical counts
     * @see org.kuali.kfs.gl.batch.service.YearEndService#closeNominalActivity()
     */
    public void closeNominalActivity(String nominalClosingFileName, Map nominalClosingJobParameters, Map<String, Integer> nominalClosingCounts) {

        Integer varFiscalYear = (Integer) nominalClosingJobParameters.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR);
        NominalActivityClosingHelper closingHelper = new NominalActivityClosingHelper(varFiscalYear, (Date) nominalClosingJobParameters.get(GeneralLedgerConstants.ColumnNames.UNIV_DT), parameterService, configurationService);
        
        closingHelper.addNominalClosingJobParameters(nominalClosingJobParameters);

        nominalClosingCounts.put("globalReadCount", new Integer(balanceService.countBalancesForFiscalYear(varFiscalYear)));
        Iterator<Balance> balanceIterator = balanceService.findNominalActivityBalancesForFiscalYear(varFiscalYear);

        String accountNumberHold = null;

        boolean selectSw = false;

        nominalClosingCounts.put("globalSelectCount", new Integer(0));
        nominalClosingCounts.put("sequenceNumber", new Integer(0));
        nominalClosingCounts.put("sequenceWriteCount", new Integer(0));
        nominalClosingCounts.put("sequenceCheckCount", new Integer(0));

        boolean nonFatalErrorFlag = false;

        //create files
        File nominalClosingFile = new File(batchFileDirectoryName + File.separator + nominalClosingFileName);
        PrintStream nominalClosingPs = null;
        
        try {
            nominalClosingPs = new PrintStream(nominalClosingFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("nominalClosingFile Files doesn't exist " + nominalClosingFileName);
        }
        
        while (balanceIterator.hasNext()) {

            Balance balance = balanceIterator.next();
            balance.refreshReferenceObject("option");

            selectSw = true;

            try {

                LOG.debug("Balance selected.");

                // 792 004750 IF GLGLBL-ACCOUNT-NBR = WS-ACCOUNT-NBR-HOLD

                if (balance.getAccountNumber().equals(accountNumberHold)) {

                    // 793 004760 ADD 1 TO WS-SEQ-NBR

                    incrementCount(nominalClosingCounts, "sequenceNumber");

                    // 794 004770 ELSE

                }
                else {

                    // 795 004780 MOVE 1 TO WS-SEQ-NBR.

                    nominalClosingCounts.put("sequenceNumber", new Integer(1));

                }

                // 797 004800 ADD +1 TO GLBL-SELECT-COUNT

                incrementCount(nominalClosingCounts, "globalSelectCount");

                // 798 004810 PERFORM 4000-WRITE-OUTPUT
                // 799 004820 THRU 4000-WRITE-OUTPUT-EXIT

                // 840 005230 4000-WRITE-OUTPUT.
                // 841 005240 PERFORM 4100-WRITE-ACTIVITY
                // 842 005250 THRU 4100-WRITE-ACTIVITY-EXIT.
                
                OriginEntryFull activityEntry = closingHelper.generateActivityEntry(balance, nominalClosingCounts.get("sequenceHelper"));

                // 1051 007290 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-N
                // 1052 007300 WS-AMT-W-PERIOD.
                // 1053 007310 MOVE WS-AMT-X TO TRN-AMT-RED-X.
                // 1054 007320 WRITE GLE-DATA FROM GLEN-RECORD.
                
                originEntryService.createEntry(activityEntry, nominalClosingPs);

                // 1055 007330 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.

                // 1056 007340 IF GLEDATA-STATUS > '09'
                // 1057 007350 DISPLAY '**ERROR WRITING GLE DATA FILE '
                // 1058 007360 DISPLAY ' STATAS CODE IS ' GLEDATA-STATUS
                // 1059 007370 MOVE 08 TO RETURN-CODE
                // 1060 007380 STOP RUN
                // 1061 007390 END-IF

                // NOTE (laran) A failed write will throw an exception
                // which will automatically stop execution. So this logic
                // doesn't need to be explicitly duplicated.

                // 1062 007400 ADD +1 TO SEQ-WRITE-COUNT.

                incrementCount(nominalClosingCounts, "sequenceWriteCount");

                // 1063 MOVE SEQ-WRITE-COUNT TO SEQ-CHECK-CNT.

                nominalClosingCounts.put("sequenceCheckCount", new Integer(nominalClosingCounts.get("sequenceWriteCount").intValue()));

                // 1064 IF SEQ-CHECK-CNT (7:3) = '000'

                if (0 == nominalClosingCounts.get("sequenceCheckCount").intValue() % 1000) {

                    // 1065 DISPLAY ' SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT.

                    LOG.info(new StringBuffer("  SEQUENTIAL RECORDS WRITTEN = ").append(nominalClosingCounts.get("sequenceCheckCount")).toString());

                }

                // 1066 007410 4100-WRITE-ACTIVITY-EXIT.
                // 1067 007420 EXIT.

                // 843 005260 PERFORM 4200-WRITE-OFFSET
                // 844 005270 THRU 4200-WRITE-OFFSET-EXIT.

                
                // 1141 008160 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-N
                // 1142 008170 WS-AMT-W-PERIOD.
                // 1143 008180 MOVE WS-AMT-X TO TRN-AMT-RED-X.

                // 1144 008190 WRITE GLE-DATA FROM GLEN-RECORD.

                OriginEntryFull offsetEntry = closingHelper.generateOffset(balance, nominalClosingCounts.get("sequenceWriteCount"));
                
                originEntryService.createEntry(offsetEntry, nominalClosingPs);

                // 1145 008200 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
                // 1146 008210 IF GLEDATA-STATUS > '09'
                // 1147 008220 DISPLAY '**ERROR WRITING GLE DATA FILE '
                // 1148 008230 DISPLAY ' STATAS CODE IS ' GLEDATA-STATUS
                // 1149 008240 MOVE 08 TO RETURN-CODE
                // 1150 008250 STOP RUN
                // 1151 008260 END-IF.

                // 1152 008270 ADD +1 TO SEQ-WRITE-COUNT.

                incrementCount(nominalClosingCounts, "sequenceWriteCount");

                // 1153 MOVE SEQ-WRITE-COUNT TO SEQ-CHECK-CNT.

                nominalClosingCounts.put("sequenceCheckCount", new Integer(nominalClosingCounts.get("sequenceWriteCount").intValue()));

                // 1154 IF SEQ-CHECK-CNT (7:3) = '000'

                if (0 == nominalClosingCounts.get("sequenceCheckCount").intValue() % 1000) {

                    // 1155 DISPLAY ' SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT.

                    LOG.info(new StringBuffer(" ORIGIN ENTRIES INSERTED = ").append(nominalClosingCounts.get("sequenceCheckCount")).toString());

                }

                if (nominalClosingCounts.get("globalSelectCount").intValue() % 1000 == 0) {
                    persistenceService.clearCache();
                }


                // 802 004850 MOVE GLGLBL-ACCOUNT-NBR
                // 803 004860 TO WS-ACCOUNT-NBR-HOLD.

                accountNumberHold = balance.getAccountNumber();

                // 782 004650 THRU 2500-PROCESS-UNIT-OF-WORK-EXIT.

            }
            catch (FatalErrorException fee) {

                LOG.warn("Failed to create entry pair for balance.", fee);

            }

            // 783 004660 GO TO FETCH-CURSOR.

        }
        
        nominalClosingCounts.put("nonFatalCount", closingHelper.getNonFatalErrorCount());
        
        // 784 004670 2000-DRIVER-EXIT.
        // 785 004680 EXIT.

        nominalClosingPs.close();
    }

    /**
     * This method generates PDF report (there's only one) about the nominal activity closing job that was just completed.
     * 
     * @see org.kuali.kfs.gl.batch.service.YearEndService#generateCloseNominalActivityReports(org.kuali.kfs.gl.businessobject.OriginEntryGroup,
     *      java.util.Map, java.util.Map)
     */
    public void generateCloseNominalActivityReports(OriginEntryGroup nominalClosingOriginEntryGroup, Map nominalClosingJobParameters, Map<String, Integer> nominalActivityClosingCounts) {
        // Assemble statistics.
        List statistics = new ArrayList();
        Summary summary = new Summary();
        summary.setSortOrder(1);
        summary.setDescription("NUMBER OF GLBL RECORDS READ");
        summary.setCount(nominalActivityClosingCounts.get("globalReadCount"));
        statistics.add(summary);

        summary = new Summary();
        summary.setSortOrder(2);
        summary.setDescription("NUMBER OF GLBL RECORDS SELECTED");
        summary.setCount(nominalActivityClosingCounts.get("globalSelectCount"));
        statistics.add(summary);

        summary = new Summary();
        summary.setSortOrder(3);
        summary.setDescription("NUMBER OF SEQ RECORDS WRITTEN");
        summary.setCount(nominalActivityClosingCounts.get("sequenceWriteCount"));
        statistics.add(summary);

        Date runDate = new Date(dateTimeService.getCurrentDate().getTime());
        reportService.generateNominalActivityClosingStatisticsReport(nominalClosingJobParameters, statistics, runDate, nominalClosingOriginEntryGroup);

    }

    /**
     * A method that increments a count within a Map by 1
     * 
     * @param counts a Map of count statistics
     * @param countName the name of the specific count ot update
     * @return the newly incremented amount
     */
    private int incrementCount(Map<String, Integer> counts, String countName) {
        Integer value = counts.get(countName);
        int incremented = value.intValue() + 1;
        counts.put(countName, new Integer(incremented));
        return incremented;
    }

    /**
     * This method handles executing the loop over all balances, and generating reports on the balance forwarding process as a
     * whole. This method delegates all of the specific logic in terms of what balances to forward, according to what criteria, how
     * origin entries are generated, etc. This relationship makes YearEndServiceImpl and BalanceForwardRuleHelper heavily dependent
     * upon one another in terms of expected behavior.
     * 
     * @param balanceForwardsUnclosedPriorYearAccountGroup the origin entry group to save balance forwarding origin entries with
     *        open accounts
     * @param balanceForwardsClosedPriorYearAccountGroup the origin entry group to save balance forwarding origin entries with
     *        closed accounts
     * @param balanceForwardRuleHelper the BalanceForwardRuleHelper which holds the important state - the job parameters and
     *        statistics - for the job to run
     */
    public void forwardBalances(String balanceForwardsUnclosedFileName, String balanceForwardsclosedFileName, BalanceForwardRuleHelper balanceForwardRuleHelper) {
        LOG.debug("forwardBalances() started");

        // The rule helper maintains the state of the overall processing of the entire
        // set of year end balances. This state is available via balanceForwardRuleHelper.getState().
        // The helper and this class (YearEndServiceImpl) are heavily dependent upon one
        // another in terms of expected behavior and shared responsibilities.
        balanceForwardRuleHelper.setPriorYearAccountService(priorYearAccountService);
        balanceForwardRuleHelper.setSubFundGroupService(subFundGroupService);
        balanceForwardRuleHelper.setOriginEntryService(originEntryService);
        balanceForwardRuleHelper.getState().setGlobalReadCount(balanceService.countBalancesForFiscalYear(balanceForwardRuleHelper.getClosingFiscalYear()));

        Balance balance;

        //create files
        File unclosedOutputFile = new File(batchFileDirectoryName + File.separator + balanceForwardsUnclosedFileName);
        File closedOutputFile = new File(batchFileDirectoryName + File.separator + balanceForwardsclosedFileName);
        PrintStream unclosedPs = null;
        PrintStream closedPs = null;
        
        try {
            unclosedPs = new PrintStream(unclosedOutputFile);
            closedPs = new PrintStream(closedOutputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("balanceForwards Files don't exist " + balanceForwardsUnclosedFileName + " and " + balanceForwardsclosedFileName);
        }
        
        // do the general forwards
        Iterator<Balance> generalBalances = balanceService.findGeneralBalancesToForwardForFiscalYear(balanceForwardRuleHelper.getClosingFiscalYear());
        while (generalBalances.hasNext()) {
            balance = generalBalances.next();
            try {
                balanceForwardRuleHelper.processGeneralForwardBalance(balance, closedPs, unclosedPs);
                if (balanceForwardRuleHelper.getState().getGlobalSelectCount() % 1000 == 0) {
                    persistenceService.clearCache();
                }
            }
            catch (FatalErrorException fee) {
                LOG.info(fee.getMessage());
            }
        }

        // do the cumulative forwards
        Iterator<Balance> cumulativeBalances = balanceService.findCumulativeBalancesToForwardForFiscalYear(balanceForwardRuleHelper.getClosingFiscalYear());
        while (cumulativeBalances.hasNext()) {
            balance = cumulativeBalances.next();
            balanceForwardRuleHelper.processCumulativeForwardBalance(balance, closedPs, unclosedPs);
            if (balanceForwardRuleHelper.getState().getGlobalSelectCount() % 1000 == 0) {
                persistenceService.clearCache();
            }
        }

    }

    /**
     * This generates a PDF that summarizes the activity of a forward balance job that's just been run
     * 
     * @param balanceForwardsUnclosedPriorYearAccountGroup the origin entry group where balance forwarding origin entries with open
     *        accounts are stored
     * @param balanceForwardsClosedPriorYearAccountGroup the origin entry group where balance forwarding origin entries with closed
     *        accounts are stored
     * @param balanceForwardRuleHelper the BalanceForwardRuleHelper that held the state of the balance forward job to report on
     * @see org.kuali.kfs.gl.batch.service.YearEndService#generateForwardBalanceReports(org.kuali.kfs.gl.businessobject.OriginEntryGroup,
     *      org.kuali.kfs.gl.businessobject.OriginEntryGroup,
     *      org.kuali.kfs.gl.batch.BalanceForwardRuleHelper)
     */
    public void generateForwardBalanceReports(OriginEntryGroup balanceForwardsUnclosedPriorYearAccountGroup, OriginEntryGroup balanceForwardsClosedPriorYearAccountGroup, BalanceForwardRuleHelper balanceForwardRuleHelper) {
        // Assemble statistics.
        List statistics = new ArrayList();
        Summary summary = new Summary();
        summary.setSortOrder(1);
        summary.setDescription("NUMBER OF GLBL RECORDS READ....:");
        summary.setCount(balanceForwardRuleHelper.getState().getGlobalReadCount());
        statistics.add(summary);

        summary = new Summary();
        summary.setSortOrder(2);
        summary.setDescription("NUMBER OF GLBL RECORDS SELECTED:");
        summary.setCount(balanceForwardRuleHelper.getState().getGlobalSelectCount());
        statistics.add(summary);

        summary = new Summary();
        summary.setSortOrder(3);
        summary.setDescription("NUMBER OF SEQ RECORDS WRITTEN..:");
        summary.setCount(balanceForwardRuleHelper.getState().getSequenceWriteCount());
        statistics.add(summary);

        summary = new Summary();
        summary.setSortOrder(3);
        summary.setDescription("RECORDS FOR CLOSED ACCOUNTS....:");
        summary.setCount(balanceForwardRuleHelper.getState().getSequenceClosedCount());
        statistics.add(summary);

        Date runDate = new Date(dateTimeService.getCurrentDate().getTime());
        reportService.generateBalanceForwardStatisticsReport(statistics, runDate, balanceForwardsUnclosedPriorYearAccountGroup, balanceForwardsClosedPriorYearAccountGroup);
    }

    /**
     * Create origin entries to carry forward all open encumbrances from the closing fiscal year into the opening fiscal year.
     * 
     * @param originEntryGroup the origin entry group where generated origin entries should be saved
     * @param jobParameters the parameters necessary to run this job: the fiscal year to close and the university date the job was
     *        run
     * @param forwardEncumbrancesCounts the statistical counts generated by this job
     */
    public void forwardEncumbrances(String encumbranceForwardFileName, Map jobParameters, Map<String, Integer> counts) {
        LOG.debug("forwardEncumbrances() started");

        // counters for the report
        counts.put("encumbrancesRead", new Integer(0));
        counts.put("encumbrancesSelected", new Integer(0));
        counts.put("originEntriesWritten", new Integer(0));

        //create files
        File encumbranceForwardFile = new File(batchFileDirectoryName + File.separator + encumbranceForwardFileName);
        PrintStream encumbranceForwardPs = null;
        
        try {
            encumbranceForwardPs = new PrintStream(encumbranceForwardFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("nominalClosingFile Files doesn't exist " + encumbranceForwardFileName);
        }

        // encumbranceDao will return all encumbrances for the fiscal year sorted properly by all of the appropriate keys.
        Iterator encumbranceIterator = encumbranceDao.getEncumbrancesToClose((Integer) jobParameters.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR));
        while (encumbranceIterator.hasNext()) {

            Encumbrance encumbrance = (Encumbrance) encumbranceIterator.next();
            incrementCount(counts, "encumbrancesRead");
            
            // if the encumbrance is not completely relieved
            if (encumbranceClosingRuleHelper.anEntryShouldBeCreatedForThisEncumbrance(encumbrance)) {

                incrementCount(counts, "encumbrancesSelected");

                // build a pair of origin entries to carry forward the encumbrance.
                OriginEntryOffsetPair beginningBalanceEntryPair = EncumbranceClosingOriginEntryFactory.createBeginningBalanceEntryOffsetPair(encumbrance, (Integer) jobParameters.get(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR), (Date) jobParameters.get(GeneralLedgerConstants.ColumnNames.UNIV_DT));

                if (beginningBalanceEntryPair.isFatalErrorFlag()) {

                    continue;

                }
                else {

                    //beginningBalanceEntryPair.getEntry().setGroup(originEntryGroup);
                    //beginningBalanceEntryPair.getOffset().setGroup(originEntryGroup);

                    // save the entries.

                    originEntryService.createEntry(beginningBalanceEntryPair.getEntry(), encumbranceForwardPs);
                    originEntryService.createEntry(beginningBalanceEntryPair.getOffset(), encumbranceForwardPs);
                    incrementCount(counts, "originEntriesWritten");
                    incrementCount(counts, "originEntriesWritten");

                }

                // handle cost sharing if appropriate.

                boolean isEligibleForCostShare = false;

                try {

                    isEligibleForCostShare = encumbranceClosingRuleHelper.isEncumbranceEligibleForCostShare(beginningBalanceEntryPair.getEntry(), beginningBalanceEntryPair.getOffset(), encumbrance, beginningBalanceEntryPair.getEntry().getFinancialObjectTypeCode());

                }
                catch (FatalErrorException fee) {

                    LOG.info(fee.getMessage());

                }

                if (isEligibleForCostShare) {

                    // build and save an additional pair of origin entries to carry forward the encumbrance.

                    OriginEntryOffsetPair costShareBeginningBalanceEntryPair = EncumbranceClosingOriginEntryFactory.createCostShareBeginningBalanceEntryOffsetPair(encumbrance, (Date) jobParameters.get(GeneralLedgerConstants.ColumnNames.UNIV_DT));

                    if (!costShareBeginningBalanceEntryPair.isFatalErrorFlag()) {

                        //costShareBeginningBalanceEntryPair.getEntry().setGroup(originEntryGroup);
                        //costShareBeginningBalanceEntryPair.getOffset().setGroup(originEntryGroup);

                        // save the cost share entries.

                        originEntryService.createEntry(costShareBeginningBalanceEntryPair.getEntry(), encumbranceForwardPs);
                        originEntryService.createEntry(costShareBeginningBalanceEntryPair.getOffset(), encumbranceForwardPs);
                        incrementCount(counts, "originEntriesWritten");
                        incrementCount(counts, "originEntriesWritten");

                    }
                }
            }

            if (counts.get("encumbrancesSelected").intValue() % 1000 == 0) {
                persistenceService.clearCache();
            }
        }
        
        encumbranceForwardPs.close();
    }

    /**
     * This generates a PDF that summarizes the result of a forward encumbrance job that's just been run
     * 
     * @param originEntryGroup the origin entry group that encumbrance forwarding origin entries were saved in
     * @param jobParameters the parameters needed to run the job in the first place
     * @param forwardEncumbrancesCounts the statistical counts generated by the forward encumbrances job
     * @see org.kuali.kfs.gl.batch.service.YearEndService#generateForwardEncumbrancesReports(org.kuali.kfs.gl.businessobject.OriginEntryGroup,
     *      java.util.Map, java.util.Map)
     */
    public void generateForwardEncumbrancesReports(OriginEntryGroup originEntryGroup, Map jobParameters, Map<String, Integer> forwardEncumbrancesCounts) {
        // Assemble statistics.
        List statistics = new ArrayList();
        Summary summary = new Summary();
        summary.setSortOrder(1);
        summary.setDescription("NUMBER OF ENCUMBRANCE RECORDS READ");
        summary.setCount(forwardEncumbrancesCounts.get("encumbrancesRead"));
        statistics.add(summary);

        summary = new Summary();
        summary.setSortOrder(2);
        summary.setDescription("NUMBER OF ENCUMBRANCE RECORDS SELECTED");
        summary.setCount(forwardEncumbrancesCounts.get("encumbrancesSelected"));
        statistics.add(summary);

        summary = new Summary();
        summary.setSortOrder(3);
        summary.setDescription("NUMBER OF SEQ RECORDS WRITTEN");
        summary.setCount(forwardEncumbrancesCounts.get("originEntriesWritten"));
        statistics.add(summary);

        Date runDate = new Date(dateTimeService.getCurrentDate().getTime());
        reportService.generateEncumbranceClosingStatisticsReport(jobParameters, statistics, runDate, originEntryGroup);
    }

    /**
     * @param balanceFiscalYear the fiscal year to find balances encumbrances for
     * @see org.kuali.kfs.gl.batch.service.YearEndService#logAllMissingPriorYearAccounts(java.lang.Integer)
     */
    public void logAllMissingPriorYearAccounts(Integer fiscalYear) {
        Set<Map<String, String>> missingPriorYearAccountKeys = yearEndDao.findKeysOfMissingPriorYearAccountsForBalances(fiscalYear);
        missingPriorYearAccountKeys.addAll(yearEndDao.findKeysOfMissingPriorYearAccountsForOpenEncumbrances(fiscalYear));
        for (Map<String, String> key : missingPriorYearAccountKeys) {
            LOG.info("PRIOR YEAR ACCOUNT MISSING FOR " + key.get("chartOfAccountsCode") + "-" + key.get("accountNumber"));
        }
    }

    /**
     * @param balanceFiscalYear the fiscal year to find balances encumbrances for
     * @see org.kuali.kfs.gl.batch.service.YearEndService#logAllMissingSubFundGroups(java.lang.Integer)
     */
    public void logAllMissingSubFundGroups(Integer fiscalYear) {
        Set missingSubFundGroupKeys = yearEndDao.findKeysOfMissingSubFundGroupsForBalances(fiscalYear);
        missingSubFundGroupKeys.addAll(yearEndDao.findKeysOfMissingSubFundGroupsForOpenEncumbrances(fiscalYear));
        for (Object key : missingSubFundGroupKeys) {
            LOG.info("SUB FUND GROUP MISSING FOR " + (String) ((Map) key).get("subFundGroupCode"));
        }

    }

    /**
     * Sets the encumbranceDao attribute, allowing the injection of an implementation of the data access object that uses a specific
     * O/R mechanism.
     * 
     * @param encumbranceDao the implementation of encumbranceDao to set
     * @see org.kuali.kfs.gl.dataaccess.EncumbranceDao
     */
    public void setEncumbranceDao(EncumbranceDao encumbranceDao) {
        this.encumbranceDao = encumbranceDao;
    }

    /**
     * Sets the originEntryService attribute, allowing the injection of an implementation of that service
     * 
     * @param originEntryService the implementation of originEntryService to set
     * @see org.kuali.kfs.gl.service.OriginEntryService
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    /**
     * Sets the reportService attribute, allowing the injection of an implementation of that service
     * 
     * @param originEntryService the implementation of reportService to set
     * @see org.kuali.kfs.gl.service.ReportService
     */
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setEncumbranceClosingRuleHelper(EncumbranceClosingRuleHelper encumbranceClosingRuleHelper) {
        this.encumbranceClosingRuleHelper = encumbranceClosingRuleHelper;
    }

    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public void setBalanceTypeService(BalanceTypService balanceTypeService) {
        this.balanceTypeService = balanceTypeService;
    }

    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

    public void setSubFundGroupService(SubFundGroupService subFundGroupService) {
        this.subFundGroupService = subFundGroupService;
    }

    /**
     * Sets the persistenceService attribute value.
     * 
     * @param persistenceService The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setYearEndDao(YearEndDao yearEndDao) {
        this.yearEndDao = yearEndDao;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
