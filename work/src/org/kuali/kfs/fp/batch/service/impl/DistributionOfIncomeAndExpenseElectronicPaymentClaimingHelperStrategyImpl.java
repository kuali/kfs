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
package org.kuali.kfs.fp.batch.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.DocumentService;

public class DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl implements ElectronicPaymentClaimingDocumentGenerationStrategy {
    private static final Logger LOG = Logger.getLogger(DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.class);
    
    protected DocumentService documentService;
    protected ElectronicPaymentClaimingService electronicPaymentClaimingService;
    protected ParameterService parameterService;

    /**
     * The name of the parameter to get the description for this document; without a description, we can't save the document, and if
     * we don't save the document, there's a chance that electronic payment claims will go to limbo
     */
    protected final static String DOCUMENT_DESCRIPTION_PARAM_NAME = "ELECTRONIC_FUNDS_DOCUMENT_DESCRIPTION";
    protected final static String URL_PREFIX = "financial";
    protected final static String URL_MIDDLE = ".do?methodToCall=docHandler&command=";
    protected final static String URL_SUFFIX = "&docId=";
    protected final static String URL_DOC_TYPE = "DistributionOfIncomeAndExpense";

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#createDocumentFromElectronicPayments(java.util.List)
     */
    public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
        DistributionOfIncomeAndExpenseDocument document = null;
        try {
            document = (DistributionOfIncomeAndExpenseDocument) documentService.getNewDocument(getClaimingDocumentWorkflowDocumentType());
            addAccountingLinesToDocument(document, electronicPayments);
            addDescriptionToDocument(document);
            addNotesToDocument(document, electronicPayments, user);
            documentService.saveDocument(document);
            electronicPaymentClaimingService.claimElectronicPayments(electronicPayments, document.getDocumentNumber());
        }
        catch (WorkflowException we) {
            throw new RuntimeException("WorkflowException while creating a DistributionOfIncomeAndExpenseDocument to claim ElectronicPaymentClaim records.", we);
        }

        return getURLForDocument(document);
    }

    /**
     * Builds the URL that can be used to redirect to the correct document
     * 
     * @param doc the document to build the URL for
     * @return the relative URL to redirect to
     */
    protected String getURLForDocument(DistributionOfIncomeAndExpenseDocument doc) {
        StringBuilder url = new StringBuilder();
        url.append(URL_PREFIX);
        url.append(getUrlDocType());
        url.append(URL_MIDDLE);
        url.append(KewApiConstants.ACTIONLIST_COMMAND);
        url.append(URL_SUFFIX);
        url.append(doc.getDocumentNumber());
        return url.toString();
    }

    /**
     * Creates notes for the claims (using the ElectronicPaymentClaimingService) and then adds them to the document
     * 
     * @param claimingDoc the claiming document
     * @param claims the electronic payments being claimed
     * @param user the user doing the claiming
     */
    protected void addNotesToDocument(DistributionOfIncomeAndExpenseDocument claimingDoc, List<ElectronicPaymentClaim> claims, Person user) {
        for (String noteText : electronicPaymentClaimingService.constructNoteTextsForClaims(claims)) {
            try {
                Note note = documentService.createNoteFromDocument(claimingDoc, noteText);
                claimingDoc.addNote(note);
             }
            catch (Exception e) {
                LOG.error("Exception while attempting to create or add note: ", e);
            }
        }
    }

    /**
     * Adds an accounting line to the document for each ElectronicPaymentClaim record that is being added
     * 
     * @param document the claiming Distribution of Income and Expense document
     * @param electronicPayments the list of ElectronicPaymentClaim records that are being claimed
     */
    protected void addAccountingLinesToDocument(DistributionOfIncomeAndExpenseDocument document, List<ElectronicPaymentClaim> electronicPayments) {
        for (ElectronicPaymentClaim payment : electronicPayments) {
            SourceAccountingLine claimingAccountingLine = copyAccountingLineToNew(payment.getGeneratingAccountingLine(), createNewAccountingLineForDocument(document));
            document.addSourceAccountingLine(claimingAccountingLine);
        }
    }

    /**
     * Adds the parameterized description to the document, so the doc can be saved
     * 
     * @param document the document to add a description to
     */
    protected void addDescriptionToDocument(DistributionOfIncomeAndExpenseDocument document) {
        String description = parameterService.getParameterValueAsString(DistributionOfIncomeAndExpenseDocument.class, DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.DOCUMENT_DESCRIPTION_PARAM_NAME);
        if (description != null) {
            document.getDocumentHeader().setDocumentDescription(description);
        }
        else {
            throw new RuntimeException("There is evidently no value for Parameter KFS-FP / Distribution of Income and Expense / " + DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.DOCUMENT_DESCRIPTION_PARAM_NAME + "; please set a value before claiming Electronic Payments");
        }
    }

    /**
     * Creates a new accounting line, based on what the source accounting line class for the document is
     * 
     * @param document the document that is claiming these payments
     * @return a new, ready-to-be-filled in accounting line of the class that the given document uses for Source Accounting Lines
     */
    protected SourceAccountingLine createNewAccountingLineForDocument(DistributionOfIncomeAndExpenseDocument document) {
        try {
            Class<? extends SourceAccountingLine> accountingLineClass = document.getSourceAccountingLineClass();
            return accountingLineClass.newInstance();
        }
        catch (Exception ex) {
            throw new RuntimeException( "Unable to create source accounting line for document: " + document, ex);
        }
    }

    /**
     * Copies an original accounting line to a new accounting line
     * 
     * @param line the original accounting line
     * @return an accounting line that copies that accounting line
     */
    protected SourceAccountingLine copyAccountingLineToNew(SourceAccountingLine line, SourceAccountingLine newLine) {
        newLine.setChartOfAccountsCode(line.getChartOfAccountsCode());
        newLine.setAccountNumber(line.getAccountNumber());
        newLine.setSubAccountNumber(line.getSubAccountNumber());
        newLine.setFinancialObjectCode(line.getFinancialObjectCode());
        newLine.setFinancialSubObjectCode(line.getFinancialSubObjectCode());
        newLine.setProjectCode(line.getProjectCode());
        newLine.setOrganizationReferenceId(line.getOrganizationReferenceId());
        newLine.setAmount(line.getAmount());
        return newLine;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getClaimingDocumentWorkflowDocumentType()
     * @return the name DistributionOfIncomeAndExpenseDocument workflow document type
     */
    public String getClaimingDocumentWorkflowDocumentType() {
        return KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE;
    }
    
    /**
     * @return the class of the document which claims these electronic payments
     */
    protected String getUrlDocType() {
        return DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.URL_DOC_TYPE;
    }

    /**
     * Uses the data dictionary to find the label for this document
     * 
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentLabel()
     */
    public String getDocumentLabel() {
        try {
            return SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(getClaimingDocumentWorkflowDocumentType()).getLabel();
        } catch (Exception e) {
            LOG.error("Caught Exception trying to get Workflow Document Type" + e);
        }
        return "DI";
    }

    /**
     * This always returns true if the given user in the claiming workgroup.
     * 
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#userMayUseToClaim(org.kuali.rice.kim.api.identity.Person)
     */
    public boolean userMayUseToClaim(Person claimingUser) {
        final String documentTypeName = getClaimingDocumentWorkflowDocumentType();

        final boolean canClaim = electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, documentTypeName) 
                || electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, null);       
        
        return canClaim;
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
            // the doc # can't be parsed into a Long? Then it ain't no valid!
            LOG.warn( "Unable to parse referenceDocumentNumber (" + referenceDocumentNumber + ") into a number: " + nfe.getMessage() );
        }
        return valid;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the electronicPaymentClaimingService attribute value.
     * 
     * @param electronicPaymentClaimingService The electronicPaymentClaimingService to set.
     */
    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    
}
