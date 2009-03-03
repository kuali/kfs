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
package org.kuali.kfs.pdp.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.dataaccess.PendingTransactionDao;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.pdp.service.PendingTransactionService
 */
@Transactional
public class PendingTransactionServiceImpl implements PendingTransactionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PendingTransactionServiceImpl.class);

    private static String FDOC_TYP_CD_PROCESS_ACH = "ACHD";
    private static String FDOC_TYP_CD_PROCESS_CHECK = "CHKD";
    private static String FDOC_TYP_CD_CANCEL_REISSUE_ACH = "ACHR";
    private static String FDOC_TYP_CD_CANCEL_REISSUE_CHECK = "CHKR";
    private static String FDOC_TYP_CD_CANCEL_ACH = "ACHC";
    private static String FDOC_TYP_CD_CANCEL_CHECK = "CHKC";
    private BusinessObjectService businessObjectService;

    private PendingTransactionDao glPendingTransactionDao;
    private ChartService chartService;
    private AccountingPeriodService accountingPeriodService;
    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;

    public PendingTransactionServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#generatePaymentGeneralLedgerPendingEntry(org.kuali.kfs.pdp.businessobject.PaymentGroup)
     */
    public void generatePaymentGeneralLedgerPendingEntry(PaymentGroup paymentGroup) {
        this.populatePaymentGeneralLedgerPendingEntry(paymentGroup, FDOC_TYP_CD_PROCESS_ACH, FDOC_TYP_CD_PROCESS_CHECK, false);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#generateCancellationGeneralLedgerPendingEntry(org.kuali.kfs.pdp.businessobject.PaymentGroup)
     */
    public void generateCancellationGeneralLedgerPendingEntry(PaymentGroup paymentGroup) {
        this.populatePaymentGeneralLedgerPendingEntry(paymentGroup, FDOC_TYP_CD_CANCEL_ACH, FDOC_TYP_CD_CANCEL_CHECK, true);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#generateReissueGeneralLedgerPendingEntry(org.kuali.kfs.pdp.businessobject.PaymentGroup)
     */
    public void generateReissueGeneralLedgerPendingEntry(PaymentGroup paymentGroup) {
        this.populatePaymentGeneralLedgerPendingEntry(paymentGroup, FDOC_TYP_CD_CANCEL_REISSUE_ACH, FDOC_TYP_CD_CANCEL_REISSUE_CHECK, true);
    }

    /**
     * Populates and stores a new GLPE for each account detail in the payment group.
     * 
     * @param paymentGroup payment group to generate entries for
     * @param achFdocTypeCode doc type for ach disbursements
     * @param checkFdocTypeCod doc type for check disbursements
     * @param reversal boolean indicating if this is a reversal
     */
    protected void populatePaymentGeneralLedgerPendingEntry(PaymentGroup paymentGroup, String achFdocTypeCode, String checkFdocTypeCod, boolean reversal) {
        List<PaymentAccountDetail> accountListings = new ArrayList<PaymentAccountDetail>();
        for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
            accountListings.addAll(paymentDetail.getAccountDetail());
        }

        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
        for (PaymentAccountDetail paymentAccountDetail : accountListings) {
            GlPendingTransaction glPendingTransaction = new GlPendingTransaction();
            glPendingTransaction.setSequenceNbr(new KualiInteger(sequenceHelper.getSequenceCounter()));

            if (StringUtils.isNotBlank(paymentAccountDetail.getPaymentDetail().getFinancialSystemOriginCode()) && StringUtils.isNotBlank(paymentAccountDetail.getPaymentDetail().getFinancialDocumentTypeCode())) {
                glPendingTransaction.setFdocRefTypCd(paymentAccountDetail.getPaymentDetail().getFinancialDocumentTypeCode());
                glPendingTransaction.setFsRefOriginCd(paymentAccountDetail.getPaymentDetail().getFinancialSystemOriginCode());
            }
            else {
                glPendingTransaction.setFdocRefTypCd(PdpConstants.PDP_FDOC_TYPE_CODE);
                glPendingTransaction.setFsRefOriginCd(PdpConstants.PDP_FDOC_ORIGIN_CODE);
            }

            glPendingTransaction.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);

            Date transactionTimestamp = new Date(dateTimeService.getCurrentDate().getTime());
            glPendingTransaction.setTransactionDt(transactionTimestamp);
            AccountingPeriod fiscalPeriod = accountingPeriodService.getByDate(new java.sql.Date(transactionTimestamp.getTime()));
            glPendingTransaction.setUniversityFiscalYear(fiscalPeriod.getUniversityFiscalYear());
            glPendingTransaction.setUnivFiscalPrdCd(fiscalPeriod.getUniversityFiscalPeriodCode());

            glPendingTransaction.setAccountNumber(paymentAccountDetail.getAccountNbr());
            glPendingTransaction.setSubAccountNumber(paymentAccountDetail.getSubAccountNbr());
            glPendingTransaction.setChartOfAccountsCode(paymentAccountDetail.getFinChartCode());

            if (paymentGroup.getDisbursementType().getCode().equals(PdpConstants.DisbursementTypeCodes.ACH)) {
                glPendingTransaction.setFinancialDocumentTypeCode(achFdocTypeCode);
            }
            else if (paymentGroup.getDisbursementType().getCode().equals(PdpConstants.DisbursementTypeCodes.CHECK)) {
                glPendingTransaction.setFinancialDocumentTypeCode(checkFdocTypeCod);
            }

            glPendingTransaction.setFsOriginCd(PdpConstants.PDP_FDOC_ORIGIN_CODE);
            glPendingTransaction.setFdocNbr(paymentGroup.getDisbursementNbr().toString());

            Boolean relieveLiabilities = paymentGroup.getBatch().getCustomerProfile().getRelieveLiabilities();
            if ((relieveLiabilities != null) && (relieveLiabilities.booleanValue()) && paymentAccountDetail.getPaymentDetail().getFinancialDocumentTypeCode() != null) {
                OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(glPendingTransaction.getUniversityFiscalYear(), glPendingTransaction.getChartOfAccountsCode(), paymentAccountDetail.getPaymentDetail().getFinancialDocumentTypeCode(), glPendingTransaction.getFinancialBalanceTypeCode());
                glPendingTransaction.setFinancialObjectCode(offsetDefinition != null ? offsetDefinition.getFinancialObjectCode() : paymentAccountDetail.getFinObjectCode());
                glPendingTransaction.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            else {
                glPendingTransaction.setFinancialObjectCode(paymentAccountDetail.getFinObjectCode());
                glPendingTransaction.setFinancialSubObjectCode(paymentAccountDetail.getFinSubObjectCode());
            }

            glPendingTransaction.setProjectCd(paymentAccountDetail.getProjectCode());

            if (paymentAccountDetail.getAccountNetAmount().bigDecimalValue().signum() >= 0) {
                glPendingTransaction.setDebitCrdtCd(reversal ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
            }
            else {
                glPendingTransaction.setDebitCrdtCd(reversal ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
            }
            glPendingTransaction.setAmount(paymentAccountDetail.getAccountNetAmount().abs());

            String trnDesc;

            String payeeName = paymentGroup.getPayeeName();
            trnDesc = payeeName.length() > 40 ? payeeName.substring(0, 40) : StringUtils.rightPad(payeeName, 40);

            if (reversal) {
                String poNbr = paymentAccountDetail.getPaymentDetail().getPurchaseOrderNbr();
                if (StringUtils.isNotBlank(poNbr)) {
                    trnDesc += " " + (poNbr.length() > 9 ? poNbr.substring(0, 9) : StringUtils.rightPad(poNbr, 9));
                }

                String invoiceNbr = paymentAccountDetail.getPaymentDetail().getInvoiceNbr();
                if (StringUtils.isNotBlank(invoiceNbr)) {
                    trnDesc += " " + (invoiceNbr.length() > 14 ? invoiceNbr.substring(0, 14) : StringUtils.rightPad(invoiceNbr, 14));
                }

                if (trnDesc.length() > 40) {
                    trnDesc = trnDesc.substring(0, 40);
                }
            }

            glPendingTransaction.setDescription(trnDesc);

            glPendingTransaction.setOrgDocNbr(paymentAccountDetail.getPaymentDetail().getOrganizationDocNbr());
            glPendingTransaction.setOrgReferenceId(paymentAccountDetail.getOrgReferenceId());
            glPendingTransaction.setFdocRefNbr(paymentAccountDetail.getPaymentDetail().getCustPaymentDocNbr());

            // update the offset account if necessary
            SpringContext.getBean(FlexibleOffsetAccountService.class).updateOffset(glPendingTransaction);

            this.businessObjectService.save(glPendingTransaction);
            
            sequenceHelper.increment();
        }
    }
    
    /**
     * Sets the glPendingTransactionDao attribute value.
     * 
     * @param glPendingTransactionDao The glPendingTransactionDao to set.
     */
    public void setGlPendingTransactionDao(PendingTransactionDao glPendingTransactionDao) {
        this.glPendingTransactionDao = glPendingTransactionDao;
    }

    /**
     * Sets the chartService attribute value.
     * 
     * @param chartService The chartService to set.
     */
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * Sets the accountingPeriodService attribute value.
     * 
     * @param accountingPeriodService The accountingPeriodService to set.
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#save(org.kuali.kfs.pdp.businessobject.GlPendingTransaction)
     */
    public void save(GlPendingTransaction tran) {
        LOG.debug("save() started");

        this.businessObjectService.save(tran);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#getUnextractedTransactions()
     */
    public Iterator<GlPendingTransaction> getUnextractedTransactions() {
        LOG.debug("getUnextractedTransactions() started");

        return glPendingTransactionDao.getUnextractedTransactions();
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#clearExtractedTransactions()
     */
    public void clearExtractedTransactions() {
        glPendingTransactionDao.clearExtractedTransactions();
    }
    
     /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
}
