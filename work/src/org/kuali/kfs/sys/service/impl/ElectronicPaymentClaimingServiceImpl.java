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
package org.kuali.kfs.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.ElectronicPaymentClaim;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.ElectronicPaymentClaiming;
import org.kuali.kfs.service.ElectronicPaymentClaimingDocument;
import org.kuali.kfs.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.document.AdvanceDepositDocument;

import edu.iu.uis.eden.exception.WorkflowException;

public class ElectronicPaymentClaimingServiceImpl implements ElectronicPaymentClaimingService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicPaymentClaimingServiceImpl.class);
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    
    private static final String ELECTRONIC_FUNDS_CLAIMANT_GROUP_PARAMETER = "ELECTRONIC_FUNDS_CLAIMANT_GROUP";
    private final static String ELECTRONIC_PAYMENT_ADMINISTRATOR_GROUP_PARAM_NAME = "ELECTRONIC_FUNDS_ADMINISTRATOR_GROUP";
    private static final String ELECTRONIC_FUNDS_CLAIM_SUMMARIES_PER_NOTE_PARAMETER = "ELECTRONIC_FUNDS_CLAIM_SUMMARIES_PER_NOTE";
    private static final String CLAIMING_NOTE_PRELUDE = "Claiming CR Items: \n";
    private static final String DI_CLAIMING_DOC_HELPER_BEAN_NAME = "distributionOfIncomeAndExpenseElectronicPaymentClaimingDocumentHelper";
    private static final String YEDI_CLAIMING_DOC_HELPER_BEAN_NAME = "yearEndDistributionOfIncomeAndExpenseElectronicPaymentClaimingDocumentHelper";
    private static final String ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER = "ELECTRONIC_FUNDS_ACCOUNTS";

    /**
     * 
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingService#constructNotesForClaims(java.util.List)
     */
    public List<Note> constructNotesForClaims(List<ElectronicPaymentClaim> claims, UniversalUser claimingUser) {
        int summariesPerNote;
        List<Note> notes = new ArrayList<Note>();
        try {
            summariesPerNote = Integer.parseInt(parameterService.getParameterValue(ElectronicPaymentClaim.class, ELECTRONIC_FUNDS_CLAIM_SUMMARIES_PER_NOTE_PARAMETER));
            int i = 0;
            while (i < claims.size()) {
                Note note = constructNote(claims, i, summariesPerNote);
                note.setAuthorUniversalIdentifier(claimingUser.getPersonUniversalIdentifier());
                notes.add(note);
                i += summariesPerNote;
            }
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("The KFS-SY / ElectronicPaymentClaim / "+ELECTRONIC_FUNDS_CLAIM_SUMMARIES_PER_NOTE_PARAMETER+" should have a value that can be parsed into an integer.", nfe);
        } catch (NullPointerException npe) {
            throw new RuntimeException(npe);
        }
        return notes;
    }
    
    /**
     * This creates a note for the given point in the list of summaries.
     * @param claims a List of ElectronicPaymentClaim records that are being claimed
     * @param startPoint the point in the list the note is starting at
     * @param maxSummariesPerNote the number of ElectronicPaymentClaim summaries we can have on a note
     * @return a newly constructed note, that needs to have a user added
     */
    private Note constructNote(List<ElectronicPaymentClaim> claims, int startPoint, int maxSummariesPerNote) {
        Note note = new Note();
        StringBuilder sb = new StringBuilder();
        sb.append(CLAIMING_NOTE_PRELUDE);
        for (int i = startPoint; i < (startPoint + maxSummariesPerNote) && i < claims.size(); i++) {
            ElectronicPaymentClaim claim = claims.get(i);
            sb.append(createSummaryLineForClaim(claim));
        }
        note.setNoteText(sb.toString());
        note.setNotePostedTimestamp(dateTimeService.getCurrentTimestamp());
        return note;
    }
    
    /**
     * Creates a summary line for a note from a claim
     * @param claim the electronic payment claim to summarize
     * @return a String with the summary of the claim.
     */
    private String createSummaryLineForClaim(ElectronicPaymentClaim claim) {
        StringBuilder summary = new StringBuilder();
        summary.append(claim.getDocumentNumber());
        summary.append('-');
        summary.append(claim.getFinancialDocumentLineNumber().toString());
        try {
            summary.append(' ');
            summary.append(dateTimeService.toDateTimeString(claim.getGeneratingDocument().getDocumentHeader().getDocumentFinalDate()));
            summary.append(' ');
            summary.append(claim.getGeneratingAccountingLine().getAmount());
            summary.append('\n');
        }
        catch (WorkflowException e) {
            LOG.warn("Could not find generating document "+claim.getDocumentNumber());
        }
        return summary.toString();
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingService#createPaymentClaimingDocument(java.util.List, org.kuali.kfs.service.ElectronicPaymentClaimingDocument)
     */
    public String createPaymentClaimingDocument(List<ElectronicPaymentClaim> claims, ElectronicPaymentClaimingDocument documentCreationHelper, UniversalUser user) {
        return documentCreationHelper.createDocumentFromElectronicPayments(claims, user);
    }

    /**
     * 
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingService#getClaimingDocumentChoices(org.kuali.core.bo.user.UniversalUser)
     */
    public List<ElectronicPaymentClaimingDocument> getClaimingDocumentChoices(UniversalUser user) {
        List<ElectronicPaymentClaimingDocument> documentChoices = new ArrayList<ElectronicPaymentClaimingDocument>();
        if (isUserMemberOfClaimingGroup(user)) {
            Map<String, ElectronicPaymentClaimingDocument> claimingDocHelpers = SpringContext.getBeansOfType(ElectronicPaymentClaimingDocument.class);
            ElectronicPaymentClaimingDocument claimingDocHelper;
            
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
            // TODO how 'bout AR?
        }
        return documentChoices;
    }

    /**
     * Sets the referenceFinancialDocumentNumber on each of the payments passed in with the given document number and then saves them. 
     * @param payments a list of payments to claim
     * @param docmentNumber the document number of the claiming document
     */
    public void claimElectronicPayments(List<ElectronicPaymentClaim> payments, String documentNumber) {
        for (ElectronicPaymentClaim payment: payments) {
            payment.setReferenceFinancialDocumentNumber(documentNumber);
            payment.setPaymentClaimStatusCode(ElectronicPaymentClaim.ClaimStatusCodes.CLAIMED);
            businessObjectService.save(payment);
        }
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingService#declaimElectronicPaymentClaimsForDocument(org.kuali.core.document.Document)
     */
    public void declaimElectronicPaymentClaimsForDocument(Document document) {
        Map searchKeys = new HashMap();
        searchKeys.put("referenceFinancialDocumentNumber", document.getDocumentNumber());
        Collection claimsAsObjects = businessObjectService.findMatching(ElectronicPaymentClaim.class, searchKeys);
        for (Object claimAsObject: claimsAsObjects) {
            ElectronicPaymentClaim claim = (ElectronicPaymentClaim)claimAsObject;
            claim.setReferenceFinancialDocumentNumber(null);
            claim.setPaymentClaimStatusCode(ElectronicPaymentClaim.ClaimStatusCodes.UNCLAIMED);
            businessObjectService.save(claim);
        }
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingService#isUserMemberOfClaimingGroup(org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isUserMemberOfClaimingGroup(UniversalUser user) {
        return user.isMember(parameterService.getParameterValue(ElectronicPaymentClaim.class, ELECTRONIC_FUNDS_CLAIMANT_GROUP_PARAMETER)) || isElectronicPaymentAdministrator(user);
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingService#isElectronicPaymentAdministrator(org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isElectronicPaymentAdministrator(UniversalUser user) {
        return user.isMember(parameterService.getParameterValue(ElectronicPaymentClaim.class, ELECTRONIC_PAYMENT_ADMINISTRATOR_GROUP_PARAM_NAME));
    }

    /**
     * @see org.kuali.kfs.service.ElectronicPaymentClaimingService#generateElectronicPaymentClaimRecords(org.kuali.module.financial.document.AdvanceDepositDocument)
     */
    public List<ElectronicPaymentClaim> generateElectronicPaymentClaimRecords(AdvanceDepositDocument doc) {
        List<ElectronicPaymentClaim> claimRecords = new ArrayList<ElectronicPaymentClaim>();
        Map<String, List<String>> electronicFundAccounts = getElectronicFundAccounts();
        for (Object accountingLineAsObj: doc.getSourceAccountingLines()) {
            AccountingLine accountingLine = (AccountingLine)accountingLineAsObj;
            List<String> electronicFundsAccountNumbers = electronicFundAccounts.get(accountingLine.getChartOfAccountsCode());
            if (electronicFundsAccountNumbers != null && electronicFundsAccountNumbers.contains(accountingLine.getAccountNumber())) {
                ElectronicPaymentClaim electronicPayment = createElectronicPayment(doc, accountingLine);
                businessObjectService.save(electronicPayment);
                claimRecords.add(electronicPayment);
            }
        }
        return claimRecords;
    }
    /**
     * This method uses the ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER to find which accounts should cause an accounting line to create an ElectronicPaymentClaim record.
     * @return a List of Maps, where each Map represents an account that electronic funds are posted to.  Each Map has a chart of accounts code as a key and a List of account numbers as a value.
     */
    private Map<String, List<String>> getElectronicFundAccounts() {
        Map<String, List<String>> electronicFundAccounts = new HashMap<String, List<String>>();
        String electronicPaymentAccounts = SpringContext.getBean(ParameterService.class).getParameterValue(AdvanceDepositDocument.class, ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER);
        
        String[] chartSections = electronicPaymentAccounts.split(";");
        for (String chartSection: chartSections) {
            String[] chartAccountPieces = chartSection.split("=");
            if (chartAccountPieces.length >= 2) {
                String chartCode = chartAccountPieces[0];
                if (chartCode != null && chartCode.length() > 0) {
                    String[] accountNumbers = chartAccountPieces[1].split(",");
                    List<String> accountNumbersForChart = electronicFundAccounts.get(chartCode);
                    if (accountNumbersForChart == null) {
                        accountNumbersForChart = new ArrayList<String>();
                    }
                    for (String accountNumber: accountNumbers) {
                        if (accountNumber != null && accountNumber.length() > 0) {
                            accountNumbersForChart.add(accountNumber);
                        }
                    }
                    electronicFundAccounts.put(chartCode, accountNumbersForChart);
                }
            }
        }
        return electronicFundAccounts;
    }
    
    /**
     * Creates an electronic payment claim record to match the given accounting line on the document
     * @param accountingLine an accounting line that an electronic payment claim record should be created for
     * @return the created ElectronicPaymentClaim business object
     */
    private ElectronicPaymentClaim createElectronicPayment(AdvanceDepositDocument document, AccountingLine accountingLine) {
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
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
}
