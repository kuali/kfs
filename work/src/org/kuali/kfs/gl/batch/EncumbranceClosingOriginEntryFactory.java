/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.batch.closing.year.util;

import java.sql.Date;

import org.kuali.Constants;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.service.A21SubAccountService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.chart.service.SubObjectCodeService;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.util.OriginEntryOffsetPair;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id$
 */

public class EncumbranceClosingOriginEntryFactory {

    private static org.apache.log4j.Logger LOG = 
        org.apache.log4j.Logger.getLogger(EncumbranceClosingOriginEntryFactory.class);
    
    /**
     * @param encumbrance
     * @param debitCreditCode
     * @return a cost share entry/offset pair for the given encumbrance.
     */
    static final public OriginEntryOffsetPair createCostShareBeginningBalanceEntryOffsetPair(
            Encumbrance encumbrance, String debitCreditCode) {
        
        OriginEntryOffsetPair pair = new OriginEntryOffsetPair();
        
        // Generate the entry ...
        
        OriginEntry entry = new OriginEntry("ACLO", "MF");
        
//        1370  008180     MOVE 'FR-'
//        1371  008190          TO TRN-LDGR-ENTR-DESC OF GLEN-RECORD (29:3).
//        1372  008200     MOVE FIN-COA-CD OF GLEN-RECORD
//        1373  008210          TO TRN-LDGR-ENTR-DESC OF GLEN-RECORD (32:2).
//        1374  008220     MOVE ACCOUNT-NBR OF GLEN-RECORD
//        1375  008230          TO TRN-LDGR-ENTR-DESC OF GLEN-RECORD (34:7).
        
        String description = encumbrance.getTransactionEncumbranceDescription();
        description += "FR-" + encumbrance.getChartOfAccountsCode() + encumbrance.getAccountNumber();
        entry.setTransactionLedgerEntryDescription(description);
        
        // In the cobol code looking up the a21SubAccount fields happens between lines 1322 and 1366.
        // The logic that encloses that doesn't belong here. So check those lines to see it if you like.
        // But I won't include it here because the line numbers would get out of sync and possibly confuse people.
        
        A21SubAccountService a21SubAccountService = SpringServiceLocator.getA21SubAccountService();
        A21SubAccount a21SubAccount = 
            a21SubAccountService.getByPrimaryKey(
                    encumbrance.getChartOfAccountsCode(), 
                    encumbrance.getAccountNumber(), 
                    encumbrance.getSubAccountNumber());
        
//        1376  008240     MOVE CASA21-CST-SHR-COA-CD TO FIN-COA-CD OF GLEN-RECORD.
        
        entry.setChartOfAccountsCode(a21SubAccount.getCostShareChartOfAccountCode());
        
//        1377  008250     MOVE CASA21-CST-SHRSRCACCT-NBR TO ACCOUNT-NBR
//        1378  008260       OF GLEN-RECORD.
        
        entry.setAccountNumber(a21SubAccount.getCostShareSourceAccountNumber());
        
//        1379  008270     MOVE CASA21-CST-SRCSUBACCT-NBR TO SUB-ACCT-NBR
//        1380  008280       OF GLEN-RECORD.
        
        entry.setSubAccountNumber(a21SubAccount.getCostShareSourceSubAccountNumber());
        
//        1381  008290     IF SUB-ACCT-NBR OF GLEN-RECORD = SPACES
//        1382  008300         MOVE SUB-ACCT-NBR-DASHES
//        1383  008310         TO SUB-ACCT-NBR OF GLEN-RECORD.
        
        // The subAccountNumber is set to dashes in the OriginEntry constructor.
        
        if ("".equals(encumbrance.getSubAccountNumber().trim())) {
            
            entry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
            
        }
        
//        1384  008320     MOVE 'CE' TO FIN-BALANCE-TYP-CD OF GLEN-RECORD.
        
        entry.setFinancialBalanceTypeCode("CE");
        
//        1385  008330     MOVE FIN-SUB-OBJ-CD-DASHES
//        1386  008340       TO FIN-SUB-OBJ-CD OF GLEN-RECORD
        
        entry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
       
//        1387  008350     MOVE +0              TO TRN-ENTR-SEQ-NBR
//        1388  008360       OF GLEN-RECORD.
        
        entry.setTransactionLedgerEntrySequenceNumber(new Integer(0));
        
//        1389  008370     IF TRN-DEBIT-CRDT-CD OF GLEN-RECORD = SPACES
        
        if (null == debitCreditCode || "".equals(debitCreditCode.trim())) {
        
//        1390  008380      IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD > ZEROES
//        1391  008390        MOVE DEBIT
//        1392  008400          TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD.
            
            if(encumbrance.getAccountLineEncumbranceAmount().isPositive()) {
                
                entry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
        }

//        1393  008410     IF TRN-DEBIT-CRDT-CD OF GLEN-RECORD = SPACES

        if (null == debitCreditCode || "".equals(debitCreditCode.trim())) {
                
//        1394  008420      IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < ZEROES
        
            if(encumbrance.getAccountLineEncumbranceAmount().isNegative()) {
        
//        1395  008430        MOVE CREDIT
//        1396  008440          TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD
            
                entry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            
//        1397  008450          COMPUTE TRN-LDGR-ENTR-AMT OF GLEN-RECORD
//        1398  008460          = TRN-LDGR-ENTR-AMT * -1.
            
                entry.setTransactionLedgerEntryAmount(
                        encumbrance.getAccountLineEncumbranceAmount().negated());
                
            }
            
        }
        
//        1399  008470     MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-N
//        1400  008480                               WS-AMT-W-PERIOD.
//        1401  008490     MOVE WS-AMT-X TO TRN-AMT-RED-X.
        
        // TODO: Figure out what these two values are for.
        
//        1402  008500     WRITE GLE-DATA FROM GLEN-RECORD.
        
        // This is slightly different from the cobol in that the write happens outside of
        // this method in the calling code.
        
        pair.setEntry(entry);
        
        // And now the offset ...
        
        OriginEntry offset = new OriginEntry("ACLO", "MF");
        
//        1403  008510     MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        
        // TODO Figure out why this value is put into working storage and how it's used.
        
//        1404  008520     IF GLEDATA-STATUS > '09'
//        1405  008530        DISPLAY '**ERROR WRITING GLE DATA FILE'
//        1406  008540        DISPLAY '  STATUS IS ' GLEDATA-STATUS
//        1407  008550        MOVE 08 TO RETURN-CODE
//        1408  008560        STOP RUN.
        
        // TODO Writes are done in the calling code, so this should be replicated there.
        
//        1409  008570     ADD +1 TO SEQ-WRITE-COUNT.
        
        // This is done in the calling code.
        
//        1410             MOVE SEQ-WRITE-COUNT TO SEQ-CHECK-CNT.
//        1411             IF SEQ-CHECK-CNT (7:3) = '000'
//        1412                DISPLAY '  SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT.
        
        // TODO Pretty sure this is irrelevant. Double check that assumption.
        
//        1413  008580     MOVE 'GENERATED OFFSET'
//        1414  008590       TO TRN-LDGR-ENTR-DESC OF GLEN-RECORD.
        
        offset.setTransactionLedgerEntryDescription("GENERATED OFFSET");
        
//        1415  008600     MOVE UNIV-FISCAL-YR     OF GLEN-RECORD
//        1416  008610       TO GLOFSD-UNIV-FISCAL-YR.
//        1417  008620     MOVE FIN-COA-CD         OF GLEN-RECORD
//        1418  008630       TO GLOFSD-FIN-COA-CD.
//        1419  008640     MOVE FDOC-TYP-CD OF GLEN-RECORD
//        1420  008650       TO  GLOFSD-FDOC-TYP-CD
//        1421  008660     MOVE FIN-BALANCE-TYP-CD OF GLEN-RECORD
//        1422  008670       TO GLOFSD-FIN-BALANCE-TYP-CD.
//        1423  008680     EXEC SQL
//        1424  008690          SELECT FIN_OBJECT_CD,
//        1425  008700                 FIN_SUB_OBJ_CD
//        1426  008710          INTO   :GLOFSD-FIN-OBJECT-CD :GLOFSD-FOC-I,
//        1427  008720                 :GLOFSD-FIN-SUB-OBJ-CD :GLOFSD-FSOC-I
//        1428  008730          FROM   GL_OFFSET_DEFN_T
//        1429  008740          WHERE  UNIV_FISCAL_YR = RTRIM(:GLOFSD-UNIV-FISCAL-YR)
//        1430  008750            AND  FIN_COA_CD =    RTRIM(:GLOFSD-FIN-COA-CD)
//        1431  008760            AND  FDOC_TYP_CD =   RTRIM(:GLOFSD-FDOC-TYP-CD)
//        1432  008770            AND  FIN_BALANCE_TYP_CD
//        1433  008780                    = RTRIM(:GLOFSD-FIN-BALANCE-TYP-CD)
//        1434  008790     END-EXEC.
        
        OffsetDefinitionService offsetDefinitionService = SpringServiceLocator.getOffsetDefinitionService();
        OffsetDefinition offsetDefinition = 
            offsetDefinitionService.getByPrimaryId(
                entry.getUniversityFiscalYear(), entry.getChartOfAccountsCode(), 
                entry.getFinancialDocumentTypeCode(), entry.getFinancialBalanceTypeCode());
        
//        1435  008800     IF GLOFSD-FOC-I < ZERO
//        1436  008810        MOVE SPACE TO GLOFSD-FIN-OBJECT-CD.
//        1437  008830     IF GLOFSD-FSOC-I < ZERO
//        1438  008840        MOVE SPACE TO GLOFSD-FIN-SUB-OBJ-CD.
//        1439  008860     EVALUATE SQLCODE
//        1440  008870          WHEN 0
        
        if(null != offsetDefinition) {
        
//        1441  008880              MOVE GLOFSD-FIN-OBJECT-CD TO
//        1442  008890                   FIN-OBJECT-CD OF GLEN-RECORD
            
            offset.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            
//        1443  008900              IF GLOFSD-FIN-SUB-OBJ-CD = SPACES
            
            if(null == offsetDefinition.getFinancialSubObjectCode() 
                    || "".equals(offsetDefinition.getFinancialSubObjectCode().trim())) {
            
//        1444  008910                   MOVE FIN-SUB-OBJ-CD-DASHES
//        1445  008920                       TO FIN-SUB-OBJ-CD OF GLEN-RECORD
                
                offset.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
                
//        1446  008930              ELSE
                
            } else {
                
//        1447  008940                   MOVE GLOFSD-FIN-SUB-OBJ-CD TO
//        1448  008950                          FIN-SUB-OBJ-CD OF GLEN-RECORD
                
                offset.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());
                
//        1449  008960              END-IF
                
            }
            
        } else {
            
//        1450  008970          WHEN +100
//        1451  008980          WHEN +1403
//        1452  008990              MOVE GLEN-RECORD (1:51) TO PRINT-DATA
//        1453  009000              MOVE '    OFFSET DEFINITION NOT FOUND'
//        1454  009010                  TO PRINT-DATA (52:32)
//        1455  009020              WRITE PRINT-DATA
//        1456  009030             PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
//        1457  009040              MOVE SPACES TO DCLGL-OFFSET-DEFN-T
//        1458  009050          WHEN OTHER
//        1459  009060              DISPLAY ' ERROR ACCESSING OFSD TABLE '
//        1460  009070                      'SQL CODE IS ' SQLCODE
//        1461  009080              MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        1462  009090               GO TO 4000-WRITE-OUTPUT-EXIT
            
            LOG.info("FATAL ERROR: One of the following errors occurred (no way to know exactly which):\n\t"
                    + "- OFFSET DEFINITION NOT FOUND\n\t"
                    + "- ERROR ACCESSING OFSD TABLE");
            
            pair.setFatalErrorFlag(true);
            
            return pair;
            
//        1463  009100     END-EVALUATE.
            
        }
        
//        1464  009110     MOVE UNIV-FISCAL-YR OF GLEN-RECORD
//        1465  009120       TO CAOBJT-UNIV-FISCAL-YR
//        1466  009130     MOVE FIN-COA-CD     OF GLEN-RECORD
//        1467  009140       TO CAOBJT-FIN-COA-CD
//        1468  009150     MOVE FIN-OBJECT-CD  OF GLEN-RECORD
//        1469  009160       TO CAOBJT-FIN-OBJECT-CD
//        1470  009170     EXEC SQL
//        1471  009180       SELECT    FIN_OBJ_TYP_CD,
//        1472  009190                 FIN_OBJ_SUB_TYP_CD,
//        1473  009200                 FIN_OBJ_ACTIVE_CD,
//        1474  009210                 FOBJ_MNXFR_ELIM_CD
//        1475  009220       INTO      :CAOBJT-FIN-OBJ-TYP-CD :CAOBJT-FOTC-I,
//        1476  009230                 :CAOBJT-FIN-OBJ-SUB-TYP-CD :CAOBJT-FOSTC-I,
//        1477  009240                 :CAOBJT-FIN-OBJ-ACTIVE-CD :CAOBJT-FOAC-I,
//        1478  009250                 :CAOBJT-FOBJ-MNXFR-ELIM-CD :CAOBJT-FMEC-I
//        1479  009260       FROM      CA_OBJECT_CODE_T
//        1480  009270       WHERE     UNIV_FISCAL_YR=  RTRIM(:CAOBJT-UNIV-FISCAL-YR)
//        1481  009280         AND     FIN_COA_CD=      RTRIM(:CAOBJT-FIN-COA-CD)
//        1482  009290         AND     FIN_OBJECT_CD=   RTRIM(:CAOBJT-FIN-OBJECT-CD)
//        1483  009300     END-EXEC.
        
        ObjectCodeService objectCodeService = SpringServiceLocator.getObjectCodeService();
        ObjectCode objectCodeBo = objectCodeService.getByPrimaryId(entry.getUniversityFiscalYear(),
                entry.getChartOfAccountsCode(), entry.getFinancialObjectCode());
        
//        1484  009310     IF CAOBJT-FOTC-I < ZERO
//        1485  009320        MOVE SPACE TO CAOBJT-FIN-OBJ-TYP-CD.
//        1486  009340     IF CAOBJT-FOSTC-I < ZERO
//        1487  009350        MOVE SPACE TO CAOBJT-FIN-OBJ-SUB-TYP-CD.
//        1488  009370     IF CAOBJT-FOAC-I < ZERO
//        1489  009380        MOVE SPACE TO CAOBJT-FIN-OBJ-ACTIVE-CD.
//        1490  009400     IF CAOBJT-FMEC-I < ZERO
//        1491  009410        MOVE SPACE TO CAOBJT-FOBJ-MNXFR-ELIM-CD.
//        1492  009430        EVALUATE SQLCODE
//        1493  009440           WHEN 0
        
        if(null != objectCodeBo) {
            
//        1494  009450            MOVE CAOBJT-FIN-OBJ-TYP-CD TO FIN-OBJ-TYP-CD
//        1495  009460              OF GLEN-RECORD
            
            offset.setFinancialObjectTypeCode(objectCodeBo.getFinancialObjectTypeCode());
            
        } else {
        
//        1496  009470           WHEN +100
//        1497  009480           WHEN +1403
//        1498  009490              MOVE GLEN-RECORD (1:51) TO PRINT-DATA
//        1499  009500              MOVE CAOBJT-FIN-OBJECT-CD TO PRINT-DATA (52:4)
//        1500  009510              MOVE 'NO OBJECT FOR OBJECT ON OFSD'
//        1501  009520                   TO PRINT-DATA (56:28)
//        1502  009530              WRITE PRINT-DATA
//        1503  009540             PERFORM CK-PRINT-STATUS THRU CK-PRINT-STATUS-EXIT
//        1504  009550              MOVE SPACES TO DCLCA-OBJECT-CODE-T
//        1505  009560           WHEN OTHER
//        1506  009570               DISPLAY 'ERROR ACCESSING OBJECT TABLE'
//        1507  009580                       ' SQL CODE IS ' SQLCODE
//        1508  009590               MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        1509  009600               GO TO 4000-WRITE-OUTPUT-EXIT
//        1510  009610        END-EVALUATE.
            
            LOG.info("FATAL ERROR: One of the following errors occurred (no way to know exactly which):\n\t"
                    + "- NO OBJECT FOR OBJECT ON OFSD\n\t"
                    + "- ERROR ACCESSING OBJECT TABLE");
            
            pair.setFatalErrorFlag(true);
            
            return pair;
            
        }
        
//        1511  009620     IF TRN-DEBIT-CRDT-CD OF GLEN-RECORD = CREDIT
//        1512  009630        MOVE DEBIT
//        1513  009640          TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD
//        1514  009650     ELSE
//        1515  009660        MOVE CREDIT
//        1516  009670          TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD.
        
        if(Constants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
            
            offset.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            
        } else {
            
            offset.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            
        }
        
//        1517  009680     MOVE SPACES TO TRN-ENCUM-UPDT-CD OF GLEN-RECORD.
        
        offset.setTransactionEncumbranceUpdateCode(null);
        
//        1518  009690     MOVE SPACES TO ORG-DOC-NBR OF GLEN-RECORD.
        
        offset.setOrganizationDocumentNumber(null);
        
//        1519  009700     MOVE '----------' TO PROJECT-CD OF GLEN-RECORD.
        
        offset.setProjectCode(Constants.DASHES_PROJECT_CODE);
        
//        1520  009710     MOVE SPACES TO ORG-REFERENCE-ID OF GLEN-RECORD.
        
        offset.setOrganizationReferenceId(null);
        
//        1521  009720     MOVE SPACES TO FDOC-REF-TYP-CD OF GLEN-RECORD.
        
        offset.setReferenceFinancialDocumentTypeCode(null);
        
//        1522  009730     MOVE SPACES TO FS-REF-ORIGIN-CD OF GLEN-RECORD.
        
        offset.setReferenceFinancialSystemOriginationCode(null);
        
//        1523  009740     MOVE SPACES TO FDOC-REF-NBR OF GLEN-RECORD.
        
        offset.setReferenceFinancialDocumentNumber(null);
        
//        1524  009750     MOVE SPACES TO FDOC-REVERSAL-DT OF GLEN-RECORD.

        offset.setReversalDate(null);
        
//        1525  009760     MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-N
//        1526  009770                               WS-AMT-W-PERIOD.
//        1527  009780     MOVE WS-AMT-X TO TRN-AMT-RED-X.
        
        // TODO: Figure out what these two values are for.
        
//        1528  009790     WRITE GLE-DATA FROM GLEN-RECORD.
        
        pair.setOffset(offset);
        
//        1529  009800     MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
//        1530  009810     IF GLEDATA-STATUS > '09'
//        1531  009820        DISPLAY '**ERROR WRITING GLE DATA FILE '
//        1532  009830        DISPLAY '  STATUS IS ' GLEDATA-STATUS
//        1533  009840        MOVE 08 TO RETURN-CODE
//        1534  009850        STOP RUN.
//        1535  009860     ADD +1 TO SEQ-WRITE-COUNT.
//        1536             MOVE SEQ-WRITE-COUNT TO SEQ-CHECK-CNT.
//        1537             IF SEQ-CHECK-CNT (7:3) = '000'
//        1538                DISPLAY '  SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT.        

        return pair;
        
    }
    
    /**
     * @param encumbrance
     * @param closingFiscalYear
     * @param transactionDate
     * @return a entry/offset pair for the given encumbrance
     */
    static final public OriginEntryOffsetPair createBeginningBalanceEntryOffsetPair(
            Encumbrance encumbrance, Integer closingFiscalYear, Date transactionDate) {
        
        OriginEntryOffsetPair pair = new OriginEntryOffsetPair();
        
        // Build the entry ...
        OriginEntry entry = new OriginEntry("ACLO", "MF");
        
//        1107  005500     MOVE SPACES TO GLEN-RECORD.
//        1108  005510     MOVE WS-UNIV-FISCAL-YR-PLUS-1
//        1109  005520       TO UNIV-FISCAL-YR                 OF GLEN-RECORD.

        Integer thisFiscalYear = new Integer(closingFiscalYear.intValue() + 1);
        entry.setUniversityFiscalYear(thisFiscalYear);
        
//        1110  005530     MOVE GLGLEC-FIN-COA-CD
//        1111  005540       TO FIN-COA-CD                     OF GLEN-RECORD.
        
        entry.setChartOfAccountsCode(encumbrance.getChartOfAccountsCode());
        
//        1112  005550     MOVE GLGLEC-ACCOUNT-NBR
//        1113  005560       TO ACCOUNT-NBR                    OF GLEN-RECORD.
        
        entry.setAccountNumber(encumbrance.getAccountNumber());
        
//        1114  005570     MOVE GLGLEC-SUB-ACCT-NBR
//        1115  005580       TO SUB-ACCT-NBR                   OF GLEN-RECORD.
        
        entry.setSubAccountNumber(encumbrance.getSubAccountNumber());
        
//        1116  005590     MOVE GLGLEC-UNIV-FISCAL-YR
//        1117  005600       TO CAOBJT-UNIV-FISCAL-YR.
//        1118  005610     MOVE GLGLEC-FIN-COA-CD
//        1119  005620       TO CAOBJT-FIN-COA-CD.
//        1120  005630     MOVE GLGLEC-FIN-OBJECT-CD
//        1121  005640       TO CAOBJT-FIN-OBJECT-CD.
//        1122  005650         EXEC SQL
//        1123  005660              SELECT FIN_OBJ_TYP_CD,
//        1124  005670                     NXT_YR_FIN_OBJ_CD
//        1125  005680              INTO   :CAOBJT-FIN-OBJ-TYP-CD :CAOBJT-FOTC-I,
//        1126  005690                     :CAOBJT-NXT-YR-FIN-OBJ-CD :CAOBJT-NYFOC-I
//        1127  005700              FROM   CA_OBJECT_CODE_T
//        1128  005710           WHERE UNIV_FISCAL_YR = RTRIM(:CAOBJT-UNIV-FISCAL-YR)
//        1129  005720            AND   FIN_COA_CD     = RTRIM(:CAOBJT-FIN-COA-CD)
//        1130  005730            AND   FIN_OBJECT_CD  = RTRIM(:CAOBJT-FIN-OBJECT-CD)
//        1131  005740         END-EXEC.
        
        ObjectCodeService objectCodeService = SpringServiceLocator.getObjectCodeService();
        ObjectCode objectCode = objectCodeService.getByPrimaryId(encumbrance.getUniversityFiscalYear(),
                encumbrance.getChartOfAccountsCode(), encumbrance.getObjectCode());

//        1132  005750         IF CAOBJT-FOTC-I < ZERO
//        1133  005760            MOVE SPACE TO CAOBJT-FIN-OBJ-TYP-CD.
//        1134  005780         IF CAOBJT-NYFOC-I < ZERO
//        1135  005790            MOVE SPACE TO CAOBJT-NXT-YR-FIN-OBJ-CD.
//        1136  005810         EVALUATE SQLCODE
//        1137  005820            WHEN 0
        
        if(null != objectCode) {
            
//        1138  005830              MOVE CAOBJT-FIN-OBJ-TYP-CD
//        1139  005840                   TO FIN-OBJ-TYP-CD OF GLEN-RECORD
            
            entry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

//        1140  005850              IF CAOBJT-NXT-YR-FIN-OBJ-CD NOT = SPACES
            
            if(null != objectCode.getNextYearFinancialObjectCode()
                    && !"".equals(objectCode.getNextYearFinancialObjectCode().trim())) {
            
//        1141  005860                 MOVE CAOBJT-NXT-YR-FIN-OBJ-CD
//        1142  005870                   TO FIN-OBJECT-CD OF GLEN-RECORD
                
                entry.setFinancialObjectCode(objectCode.getNextYearFinancialObjectCode());
                
//        1143  005880                ELSE
                
            } else {
                
//        1144  005890                 MOVE GLGLEC-FIN-OBJECT-CD
//        1145  005900                   TO FIN-OBJECT-CD OF GLEN-RECORD
                
                entry.setFinancialObjectCode(encumbrance.getObjectCode());
                
//        1146  005910              END-IF
                
            }
            
        
//        1147  005920            WHEN OTHER
            
        } else {
            
//        1148  005930              DISPLAY 'ERROR ACCESSING OBJECT TABLE FOR '
//        1149  005940                   CAOBJT-FIN-OBJECT-CD
            
            LOG.info("FATAL ERROR: ERROR ACCESSING OBJECT TABLE FOR CAOBJT-FIN-OBJECT-CD");
            
//        1150  005950              MOVE 'Y' TO WS-FATAL-ERROR-FLAG
            
            pair.setFatalErrorFlag(true);
            
//        1151  005960              GO TO 4000-WRITE-OUTPUT-EXIT
            
            return pair;
            
//        1152  005970         END-EVALUATE.
            
        }
        
//        1153  005980     MOVE GLGLEC-UNIV-FISCAL-YR
//        1154  005990       TO CASOBJ-UNIV-FISCAL-YR.
//        1155  006000     MOVE GLGLEC-FIN-COA-CD
//        1156  006010       TO CASOBJ-FIN-COA-CD.
//        1157  006020     MOVE GLGLEC-ACCOUNT-NBR
//        1158  006030       TO CASOBJ-ACCOUNT-NBR.
//        1159  006040     MOVE GLGLEC-FIN-OBJECT-CD
//        1160  006050       TO CASOBJ-FIN-OBJECT-CD.
//        1161  006060     MOVE GLGLEC-FIN-SUB-OBJ-CD
//        1162  006070       TO CASOBJ-FIN-SUB-OBJ-CD.
//        1163  006080         EXEC SQL
//        1164  006090          SELECT FIN_SUB_OBJ_CD
//        1165  006100          INTO   :CASOBJ-FIN-SUB-OBJ-CD :CASOBJ-FSOC-I
//        1166  006110          FROM   CA_SUB_OBJECT_CD_T
//        1167  006120          WHERE  UNIV_FISCAL_YR = RTRIM(:CASOBJ-UNIV-FISCAL-YR)
//        1168  006130           AND    FIN_COA_CD     = RTRIM(:CASOBJ-FIN-COA-CD)
//        1169  006140           AND  ACCOUNT_NBR      = RTRIM(:CASOBJ-ACCOUNT-NBR)
//        1170  006150           AND  FIN_OBJECT_CD    = RTRIM(:CASOBJ-FIN-OBJECT-CD)
//        1171  006160           AND  FIN_SUB_OBJ_CD   = RTRIM(:CASOBJ-FIN-SUB-OBJ-CD)
//        1172  006170         END-EXEC.
        
        SubObjectCodeService subObjectCodeService = SpringServiceLocator.getSubObjectCodeService();
        SubObjCd subObjectCode = 
            subObjectCodeService.getByPrimaryId(encumbrance.getUniversityFiscalYear(),
                encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), 
                encumbrance.getObjectCode(), encumbrance.getSubObjectCode());
        
//        1173  006180         IF CASOBJ-FSOC-I < ZERO
//        1174  006190            MOVE SPACE TO CASOBJ-FIN-SUB-OBJ-CD.
//        1175  006210         EVALUATE SQLCODE
//        1176  006220            WHEN 0
        
        if (null != subObjectCode) {
        
//        1177  006230              MOVE CASOBJ-FIN-SUB-OBJ-CD
//        1178  006240                TO FIN-SUB-OBJ-CD OF GLEN-RECORD
            
            entry.setFinancialSubObjectCode(subObjectCode.getFinancialSubObjectCode());
            
//        1179  006250            WHEN OTHER
            
        } else {
            
//        1180  006260              MOVE '---'
//        1181  006270                TO FIN-SUB-OBJ-CD OF GLEN-RECORD
            
            entry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            
//        1182  006280         END-EVALUATE.
            
        }
        
//        1183  006290     MOVE GLGLEC-FIN-BALANCE-TYP-CD
//        1184  006300       TO FIN-BALANCE-TYP-CD             OF GLEN-RECORD.
        
        entry.setFinancialBalanceTypeCode(encumbrance.getBalanceTypeCode());

//        1185  006310     MOVE 'BB'
//        1186  006320       TO UNIV-FISCAL-PRD-CD             OF GLEN-RECORD.
        
        entry.setUniversityFiscalPeriodCode("BB");
        
//        1187  006330     MOVE GLGLEC-FDOC-TYP-CD
//        1188  006340       TO FDOC-TYP-CD                    OF GLEN-RECORD.
        
        entry.setFinancialDocumentTypeCode(encumbrance.getDocumentTypeCode());
        
//        1189  006350     MOVE GLGLEC-FS-ORIGIN-CD
//        1190  006360       TO FS-ORIGIN-CD                   OF GLEN-RECORD.
        
        entry.setFinancialSystemOriginationCode(encumbrance.getOriginCode());
        
//        1191  006370     MOVE GLGLEC-FDOC-NBR
//        1192  006380       TO FDOC-NBR                       OF GLEN-RECORD.

        entry.setFinancialDocumentNumber(encumbrance.getDocumentNumber());

//        1193  006390     MOVE 1
//        1194  006400       TO TRN-ENTR-SEQ-NBR               OF GLEN-RECORD.
        
        entry.setTransactionLedgerEntrySequenceNumber(new Integer(1));
        
//        1195  006410     MOVE GLGLEC-TRN-ENCUM-DESC
//        1196  006420       TO TRN-LDGR-ENTR-DESC             OF GLEN-RECORD.
        
        entry.setTransactionLedgerEntryDescription(
                encumbrance.getTransactionEncumbranceDescription());
        
//        1197  006430     SUBTRACT GLGLEC-ACLN-ENCUM-CLS-AMT
//        1198  006440         FROM GLGLEC-ACLN-ENCUM-AMT
//        1199  006450       GIVING TRN-LDGR-ENTR-AMT          OF GLEN-RECORD.
        
        entry.setTransactionLedgerEntryAmount(
                encumbrance.getAccountLineEncumbranceAmount().subtract(
                        encumbrance.getAccountLineEncumbranceClosedAmount()));
        
//        1200  006460     IF TRN-LDGR-ENTR-AMT OF GLEN-RECORD < 0
        
        if(entry.getTransactionLedgerEntryAmount().isNegative()) {
        
//        1201  006470        COMPUTE TRN-LDGR-ENTR-AMT = TRN-LDGR-ENTR-AMT * -1
            
            entry.setTransactionLedgerEntryAmount(
                    entry.getTransactionLedgerEntryAmount().negated());
            
//        1202  006480        MOVE 'C' TO TRN-DEBIT-CRDT-CD OF GLEN-RECORD
            
            entry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            
//        1203  006490       ELSE
            
        } else {
            
//        1204  006500     MOVE 'D'
//        1205  006510       TO TRN-DEBIT-CRDT-CD              OF GLEN-RECORD.
            
            entry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            
        }
        
//        1206  006520     MOVE VAR-TRANSACTION-DT
//        1207  006530       TO TRANSACTION-DT                 OF GLEN-RECORD.
        
        entry.setTransactionDate(transactionDate);
        
//        1208  006540     MOVE SPACES
//        1209  006550       TO ORG-DOC-NBR                    OF GLEN-RECORD.
        
        entry.setOrganizationDocumentNumber(null);
        
//        1210  006560     MOVE ALL '-'
//        1211  006570       TO PROJECT-CD                     OF GLEN-RECORD.
        
        entry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        
//        1212  006580     MOVE SPACES
//        1213  006590       TO ORG-REFERENCE-ID               OF GLEN-RECORD.
        
        entry.setOrganizationReferenceId(null);
        
//        1214  006600     MOVE SPACES
//        1215  006610       TO FDOC-REF-TYP-CD                OF GLEN-RECORD.
        
        entry.setReferenceFinancialDocumentTypeCode(null);
        
//        1216  006620     MOVE SPACES
//        1217  006630       TO FS-REF-ORIGIN-CD               OF GLEN-RECORD.
        
        entry.setReferenceFinancialSystemOriginationCode(null);
        
//        1218  006640     MOVE SPACES
//        1219  006650       TO FDOC-REF-NBR                   OF GLEN-RECORD.
        
        entry.setReferenceFinancialDocumentNumber(null);
        
//        1220  006660     MOVE SPACES
//        1221  006670       TO FDOC-REVERSAL-DT               OF GLEN-RECORD.
        
        entry.setReversalDate(null);
        
//        1222  006680     MOVE 'D'
//        1223  006690       TO TRN-ENCUM-UPDT-CD              OF GLEN-RECORD.
        
        entry.setTransactionEncumbranceUpdateCode(Constants.GL_DEBIT_CODE);
        
//        1224  006700     MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-N
//        1225  006710                               WS-AMT-W-PERIOD.
//        1226  006720     MOVE WS-AMT-X TO TRN-AMT-RED-X.
        
        // TODO Figure out why these two statements are here.
        
//        1227  006730     WRITE GLE-DATA FROM GLEN-RECORD.
        
        pair.setEntry(entry);
        
        // And now build the offset.
        
        OriginEntry offset = new OriginEntry(entry);
        
//        1228  006740     MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
        
        offset.setTransactionLedgerEntryAmount(entry.getTransactionLedgerEntryAmount());
        
//        1229  006750     IF GLEDATA-STATUS > '09'
//        1230  006760        DISPLAY '**ERROR WRITING GLEDATA'
//        1231  006770        DISPLAY ' STATUS IS ' GLEDATA-STATUS
//        1232  006780        MOVE 08 TO RETURN-CODE
//        1233  006790        STOP RUN.
//        1234  006810     MOVE GLEN-RECORD TO WS-HOLD-GLEN.
//        1235  006820     ADD +1 TO SEQ-WRITE-COUNT.
//        1236             MOVE SEQ-WRITE-COUNT TO SEQ-CHECK-CNT.
//        1237             IF SEQ-CHECK-CNT (7:3) = '000'
//        1238                DISPLAY '  SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT.
//        1239  006830     MOVE '9891' TO FIN-OBJECT-CD.
        
        offset.setFinancialObjectCode("9891");
        
//        1240  006840     IF FIN-BALANCE-TYP-CD = 'PE'
        
        if ("PE".equals(entry.getFinancialBalanceTypeCode())) {
            
//        1241  006850         MOVE '9890' TO FIN-OBJECT-CD.
            
            offset.setFinancialObjectCode("9890");
            
//        1242  006860     IF FIN-BALANCE-TYP-CD = 'EX'
            
        } else if ("EX".equals(entry.getFinancialBalanceTypeCode())) {
            
//        1243  006870         MOVE '9892' TO FIN-OBJECT-CD.
            
            offset.setFinancialObjectCode("9892");
            
        }
        
//        1244  006880     MOVE 'FB' TO FIN-OBJ-TYP-CD.
        
        offset.setFinancialObjectTypeCode("FB");
        
//        1245  006890     MOVE 'BEGINNING FUND BALANCE OFFSET' TO
//        1246  006900         TRN-LDGR-ENTR-DESC.
        
        offset.setTransactionLedgerEntryDescription("BEGINNING FUND BALANCE OFFSET");
        
//        1247  006910     IF TRN-DEBIT-CRDT-CD = 'D'
        
        if(Constants.GL_DEBIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
        
//        1248  006920        MOVE 'C' TO TRN-DEBIT-CRDT-CD
            
            offset.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            
//        1249  006930       ELSE
            
        } else {
            
//        1250  006940        MOVE 'D' TO TRN-DEBIT-CRDT-CD.
            
            offset.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            
        }
        
//        1251  006950     MOVE TRN-LDGR-ENTR-AMT TO WS-AMT-N
//        1252  006960                               WS-AMT-W-PERIOD.
//        1253  006970     MOVE WS-AMT-X TO TRN-AMT-RED-X.
        
        // TODO Figure out why these two statements are here.
        
//        1254  006980     WRITE GLE-DATA FROM GLEN-RECORD.
        
        pair.setOffset(offset);
        
//        1255  006990     MOVE WS-AMT-N TO TRN-LDGR-ENTR-AMT.
//        1256  007000     IF GLEDATA-STATUS > '09'
//        1257  007010        DISPLAY '**ERROR WRITING GLEDATA '
//        1258  007020        DISPLAY '  STATUS IS ' GLEDATA-STATUS
//        1259  007030        MOVE 08 TO RETURN-CODE
//        1260  007040        STOP RUN.
//        1261  007060     ADD +1 TO SEQ-WRITE-COUNT.        
        
        return pair;
        
    }
    
}
