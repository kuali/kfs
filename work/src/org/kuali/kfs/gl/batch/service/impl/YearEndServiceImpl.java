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
package org.kuali.module.gl.batch.closing.year.service.impl;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.closing.year.service.YearEndService;
import org.kuali.module.gl.batch.closing.year.service.impl.helper.BalanceForwardRuleHelper;
import org.kuali.module.gl.batch.closing.year.service.impl.helper.EncumbranceClosingRuleHelper;
import org.kuali.module.gl.batch.closing.year.util.EncumbranceClosingOriginEntryFactory;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.EncumbranceDao;
import org.kuali.module.gl.dao.YearEndDao;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.util.FatalErrorException;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.util.OriginEntryOffsetPair;
import org.kuali.module.gl.util.Summary;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class owns the logic to perform year end tasks.
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
    private KualiConfigurationService kualiConfigurationService;
    private PriorYearAccountService priorYearAccountService;
    private SubFundGroupService subFundGroupService;
    private EncumbranceClosingRuleHelper encumbranceClosingRuleHelper;
    private ReportService reportService;
    private PersistenceService persistenceService;
    private YearEndDao yearEndDao;
    
    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";
    
    public YearEndServiceImpl() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.batch.closing.year.service.YearEndService#closeNominalActivity()
     */
    public void closeNominalActivity(OriginEntryGroup nominalClosingOriginEntryGroup, Map nominalClosingJobParameters, Map<String, Integer> nominalClosingCounts) {

        // Do some preliminary setup, getting application parameters.

        String varNetExpenseObjectCode = null;
        String varNetRevenueObjectCode = null;
        String varFundBalanceObjectCode = null;
        String varFundBalanceObjectTypeCode = null;
        Integer varFiscalYear = (Integer)nominalClosingJobParameters.get(GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR);
        Date varTransactionDate = (Date)nominalClosingJobParameters.get(GLConstants.ColumnNames.UNIV_DT);

        // 678 003650 DISPLAY "UNIV_FISCAL_YR" UPON ENVIRONMENT-NAME.
        // 679 003660 ACCEPT VAR-UNIV-FISCAL-YR FROM ENVIRONMENT-VALUE.

        varFiscalYear = (Integer)nominalClosingJobParameters.get(GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR);
        
        ObjectTypeService objectTypeService = (ObjectTypeService)SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getBasicExpenseObjectTypes(varFiscalYear);
        objectTypes.add( objectTypeService.getExpenseTransferObjectType(varFiscalYear));
        String[] expenseObjectCodeTypes = objectTypes.toArray(new String[0]);

        // 682 003690 DISPLAY "NET_EXP_OBJECT_CD" UPON ENVIRONMENT-NAME.
        // 683 003700 ACCEPT VAR-NET-EXP-OBJECT-CD FROM ENVIRONMENT-VALUE.

        varNetExpenseObjectCode = kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, GLConstants.Components.NOMINAL_ACTIVITY_CLOSING_STEP, "NET_EXPENSE_OBJECT_CODE");
        varNetRevenueObjectCode = kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, GLConstants.Components.NOMINAL_ACTIVITY_CLOSING_STEP, "NET_REVENUE_OBJECT_CODE");
        varFundBalanceObjectCode = kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, GLConstants.ANNUAL_CLOSING_FUND_BALANCE_OBJECT_CODE_PARM);
        varFundBalanceObjectTypeCode = kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, GLConstants.ANNUAL_CLOSING_FUND_BALANCE_OBJECT_TYPE_PARM);

        nominalClosingJobParameters.put(GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR, varFiscalYear);
        nominalClosingJobParameters.put(GLConstants.ColumnNames.NET_EXP_OBJECT_CD, varNetExpenseObjectCode);
        nominalClosingJobParameters.put(GLConstants.ColumnNames.NET_REV_OBJECT_CD, varNetRevenueObjectCode);
        nominalClosingJobParameters.put(GLConstants.ColumnNames.FUND_BAL_OBJECT_CD, varFundBalanceObjectCode);
        nominalClosingJobParameters.put(GLConstants.ColumnNames.FUND_BAL_OBJ_TYP_CD, varFundBalanceObjectTypeCode);

        nominalClosingCounts.put("globalReadCount", new Integer(balanceService.countBalancesForFiscalYear(varFiscalYear)));
        Iterator<Balance> balanceIterator = balanceService.findNominalActivityBalancesForFiscalYear(varFiscalYear);

        String accountNumberHold = null;

        boolean selectSw = false;
        
        nominalClosingCounts.put("globalSelectCount", new Integer(0));
        nominalClosingCounts.put("sequenceNumber", new Integer(0));
        nominalClosingCounts.put("sequenceWriteCount", new Integer(0));
        nominalClosingCounts.put("sequenceCheckCount", new Integer(0));
        nominalClosingCounts.put("nonFatalCount", new Integer(0));

        boolean nonFatalErrorFlag = false;

        while (balanceIterator.hasNext()) {

            Balance balance = balanceIterator.next();
            String actualFinancial = balance.getOption().getActualFinancialBalanceTypeCd();

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

                // 847 005300 4100-WRITE-ACTIVITY.

                OriginEntryFull activityEntry = new OriginEntryFull();

                // 848 005310 MOVE SPACES TO GLEN-RECORD.
                // 849 005320 MOVE VAR-UNIV-FISCAL-YR
                // 850 005330 TO UNIV-FISCAL-YR OF GLEN-RECORD.

                activityEntry.setUniversityFiscalYear(varFiscalYear);

                // 851 005340 MOVE GLGLBL-FIN-COA-CD
                // 852 005350 TO FIN-COA-CD OF GLEN-RECORD.

                activityEntry.setChartOfAccountsCode(balance.getChartOfAccountsCode());

                // 853 005360 MOVE GLGLBL-ACCOUNT-NBR
                // 854 005370 TO ACCOUNT-NBR OF GLEN-RECORD.

                activityEntry.setAccountNumber(balance.getAccountNumber());

                // 855 005380 MOVE GLGLBL-SUB-ACCT-NBR
                // 856 005390 TO SUB-ACCT-NBR OF GLEN-RECORD.

                activityEntry.setSubAccountNumber(balance.getSubAccountNumber());

                // 857 005400* FOR OBJECT TYPES = 'ES' OR 'EX' OR 'EE' USE THE NEXT EXPENSE
                // 858 005410* OBJECT FROM THE INPUT VAR. ELSE USE THE NET REVENUE OBJECT
                // 859 005420* FROM THE INPUT VAR.

                // 860 005430 IF GLGLBL-FIN-OBJ-TYP-CD = 'ES' OR 'EX' OR 'EE'
                // 861 005440 OR 'TE'

                if (ObjectHelper.isOneOf(balance.getObjectTypeCode(), expenseObjectCodeTypes)) {

                    // 862 005450 MOVE VAR-NET-EXP-OBJECT-CD
                    // 863 005460 TO FIN-OBJECT-CD OF GLEN-RECORD

                    activityEntry.setFinancialObjectCode(varNetExpenseObjectCode);

                    // 864 005470 ELSE

                }
                else {

                    // 865 005480 MOVE VAR-NET-REV-OBJECT-CD
                    // 866 005490 TO FIN-OBJECT-CD OF GLEN-RECORD.

                    activityEntry.setFinancialObjectCode(varNetRevenueObjectCode);

                }

                activityEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                activityEntry.setFinancialBalanceTypeCode(balance.getOption().getNominalFinancialBalanceTypeCd());

                if (null == balance.getObjectTypeCode()) {
                    throw new FatalErrorException(" BALANCE SELECTED FOR PROCESSING IS MISSING ITS OBJECT TYPE CODE ");

                }

                activityEntry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

                // 913 005940 MOVE '13'
                // 914 005950 TO UNIV-FISCAL-PRD-CD OF GLEN-RECORD.

                activityEntry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);

                // 915 005960 MOVE 'ACLO'
                // 916 005970 TO FDOC-TYP-CD OF GLEN-RECORD.

                activityEntry.setFinancialDocumentTypeCode(kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE));

                // 917 005980 MOVE 'MF'
                // 918 005990 TO FS-ORIGIN-CD OF GLEN-RECORD.

                activityEntry.setFinancialSystemOriginationCode(kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));

                // 919 006000 STRING 'AC'
                // 920 006010 GLGLBL-ACCOUNT-NBR
                // 921 006020 RP-BLANK-LINE
                // 922 006030 DELIMITED BY SIZE
                // 923 006040 INTO FDOC-NBR OF GLEN-RECORD.

                activityEntry.setDocumentNumber(new StringBuffer(actualFinancial).append(balance.getAccountNumber()).toString());

                // 924 006050 MOVE WS-SEQ-NBR
                // 925 006060 TO TRN-ENTR-SEQ-NBR OF GLEN-RECORD.

                activityEntry.setTransactionLedgerEntrySequenceNumber(nominalClosingCounts.get("sequenceNumber"));

                // 926 006070 IF GLGLBL-FIN-OBJ-TYP-CD = 'EX' OR 'ES' OR 'EE'
                // 927 006080 OR 'TE'

                if (ObjectHelper.isOneOf(balance.getObjectTypeCode(), expenseObjectCodeTypes)) {

                    // 928 006090 STRING 'CLS ENT TO NE FOR '
                    // 929 006100 GLGLBL-SUB-ACCT-NBR
                    // 930 006110 '-'
                    // 931 006120 GLGLBL-FIN-OBJECT-CD
                    // 932 006130 '-'
                    // 933 006140 GLGLBL-FIN-SUB-OBJ-CD
                    // 934 006150 '-'
                    // 935 006160 GLGLBL-FIN-OBJ-TYP-CD
                    // 936 006170 RP-BLANK-LINE
                    // 937 006180 DELIMITED BY SIZE INTO
                    // 938 006190 TRN-LDGR-ENTR-DESC OF GLEN-RECORD

                    activityEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_NOMINAL_EXPENSE), balance));

                    // 939 006200 ELSE

                }
                else {

                    // 940 006210 STRING 'CLS ENT TO NR FOR '
                    // 941 006220 GLGLBL-SUB-ACCT-NBR
                    // 942 006230 '-'
                    // 943 006240 GLGLBL-FIN-OBJECT-CD
                    // 944 006250 '-'
                    // 945 006260 GLGLBL-FIN-SUB-OBJ-CD
                    // 946 006270 '-'
                    // 947 006280 GLGLBL-FIN-OBJ-TYP-CD
                    // 948 006290 RP-BLANK-LINE
                    // 949 006300 DELIMITED BY SIZE INTO
                    // 950 006310 TRN-LDGR-ENTR-DESC OF GLEN-RECORD.

                    activityEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_NOMINAL_REVENUE), balance));

                }

                // 951 006320 MOVE GLGLBL-ACLN-ANNL-BAL-AMT
                // 952 006330 TO TRN-LDGR-ENTR-AMT OF GLEN-RECORD.

                activityEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount());

                // 953 006340 MOVE GLGLBL-FIN-OBJ-TYP-CD
                // 954 006350 TO CAOTYP-FIN-OBJ-TYP-CD.

                activityEntry.setFinancialObjectTypeCode(balance.getObjectTypeCode());

                // 955 006360 EXEC SQL
                // 956 006370 SELECT FIN_OBJTYP_DBCR_CD
                // 957 006380 INTO :CAOTYP-FIN-OBJTYP-DBCR-CD :CAOTYP-FODC-I
                // 958 006390 FROM CA_OBJ_TYPE_T
                // 959 006400 WHERE FIN_OBJ_TYP_CD = RTRIM(:CAOTYP-FIN-OBJ-TYP-CD)
                // 960 006410 END-EXEC.
                // 961 IF RETURN-CODE NOT = ZERO
                // 962 DISPLAY ' RETURN CODE 6410 ' RETURN-CODE.
                // 963
                // 964 006420 IF CAOTYP-FODC-I < ZERO
                // 965 006430 MOVE SPACE TO CAOTYP-FIN-OBJTYP-DBCR-CD
                // 966 006440 END-IF
                // 967 006450 EVALUATE SQLCODE
                // 968 006460 WHEN 0

                String debitCreditCode = null;

                if (null != balance.getObjectType()) {

                    // 969 006470 IF CAOTYP-FIN-OBJTYP-DBCR-CD = 'C' OR 'D'

                    if (ObjectHelper.isOneOf(balance.getObjectType().getFinObjectTypeDebitcreditCd(), new String[] { KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE })) {

                        // 970 006480 MOVE CAOTYP-FIN-OBJTYP-DBCR-CD
                        // 971 006490 TO WS-FIN-OBJTYP-DBCR-CD

                        debitCreditCode = balance.getObjectType().getFinObjectTypeDebitcreditCd();

                        // 972 006500 ELSE

                    }
                    else {

                        // 973 006510 MOVE 'C' TO WS-FIN-OBJTYP-DBCR-CD

                        debitCreditCode = KFSConstants.GL_CREDIT_CODE;

                        // 974 006520 END-IF

                    }

                    // 975 006530 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < ZERO

                    // NOTE (laran) The amount on the origin entry is set to this value above.
                    // NOTE (laran) I'm using the balance value here because to me it was easier than remembering the
                    // indirection.
                    if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {

                        // 976 006540 IF CAOTYP-FIN-OBJTYP-DBCR-CD = 'C' OR 'D'

                        if (ObjectHelper.isOneOf(balance.getObjectType().getFinObjectTypeDebitcreditCd(), new String[] { KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE })) {

                            // 977 006550 MOVE CAOTYP-FIN-OBJTYP-DBCR-CD
                            // 978 006560 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                            activityEntry.setTransactionDebitCreditCode(balance.getObjectType().getFinObjectTypeDebitcreditCd());

                            // 979 006570 ELSE

                        }
                        else {

                            // 980 006580 MOVE 'C' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                            activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

                            // 981 006590 MOVE LIT-KEY TO RP-ERROR-LABEL OF RP-ERROR-LINE-1

                            // TODO figure out what this bit re: the LIT-KEY means.

                            // 982 006600 MOVE CAOBJT-FIN-OBJ-TYP-CD TO
                            // 983 006610 RP-ERROR-MSG OF RP-ERROR-LINE-1
                            // 984 006620 MOVE RP-ERROR-LINE-1 TO PRINT-DATA
                            // 985 006630 WRITE PRINT-DATA

                            // TODO figure out how to handle this little flourish of error handling code here.

                        }

                        // 986 006640 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
                        // 987 006650 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG
                        // 988 006660 ADD +1 TO NON-FATAL-COUNT
                        // 989 006670 ELSE

                    }
                    else {

                        // 990 006680 IF CAOTYP-FIN-OBJTYP-DBCR-CD = 'C'

                        if (KFSConstants.GL_CREDIT_CODE.equals(balance.getObjectType().getFinObjectTypeDebitcreditCd())) {

                            // 991 006690 MOVE 'D' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                            activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

                            // 992 006700 ELSE

                        }
                        else {

                            // 993 006710 IF CAOTYP-FIN-OBJTYP-DBCR-CD = 'D'

                            if (KFSConstants.GL_DEBIT_CODE.equals(balance.getObjectType().getFinObjectTypeDebitcreditCd())) {

                                // 994 006720 MOVE 'C' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                                activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

                                // 995 006730 ELSE

                            }
                            else {

                                // 996 006740 MOVE 'D' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                                activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

                                // 997 006750 MOVE LIT-KEY TO RP-ERROR-LABEL OF RP-ERROR-LINE-1

                                // TODO figure out what this bit re: the LIT-KEY means.

                                // 998 006760 MOVE CAOBJT-FIN-OBJ-TYP-CD TO
                                // 999 006770 RP-ERROR-MSG OF RP-ERROR-LINE-1
                                // 1000 006780 MOVE RP-ERROR-LINE-1 TO PRINT-DATA
                                // 1001 006790 WRITE PRINT-DATA

                                // TODO figure out how to handle this little flourish of error handling code here.

                            }

                            // 1002 006800 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
                            // 1003 006810 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG
                            // 1004 006820 ADD +1 TO NON-FATAL-COUNT

                            // TODO figure out how to handle this CK-PRINT-STATUS/non-fatal error business

                            // 1005 006830 END-IF

                        }

                        // 1006 006840 END-IF

                    }

                    // 1007 006850 END-IF

                    // 1008 006860 WHEN +100
                    // 1009 006870 WHEN +1403

                }
                else {

                    // 1010 006880 MOVE 'C' TO WS-FIN-OBJTYP-DBCR-CD

                    // 1011 006890 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < ZERO

                    if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {

                        // 1012 006900 MOVE 'C' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                        activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

                        // 1013 006910 ELSE

                    }
                    else {

                        // 1014 006920 MOVE 'D' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                        activityEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

                        // 1015 006930 END-IF

                    }

                    // 1016 006940 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG

                    nonFatalErrorFlag = true;

                    // 1017 006950 ADD +1 TO NON-FATAL-COUNT

                    incrementCount(nominalClosingCounts, "nonFatalCount");

                    // 1018 006960 MOVE 'FIN OBJ TYP CODE ' TO PRINT-DATA
                    // 1019 006970 MOVE CAOTYP-FIN-OBJ-TYP-CD TO PRINT-DATA (19:2)
                    // 1020 006980 MOVE ' NOT IN TABLE' TO PRINT-DATA (22:13)
                    // 1021 006990 WRITE PRINT-DATA

                    LOG.info(new StringBuffer("FIN OBJ TYP CODE ").append(balance.getObjectTypeCode()).append(" NOT IN TABLE").toString());

                    // 1022 007000 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT

                    // TODO work out this non-fatal-error handling bit here

                    // 1023 007010 WHEN OTHER
                    // 1024 007020 DISPLAY ' ERROR ACCESSING OBJECT TYPE TABLE FOR '
                    // 1025 007030 CAOTYP-FIN-OBJ-TYP-CD
                    // 1026 007040 MOVE 'Y' TO WS-FATAL-ERROR-FLAG
                    // 1027 007050 GO TO 4100-WRITE-ACTIVITY-EXIT
                    // 1028 007060 END-EVALUATE.

                }

                // 1029 007070 MOVE VAR-TRANSACTION-DT
                // 1030 007080 TO TRANSACTION-DT OF GLEN-RECORD.

                activityEntry.setTransactionDate(varTransactionDate);

                // 1031 007090 MOVE SPACES
                // 1032 007100 TO ORG-DOC-NBR OF GLEN-RECORD.

                activityEntry.setOrganizationDocumentNumber(null);

                // 1033 007110 MOVE ALL '-'
                // 1034 007120 TO PROJECT-CD OF GLEN-RECORD.

                activityEntry.setProjectCode(KFSConstants.getDashProjectCode());

                // 1035 007130 MOVE SPACES
                // 1036 007140 TO ORG-REFERENCE-ID OF GLEN-RECORD.

                activityEntry.setOrganizationReferenceId(null);

                // 1037 007150 MOVE SPACES
                // 1038 007160 TO FDOC-REF-TYP-CD OF GLEN-RECORD.

                activityEntry.setReferenceFinancialDocumentTypeCode(null);

                // 1039 007170 MOVE SPACES
                // 1040 007180 TO FS-REF-ORIGIN-CD OF GLEN-RECORD.

                activityEntry.setReferenceFinancialSystemOriginationCode(null);

                // 1041 007190 MOVE SPACES
                // 1042 007200 TO FDOC-REF-NBR OF GLEN-RECORD.

                activityEntry.setReferenceFinancialDocumentNumber(null);

                // 1043 007210 MOVE SPACES
                // 1044 007220 TO FDOC-REVERSAL-DT OF GLEN-RECORD.

                activityEntry.setReversalDate(null);

                // 1045 007230 MOVE SPACES
                // 1046 007240 TO TRN-ENCUM-UPDT-CD OF GLEN-RECORD.

                activityEntry.setTransactionEncumbranceUpdateCode(null);

                // 1047 007250 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < 0

                if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {

                    // 1048 007260 COMPUTE
                    // 1049 007270 TRN-LDGR-ENTR-AMT OF GLEN-RECORD =
                    // 1050 007280 TRN-LDGR-ENTR-AMT OF GLEN-RECORD * -1.

                    activityEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount().negated());

                }

                // 1051 007290 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-N
                // 1052 007300 WS-AMT-W-PERIOD.
                // 1053 007310 MOVE WS-AMT-X TO TRN-AMT-RED-X.
                // 1054 007320 WRITE GLE-DATA FROM GLEN-RECORD.

                originEntryService.createEntry(activityEntry, nominalClosingOriginEntryGroup);

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

                // 1068 007430 4200-WRITE-OFFSET.
                // 1069 007440 MOVE SPACES TO GLEN-RECORD.

                OriginEntryFull offsetEntry = new OriginEntryFull();

                // 1070 007450 MOVE VAR-UNIV-FISCAL-YR
                // 1071 007460 TO UNIV-FISCAL-YR OF GLEN-RECORD.

                offsetEntry.setUniversityFiscalYear(varFiscalYear);

                // 1072 007470 MOVE GLGLBL-FIN-COA-CD
                // 1073 007480 TO FIN-COA-CD OF GLEN-RECORD.

                offsetEntry.setChartOfAccountsCode(balance.getChartOfAccountsCode());

                // 1074 007490 MOVE GLGLBL-ACCOUNT-NBR
                // 1075 007500 TO ACCOUNT-NBR OF GLEN-RECORD.

                offsetEntry.setAccountNumber(balance.getAccountNumber());

                // 1076 007510 MOVE GLGLBL-SUB-ACCT-NBR
                // 1077 007520 TO SUB-ACCT-NBR OF GLEN-RECORD.

                offsetEntry.setSubAccountNumber(balance.getSubAccountNumber());

                // 1078 007530 MOVE VAR-FUND-BAL-OBJECT-CD
                // 1079 007540 TO FIN-OBJECT-CD OF GLEN-RECORD.

                offsetEntry.setFinancialObjectCode(varFundBalanceObjectCode);

                // 1080 007550 MOVE ALL '-'
                // 1081 007560 TO FIN-SUB-OBJ-CD OF GLEN-RECORD.

                offsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

                // 1082 007570 MOVE 'NB'
                // 1083 007580 TO FIN-BALANCE-TYP-CD OF GLEN-RECORD.

                offsetEntry.setFinancialBalanceTypeCode(balance.getOption().getNominalFinancialBalanceTypeCd());

                // 1084 007590 MOVE VAR-FUND-BAL-OBJ-TYP-CD
                // 1085 007600 TO FIN-OBJ-TYP-CD OF GLEN-RECORD.

                offsetEntry.setFinancialObjectTypeCode(varFundBalanceObjectTypeCode);

                // 1086 007610 MOVE '13'
                // 1087 007620 TO UNIV-FISCAL-PRD-CD OF GLEN-RECORD.

                offsetEntry.setUniversityFiscalPeriodCode(KFSConstants.MONTH13);

                // 1088 007630 MOVE 'ACLO'
                // 1089 007640 TO FDOC-TYP-CD OF GLEN-RECORD.

                offsetEntry.setFinancialDocumentTypeCode(kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE));

                // 1090 007650 MOVE 'MF'
                // 1091 007660 TO FS-ORIGIN-CD OF GLEN-RECORD.

                offsetEntry.setFinancialSystemOriginationCode(kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));

                // 1092 007670 STRING 'AC'
                // 1093 007680 GLGLBL-ACCOUNT-NBR
                // 1094 007690 RP-BLANK-LINE
                // 1095 007700 DELIMITED BY SIZE
                // 1096 007710 INTO FDOC-NBR OF GLEN-RECORD.

                offsetEntry.setDocumentNumber(new StringBuffer(actualFinancial).append(balance.getAccountNumber()).toString());

                // 1097 007720 MOVE WS-SEQ-NBR
                // 1098 007730 TO TRN-ENTR-SEQ-NBR OF GLEN-RECORD.

                offsetEntry.setTransactionLedgerEntrySequenceNumber(new Integer(nominalClosingCounts.get("sequenceNumber").intValue()));

                // 1099 007740 STRING 'CLS ENT TO FB FOR '
                // 1100 007750 GLGLBL-SUB-ACCT-NBR
                // 1101 007760 '-'
                // 1102 007770 GLGLBL-FIN-OBJECT-CD
                // 1103 007780 '-'
                // 1104 007790 GLGLBL-FIN-SUB-OBJ-CD
                // 1105 007800 '-'
                // 1106 007810 GLGLBL-FIN-OBJ-TYP-CD
                // 1107 007820 RP-BLANK-LINE
                // 1108 007830 DELIMITED BY SIZE INTO
                // 1109 007840 TRN-LDGR-ENTR-DESC OF GLEN-RECORD.

                offsetEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_FUND_BALANCE), balance));

                // 1110 007850 MOVE GLGLBL-ACLN-ANNL-BAL-AMT
                // 1111 007860 TO TRN-LDGR-ENTR-AMT OF GLEN-RECORD.

                offsetEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount());

                // 1112 007870 MOVE WS-FIN-OBJTYP-DBCR-CD
                // 1113 007880 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD.

                offsetEntry.setTransactionDebitCreditCode(debitCreditCode);

                // 1114 007890 MOVE VAR-TRANSACTION-DT
                // 1115 007900 TO TRANSACTION-DT OF GLEN-RECORD.

                offsetEntry.setTransactionDate(varTransactionDate);

                // 1116 007910 MOVE SPACES
                // 1117 007920 TO ORG-DOC-NBR OF GLEN-RECORD.

                offsetEntry.setOrganizationDocumentNumber(null);

                // 1118 007930 MOVE ALL '-'
                // 1119 007940 TO PROJECT-CD OF GLEN-RECORD.

                offsetEntry.setProjectCode(KFSConstants.getDashProjectCode());

                // 1120 007950 MOVE SPACES
                // 1121 007960 TO ORG-REFERENCE-ID OF GLEN-RECORD.

                offsetEntry.setOrganizationReferenceId(null);

                // 1122 007970 MOVE SPACES
                // 1123 007980 TO FDOC-REF-TYP-CD OF GLEN-RECORD.

                offsetEntry.setReferenceFinancialDocumentTypeCode(null);

                // 1124 007990 MOVE SPACES
                // 1125 008000 TO FS-REF-ORIGIN-CD OF GLEN-RECORD.

                offsetEntry.setReferenceFinancialSystemOriginationCode(null);

                // 1126 008010 MOVE SPACES
                // 1127 008020 TO FDOC-REF-NBR OF GLEN-RECORD.

                offsetEntry.setReferenceFinancialDocumentNumber(null);

                // 1128 008030 MOVE SPACES
                // 1129 008040 TO FDOC-REVERSAL-DT OF GLEN-RECORD.

                offsetEntry.setFinancialDocumentReversalDate(null);

                // 1130 008050 MOVE SPACES
                // 1131 008060 TO TRN-ENCUM-UPDT-CD OF GLEN-RECORD.

                offsetEntry.setTransactionEncumbranceUpdateCode(null);

                // 1132 008070 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < 0

                if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {

                    // 1133 008080 IF TRN-DEBIT-CRDT-CD OF GLEN-RECORD = 'C'

                    if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {

                        // 1134 008090 MOVE 'D' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD

                        offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);

                        // 1135 008100 ELSE

                    }
                    else {

                        // 1136 008110 MOVE 'C' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD.

                        offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);

                    }

                }

                // 1137 008120 IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < 0

                if (balance.getAccountLineAnnualBalanceAmount().isNegative()) {

                    // 1138 008130 COMPUTE
                    // 1139 008140 TRN-LDGR-ENTR-AMT OF GLEN-RECORD =
                    // 1140 008150 TRN-LDGR-ENTR-AMT OF GLEN-RECORD * -1.

                    offsetEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount().negated());

                }

                // 1141 008160 MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-N
                // 1142 008170 WS-AMT-W-PERIOD.
                // 1143 008180 MOVE WS-AMT-X TO TRN-AMT-RED-X.

                // 1144 008190 WRITE GLE-DATA FROM GLEN-RECORD.

                originEntryService.createEntry(offsetEntry, nominalClosingOriginEntryGroup);

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

        // 784 004670 2000-DRIVER-EXIT.
        // 785 004680 EXIT.

    }

    /**
     * This method determines if the balance should be included.
     * 
     * @param balance
     * @return
     */
    public boolean selectBalanceForClosingOfNominalActivity(Balance balance) {

        if (null == balance)
            return false;

        // 806 004890 3000-SELECT-CRITERIA.
        // 807 004900* SEE IF RECORD PASSES THE SELECTION CRITERIA
        // 808 004910 IF (GLGLBL-FIN-BALANCE-TYP-CD = 'AC')
        // 809 004920 AND
        // 810 004930 (GLGLBL-FIN-OBJ-TYP-CD = 'ES' OR 'EX'
        // 811 004940 OR 'TE' OR 'TI'
        // 812 004950 OR 'EE' OR 'CH' OR 'IC' OR 'IN')

        String actualFinancial = balance.getOption().getActualFinancialBalanceTypeCd();
        if (actualFinancial.equals(balance.getBalanceTypeCode()) && (objectTypeService.getNominalActivityClosingAllowedObjectTypes(balance.getUniversityFiscalYear()).contains(balance.getObjectTypeCode()))) {

            // 813 004960 NEXT SENTENCE

            // continue

            // 814 004970 ELSE

        }
        else {

            // 815 004980 MOVE 'N' TO WS-SELECT-SW
            // 816 004990 GO TO 3000-SELECT-CRITERIA-EXIT.

            return false;

        }

        // 817 005000 IF GLGLBL-ACLN-ANNL-BAL-AMT NUMERIC

        if (null != balance.getAccountLineAnnualBalanceAmount()) {

            // 818 005010 IF GLGLBL-ACLN-ANNL-BAL-AMT = ZERO

            if (balance.getAccountLineAnnualBalanceAmount().isZero()) {

                // 819 005020 MOVE 'N' TO WS-SELECT-SW
                // 820 005030 GO TO 3000-SELECT-CRITERIA-EXIT

                return false;

                // 821 005040 ELSE

            }
            else {

                // 822 005050 NEXT SENTENCE

                // continue

            }

            // 823 005060 ELSE

        }
        else {

            // 824 005070 MOVE +1 TO LINES-TO-PRINT
            // 825 005080 PERFORM 8500-CHECK-NEW-PAGE
            // 826 005090 THRU 8500-CHECK-NEW-PAGE-EXIT
            // 827 005100 MOVE LIT-KEY TO RP-ERROR-LABEL OF RP-ERROR-LINE-1
            // 828 005110 MOVE DCLGL-BALANCE-T (1:29)
            // 829 005120 TO RP-ERROR-MSG OF RP-ERROR-LINE-1
            // 830 005130 MOVE RP-ERROR-LINE-1 TO PRINT-DATA
            // 831 005140 MOVE 'Y' TO WS-NON-FATAL-ERROR-FLAG
            // 832 005150 ADD +1 TO NON-FATAL-COUNT
            // 833 005160 WRITE PRINT-DATA
            // 834 005170 PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
            // 835 005180 ADD +1 TO LINE-COUNT
            // 836 005190 MOVE 'N' TO WS-SELECT-SW
            // 837 005200 GO TO 3000-SELECT-CRITERIA-EXIT.

            return false;

        }

        return true;

    }

    /**
     * @see org.kuali.module.gl.batch.closing.year.service.YearEndService#generateCloseNominalActivityReports(org.kuali.module.gl.bo.OriginEntryGroup, java.util.Map, java.util.Map)
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

    private String createTransactionLedgerEntryDescription(String descriptorIntro, Balance balance) {
        StringBuilder description = new StringBuilder();
        description.append(descriptorIntro.trim()).append(' ');
        return description.append(getSizedField(5, balance.getSubAccountNumber())).append("-").append(getSizedField(4, balance.getObjectCode())).append("-").append(getSizedField(3, balance.getSubObjectCode())).append("-").append(getSizedField(2, balance.getObjectTypeCode())).toString();
    }
    
    private StringBuilder getSizedField(int size, String value) {
        StringBuilder fieldString = new StringBuilder();
        if (value != null) {
            fieldString.append(value);
            while (fieldString.length() < size) {
                fieldString.append(' ');
            }
        } else {
            while (fieldString.length() < size) {
                fieldString.append('-');
            }
        }
        return fieldString;
    } 
    
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
     */
    public void forwardBalances(OriginEntryGroup balanceForwardsUnclosedPriorYearAccountGroup, OriginEntryGroup balanceForwardsClosedPriorYearAccountGroup, BalanceForwardRuleHelper balanceForwardRuleHelper) {
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
        
        // do the general forwards
        Iterator<Balance> generalBalances = balanceService.findGeneralBalancesToForwardForFiscalYear(balanceForwardRuleHelper.getClosingFiscalYear());
        while (generalBalances.hasNext()) {
            balance = generalBalances.next();
            balanceForwardRuleHelper.processGeneralForwardBalance(balance, balanceForwardsClosedPriorYearAccountGroup, balanceForwardsUnclosedPriorYearAccountGroup);
            if (balanceForwardRuleHelper.getState().getGlobalSelectCount() % 1000 == 0) {
                persistenceService.clearCache();
            }
        }
        
        // do the cumulative forwards
        Iterator<Balance> cumulativeBalances = balanceService.findCumulativeBalancesToForwardForFiscalYear(balanceForwardRuleHelper.getClosingFiscalYear());
        while (cumulativeBalances.hasNext()) {
            balance = cumulativeBalances.next();
            balanceForwardRuleHelper.processCumulativeForwardBalance(balance, balanceForwardsClosedPriorYearAccountGroup, balanceForwardsUnclosedPriorYearAccountGroup);
            if (balanceForwardRuleHelper.getState().getGlobalSelectCount() % 1000 == 0) {
                persistenceService.clearCache();
            }
        }
        
    }
    
    /**
     * @see org.kuali.module.gl.batch.closing.year.service.YearEndService#generateForwardBalanceReports(org.kuali.module.gl.bo.OriginEntryGroup, org.kuali.module.gl.bo.OriginEntryGroup, org.kuali.module.gl.batch.closing.year.service.impl.helper.BalanceForwardRuleHelper)
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
     */
    public void forwardEncumbrances(OriginEntryGroup originEntryGroup, Map jobParameters, Map<String, Integer> counts) {
        LOG.debug("forwardEncumbrances() started");

        // counters for the report
        counts.put("encumbrancesRead", new Integer(0));
        counts.put("encumbrancesSelected", new Integer(0));
        counts.put("originEntriesWritten", new Integer(0));

        // encumbranceDao will return all encumbrances for the fiscal year sorted properly by all of the appropriate keys.
        Iterator encumbranceIterator = encumbranceDao.getEncumbrancesToClose((Integer)jobParameters.get(GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR));
        while (encumbranceIterator.hasNext()) {

            Encumbrance encumbrance = (Encumbrance) encumbranceIterator.next();
            incrementCount(counts, "encumbrancesRead");

            // if the encumbrance is not completely relieved
            if (encumbranceClosingRuleHelper.anEntryShouldBeCreatedForThisEncumbrance(encumbrance)) {

                incrementCount(counts, "encumbrancesSelected");

                // build a pair of origin entries to carry forward the encumbrance.
                OriginEntryOffsetPair beginningBalanceEntryPair = EncumbranceClosingOriginEntryFactory.createBeginningBalanceEntryOffsetPair(encumbrance, (Integer)jobParameters.get(GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR), (Date)jobParameters.get(GLConstants.ColumnNames.UNIV_DT));

                if (beginningBalanceEntryPair.isFatalErrorFlag()) {

                    continue;

                }
                else {

                    beginningBalanceEntryPair.getEntry().setGroup(originEntryGroup);
                    beginningBalanceEntryPair.getOffset().setGroup(originEntryGroup);

                    // save the entries.

                    originEntryService.createEntry(beginningBalanceEntryPair.getEntry(), originEntryGroup);
                    originEntryService.createEntry(beginningBalanceEntryPair.getOffset(), originEntryGroup);
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

                    OriginEntryOffsetPair costShareBeginningBalanceEntryPair = EncumbranceClosingOriginEntryFactory.createCostShareBeginningBalanceEntryOffsetPair(encumbrance, beginningBalanceEntryPair.getEntry().getTransactionDebitCreditCode());

                    if (!costShareBeginningBalanceEntryPair.isFatalErrorFlag()) {

                        costShareBeginningBalanceEntryPair.getEntry().setGroup(originEntryGroup);
                        costShareBeginningBalanceEntryPair.getOffset().setGroup(originEntryGroup);

                        // save the cost share entries.

                        originEntryService.createEntry(costShareBeginningBalanceEntryPair.getEntry(), originEntryGroup);
                        originEntryService.createEntry(costShareBeginningBalanceEntryPair.getOffset(), originEntryGroup);
                        incrementCount(counts, "originEntriesWritten");
                        incrementCount(counts, "originEntriesWritten");

                    }
                }
            }
            
            if (counts.get("encumbrancesSelected").intValue() % 1000 == 0) {
                persistenceService.clearCache();
            }
        }   
    }

    /**
     * @see org.kuali.module.gl.batch.closing.year.service.YearEndService#generateForwardEncumbrancesReports(org.kuali.module.gl.bo.OriginEntryGroup, java.util.Map, java.util.Map)
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
     * @see org.kuali.module.gl.batch.closing.year.service.YearEndService#logAllMissingPriorYearAccounts(java.lang.Integer)
     */
    public void logAllMissingPriorYearAccounts(Integer fiscalYear) {
        Set<Map<String, String>> missingPriorYearAccountKeys = yearEndDao.findKeysOfMissingPriorYearAccountsForBalances(fiscalYear);
        missingPriorYearAccountKeys.addAll(yearEndDao.findKeysOfMissingPriorYearAccountsForOpenEncumbrances(fiscalYear));
        for (Map<String, String> key: missingPriorYearAccountKeys) {
            LOG.info("PRIOR YEAR ACCOUNT MISSING FOR "+key.get("chartOfAccountsCode")+"-"+key.get("accountNumber"));
        }
    }

    /**
     * @see org.kuali.module.gl.batch.closing.year.service.YearEndService#logAllMissingSubFundGroups(java.lang.Integer)
     */
    public void logAllMissingSubFundGroups(Integer fiscalYear) {
        Set missingSubFundGroupKeys = yearEndDao.findKeysOfMissingSubFundGroupsForBalances(fiscalYear);
        missingSubFundGroupKeys.addAll(yearEndDao.findKeysOfMissingSubFundGroupsForOpenEncumbrances(fiscalYear));
        for (Object key: missingSubFundGroupKeys) {
            LOG.info("SUB FUND GROUP MISSING FOR "+(String)((Map)key).get("subFundGroupCode"));
        }
        
    }

    public void setEncumbranceDao(EncumbranceDao encumbranceDao) {
        this.encumbranceDao = encumbranceDao;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

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

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

    public void setSubFundGroupService(SubFundGroupService subFundGroupService) {
        this.subFundGroupService = subFundGroupService;
    }

    /**
     * Sets the persistenceService attribute value.
     * @param persistenceService The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }
    
    public void setYearEndDao(YearEndDao yearEndDao) {
        this.yearEndDao = yearEndDao;
    }
    
}
