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

import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.bo.ElectronicPaymentClaim;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.service.ElectronicPaymentClaimingService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CashControlDocumentService;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.exception.WorkflowException;

public class CashControlElectronicPaymentClaimingHelperImpl implements ElectronicPaymentClaimingDocumentGenerationStrategy {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlElectronicPaymentClaimingHelperImpl.class);

    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    private DocumentTypeService documentTypeService;
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;
    private CashControlDocumentService cashControlDocumentService;
    private KualiConfigurationService kualiConfigurationService;

    private final static String CC_WORKFLOW_DOCUMENT_TYPE = "CashControlDocument";
    private final static String URL_PREFIX = "ar";
    private final static String URL_MIDDLE = "Document.do?methodToCall=docHandler&command=";
    private final static String URL_SUFFIX = "&docId=";

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingDocumentGenerationStrategy#createDocumentFromElectronicPayments(java.util.List,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, UniversalUser user) {
        CashControlDocument document = null;
        try {
            document = (CashControlDocument) documentService.getNewDocument(getClaimingDocumentWorkflowDocumentType());
            document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.WIRE_TRANSFER);

            //create and set AccountsReceivableDocumentHeader
            ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
            AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
            accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
            document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

            addCashControlDetailsToDocument(document, electronicPayments);
            addDescriptionToDocument(document);
            addNotesToDocument(document, electronicPayments, user);
            documentService.saveDocument(document);
            electronicPaymentClaimingService.claimElectronicPayments(electronicPayments, document.getDocumentNumber());
        }
        catch (WorkflowException we) {
            throw new RuntimeException("WorkflowException while creating a CashControlDocument to claim ElectronicPaymentClaim records.", we);
        }

        return getURLForDocument(document);
    }

    /**
     * This method add a description to the cash control document
     * 
     * @param document the cash control document
     */
    private void addDescriptionToDocument(CashControlDocument document) {
        document.getDocumentHeader().setFinancialDocumentDescription(kualiConfigurationService.getPropertyString(ArConstants.ELECTRONIC_PAYMENT_CLAIM));
    }

    /**
     * This method adds notes to the cash control document
     * 
     * @param claimingDoc the cash control document
     * @param claims the list of electronic payments being claimed
     * @param user the current user
     */
    protected void addNotesToDocument(CashControlDocument claimingDoc, List<ElectronicPaymentClaim> claims, UniversalUser user) {
        for (String noteText : electronicPaymentClaimingService.constructNoteTextsForClaims(claims)) {
            try {
                Note note = documentService.createNoteFromDocument(claimingDoc, noteText);
                documentService.addNoteToDocument(claimingDoc, note);
            }
            catch (Exception e) {
                LOG.error("Exception while attempting to create or add note: " + e);
            }
        }
    }

    /**
     * This method adds new cash control details to the cash control document based on the list of electronic payments.
     * 
     * @param document cash control document
     * @param electronicPayments the electronic payments to be claimed
     * @throws WorkflowException workflow exception
     */
    private void addCashControlDetailsToDocument(CashControlDocument document, List<ElectronicPaymentClaim> electronicPayments) throws WorkflowException {
        for (ElectronicPaymentClaim electronicPaymentClaim : electronicPayments) {
            CashControlDetail newCashControlDetail = new CashControlDetail();
            newCashControlDetail.setCashControlDocument(document);
            newCashControlDetail.setDocumentNumber(document.getDocumentNumber());
            newCashControlDetail.setFinancialDocumentLineAmount(electronicPaymentClaim.getGeneratingAdvanceDepositDetail().getFinancialDocumentAdvanceDepositAmount());
            cashControlDocumentService.addNewCashControlDetail(kualiConfigurationService.getPropertyString(ArConstants.CREATED_BY_CASH_CTRL_DOC), document, newCashControlDetail);
        }

    }

    /**
     * Builds the URL that can be used to redirect to the correct document
     * 
     * @param doc the document to build the URL for
     * @return the relative URL to redirect to
     */
    protected String getURLForDocument(CashControlDocument doc) {
        StringBuilder url = new StringBuilder();
        url.append(URL_PREFIX);
        url.append(getClaimingDocumentWorkflowDocumentType().replace("Document", ""));
        url.append(URL_MIDDLE);
        url.append(IDocHandler.ACTIONLIST_COMMAND);
        url.append(URL_SUFFIX);
        url.append(doc.getDocumentNumber());
        return url.toString();
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentCode()
     */
    public String getDocumentCode() {
        return dataDictionaryService.getDataDictionary().getDocumentEntry(documentTypeService.getClassByName(getClaimingDocumentWorkflowDocumentType()).getCanonicalName()).getDocumentTypeCode();
    }

    /**
     * Returns the name CashControlDocument workflow document type
     * 
     * @return the name CashControlDocument workflow document type
     */
    public String getClaimingDocumentWorkflowDocumentType() {
        return CashControlElectronicPaymentClaimingHelperImpl.CC_WORKFLOW_DOCUMENT_TYPE;
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentLabel()
     */
    public String getDocumentLabel() {
        return dataDictionaryService.getDataDictionary().getDocumentEntry(documentTypeService.getClassByName(getClaimingDocumentWorkflowDocumentType()).getCanonicalName()).getLabel();
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingDocumentGenerationStrategy#isDocumentReferenceValid(java.lang.String)
     */
    public boolean isDocumentReferenceValid(String referenceDocumentNumber) {
        boolean valid = false;
        try {
            long docNumberAsLong = Long.parseLong(referenceDocumentNumber);
            if (docNumberAsLong > 0L) {
                valid = documentService.documentExists(referenceDocumentNumber);
            }
        }
        catch (NumberFormatException nfe) {
            valid = false;
        }
        return valid;
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingDocumentGenerationStrategy#userMayUseToClaim(org.kuali.core.bo.user.UniversalUser)
     */
    public boolean userMayUseToClaim(UniversalUser claimingUser) {
        return electronicPaymentClaimingService.isUserMemberOfClaimingGroup(claimingUser);
    }

    /**
     * This method gets cashControlDocumentService value
     * 
     * @return cashControlDocumentService
     */
    public CashControlDocumentService getCashControlDocumentService() {
        return cashControlDocumentService;
    }

    /**
     * This method sets cashControlDocumentService value
     * 
     * @param cashControlDocumentService
     */
    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }

    /**
     * This method gets document service value
     * 
     * @return documentService
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * This method sets document service value
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * This method gets document type service value
     * 
     * @return documentTypeService
     */
    public DocumentTypeService getDocumentTypeService() {
        return documentTypeService;
    }

    /**
     * This method sets documentTypeService value
     * 
     * @param documentTypeService
     */
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    /**
     * This method gets electronicPaymentClaimingService value
     * 
     * @return electronicPaymentClaimingService
     */
    public ElectronicPaymentClaimingService getElectronicPaymentClaimingService() {
        return electronicPaymentClaimingService;
    }

    /**
     * This method sets electronicPaymentClaimingService value
     * 
     * @param electronicPaymentClaimingService
     */
    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }

    /**
     * This method gets dataDictionaryService value
     * 
     * @return dataDictionaryService
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * This method sets dataDictionaryService value
     * 
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * This method gets kualiConfigurationService
     * @return kualiConfigurationService
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * This method sets kualiConfigurationService
     * @param kualiConfigurationService
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
