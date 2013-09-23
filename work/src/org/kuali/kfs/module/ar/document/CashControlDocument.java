/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.gl.service.EntryService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.PaymentMedium;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.ElectronicPaymentClaiming;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashControlDocument extends GeneralLedgerPostingDocumentBase implements AmountTotaling, GeneralLedgerPendingEntrySource, ElectronicPaymentClaiming, GeneralLedgerPostingDocument {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocument.class);

    protected final static String GENERAL_LEDGER_POSTING_HELPER_BEAN_ID = "kfsGenericGeneralLedgerPostingHelper";
    protected static final String NODE_ASSOCIATED_WITH_ELECTRONIC_PAYMENT = "AssociatedWithElectronicPayment";

    protected String referenceFinancialDocumentNumber;
    protected Integer universityFiscalYear;
    protected String universityFiscalPeriodCode;
    protected String customerPaymentMediumCode;
    protected KualiDecimal cashControlTotalAmount = KualiDecimal.ZERO;
    protected String lockboxNumber;
    protected String bankCode;

    protected Bank bank;
    protected PaymentMedium customerPaymentMedium;
    protected AccountingPeriod universityFiscalPeriod;
    protected AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;

    protected List<CashControlDetail> cashControlDetails;
    protected List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;
    protected List<ElectronicPaymentClaim> electronicPaymentClaims;

    /**
     * Default constructor.
     */
    public CashControlDocument() {
        super();
        accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        customerPaymentMedium = new PaymentMedium();

        // Set the university fiscal year to the current values
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        universityFiscalYear = universityDateService.getCurrentUniversityDate().getUniversityFiscalYear();
        universityFiscalPeriod = universityDateService.getCurrentUniversityDate().getAccountingPeriod();
        universityFiscalPeriodCode = universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod();

        cashControlDetails = new ArrayList<CashControlDetail>();
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
        electronicPaymentClaims = new ArrayList<ElectronicPaymentClaim>();
        // retrieve value from param table and set to default
        try {
            DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);
            org.kuali.rice.krad.datadictionary.DocumentEntry docEntry = ddService.getDataDictionary().getDocumentEntry(ddService.getValidDocumentClassByTypeName(KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL).getCanonicalName());
            String documentTypeCode = docEntry.getDocumentTypeName();
            if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
                bankCode = SpringContext.getBean(ParameterService.class).getSubParameterValueAsString(Bank.class, KFSParameterKeyConstants.DEFAULT_BANK_BY_DOCUMENT_TYPE, documentTypeCode);
            }
        }
        catch (Exception x) {
            LOG.error("Problem occurred setting default bank code for cash control document", x);
        }
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    @Override
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     *
     * @return Returns the referenceFinancialDocumentNumber
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     *
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }


    /**
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the universityFiscalPeriodCode attribute.
     *
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     *
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }


    /**
     * Gets the customerPaymentMediumCode attribute.
     *
     * @return Returns the customerPaymentMediumCode
     */
    public String getCustomerPaymentMediumCode() {
        return customerPaymentMediumCode;
    }

    /**
     * Sets the customerPaymentMediumCode attribute.
     *
     * @param customerPaymentMediumCode The customerPaymentMediumCode to set.
     */
    public void setCustomerPaymentMediumCode(String customerPaymentMediumCode) {
        this.customerPaymentMediumCode = customerPaymentMediumCode;
    }


    /**
     * Gets the cashControlTotalAmount attribute.
     *
     * @return Returns the cashControlTotalAmount
     */
    public KualiDecimal getCashControlTotalAmount() {
        return cashControlTotalAmount;
    }

    /**
     * Sets the cashControlTotalAmount attribute.
     *
     * @param cashControlTotalAmount The cashControlTotalAmount to set.
     */
    public void setCashControlTotalAmount(KualiDecimal cashControlTotalAmount) {
        this.cashControlTotalAmount = cashControlTotalAmount;
    }

    /**
     * Gets the universityFiscalPeriod attribute.
     *
     * @return Returns the universityFiscalPeriod
     */
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod attribute.
     *
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     * @deprecated
     */
    @Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }

    /**
     * Gets the accountsReceivableDocumentHeader attribute.
     *
     * @return Returns the accountsReceivableDocumentHeader.
     */
    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    /**
     * Sets the accountsReceivableDocumentHeader attribute value.
     *
     * @param accountsReceivableDocumentHeader The accountsReceivableDocumentHeader to set.
     */
    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }

    /**
     * Gets the cashControlDetails attribute.
     *
     * @return Returns the cashControlDetails.
     */
    public List<CashControlDetail> getCashControlDetails() {
        return cashControlDetails;
    }

    /**
     * Sets the cashControlDetails attribute value.
     *
     * @param cashControlDetails The cashControlDetails to set.
     */
    public void setCashControlDetails(List<CashControlDetail> cashControlDetails) {
        this.cashControlDetails = cashControlDetails;
    }

    /**
     * This method adds a new cash control detail to the list
     *
     * @param cashControlDetail
     */
    public void addCashControlDetail(CashControlDetail cashControlDetail) {
        prepareCashControlDetail(cashControlDetail);
        if (cashControlDetail.getFinancialDocumentLineAmount() != null) {
            this.cashControlTotalAmount = this.cashControlTotalAmount.add(cashControlDetail.getFinancialDocumentLineAmount());
        }
        cashControlDetails.add(cashControlDetail);
    }

    /**
     * This method removes a cash control detail from the list
     *
     * @param index
     */
    public void deleteCashControlDetail(int index) {
        CashControlDetail cashControlDetail = cashControlDetails.remove(index);
        this.cashControlTotalAmount = this.cashControlTotalAmount.subtract(cashControlDetail.getFinancialDocumentLineAmount());
    }

    /**
     * This is a helper method that automatically populates document specfic information into the cash control detail deposit
     * (CashControlDetail) instance.
     */
    protected void prepareCashControlDetail(CashControlDetail cashControlDetail) {
        cashControlDetail.setDocumentNumber(this.getDocumentNumber());
    }

    /**
     * Gets the customerPaymentMedium attribute.
     *
     * @return Returns the customerPaymentMedium
     */
    public PaymentMedium getCustomerPaymentMedium() {
        return customerPaymentMedium;
    }

    /**
     * Sets the customerPaymentMedium attribute value.
     *
     * @param customerPaymentMedium The customerPaymentMedium to set.
     */
    public void setCustomerPaymentMedium(PaymentMedium customerPaymentMedium) {
        this.customerPaymentMedium = customerPaymentMedium;
    }

    /**
     * @see org.kuali.kfs.sys.document.AmountTotaling#getTotalDollarAmount()
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getCashControlTotalAmount();
    }

    /**
     * This method returns the advance deposit total amount as a currency formatted string.
     *
     * @return String
     */
    public String getCurrencyFormattedTotalCashControlAmount() {
        return (String) new CurrencyFormatter().format(getCashControlTotalAmount());
    }

    /**
     * Retrieves a specific cash control detail from the list, by array index
     *
     * @param index the index of the cash control details to retrieve the cash control detail from
     * @return a CashControlDetail
     */
    public CashControlDetail getCashControlDetail(int index) {
        if (index >= cashControlDetails.size()) {
            for (int i = cashControlDetails.size(); i <= index; i++) {
                cashControlDetails.add(new CashControlDetail());
            }
        }
        return cashControlDetails.get(index);
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#addPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void addPendingEntry(GeneralLedgerPendingEntry entry) {
        generalLedgerPendingEntries.add(entry);

    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#clearAnyGeneralLedgerPendingEntries()
     */
    @Override
    public void clearAnyGeneralLedgerPendingEntries() {
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();

    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        if (explicitEntry.getFinancialDocumentTypeCode().equalsIgnoreCase(KFSConstants.FinancialDocumentTypeCodes.GENERAL_ERROR_CORRECTION)) {
            explicitEntry.setTransactionLedgerEntryDescription(buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(postable));

            // Clearing fields that are already handled by the parent algorithm - we don't actually want
            // these to copy over from the accounting lines b/c they don't belong in the GLPEs
            // if the aren't nulled, then GECs fail to post
            explicitEntry.setReferenceFinancialDocumentNumber(null);
            explicitEntry.setReferenceFinancialSystemOriginationCode(null);
            explicitEntry.setReferenceFinancialDocumentTypeCode(null);
        }

    }

    /**
     * Builds an appropriately formatted string to be used for the <code>transactionLedgerEntryDescription</code>. It is built
     * using information from the <code>{@link AccountingLine}</code>. Format is "01-12345: blah blah blah".
     *
     * @param line accounting line
     * @param transactionalDocument submitted accounting document
     * @return String formatted string to be used for transaction ledger entry description
     */
    protected String buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(GeneralLedgerPendingEntrySourceDetail line) {
        String description = "";
        description = line.getReferenceOriginCode() + "-" + line.getReferenceNumber();

        if (StringUtils.isNotBlank(line.getFinancialDocumentLineDescription())) {
            description += ": " + line.getFinancialDocumentLineDescription();
        }
        else {
            description += ": " + getDocumentHeader().getDocumentDescription();
        }

        if (description.length() > GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH) {
            description = description.substring(0, GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH - 3) + "...";
        }

        return description;
    }

    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;
        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);

        if (this.getCustomerPaymentMediumCode().equalsIgnoreCase(ArConstants.PaymentMediumCode.CHECK)) {
            success &= cashControlDocumentService.createCashReceiptGLPEs(this, sequenceHelper);
            success &= cashControlDocumentService.createBankOffsetGLPEs(this, sequenceHelper);
        }
        else if (this.getCustomerPaymentMediumCode().equalsIgnoreCase(ArConstants.PaymentMediumCode.WIRE_TRANSFER)) {
            success &= cashControlDocumentService.createDistributionOfIncomeAndExpenseGLPEs(this, sequenceHelper);
        }
        else if (this.getCustomerPaymentMediumCode().equalsIgnoreCase(ArConstants.PaymentMediumCode.CREDIT_CARD)) {
            success &= cashControlDocumentService.createGeneralErrorCorrectionGLPEs(this, sequenceHelper);
        }

        return success;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPendingEntryAmountForGeneralLedgerPostable(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount().abs();
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#getGeneralLedgerPostables()
     */
    @Override
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        return new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
    }


    /**
     * The Cash Control document doesn't generate general ledger pending entries based off of the accounting lines on the document
     *
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#generateGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#getPostingYear()
     */
    @Override
    public Integer getPostingYear() {
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        AccountingLine accountingLine = (AccountingLine) postable;
        return (accountingLine.getDebitCreditCode().equalsIgnoreCase(KFSConstants.GL_DEBIT_CODE));
    }

    /**
     * This method gets the glpes
     *
     * @return a list of glpes
     */
    @Override
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }

    /**
     * This method sets the glpes
     *
     * @param generalLedgerPendingEntries
     */
    @Override
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.generalLedgerPendingEntries = generalLedgerPendingEntries;
    }

    /**
     * This method set glpes status to approved
     */
    @Override
    public void changeGeneralLedgerPendingEntriesApprovedStatusCode() {
        for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
            glpe.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
    }

    /**
     * This method gets an glpe by it's index in the list of glpes
     *
     * @param index the glpe index
     * @return the glpe
     */
    @Override
    public GeneralLedgerPendingEntry getGeneralLedgerPendingEntry(int index) {
        while (generalLedgerPendingEntries.size() <= index) {
            generalLedgerPendingEntries.add(new GeneralLedgerPendingEntry());
        }
        return generalLedgerPendingEntries.get(index);
    }

    public String getLockboxNumber() {
        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        this.lockboxNumber = cashControlDocumentService.getLockboxNumber(this);
        return lockboxNumber;
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {

        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        this.lockboxNumber = cashControlDocumentService.getLockboxNumber(this);
        super.populateDocumentForRouting();

    }

    /**
     * @see org.kuali.kfs.sys.document.ElectronicPaymentClaiming#declaimElectronicPaymentClaims()
     */
    @Override
    public void declaimElectronicPaymentClaims() {
        SpringContext.getBean(ElectronicPaymentClaimingService.class).declaimElectronicPaymentClaimsForDocument(this);
    }

    /**
     * This method gets electronicPaymentClaims
     *
     * @return electronicPaymentClaims
     */
    public List<ElectronicPaymentClaim> getElectronicPaymentClaims() {
        return electronicPaymentClaims;
    }

    /**
     * This method sets electronicPaymentClaims
     *
     * @param electronicPaymentClaims
     * @deprecated
     */
    @Deprecated
    public void setElectronicPaymentClaims(List<ElectronicPaymentClaim> electronicPaymentClaims) {
        this.electronicPaymentClaims = electronicPaymentClaims;
    }

    public Document getReferenceFinancialDocument() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        Document document = null;
        try {
            document = documentService.getByDocumentHeaderId(getReferenceFinancialDocumentNumber());
        }
        catch (WorkflowException we) {
            LOG.warn( "Unable to retreive reference financial document: " + getReferenceFinancialDocumentNumber(), we);
        }
        return document;
    }

    /**
     * Gets the bankCode attribute.
     *
     * @return Returns the bankCode.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bankCode attribute value.
     *
     * @param bankCode The bankCode to set.
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }


    /**
     * Answers true when document payment medium is WIRE transfer
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (NODE_ASSOCIATED_WITH_ELECTRONIC_PAYMENT.equals(nodeName)) {
            if (ArConstants.PaymentMediumCode.WIRE_TRANSFER.equals(getCustomerPaymentMediumCode())) {
                return true;
            }
            else {
                return false;
            }
        }
        return super.answerSplitNodeQuestion(nodeName);
    }

    /**
     * This is a helper method added to support workflow attribute configuration. This method helps to avoid attribute name mismatch
     * between ProcessingChartOfAccountCode and chartOfAccountsCode
     *
     * @return ProcessingChartOfAccountCode
     */
    public String getChartOfAccountsCode() {
        if (getAccountsReceivableDocumentHeader() != null) {
            return getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        }
        return null;
    }

    /**
     * This is a helper method added to support workflow attribute configuration. This method helps to avoid attribute name mismatch
     * between ProcessingOrganizationCode and organizationCode
     *
     * @return ProcessingOrganizationCode
     */
    public String getOrganizationCode() {
        if (getAccountsReceivableDocumentHeader() != null) {
            return getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        }
        return null;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public void recalculateTotals() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (CashControlDetail cashControlDetail : getCashControlDetails()) {
            total = total.add(cashControlDetail.getFinancialDocumentLineAmount());
        }
        cashControlTotalAmount = total;
        getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(total);
    }

    @Override
    public void prepareForSave() {

        // remove all the cash control detail records from the db in prep for the save,
        // where they'll get re-persisted. This is necessary to make sure that details
        // deleted on the form are actually deleted, as OJB does a terrible job at this
        // by itself.
        deleteCashControlDetailsFromDB();
        recalculateTotals();
    }

    protected void deleteCashControlDetailsFromDB() {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, String> pkMap = new HashMap<String, String>();
        pkMap.put("documentNumber", getDocumentNumber());
        boService.deleteMatching(CashControlDetail.class, pkMap);
    }

    /**
     * This is a method to check the count of gl entries according to the input fields and values
     *
     * @return totalGLRecordsCreated returns the count of the gl entries
     */
    public Integer getGeneralLedgerEntriesPostedCount() {
        Map<String, Object> pkMap = new HashMap<String, Object>();
        pkMap.put("documentNumber", this.getDocumentNumber());
        pkMap.put("chartOfAccountsCode", this.getChartOfAccountsCode());

        Integer totalGLRecordsCreated = SpringContext.getBean(EntryService.class).getEntryRecordCount(pkMap);

        return totalGLRecordsCreated;
    }
}
