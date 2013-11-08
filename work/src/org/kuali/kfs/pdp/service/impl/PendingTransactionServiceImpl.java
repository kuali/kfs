/*
 * Copyright 2007 The Kuali Foundation
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
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.dataaccess.PendingTransactionDao;
import org.kuali.kfs.pdp.service.PdpUtilService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.pdp.service.ResearchParticipantPaymentValidationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatterLiteral;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.pdp.service.PendingTransactionService
 */
@Transactional
public class PendingTransactionServiceImpl implements PendingTransactionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PendingTransactionServiceImpl.class);

    protected static String FDOC_TYP_CD_PROCESS_ACH = "ACHD";
    protected static String FDOC_TYP_CD_PROCESS_CHECK = "CHKD";
    protected static String FDOC_TYP_CD_CANCEL_REISSUE_ACH = "ACHR";
    protected static String FDOC_TYP_CD_CANCEL_REISSUE_CHECK = "CHKR";
    protected static String FDOC_TYP_CD_CANCEL_ACH = "ACHC";
    protected static String FDOC_TYP_CD_CANCEL_CHECK = "CHKC";

    private PendingTransactionDao glPendingTransactionDao;
    private AccountingPeriodService accountingPeriodService;
    private DateTimeService dateTimeService;
    private ConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private BankService bankService;
    protected DataDictionaryService dataDictionaryService;
    protected ParameterService parameterService;
    protected ResearchParticipantPaymentValidationService researchParticipantPaymentValidationService;
    private PdpUtilService pdpUtilService;

    public PendingTransactionServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#generatePaymentGeneralLedgerPendingEntry(org.kuali.kfs.pdp.businessobject.PaymentGroup)
     */
    @Override
    public void generatePaymentGeneralLedgerPendingEntry(PaymentGroup paymentGroup) {
        this.populatePaymentGeneralLedgerPendingEntry(paymentGroup, FDOC_TYP_CD_PROCESS_ACH, FDOC_TYP_CD_PROCESS_CHECK, false);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#generateCancellationGeneralLedgerPendingEntry(org.kuali.kfs.pdp.businessobject.PaymentGroup)
     */
    @Override
    public void generateCancellationGeneralLedgerPendingEntry(PaymentGroup paymentGroup) {
        this.populatePaymentGeneralLedgerPendingEntry(paymentGroup, FDOC_TYP_CD_CANCEL_ACH, FDOC_TYP_CD_CANCEL_CHECK, true);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#generateReissueGeneralLedgerPendingEntry(org.kuali.kfs.pdp.businessobject.PaymentGroup)
     */
    @Override
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
            glPendingTransaction.setDebitCrdtCd(pdpUtilService.isDebit(paymentAccountDetail, reversal) ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
            glPendingTransaction.setAmount(paymentAccountDetail.getAccountNetAmount().abs());

            //Changes for Research Participant Upload
            String trnDesc = StringUtils.EMPTY;
            CustomerProfile customerProfile = paymentGroup.getBatch().getCustomerProfile();


            if (researchParticipantPaymentValidationService.isResearchParticipantPayment(customerProfile)) {
                BusinessObjectEntry businessObjectEntry = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(PaymentDetail.class.getName());
                AttributeDefinition attributeDefinition = businessObjectEntry.getAttributeDefinition("paymentGroup.payeeName");
                AttributeSecurity originalPayeeNameAttributeSecurity = attributeDefinition.getAttributeSecurity();
                //This is a temporary work around for an issue introduced with KFSCNTRB-705.
                if (ObjectUtils.isNotNull(originalPayeeNameAttributeSecurity)) {
                    String maskLiteral = ((MaskFormatterLiteral) originalPayeeNameAttributeSecurity.getMaskFormatter()).getLiteral();
                    trnDesc = maskLiteral;
                }
            }
            else {
                String payeeName = paymentGroup.getPayeeName();
                if (StringUtils.isNotBlank(payeeName)) {
                    trnDesc = payeeName.length() > 40 ? payeeName.substring(0, 40) : StringUtils.rightPad(payeeName, 40);
                }

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
            }
            glPendingTransaction.setDescription(trnDesc);

            glPendingTransaction.setOrgDocNbr(paymentAccountDetail.getPaymentDetail().getOrganizationDocNbr());
            glPendingTransaction.setOrgReferenceId(paymentAccountDetail.getOrgReferenceId());
            glPendingTransaction.setFdocRefNbr(paymentAccountDetail.getPaymentDetail().getCustPaymentDocNbr());

            // update the offset account if necessary
            SpringContext.getBean(FlexibleOffsetAccountService.class).updateOffset(glPendingTransaction);

            this.businessObjectService.save(glPendingTransaction);

            sequenceHelper.increment();

            if (bankService.isBankSpecificationEnabled()) {
                this.populateBankOffsetEntry(paymentGroup, glPendingTransaction, sequenceHelper);
            }
        }
    }

    /**
     * Generates the bank offset for an entry (when enabled in the system)
     *
     * @param paymentGroup PaymentGroup for which entries are being generated, contains the Bank
     * @param glPendingTransaction PDP entry created for payment detail
     * @param sequenceHelper holds current entry sequence value
     */
    protected void populateBankOffsetEntry(PaymentGroup paymentGroup, GlPendingTransaction glPendingTransaction, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        GlPendingTransaction bankPendingTransaction = new GlPendingTransaction();

        bankPendingTransaction.setSequenceNbr(new KualiInteger(sequenceHelper.getSequenceCounter()));
        bankPendingTransaction.setFdocRefTypCd(null);
        bankPendingTransaction.setFsRefOriginCd(null);
        bankPendingTransaction.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        bankPendingTransaction.setTransactionDt(glPendingTransaction.getTransactionDt());
        bankPendingTransaction.setUniversityFiscalYear(glPendingTransaction.getUniversityFiscalYear());
        bankPendingTransaction.setUnivFiscalPrdCd(glPendingTransaction.getUnivFiscalPrdCd());
        bankPendingTransaction.setFinancialDocumentTypeCode(glPendingTransaction.getFinancialDocumentTypeCode());
        bankPendingTransaction.setFsOriginCd(glPendingTransaction.getFsOriginCd());
        bankPendingTransaction.setFdocNbr(glPendingTransaction.getFdocNbr());

        Bank bank = paymentGroup.getBank();
        bankPendingTransaction.setChartOfAccountsCode(bank.getCashOffsetFinancialChartOfAccountCode());
        bankPendingTransaction.setAccountNumber(bank.getCashOffsetAccountNumber());
        if (StringUtils.isBlank(bank.getCashOffsetSubAccountNumber())) {
            bankPendingTransaction.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        else {
            bankPendingTransaction.setSubAccountNumber(bank.getCashOffsetSubAccountNumber());
        }

        bankPendingTransaction.setFinancialObjectCode(bank.getCashOffsetObjectCode());
        if (StringUtils.isBlank(bank.getCashOffsetSubObjectCode())) {
            bankPendingTransaction.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            bankPendingTransaction.setFinancialSubObjectCode(bank.getCashOffsetSubObjectCode());
        }
        bankPendingTransaction.setProjectCd(KFSConstants.getDashProjectCode());

        if (KFSConstants.GL_CREDIT_CODE.equals(glPendingTransaction.getDebitCrdtCd())) {
            bankPendingTransaction.setDebitCrdtCd(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            bankPendingTransaction.setDebitCrdtCd(KFSConstants.GL_CREDIT_CODE);
        }
        bankPendingTransaction.setAmount(glPendingTransaction.getAmount());

        String description = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET);
        bankPendingTransaction.setDescription(description);
        bankPendingTransaction.setOrgDocNbr(glPendingTransaction.getOrgDocNbr());
        bankPendingTransaction.setOrgReferenceId(null);
        bankPendingTransaction.setFdocRefNbr(null);

        this.businessObjectService.save(bankPendingTransaction);

        sequenceHelper.increment();
    }

    /**
     * Gets the bankService attribute.
     *
     * @return Returns the bankService.
     */
    protected BankService getBankService() {
        return bankService;
    }

    /**
     * Sets the bankService attribute value.
     *
     * @param bankService The bankService to set.
     */
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
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
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#save(org.kuali.kfs.pdp.businessobject.GlPendingTransaction)
     */
    @Override
    public void save(GlPendingTransaction tran) {
        LOG.debug("save() started");

        this.businessObjectService.save(tran);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#getUnextractedTransactions()
     */
    @Override
    public Iterator<GlPendingTransaction> getUnextractedTransactions() {
        LOG.debug("getUnextractedTransactions() started");

        return glPendingTransactionDao.getUnextractedTransactions();
    }

    /**
     * @see org.kuali.kfs.pdp.service.PendingTransactionService#clearExtractedTransactions()
     */
    @Override
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

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public ResearchParticipantPaymentValidationService getResearchParticipantPaymentValidationService() {
        return researchParticipantPaymentValidationService;
    }

    public void setResearchParticipantPaymentValidationService(ResearchParticipantPaymentValidationService researchParticipantPaymentValidationService) {
        this.researchParticipantPaymentValidationService = researchParticipantPaymentValidationService;
    }


    /**
     * Sets the pdp util service
     *
     * @param pdpUtilService
     */
    public void setPdpUtilService(PdpUtilService pdpUtilService) {
        this.pdpUtilService = pdpUtilService;
    }

}
