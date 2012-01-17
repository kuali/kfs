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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.batch.service.impl.exception.FatalErrorException;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class helps generate the entries for the nominal activity closing year end job.
 */
public class NominalActivityClosingHelper {
    private Integer fiscalYear;
    private Date transactionDate;
    private String varNetExpenseObjectCode;
    private String varNetRevenueObjectCode;
    private String varFundBalanceObjectCode;
    private String varFundBalanceObjectTypeCode;
    private String[] expenseObjectCodeTypes;
    private ParameterService parameterService;
    private ConfigurationService configurationService;
    private FlexibleOffsetAccountService flexibleOffsetService;
    private Logger LOG = Logger.getLogger(NominalActivityClosingHelper.class);
    private int nonFatalErrorCount;
    
    /**
     * Constructs a NominalActivityClosingHelper
     * @param fiscalYear the fiscal year this job is being run for
     * @param transactionDate the transaction date that origin entries should hit the ledger
     * @param parameterService an implementation of the ParameterService
     * @param configurationService an implementation of the ConfigurationService
     */
    public NominalActivityClosingHelper(Integer fiscalYear, Date transactionDate, ParameterService parameterService, ConfigurationService configurationService) {
        this.fiscalYear = fiscalYear;
        this.transactionDate = transactionDate;
        this.parameterService = parameterService;
        this.configurationService = configurationService;
        this.flexibleOffsetService = SpringContext.getBean(FlexibleOffsetAccountService.class);
        this.nonFatalErrorCount = 0;
        
        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getExpenseObjectTypes(fiscalYear);
        expenseObjectCodeTypes = objectTypes.toArray(new String[0]);
        
        // 682 003690 DISPLAY "NET_EXP_OBJECT_CD" UPON ENVIRONMENT-NAME.
        // 683 003700 ACCEPT VAR-NET-EXP-OBJECT-CD FROM ENVIRONMENT-VALUE.

        varNetExpenseObjectCode = parameterService.getParameterValueAsString(NominalActivityClosingStep.class, "NET_EXPENSE_OBJECT_CODE");
        varNetRevenueObjectCode = parameterService.getParameterValueAsString(NominalActivityClosingStep.class, "NET_REVENUE_OBJECT_CODE");
        varFundBalanceObjectCode = parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FUND_BALANCE_OBJECT_CODE_PARM);
        varFundBalanceObjectTypeCode = parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FUND_BALANCE_OBJECT_TYPE_PARM);

    }
    
    /**
     * Generates an origin entry that will summarize close out of nominal items (income and expense)
     * @param balance the balance this activity closing entry needs to be created for
     * @param sequenceNumber the sequence number of the origin entry
     * @return an origin entry which will close out nominal activity on a balance
     * @throws FatalErrorException thrown if the given balance lacks an object type code
     */
    public OriginEntryFull generateActivityEntry(Balance balance, Integer sequenceNumber) throws FatalErrorException {
        // 847 005300 4100-WRITE-ACTIVITY.

        OriginEntryFull activityEntry = new OriginEntryFull();

        // 848 005310 MOVE SPACES TO GLEN-RECORD.
        // 849 005320 MOVE VAR-UNIV-FISCAL-YR
        // 850 005330 TO UNIV-FISCAL-YR OF GLEN-RECORD.

        activityEntry.setUniversityFiscalYear(fiscalYear);

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

        activityEntry.setFinancialDocumentTypeCode(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE));

        // 917 005980 MOVE 'MF'
        // 918 005990 TO FS-ORIGIN-CD OF GLEN-RECORD.

        activityEntry.setFinancialSystemOriginationCode(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));

        // 919 006000 STRING 'AC'
        // 920 006010 GLGLBL-ACCOUNT-NBR
        // 921 006020 RP-BLANK-LINE
        // 922 006030 DELIMITED BY SIZE
        // 923 006040 INTO FDOC-NBR OF GLEN-RECORD.
        activityEntry.setDocumentNumber(new StringBuffer(balance.getOption().getActualFinancialBalanceTypeCd()).append(balance.getAccountNumber()).toString());

        // 924 006050 MOVE WS-SEQ-NBR
        // 925 006060 TO TRN-ENTR-SEQ-NBR OF GLEN-RECORD.

        activityEntry.setTransactionLedgerEntrySequenceNumber(sequenceNumber);

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

            activityEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(configurationService.getPropertyValueAsString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_NOMINAL_EXPENSE), balance));

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

            activityEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(configurationService.getPropertyValueAsString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_NOMINAL_REVENUE), balance));

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

            nonFatalErrorCount += 1;

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

        activityEntry.setTransactionDate(transactionDate);

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

        return activityEntry;

    }
    
    /**
     * Genereates an origin entry to update a fund balance as a result of closing income and expense
     * @param balance the balance this offset needs to be created for
     * @param sequenceNumber the sequence number of the origin entry full
     * @return an origin entry which will offset the nominal closing activity
     * @throws FatalErrorException thrown if the given balance lacks an object type code
     */
    public OriginEntryFull generateOffset(Balance balance, Integer sequenceNumber) throws FatalErrorException {
        String debitCreditCode = null;
        
        // 969 006470 IF CAOTYP-FIN-OBJTYP-DBCR-CD = 'C' OR 'D'
        
        if (null == balance.getObjectTypeCode()) {
            throw new FatalErrorException(" BALANCE SELECTED FOR PROCESSING IS MISSING ITS OBJECT TYPE CODE ");

        }

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
        
        // 1068 007430 4200-WRITE-OFFSET.
        // 1069 007440 MOVE SPACES TO GLEN-RECORD.

        OriginEntryFull offsetEntry = new OriginEntryFull();

        // 1070 007450 MOVE VAR-UNIV-FISCAL-YR
        // 1071 007460 TO UNIV-FISCAL-YR OF GLEN-RECORD.

        offsetEntry.setUniversityFiscalYear(fiscalYear);

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

        offsetEntry.setFinancialDocumentTypeCode(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE));

        // 1090 007650 MOVE 'MF'
        // 1091 007660 TO FS-ORIGIN-CD OF GLEN-RECORD.

        offsetEntry.setFinancialSystemOriginationCode(parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));

        // 1092 007670 STRING 'AC'
        // 1093 007680 GLGLBL-ACCOUNT-NBR
        // 1094 007690 RP-BLANK-LINE
        // 1095 007700 DELIMITED BY SIZE
        // 1096 007710 INTO FDOC-NBR OF GLEN-RECORD.

        offsetEntry.setDocumentNumber(new StringBuffer(balance.getOption().getActualFinancialBalanceTypeCd()).append(balance.getAccountNumber()).toString());

        // 1097 007720 MOVE WS-SEQ-NBR
        // 1098 007730 TO TRN-ENTR-SEQ-NBR OF GLEN-RECORD.

        offsetEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceNumber.intValue()));

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

        offsetEntry.setTransactionLedgerEntryDescription(this.createTransactionLedgerEntryDescription(configurationService.getPropertyValueAsString(KFSKeyConstants.MSG_CLOSE_ENTRY_TO_FUND_BALANCE), balance));

        // 1110 007850 MOVE GLGLBL-ACLN-ANNL-BAL-AMT
        // 1111 007860 TO TRN-LDGR-ENTR-AMT OF GLEN-RECORD.

        offsetEntry.setTransactionLedgerEntryAmount(balance.getAccountLineAnnualBalanceAmount());

        // 1112 007870 MOVE WS-FIN-OBJTYP-DBCR-CD
        // 1113 007880 TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD.

        offsetEntry.setTransactionDebitCreditCode(debitCreditCode);

        // 1114 007890 MOVE VAR-TRANSACTION-DT
        // 1115 007900 TO TRANSACTION-DT OF GLEN-RECORD.

        offsetEntry.setTransactionDate(transactionDate);

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
        
        flexibleOffsetService.updateOffset(offsetEntry);
        
        return offsetEntry;
    }
    
    /**
     * Adds the job parameters used to generate the origin entries to the given map
     * @param nominalClosingJobParameters a map of batch job parameters to add nominal activity closing parameters to
     */
    public void addNominalClosingJobParameters(Map nominalClosingJobParameters) {
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.NET_EXP_OBJECT_CD, varNetExpenseObjectCode);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.NET_REV_OBJECT_CD, varNetRevenueObjectCode);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.FUND_BAL_OBJECT_CD, varFundBalanceObjectCode);
        nominalClosingJobParameters.put(GeneralLedgerConstants.ColumnNames.FUND_BAL_OBJ_TYP_CD, varFundBalanceObjectTypeCode);
    }
    
    /**
     * Generates the transaction ledger entry description for a given balance
     * 
     * @param descriptorIntro the introduction to the description
     * @param balance the balance the transaction description will refer to
     * @return the generated transaction ledger entry description
     */
    private String createTransactionLedgerEntryDescription(String descriptorIntro, Balance balance) {
        StringBuilder description = new StringBuilder();
        description.append(descriptorIntro.trim()).append(' ');
        return description.append(getSizedField(5, balance.getSubAccountNumber())).append("-").append(getSizedField(4, balance.getObjectCode())).append("-").append(getSizedField(3, balance.getSubObjectCode())).append("-").append(getSizedField(2, balance.getObjectTypeCode())).toString();
    }

    /**
     * Pads out a string so that it will be a certain length
     * 
     * @param size the size to pad to
     * @param value the String being padded
     * @return the padded String
     */
    private StringBuilder getSizedField(int size, String value) {
        StringBuilder fieldString = new StringBuilder();
        if (value != null) {
            fieldString.append(value);
            while (fieldString.length() < size) {
                fieldString.append(' ');
            }
        }
        else {
            while (fieldString.length() < size) {
                fieldString.append('-');
            }
        }
        return fieldString;
    }
    
    /**
     * Returns the count of non-fatal errors encountered during the process by this helper
     * @return the count of non-fatal errors
     */
    public Integer getNonFatalErrorCount() {
        return new Integer(this.nonFatalErrorCount);
    }
}
