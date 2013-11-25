/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public class CashControlDocumentServiceImpl implements CashControlDocumentService {

    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private GeneralLedgerPendingEntryService glpeService;
    private OptionsService optionsService;
    private SystemInformationService systemInformationService;
    private DataDictionaryService dataDictionaryService;
    private ChartService chartService;
    private UniversityDateService universityDateService;
    private BankService bankService;
    private AccountingDocumentRuleHelperService accountingDocumentRuleHelperService;
    private DocumentTypeService documentTypeService;
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.CashControlDocumentService#createAndSavePaymentApplicationDocument(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CashControlDocument, org.kuali.kfs.module.ar.businessobject.CashControlDetail)
     */
    @Override
    public PaymentApplicationDocument createAndSavePaymentApplicationDocument(String description, CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException {

        // create a new PaymentApplicationdocument
        PaymentApplicationDocument doc = (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);

        // set a description to say that this application document has been created by the CashControldocument
        doc.getDocumentHeader().setDocumentDescription(description);

        // the line amount for the new PaymentApplicationDocument should be the line amount in the new cash control detail
        doc.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(cashControlDetail.getFinancialDocumentLineAmount());

        //  re-use the Processing Chart/Org from the CashControlDoc's arDocHeader
        String processingChartCode = cashControlDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrgCode = cashControlDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();

        //  create the arDocHeader
        // NOTE that we re-use the processing chart/org from the CashControl document, rather than
        // pull from the current user.  This is done to bypass some challenges in the Lockbox batch process,
        // and we dont believe it will impact the processing CashControl processing, as the PayApps created
        // always have the same processing chart/org as the cashcontrol doc that creates is
        AccountsReceivableDocumentHeader arDocHeader = new AccountsReceivableDocumentHeader();
        arDocHeader.setProcessingChartOfAccountCode(processingChartCode);
        arDocHeader.setProcessingOrganizationCode(processingOrgCode);
        arDocHeader.setDocumentNumber(doc.getDocumentNumber());
        arDocHeader.setCustomerNumber(cashControlDetail.getCustomerNumber());
        doc.setAccountsReceivableDocumentHeader(arDocHeader);

        doc.getDocumentHeader().setOrganizationDocumentNumber(cashControlDocument.getDocumentNumber());

        // refresh nonupdatable references and save the PaymentApplicationDocument
        doc.refreshNonUpdateableReferences();

        // This attribute is transient but is necessary to sort of bootstrap, allowing some validation rules
        // to succeed before the document is saved again.
        doc.setCashControlDetail(cashControlDetail);

//        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader2 = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
//        accountsReceivableDocumentHeader2.setDocumentNumber(doc.getDocumentNumber());
//        accountsReceivableDocumentHeader2.setCustomerNumber(cashControlDetail.getCustomerNumber());
//        doc.getNonAppliedHolding().setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader2);
//        doc.getNonAppliedHolding().setCustomerNumber(cashControlDetail.getCustomerNumber());
//        doc.getNonAppliedHolding().setReferenceFinancialDocumentNumber(doc.getDocumentNumber());
        doc.setNonAppliedHolding(null);

        documentService.saveDocument(doc);

        //RICE20 replaced searchableAttributeProcessingService.indexDocument with DocumentAttributeIndexingQueue.indexDocument
        DocumentType documentType = documentTypeService.getDocumentTypeByName(doc.getFinancialDocumentTypeCode());
        DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(documentType.getApplicationId());
        queue.indexDocument(doc.getDocumentNumber());

        return doc;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CashControlDocumentService#addNewCashControlDetail(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CashControlDocument, org.kuali.kfs.module.ar.businessobject.CashControlDetail)
     */
    @Override
    public void addNewCashControlDetail(String description, CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException {

        // add cash control detail
        cashControlDocument.addCashControlDetail(cashControlDetail);

        // create a new PaymentApplicationdocument
        // This has to happen after adding the cash control detail so that payment application saving rules succeed.
        PaymentApplicationDocument paymentApplicationDocument = createAndSavePaymentApplicationDocument(description, cashControlDocument, cashControlDetail);

        // update new cash control detail fields to refer to the new created PaymentApplicationDocument
        cashControlDetail.setReferenceFinancialDocument(paymentApplicationDocument);
        cashControlDetail.setReferenceFinancialDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        // newCashControlDetail.setStatus(doc.getDocumentHeader().getWorkflowDocument().getStatusDisplayValue());

        // Save the cash control document, but do NOT do a full workflow-save, just persist the state
        paymentApplicationDocument.populateDocumentForRouting();
        paymentApplicationDocument.prepareForSave();
        documentService.prepareWorkflowDocument(paymentApplicationDocument);
        documentService.saveDocument(cashControlDocument);

        //RICE20 replaced searchableAttributeProcessingService.indexDocument with DocumentAttributeIndexingQueue.indexDocument
        DocumentType cashDocumentType = documentTypeService.getDocumentTypeByName(cashControlDocument.getFinancialDocumentTypeCode());
        DocumentAttributeIndexingQueue cashQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(cashDocumentType.getApplicationId());
        cashQueue.indexDocument(cashControlDocument.getDocumentNumber());

        DocumentType payDocumentType = documentTypeService.getDocumentTypeByName(paymentApplicationDocument.getFinancialDocumentTypeCode());
        DocumentAttributeIndexingQueue payQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(payDocumentType.getApplicationId());
        payQueue.indexDocument(paymentApplicationDocument.getDocumentNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CashControlDocumentService#saveGLPEs(org.kuali.kfs.module.ar.document.CashControlDocument)
     */
    @Override
    public void saveGLPEs(CashControlDocument cashControlDocument) {
        businessObjectService.save(cashControlDocument.getGeneralLedgerPendingEntries());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CashControlDocumentService#createCashReceiptGLPEs(org.kuali.kfs.module.ar.document.CashControlDocument,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean createCashReceiptGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        boolean isSuccess = true;
        AccountingLine accountingLine = null;
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();

        // get accounts receivable document header to get processing chart of accounts and organization
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = cashControlDocument.getAccountsReceivableDocumentHeader();
        String processingChartOfAccountCode = accountsReceivableDocumentHeader.getProcessingChartOfAccountCode();
        String processingOrganizationCode = accountsReceivableDocumentHeader.getProcessingOrganizationCode();

        // get system information by processing chart of accounts and organization
        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", currentFiscalYear);
        criteria.put("processingChartOfAccountCode", processingChartOfAccountCode);
        criteria.put("processingOrganizationCode", processingOrganizationCode);
        SystemInformation systemInformation = businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        // if there is no system information for this processing chart of accounts and organization return false; glpes cannot be
        // created
        if (ObjectUtils.isNull(systemInformation)) {
            return false;
        }

        // get current year options
        SystemOptions options = optionsService.getCurrentYearOptions();

        // build an accounting line that will be used to create the glpe
        // ignore university clearing sub-object-code KULAR-633
        accountingLine = buildAccountingLine(systemInformation.getUniversityClearingAccountNumber(), systemInformation.getUniversityClearingSubAccountNumber(), systemInformation.getUniversityClearingObjectCode(), null, systemInformation.getUniversityClearingChartOfAccountsCode(), KFSConstants.GL_CREDIT_CODE, cashControlDocument.getCashControlTotalAmount());
        // get document type for the glpes
        String financialSystemDocumentTypeCode = getDataDictionaryService().getDocumentTypeNameByClass(cashControlDocument.getClass());
        // create and add the new explicit entry based on this accounting line
        explicitEntry = createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, financialSystemDocumentTypeCode);
        // create and add the offset entry
        isSuccess &= createAndAddTheOffsetEntry(cashControlDocument, explicitEntry, accountingLine, sequenceHelper);

        return isSuccess;
    }

    /**
     * Creates bank offset GLPEs for the cash control document
     * @param cashControlDocument the document to create cash control GLPEs for
     * @param sequenceHelper the sequence helper which will sequence the new GLPEs
     * @return true if the new bank offset GLPEs were created successfully, false otherwise
     */
    @Override
    public boolean createBankOffsetGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean isSuccess = true;
        if (bankService.isBankSpecificationEnabled()) {
            // get associated bank
            Bank bank = bankService.getByPrimaryId(cashControlDocument.getBankCode());
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            final KualiDecimal totalAmount = glpeService.getOffsetToCashAmount(cashControlDocument).negated();
            // add additional GLPE's based on bank code
            if (glpeService.populateBankOffsetGeneralLedgerPendingEntry(bank, totalAmount, cashControlDocument, cashControlDocument.getPostingYear(), sequenceHelper, bankOffsetEntry, ArPropertyConstants.CashControlDocumentFields.BANK_CODE)) {
                bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleHelperService.formatProperty(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET));
                cashControlDocument.addPendingEntry(bankOffsetEntry);
                sequenceHelper.increment();
                GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
                isSuccess &= glpeService.populateOffsetGeneralLedgerPendingEntry(cashControlDocument.getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                cashControlDocument.addPendingEntry(offsetEntry);
                sequenceHelper.increment();
            }
            else {
                isSuccess = false;
            }
        }
        return isSuccess;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CashControlDocumentService#createDistributionOfIncomeAndExpenseGLPEs(org.kuali.kfs.module.ar.document.CashControlDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean createDistributionOfIncomeAndExpenseGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean isSuccess = true;

        AccountingLine accountingLine = null;
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();

        // get the accounts receivable document header to get the processing chart of accounts code and organization
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = cashControlDocument.getAccountsReceivableDocumentHeader();
        String processingChartOfAccountCode = accountsReceivableDocumentHeader.getProcessingChartOfAccountCode();
        String processingOrganizationCode = accountsReceivableDocumentHeader.getProcessingOrganizationCode();

        // get system information by processing chart of acccounts and organization
        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", currentFiscalYear);
        criteria.put("processingChartOfAccountCode", processingChartOfAccountCode);
        criteria.put("processingOrganizationCode", processingOrganizationCode);
        SystemInformation systemInformation = businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        // if there is no system information set up for this processing chart of accounts and organization return false, glpes
        // cannot be created
        if (ObjectUtils.isNull(systemInformation)) {
            return false;
        }

        // get current year options
        SystemOptions options = optionsService.getCurrentYearOptions();

        // build dummy accounting line for gl population
        // ignore university clearing sub-object-code KULAR-633
        accountingLine = buildAccountingLine(systemInformation.getUniversityClearingAccountNumber(), systemInformation.getUniversityClearingSubAccountNumber(), systemInformation.getUniversityClearingObjectCode(), null, systemInformation.getUniversityClearingChartOfAccountsCode(), KFSConstants.GL_CREDIT_CODE, cashControlDocument.getCashControlTotalAmount());
        // get document type for the glpes
        String financialSystemDocumentTypeCode = getDataDictionaryService().getDocumentTypeNameByClass(cashControlDocument.getClass());
        // create and add the new explicit entry based on this accounting line
        explicitEntry = createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, financialSystemDocumentTypeCode);
        // create and add the offset entry
        isSuccess &= createAndAddTheOffsetEntry(cashControlDocument, explicitEntry, accountingLine, sequenceHelper);

        // get Advance Deposit accounting lines by getting Electronic Payment Claims
        Map criteria2 = new HashMap();
        criteria2.put("referenceFinancialDocumentNumber", cashControlDocument.getDocumentNumber());
        Collection<ElectronicPaymentClaim> electronicPaymentClaims = businessObjectService.findMatching(ElectronicPaymentClaim.class, criteria2);

        // no EFT claims found
        if (ObjectUtils.isNull(electronicPaymentClaims)) {
            return false;
        }

        // for each Advance Deposit accounting line, create a reverse GLPE in CashControl document.
        for (ElectronicPaymentClaim electronicPaymentClaim : electronicPaymentClaims ) {
            Map criteria3= new HashMap();
            criteria3.put("documentNumber", electronicPaymentClaim.getDocumentNumber());
            criteria3.put("sequenceNumber", electronicPaymentClaim.getFinancialDocumentLineNumber());
            criteria3.put("financialDocumentLineTypeCode", "F");
            SourceAccountingLine advanceDepositAccountingLine = businessObjectService.findByPrimaryKey(SourceAccountingLine.class, criteria3);

            // build dummy accounting line for gl creation
            accountingLine = buildAccountingLine(advanceDepositAccountingLine.getAccountNumber(), advanceDepositAccountingLine.getSubAccountNumber(), advanceDepositAccountingLine.getFinancialObjectCode(), advanceDepositAccountingLine.getFinancialSubObjectCode(), advanceDepositAccountingLine.getChartOfAccountsCode(), KFSConstants.GL_DEBIT_CODE, advanceDepositAccountingLine.getAmount());
            // create and add the new explicit entry based on this accounting line
            explicitEntry = createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, financialSystemDocumentTypeCode);
            // create and add the offset entry
            isSuccess &= createAndAddTheOffsetEntry(cashControlDocument, explicitEntry, accountingLine, sequenceHelper);
        }

        return isSuccess;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CashControlDocumentService#createGeneralErrorCorrectionGLPEs(org.kuali.kfs.module.ar.document.CashControlDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean createGeneralErrorCorrectionGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean isSuccess = true;

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        AccountingLine accountingLine = null;

        // get accounts receivable document header to get processing chart of accounts and organization code
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = cashControlDocument.getAccountsReceivableDocumentHeader();
        String processingChartOfAccountCode = accountsReceivableDocumentHeader.getProcessingChartOfAccountCode();
        String processingOrganizationCode = accountsReceivableDocumentHeader.getProcessingOrganizationCode();

        // get system information by processing chart of accounts and organization code
        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", currentFiscalYear);
        criteria.put("processingChartOfAccountCode", processingChartOfAccountCode);
        criteria.put("processingOrganizationCode", processingOrganizationCode);
        SystemInformation systemInformation = businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        // if no system information is set up for this processing chart of accounts and organization code return false, the glpes
        // cannot be created
        if (ObjectUtils.isNull(systemInformation)) {
            return false;
        }

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

        SystemOptions options = optionsService.getCurrentYearOptions();

        // build dummy accounting line for gl creation
        // ignore university clearing sub-object-code KULAR-633
        accountingLine = buildAccountingLine(systemInformation.getUniversityClearingAccountNumber(), systemInformation.getUniversityClearingSubAccountNumber(), systemInformation.getCreditCardObjectCode(), null, systemInformation.getUniversityClearingChartOfAccountsCode(), KFSConstants.GL_DEBIT_CODE, cashControlDocument.getCashControlTotalAmount());
        //get document type for the glpes
        String financialSystemDocumentTypeCode = getDataDictionaryService().getDocumentTypeNameByClass(cashControlDocument.getClass());
        // create and add the new explicit entry based on this accounting line
        createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, financialSystemDocumentTypeCode);

        // build dummy accounting line for gl creation
        // ignore university clearing sub-object-code KULAR-633
        accountingLine = buildAccountingLine(systemInformation.getUniversityClearingAccountNumber(), systemInformation.getUniversityClearingSubAccountNumber(), systemInformation.getUniversityClearingObjectCode(), null, systemInformation.getUniversityClearingChartOfAccountsCode(), KFSConstants.GL_CREDIT_CODE, cashControlDocument.getCashControlTotalAmount());
        // create and add the new explicit entry based on this accounting line
        createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, financialSystemDocumentTypeCode);

        return isSuccess;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CashControlDocumentService#getLockboxNumber(org.kuali.kfs.module.ar.document.CashControlDocument)
     */
    @Override
    public String getLockboxNumber(CashControlDocument cashControlDocument) {

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        String chartOfAccountsCode = cashControlDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrgCode = cashControlDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        Map criteria = new HashMap();
        criteria.put("universityFiscalYear", currentFiscalYear);
        criteria.put("processingChartOfAccountCode", chartOfAccountsCode);
        criteria.put("processingOrganizationCode", processingOrgCode);
        SystemInformation systemInformation = businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        return (systemInformation == null) ? null : systemInformation.getLockboxNumber();

    }

    /**
     * This method creates an accounting line.
     *
     * @param systemInformation System Information used to set accounting line data
     * @param debitOrCredit Tells if it is debit or credit
     * @param amount The amount
     * @return The created accounting line
     */
    protected AccountingLine buildAccountingLine(String accountNumber, String subAccountNumber, String objectCode, String subObjectCode, String chartOfAccountsCode, String debitOrCredit, KualiDecimal amount) {

        AccountingLine accountingLine = null;

        // get new accounting line
        try {
            accountingLine = SourceAccountingLine.class.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to access sourceAccountingLineClass", e);
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("unable to instantiate sourceAccountingLineClass", e);
        }

        accountingLine.setAccountNumber(accountNumber);
        accountingLine.setFinancialObjectCode(objectCode);
        accountingLine.setSubAccountNumber(subAccountNumber);
        accountingLine.setChartOfAccountsCode(chartOfAccountsCode);
        accountingLine.setFinancialSubObjectCode(subObjectCode);
        accountingLine.setDebitCreditCode(debitOrCredit);
        accountingLine.setAmount(amount);

        return accountingLine;

    }

    /**
     * This method creates and adds a new explicit glpe
     *
     * @param cashControlDocument the cash control document
     * @param sequenceHelper sequence helper
     * @param accountingLine the accounting line based on which the glpe is created
     * @param options the current year oprions
     * @param documentType the document type to be associated with the glpe
     */
    protected GeneralLedgerPendingEntry createAndAddNewExplicitEntry(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, SystemOptions options, String documentType) {

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

        glpeService.populateExplicitGeneralLedgerPendingEntry(cashControlDocument, accountingLine, sequenceHelper, explicitEntry);

        explicitEntry.setFinancialDocumentTypeCode(documentType);
        explicitEntry.setDocumentNumber(cashControlDocument.getDocumentNumber());

        // add the new explicit entry to the document now
        cashControlDocument.addPendingEntry(explicitEntry);

        cashControlDocument.customizeExplicitGeneralLedgerPendingEntry(accountingLine, explicitEntry);

        // increment the sequence counter
        sequenceHelper.increment();
        return explicitEntry;

    }

    /**
     * This method creates and adds an offset entry for the given explicit entry and accounting line.
     *
     * @param cashControlDocument the cash control document
     * @param explicitEntry the explicit entry for which we create the offset entry
     * @param accountingLine the accounting line used to populate the offset entry
     * @param sequenceHelper the sequence helper
     * @return true if successfuly created and added, false otherwise
     */
    protected boolean createAndAddTheOffsetEntry(CashControlDocument cashControlDocument, GeneralLedgerPendingEntry explicitEntry, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean isSuccess = true;

        // create offset
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        isSuccess &= glpeService.populateOffsetGeneralLedgerPendingEntry(cashControlDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
        cashControlDocument.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);

        // add the offset
        cashControlDocument.addPendingEntry(offsetEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        return isSuccess;
    }


    /**
     * This method gets the system information servcie
     * @return the system information service
     */
    public SystemInformationService getSystemInformationService() {
        return systemInformationService;
    }

    /**
     * This method sets the system information service
     * @param systemInformationService
     */
    public void setSystemInformationService(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    /**
     * This method gets the chart service
     * @return
     */
    public ChartService getChartService() {
        return chartService;
    }

    /**
     * This method sets the chart service
     * @param chartService
     */
    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * Gets the dataDictionaryService attribute.
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * This method gets the glpe service
     * @return
     */
    public GeneralLedgerPendingEntryService getGlpeService() {
        return glpeService;
    }

    /**
     * This method sets the glpe service
     * @param glpeService
     */
    public void setGlpeService(GeneralLedgerPendingEntryService glpeService) {
        this.glpeService = glpeService;
    }

    /**
     * This method gets the business object service
     * @return the business object service
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the business object service
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * This method gets the option service
     * @return the option service
     */
    public OptionsService getOptionsService() {
        return optionsService;
    }

    /**
     * This method sets the option service
     * @param optionsService
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * This method gets the accounts receivable header
     * @return the accounts receivable header
     */
    public AccountsReceivableDocumentHeaderService getAccountsReceivableDocumentHeaderService() {
        return accountsReceivableDocumentHeaderService;
    }

    /**
     * This method sets the accounts receivable header
     * @param accountsReceivableDocumentHeaderService
     */
    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    /**
     * This method gets the document service
     * @return the document service
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * This method sets the document service
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
    
    @NonTransactional
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
    
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
}
