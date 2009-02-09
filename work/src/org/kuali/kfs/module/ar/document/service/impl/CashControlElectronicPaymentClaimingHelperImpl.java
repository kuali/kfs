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
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;
import org.kuali.rice.kns.workflow.service.WorkflowInfoService;

public class CashControlElectronicPaymentClaimingHelperImpl implements ElectronicPaymentClaimingDocumentGenerationStrategy {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlElectronicPaymentClaimingHelperImpl.class);

    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;
    private CashControlDocumentService cashControlDocumentService;
    private KualiConfigurationService kualiConfigurationService;

    private final static String CC_WORKFLOW_DOCUMENT_TYPE = "CTRL";
    private final static String URL_PREFIX = "ar";
    private final static String URL_MIDDLE = "Document.do?methodToCall=docHandler&command=";
    private final static String URL_SUFFIX = "&docId=";

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#createDocumentFromElectronicPayments(java.util.List,
     *      org.kuali.rice.kim.bo.Person)
     */
    public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
        CashControlDocument document = null;
        try {
            document = (CashControlDocument) documentService.getNewDocument(getClaimingDocumentWorkflowDocumentType());
            document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.WIRE_TRANSFER);

            //create and set AccountsReceivableDocumentHeader
            ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
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
        document.getDocumentHeader().setDocumentDescription(kualiConfigurationService.getPropertyString(ArKeyConstants.ELECTRONIC_PAYMENT_CLAIM));
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
            cashControlDocumentService.addNewCashControlDetail(kualiConfigurationService.getPropertyString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), document, newCashControlDetail);
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
        url.append(KEWConstants.ACTIONLIST_COMMAND);
        url.append(URL_SUFFIX);
        url.append(doc.getDocumentNumber());
        return url.toString();
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getClaimingDocumentWorkflowDocumentType()
     * 
     * @return the name CashControlDocument workflow document type
     */
    public String getClaimingDocumentWorkflowDocumentType() {
        return CashControlElectronicPaymentClaimingHelperImpl.CC_WORKFLOW_DOCUMENT_TYPE;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentLabel()
     */
    public String getDocumentLabel() {
        try {
            KualiWorkflowInfo workflowInfo = KNSServiceLocator.getWorkflowInfoService();;
            
            return workflowInfo.getDocType(getClaimingDocumentWorkflowDocumentType()).getDocTypeLabel();
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Caught Exception trying to get Workflow Document Type", e);
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#isDocumentReferenceValid(java.lang.String)
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
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#userMayUseToClaim(org.kuali.rice.kim.bo.Person)
     */
    public boolean userMayUseToClaim(Person claimingUser) {
        String namespaceCode = ArConstants.AR_NAMESPACE_CODE;
        String documentTypeName = this.getClaimingDocumentWorkflowDocumentType();
        
        boolean canClaim = electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, namespaceCode, documentTypeName);       
        canClaim |= electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, KFSConstants.ParameterNamespaces.KFS, null);
        
        return canClaim;
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
     * This method sets document service value
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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
     * This method sets dataDictionaryService value
     * 
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * This method sets kualiConfigurationService
     * @param kualiConfigurationService
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}

