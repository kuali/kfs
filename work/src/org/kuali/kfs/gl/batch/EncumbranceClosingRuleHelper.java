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
package org.kuali.module.gl.batch.closing.year.service.impl.helper;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.PriorYearAccount;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.A21SubAccountService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.util.FatalErrorException;
import org.kuali.module.gl.util.ObjectHelper;

/**
 * A helper class which contains the more complicated logic involved in the year end encumbrance
 * closing process. This logic is likely going to need to be modular which is why I put it in its
 * own class.
 * 
 * @author Kuali General Ledger Team (kualigltech@oncourse.iu.edu)
 * @version $Id$
 */

public class EncumbranceClosingRuleHelper {

    static private org.apache.log4j.Logger LOG = 
        org.apache.log4j.Logger.getLogger(EncumbranceClosingRuleHelper.class);

    static private String[] EXPENSE_OBJECT_CODE_TYPES = 
        new String[] {"EE", "EX", "ES", "TE"};
    
    static private String[] EXTERNAL_INTERNAL_AND_PRE_ENCUMBRANCE_BALANCE_TYPE_CODES
        = new String[] { "IE", "EX", "PE"};
    
    private A21SubAccountService a21SubAccountService;
    private PriorYearAccountService priorYearAccountService;
    private SubFundGroupService subFundGroupService;
    
    public void setA21SubAccountService(A21SubAccountService a21SubAccountService) {
        this.a21SubAccountService = a21SubAccountService;
    }
    
    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }
    
    public void setSubFundGroupService(SubFundGroupService subFundGroupService) {
        this.subFundGroupService = subFundGroupService;
    }
    
    /**
     * @param encumbrance
     * @return true if the encumbrance should be rolled forward from the closing fiscal year
     *         to the opening fiscal year.
     */
    public boolean anEntryShouldBeCreatedForThisEncumbrance(Encumbrance encumbrance) {
        
        if(null == encumbrance) {
            
            return false;
            
        }
        
//        1033  004770 3000-SELECT-CRITERIA.
//        1034  004780     IF GLGLEC-FIN-BALANCE-TYP-CD = 'IE'
//        1035  004790        AND GLGLEC-FS-ORIGIN-CD = 'LD'
        
        if("IE".equals(encumbrance.getBalanceTypeCode())
                && "LD".equals(encumbrance.getOriginCode())) {
        
//        1036  004800        MOVE 'N' TO WS-SELECT-SW
//        1037  004810        GO TO 3000-SELECT-CRITERIA-EXIT.
            
            return false;

        }
        
//        1038  004820     IF GLGLEC-FIN-BALANCE-TYP-CD = 'CE'
        
        if("CE".equals(encumbrance.getBalanceTypeCode())) {
        
//        1039  004830        MOVE 'N' TO WS-SELECT-SW
//        1040  004840        GO TO 3000-SELECT-CRITERIA-EXIT.
            
            return false;
            
        }
        
//        1041  004850     IF GLGLEC-FIN-BALANCE-TYP-CD = 'EX' OR 'IE'
        
        if(ObjectHelper.isOneOf(encumbrance.getBalanceTypeCode(), new String[] {"EX", "IE"})) {
        
//        1042  004860        GO TO CHECK-AMT.
            
            return isEncumbranceClosed(encumbrance);
            
        }
        
//        1043  004870     IF GLGLEC-FIN-BALANCE-TYP-CD = 'PE'
        
        if("PE".equals(encumbrance.getBalanceTypeCode())) {
            
//        1044  004880        MOVE GLGLEC-FIN-COA-CD
//        1045  004890          TO CAPYACTT-FIN-COA-CD
//        1046  004900        MOVE GLGLEC-ACCOUNT-NBR
//        1047  004910          TO CAPYACTT-ACCOUNT-NBR
//        1048  004920     EXEC SQL
//        1049  004930          SELECT   SUB_FUND_GRP_CD
//        1050  004940          INTO     :CAPYACTT-SUB-FUND-GRP-CD :CAPYACTT-SFGC-I
//        1051  004950          FROM     CA_PRIOR_YR_ACCT_T
//        1052  004960          WHERE FIN_COA_CD  = RTRIM(:CAPYACTT-FIN-COA-CD)
//        1053  004970          AND   ACCOUNT_NBR = RTRIM(:CAPYACTT-ACCOUNT-NBR)
//        1054  004980     END-EXEC
            
            PriorYearAccount priorYearAccount = 
                priorYearAccountService.getByPrimaryKey(
                        encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber());
            
//        1055  004990     IF CAPYACTT-SFGC-I < ZERO
//        1056  005000        MOVE SPACE TO CAPYACTT-SUB-FUND-GRP-CD
//        1057  005010     END-IF
//        1058  005020     EVALUATE SQLCODE
//        1059  005030       WHEN 0
//        1060  005040           MOVE CAPYACTT-SUB-FUND-GRP-CD TO WS-SUB-FUND-GRP-CD
//        1061  005050       WHEN OTHER

            if(null == priorYearAccount) {
            
//        1062  005060           DISPLAY ' ERROR ACCESSING PRIOR YR ACCT TABLE FOR '
//        1063                       'IN 3000 SELECT '
//        1064  005070               CAPYACTT-ACCOUNT-NBR
//        1065  005080           MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        1066  005090           GO TO 3000-SELECT-CRITERIA-EXIT
                
                LOG.info("No prior year account for chart \"" + encumbrance.getChartOfAccountsCode()
                        + "\" and account \"" + encumbrance.getAccountNumber() + "\"");
                
                return false;
                
            }
            
            // Otherwise th prior year sub fund group code is OK.
            
//        1067  005100     END-EVALUATE
//        1068  005110           MOVE WS-SUB-FUND-GRP-CD
//        1069  005120             TO CASFGR-SUB-FUND-GRP-CD
//        1070  005130     EXEC SQL
//        1071  005140          SELECT   FUND_GRP_CD
//        1072  005150          INTO     :CASFGR-FUND-GRP-CD :CASFGR-FGC-I
//        1073  005160          FROM     CA_SUB_FUND_GRP_T
//        1074  005170          WHERE SUB_FUND_GRP_CD = RTRIM(:CASFGR-SUB-FUND-GRP-CD)
//        1075  005180     END-EXEC
            
            SubFundGroup subFundGroup = 
                subFundGroupService.getByPrimaryId(priorYearAccount.getSubFundGroupCode());
            
//        1076  005190     IF CASFGR-FGC-I < ZERO
//        1077  005200        MOVE SPACE TO CASFGR-FUND-GRP-CD
//        1078  005210     END-IF
//        1079  005220     EVALUATE SQLCODE
//        1080  005230       WHEN 0
            
//        1081  005240           IF CASFGR-FUND-GRP-CD  = 'CG'
            
            if(null != subFundGroup && "CG".equals(subFundGroup.getSubFundGroupCode())) {
                
//        1082  005250              GO TO CHECK-AMT
                
                return isEncumbranceClosed(encumbrance);
                
//        1083  005260             ELSE
                
            } else {
                
//        1084  005270              MOVE 'N' TO WS-SELECT-SW
//        1085  005280              GO TO 3000-SELECT-CRITERIA-EXIT
                
                return false;
                
//        1086  005290           END-IF
                
            }
            
//        1087  005300       WHEN OTHER
//        1088  005310           DISPLAY ' ERROR ACCESSING SUB FUND GROUP TABLE FOR '
//        1089  005320               CASFGR-SUB-FUND-GRP-CD
//        1090  005330           MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        1091  005340           GO TO 3000-SELECT-CRITERIA-EXIT
//        1092  005350     END-EVALUATE.
        
        }
        
//        1093  005360        MOVE 'N' TO WS-SELECT-SW
//        1094  005370        GO TO 3000-SELECT-CRITERIA-EXIT.        
        
        return false;
        
    }
    
    /**
     * @param encumbrance
     * @return true if the amount closed on the encumbrance is NOT equal to the amount of
     *         the encumbrance itself, e.g. if the encumbrance has not yet been paid off.
     */
    private boolean isEncumbranceClosed(Encumbrance encumbrance) {
        
//        1095  005380 CHECK-AMT.
        
//        1096  005390     IF GLGLEC-ACLN-ENCUM-AMT =
//        1097  005400        GLGLEC-ACLN-ENCUM-CLS-AMT
        
        if(encumbrance.getAccountLineEncumbranceAmount().doubleValue() == 
            encumbrance.getAccountLineEncumbranceClosedAmount().doubleValue()) {
            
//        1098  005410           MOVE 'N' TO WS-SELECT-SW
            
            return false;
            
//        1099  005420           GO TO 3000-SELECT-CRITERIA-EXIT.
            
        }
        
//        1100  005430     IF GLGLEC-ACLN-ENCUM-AMT   NUMERIC AND
//        1101  005440        GLGLEC-ACLN-ENCUM-CLS-AMT  NUMERIC
        
        // No real analog to this in Java given that the amounts are KualiDecimals.
        // The amount values are guaranteed to be numeric.
        
//        1102  005450      GO TO 3000-SELECT-CRITERIA-EXIT.
//        1103  005460     MOVE 'N' TO WS-SELECT-SW.        
        
        return true;
        
    }
    
    /**
     * @param entry
     * @param offset
     * @param encumbrance
     * @param objectTypeCode
     * @return true if the encumbrance is eligible for cost share.
     * @throws FatalErrorException
     */
    public boolean isEncumbranceEligibleForCostShare(OriginEntry entry, OriginEntry offset, Encumbrance encumbrance, String objectTypeCode) 
    throws FatalErrorException {
        
//            1262             MOVE SEQ-WRITE-COUNT TO SEQ-CHECK-CNT.
//            1263             IF SEQ-CHECK-CNT (7:3) = '000'
//            1264                DISPLAY '  SEQUENTIAL RECORDS WRITTEN = ' SEQ-CHECK-CNT.
//            1265  007070     MOVE WS-HOLD-GLEN TO GLEN-RECORD.
//            1266  007080        MOVE GLGLEC-FIN-COA-CD
//            1267  007090          TO CAPYACTT-FIN-COA-CD
//            1268  007100        MOVE GLGLEC-ACCOUNT-NBR
//            1269  007110          TO CAPYACTT-ACCOUNT-NBR.
//            1270  007120     EXEC SQL
//            1271  007130          SELECT   SUB_FUND_GRP_CD
//            1272  007140          INTO     :CAPYACTT-SUB-FUND-GRP-CD :CAPYACTT-SFGC-I
//            1273  007150          FROM     CA_PRIOR_YR_ACCT_T
//            1274  007160          WHERE  FIN_COA_CD = RTRIM(:CAPYACTT-FIN-COA-CD)
//            1275  007170          AND    ACCOUNT_NBR = RTRIM(:CAPYACTT-ACCOUNT-NBR)
//            1276  007180     END-EXEC.
        
        PriorYearAccount priorYearAccount = 
            priorYearAccountService.getByPrimaryKey(
                encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber());

//            1277  007190     IF CAPYACTT-SFGC-I < ZERO
//            1278  007200        MOVE SPACE TO CAPYACTT-SUB-FUND-GRP-CD.
//            1279  007220     EVALUATE SQLCODE
//            1280  007230       WHEN 0
        
        String subFundGroupCode = null;
        if(null != priorYearAccount) {
            
//            1281  007240           MOVE CAPYACTT-SUB-FUND-GRP-CD TO WS-SUB-FUND-GRP-CD
            
            subFundGroupCode = priorYearAccount.getSubFundGroupCode();
        
//            1282  007250       WHEN OTHER
            
        } else {
            
//            1283  007260           DISPLAY ' ERROR ACCESSING PRIOR YR ACCT TABLE FOR '
//            1284  007270               CAPYACTT-ACCOUNT-NBR
//            1285  007280           MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//            1286                   GO TO 4000-WRITE-OUTPUT-EXIT
            
            throw new FatalErrorException("ERROR ACCESSING PRIOR YR ACCT TABLE FOR " + encumbrance.getAccountNumber());
            
//            1287  007300     END-EVALUATE.
            
        }
        
//            1288  007310           MOVE WS-SUB-FUND-GRP-CD
//            1289  007320             TO CASFGR-SUB-FUND-GRP-CD
//            1290  007330     EXEC SQL
//            1291  007340          SELECT   FUND_GRP_CD
//            1292  007350          INTO     :CASFGR-FUND-GRP-CD :CASFGR-FGC-I
//            1293  007360          FROM     CA_SUB_FUND_GRP_T
//            1294  007370          WHERE SUB_FUND_GRP_CD = RTRIM(:CASFGR-SUB-FUND-GRP-CD)
//            1295  007380     END-EXEC.
        
        SubFundGroup subFundGroup = subFundGroupService.getByPrimaryId(subFundGroupCode);

//            1296  007390     IF CASFGR-FGC-I < ZERO
//            1297  007400        MOVE SPACE TO CASFGR-FUND-GRP-CD.
//            1298  007420     EVALUATE SQLCODE
//            1299  007430       WHEN 0
        
        if(null != subFundGroup) {
        
            if(!"CG".equals(subFundGroup.getFundGroupCode())) {
            
//            1300  007440           IF CASFGR-FUND-GRP-CD NOT = 'CG'
//            1301  007450              GO TO 4000-WRITE-OUTPUT-EXIT
                
                return false;
                
//            1302  007460           END-IF
                
            }
                
//            1303  007470       WHEN OTHER
            
        } else {
            
//            1304  007480           DISPLAY ' ERROR ACCESSING SUB FUND GROUP TABLE FOR '
//            1305  007490               CASFGR-SUB-FUND-GRP-CD
//            1306  007500           MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//            1307  007510           GO TO 4000-WRITE-OUTPUT-EXIT
            
            throw new FatalErrorException("ERROR ACCESSING SUB FUND GROUP TABLE FOR " + subFundGroupCode);
            
//            1308  007520     END-EVALUATE.        
        
        }
        
//            1309  007530     IF CASFGR-FUND-GRP-CD NOT = 'CG'
//            1310  007540          GO TO 4000-WRITE-OUTPUT-EXIT.
        
        // Yes. This is redundant to the statement about 22 lines above here.
        
        if(!"CG".equals(subFundGroup.getFundGroupCode())) {
            
            return false;
            
        }
        
//            1311  007550     IF (FIN-OBJ-TYP-CD OF GLEN-RECORD = 'EE'
//            1312  007560         OR FIN-OBJ-TYP-CD OF GLEN-RECORD = 'EX'
//            1313  007570         OR FIN-OBJ-TYP-CD OF GLEN-RECORD = 'ES'
//            1314  007580         OR FIN-OBJ-TYP-CD OF GLEN-RECORD = 'TE')
//            1315  007590         AND (FIN-BALANCE-TYP-CD OF GLEN-RECORD = 'AC'
//            1316  007600           OR FIN-BALANCE-TYP-CD OF GLEN-RECORD = 'EX'
//            1317  007610           OR FIN-BALANCE-TYP-CD OF GLEN-RECORD = 'IE'
//            1318  007620           OR FIN-BALANCE-TYP-CD OF GLEN-RECORD = 'PE')
//            1319  007630         CONTINUE
//            1320  007640          ELSE
//            1321  007650          GO TO 4000-WRITE-OUTPUT-EXIT.
        
        // The if/else logic is inverted because there's no 'continue' analog in java.
        // There's just a return statement which is the opposite of continue.
        if (!(ArrayUtils.contains(EXPENSE_OBJECT_CODE_TYPES, objectTypeCode) 
                && ArrayUtils.contains(
                        EXTERNAL_INTERNAL_AND_PRE_ENCUMBRANCE_BALANCE_TYPE_CODES, 
                        encumbrance.getBalanceTypeCode()))) {
            
            return false;
            
        } else {
            
//            1322  007660         MOVE FIN-COA-CD OF GLEN-RECORD
//            1323  007670           TO CASA21-FIN-COA-CD
//            1324  007680         MOVE ACCOUNT-NBR OF GLEN-RECORD
//            1325  007690           TO CASA21-ACCOUNT-NBR
//            1326  007700         MOVE SUB-ACCT-NBR OF GLEN-RECORD
//            1327  007710           TO CASA21-SUB-ACCT-NBR
//            1328  007720         EXEC SQL
//            1329  007730           SELECT SUB_ACCT_TYP_CD,
//            1330  007740                  CST_SHR_COA_CD,
//            1331  007750                  CST_SHRSRCACCT_NBR,
//            1332  007760                  CST_SRCSUBACCT_NBR
//            1333  007770           INTO :CASA21-SUB-ACCT-TYP-CD :CASA21-SATC-I,
//            1334  007780                :CASA21-CST-SHR-COA-CD :CASA21-CSCC-I,
//            1335  007790                :CASA21-CST-SHRSRCACCT-NBR :CASA21-CSN-I,
//            1336  007800                :CASA21-CST-SRCSUBACCT-NBR :CASA21-CSN2-I
//            1337  007810           FROM CA_A21_SUB_ACCT_T
//            1338  007820           WHERE FIN_COA_CD = RTRIM(:CASA21-FIN-COA-CD)
//            1339  007830             AND ACCOUNT_NBR = RTRIM(:CASA21-ACCOUNT-NBR)
//            1340  007840             AND SUB_ACCT_NBR = RTRIM(:CASA21-SUB-ACCT-NBR)
//            1341  007850         END-EXEC.
            
            A21SubAccount a21SubAccount = 
                a21SubAccountService.getByPrimaryKey(
                        encumbrance.getChartOfAccountsCode(), 
                        encumbrance.getAccountNumber(), 
                        encumbrance.getSubAccountNumber());
            
//            1342  007860         IF CASA21-SATC-I < ZERO
//            1343  007870            MOVE SPACE TO CASA21-SUB-ACCT-TYP-CD.
//            1344  007890         IF CASA21-CSCC-I < ZERO
//            1345  007900            MOVE SPACE TO CASA21-CST-SHR-COA-CD.
//            1346  007920         IF CASA21-CSN-I < ZERO
//            1347  007930            MOVE SPACE TO CASA21-CST-SHRSRCACCT-NBR.
//            1348  007950         IF CASA21-CSN2-I < ZERO
//            1349  007960            MOVE SPACE TO CASA21-CST-SRCSUBACCT-NBR.
//            1350  007980         EVALUATE SQLCODE
//            1351  007990           WHEN 0
            
            if(null != a21SubAccount) {
            
//            1352  008000             MOVE ACCOUNT-NBR OF GLEN-RECORD
//            1353  008010               TO COSTSHARE-ACCOUNT
//            1354  008020             MOVE CASA21-SUB-ACCT-TYP-CD TO
//            1355  008030                  WS-SUB-ACCT-TYP-CD
                
                // Working storage fields aren't needed. We have a non-null A21SubAccount
                // which holds the fields we need.
                
            } else {
                
//            1356  008040           WHEN +100
//            1357  008050           WHEN +1403
//            1358  008060               MOVE SPACES TO WS-SUB-ACCT-TYP-CD
//            1359  008070               MOVE SPACES TO COSTSHARE-ACCOUNT
//            1360  008080               GO TO 4000-WRITE-OUTPUT-EXIT
//            1361  008090           WHEN OTHER
//            1362  008100               DISPLAY 'ERROR ACCESSING A21 SUB ACCOUNT TABLE'
//            1363  008110                       ' SQL CODE IS ' SQLCODE
//            1364  008120               MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//            1365  008130               GO TO 4000-WRITE-OUTPUT-EXIT
                
                throw new FatalErrorException("ERROR ACCESSING A21 SUB ACCOUNT TABLE");
                
//            1366  008140         END-EVALUATE.
                
            }
            
//            1367  008150     IF WS-SUB-ACCT-TYP-CD = 'CS'
//            1368  008160         CONTINUE
//            1369  008170        ELSE GO TO 4000-WRITE-OUTPUT-EXIT.
            
            return "CS".equals(a21SubAccount.getSubAccountTypeCode());
            
        }
        
    }
}
