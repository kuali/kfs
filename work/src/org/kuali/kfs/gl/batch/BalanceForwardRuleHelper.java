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
package org.kuali.module.gl.batch.closing.year.service.impl.helper;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.financial.exceptions.InvalidFlexibleOffsetException;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.FatalErrorException;
import org.kuali.module.gl.util.NonFatalErrorException;
import org.kuali.module.gl.util.ObjectHelper;

/**
 * A class to hold significant state for a balance forward job; it also has the methods that actually accomplish the job
 */
public class BalanceForwardRuleHelper {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardRuleHelper.class);
    private FlexibleOffsetAccountService flexibleOffsetAccountService;

    /**
     * A container for the state of the balance forward process. The way state is handled is heavily dependent upon the way in which
     * YearEndServiceImpl.forwardBalancesForFiscalYear works.
     */
    public static class BalanceForwardProcessState {
        private int globalReadCount;
        private int globalSelectCount;
        private int sequenceNumber;
        private int sequenceClosedCount;
        private int sequenceWriteCount;
        private String accountNumberHold;
        private int nonFatalCount;

        public String getAccountNumberHold() {
            return accountNumberHold;
        }

        public void setAccountNumberHold(String accountNumberHold) {
            this.accountNumberHold = accountNumberHold;
        }

        public void incrementGlobalReadCount() {
            globalReadCount++;
        }

        public void incrementGlobalSelectCount() {
            globalSelectCount++;
        }

        public void incrementSequenceNumber() {
            sequenceNumber++;
        }

        public void incrementSequenceClosedCount() {
            sequenceClosedCount++;
        }

        public void incrementSequenceWriteCount() {
            sequenceWriteCount++;
        }

        public void incrementNonFatalCount() {
            nonFatalCount += 1;
        }

        public int getGlobalReadCount() {
            return globalReadCount;
        }

        public void setGlobalReadCount(int globalReadCount) {
            this.globalReadCount = globalReadCount;
        }

        public int getGlobalSelectCount() {
            return globalSelectCount;
        }

        public void setGlobalSelectCount(int globalSelectCount) {
            this.globalSelectCount = globalSelectCount;
        }

        public int getSequenceClosedCount() {
            return sequenceClosedCount;
        }

        public int getNonFatalCount() {
            return nonFatalCount;
        }

        public void setSequenceClosedCount(int sequenceClosedCount) {
            this.sequenceClosedCount = sequenceClosedCount;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public int getSequenceWriteCount() {
            return sequenceWriteCount;
        }

        public void setSequenceWriteCount(int sequenceWriteCount) {
            this.sequenceWriteCount = sequenceWriteCount;
        }

        public void setNonFatalCount(int nonFatalCount) {
            this.nonFatalCount = nonFatalCount;
        }
    }

    private Integer closingFiscalYear;
    private Date transactionDate;

    private OriginEntryGroup closedPriorYearAccountGroup;
    private OriginEntryGroup unclosedPriorYearAccountGroup;

    private PriorYearAccountService priorYearAccountService;
    private SubFundGroupService subFundGroupService;
    private OriginEntryService originEntryService;
    private ParameterService parameterService;
    private Options currentYearOptions;
    private String[] priorYearAccountObjectTypes;
    private String[] generalSwObjectTypes;
    private String annualClosingDocType;
    private String glOriginationCode;
    private Map<String, Boolean> balanceTypeEncumbranceIndicators;

    private BalanceForwardProcessState state;

    /**
     * Constructs a BalanceForwardRuleHelper
     */
    public BalanceForwardRuleHelper() {
        super();
        state = new BalanceForwardProcessState();
        flexibleOffsetAccountService = SpringContext.getBean(FlexibleOffsetAccountService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
        annualClosingDocType = parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE);
        glOriginationCode = parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE);

    }

    /**
     * Constructs a BalanceForwardRuleHelper, using a fiscal year. This also initializes object type arrays based on the options of
     * the closing fiscal year
     * 
     * @param closingFiscalYear the fiscal year that is closing out
     */
    public BalanceForwardRuleHelper(Integer closingFiscalYear) {
        this();
        setClosingFiscalYear(closingFiscalYear);

        Options jobYearRunOptions = SpringContext.getBean(OptionsService.class).getOptions(closingFiscalYear);

        generalSwObjectTypes = new String[3];
        generalSwObjectTypes[0] = jobYearRunOptions.getFinancialObjectTypeAssetsCd();
        generalSwObjectTypes[1] = jobYearRunOptions.getFinObjectTypeLiabilitiesCode();
        generalSwObjectTypes[2] = jobYearRunOptions.getFinObjectTypeFundBalanceCd();

        // "EE", "ES", "EX", "IC", "TE", "TI", "IN", "CH"
        priorYearAccountObjectTypes = new String[8];
        priorYearAccountObjectTypes[0] = jobYearRunOptions.getFinObjTypeExpendNotExpCode();
        priorYearAccountObjectTypes[1] = jobYearRunOptions.getFinObjTypeExpNotExpendCode();
        priorYearAccountObjectTypes[2] = jobYearRunOptions.getFinObjTypeExpenditureexpCd();
        priorYearAccountObjectTypes[3] = jobYearRunOptions.getFinObjTypeIncomeNotCashCd();
        priorYearAccountObjectTypes[4] = jobYearRunOptions.getFinancialObjectTypeTransferExpenseCd();
        priorYearAccountObjectTypes[5] = jobYearRunOptions.getFinancialObjectTypeTransferIncomeCd();
        priorYearAccountObjectTypes[6] = jobYearRunOptions.getFinObjectTypeIncomecashCode();
        priorYearAccountObjectTypes[7] = jobYearRunOptions.getFinObjTypeCshNotIncomeCd();
    }

    /**
     * Constructs a BalanceForwardRuleHelper, but this one goes whole hog: initializes all of the relevant parameters and the
     * balance types to process
     * 
     * @param closingFiscalYear the fiscal year to close
     * @param transactionDate the date this job is being run
     * @param closedPriorYearAccountGroup the group to put balance forwarding origin entries with closed accounts into
     * @param unclosedPriorYearAccountGroup the group to put balance forwarding origin entries with open accounts into
     */
    public BalanceForwardRuleHelper(Integer closingFiscalYear, Date transactionDate, OriginEntryGroup closedPriorYearAccountGroup, OriginEntryGroup unclosedPriorYearAccountGroup) {
        this(closingFiscalYear);
        setTransactionDate(transactionDate);
        setClosingFiscalYear(closingFiscalYear);
        setClosedPriorYearAccountGroup(closedPriorYearAccountGroup);
        setUnclosedPriorYearAccountGroup(unclosedPriorYearAccountGroup);
        currentYearOptions = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();

        balanceTypeEncumbranceIndicators = new HashMap<String, Boolean>();
        for (Object balanceTypAsObj : SpringContext.getBean(BalanceTypService.class).getAllBalanceTyps()) {
            BalanceTyp balanceType = (BalanceTyp) balanceTypAsObj;
            balanceTypeEncumbranceIndicators.put(balanceType.getCode(), (balanceType.isFinBalanceTypeEncumIndicator() ? Boolean.TRUE : Boolean.FALSE));
        }
    }


    /**
     * The balance to create a general balance forward origin entry for
     * 
     * @param balance a balance to create an origin entry for
     * @param closedPriorYearAccountGroup the group to put balance forwarding origin entries with closed accounts into
     * @param unclosedPriorYearAccountGroup the group to put balance forwarding origin entries with open accounts into
     * @throws FatalErrorException
     */
    public void processGeneralForwardBalance(Balance balance, OriginEntryGroup closedPriorYearAccountGroup, OriginEntryGroup unclosedPriorYearAccountGroup) throws FatalErrorException {
        if (ObjectUtils.isNull(balance.getPriorYearAccount())) {
            throw new FatalErrorException("COULD NOT RETRIEVE INFORMATION ON ACCOUNT " + balance.getChartOfAccountsCode() + "-" + balance.getAccountNumber());
        }

        if ((null == balance.getAccountNumber() && null == state.getAccountNumberHold()) || (null != balance.getAccountNumber() && balance.getAccountNumber().equals(state.getAccountNumberHold()))) {

            // 954 004770 ADD 1 TO WS-SEQ-NBR

            state.incrementSequenceNumber();

            // 955 004780 ELSE

        }
        else {

            // 956 004790 MOVE 1 TO WS-SEQ-NBR.

            state.setSequenceNumber(1);

        }

        state.incrementGlobalSelectCount();

        OriginEntryFull entry = generateGeneralForwardOriginEntry(balance);
        saveForwardingEntry(balance, entry, closedPriorYearAccountGroup, unclosedPriorYearAccountGroup);
    }

    /**
     * This method creates an origin entry for a cumulative balance forward and saves it in its proper origin entry group
     * 
     * @param balance a balance which needs to have a cumulative origin entry generated for it
     * @param closedPriorYearAccountGroup the origin entry group where forwarding origin entries with closed prior year accounts go
     * @param unclosedPriorYearAcocuntGroup the origin entry group where forwarding origin entries with open prior year accounts go
     */
    public void processCumulativeForwardBalance(Balance balance, OriginEntryGroup closedPriorYearAccountGroup, OriginEntryGroup unclosedPriorYearAccountGroup) {
        if ((null == balance.getAccountNumber() && null == state.getAccountNumberHold()) || (null != balance.getAccountNumber() && balance.getAccountNumber().equals(state.getAccountNumberHold()))) {

            // 954 004770 ADD 1 TO WS-SEQ-NBR

            state.incrementSequenceNumber();

            // 955 004780 ELSE

        }
        else {

            // 956 004790 MOVE 1 TO WS-SEQ-NBR.

            state.setSequenceNumber(1);

        }

        state.incrementGlobalSelectCount();

        OriginEntryFull activeEntry = generateCumulativeForwardOriginEntry(balance);
        saveForwardingEntry(balance, activeEntry, closedPriorYearAccountGroup, unclosedPriorYearAccountGroup);
    }

    /**
     * This method generates an origin entry for a given cumulative balance forward balance
     * 
     * @param balance a balance to foward, cumulative style
     * @return an OriginEntryFull to forward the given balance
     */
    public OriginEntryFull generateCumulativeForwardOriginEntry(Balance balance) {
        // 1220 007470 PERFORM 4200-WRITE-ACTIVE
        // 1221 007480 THRU 4200-WRITE-ACTIVE-EXIT

        // 1332 008540 4200-WRITE-ACTIVE.
        // 1333 008550 MOVE SPACES TO GLEN-RECORD.

        OriginEntryFull activeEntry = new OriginEntryFull();

        // 1334 008560 MOVE WS-UNIV-FISCAL-YR-PLUS-1
        // 1335 008570 TO UNIV-FISCAL-YR OF GLEN-RECORD.

        activeEntry.setUniversityFiscalYear(new Integer(closingFiscalYear.intValue() + 1));

        // 1336 008580 MOVE GLGLBL-FIN-COA-CD
        // 1337 008590 TO FIN-COA-CD OF GLEN-RECORD.

        activeEntry.setChartOfAccountsCode(balance.getChartOfAccountsCode());

        // 1338 008600 MOVE GLGLBL-ACCOUNT-NBR
        // 1339 008610 TO ACCOUNT-NBR OF GLEN-RECORD.

        activeEntry.setAccountNumber(balance.getAccountNumber());

        // 1340 008620 MOVE GLGLBL-SUB-ACCT-NBR
        // 1341 008630 TO SUB-ACCT-NBR OF GLEN-RECORD.

        activeEntry.setSubAccountNumber(balance.getSubAccountNumber());

        // 1342 008640 MOVE GLGLBL-FIN-OBJECT-CD
        // 1343 008650 TO FIN-OBJECT-CD OF GLEN-RECORD.

        activeEntry.setFinancialObjectCode(balance.getObjectCode());

        // 1344 008660 MOVE GLGLBL-FIN-SUB-OBJ-CD
        // 1345 008670 TO FIN-SUB-OBJ-CD OF GLEN-RECORD.

        activeEntry.setFinancialSubObjectCode(balance.getSubObjectCode());

        // 1346 008680 MOVE GLGLBL-FIN-BALANCE-TYP-CD
        // 1347 008690 TO FIN-BALANCE-TYP-CD OF GLEN-RECORD.

        activeEntry.setFinancialBalanceTypeCode(balance.getBalanceTypeCode());

        // 1348 008700 MOVE GLGLBL-FIN-OBJ-TYP-CD
        // 1349 008710 TO FIN-OBJ-TYP-CD OF GLEN-RECORD.

        activeEntry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

        try {
            flexibleOffsetAccountService.updateOffset(activeEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            LOG.debug("processBalance() Balance Forward Flexible Offset Error: " + e.getMessage());
        }

        // 1350 008720 MOVE 'CB'
        // 1351 008730 TO UNIV-FISCAL-PRD-CD OF GLEN-RECORD.

        activeEntry.setUniversityFiscalPeriodCode(KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE);

        // 1352 008740 MOVE 'ACLO'
        // 1353 008750 TO FDOC-TYP-CD OF GLEN-RECORD.

        activeEntry.setFinancialDocumentTypeCode(this.annualClosingDocType);

        // 1354 008760 MOVE 'MF'
        // 1355 008770 TO FS-ORIGIN-CD OF GLEN-RECORD.

        activeEntry.setFinancialSystemOriginationCode(this.glOriginationCode);

        // 1356 008780 STRING 'AC'
        // 1357 008790 GLGLBL-ACCOUNT-NBR
        // 1358 008800 RP-BLANK-LINE
        // 1359 008810 DELIMITED BY SIZE
        // 1360 008820 INTO FDOC-NBR OF GLEN-RECORD.

        activeEntry.setDocumentNumber(new StringBuffer(KFSConstants.BALANCE_TYPE_ACTUAL).append(balance.getAccountNumber()).toString());

        // 1361 008830 MOVE WS-SEQ-NBR
        // 1362 008840 TO TRN-ENTR-SEQ-NBR OF GLEN-RECORD.

        activeEntry.setTransactionLedgerEntrySequenceNumber(new Integer(state.getSequenceNumber()));

        // 1363 008850 STRING 'BEG C & G BAL BROUGHT FORWARD FROM '
        // 1364 008860 VAR-UNIV-FISCAL-YR
        // 1365 008870 RP-BLANK-LINE
        // 1366 008880 DELIMITED BY SIZE INTO
        // 1367 008890 TRN-LDGR-ENTR-DESC OF GLEN-RECORD.

        activeEntry.setTransactionLedgerEntryDescription(new StringBuffer("BEG C & G BAL BROUGHT FORWARD FROM ").append(closingFiscalYear).toString());

        // 1368 008900 ADD GLGLBL-ACLN-ANNL-BAL-AMT
        // 1369 008910 GLGLBL-CONTR-GR-BB-AC-AMT
        // 1370 008920 GIVING TRN-LDGR-ENTR-AMT OF GLEN-RECORD.

        activeEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount().add(balance.getContractsGrantsBeginningBalanceAmount()));

        // 1371 008930 IF GLGLBL-FIN-BALANCE-TYP-CD = 'CB'

        if (KFSConstants.BALANCE_TYPE_CURRENT_BUDGET.equals(balance.getBalanceTypeCode())) {

            // 1372 008940 MOVE SPACES
            // 1373 008950 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

            activeEntry.setTransactionDebitCreditCode(null);

            // 1374 008960 ELSE

        }
        else {

            String wsFinancialObjectTypeDebitCreditCode = null;

            try {
                wsFinancialObjectTypeDebitCreditCode = getFinancialObjectTypeDebitCreditCode(balance);
            }
            catch (NonFatalErrorException nfee) {

                getState().incrementNonFatalCount();

                // 1197 007240 PERFORM 8500-CHECK-NEW-PAGE
                // 1198 007250 THRU 8500-CHECK-NEW-PAGE-EXIT
                // 1199 007260 MOVE 'C' TO WS-FIN-OBJTYP-DBCR-CD

                wsFinancialObjectTypeDebitCreditCode = KFSConstants.GL_CREDIT_CODE;

                LOG.info(nfee.getMessage());
            }

            // 1375 008970 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < ZERO

            if (activeEntry.getTransactionLedgerEntryAmount().isNegative()) {

                // 1376 008980 IF WS-FIN-OBJTYP-DBCR-CD = 'C'

                if (KFSConstants.GL_CREDIT_CODE.equals(wsFinancialObjectTypeDebitCreditCode)) {

                    // 1377 008990 MOVE 'D'
                    // 1378 009000 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                    activeEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

                    // 1379 009010 ELSE

                }
                else {

                    // 1380 009020 MOVE 'C'
                    // 1381 009030 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                    activeEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

                }

                // 1382 009040 ELSE

            }
            else {

                // 1383 009050 MOVE WS-FIN-OBJTYP-DBCR-CD
                // 1384 009060 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD.

                activeEntry.setTransactionDebitCreditCode(wsFinancialObjectTypeDebitCreditCode);

            }

        }

        // 1385 009070 MOVE VAR-TRANSACTION-DT
        // 1386 009080 TO TRANSACTION-DT OF GLEN-RECORD.

        activeEntry.setTransactionDate(transactionDate);

        // 1387 009090 MOVE SPACES
        // 1388 009100 TO ORG-DOC-NBR OF GLEN-RECORD.

        activeEntry.setOrganizationDocumentNumber(null);

        // 1389 009110 MOVE ALL '-'
        // 1390 009120 TO PROJECT-CD OF GLEN-RECORD.

        activeEntry.setProjectCode(KFSConstants.getDashProjectCode());

        // 1391 009130 MOVE SPACES
        // 1392 009140 TO ORG-REFERENCE-ID OF GLEN-RECORD.

        activeEntry.setOrganizationReferenceId(null);

        // 1393 009150 MOVE SPACES
        // 1394 009160 TO FDOC-REF-TYP-CD OF GLEN-RECORD.

        activeEntry.setReferenceFinancialDocumentNumber(null);

        // 1395 009170 MOVE SPACES
        // 1396 009180 TO FS-REF-ORIGIN-CD OF GLEN-RECORD.

        activeEntry.setReferenceFinancialSystemOriginationCode(null);

        // 1397 009190 MOVE SPACES
        // 1398 009200 TO FDOC-REF-NBR OF GLEN-RECORD.

        activeEntry.setReferenceFinancialDocumentNumber(null);

        // 1399 009210 MOVE SPACES
        // 1400 009220 TO FDOC-REVERSAL-DT OF GLEN-RECORD.

        activeEntry.setReversalDate(null);

        // 1401 009230 MOVE WS-TRN-ENCUM-UPDT-CD
        // 1402 009240 TO TRN-ENCUM-UPDT-CD OF GLEN-RECORD.

        String transactionEncumbranceUpdateCode = null;
        try {
            transactionEncumbranceUpdateCode = getTransactionEncumbranceUpdateCode(balance);
        }
        catch (NonFatalErrorException nfee) {

            // 1161 006880 ADD +1 TO NON-FATAL-COUNT

            getState().incrementNonFatalCount();

            LOG.info(nfee.getMessage());
        }

        activeEntry.setTransactionEncumbranceUpdateCode(transactionEncumbranceUpdateCode);

        // 1403 009250 IF FIN-BALANCE-TYP-CD OF GLEN-RECORD = 'NB'

        // NOTE (laran) This might be a bug. I'm not sure if
        // "FIN-BALANCE-TYP-CD OF GLEN-RECORD" refers to the
        // value previously set into the GLEN-RECORD, which
        // would be the variable called "entry" at this point.
        // Or perhaps it refers to a value set previously on the
        // variable called "activeEntry" at this point.

        if (KFSConstants.BALANCE_TYPE_AUDIT_TRAIL.equals(balance.getBalanceTypeCode())) {

            // 1404 009260 MOVE 'AC' TO FIN-BALANCE-TYP-CD OF GLEN-RECORD.

            activeEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);

        }

        // 1405 009270 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < 0

        if (activeEntry.getTransactionLedgerEntryAmount().isNegative()) {

            // 1406 009280 IF FIN-BALANCE-TYP-CD OF GLEN-RECORD = 'AC'

            if (KFSConstants.BALANCE_TYPE_ACTUAL.equals(activeEntry.getFinancialBalanceTypeCode())) {

                // 1407 009290 COMPUTE TRN-LDGR-ENTR-AMT OF GLEN-RECORD
                // 1408 009300 = TRN-LDGR-ENTR-AMT OF GLEN-RECORD * -1.

                activeEntry.setTransactionLedgerEntryAmount(activeEntry.getTransactionLedgerEntryAmount().negated());

            }

        }

        // 1409 009310 IF CAPYACTT-ACCT-CLOSED-IND NOT = 'Y'

        return activeEntry;
    }

    /**
     * Creates an origin entry that will forward this "general" balance
     * 
     * @param balance the balance to create a general origin entry for
     * @return the generated origin entry
     */
    public OriginEntryFull generateGeneralForwardOriginEntry(Balance balance) {

        OriginEntryFull entry = new OriginEntryFull();

        // 1215 007420 PERFORM 4100-WRITE-GENERAL
        // 1216 007430 THRU 4100-WRITE-GENERAL-EXIT

        // 1225 007520 4100-WRITE-GENERAL.
        // 1226 007530 MOVE SPACES TO GLEN-RECORD.
        // 1227 007540 MOVE WS-UNIV-FISCAL-YR-PLUS-1
        // 1228 007550 TO UNIV-FISCAL-YR OF GLEN-RECORD.

        entry.setUniversityFiscalYear(new Integer(closingFiscalYear.intValue() + 1));

        // 1229 007560 MOVE GLGLBL-FIN-COA-CD
        // 1230 007570 TO FIN-COA-CD.

        entry.setChartOfAccountsCode(balance.getChartOfAccountsCode());

        // 1231 007580 MOVE GLGLBL-ACCOUNT-NBR
        // 1232 007590 TO ACCOUNT-NBR.

        entry.setAccountNumber(balance.getAccountNumber());

        // 1233 007600 MOVE GLGLBL-SUB-ACCT-NBR
        // 1234 007610 TO SUB-ACCT-NBR.

        entry.setSubAccountNumber(balance.getSubAccountNumber());

        // 1235 007620 MOVE GLGLBL-FIN-OBJECT-CD
        // 1236 007630 TO FIN-OBJECT-CD.

        entry.setFinancialObjectCode(balance.getObjectCode());

        // 1237 007640 MOVE GLGLBL-FIN-SUB-OBJ-CD
        // 1238 007650 TO FIN-SUB-OBJ-CD.

        entry.setFinancialSubObjectCode(balance.getSubObjectCode());

        // 1239 007660 MOVE GLGLBL-FIN-BALANCE-TYP-CD
        // 1240 007670 TO FIN-BALANCE-TYP-CD.

        entry.setFinancialBalanceTypeCode(balance.getBalanceTypeCode());

        // 1241 007680 IF GLGLBL-FIN-OBJ-TYP-CD = 'EE'

        if (currentYearOptions.getFinObjTypeExpendNotExpCode().equals(balance.getObjectTypeCode())) {

            // 1242 007690 MOVE 'AS'
            // 1243 007700 TO FIN-OBJ-TYP-CD

            entry.setFinancialObjectTypeCode(currentYearOptions.getFinancialObjectTypeAssetsCd());

            // 1244 007710 ELSE

        }
        else {

            // 1245 007720 MOVE GLGLBL-FIN-OBJ-TYP-CD
            // 1246 007730 TO FIN-OBJ-TYP-CD.

            entry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

        }

        // 1247 007740 MOVE 'BB'
        // 1248 007750 TO UNIV-FISCAL-PRD-CD.

        entry.setUniversityFiscalPeriodCode(KFSConstants.PERIOD_CODE_BEGINNING_BALANCE);

        // 1249 007760 MOVE 'ACLO'
        // 1250 007770 TO FDOC-TYP-CD.

        entry.setFinancialDocumentTypeCode(this.annualClosingDocType);

        // 1251 007780 MOVE 'MF'
        // 1252 007790 TO FS-ORIGIN-CD.

        entry.setFinancialSystemOriginationCode(this.glOriginationCode);

        // 1253 007800 STRING 'AC'
        // 1254 007810 GLGLBL-ACCOUNT-NBR
        // 1255 007820 RP-BLANK-LINE
        // 1256 007830 DELIMITED BY SIZE
        // 1257 007840 INTO FDOC-NBR.

        // FIXME Once tests are running properly uncomment the code to include the
        // chartOfAccountsCode in the document number. It will cause the tests to
        // break given the current framework but is desired as an enhancement for Kuali.
        entry.setDocumentNumber(new StringBuffer(KFSConstants.BALANCE_TYPE_ACTUAL).append(balance.getAccountNumber())/* .append(balance.getChartOfAccountsCode()) */.toString());

        // 1258 007850 MOVE WS-SEQ-NBR
        // 1259 007860 TO TRN-ENTR-SEQ-NBR.

        entry.setTransactionLedgerEntrySequenceNumber(new Integer(state.getSequenceNumber()));

        // 1260 007870 STRING 'BEG BAL BROUGHT FORWARD FROM '
        // 1261 007880 VAR-UNIV-FISCAL-YR
        // 1262 007890 RP-BLANK-LINE
        // 1263 007900 DELIMITED BY SIZE INTO
        // 1264 007910 TRN-LDGR-ENTR-DESC.

        entry.setTransactionLedgerEntryDescription(new StringBuffer("BEG BAL BROUGHT FORWARD FROM ").append(closingFiscalYear).toString());

        String transactionEncumbranceUpdateCode = null;
        try {
            transactionEncumbranceUpdateCode = getTransactionEncumbranceUpdateCode(balance);
        }
        catch (NonFatalErrorException nfee) {

            // 1161 006880 ADD +1 TO NON-FATAL-COUNT

            getState().incrementNonFatalCount();

            LOG.info(nfee.getMessage());
        }
        entry.setTransactionEncumbranceUpdateCode(transactionEncumbranceUpdateCode);

        // 1265 007920 ADD GLGLBL-ACLN-ANNL-BAL-AMT
        // 1266 007930 GLGLBL-FIN-BEG-BAL-LN-AMT
        // 1267 007940 GLGLBL-CONTR-GR-BB-AC-AMT
        // 1268 007950 GIVING TRN-LDGR-ENTR-AMT.

        KualiDecimal transactionLedgerEntryAmount = KualiDecimal.ZERO;
        transactionLedgerEntryAmount = transactionLedgerEntryAmount.add(balance.getAccountLineAnnualBalanceAmount()).add(balance.getBeginningBalanceLineAmount()).add(balance.getContractsGrantsBeginningBalanceAmount());

        String wsFinancialObjectTypeDebitCreditCode = null;

        try {
            wsFinancialObjectTypeDebitCreditCode = getFinancialObjectTypeDebitCreditCode(balance);
        }
        catch (NonFatalErrorException nfee) {

            getState().incrementNonFatalCount();

            // 1197 007240 PERFORM 8500-CHECK-NEW-PAGE
            // 1198 007250 THRU 8500-CHECK-NEW-PAGE-EXIT
            // 1199 007260 MOVE 'C' TO WS-FIN-OBJTYP-DBCR-CD

            wsFinancialObjectTypeDebitCreditCode = KFSConstants.GL_CREDIT_CODE;

            LOG.info(nfee.getMessage());
        }

        // 1269 007960 IF TRN-LDGR-ENTR-AMT < ZERO

        if (transactionLedgerEntryAmount.isNegative()) {

            // 1270 007970 IF WS-FIN-OBJTYP-DBCR-CD = 'D'

            if (KFSConstants.GL_DEBIT_CODE.equals(wsFinancialObjectTypeDebitCreditCode)) {

                // 1271 007980 MOVE 'C' TO TRN-DEBIT-CRDT-CD

                entry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

                // 1272 007990 ELSE

            }
            else {

                // 1273 008000 MOVE 'D' TO TRN-DEBIT-CRDT-CD

                entry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

            }

            // 1274 008010 ELSE

        }
        else {

            // 1275 008020 MOVE WS-FIN-OBJTYP-DBCR-CD
            // 1276 008030 TO TRN-DEBIT-CRDT-CD.

            entry.setTransactionDebitCreditCode(wsFinancialObjectTypeDebitCreditCode);

        }

        // 1277 008040 MOVE VAR-TRANSACTION-DT
        // 1278 008050 TO TRANSACTION-DT OF GLEN-RECORD.

        entry.setTransactionDate(transactionDate);

        // 1279 008060 MOVE SPACES
        // 1280 008070 TO ORG-DOC-NBR.

        entry.setOrganizationDocumentNumber(null);

        // 1281 008080 MOVE ALL '-'
        // 1282 008090 TO PROJECT-CD.

        entry.setProjectCode(KFSConstants.getDashProjectCode());

        // 1283 008100 MOVE SPACES
        // 1284 008110 TO ORG-REFERENCE-ID.

        entry.setOrganizationReferenceId(null);

        // 1285 008120 MOVE SPACES
        // 1286 008130 TO FDOC-REF-TYP-CD.

        entry.setReferenceFinancialDocumentTypeCode(null);

        // 1287 008140 MOVE SPACES
        // 1288 008150 TO FS-REF-ORIGIN-CD.

        entry.setReferenceFinancialSystemOriginationCode(null);

        // 1289 008160 MOVE SPACES
        // 1290 008170 TO FDOC-REF-NBR.

        entry.setReferenceFinancialDocumentNumber(null);

        // 1291 008180 MOVE SPACES
        // 1292 008190 TO FDOC-REVERSAL-DT.

        entry.setFinancialDocumentReversalDate(null);

        // 1293 008200 MOVE WS-TRN-ENCUM-UPDT-CD
        // 1294 008210 TO TRN-ENCUM-UPDT-CD.

        // NOTE (laran) this was set above.

        // 1295 008220 IF FIN-BALANCE-TYP-CD = 'NB'

        if (KFSConstants.BALANCE_TYPE_AUDIT_TRAIL.equals(entry.getFinancialBalanceTypeCode())) {

            // 1296 008230 MOVE 'AC' TO FIN-BALANCE-TYP-CD.

            entry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);

        }

        // 1297 008240 IF TRN-LDGR-ENTR-AMT < 0

        if (transactionLedgerEntryAmount.isNegative()) {

            // 1298 008250 IF FIN-BALANCE-TYP-CD = 'AC'

            if (KFSConstants.BALANCE_TYPE_ACTUAL.equals(entry.getFinancialBalanceTypeCode())) {

                // 1299 008260 COMPUTE TRN-LDGR-ENTR-AMT
                // 1300 008270 = TRN-LDGR-ENTR-AMT * -1.

                transactionLedgerEntryAmount = transactionLedgerEntryAmount.negated();

            }

        }

        entry.setTransactionLedgerEntryAmount(transactionLedgerEntryAmount);

        // 1301 008280 IF CAPYACTT-ACCT-CLOSED-IND NOT = 'Y'

        return entry;
    }

    /**
     * Retrieves the transaction encumbrance update code, based on the balance type code of the balance. These codes are cached,
     * based off a cache generated in the big constructor
     * 
     * @param balance the balance to find the encumbrance update code for
     * @return the transaction update code
     * @throws NonFatalErrorException if an encumbrance update code cannot be found for this balance
     */
    private String getTransactionEncumbranceUpdateCode(Balance balance) throws NonFatalErrorException {
        String updateCode = null;

        // 1142 006690 IF CABTYP-FBEC-I < ZERO
        // 1143 006700 MOVE SPACE TO CABTYP-FIN-BALTYP-ENC-CD
        // 1144 006710 END-IF

        // 1145 006720 EVALUATE SQLCODE
        // 1146 006730 WHEN 0
        Boolean encumIndicator = this.balanceTypeEncumbranceIndicators.get(balance.getBalanceTypeCode());
        if (encumIndicator == null) {
            throw new NonFatalErrorException(new StringBuffer(" ERROR ").append(balance.getBalanceTypeCode()).append(" NOT ON TABLE ").toString());
        }
        else if (encumIndicator.booleanValue()) {
            updateCode = KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD;
        }

        return updateCode;
    }

    /**
     * This method attempts to determine the debit/credit code of a given balance based on the object type
     * 
     * @param balance the balance to determin the debit/credit code for
     * @return the debit or credit code
     */
    private String getFinancialObjectTypeDebitCreditCode(Balance balance) throws NonFatalErrorException {
        String balanceObjectTypeDebitCreditCode = null != balance.getObjectType() ? balance.getObjectType().getFinObjectTypeDebitcreditCd() : null;

        String wsFinancialObjectTypeDebitCreditCode = null;

        // 1182 007090 IF CAOTYP-FODC-I < ZERO
        // 1183 007100 MOVE SPACE TO CAOTYP-FIN-OBJTYP-DBCR-CD
        // 1184 007110 END-IF
        // 1185 007120 EVALUATE SQLCODE
        // 1186 007130 WHEN 0

        if (null != balanceObjectTypeDebitCreditCode) {

            // 1187 007140 IF CAOTYP-FIN-OBJTYP-DBCR-CD = 'C' OR 'D'

            // NOTE this field doesn't seem to be used anywhere in the cobol
            // String subFundGroupCode = null;

            if (ObjectHelper.isOneOf(balanceObjectTypeDebitCreditCode, new String[] { KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE })) {

                // 1188 007150 MOVE CAOTYP-FIN-OBJTYP-DBCR-CD
                // 1189 007160 TO WS-FIN-OBJTYP-DBCR-CD

                wsFinancialObjectTypeDebitCreditCode = balanceObjectTypeDebitCreditCode;

                // 1190 007170 MOVE CAPYACTT-SUB-FUND-GRP-CD TO WS-SUB-FUND-GRP-CD

                // NOTE this field doesn't seem to be used anywhere in the cobol
                // subFundGroupCode = priorYearAccount.getSubFundGroupCode();

                // 1191 007180 ELSE

            }
            else {

                // 1192 007190 MOVE 'C' TO WS-FIN-OBJTYP-DBCR-CD

                wsFinancialObjectTypeDebitCreditCode = KFSConstants.GL_CREDIT_CODE;

                // 1194 007210 END-IF

            }

            // 1195 007220 WHEN +100
            // 1196 007230 WHEN +1403

        }
        else {

            throw new NonFatalErrorException(new StringBuffer("FIN OBJ TYP CODE ").append(balance.getObjectTypeCode()).append(" NOT IN TABLE").toString());

            // 1206 007330 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
            // 1207 007340 ADD 1 TO LINE-COUNT

        }
        return wsFinancialObjectTypeDebitCreditCode;
    }

    /**
     * Saves a generated origin entry to the database, within the proper group
     * 
     * @param balance the original balance, which still has the account to check if it is closed or not
     * @param entry the origin entry to save
     * @param closedPriorYearAccountGroup the group to put balance forwarding origin entries with closed accounts into
     * @param unclosedPriorYearAccountGroup the group to put balance forwarding origin entries with open accounts into
     */
    private void saveForwardingEntry(Balance balance, OriginEntryFull entry, OriginEntryGroup closedPriorYearAccountGroup, OriginEntryGroup unclosedPriorYearAccountGroup) {
        if (ObjectUtils.isNotNull(balance.getPriorYearAccount()) && !balance.getPriorYearAccount().isAccountClosedIndicator()) {

            // 1410 009320 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
            // 1411 009330 WS-AMT-N
            // 1412 009340 MOVE WS-AMT-X TO TRN-AMT-RED-X

            // 1413 009350 WRITE GLE-DATA FROM GLEN-RECORD

            originEntryService.createEntry(entry, unclosedPriorYearAccountGroup);

            // 1414 009360 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT

            // NOTE (laran) These fields don't seem to do anything at all in the COBOL.

            // NOTE (laran) If createEntry fails an exception will be thrown, stopping the run.
            // NOTE (laran) No explicit handling is required.

            // 1415 009370 IF GLEDATA-STATUS > '09'
            // 1416 009380 DISPLAY '**ERROR WRITING TO GLEDATA FILE'
            // 1417 009390 DISPLAY ' STATUS IS ' GLEDATA-STATUS
            // 1418 009400 MOVE 8 TO RETURN-CODE
            // 1419 009410 STOP RUN
            // 1420 END-IF

            // 1421 009420 ADD +1 TO SEQ-WRITE-COUNT

            state.incrementSequenceWriteCount();

            // 1422 MOVE SEQ-WRITE-COUNT TO SEQ-CHECK-CNT
            // 1423 IF SEQ-CHECK-CNT (7:3) = '000'

            if (0 == state.getSequenceWriteCount() % 1000) {

                // 1424 DISPLAY ' SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT

                LOG.info("  SEQUENTIAL RECORDS WRITTEN = " + state.getSequenceWriteCount());

                // 1425 END-IF

            }

            // 1426 009430 ELSE

        }
        else {

            // 1427 009440 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
            // 1428 009450 WS-AMT-N

            // NOTE (laran) These fields don't seem to do anything at all in the COBOL.

            // 1429 009460 MOVE WS-AMT-X TO TRN-AMT-RED-X

            // NOTE (laran) These fields don't seem to do anything at all in the COBOL.

            // 1430 009470 WRITE CLOSE-DATA FROM GLEN-RECORD

            originEntryService.createEntry(entry, closedPriorYearAccountGroup);

            // 1431 009480 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT

            // NOTE (laran) These fields don't seem to do anything at all in the COBOL.

            // NOTE (laran) If createEntry fails an exception will be thrown, stopping the run.
            // NOTE (laran) No explicit handling is required.

            // 1432 009490 IF CLOSEDATA-STATUS > '09'
            // 1433 009500 DISPLAY '**ERROR WRITING TO CLOSE FILE'
            // 1434 009510 DISPLAY ' STATUS IS ' CLOSEDATA-STATUS
            // 1435 009520 MOVE 8 TO RETURN-CODE
            // 1436 009530 STOP RUN
            // 1437 009540 END-IF

            // 1438 009550 ADD +1 TO SEQ-CLOSE-COUNT.

            state.incrementSequenceClosedCount();

            if (0 == state.getSequenceClosedCount() % 1000) {

                // 1424 DISPLAY ' SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT

                LOG.info("  CLOSED SEQUENTIAL RECORDS WRITTEN = " + state.getSequenceClosedCount());

                // 1425 END-IF

            }
        }
    }

    /**
     * @param priorYearAccountService The priorYearAccountService to set.
     */
    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

    /**
     * @param subFundGroupService The subFundGroupService to set.
     */
    public void setSubFundGroupService(SubFundGroupService subFundGroupService) {
        this.subFundGroupService = subFundGroupService;
    }

    /**
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public Integer getClosingFiscalYear() {
        return closingFiscalYear;
    }

    public void setClosingFiscalYear(Integer fiscalYear) {
        this.closingFiscalYear = fiscalYear;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public OriginEntryGroup getClosedPriorYearAccountGroup() {
        return closedPriorYearAccountGroup;
    }

    public void setClosedPriorYearAccountGroup(OriginEntryGroup closedPriorYearAccountGroup) {
        this.closedPriorYearAccountGroup = closedPriorYearAccountGroup;
    }

    public OriginEntryGroup getUnclosedPriorYearAccountGroup() {
        return unclosedPriorYearAccountGroup;
    }

    public void setUnclosedPriorYearAccountGroup(OriginEntryGroup unclosedPriorYearAccountGroup) {
        this.unclosedPriorYearAccountGroup = unclosedPriorYearAccountGroup;
    }

    public BalanceForwardProcessState getState() {
        return state;
    }
}
