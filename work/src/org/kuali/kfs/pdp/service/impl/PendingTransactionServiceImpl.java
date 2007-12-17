/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Aug 30, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.pdp.bo.GlPendingTransaction;
import org.kuali.module.pdp.bo.PaymentAccountDetail;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.dao.GlPendingTransactionDao;
import org.kuali.module.pdp.service.GlPendingTransactionService;
import org.kuali.module.pdp.utilities.GeneralUtilities;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;


@Transactional
public class GlPendingTransactionServiceImpl implements GlPendingTransactionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlPendingTransactionServiceImpl.class);

    private static String FDOC_TYP_CD_PROCESS_ACH = "ACHD";
    private static String FDOC_TYP_CD_PROCESS_CHECK = "CHKD";
    private static String FDOC_TYP_CD_CANCEL_REISSUE_ACH = "ACHR";
    private static String FDOC_TYP_CD_CANCEL_REISSUE_CHECK = "CHKR";
    private static String FDOC_TYP_CD_CANCEL_ACH = "ACHC";
    private static String FDOC_TYP_CD_CANCEL_CHECK = "CHKC";

    private GlPendingTransactionDao glPendingTransactionDao;
    private ChartService chartService;
    private AccountingPeriodService accountingPeriodService;

    // Inject
    public void setGlPendingTransactionDao(GlPendingTransactionDao g) {
        glPendingTransactionDao = g;
    }

    // Inject
    public void setAccountingPeriodService(AccountingPeriodService aps) {
        this.accountingPeriodService = aps;
    }

    // Inject
    public void setChartService(ChartService c) {
        chartService = c;
    }

    public GlPendingTransactionServiceImpl() {
        super();
    }

    /*
     * GlPendingTransaction Fields not used: sequenceNbr // TRN_ENTR_SEQ_NBR NUMBER 5 unifaceVersion // U_VERSION VARCHAR2 1
     * finObjTypCd // FIN_OBJ_TYP_CD VARCHAR2 2 fdocReversalDt // FDOC_REVERSAL_DT DATE 7 trnEncumUpdtCd // TRN_ENCUM_UPDT_CD
     * VARCHAR2 1 fdocApprovedCd // FDOC_APPROVED_CD VARCHAR2 1 acctSfFinObjCd // ACCT_SF_FINOBJ_CD VARCHAR2 4 trnEntrOfstCd //
     * TRN_ENTR_OFST_CD VARCHAR2 1 processInd // TRN_EXTRT_IND VARCHAR2 1
     */


    /**
     * @see org.kuali.module.pdp.service.GlPendingTransactionService#save(org.kuali.module.pdp.bo.GlPendingTransaction)
     */
    public void save(GlPendingTransaction tran) {
        LOG.debug("save() started");

        glPendingTransactionDao.save(tran);
    }

    /**
     * @see org.kuali.module.pdp.service.GlPendingTransactionService#getUnextractedTransactions()
     */
    public Iterator getUnextractedTransactions() {
        LOG.debug("getUnextractedTransactions() started");

        return glPendingTransactionDao.getUnextractedTransactions();
    }

    /**
     * @see org.kuali.module.pdp.service.GlPendingTransactionService#createProcessPaymentTransaction(org.kuali.module.pdp.bo.PaymentDetail, java.lang.Boolean)
     */
    public void createProcessPaymentTransaction(PaymentDetail pd, Boolean relieveLiabilities) {

        List accountListings = pd.getAccountDetail();

        for (Iterator iter = accountListings.iterator(); iter.hasNext();) {
            PaymentAccountDetail elem = (PaymentAccountDetail) iter.next();

            GlPendingTransaction gpt = new GlPendingTransaction();
            gpt.setFdocRefTypCd("PDP");
            gpt.setFsRefOriginCd("PD");
            gpt.setFsOriginCd("PD");
            gpt.setFinancialBalanceTypeCode("AC");
            Date d = new Date((new java.util.Date()).getTime());
            gpt.setTransactionDt(new Timestamp(d.getTime()));
            AccountingPeriod fiscalPeriod = accountingPeriodService.getByDate(d);
            gpt.setUniversityFiscalYear(fiscalPeriod.getUniversityFiscalYear());
            gpt.setUnivFiscalPrdCd(fiscalPeriod.getUniversityFiscalPeriodCode());

            gpt.setAccountNumber(elem.getAccountNbr());
            gpt.setSubAccountNumber(elem.getSubAccountNbr());
            gpt.setChartOfAccountsCode(elem.getFinChartCode());
            
            if (pd.getPaymentGroup().getDisbursementType().getCode().equals("ACH")) {
                gpt.setFinancialDocumentTypeCode(FDOC_TYP_CD_PROCESS_ACH);
            }
            else if (pd.getPaymentGroup().getDisbursementType().getCode().equals("CHCK")) {
                gpt.setFinancialDocumentTypeCode(FDOC_TYP_CD_PROCESS_CHECK);
            }
            gpt.setFdocNbr(pd.getPaymentGroup().getDisbursementNbr().toString());
            
            if ((relieveLiabilities != null) && (relieveLiabilities.booleanValue()) && pd.getFinancialDocumentTypeCode() != null) {
                OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(gpt.getUniversityFiscalYear(), gpt.getChartOfAccountsCode(), pd.getFinancialDocumentTypeCode(), gpt.getFinancialBalanceTypeCode());
                if (offsetDefinition != null) {
                    gpt.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
                } else {
                    gpt.setFinancialObjectCode(elem.getFinObjectCode());
                }
                gpt.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            else {
                gpt.setFinancialObjectCode(elem.getFinObjectCode());
                gpt.setFinancialSubObjectCode(elem.getFinSubObjectCode());
            }
            gpt.setProjectCd(elem.getProjectCode());
            if (elem.getAccountNetAmount().signum() >= 0) {
                gpt.setDebitCrdtCd("D");
            }
            else {
                gpt.setDebitCrdtCd("C");
            }
            gpt.setAmount(elem.getAccountNetAmount().abs());

            String trnDesc;
            String payeeName = pd.getPaymentGroup().getPayeeName();
            // removed 2/8/2006 per JIRA KULPDP-32
            // if (payeeName.length() > 32) {
            // trnDesc = payeeName.substring(0,32) + pd.getId();
            // } else {
            // String fill = "";
            // int j = 32 - payeeName.length();
            // for (int i = 0; i < j; i++) {
            // fill = fill + " ";
            // }
            // trnDesc = payeeName + fill + pd.getId();
            // }
            if (payeeName.length() > 40) {
                trnDesc = payeeName.substring(0, 40);
            }
            else {
                String fill = "";
                int j = 40 - payeeName.length();
                for (int i = 0; i < j; i++) {
                    fill = fill + " ";
                }
                trnDesc = payeeName + fill;
            }
            gpt.setDescription(trnDesc);

            gpt.setOrgDocNbr(pd.getOrganizationDocNbr());

            gpt.setOrgReferenceId(elem.getOrgReferenceId());
            gpt.setFdocRefNbr(pd.getCustPaymentDocNbr());
            
            // update the offset account if necessary
            SpringContext.getBean(FlexibleOffsetAccountService.class).updateOffset(gpt);

            glPendingTransactionDao.save(gpt);
        }
    }

    public void createCancellationTransaction(PaymentGroup pg) {
        this.createCancellationEntries(pg, FDOC_TYP_CD_CANCEL_ACH, FDOC_TYP_CD_CANCEL_CHECK);
    }

    public void createCancelReissueTransaction(PaymentGroup pg) {
        this.createCancellationEntries(pg, FDOC_TYP_CD_CANCEL_REISSUE_ACH, FDOC_TYP_CD_CANCEL_REISSUE_CHECK);
    }

    private void createCancellationEntries(PaymentGroup pg, String achFdocTypeCode, String checkFdocTypeCode) {
        List accountListings = new ArrayList();
        for (Iterator iter = pg.getPaymentDetails().iterator(); iter.hasNext();) {
            PaymentDetail elem = (PaymentDetail) iter.next();
            accountListings.addAll(elem.getAccountDetail());
        }

        for (Iterator iter = accountListings.iterator(); iter.hasNext();) {
            PaymentAccountDetail elem = (PaymentAccountDetail) iter.next();

            GlPendingTransaction gpt = new GlPendingTransaction();
            gpt.setFdocRefTypCd("PDP");
            gpt.setFsRefOriginCd("PD");
            gpt.setFsOriginCd("PD");
            gpt.setFinancialBalanceTypeCode("AC");
            Date d = new Date((new java.util.Date()).getTime());
            gpt.setTransactionDt(new Timestamp(d.getTime()));
            AccountingPeriod fiscalPeriod = accountingPeriodService.getByDate(d);
            gpt.setUniversityFiscalYear(fiscalPeriod.getUniversityFiscalYear());
            gpt.setUnivFiscalPrdCd(fiscalPeriod.getUniversityFiscalPeriodCode());

            gpt.setAccountNumber(elem.getAccountNbr());
            gpt.setSubAccountNumber(elem.getSubAccountNbr());
            gpt.setChartOfAccountsCode(elem.getFinChartCode());
            Boolean relieveLiabilities = pg.getBatch().getCustomerProfile().getRelieveLiabilities();
            if ((relieveLiabilities != null) && (relieveLiabilities.booleanValue()) && elem.getPaymentDetail().getFinancialDocumentTypeCode() != null) {
                OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(gpt.getUniversityFiscalYear(), gpt.getChartOfAccountsCode(), elem.getPaymentDetail().getFinancialDocumentTypeCode(), gpt.getFinancialBalanceTypeCode());
                gpt.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
                gpt.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            else {
                gpt.setFinancialObjectCode(elem.getFinObjectCode());
                gpt.setFinancialSubObjectCode(elem.getFinSubObjectCode());
            }
            gpt.setProjectCd(elem.getProjectCode());
            gpt.setOrgReferenceId(elem.getOrgReferenceId());
            gpt.setAmount(elem.getAccountNetAmount().abs());
            gpt.setFdocNbr(pg.getDisbursementNbr().toString());
            if (elem.getAccountNetAmount().signum() >= 0) {
                gpt.setDebitCrdtCd("C");
            }
            else {
                gpt.setDebitCrdtCd("D");
            }
            if (pg.getDisbursementType().getCode().equals("ACH")) {
                gpt.setFinancialDocumentTypeCode(achFdocTypeCode);
            }
            else if (pg.getDisbursementType().getCode().equals("CHCK")) {
                gpt.setFinancialDocumentTypeCode(checkFdocTypeCode);
            }

            PaymentDetail pd = elem.getPaymentDetail();
            String trnDesc;
            String payeeNameGL;
            String poNbrGL = "";
            String invoiceNbrGL = "";
            if (pg.getPayeeName().length() > 15) {
                payeeNameGL = pg.getPayeeName().substring(0, 15);
            }
            else {
                String fill = "";
                int j = 15 - pg.getPayeeName().length();
                for (int i = 0; i < j; i++) {
                    fill = fill + " ";
                }
                payeeNameGL = pg.getPayeeName() + fill;
            }
            String poNbr = pd.getPurchaseOrderNbr();
            if (!(GeneralUtilities.isStringEmpty(poNbr))) {
                if (poNbr.length() > 9) {
                    poNbrGL = poNbr.substring(0, 9);
                }
                else {
                    String fill = "";
                    int j = 9 - poNbr.length();
                    for (int i = 0; i < j; i++) {
                        fill = fill + " ";
                    }
                    poNbrGL = poNbr + fill;
                }
            }
            String invoiceNbr = pd.getInvoiceNbr();
            if (!(GeneralUtilities.isStringEmpty(invoiceNbr))) {
                if (invoiceNbr.length() > 14) {
                    invoiceNbrGL = invoiceNbr.substring(0, 14);
                }
                else {
                    String fill = "";
                    int j = 14 - invoiceNbr.length();
                    for (int i = 0; i < j; i++) {
                        fill = fill + " ";
                    }
                    invoiceNbrGL = invoiceNbr + fill;
                }
            }
            trnDesc = payeeNameGL + " " + poNbrGL + " " + invoiceNbrGL;
            if (trnDesc.length() > 40) {
                trnDesc = trnDesc.substring(0, 40);
            }
            gpt.setDescription(trnDesc);
            gpt.setOrgDocNbr(pd.getOrganizationDocNbr());
            gpt.setFdocRefNbr(pd.getCustPaymentDocNbr());
            
            // update the offset account if necessary
            SpringContext.getBean(FlexibleOffsetAccountService.class).updateOffset(gpt);

            glPendingTransactionDao.save(gpt);
        }
    }
}
