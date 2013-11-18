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

import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashControlElectronicPaymentClaimingHelperImpl implements ElectronicPaymentClaimingDocumentGenerationStrategy {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlElectronicPaymentClaimingHelperImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected DocumentService documentService;
    protected ElectronicPaymentClaimingService electronicPaymentClaimingService;
    protected CashControlDocumentService cashControlDocumentService;
    protected ConfigurationService kualiConfigurationService;
    private static DocumentTypeService documentTypeService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    protected final static String URL_PREFIX = "ar";
    protected final static String URL_MIDDLE = "Document.do?methodToCall=docHandler&command=";
    protected final static String URL_SUFFIX = "&docId=";
    protected final static String URL_DOC_TYPE = "CashControl";

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#createDocumentFromElectronicPayments(java.util.List,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
        CashControlDocument document = null;
        try {
            document = (CashControlDocument) documentService.getNewDocument(getClaimingDocumentWorkflowDocumentType());
            document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.WIRE_TRANSFER);

            //create and set AccountsReceivableDocumentHeader
            ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
            accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
            document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

            addDescriptionToDocument(document);
            addNotesToDocument(document, electronicPayments, user);
            addCashControlDetailsToDocument(document, electronicPayments);
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
    protected void addDescriptionToDocument(CashControlDocument document) {
        document.getDocumentHeader().setDocumentDescription(kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.ELECTRONIC_PAYMENT_CLAIM));
    }

    /**
     * This method adds notes to the cash control document
     *
     * @param claimingDoc the cash control document
     * @param claims the list of electronic payments being claimed
     * @param user the current user
     */
    protected void addNotesToDocument(CashControlDocument claimingDoc, List<ElectronicPaymentClaim> claims, Person user) {
        for (String noteText : electronicPaymentClaimingService.constructNoteTextsForClaims(claims)) {
            try {
                Note note = documentService.createNoteFromDocument(claimingDoc, noteText);
                claimingDoc.addNote(note);
                documentService.saveDocumentNotes(claimingDoc);
            } catch (Exception e) {
                LOG.error("Exception while attempting to create or add note: ", e);
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
    protected void addCashControlDetailsToDocument(CashControlDocument document, List<ElectronicPaymentClaim> electronicPayments) throws WorkflowException {
        for (ElectronicPaymentClaim electronicPaymentClaim : electronicPayments) {
            CashControlDetail newCashControlDetail = new CashControlDetail();
            newCashControlDetail.setCashControlDocument(document);
            newCashControlDetail.setDocumentNumber(document.getDocumentNumber());
            newCashControlDetail.setFinancialDocumentLineAmount(electronicPaymentClaim.getGeneratingAccountingLine().getAmount());
            newCashControlDetail.setCustomerPaymentDescription(electronicPaymentClaim.getGeneratingAccountingLine().getFinancialDocumentLineDescription());
            cashControlDocumentService.addNewCashControlDetail(kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), document, newCashControlDetail);
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
        url.append(URL_DOC_TYPE);
        url.append(URL_MIDDLE);
        url.append(KewApiConstants.ACTIONLIST_COMMAND);
        url.append(URL_SUFFIX);
        url.append(doc.getDocumentNumber());
        return url.toString();
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getClaimingDocumentWorkflowDocumentType()
     *
     * @return the name CashControlDocument workflow document type
     */
    @Override
    public String getClaimingDocumentWorkflowDocumentType() {
        return KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentLabel()
     */
    @Override
    public String getDocumentLabel() {
        return getDocumentTypeService().getDocumentTypeByName(getClaimingDocumentWorkflowDocumentType()).getLabel();
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#isDocumentReferenceValid(java.lang.String)
     */
    @Override
    public boolean isDocumentReferenceValid(String referenceDocumentNumber) {
        boolean isValid = false;
        try {
            long docNumberAsLong = Long.parseLong(referenceDocumentNumber);
            if (docNumberAsLong > 0L) {
                isValid = documentService.documentExists(referenceDocumentNumber);
            }
        }
        catch (NumberFormatException nfe) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#userMayUseToClaim(org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean userMayUseToClaim(Person claimingUser) {
        final String documentTypeName = this.getClaimingDocumentWorkflowDocumentType();

        final boolean canClaim = electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, documentTypeName) || electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, null);

        return canClaim;
    }

    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KewApiServiceLocator.getDocumentTypeService();
        }
        return documentTypeService;
    }
    
    /**
     * Sets the accountsReceivableDocumentHeaderService attribute value.
     *
     * @param accountsReceivableDocumentHeaderService The accountsReceivableDocumentHeaderService to set.
     */
    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }
}

