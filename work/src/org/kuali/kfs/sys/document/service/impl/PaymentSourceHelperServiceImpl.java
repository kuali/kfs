/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.service.impl;

import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;

import java.util.Properties;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PurchasingPaymentDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.WireCharge;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class PaymentSourceHelperServiceImpl implements PaymentSourceHelperService {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentSourceHelperServiceImpl.class);

    protected UniversityDateService universityDateService;
    protected BusinessObjectService businessObjectService;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected BankService bankService;
    protected AccountingDocumentRuleHelperService accountingDocumentRuleHelperService;
    protected ParameterService parameterService;
    protected ConfigurationService configurationService;

    /**
     * Retrieves the wire transfer information for the current fiscal year.
     *
     * @return <code>WireCharge</code>
     */
    @Override
    public WireCharge retrieveCurrentYearWireCharge() {
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(getUniversityDateService().getCurrentFiscalYear());

        wireCharge = (WireCharge) getBusinessObjectService().retrieve(wireCharge);
        if (wireCharge == null) {
            LOG.error("Wire charge information not found for current fiscal year.");
            throw new RuntimeException("Wire charge information not found for current fiscal year.");
        }

        return wireCharge;
    }

    /**
     * Builds an explicit and offset for the wire charge debit. The account associated with the first accounting is used for the
     * debit. The explicit and offset entries for the first accounting line and copied and customized for the wire charge.
     *
     * @param dvDocument submitted disbursement voucher document
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param wireCharge wireCharge object from current fiscal year
     * @return GeneralLedgerPendingEntry generated wire charge debit
     */
    @Override
    public GeneralLedgerPendingEntry processWireChargeDebitEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge) {
        LOG.debug("processWireChargeDebitEntries started");

        // grab the explicit entry for the first accounting line and adjust for wire charge entry
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry(paymentSource.getGeneralLedgerPendingEntry(0));
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setFinancialObjectCode(wireCharge.getExpenseFinancialObjectCode());
        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
        explicitEntry.setTransactionDebitCreditCode(GL_DEBIT_CODE);

        String objectTypeCode = SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getFinObjTypeExpenditureexpCd();
        explicitEntry.setFinancialObjectTypeCode(objectTypeCode);

        String originationCode = SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode();
        explicitEntry.setFinancialSystemOriginationCode(originationCode);

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(paymentSource.getWireTransfer().getBankCountryCode())) {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getDomesticChargeAmt());
        }
        else {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getForeignChargeAmt());
        }

        explicitEntry.setTransactionLedgerEntryDescription("Automatic debit for wire transfer fee");

        paymentSource.addPendingEntry(explicitEntry);
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(paymentSource.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        paymentSource.addPendingEntry(offsetEntry);
        sequenceHelper.increment();

        return explicitEntry;
    }

    /**
     * Builds an explicit and offset for the wire charge credit. The account and income object code found in the wire charge table
     * is used for the entry.
     *
     * @param dvDocument submitted disbursement voucher document
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param chargeEntry GLPE charge
     * @param wireCharge wireCharge object from current fiscal year
     */
    @Override
    public void processWireChargeCreditEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge, GeneralLedgerPendingEntry chargeEntry) {
        LOG.debug("processWireChargeCreditEntries started");

        // copy the charge entry and adjust for credit
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry(chargeEntry);
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setChartOfAccountsCode(wireCharge.getChartOfAccountsCode());
        explicitEntry.setAccountNumber(wireCharge.getAccountNumber());
        explicitEntry.setFinancialObjectCode(wireCharge.getIncomeFinancialObjectCode());

        // retrieve object type
        ObjectCode objectCode = wireCharge.getIncomeFinancialObject();
        explicitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

        explicitEntry.setTransactionDebitCreditCode(GL_CREDIT_CODE);

        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
        explicitEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber());
        explicitEntry.setProjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode());

        explicitEntry.setTransactionLedgerEntryDescription("Automatic credit for wire transfer fee");

        paymentSource.addPendingEntry(explicitEntry);
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(paymentSource.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        paymentSource.addPendingEntry(offsetEntry);
        sequenceHelper.increment();
    }

    /**
     * If bank specification is enabled generates bank offsetting entries for the document amount
     *
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @param paymentMethodCode the payment method of this DV
     */
    @Override
    public boolean generateDocumentBankOffsetEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String wireTransferOrForeignDraftEntryDocumentType) {
        boolean success = true;

        if (!getBankService().isBankSpecificationEnabled()) {
            return success;
        }

        paymentSource.refreshReferenceObject(KFSPropertyConstants.BANK);

        final KualiDecimal bankOffsetAmount = getGeneralLedgerPendingEntryService().getOffsetToCashAmount(paymentSource).negated();
        GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
        success &= getGeneralLedgerPendingEntryService().populateBankOffsetGeneralLedgerPendingEntry(paymentSource.getBank(), bankOffsetAmount, paymentSource, paymentSource.getPostingYear(), sequenceHelper, bankOffsetEntry, KRADConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.DISB_VCHR_BANK_CODE);

        if (success) {
            bankOffsetEntry.setTransactionLedgerEntryDescription(getAccountingDocumentRuleHelperService().formatProperty(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET));
            bankOffsetEntry.setFinancialDocumentTypeCode(wireTransferOrForeignDraftEntryDocumentType);
            paymentSource.addPendingEntry(bankOffsetEntry);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
            success &= getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(paymentSource.getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
            bankOffsetEntry.setFinancialDocumentTypeCode(wireTransferOrForeignDraftEntryDocumentType);
            paymentSource.addPendingEntry(offsetEntry);
            sequenceHelper.increment();
        }

        return success;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.PaymentSourceHelperService#generateDocumentBankOffsetEntries(org.kuali.kfs.sys.document.PaymentSource, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, java.lang.String, org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public boolean generateDocumentBankOffsetEntries(PaymentSource paymentSource, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String wireTransferOrForeignDraftEntryDocumentType, KualiDecimal bankOffsetAmount) {
        boolean success = true;

        if (!getBankService().isBankSpecificationEnabled()) {
            return success;
        }

        paymentSource.refreshReferenceObject(KFSPropertyConstants.BANK);

        GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
        success &= getGeneralLedgerPendingEntryService().populateBankOffsetGeneralLedgerPendingEntry(paymentSource.getBank(), bankOffsetAmount, paymentSource, paymentSource.getPostingYear(), sequenceHelper, bankOffsetEntry, KRADConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.DISB_VCHR_BANK_CODE);

        if (success) {
            bankOffsetEntry.setTransactionLedgerEntryDescription(getAccountingDocumentRuleHelperService().formatProperty(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET));
            bankOffsetEntry.setFinancialDocumentTypeCode(wireTransferOrForeignDraftEntryDocumentType);
            paymentSource.addPendingEntry(bankOffsetEntry);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
            success &= getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(paymentSource.getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
            bankOffsetEntry.setFinancialDocumentTypeCode(wireTransferOrForeignDraftEntryDocumentType);
            paymentSource.addPendingEntry(offsetEntry);
            sequenceHelper.increment();
        }

        return success;
    }

    /**
     * Builds a link to a PurchasingPaymentDetail object
     * @see org.kuali.kfs.sys.document.service.PaymentSourceHelperService#getDisbursementInfoUrl()
     */
    @Override
    public String getDisbursementInfoUrl() {
        final String basePath = getConfigurationService().getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        final String orgCode = getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_SBUNT_CODE);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PurchasingPaymentDetail.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");
        parameters.put(PdpPropertyConstants.PaymentDetail.PAYMENT_UNIT_CODE, orgCode);

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);

        return lookupUrl;
    }

    /**
     * @return the implementation of the UniversityDateService to use
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    /**
     * Sets the implementation of the UniversityDateService to use
     * @param universityDateService the implementation of the UniversityDateService to use
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * @return the implementation of the BusinessObjectService to use
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the implementation of the BusinessObjectService to use
     * @param businessObjectService the implementation of the BusinessObjectService to use
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @return the implementation of the GeneralLedgerPendingEntryService to use
     */
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    /**
     * Sets the implementation of the GeneralLedgerPendingEntryService to use
     * @param generalLedgerPendingEntryService the implementation of the GeneralLedgerPendingEntryService to use
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    /**
     * @return the implementation of the BankService to use
     */
    public BankService getBankService() {
        return bankService;
    }

    /**
     * Sets the implementation of the BankService to use
     * @param bankService the implementation of the BankService to use
     */
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    /**
     * @return the implementation of the AccountingDocumentRuleHelperService to use
     */
    public AccountingDocumentRuleHelperService getAccountingDocumentRuleHelperService() {
        return accountingDocumentRuleHelperService;
    }

    /**
     * Sets the implementation of the AccountingDocumentRuleHelperService to use
     * @param accountingDocumentRuleService the implementation of the AccountingDocumentRuleHelperService to use
     */
    public void setAccountingDocumentRuleHelperService(AccountingDocumentRuleHelperService accountingDocumentRuleService) {
        this.accountingDocumentRuleHelperService = accountingDocumentRuleService;
    }

    /**
     * @return the implementation of the ParameterService to use
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the implementation of the ParameterService to use
     * @param parameterService the implementation of the ParameterService to use
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * @return the implementation of the ConfigurationService to use
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Sets the implementation of the ConfigurationService to use
     * @param configurationService the implementation of the ConfigurationService to use
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
