/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.service;

import java.util.List;

import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;

/**
 * A service which helps in the claiming of ElectronicPaymentClaim records
 */
public interface ElectronicPaymentClaimingService {

    /**
     * Constructs a List of Notes that detail which ElectronicPaymentClaim records have been claimed by a document
     * 
     * @param claims the ElectronicPaymentClaim record that will be claimed by a document
     * @param claimingUser the user who's actually claiming ElectronicPaymentClaim records
     * @return a List of Notes that will summarize that claiming.
     */
    public abstract List<String> constructNoteTextsForClaims(List<ElectronicPaymentClaim> claims);

    /**
     * Returns a list of which document types the given user can claim Electronic Payment Claims with.
     * 
     * @param user the user attempting to use a document to claim ElectronicPaymentClaim records
     * @return a list of ElectronicPaymentClaimingDocumentGenerationStrategy document helper implementations
     */
    public abstract List<ElectronicPaymentClaimingDocumentGenerationStrategy> getClaimingDocumentChoices(Person user);

    /**
     * Given a List of ElectronicPaymentClaim records and a ElectronicPaymentClaimingDocumentGenerationStrategy document helper
     * implementation, creates a document that will claim; this method should also do the work of "claiming" each of the given
     * ElectronicPaymentClaim records by filling in their referenceFinancialDocumentNumber field.
     * 
     * @param claims the List of ElectronicPaymentClaim records to claim with a document
     * @param documentCreationHelper the document helper which will help this method in constructing the claiming document
     * @param user the Person record of the user who is claiming the given electronic payments
     * @return the URL to redirect to, so the user can edit the document
     */
    public abstract String createPaymentClaimingDocument(List<ElectronicPaymentClaim> claims, ElectronicPaymentClaimingDocumentGenerationStrategy documentCreationHelper, Person user);

    /**
     * Unclaims all ElectronicPaymentClaim records claimed by the given document, by setting the ElectronicPaymentClaim's reference
     * document to null.
     * 
     * @param document the document that claimed ElectronicPaymentClaims and now needs to give them back
     */
    public abstract void declaimElectronicPaymentClaimsForDocument(Document document);

    /**
     * Sets the referenceFinancialDocumentNumber on each of the payments passed in with the given document number and then saves
     * them.
     * 
     * @param payments a list of payments to claim
     * @param docmentNumber the document number of the claiming document
     */
    public abstract void claimElectronicPayments(List<ElectronicPaymentClaim> payments, String documentNumber);

    /**
     * Returns a list of SAVED electronic payment claims from the lines of an AdvanceDepositDocument
     * 
     * @param doc the document that is generating electronic payment claim records
     * @return a list of the generated electronic payment claim records
     */
    public abstract List<ElectronicPaymentClaim> generateElectronicPaymentClaimRecords(AdvanceDepositDocument doc);
    
    /**
     * Determines if the given accounting line represents an electronic payment
     * @param accountingLine the accounting line to check
     * @return true if the accounting line does represent an electronic payment, false otherwise
     */
    public abstract boolean representsElectronicFundAccount(AccountingLine accountingLine);

    /**
     * check whether the given user has permission to claim eletronic payment for the given document type defined in the specified
     * namespace
     * 
     * @param user the given user being checked
     * @param namespaceCode the specified namespace
     * @param workflowDocumentTypeName the workflow document type name of the document being claimed
     * @return true if the user has permisson to claim electronic payment; otherwise, false
     */
    public abstract boolean isAuthorizedForClaimingElectronicPayment(Person user, String workflowDocumentTypeName);

}
