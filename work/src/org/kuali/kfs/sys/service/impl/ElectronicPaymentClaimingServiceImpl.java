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
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public class ElectronicPaymentClaimingServiceImpl implements ElectronicPaymentClaimingService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicPaymentClaimingServiceImpl.class);
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    protected static final String ELECTRONIC_FUNDS_CLAIMANT_GROUP_PARAMETER = "ELECTRONIC_FUNDS_CLAIMANT_GROUP";
    protected final static String ELECTRONIC_PAYMENT_ADMINISTRATOR_GROUP_PARAM_NAME = "ELECTRONIC_FUNDS_ADMINISTRATOR_GROUP";
    protected static final String ELECTRONIC_FUNDS_CLAIM_SUMMARIES_PER_NOTE_PARAMETER = "ELECTRONIC_FUNDS_CLAIM_SUMMARIES_PER_NOTE";
    protected static final String CLAIMING_NOTE_PRELUDE = "Claiming CR Items: ";
    protected static final String DI_CLAIMING_DOC_HELPER_BEAN_NAME = "distributionOfIncomeAndExpenseElectronicPaymentClaimingDocumentHelper";
    protected static final String YEDI_CLAIMING_DOC_HELPER_BEAN_NAME = "yearEndDistributionOfIncomeAndExpenseElectronicPaymentClaimingDocumentHelper";
    protected static final String CLAIMING_DOC_HELPER_BEAN_NAME = "expenseElectronicPaymentClaimingDocumentHelper";
    protected static final String ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER = "ELECTRONIC_FUNDS_ACCOUNTS";

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingService#constructNotesForClaims(java.util.List)
     */
    public List<String> constructNoteTextsForClaims(List<ElectronicPaymentClaim> claims) {
        int summariesPerNote;
        List<String> noteTexts = new ArrayList<String>();
        try {
            summariesPerNote = Integer.parseInt(parameterService.getParameterValueAsString(ElectronicPaymentClaim.class, ELECTRONIC_FUNDS_CLAIM_SUMMARIES_PER_NOTE_PARAMETER));
            int i = 0;
            while (i < claims.size()) {
                String noteText = constructNoteText(claims, i, summariesPerNote);
                noteTexts.add(noteText);
                i += summariesPerNote;
            }
        }
        catch (NumberFormatException nfe) {
            throw new RuntimeException("The KFS-SYS / ElectronicPaymentClaim / " + ELECTRONIC_FUNDS_CLAIM_SUMMARIES_PER_NOTE_PARAMETER + " should have a value that can be parsed into an integer.", nfe);
        }
        return noteTexts;
    }

    /**
     * This creates a note for the given point in the list of summaries.
     * 
     * @param claims a List of ElectronicPaymentClaim records that are being claimed
     * @param startPoint the point in the list the note is starting at
     * @param maxSummariesPerNote the number of ElectronicPaymentClaim summaries we can have on a note
     * @return a newly constructed note, that needs to have a user added
     */
    protected String constructNoteText(List<ElectronicPaymentClaim> claims, int startPoint, int maxSummariesPerNote) {
        StringBuilder sb = new StringBuilder();
        sb.append(CLAIMING_NOTE_PRELUDE);
        for (int i = startPoint; i < (startPoint + maxSummariesPerNote) && i < claims.size(); i++) {
            ElectronicPaymentClaim claim = claims.get(i);
            sb.append(createSummaryLineForClaim(claim));
        }
        // substring out the final " ; "
        String noteText = sb.substring(0, sb.length() - 3);
        return noteText;
    }

    /**
     * Creates a summary line for a note from a claim
     * 
     * @param claim the electronic payment claim to summarize
     * @return a String with the summary of the claim.
     */
    protected String createSummaryLineForClaim(ElectronicPaymentClaim claim) {
        StringBuilder summary = new StringBuilder();
        summary.append(claim.getDocumentNumber());
        summary.append('-');
        summary.append(claim.getFinancialDocumentLineNumber().toString());
        summary.append(' ');
        final Date advanceDepositDate = claim.getGeneratingAdvanceDepositDetail().getFinancialDocumentAdvanceDepositDate();
        if (!ObjectUtils.isNull(advanceDepositDate)) {
            summary.append(dateTimeService.toDateString(advanceDepositDate));
            summary.append(' ');
        }
        summary.append('$');
        summary.append(claim.getGeneratingAccountingLine().getAmount());
        summary.append(' ');
        summary.append(';');
        summary.append(' ');
        return summary.toString();
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingService#createPaymentClaimingDocument(java.util.List,
     *      org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy)
     */
    public String createPaymentClaimingDocument(List<ElectronicPaymentClaim> claims, ElectronicPaymentClaimingDocumentGenerationStrategy documentCreationHelper, Person user) {
        return documentCreationHelper.createDocumentFromElectronicPayments(claims, user);
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingService#getClaimingDocumentChoices(org.kuali.rice.kim.api.identity.Person)
     */
    public List<ElectronicPaymentClaimingDocumentGenerationStrategy> getClaimingDocumentChoices(Person user) {
        List<ElectronicPaymentClaimingDocumentGenerationStrategy> documentChoices = new ArrayList<ElectronicPaymentClaimingDocumentGenerationStrategy>();
        Map<String, ElectronicPaymentClaimingDocumentGenerationStrategy> claimingDocHelpers = SpringContext.getBeansOfType(ElectronicPaymentClaimingDocumentGenerationStrategy.class);
        ElectronicPaymentClaimingDocumentGenerationStrategy claimingDocHelper;
        
        // try the helper for no document case
        claimingDocHelper = claimingDocHelpers.get(CLAIMING_DOC_HELPER_BEAN_NAME);
        if (claimingDocHelper.userMayUseToClaim(user)) {
            documentChoices.add(claimingDocHelper);
        }        

        // try the DI
        claimingDocHelper = claimingDocHelpers.get(DI_CLAIMING_DOC_HELPER_BEAN_NAME);
        if (claimingDocHelper.userMayUseToClaim(user)) {
            documentChoices.add(claimingDocHelper);
        }
        
        // try the YEDI
        claimingDocHelper = claimingDocHelpers.get(YEDI_CLAIMING_DOC_HELPER_BEAN_NAME);
        if (claimingDocHelper.userMayUseToClaim(user)) {
            documentChoices.add(claimingDocHelper);
        }

        // try the AR Cash Control
        claimingDocHelper = SpringContext.getBean(AccountsReceivableModuleService.class).getAccountsReceivablePaymentClaimingStrategy();
        if (claimingDocHelper.userMayUseToClaim(user)) {
            documentChoices.add(claimingDocHelper);
        }

        return documentChoices;
    }

    /**
     * Sets the referenceFinancialDocumentNumber on each of the payments passed in with the given document number and then saves
     * them.
     * 
     * @param payments a list of payments to claim
     * @param docmentNumber the document number of the claiming document
     */
    public void claimElectronicPayments(List<ElectronicPaymentClaim> payments, String documentNumber) {
        for (ElectronicPaymentClaim payment : payments) {
            payment.setReferenceFinancialDocumentNumber(documentNumber);
            payment.setPaymentClaimStatusCode(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED);
            businessObjectService.save(payment);
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingService#declaimElectronicPaymentClaimsForDocument(org.kuali.rice.krad.document.Document)
     */
    public void declaimElectronicPaymentClaimsForDocument(Document document) {
        Map<String, String> searchKeys = new HashMap<String, String>();
        searchKeys.put("referenceFinancialDocumentNumber", document.getDocumentNumber());
        Collection<ElectronicPaymentClaim> claimsAsObjects = businessObjectService.findMatching(ElectronicPaymentClaim.class, searchKeys);
        for (Object claimAsObject : claimsAsObjects) {
            ElectronicPaymentClaim claim = (ElectronicPaymentClaim) claimAsObject;
            claim.setReferenceFinancialDocumentNumber(null);
            claim.setPaymentClaimStatusCode(ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED);
            businessObjectService.save(claim);
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingService#isAuthorizedForClaimingElectronicPayment(org.kuali.rice.kim.api.identity.Person,
     *      java.lang.String, java.lang.String)
     */
    public boolean isAuthorizedForClaimingElectronicPayment(Person user, String workflowDocumentTypeName) {
        String principalId = user.getPrincipalId();
        String namespaceCode = KFSConstants.ParameterNamespaces.KFS;
        String permissionTemplateName = KFSConstants.PermissionTemplate.CLAIM_ELECTRONIC_PAYMENT.name;

        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, workflowDocumentTypeName);      

        IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        boolean isAuthorized = identityManagementService.hasPermissionByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails);
        
        return isAuthorized;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingService#generateElectronicPaymentClaimRecords(org.kuali.kfs.fp.document.AdvanceDepositDocument)
     */
    public List<ElectronicPaymentClaim> generateElectronicPaymentClaimRecords(AdvanceDepositDocument doc) {
        List<ElectronicPaymentClaim> claimRecords = new ArrayList<ElectronicPaymentClaim>();
        for (Object accountingLineAsObj : doc.getSourceAccountingLines()) {
            final AccountingLine accountingLine = (AccountingLine) accountingLineAsObj;
            if (this.representsElectronicFundAccount(accountingLine)) {
                ElectronicPaymentClaim electronicPayment = createElectronicPayment(doc, accountingLine);
                businessObjectService.save(electronicPayment);
                claimRecords.add(electronicPayment);
            }
        }
        return claimRecords;
    }
    
    /**
     * Determines if the given accounting line represents an electronic payment
     * @param accountingLine the accounting line to check
     * @return true if the accounting line does represent an electronic payment, false otherwise
     */
    public boolean representsElectronicFundAccount(AccountingLine accountingLine) {
        return /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(AdvanceDepositDocument.class, ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER, accountingLine.getChartOfAccountsCode(), accountingLine.getAccountNumber()).evaluationSucceeds();
    }

    /**
     * Creates an electronic payment claim record to match the given accounting line on the document
     * 
     * @param accountingLine an accounting line that an electronic payment claim record should be created for
     * @return the created ElectronicPaymentClaim business object
     */
    protected ElectronicPaymentClaim createElectronicPayment(AdvanceDepositDocument document, AccountingLine accountingLine) {
        ElectronicPaymentClaim electronicPayment = new ElectronicPaymentClaim();
        electronicPayment.setDocumentNumber(document.getDocumentNumber());
        electronicPayment.setFinancialDocumentLineNumber(accountingLine.getSequenceNumber());
        electronicPayment.setFinancialDocumentPostingPeriodCode(document.getPostingPeriodCode());
        electronicPayment.setFinancialDocumentPostingYear(document.getPostingYear());
        electronicPayment.setPaymentClaimStatusCode(ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED);
        return electronicPayment;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
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
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
