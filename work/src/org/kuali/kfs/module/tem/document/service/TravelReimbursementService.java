/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLineTotalPercentage;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * Travel Reimbursement Service
 *
 */
public interface TravelReimbursementService {

    /**
     * Locate all {@link TravelReimbursementDocument} instances with the same
     * <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelReimbursementDocument} instances
     * @return {@link List} of {@link TravelReimbursementDocument} instances
     */
    List<TravelReimbursementDocument> findByTravelId(String travelDocumentIdentifier) throws WorkflowException;

    /**
     * Find the {@link TravelReimbursementDocument} instance with the same <code>documentNumber</code>
     *
     * @param documentNumber to locate {@link TravelReimbursementDocument} instances
     * @return  {@link TravelReimbursementDocument}
     */
    TravelReimbursementDocument find(String documentNumber) throws WorkflowException;

    /**
     * Adds {@link PropertyChangeListener} instances to the reimbursement to abstract property changes
     *
     * @param reimbursement to add listeners to
     */
    void addListenersTo(final TravelReimbursementDocument reimbursement);

    /**
     *     Search for the INV associated with the Travel Authorization from AR (Org Doc Number = Trip ID)
     *
     *     If any amount is left in the invoice - determine CRM spawn by TA
     *
     *     Compute the reimbursable amount = total year-to-date amount reimbursed for this trip plus reimbursable amount for this TR
     *     (possibly in TA?)
     *
     *         1. reimbursable amount >= INV
     *              Spawn a customer credit memo (CRM) up to the Invoice amount
     *              The traveler will be reimbursed for the difference by (DV)
     *
     *         2. reimbursable amount < INV
     *              Spawn a customer credit memo (CRM) for the reimbursable amount
     *              The traveler will not receive any reimbursement - No DV necessary
     *
     *        3. If there is no reimbursement for this travel $0
     *              No CRM & No DV ?? TR w/ no reimbursement?
     *
     *        4. There is no INV, then do not spawn a credit memo - under case 1
     *
     * @param reimbursement
     * @throws WorkflowException
     */
    void processCustomerReimbursement(final TravelReimbursementDocument reimbursement) throws WorkflowException;

    /**
     * Use a reimbursement to create a {@link CustomerCreditMemoDocument}. Nothing is returned because the
     * created document will be blanketApproved and will show up in the "relatedDocuments" section
     *
     * AccountsReceivableCustomerInvoice
     *
     * @param reimbursement
     * @param invoice {@link AccountsReceivableCustomerInvoice} invoice used for generating  {@link CustomerCreditMemoDocument}
     * @param creditAmount amount to be credited by the {@link CustomerCreditMemoDocument}
     * @throws WorkflowException
     */
    void spawnCustomerCreditMemoDocument(final TravelReimbursementDocument reimbursement, AccountsReceivableCustomerInvoice invoice, KualiDecimal creditAmount) throws WorkflowException;

    /**
     * Using the related documents of the {@link TravelReimbursementDocument} TR, look up the open {@link TravelAuthorizationDocument} TA
     * If there are ANY {@link TravelAuthorizationAmendmentDocument} TAA, it should select the latest TAA doc with OPEN status.
     *
     * @param reimbursement to use for creating the {@link CustomerCreditMemoDocument}
     */
    TravelAuthorizationDocument getRelatedOpenTravelAuthorizationDocument(final TravelReimbursementDocument reimbursement) ;

    /**
     * Notification when the original trip date is changed. A note is left on the workflow document detailing
     * the date change with the message DATE_CHANGED_MESSAGE
     *
     * @param reimbursement {@link TravelReimbursementDocument} for this trip
     * @param start original start {@link Date}
     * @param end original end {@link Date}
     */
    void notifyDateChangedOn(final TravelReimbursementDocument reimbursement, final Date start, final Date end) throws Exception;

    /**
     *
     * Checks to see if the trip date changed from the TA dates. If the dates have changed, a note is left on the
     * workflow document detailing the date change with the message DATE_CHANGED_MESSAGE (by calling notifyDateChangedOn())
     *
     * @param travelReqDoc {@link TravelReimbursementDocument} for this trip
     * @param taDoc {@link TravelAuthorizationDocument} for this trip
     */
    void addDateChangedNote(TravelReimbursementDocument travelReqDoc, TravelAuthorizationDocument taDoc);

    /**
     * This method uses the values provided to build and populate a cover sheet associated with a given {@link Document}.
     *
     * @param document {@link TravelReimbursementDocument} to generate a coversheet for
     * @return {@link Coversheet} instance
     */
    Coversheet generateCoversheetFor(final TravelReimbursementDocument document) throws Exception;

    /**
     * the actual reimbursable amount to the traveler.  This includes the calculation for open invoices which will
     * not be paid back to the traveler.
     *
     *  TEM requested reimbursable amount subtract open invoices amount
     *
     * @param reimbursementDocument
     * @return
     */
    public KualiDecimal getReimbursableToTraveler(TEMReimbursementDocument reimbursementDocument);

    /**
     * This checks to see if the expense no longer exists, and if it doesn't, enables the expense that was disabled
     * @param trDocument
     * @param actualExpense
     */
    public void enableDuplicateExpenses(TravelReimbursementDocument trDocument, ActualExpense actualExpense);

    /**
     * Generates pending entries to balance out non-balanced out travel advances; the generated pending entries will be added to the document directly
     * @param trDocument the reimbursement document of the trip to balance out non-balanced out advances for
     * @param sequenceHelper the pending entry sequence generator for this generation process
     */
    public void generateEntriesForAdvances(TravelReimbursementDocument trDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * @return true if Travel Authorization Required is set to true for all Trip Types
     */
    public boolean doAllReimbursementTripTypesRequireTravelAuthorization();

    /**
     * Calculates the total amount of open invoices for this trip
     * @param reimbursementDocument a reimbursement in the trip to find the open invoice amount for
     * @return the total open invoice amount
     */
    public KualiDecimal getInvoiceAmount(TEMReimbursementDocument reimbursementDocument);

    /**
     * Calculates how much each of the given accounting lines contributes to the total of the accounting lines
     * @param accountingLines the accounting lines to find the percentage contribution of each of
     * @return a List of the accounting lines and their corresponding percentages
     */
    public List<TemSourceAccountingLineTotalPercentage> getPercentagesForLines(List<TemSourceAccountingLine> accountingLines);

    /**
     * Generates accounting lines which will act as source details to generate the crediting glpes to pay back the advance
     * @param linePercentages the accounting lines which paid for the advance and the amount they
     * @param paymentAmount the total amount of the current invoice which is being paid back
     * @param documentNumber the document number of the reimbursement which is crediting the advance we're paying back here
     * @return a List of TemSourceAccountingLines which will be source details to generate GLPEs
     */
    public List<TemSourceAccountingLine> createAccountingLinesFromPercentages(List<TemSourceAccountingLineTotalPercentage> linePercentages, KualiDecimal paymentAmount, String documentNumber);

    /**
     * Calculates the sum of a list of AccountingLines
     * @param accountingLines the accounting lines to add together
     * @return the sum of those accounting lines
     */
    public KualiDecimal calculateLinesTotal(List<TemSourceAccountingLine> accountingLines);
}
