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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.KeyConstants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.dao.OptionsDao;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.SufficientFundsRebuilderService;

/**
 * @author Anthony Potts
 */

public class SufficientFundsRebuilderServiceImpl implements SufficientFundsRebuilderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsRebuilderServiceImpl.class);

    private OriginEntryService originEntryService;
    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;
//    private SufficientFundsRebuildDao sufficientFundsRebuildDao;
    private OptionsDao optionsDao;

    private Date runDate;
    private Calendar runCal;
    private Options options;
    
    Map batchError;
    List transactionErrors;
    private int sfrbRecsConvertedCount;
    
    public SufficientFundsRebuilderServiceImpl() {
      super();
    }

    public void rebuildSufficientFunds() {
        // TODO Auto-generated method stub
        LOG.debug("beginning sufficient funds rebuild process");
        initService();
        
/*
 *        sufficientFundsRebuildDao.getAllEntries();
        iterate through above {
            transactionErrors = new ArrayList();
            convertSFRB(sfrb);
        
        }
        
*/    }

    private void initService() {
        batchError = new HashMap();

        runDate = new Date(dateTimeService.getCurrentDate().getTime());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        options = optionsDao.getByPrimaryId(new Integer(2006));
        
        if (options == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }
    }
    
    private void convertSFRB(SufficientFundRebuild sfrb) {
        if("A".equalsIgnoreCase(sfrb.getAccountFinancialObjectTypeCode())) {
            return;
        }
        if("O".equalsIgnoreCase(sfrb.getAccountFinancialObjectTypeCode())) {
            processSfrbObject(sfrb);
            ++sfrbRecsConvertedCount;
        } else {
            addTransactionError("ACCOUNT/FINANCIAL OBJECT TYPE CODE MUST='A' OR 'O'", sfrb.getAccountFinancialObjectTypeCode());
        }
    }

    private void processSfrbObject(SufficientFundRebuild sfrb) {
/*        endOfSfovSw = "N";
        glsfovUnivFiscalYr = universityFiscalYear;
        glsfovFinCoaCd = glsfrbFinCoaCd;
        glsfovFinObjectCd = glsfrbAcctNbrFobjCd;
        getFirstSfov();
        if((glsfovUnivFiscalYr.equalsIgnoreCase(universityFiscalYear) &&
            glsfovFinCoaCd.equalsIgnoreCase(glsfrbFinCoaCd) &&
            glsfovFinObjectCd.equalsIgnoreCase(glsfrbAcctNbrFobjCd)) &&
            endOfSfovSw.equalsIgnoreCase("N") && !stopRun) {
                do {
                    convertObjtToAcct();
                }while(!glsfovUnivFiscalYr.equalsIgnoreCase(universityFiscalYear) ||
                        !glsfovFinCoaCd.equalsIgnoreCase(glsfrbFinCoaCd) ||
                        !glsfovFinObjectCd.equalsIgnoreCase(glsfrbAcctNbrFobjCd) ||
                        endOfSfovSw.equalsIgnoreCase("Y") ||
                        !stopRun);
        }
        deleteSfrb();
*/
    }

    private void processSFRB(SufficientFundRebuild sfrb) {
/*
 *         if(!"A".equalsIgnoreCase(sfrb.getAccountFinancialObjectTypeCode())) {
//            wsWarningFlag = "y";
//            warningCount++;
//            sfrbNotDeletedCount++;
            addTransactionError("ACCOUNT/FINANCIAL OBJECT TYPE CODE MUST='A' OR 'O'", sfrb.getAccountFinancialObjectTypeCode());
            return;
        }
        cgAccountSw = "N";
        sfblExistsSq = "N";
        errorsFoundSw = "N";

        if(!glsfrbFinCoaCd.equalsIgnoreCase(saveCoaCode)){
            cacoatFinCoaCd = glsfrbFinCoaCd;
            query = "SELECT FIN_COA_CD, FIN_COA_MGRUNVL_ID' FIN_COA_ORIGIN_CD, FIN_COA_DESC " +
                    ",FIN_COA_ACTIVE_CD, FIN_CASH_OBJ_CD, FIN_AP_OBJ_CD, INCBDGT_ELIMOBJ_CD " +
                    ",EXPBDGT_ELIMOBJ_CDR PTS_TO_FIN_COA_CD, FIN_AR_OBJ_CD, FIN_INT_ENC_OBJ_CD " +
                    ",FIN_EXT_ENC_OBJ_CD, FIN_PRE_ENC_OBJ_CD,ICR_INC_FIN_OBJ_CD, ICR_EXP_FIN_OBJ_CD";
            sqlcode = read(query);
            if(sqlcode == 9999) {
                sqlError();
                abendProgram();
            }
            if(sqlcode == 100) {
                checkNewPage();
                //Error Message
                wsWarningFlag = "Y";
                warningCount++;
                lineCount++;
                sfrbNotDeletedCount++;
            }
            else {
                saveCoaCode = cacoatFinCoaCd;
            }
        }
        caacctFinCoaCd = glsfrbFinCoaCd;
        caacctAccountNbr = glsfrbAcctNbrFobjCd;
        query = "Select * from CA_ACCOUNT_T Where FIN_COA_CD = caacctFinCoaCd and ACCOUNT_NBR = caacctAccountNbr";
        sqlcode = read(query);
        if(sqlcode == 9999) {
            sqlError();
            abendProgram();
        }
        if(sqlcode == 100) {
            checkNewPage();
            //Error Message
            wsWarningFlag = "Y";
            warningCount++;
            lineCount++;
            sfrbNotDeletedCount++;
            getNextSfrb();
        }
        casfgrSubFundGrpCd = caacctSubFundGrpCd;
        query = "Select * from CA_SUB_FUND_CRP_T whereSUB_FUND_GRP_CD = casfgrSubFundGrpCD";
        sqlcode = read(query);
        if(sqlcode == 9999) {
            sqlError();
            abendProgram();
        }
        if(sqlcode == 100) {
            checkNewPage();
            //Error Message
            wsWarningFlag = "Y";
            warningCount++;
            lineCount++;
            sfrbNotDeletedCount++;
            getNextSfrb();
        }
        if("CG".equalsIgnoreCase(casfgrFundGrpCd)) {
            cgAccountSw = "Y";
        }
        glglblUnivFiscalYr = universityFiscalYear;
        glglblFinCoaCd = glsfrbFinCoaCd;
        glglblAccountNbr = glsfrbAcctNbrFobjCd;
        endOfGlblSw = "N";
        noGlblSw = "Y";
        query = "Select * from GL_BALANCE_T where UNIV_FISCAL_YR = glglblUnivFiscalYr and FIN_COA_CD = glglblFinCoaCd and ACCOUNT_NBR = glglblAccountNbr";
        sqlcode = read(query);
        if(sqlcode == 9999) {
            sqlError();
            abendProgram();
        }
        if(glglblDtI == -1) {
            glglblTimestamp = wsCompleteTimestamp;
        }

        if(universityFiscalYear.equalsIgnoreCase(glglblUnivFiscalYr) &&
           glsfrbFinCoaCd.equalsIgnoreCase(glglblFinCoaCd) &&
           glsfrbAcctNbrFobjCd.equalsIgnoreCase(glglblAccountNbr)) {}
        else {
            //Close Cursor
            errorsFoundSw = "Y";
            noGlblSw = "N";
            checkNewPage();
            //Error Message
            wsWarningFlag = "Y";
            warningCount++;
            lineCount++;
            sfrbNotDeletedCount++;
        }
        if("O".equalsIgnoreCase(caacctAcctSfCd) ||
           "L".equalsIgnoreCase(caacctAcctSfCd) ||
           "C".equalsIgnoreCase(caacctAcctSfCd) ||
           "A".equalsIgnoreCase(caacctAcctSfCd) ||
           "H".equalsIgnoreCase(caacctAcctSfCd)) {}
        else {
            //CLOSE GLBL_CURSOR
            errorsFoundSw = "Y";
            checkNewPage();
            //Error Message
            wsWarningFlag = "Y";
            warningCount++;
            lineCount++;
            sfrbNotDeletedCount++;
            getNextSfrb();
            return;
        }

        glsfblUnivFiscalYr = universityFiscalYear;
        glsfblFinCoaCd = glsfrbFinCoaCd;
        glsfblAccountNbr = glsfrbAcctNbrFobjCd;
        endOfSfblSw = "N";
        query = "Select * from GL_SF_BALANCES_T";
        if(sqlcode == 9999) {
            sqlError();
            abendProgram();
        }

        if("N".equalsIgnoreCase(errorsFoundSw) &&
           universityFiscalYear.equalsIgnoreCase(glsfblUnivFiscalYr) &&
           glsfrbFinCoaCd.equalsIgnoreCase(glsfblFinCoaCd) &&
           glsfrbAcctNbrFobjCd.equalsIgnoreCase(glsfblAccountNbr) &&sqlcode == 0) {
            do {
                deleteSfblAccounts();
            } while(!glsfblUnivFiscalYr.equalsIgnoreCase(universityFiscalYear) ||
                    !glsfblFinCoaCd.equalsIgnoreCase(glsfrbFinCoaCd) ||
                    !glsfblAccountNbr.equalsIgnoreCase(glsfrbAcctNbrFobjCd) ||
                    "Y".equalsIgnoreCase(endOfSfblSw) || stopRun);
        }
        else {
            //CLOSE SFBL_CURSOR
        }

        if("N".equalsIgnoreCase(caacctAcctSfCd)) {
            sfblExcluded = true;
        }
        else {
            if("N".equalsIgnoreCase(errorsFoundSw)) {
                do {
                    processSfbl();
                }while(!glglblUnivFiscalYr.equalsIgnoreCase(universityFiscalYear) ||
                       !glglblFinCoaCd.equalsIgnoreCase(glsfrbFinCoaCd) ||
                       !glglblAccountNbr.equalsIgnoreCase(glsfrbAcctNbrFobjCd) ||
                       "Y".equalsIgnoreCase(endOfGlblSw)  || stopRun);
            }
        }
        if("N".equalsIgnoreCase(errorsfoundSw)) {
            deleteSfrb();
            sfrbRecsDeletedCount++;
        }
        if("N".equalsIgnoreCase(noGlblSw)) {
            deleteSfrb();
            sfrbRecsDeletedCount++;
        }
        getNextSfrb();
*/    }

    /**
     * @param errorMessage
     */
    private void addTransactionError(String errorMessage, String errorValue) {
        transactionErrors.add(errorMessage + " (" + errorValue + ")");
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setOptionsDao(OptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

}
