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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.businessobject.PurchasingPaymentDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.WireCharge;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
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
    protected DocumentService documentService;
    protected OptionsService optionsService;
    protected HomeOriginationService homeOriginationService;

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

        String objectTypeCode = getOptionsService().getCurrentYearOptions().getFinObjTypeExpenditureexpCd();
        explicitEntry.setFinancialObjectTypeCode(objectTypeCode);

        String originationCode = getHomeOriginationService().getHomeOrigination().getFinSystemHomeOriginationCode();
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

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + KFSConstants.MAPPING_PORTAL + ".do");
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PurchasingPaymentDetail.class.getName());
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        parameters.put(KFSConstants.SUPPRESS_ACTIONS, "false");

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.LOOKUP_ACTION, parameters);

        return lookupUrl;
    }
    /**
    *
    * @see org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService#buildNoteForCheckStubText(java.lang.String)
    */
   @Override
   public PaymentNoteText buildNoteForCheckStubText(String text, int previousLineCount) {
       PaymentNoteText pnt = null;
       final String maxNoteLinesParam = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.MAX_NOTE_LINES);

       int maxNoteLines;
       try {
           maxNoteLines = Integer.parseInt(maxNoteLinesParam);
       }
       catch (NumberFormatException nfe) {
           throw new IllegalArgumentException("Invalid Max Notes Lines parameter, value: "+maxNoteLinesParam+" cannot be converted to an integer");
       }

       // The WordUtils should be sufficient for the majority of cases.  This method will
       // word wrap the whole string based on the MAX_NOTE_LINE_SIZE, separating each wrapped
       // word by a newline character.  The 'wrap' method adds line feeds to the end causing
       // the character length to exceed the max length by 1, hence the need for the replace
       // method before splitting.
       String   wrappedText = WordUtils.wrap(text, KFSConstants.MAX_NOTE_LINE_SIZE);
       String[] noteLines   = wrappedText.replaceAll("[\r]", "").split("\\n");

       // Loop through all the note lines.
       for (String noteLine : noteLines) {
           if (previousLineCount < (maxNoteLines - 3) && !StringUtils.isEmpty(noteLine)) {

               // This should only happen if we encounter a word that is greater than the max length.
               // The only concern I have for this occurring is with URLs/email addresses.
               if (noteLine.length() >KFSConstants.MAX_NOTE_LINE_SIZE) {
                   for (String choppedWord : chopWord(noteLine, KFSConstants.MAX_NOTE_LINE_SIZE)) {

                       // Make sure we're still under the maximum number of note lines.
                       if (previousLineCount < (maxNoteLines - 3) && !StringUtils.isEmpty(choppedWord)) {
                           pnt = new PaymentNoteText();
                           pnt.setCustomerNoteLineNbr(new KualiInteger(previousLineCount++));
                           pnt.setCustomerNoteText(choppedWord.replaceAll("\\n", "").trim());
                       }
                       // We can't add any additional note lines, or we'll exceed the maximum, therefore
                       // just break out of the loop early - there's nothing left to do.
                       else {
                           break;
                       }
                   }
               }
               // This should be the most common case.  Simply create a new PaymentNoteText,
               // add the line at the correct line location.
               else {
                   pnt = new PaymentNoteText();
                   pnt.setCustomerNoteLineNbr(new KualiInteger(previousLineCount++));
                   pnt.setCustomerNoteText(noteLine.replaceAll("\\n", "").trim());
               }
           }
       }
       return pnt;
   }

   /**
    * This method will take a word and simply chop into smaller
    * text segments that satisfy the limit requirements.  All words
    * brute force chopped, with no regard to preserving whole words.
    *
    * For example:
    *
    *      "Java is a fun programming language!"
    *
    * Might be chopped into:
    *
    *      "Java is a fun prog"
    *      "ramming language!"
    *
    * @param word The word that needs chopping
    * @param limit Number of character that should represent a chopped word
    * @return String [] of chopped words
    */
   protected String[] chopWord(String word, int limit) {
       StringBuilder builder = new StringBuilder();
       if (word != null && word.trim().length() > 0) {

           char[] chars = word.toCharArray();
           int index = 0;

           // First process all the words that fit into the limit.
           for (int i = 0; i < chars.length/limit; i++) {
               builder.append(String.copyValueOf(chars, index, limit));
               builder.append("\n");

               index += limit;
           }

           // Not all words will fit perfectly into the limit amount, so
           // calculate the modulus value to determine any remaining characters.
           int modValue =  chars.length%limit;
           if (modValue > 0) {
               builder.append(String.copyValueOf(chars, index, modValue));
           }

       }

       // Split the chopped words into individual segments.
       return builder.toString().split("\\n");
   }

   /**
    * @see org.kuali.kfs.sys.document.service.PaymentSourceHelperService#oppositifyAndSaveEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
    */
   @Override
   public void oppositifyAndSaveEntry(GeneralLedgerPendingEntry glpe, GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper) {
       if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
           glpe.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
       }
       else if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
           glpe.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
       }
       glpe.setTransactionLedgerEntrySequenceNumber(glpeSeqHelper.getSequenceCounter());
       glpeSeqHelper.increment();
       glpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
       businessObjectService.save(glpe);
   }

   /**
    *
    * @see org.kuali.kfs.sys.document.service.PaymentSourceHelperService#handleEntryCancellation(org.kuali.kfs.sys.document.PaymentSource)
    */
   @Override
   public void handleEntryCancellation(PaymentSource paymentSource, PaymentSourceToExtractService<?> extractionService) {
       if (ObjectUtils.isNull(paymentSource.getGeneralLedgerPendingEntries()) || paymentSource.getGeneralLedgerPendingEntries().size() == 0) {
           // generate all the pending entries for the document
           getGeneralLedgerPendingEntryService().generateGeneralLedgerPendingEntries(paymentSource);
           // for each pending entry, opposite-ify it and reattach it to the document
           GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper = new GeneralLedgerPendingEntrySequenceHelper();
           for (GeneralLedgerPendingEntry glpe : paymentSource.getGeneralLedgerPendingEntries()) {
               if (extractionService.shouldRollBackPendingEntry(glpe)) {
                   oppositifyAndSaveEntry(glpe, glpeSeqHelper);
               }
           }
       }
       else {
           List<GeneralLedgerPendingEntry> newGLPEs = new ArrayList<GeneralLedgerPendingEntry>();
           GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper = new GeneralLedgerPendingEntrySequenceHelper(paymentSource.getGeneralLedgerPendingEntries().size() + 1);
           for (GeneralLedgerPendingEntry glpe : paymentSource.getGeneralLedgerPendingEntries()) {
               glpe.refresh();
               if (extractionService.shouldRollBackPendingEntry(glpe)) {
                   if (glpe.getFinancialDocumentApprovedCode().equals(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED)) {
                       // damn! it got processed! well, make a copy, oppositify, and save
                       GeneralLedgerPendingEntry undoer = new GeneralLedgerPendingEntry(glpe);
                       oppositifyAndSaveEntry(undoer, glpeSeqHelper);
                       newGLPEs.add(undoer);
                   }
                   else {
                       // just delete the GLPE before anything happens to it
                       businessObjectService.delete(glpe);
                   }
               } else {
                   newGLPEs.add(glpe);
               }
           }
           paymentSource.setGeneralLedgerPendingEntries(newGLPEs);
       }
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

    /**
     * @return the injected implementation of the DocumentService to use
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Injects the implementation of DocumentService to use
     * @param documentService an implementation of DocumentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * @return the injected implementation of OptionsService to use
     */
    public OptionsService getOptionsService() {
        return optionsService;
    }

    /**
     * Injects the implementation of OptionsService to use
     * @param optionsService an implementation of OptionsService
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * @return the injected implementation of HomeOriginationSerice to use
     */
    public HomeOriginationService getHomeOriginationService() {
        return homeOriginationService;
    }

    /**
     * Injects the implementation of HomeOriginationService to use
     * @param homeOriginationService an implementation of HomeOriginationService
     */
    public void setHomeOriginationService(HomeOriginationService homeOriginationService) {
        this.homeOriginationService = homeOriginationService;
    }
}
