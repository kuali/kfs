package edu.arizona.kfs.module.cr.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.module.cr.CrConstants;
import edu.arizona.kfs.module.cr.CrParameterConstants;
import edu.arizona.kfs.module.cr.service.CheckReconciliationPendingTransactionService;
import edu.arizona.kfs.sys.KFSConstants;

public class CheckReconciliationPendingTransactionServiceImpl implements CheckReconciliationPendingTransactionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationPendingTransactionService.class);

    // Injected Objects

    private AccountingPeriodService accountingPeriodService;
    private BankService bankService;
    private BusinessObjectService businessObjectService;
    private ConfigurationService configurationService;
    private DateTimeService dateTimeService;
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private OffsetDefinitionService offsetDefinitionService;
    private ParameterService parameterService;

    // Service Objects

    private String clearingAccount;
    private String clearingObjectCode;
    private List<String> tradeInObjectCodes;

    // Spring Injection

    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }

    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    // Implemented Methods

    @Override
    public void generatePendingTransactionStop(PaymentGroup paymentGroup) {
        generatePendingTransaction(paymentGroup, CrConstants.DocumentTypeCodes.STOP_CHECK, false);
    }

    @Override
    public void generatePendingTransactionStale(PaymentGroup paymentGroup) {
        generatePendingTransaction(paymentGroup, CrConstants.DocumentTypeCodes.STALE_CHECK, true);
    }

    @Override
    public void generatePendingTransactionCancel(PaymentGroup paymentGroup) {
        generatePendingTransaction(paymentGroup, CrConstants.DocumentTypeCodes.CANCEL_CHECK, false);
    }

    // Service Object Initializers

    private String getClearingAccount() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getting Clearing Account");
        }
        if (clearingAccount == null) {
            clearingAccount = getParameter(CrParameterConstants.CR_NAMESPACE_CODE, CrParameterConstants.CHECK_RECONCILIATION_TRANSACTION_STEP_COMPONENT, CrParameterConstants.CheckReconciliationTransactionStep.CLEARING_ACCOUNT);
        }
        return clearingAccount;
    }

    private String getClearingObjectCode() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getting Clearing Object Code");
        }
        if (clearingObjectCode == null) {
            clearingObjectCode = getParameter(CrParameterConstants.CR_NAMESPACE_CODE, CrParameterConstants.CHECK_RECONCILIATION_TRANSACTION_STEP_COMPONENT, CrParameterConstants.CheckReconciliationTransactionStep.CLEARING_OBJECT_CODE);
        }
        return clearingObjectCode;
    }

    private List<String> getTradeInObjectCodes() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getting Trade In Object Codes");
        }
        if (tradeInObjectCodes == null) {
            tradeInObjectCodes = new ArrayList<String>();
            String tradeInCapitalObjectCode = getParameter(KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, ParameterConstants.DOCUMENT_COMPONENT, CrParameterConstants.PurapParameters.TRADE_IN_OBJECT_CODE_FOR_CAPITAL_ASSET);
            String tradeInCapitalLeaseObjectCode = getParameter(KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, ParameterConstants.DOCUMENT_COMPONENT, CrParameterConstants.PurapParameters.TRADE_IN_OBJECT_CODE_FOR_CAPITAL_LEASE);

            if (StringUtils.isNotBlank(tradeInCapitalObjectCode)) {
                tradeInObjectCodes.add(tradeInCapitalObjectCode);
            }

            if (StringUtils.isNotBlank(tradeInCapitalLeaseObjectCode)) {
                tradeInObjectCodes.add(tradeInCapitalObjectCode);
            }
        }
        return tradeInObjectCodes;
    }

    private String getParameter(String namespaceCode, String componentCode, String parameterName) {
        String retval = parameterService.getParameterValueAsString(namespaceCode, componentCode, parameterName);
        if (StringUtils.isBlank(retval)) {
            throw new RuntimeException("System parameter " + namespaceCode + " " + componentCode + " " + parameterName + " is null.");
        }
        return retval;
    }

    // Private methods

    private void generatePendingTransaction(PaymentGroup paymentGroup, String financialDocumentTypeCode, boolean stale) {
        List<PaymentAccountDetail> accountListings = new ArrayList<PaymentAccountDetail>();

        for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
            accountListings.addAll(paymentDetail.getAccountDetail());
        }

        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();

        for (PaymentAccountDetail paymentAccountDetail : accountListings) {
            GlPendingTransaction glPendingTransaction = generateBasePendingTransaction(paymentGroup, financialDocumentTypeCode, stale, paymentAccountDetail, sequenceHelper.getSequenceCounter());

            Boolean relieveLiabilities = paymentGroup.getBatch().getCustomerProfile().getRelieveLiabilities();
            if (!stale && (relieveLiabilities != null) && (relieveLiabilities.booleanValue()) && paymentAccountDetail.getPaymentDetail().getFinancialDocumentTypeCode() != null) {
                OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(glPendingTransaction.getUniversityFiscalYear(), glPendingTransaction.getChartOfAccountsCode(), paymentAccountDetail.getPaymentDetail().getFinancialDocumentTypeCode(), glPendingTransaction.getFinancialBalanceTypeCode());
                if (offsetDefinition != null) {
                    glPendingTransaction.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
                }
                glPendingTransaction.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }

            flexibleOffsetAccountService.updateOffset(glPendingTransaction);
            businessObjectService.save(glPendingTransaction);
            sequenceHelper.increment();

            if (bankService.isBankSpecificationEnabled()) {
                generateBankOffsetEntry(paymentGroup, glPendingTransaction, sequenceHelper);
            }
        }
    }

    private GlPendingTransaction generateBasePendingTransaction(PaymentGroup paymentGroup, String financialDocumentTypeCode, boolean stale, PaymentAccountDetail paymentAccountDetail, int sequenceNumber) {
        Date transactionTimestamp = new Date(dateTimeService.getCurrentDate().getTime());
        AccountingPeriod fiscalPeriod = accountingPeriodService.getByDate(new Date(transactionTimestamp.getTime()));
        String accountNumber = generateAccountNumber(paymentAccountDetail, stale);
        String financialObjectCode = generateFinancialObjectCode(paymentAccountDetail, stale);
        String debitCreditCode = generateDebitCreditCode(paymentAccountDetail);
        String trnDesc = generateTransactionDescription(paymentGroup, paymentAccountDetail);

        GlPendingTransaction glPendingTransaction = new GlPendingTransaction();
        glPendingTransaction.setSequenceNbr(new KualiInteger(sequenceNumber));
        glPendingTransaction.setFdocRefTypCd(paymentAccountDetail.getPaymentDetail().getFinancialDocumentTypeCode());
        glPendingTransaction.setFsRefOriginCd(paymentAccountDetail.getPaymentDetail().getFinancialSystemOriginCode());
        glPendingTransaction.setFinancialBalanceTypeCode(CrConstants.BALANCE_TYPE_ACTUAL);
        glPendingTransaction.setTransactionDt(transactionTimestamp);
        glPendingTransaction.setUniversityFiscalYear(fiscalPeriod.getUniversityFiscalYear());
        glPendingTransaction.setUnivFiscalPrdCd(fiscalPeriod.getUniversityFiscalPeriodCode());
        glPendingTransaction.setSubAccountNumber(paymentAccountDetail.getSubAccountNbr());
        glPendingTransaction.setChartOfAccountsCode(paymentAccountDetail.getFinChartCode());
        glPendingTransaction.setFdocNbr(paymentGroup.getDisbursementNbr().toString());
        glPendingTransaction.setFinancialDocumentTypeCode(financialDocumentTypeCode);
        glPendingTransaction.setFsOriginCd(PdpConstants.PDP_FDOC_ORIGIN_CODE);
        glPendingTransaction.setAccountNumber(accountNumber);
        glPendingTransaction.setProjectCd(paymentAccountDetail.getProjectCode());
        glPendingTransaction.setAmount(paymentAccountDetail.getAccountNetAmount().abs());
        glPendingTransaction.setOrgDocNbr(paymentAccountDetail.getPaymentDetail().getOrganizationDocNbr());
        glPendingTransaction.setOrgReferenceId(paymentAccountDetail.getOrgReferenceId());
        glPendingTransaction.setFdocRefNbr(paymentAccountDetail.getPaymentDetail().getCustPaymentDocNbr());
        glPendingTransaction.setDebitCrdtCd(debitCreditCode);
        glPendingTransaction.setDescription(trnDesc);
        glPendingTransaction.setFinancialObjectCode(financialObjectCode);
        glPendingTransaction.setFinancialSubObjectCode(paymentAccountDetail.getFinSubObjectCode());

        return glPendingTransaction;

    }

    private String generateAccountNumber(PaymentAccountDetail paymentAccountDetail, boolean stale) {
        if (stale) {
            return getClearingAccount();
        }
        return paymentAccountDetail.getAccountNbr();
    }

    private String generateFinancialObjectCode(PaymentAccountDetail paymentAccountDetail, boolean stale) {
        if (stale) {
            return getClearingObjectCode();
        }
        return paymentAccountDetail.getFinObjectCode();

    }

    private String generateDebitCreditCode(PaymentAccountDetail paymentAccountDetail) {

        List<String> tradeInObjectCodes = getTradeInObjectCodes();
        if (tradeInObjectCodes.contains(paymentAccountDetail.getFinObjectCode())) {
            if (paymentAccountDetail.getPaymentDetail().getNetPaymentAmount().bigDecimalValue().signum() >= 0) {
                return KFSConstants.GL_DEBIT_CODE;
            } else {
                return KFSConstants.GL_CREDIT_CODE;
            }
        }
        if (paymentAccountDetail.getPaymentDetail().getNetPaymentAmount().bigDecimalValue().signum() >= 0) {
            return (KFSConstants.GL_CREDIT_CODE);
        }
        return (KFSConstants.GL_DEBIT_CODE);

    }

    private String generateTransactionDescription(PaymentGroup paymentGroup, PaymentAccountDetail paymentAccountDetail) {
        StringBuilder retval = new StringBuilder();

        String payeeName = paymentGroup.getPayeeName();
        if (payeeName.length() > 40) {
            retval.append(payeeName.substring(0, 40));
        } else {
            retval.append(StringUtils.rightPad(payeeName, 40));
        }

        if (paymentAccountDetail.getPaymentDetail() != null && StringUtils.isNotBlank(paymentAccountDetail.getPaymentDetail().getPurchaseOrderNbr())) {
            String poNbr = paymentAccountDetail.getPaymentDetail().getPurchaseOrderNbr();
            retval.append(" ");
            if (poNbr.length() > 9) {
                retval.append(poNbr.substring(0, 9));
            } else {
                retval.append(StringUtils.rightPad(poNbr, 9));
            }
        }

        if (paymentAccountDetail.getPaymentDetail() != null && StringUtils.isNotBlank(paymentAccountDetail.getPaymentDetail().getInvoiceNbr())) {
            String invoiceNbr = paymentAccountDetail.getPaymentDetail().getInvoiceNbr();
            retval.append(" ");
            if (invoiceNbr.length() > 14) {
                retval.append(invoiceNbr.substring(0, 14));
            } else {
                retval.append(StringUtils.rightPad(invoiceNbr, 14));
            }
        }

        retval.setLength(40);
        return retval.toString();
    }

    /**
     * Generates the bank offset for an entry (when enabled in the system)
     *
     * @param paymentGroup
     *            PaymentGroup for which entries are being generated, contains the Bank
     * @param glPendingTransaction
     *            PDP entry created for payment detail
     * @param sequenceHelper
     *            holds current entry sequence value
     */
    private void generateBankOffsetEntry(PaymentGroup paymentGroup, GlPendingTransaction glPendingTransaction, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        GlPendingTransaction bankPendingTransaction = new GlPendingTransaction();
        Bank bank = paymentGroup.getBank();
        String description = configurationService.getPropertyValueAsString(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET);
        String subAccountNumber = StringUtils.isBlank(bank.getCashOffsetSubAccountNumber()) ? KFSConstants.getDashSubAccountNumber() : bank.getCashOffsetSubAccountNumber();
        String financialSubObjectCode = StringUtils.isBlank(bank.getCashOffsetSubObjectCode()) ? KFSConstants.getDashFinancialSubObjectCode() : bank.getCashOffsetSubObjectCode();
        String debitCreditCode = KFSConstants.GL_CREDIT_CODE.equals(glPendingTransaction.getDebitCrdtCd()) ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE;

        bankPendingTransaction.setSequenceNbr(new KualiInteger(sequenceHelper.getSequenceCounter()));
        bankPendingTransaction.setFdocRefTypCd(null);
        bankPendingTransaction.setFsRefOriginCd(null);
        bankPendingTransaction.setFinancialBalanceTypeCode(CrConstants.BALANCE_TYPE_ACTUAL);
        bankPendingTransaction.setTransactionDt(glPendingTransaction.getTransactionDt());
        bankPendingTransaction.setUniversityFiscalYear(glPendingTransaction.getUniversityFiscalYear());
        bankPendingTransaction.setUnivFiscalPrdCd(glPendingTransaction.getUnivFiscalPrdCd());
        bankPendingTransaction.setFinancialDocumentTypeCode(glPendingTransaction.getFinancialDocumentTypeCode());
        bankPendingTransaction.setFsOriginCd(glPendingTransaction.getFsOriginCd());
        bankPendingTransaction.setFdocNbr(glPendingTransaction.getFdocNbr());
        bankPendingTransaction.setChartOfAccountsCode(bank.getCashOffsetFinancialChartOfAccountCode());
        bankPendingTransaction.setAccountNumber(bank.getCashOffsetAccountNumber());
        bankPendingTransaction.setSubAccountNumber(subAccountNumber);
        bankPendingTransaction.setFinancialObjectCode(bank.getCashOffsetObjectCode());
        bankPendingTransaction.setFinancialSubObjectCode(financialSubObjectCode);
        bankPendingTransaction.setProjectCd(KFSConstants.getDashProjectCode());
        bankPendingTransaction.setDebitCrdtCd(debitCreditCode);
        bankPendingTransaction.setAmount(glPendingTransaction.getAmount());
        bankPendingTransaction.setDescription(description);
        bankPendingTransaction.setOrgDocNbr(glPendingTransaction.getOrgDocNbr());
        bankPendingTransaction.setOrgReferenceId(null);
        bankPendingTransaction.setFdocRefNbr(null);

        this.businessObjectService.save(bankPendingTransaction);

        sequenceHelper.increment();
    }
}
