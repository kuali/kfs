/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.service.impl;

import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.bo.NonAppliedHolding;
import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CashControlDocumentService;
import org.kuali.module.ar.service.SystemInformationService;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.module.financial.document.GeneralErrorCorrectionDocument;
import org.kuali.module.financial.service.UniversityDateService;

import edu.iu.uis.eden.exception.WorkflowException;

public class CashControlDocumentServiceImpl implements CashControlDocumentService {

    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private GeneralLedgerPendingEntryService glpeService;
    private OptionsService optionsService;
    private SystemInformationService systemInformationService;
    private DocumentTypeService documentTypeService;
    private ChartService chartService;
    private UniversityDateService universityDateService;

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#createAndSavePaymentApplicationDocument(java.lang.String,
     *      org.kuali.module.ar.document.CashControlDocument, org.kuali.module.ar.bo.CashControlDetail)
     */
    public PaymentApplicationDocument createAndSavePaymentApplicationDocument(String description, CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException {

        // create a new PaymentApplicationdocument
        PaymentApplicationDocument doc = (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);
        // set a description to say that this application document has been created by the CashControldocument
        doc.getDocumentHeader().setFinancialDocumentDescription(description);

        // set the non applied holding
        NonAppliedHolding nonAppliedHolding = new NonAppliedHolding();
        nonAppliedHolding.setReferenceFinancialDocumentNumber(doc.getDocumentNumber());
        doc.getNonAppliedHoldings().add(nonAppliedHolding);

        // the line amount for the new PaymentApplicationDocument should be the line amount in the new cash control detail
        doc.getDocumentHeader().setFinancialDocumentTotalAmount(cashControlDetail.getFinancialDocumentLineAmount());

        // refresh nonupdatable references and save the PaymentApplicationDocument
        doc.refreshNonUpdateableReferences();
        documentService.saveDocument(doc);
        return doc;
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#hasAllApplicationDocumentsApproved(org.kuali.module.ar.document.CashControlDocument)
     */
    public boolean hasAllApplicationDocumentsApproved(CashControlDocument cashControlDocument) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#addNewCashControlDetail(java.lang.String,
     *      org.kuali.module.ar.document.CashControlDocument, org.kuali.module.ar.bo.CashControlDetail)
     */
    public void addNewCashControlDetail(String description, CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException {

        // create a new PaymentApplicationdocument
        PaymentApplicationDocument doc = createAndSavePaymentApplicationDocument(description, cashControlDocument, cashControlDetail);

        // update new cash control detail fields to refer to the new created PaymentApplicationDocument
        cashControlDetail.setReferenceFinancialDocument(doc);
        cashControlDetail.setReferenceFinancialDocumentNumber(doc.getDocumentNumber());
        // newCashControlDetail.setStatus(doc.getDocumentHeader().getWorkflowDocument().getStatusDisplayValue());

        // add cash control detail
        cashControlDocument.addCashControlDetail(cashControlDetail);

    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#saveGLPEs(org.kuali.module.ar.document.CashControlDocument)
     */
    public void saveGLPEs(CashControlDocument cashControlDocument) {
        businessObjectService.save(cashControlDocument.getGeneralLedgerPendingEntries());
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#createCashReceiptGLPEs(org.kuali.module.ar.document.CashControlDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean createCashReceiptGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        boolean success = true;
        AccountingLine accountingLine = null;
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();

        // get accounts receivable document header to get processing chart of accounts and organization
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = cashControlDocument.getAccountsReceivableDocumentHeader();
        String processingChartOfAccountCode = accountsReceivableDocumentHeader.getProcessingChartOfAccountCode();
        String processingOrganizationCode = accountsReceivableDocumentHeader.getProcessingOrganizationCode();

        // get system information by processing chart of accounts and organization
        SystemInformation systemInformation = systemInformationService.getByPrimaryKey(currentFiscalYear, processingChartOfAccountCode, processingOrganizationCode);
        // if there is no system information for this processing chart of accounts and organization return false; glpes cannot be
        // created
        if (ObjectUtils.isNull(systemInformation)) {
            return false;
        }

        // get current year options
        Options options = optionsService.getCurrentYearOptions();

        // build an accounting line that will be used to create the glpe
        accountingLine = buildAccountingLine(systemInformation.getUniversityClearingAccountNumber(), systemInformation.getUniversityClearingSubAccountNumber(), systemInformation.getUniversityClearingObjectCode(), systemInformation.getUniversityClearingSubObjectCode(), systemInformation.getUniversityClearingChartOfAccountsCode(), KFSConstants.GL_CREDIT_CODE, cashControlDocument.getCashControlTotalAmount());
        // get document type for the glpes
        String documentType = documentTypeService.getDocumentTypeCodeByClass(CashReceiptDocument.class);
        // create and add the new explicit entry based on this accounting line
        explicitEntry = createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, documentType);
        // create and add the offset entry
        success &= createAndAddTheOffsetEntry(cashControlDocument, explicitEntry, accountingLine, sequenceHelper);

        return success;
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#createDistributionOfIncomeAndExpenseGLPEs(org.kuali.module.ar.document.CashControlDocument, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean createDistributionOfIncomeAndExpenseGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        AccountingLine accountingLine = null;
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();

        // get the accounts receivable document header to get the processing chart of accounts code and organization
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = cashControlDocument.getAccountsReceivableDocumentHeader();
        String processingChartOfAccountCode = accountsReceivableDocumentHeader.getProcessingChartOfAccountCode();
        String processingOrganizationCode = accountsReceivableDocumentHeader.getProcessingOrganizationCode();

        // get system information by processing chart of acccounts and organization
        SystemInformation systemInformation = systemInformationService.getByPrimaryKey(currentFiscalYear, processingChartOfAccountCode, processingOrganizationCode);

        // if there is no system information set up for this processing chart of accounts and organization return false, glpes
        // cannot be created
        if (ObjectUtils.isNull(systemInformation)) {
            return false;
        }

        // get current year oprions
        Options options = optionsService.getCurrentYearOptions();

        // build dummy accounting line for gl population
        accountingLine = buildAccountingLine(systemInformation.getUniversityClearingAccountNumber(), systemInformation.getUniversityClearingSubAccountNumber(), systemInformation.getUniversityClearingObjectCode(), systemInformation.getUniversityClearingSubObjectCode(), systemInformation.getUniversityClearingChartOfAccountsCode(), KFSConstants.GL_CREDIT_CODE, cashControlDocument.getCashControlTotalAmount());
        // get document type for the glpes
        String documentType = documentTypeService.getDocumentTypeCodeByClass(DistributionOfIncomeAndExpenseDocument.class);
        // create and add the new explicit entry based on this accounting line
        explicitEntry = createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, documentType);
        // create and add the offset entry
        success &= createAndAddTheOffsetEntry(cashControlDocument, explicitEntry, accountingLine, sequenceHelper);

        // build dummy accounting line for gl creation
        accountingLine = buildAccountingLine(systemInformation.getWireAccountNumber(), systemInformation.getWireSubAccountNumber(), systemInformation.getWireObjectCode(), systemInformation.getWireSubObjectCode(), systemInformation.getWireChartOfAccountsCode(), KFSConstants.GL_DEBIT_CODE, cashControlDocument.getCashControlTotalAmount());
        // create and add the new explicit entry based on this accounting line
        explicitEntry = createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, documentType);
        // create and add the offset entry
        success &= createAndAddTheOffsetEntry(cashControlDocument, explicitEntry, accountingLine, sequenceHelper);

        return success;
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#createGeneralErrorCorrectionGLPEs(org.kuali.module.ar.document.CashControlDocument, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean createGeneralErrorCorrectionGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        AccountingLine accountingLine = null;

        // get accounts receivable document header to get processing chart of accounts and organization code
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = cashControlDocument.getAccountsReceivableDocumentHeader();
        String processingChartOfAccountCode = accountsReceivableDocumentHeader.getProcessingChartOfAccountCode();
        String processingOrganizationCode = accountsReceivableDocumentHeader.getProcessingOrganizationCode();

        // get system information by processing chart of accounts and organization code
        SystemInformation systemInformation = systemInformationService.getByPrimaryKey(currentFiscalYear, processingChartOfAccountCode, processingOrganizationCode);

        // if no system information is set up for this processing chart of accounts and organization code return false, the glpes
        // cannot be created
        if (ObjectUtils.isNull(systemInformation)) {
            return false;
        }

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

        Options options = optionsService.getCurrentYearOptions();

        // build dummy accounting line for gl creation
        accountingLine = buildAccountingLine(systemInformation.getUniversityClearingAccountNumber(), systemInformation.getUniversityClearingSubAccountNumber(), systemInformation.getCreditCardObjectCode(), systemInformation.getUniversityClearingSubObjectCode(), systemInformation.getUniversityClearingChartOfAccountsCode(), KFSConstants.GL_DEBIT_CODE, cashControlDocument.getCashControlTotalAmount());
        //get document type for the glpes
        String documentType = documentTypeService.getDocumentTypeCodeByClass(GeneralErrorCorrectionDocument.class);
        // create and add the new explicit entry based on this accounting line
        createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, documentType);

        // build dummy accounting line for gl creation
        accountingLine = buildAccountingLine(systemInformation.getUniversityClearingAccountNumber(), systemInformation.getUniversityClearingSubAccountNumber(), systemInformation.getUniversityClearingObjectCode(), systemInformation.getUniversityClearingSubObjectCode(), systemInformation.getUniversityClearingChartOfAccountsCode(), KFSConstants.GL_CREDIT_CODE, cashControlDocument.getCashControlTotalAmount());
        // create and add the new explicit entry based on this accounting line
        createAndAddNewExplicitEntry(cashControlDocument, sequenceHelper, accountingLine, options, documentType);

        return success;
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#getLockboxNumber(org.kuali.module.ar.document.CashControlDocument)
     */
    public String getLockboxNumber(CashControlDocument cashControlDocument) {

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        String chartOfAccountsCode = cashControlDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrgCode = cashControlDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        SystemInformation systemInformation = systemInformationService.getByPrimaryKey(currentFiscalYear, chartOfAccountsCode, processingOrgCode);

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
    private AccountingLine buildAccountingLine(String accountNumber, String subAccountNumber, String objectCode, String subObjectCode, String chartOfAccountsCode, String debitOrCredit, KualiDecimal amount) {

        AccountingLine accountingLine = null;

        // get new accounting line
        try {
            accountingLine = (SourceAccountingLine) SourceAccountingLine.class.newInstance();
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
    private GeneralLedgerPendingEntry createAndAddNewExplicitEntry(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, Options options, String documentType) {

        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

        glpeService.populateExplicitGeneralLedgerPendingEntry(cashControlDocument, accountingLine, sequenceHelper, explicitEntry);

        explicitEntry.setFinancialObjectTypeCode(options.getFinancialObjectTypeAssetsCd());
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
    private boolean createAndAddTheOffsetEntry(CashControlDocument cashControlDocument, GeneralLedgerPendingEntry explicitEntry, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        // create offset
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        success &= glpeService.populateOffsetGeneralLedgerPendingEntry(cashControlDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
        cashControlDocument.customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);

        // add the offset
        cashControlDocument.addPendingEntry(offsetEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        return success;
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
     * This method gets the document type service
     * @return
     */
    public DocumentTypeService getDocumentTypeService() {
        return documentTypeService;
    }

    /**
     * This method sets the document type service
     * @param documentTypeService
     */
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
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

}
