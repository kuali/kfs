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

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.PriorYearAccount;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.financial.exceptions.InvalidFlexibleOffsetException;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.FatalErrorException;
import org.kuali.module.gl.util.ObjectHelper;

/**
 */
public class BalanceForwardRuleHelper {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardRuleHelper.class);
    private FlexibleOffsetAccountService flexibleOffsetAccountService;

    /**
     * A container for the state of the balance forward process. The way state is handled is heavily dependent upon the way in which
     * YearEndServiceImpl.forwardBalancesForFiscalYear works.
     * 
     */
    public static class BalanceForwardProcessState {
        private int globalReadCount;
        private int globalSelectCount;
        private int sequenceNumber;
        private int sequenceClosedCount;
        private int sequenceWriteCount;
        private String accountNumberHold;

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
    }

    private Integer closingFiscalYear;
    private Date transactionDate;

    private OriginEntryGroup closedPriorYearAccountGroup;
    private OriginEntryGroup unclosedPriorYearAccountGroup;

    private PriorYearAccountService priorYearAccountService;
    private SubFundGroupService subFundGroupService;
    private OriginEntryService originEntryService;

    private BalanceForwardProcessState state;

    public BalanceForwardRuleHelper() {
        super();
        state = new BalanceForwardProcessState();
        flexibleOffsetAccountService = SpringServiceLocator.getFlexibleOffsetAccountService();
    }

    /**
     * Constructs a BalanceForwardRuleHelper.
     * 
     * @param closingFiscalYear
     * @param transactionDate
     * @param closedPriorYearAccountGroup
     * @param unclosedPriorYearAccountGroup
     */
    public BalanceForwardRuleHelper(Integer closingFiscalYear, Date transactionDate, OriginEntryGroup closedPriorYearAccountGroup, OriginEntryGroup unclosedPriorYearAccountGroup) {
        this();
        setTransactionDate(transactionDate);
        setClosingFiscalYear(closingFiscalYear);
        setClosedPriorYearAccountGroup(closedPriorYearAccountGroup);
        setUnclosedPriorYearAccountGroup(unclosedPriorYearAccountGroup);
    }

    /**
     * Generate any appropriate origin entries to transact the forwarding of this balance to the opening fiscal year.
     * 
     * @param balance
     */
    public void processBalance(Balance balance) {

        boolean nonFatalErrorFlag = false;
        int nonFatalCount = 0;

        // 943 004660 PERFORM 2500-PROCESS-UNIT-OF-WORK
        // 944 004670 THRU 2500-PROCESS-UNIT-OF-WORK-EXIT.

        // 947 004700 2500-PROCESS-UNIT-OF-WORK.

        // 948 004710 ADD +1 TO GLBL-READ-COUNT.

        state.incrementGlobalReadCount();

        // 949 004720 MOVE 'N' TO WS-SELECT-GENERAL-SW.

        boolean selectGeneralSwFlag = false;

        // 950 004730 MOVE 'N' TO WS-SELECT-ACTIVE-SW.

        boolean selectActiveSwFlag = false;

        // 951 004740 PERFORM 3000-SELECT-CRITERIA
        // 952 004750 THRU 3000-SELECT-CRITERIA-EXIT.

        try {

            // 965 004900 3000-SELECT-CRITERIA.
            // 966 004910 IF (GLGLBL-FIN-BALANCE-TYP-CD = 'AC'
            // 967 004920 OR GLGLBL-FIN-BALANCE-TYP-CD = 'NB')
            // 968 004930 AND
            // 969 004940**** 'NB' ADDED TO ABOVE 8/10/95 ARE
            // 970 004950**** 'EE' REMOVED FROM FOLLOWING LINE 7/18/95 ARE
            // 971 004960 (GLGLBL-FIN-OBJ-TYP-CD = 'AS' OR 'LI' OR 'FB')
            // 972 004970 MOVE 'Y' TO WS-SELECT-GENERAL-SW.

            Options options = SpringServiceLocator.getOptionsService().getCurrentYearOptions();
            String[] generalSwObjectTypes = new String[3];
            generalSwObjectTypes[0] = options.getFinancialObjectTypeAssetsCd();
            generalSwObjectTypes[1] = options.getFinObjectTypeLiabilitiesCode();
            generalSwObjectTypes[2] = options.getFinObjectTypeFundBalanceCd();

            if (ObjectHelper.isOneOf(balance.getBalanceTypeCode(), new String[] { "AC", "NB" }) && ObjectHelper.isOneOf(balance.getObjectTypeCode(), generalSwObjectTypes)) {

                selectGeneralSwFlag = true;

            }

            // 973 005000 IF WS-SELECT-GENERAL-YES

            // WS-SELECT-GENERAL-YES and WS-SELECT-GENERAL-SW are synonyms.
            // WS-SELECT-ACTIVE-YES and WS-SELECT-ACTIVE-SW are synonyms.
            if (selectGeneralSwFlag) { // In Java this block does nothing. Can be removed.

                // 974 005010 IF GLGLBL-ACLN-ANNL-BAL-AMT NUMERIC

                // NOTE Will always be numeric in Java.

                // 975 005020 NEXT SENTENCE
                // 976 005030 ELSE
                // 977 005040 MOVE ZERO
                // 978 005050 TO GLGLBL-ACLN-ANNL-BAL-AMT
                // 979 005060 PERFORM 8500-CHECK-NEW-PAGE
                // 980 005070 THRU 8500-CHECK-NEW-PAGE-EXIT
                // 981 005080 MOVE LIT-KEY TO RP-ERROR-LABEL OF RP-ERROR-LINE-1
                // 982 005090 MOVE DCLGL-BALANCE-T (1:29)
                // 983 005100 TO RP-ERROR-MSG OF RP-ERROR-LINE-1
                // 984 005110 MOVE RP-ERROR-LINE-1 TO PRINT-DATA
                // 985 005120 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG
                // 986 005130 ADD +1 TO NON-FATAL-COUNT
                // 987 005140 WRITE PRINT-DATA
                // 988 005150 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
                // 989 005160 ADD +1 TO LINE-COUNT
                // 990 005170 END-IF

                // 991 005180 IF GLGLBL-FIN-BEG-BAL-LN-AMT NUMERIC

                // NOTE Will always be numeric in Java.

                // 992 005190 NEXT SENTENCE
                // 993 005200 ELSE
                // 994 005210 MOVE ZERO
                // 995 005220 TO GLGLBL-FIN-BEG-BAL-LN-AMT
                // 996 005230 PERFORM 8500-CHECK-NEW-PAGE
                // 997 005240 THRU 8500-CHECK-NEW-PAGE-EXIT
                // 998 005250 MOVE LIT-KEY TO RP-ERROR-LABEL OF RP-ERROR-LINE-1
                // 999 005260 MOVE DCLGL-BALANCE-T (1:29)
                // 1000 005270 TO RP-ERROR-MSG OF RP-ERROR-LINE-1
                // 1001 005280 MOVE RP-ERROR-LINE-1 TO PRINT-DATA
                // 1002 005290 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG
                // 1003 005300 ADD +1 TO NON-FATAL-COUNT
                // 1004 005310 WRITE PRINT-DATA
                // 1005 005320 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
                // 1006 005330 ADD +1 TO LINE-COUNT
                // 1007 005340 ELSE
                // 1008 005350 NEXT SENTENCE.

            }

            // WS-SELECT-GENERAL-YES and WS-SELECT-GENERAL-SW are synonyms.
            // WS-SELECT-ACTIVE-YES and WS-SELECT-ACTIVE-SW are synonyms.
            if (selectGeneralSwFlag) {

                // 1009 005360 IF WS-SELECT-GENERAL-YES
                // 1010 005370 IF (GLGLBL-ACLN-ANNL-BAL-AMT +
                // 1011 005380 GLGLBL-CONTR-GR-BB-AC-AMT +
                // 1012 005390 GLGLBL-FIN-BEG-BAL-LN-AMT) = ZERO

                if (balance.getAccountLineAnnualBalanceAmount().add(balance.getContractsGrantsBeginningBalanceAmount()).add(balance.getBeginningBalanceLineAmount()).isZero()) {

                    // 1013 005400 MOVE 'N' TO WS-SELECT-GENERAL-SW

                    selectGeneralSwFlag = false;

                    // 1014 005410 ELSE
                    // 1015 005420 NEXT SENTENCE

                }

                // 1016 005430 ELSE
                // 1017 005440 NEXT SENTENCE.

            }

            // 1018 005450 IF (GLGLBL-FIN-BALANCE-TYP-CD = 'AC' OR 'CB')
            // 1019 005460 AND
            // 1020 005470 (GLGLBL-FIN-OBJ-TYP-CD = 'EE' OR 'ES' OR
            // 1021 005480 'EX' OR 'IC' OR
            // 1022 005490 'TE' OR 'TI' OR
            // 1023 005500 'IN' OR 'CH')
            // 1024 005510**** THE ABOVE LINE HAD 'CH' ADDED 7/18/95 ARE

            PriorYearAccount priorYearAccount = null; // This is used below in the write routine.

            // "EE", "ES", "EX", "IC", "TE", "TI", "IN", "CH"
            String[] priorYearAccountObjectTypes = new String[8];
            priorYearAccountObjectTypes[0] = options.getFinObjTypeExpendNotExpCode();
            priorYearAccountObjectTypes[1] = options.getFinObjTypeExpNotExpendCode();
            priorYearAccountObjectTypes[2] = options.getFinObjTypeExpenditureexpCd();
            priorYearAccountObjectTypes[3] = options.getFinObjTypeIncomeNotCashCd();
            priorYearAccountObjectTypes[4] = options.getFinancialObjectTypeTransferExpenseCode();
            priorYearAccountObjectTypes[5] = options.getFinancialObjectTypeTransferIncomeCode();
            priorYearAccountObjectTypes[6] = options.getFinObjectTypeIncomecashCode();
            priorYearAccountObjectTypes[7] = options.getFinObjTypeCshNotIncomeCd();

            if (ObjectHelper.isOneOf(balance.getBalanceTypeCode(), new String[] { "AC", "CB" }) && ObjectHelper.isOneOf(balance.getObjectTypeCode(), priorYearAccountObjectTypes)) {

                // 1025 005520 MOVE GLGLBL-FIN-COA-CD
                // 1026 005530 TO CAPYACTT-FIN-COA-CD
                // 1027 005540 MOVE GLGLBL-ACCOUNT-NBR
                // 1028 005550 TO CAPYACTT-ACCOUNT-NBR
                // 1029 005560 EXEC SQL
                // 1030 005570 SELECT SUB_FUND_GRP_CD,
                // 1031 005580 ACCT_EXPIRATION_DT,
                // 1032 005590 ACCT_CLOSED_IND
                // 1033 005600 INTO :CAPYACTT-SUB-FUND-GRP-CD :CAPYACTT-SFGC-I,
                // 1034 005610 :CAPYACTT-ACCT-EXPIRATION-DT :EXP-D-IND-VAR,
                // 1035 005620 :CAPYACTT-ACCT-CLOSED-IND :CAPYACTT-ACI-I
                // 1036 005630 FROM CA_PRIOR_YR_ACCT_T
                // 1037 005640 WHERE FIN_COA_CD = RTRIM(:CAPYACTT-FIN-COA-CD)
                // 1038 005650 AND ACCOUNT_NBR = RTRIM(:CAPYACTT-ACCOUNT-NBR)
                // 1039 005660 END-EXEC

                priorYearAccount = priorYearAccountService.getByPrimaryKey(balance.getChartOfAccountsCode(), balance.getAccountNumber());

                // 1040 005670 IF CAPYACTT-SFGC-I < ZERO
                // 1041 005680 MOVE SPACE TO CAPYACTT-SUB-FUND-GRP-CD
                // 1042 005690 END-IF
                // 1043 005700 IF CAPYACTT-ACI-I < ZERO
                // 1044 005710 MOVE SPACE TO CAPYACTT-ACCT-CLOSED-IND
                // 1045 005720 END-IF
                // 1046 005730 EVALUATE SQLCODE
                // 1047 005740 WHEN 0
                // 1048 005750 MOVE CAPYACTT-ACCT-EXPIRATION-DT
                // 1049 005760 TO WS-ACCT-EXPIRATION-DT
                // 1050 005770 MOVE CAPYACTT-SUB-FUND-GRP-CD
                // 1051 005780 TO WS-SUB-FUND-GRP-CD
                // 1052 005790 WHEN OTHER

                if (null == priorYearAccount) {

                    // 1053 005800 DISPLAY ' ERROR ACCESSING PRIOR YR ACCT TABLE FOR '
                    // 1054 005810 CAPYACTT-FIN-COA-CD
                    // 1055 005820 CAPYACTT-ACCOUNT-NBR
                    // 1056 005830 ' SQL CODE IS ' SQLCODE
                    // 1057 005840 MOVE 'Y' TO WS-FATAL-ERROR-FLAG

                    throw new FatalErrorException(new StringBuffer(" ERROR ACCESSING PRIOR YR ACCT TABLE FOR ").append("CHART ").append(balance.getChartOfAccountsCode()).append(" AND ACCOUNT ").append(balance.getAccountNumber()).toString());

                    // 1058 005850 GO TO 3000-SELECT-CRITERIA-EXIT

                }

                // 1059 005860 END-EVALUATE
                // 1060 005870 MOVE WS-SUB-FUND-GRP-CD
                // 1061 005880 TO CASFGR-SUB-FUND-GRP-CD
                // 1062 005890 EXEC SQL
                // 1063 005900 SELECT FUND_GRP_CD
                // 1064 005910 INTO :CASFGR-FUND-GRP-CD :CASFGR-FGC-I
                // 1065 005920 FROM CA_SUB_FUND_GRP_T
                // 1066 005930 WHERE SUB_FUND_GRP_CD = RTRIM(:CASFGR-SUB-FUND-GRP-CD)
                // 1067 005940 END-EXEC

                // priorYearAccount is guaranteed to be non-null at this point.
                SubFundGroup subFundGroup = subFundGroupService.getByPrimaryId(priorYearAccount.getSubFundGroupCode());

                // 1068 005950 IF CASFGR-FGC-I < ZERO
                // 1069 005960 MOVE SPACE TO CASFGR-FUND-GRP-CD
                // 1070 005970 END-IF
                // 1071 005980 EVALUATE SQLCODE
                // 1072 005990 WHEN 0

                if (null != subFundGroup) {

                    // 1073 006000 IF (CASFGR-FUND-GRP-CD = 'CG')
                    // 1074 006010 OR
                    // 1075 006020 (CASFGR-SUB-FUND-GRP-CD = 'SDCI '
                    // 1076 006030 OR 'PFCMR ')

                    // Contract and grants balances.
                    if (priorYearAccount.isForContractsAndGrants() || ObjectHelper.isOneOf(subFundGroup.getSubFundGroupCode().trim(), new String[] { "SDCI", "PFCMR" })) {

                        // 1077 006040 MOVE 'Y' TO WS-SELECT-ACTIVE-SW

                        selectActiveSwFlag = true;

                        // 1078 006050 END-IF

                    }

                    // 1079 006060 WHEN OTHER

                }
                else {

                    // 1080 006070 DISPLAY ' ERROR ACCESSING SUB FUND GROUP TABLE FOR '
                    // 1081 006080 CASFGR-SUB-FUND-GRP-CD
                    // 1082 006090 MOVE 'Y' TO WS-FATAL-ERROR-FLAG

                    throw new FatalErrorException(new StringBuffer(" ERROR ACCESSING SUB FUND GROUP TABLE FOR SUB FUND GROUP ").append(priorYearAccount.getSubFundGroupCode()).toString());

                    // 1083 006100 GO TO 3000-SELECT-CRITERIA-EXIT

                }

                // 1084 006110 END-EVALUATE.

            }

            // 1085 006120 IF WS-SELECT-ACTIVE-YES

            // WS-SELECT-GENERAL-YES and WS-SELECT-GENERAL-SW are synonyms.
            // WS-SELECT-ACTIVE-YES and WS-SELECT-ACTIVE-SW are synonyms.
            if (selectActiveSwFlag) { // This block does nothing in Java. Can be removed.

                // 1086 006130 IF GLGLBL-ACLN-ANNL-BAL-AMT NUMERIC
                // 1087 006140 NEXT SENTENCE
                // 1088 006150 ELSE
                // 1089 006160 MOVE ZERO
                // 1090 006170 TO GLGLBL-ACLN-ANNL-BAL-AMT
                // 1091 006180 MOVE +1 TO LINES-TO-PRINT
                // 1092 006190 PERFORM 8500-CHECK-NEW-PAGE
                // 1093 006200 THRU 8500-CHECK-NEW-PAGE-EXIT
                // 1094 006210 MOVE LIT-KEY TO RP-ERROR-LABEL OF RP-ERROR-LINE-1
                // 1095 006220 MOVE DCLGL-BALANCE-T (1:29)
                // 1096 006230 TO RP-ERROR-MSG OF RP-ERROR-LINE-1
                // 1097 006240 MOVE RP-ERROR-LINE-1 TO PRINT-DATA
                // 1098 006250 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG
                // 1099 006260 ADD +1 TO NON-FATAL-COUNT
                // 1100 006270 WRITE PRINT-DATA
                // 1101 006280 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
                // 1102 006290 ADD +1 TO LINE-COUNT
                // 1103 006300 END-IF

                // 1104 006310 IF GLGLBL-CONTR-GR-BB-AC-AMT NUMERIC
                // 1105 006320 NEXT SENTENCE
                // 1106 006330 ELSE
                // 1107 006340 MOVE ZERO
                // 1108 006350 TO GLGLBL-CONTR-GR-BB-AC-AMT
                // 1109 006360 MOVE +1 TO LINES-TO-PRINT
                // 1110 006370 PERFORM 8500-CHECK-NEW-PAGE
                // 1111 006380 THRU 8500-CHECK-NEW-PAGE-EXIT
                // 1112 006390 MOVE LIT-KEY TO RP-ERROR-LABEL OF RP-ERROR-LINE-1
                // 1113 006400 MOVE DCLGL-BALANCE-T (1:29)
                // 1114 006410 TO RP-ERROR-MSG OF RP-ERROR-LINE-1
                // 1115 006420 MOVE RP-ERROR-LINE-1 TO PRINT-DATA
                // 1116 006430 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG
                // 1117 006440 ADD +1 TO NON-FATAL-COUNT
                // 1118 006450 WRITE PRINT-DATA
                // 1119 006460 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
                // 1120 006470 ADD +1 TO LINE-COUNT
                // 1121 006480 ELSE
                // 1122 006490 NEXT SENTENCE.

            }

            // 1123 006500 IF WS-SELECT-ACTIVE-YES

            // WS-SELECT-GENERAL-YES and WS-SELECT-GENERAL-SW are synonyms.
            // WS-SELECT-ACTIVE-YES and WS-SELECT-ACTIVE-SW are synonyms.
            if (selectActiveSwFlag) {

                // 1124 006510 IF (GLGLBL-ACLN-ANNL-BAL-AMT +
                // 1125 006520 GLGLBL-CONTR-GR-BB-AC-AMT) = ZERO

                if (balance.getAccountLineAnnualBalanceAmount().add(balance.getContractsGrantsBeginningBalanceAmount()).isZero()) {

                    // 1126 006530 MOVE 'N' TO WS-SELECT-ACTIVE-SW

                    selectActiveSwFlag = false;

                }

                // 1127 006540 ELSE
                // 1128 006550 NEXT SENTENCE
                // 1129 006560 ELSE
                // 1130 006570 NEXT SENTENCE.

            }

            // 953 004760 IF GLGLBL-ACCOUNT-NBR = WS-ACCOUNT-NBR-HOLD

            if ((null == balance.getAccountNumber() && null == state.getAccountNumberHold()) || (null != balance.getAccountNumber() && balance.getAccountNumber().equals(state.getAccountNumberHold()))) {

                // 954 004770 ADD 1 TO WS-SEQ-NBR

                state.incrementSequenceNumber();

                // 955 004780 ELSE

            }
            else {

                // 956 004790 MOVE 1 TO WS-SEQ-NBR.

                state.setSequenceNumber(1);

            }

            // 957 004800 IF WS-SELECT-GENERAL-YES OR
            // 958 004810 WS-SELECT-ACTIVE-YES

            // WS-SELECT-GENERAL-YES and WS-SELECT-GENERAL-SW are synonyms.
            // WS-SELECT-ACTIVE-YES and WS-SELECT-ACTIVE-SW are synonyms.
            if (selectActiveSwFlag || selectGeneralSwFlag) {

                // 959 004820 ADD +1 TO GLBL-SELECT-COUNT

                state.incrementGlobalSelectCount();

                // 960 004830 PERFORM 4000-WRITE-OUTPUT
                // 961 004840 THRU 4000-WRITE-OUTPUT-EXIT.

                // 1132 006590 4000-WRITE-OUTPUT.
                // 1133 006600 MOVE GLGLBL-FIN-BALANCE-TYP-CD
                // 1134 006610 TO CABTYP-FIN-BALANCE-TYP-CD.
                // 1135 006620 EXEC SQL
                // 1136 006630 SELECT FIN_BALTYP_ENC_CD
                // 1137 006640 INTO :CABTYP-FIN-BALTYP-ENC-CD :CABTYP-FBEC-I
                // 1138 006650 FROM CA_BALANCE_TYPE_T
                // 1139 006660 WHERE FIN_BALANCE_TYP_CD =
                // 1140 006670 RTRIM(:CABTYP-FIN-BALANCE-TYP-CD)
                // 1141 006680 END-EXEC.

                BalanceTyp balanceType = balance.getBalanceType();

                // 1142 006690 IF CABTYP-FBEC-I < ZERO
                // 1143 006700 MOVE SPACE TO CABTYP-FIN-BALTYP-ENC-CD
                // 1144 006710 END-IF

                OriginEntry entry = new OriginEntry();

                // 1145 006720 EVALUATE SQLCODE
                // 1146 006730 WHEN 0

                if (null != balanceType) {

                    // 1147 006740 IF CABTYP-FIN-BALTYP-ENC-CD = 'Y'

                    if (balanceType.isFinBalanceTypeEncumIndicator()) {

                        // 1148 006750 MOVE 'N' TO TRN-ENCUM-UPDT-CD
                        // 1149 006760 WS-TRN-ENCUM-UPDT-CD

                        entry.setTransactionEncumbranceUpdateCode("N");

                        // 1150 006770 ELSE

                    }
                    else {

                        // 1151 006780 MOVE ' ' TO TRN-ENCUM-UPDT-CD
                        // 1152 006790 WS-TRN-ENCUM-UPDT-CD

                        entry.setTransactionEncumbranceUpdateCode(null);

                        // 1153 006800 END-IF

                    }

                    // 1154 006810 WHEN +100
                    // 1155 006820 WHEN +1403

                }
                else {

                    // 1156 006830 PERFORM 8500-CHECK-NEW-PAGE
                    // 1157 006840 THRU 8500-CHECK-NEW-PAGE-EXIT

                    // 1158 006850 MOVE ' ' TO TRN-ENCUM-UPDT-CD
                    // 1159 006860 WS-TRN-ENCUM-UPDT-CD

                    entry.setTransactionEncumbranceUpdateCode(null);

                    // 1160 006870 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG

                    nonFatalErrorFlag = true;

                    // 1161 006880 ADD +1 TO NON-FATAL-COUNT

                    nonFatalCount++;

                    // 1162 006890 MOVE ' ERROR ' TO PRINT-DATA
                    // 1163 006900 MOVE CABTYP-FIN-BALANCE-TYP-CD TO PRINT-DATA (8:4)
                    // 1164 006910 MOVE ' NOT ON TABLE ' TO PRINT-DATA (13:14)
                    // 1165 006920 WRITE PRINT-DATA

                    LOG.info(new StringBuffer(" ERROR ").append(balance.getBalanceTypeCode()).append(" NOT ON TABLE "));

                    // 1166 006930 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
                    // 1167 006940 ADD +1 TO LINE-COUNT
                    // 1168 006950 WHEN OTHER
                    // 1169 006960 DISPLAY ' ERROR ACCESSING BALANCE TYPE TABLE FOR '
                    // 1170 006970 CABTYP-FIN-BALANCE-TYP-CD
                    // 1171 006980 MOVE 'Y' TO WS-FATAL-ERROR-FLAG
                    // 1172 006990 GO TO 4000-WRITE-OUTPUT-EXIT

                }

                // 1173 007000 END-EVALUATE.

                // 1174 007010 MOVE GLGLBL-FIN-OBJ-TYP-CD
                // 1175 007020 TO CAOTYP-FIN-OBJ-TYP-CD.
                // 1176 007030 EXEC SQL
                // 1177 007040 SELECT FIN_OBJTYP_DBCR_CD
                // 1178 007050 INTO :CAOTYP-FIN-OBJTYP-DBCR-CD :CAOTYP-FODC-I
                // 1179 007060 FROM CA_OBJ_TYPE_T
                // 1180 007070 WHERE FIN_OBJ_TYP_CD = RTRIM(:CAOTYP-FIN-OBJ-TYP-CD)
                // 1181 007080 END-EXEC.

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

                    if (ObjectHelper.isOneOf(balanceObjectTypeDebitCreditCode, new String[] { Constants.GL_CREDIT_CODE, Constants.GL_DEBIT_CODE })) {

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

                        wsFinancialObjectTypeDebitCreditCode = Constants.GL_CREDIT_CODE;

                        // 1193 007200 MOVE 'D' TO TRN-DEBIT-CRDT-CD

                        entry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

                        // 1194 007210 END-IF

                    }

                    // 1195 007220 WHEN +100
                    // 1196 007230 WHEN +1403

                }
                else {

                    // 1197 007240 PERFORM 8500-CHECK-NEW-PAGE
                    // 1198 007250 THRU 8500-CHECK-NEW-PAGE-EXIT
                    // 1199 007260 MOVE 'C' TO WS-FIN-OBJTYP-DBCR-CD

                    wsFinancialObjectTypeDebitCreditCode = Constants.GL_CREDIT_CODE;

                    // 1200 007270 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG

                    nonFatalErrorFlag = true;

                    // 1201 007280 ADD +1 TO NON-FATAL-COUNT

                    nonFatalCount++;

                    // 1202 007290 MOVE 'FIN OBJ TYP CODE ' TO PRINT-DATA
                    // 1203 007300 MOVE CAOTYP-FIN-OBJ-TYP-CD TO PRINT-DATA (18:4)
                    // 1204 007310 MOVE ' NOT IN TABLE' TO PRINT-DATA (24:13)
                    // 1205 007320 WRITE PRINT-DATA

                    LOG.info(new StringBuffer("FIN OBJ TYP CODE ").append(balance.getObjectTypeCode()).append(" NOT IN TABLE"));

                    // 1206 007330 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
                    // 1207 007340 ADD 1 TO LINE-COUNT

                }

                // 1208 007350 WHEN OTHER
                // 1209 007360 DISPLAY ' ERROR ACCESSING OBJECT TYPE TABLE FOR '
                // 1210 007370 CAOTYP-FIN-OBJ-TYP-CD
                // 1211 007380 MOVE 'Y' TO WS-FATAL-ERROR-FLAG
                // 1212 007390 GO TO 4000-WRITE-OUTPUT-EXIT

                // 1213 007400 END-EVALUATE.

                // 1214 007410 IF WS-SELECT-GENERAL-YES

                // WS-SELECT-GENERAL-YES and WS-SELECT-GENERAL-SW are synonyms.
                // WS-SELECT-ACTIVE-YES and WS-SELECT-ACTIVE-SW are synonyms.
                if (selectGeneralSwFlag) {

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

                    if (options.getFinObjTypeExpendNotExpCode().equals(balance.getObjectTypeCode())) {

                        // 1242 007690 MOVE 'AS'
                        // 1243 007700 TO FIN-OBJ-TYP-CD

                        entry.setFinancialObjectTypeCode(options.getFinancialObjectTypeAssetsCd());

                        // 1244 007710 ELSE

                    }
                    else {

                        // 1245 007720 MOVE GLGLBL-FIN-OBJ-TYP-CD
                        // 1246 007730 TO FIN-OBJ-TYP-CD.

                        entry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

                    }

                    // 1247 007740 MOVE 'BB'
                    // 1248 007750 TO UNIV-FISCAL-PRD-CD.

                    entry.setUniversityFiscalPeriodCode("BB");

                    // 1249 007760 MOVE 'ACLO'
                    // 1250 007770 TO FDOC-TYP-CD.

                    entry.setFinancialDocumentTypeCode("ACLO");

                    // 1251 007780 MOVE 'MF'
                    // 1252 007790 TO FS-ORIGIN-CD.

                    entry.setFinancialSystemOriginationCode("MF");

                    // 1253 007800 STRING 'AC'
                    // 1254 007810 GLGLBL-ACCOUNT-NBR
                    // 1255 007820 RP-BLANK-LINE
                    // 1256 007830 DELIMITED BY SIZE
                    // 1257 007840 INTO FDOC-NBR.

                    // FIXME Once tests are running properly uncomment the code to include the
                    // chartOfAccountsCode in the document number. It will cause the tests to
                    // break given the current framework but is desired as an enhancement for Kuali.
                    entry.setDocumentNumber(new StringBuffer("AC").append(balance.getAccountNumber())/* .append(balance.getChartOfAccountsCode()) */.toString());

                    // 1258 007850 MOVE WS-SEQ-NBR
                    // 1259 007860 TO TRN-ENTR-SEQ-NBR.

                    entry.setTransactionLedgerEntrySequenceNumber(new Integer(state.getSequenceNumber()));

                    // 1260 007870 STRING 'BEG BAL BROUGHT FORWARD FROM '
                    // 1261 007880 VAR-UNIV-FISCAL-YR
                    // 1262 007890 RP-BLANK-LINE
                    // 1263 007900 DELIMITED BY SIZE INTO
                    // 1264 007910 TRN-LDGR-ENTR-DESC.

                    entry.setTransactionLedgerEntryDescription(new StringBuffer("BEG BAL BROUGHT FORWARD FROM ").append(closingFiscalYear).toString());

                    // 1265 007920 ADD GLGLBL-ACLN-ANNL-BAL-AMT
                    // 1266 007930 GLGLBL-FIN-BEG-BAL-LN-AMT
                    // 1267 007940 GLGLBL-CONTR-GR-BB-AC-AMT
                    // 1268 007950 GIVING TRN-LDGR-ENTR-AMT.

                    KualiDecimal transactionLedgerEntryAmount = new KualiDecimal(0);
                    transactionLedgerEntryAmount = transactionLedgerEntryAmount.add(balance.getAccountLineAnnualBalanceAmount()).add(balance.getBeginningBalanceLineAmount()).add(balance.getContractsGrantsBeginningBalanceAmount());

                    // 1269 007960 IF TRN-LDGR-ENTR-AMT < ZERO

                    if (transactionLedgerEntryAmount.isNegative()) {

                        // 1270 007970 IF WS-FIN-OBJTYP-DBCR-CD = 'D'

                        if (Constants.GL_DEBIT_CODE.equals(wsFinancialObjectTypeDebitCreditCode)) {

                            // 1271 007980 MOVE 'C' TO TRN-DEBIT-CRDT-CD

                            entry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);

                            // 1272 007990 ELSE

                        }
                        else {

                            // 1273 008000 MOVE 'D' TO TRN-DEBIT-CRDT-CD

                            entry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

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

                    entry.setProjectCode(Constants.DASHES_PROJECT_CODE);

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

                    if ("NB".equals(entry.getFinancialBalanceTypeCode())) {

                        // 1296 008230 MOVE 'AC' TO FIN-BALANCE-TYP-CD.

                        entry.setFinancialBalanceTypeCode("AC");

                    }

                    // 1297 008240 IF TRN-LDGR-ENTR-AMT < 0

                    if (transactionLedgerEntryAmount.isNegative()) {

                        // 1298 008250 IF FIN-BALANCE-TYP-CD = 'AC'

                        if ("AC".equals(entry.getFinancialBalanceTypeCode())) {

                            // 1299 008260 COMPUTE TRN-LDGR-ENTR-AMT
                            // 1300 008270 = TRN-LDGR-ENTR-AMT * -1.

                            transactionLedgerEntryAmount = transactionLedgerEntryAmount.negated();

                        }

                    }

                    entry.setTransactionLedgerEntryAmount(transactionLedgerEntryAmount);

                    // 1301 008280 IF CAPYACTT-ACCT-CLOSED-IND NOT = 'Y'

                    if (null != priorYearAccount && !priorYearAccount.isAccountClosedIndicator()) {

                        // 1302 008290 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
                        // 1303 008300 WS-AMT-N
                        // 1304 008310 MOVE WS-AMT-X TO TRN-AMT-RED-X

                        // NOTE (laran) These fields don't seem to do anything at all in the COBOL.

                        // 1305 008320 WRITE GLE-DATA FROM GLEN-RECORD

                        originEntryService.createEntry(entry, unclosedPriorYearAccountGroup);

                        // 1306 008330 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT

                        // NOTE (laran) These fields don't seem to do anything at all in the COBOL.

                        // NOTE (laran) If createEntry fails an exception will be thrown, stopping the run.
                        // NOTE (laran) No explicit handling is required.

                        // 1307 008340 IF GLEDATA-STATUS > '09'
                        // 1308 008350 DISPLAY '**ERROR WRITING TO GLE FILE'
                        // 1309 008360 DISPLAY ' STATUS IS ' GLEDATA-STATUS
                        // 1310 008370 MOVE 8 TO RETURN-CODE
                        // 1311 008380 STOP RUN
                        // 1312 END-IF

                        // NOTE (laran) This same logic would be handled in the form of a thrown
                        // exception which would halt execution analogously to a "STOP RUN".

                        // 1313 008390 ADD +1 TO SEQ-WRITE-COUNT

                        state.incrementSequenceWriteCount();

                        // 1314 MOVE SEQ-WRITE-COUNT TO SEQ-CHECK-CNT
                        // 1315 IF SEQ-CHECK-CNT (7:3) = '000'

                        if (0 == state.getSequenceWriteCount() % 1000) {

                            // 1316 DISPLAY ' SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT

                            LOG.info("  SEQUENTIAL RECORDS WRITTEN = " + state.getSequenceWriteCount());

                            // 1317 END-IF

                        }

                        // 1318 008400 ELSE

                    }
                    else {

                        // 1319 008410 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
                        // 1320 008420 WS-AMT-N

                        // NOTE (laran) These fields don't seem to do anything at all in the COBOL.

                        // 1321 008430 MOVE WS-AMT-X TO TRN-AMT-RED-X

                        // NOTE (laran) These fields don't seem to do anything at all in the COBOL.

                        // 1322 008440 WRITE CLOSE-DATA FROM GLEN-RECORD

                        originEntryService.createEntry(entry, closedPriorYearAccountGroup);

                        // TODO Create in the database an origin entry group source code for balance
                        // forwards on closed prior year accounts

                        // 1323 008450 MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT

                        // NOTE (laran) If createEntry fails an exception will be thrown, stopping the run.
                        // NOTE (laran) No explicit handling is required.

                        // 1324 008460 IF CLOSEDATA-STATUS > '09'
                        // 1325 008470 DISPLAY '**ERROR WRITING TO CLOSE FILE'
                        // 1326 008480 DISPLAY ' STATUS IS ' CLOSEDATA-STATUS
                        // 1327 008490 MOVE 8 TO RETURN-CODE
                        // 1328 008500 STOP RUN
                        // 1329 008510 END-IF

                        // 1330 008520 ADD +1 TO SEQ-CLOSE-COUNT.

                        state.incrementSequenceClosedCount();

                    }

                    // 1331 008530 4100-WRITE-GENERAL-EXIT. EXIT.

                    // 1217 007440 ELSE

                }
                else {

                    // 1218 007450 NEXT SENTENCE.

                    boolean debitCreditFlagWasNotSet = true;

                }

                // 1219 007460 IF WS-SELECT-ACTIVE-YES

                // WS-SELECT-GENERAL-YES and WS-SELECT-GENERAL-SW are synonyms.
                // WS-SELECT-ACTIVE-YES and WS-SELECT-ACTIVE-SW are synonyms.
                if (selectActiveSwFlag) {

                    // 1220 007470 PERFORM 4200-WRITE-ACTIVE
                    // 1221 007480 THRU 4200-WRITE-ACTIVE-EXIT

                    // 1332 008540 4200-WRITE-ACTIVE.
                    // 1333 008550 MOVE SPACES TO GLEN-RECORD.

                    OriginEntry activeEntry = new OriginEntry();

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

                    activeEntry.setUniversityFiscalPeriodCode("CB");

                    // 1352 008740 MOVE 'ACLO'
                    // 1353 008750 TO FDOC-TYP-CD OF GLEN-RECORD.

                    activeEntry.setFinancialDocumentTypeCode("ACLO");

                    // 1354 008760 MOVE 'MF'
                    // 1355 008770 TO FS-ORIGIN-CD OF GLEN-RECORD.

                    activeEntry.setFinancialSystemOriginationCode("MF");

                    // 1356 008780 STRING 'AC'
                    // 1357 008790 GLGLBL-ACCOUNT-NBR
                    // 1358 008800 RP-BLANK-LINE
                    // 1359 008810 DELIMITED BY SIZE
                    // 1360 008820 INTO FDOC-NBR OF GLEN-RECORD.

                    activeEntry.setDocumentNumber(new StringBuffer("AC").append(balance.getAccountNumber()).toString());

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

                    if ("CB".equals(balance.getBalanceTypeCode())) {

                        // 1372 008940 MOVE SPACES
                        // 1373 008950 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                        activeEntry.setTransactionDebitCreditCode(null);

                        // 1374 008960 ELSE

                    }
                    else {

                        // 1375 008970 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < ZERO

                        if (activeEntry.getTransactionLedgerEntryAmount().isNegative()) {

                            // 1376 008980 IF WS-FIN-OBJTYP-DBCR-CD = 'C'

                            if (Constants.GL_CREDIT_CODE.equals(wsFinancialObjectTypeDebitCreditCode)) {

                                // 1377 008990 MOVE 'D'
                                // 1378 009000 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                                activeEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

                                // 1379 009010 ELSE

                            }
                            else {

                                // 1380 009020 MOVE 'C'
                                // 1381 009030 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                                activeEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);

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

                    activeEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);

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

                    activeEntry.setTransactionEncumbranceUpdateCode(entry.getTransactionEncumbranceUpdateCode());

                    // 1403 009250 IF FIN-BALANCE-TYP-CD OF GLEN-RECORD = 'NB'

                    // NOTE (laran) This might be a bug. I'm not sure if
                    // "FIN-BALANCE-TYP-CD OF GLEN-RECORD" refers to the
                    // value previously set into the GLEN-RECORD, which
                    // would be the variable called "entry" at this point.
                    // Or perhaps it refers to a value set previously on the
                    // variable called "activeEntry" at this point.

                    if ("NB".equals(balance.getBalanceTypeCode())) {

                        // 1404 009260 MOVE 'AC' TO FIN-BALANCE-TYP-CD OF GLEN-RECORD.

                        activeEntry.setFinancialBalanceTypeCode("AC");

                    }

                    // 1405 009270 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < 0

                    if (activeEntry.getTransactionLedgerEntryAmount().isNegative()) {

                        // 1406 009280 IF FIN-BALANCE-TYP-CD OF GLEN-RECORD = 'AC'

                        if ("AC".equals(activeEntry.getFinancialBalanceTypeCode())) {

                            // 1407 009290 COMPUTE TRN-LDGR-ENTR-AMT OF GLEN-RECORD
                            // 1408 009300 = TRN-LDGR-ENTR-AMT OF GLEN-RECORD * -1.

                            activeEntry.setTransactionLedgerEntryAmount(activeEntry.getTransactionLedgerEntryAmount().negated());

                        }

                    }

                    // 1409 009310 IF CAPYACTT-ACCT-CLOSED-IND NOT = 'Y'

                    if (null != priorYearAccount && !priorYearAccount.isAccountClosedIndicator()) {

                        // 1410 009320 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-W-PERIOD
                        // 1411 009330 WS-AMT-N
                        // 1412 009340 MOVE WS-AMT-X TO TRN-AMT-RED-X

                        // 1413 009350 WRITE GLE-DATA FROM GLEN-RECORD

                        originEntryService.createEntry(activeEntry, unclosedPriorYearAccountGroup);

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

                        originEntryService.createEntry(activeEntry, closedPriorYearAccountGroup);

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

                    }

                    // 1439 009560 4200-WRITE-ACTIVE-EXIT. EXIT.

                    // 1222 007490 ELSE

                }
                else {

                    // 1223 007500 NEXT SENTENCE.

                }

                // 1224 007510 4000-WRITE-OUTPUT-EXIT. EXIT.

            }

        }
        catch (FatalErrorException fee) {

            LOG.info(fee.getMessage());

        }

        // 962 004870 MOVE GLGLBL-ACCOUNT-NBR
        // 963 004880 TO WS-ACCOUNT-NBR-HOLD.

        state.setAccountNumberHold(balance.getAccountNumber());

        // 945 004680 GO TO FETCH-CURSOR.
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
