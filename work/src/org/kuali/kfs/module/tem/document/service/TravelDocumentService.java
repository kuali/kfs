/*
 * Copyright 2011 The Kuali Foundation.
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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeAware;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;

/**
 * Travel Document Service
 */
public interface TravelDocumentService {

    public String getMessageFrom(final String messageType, String... args);

    List<SpecialCircumstances> findActiveSpecialCircumstances(String documentNumber, String documentType);

    List<TravelAuthorizationDocument> findAuthorizationDocuments(final String travelDocumentNumber);

    /**
     * Retrieves the numbers of any Travel Authorization (or child) documents associated with the given trip id
     * @param travelDocumentIdentifier the trip id to find authorizations related to
     * @return a List of TravelAuthorization (or child!) document numbers for authorizations associated with the given trip id
     */
    List<String> findAuthorizationDocumentNumbers(final String travelDocumentIdentifier);

    List<TravelReimbursementDocument> findReimbursementDocuments(final String travelDocumentIdentifier);

    /**
     * Updates the perdiem items in a {@link TravelReimbursementDocument}. Can be used on an empty {@link Collection}. This means
     * that if there are no perdiem items, it will recreate. Perfect do all stupid method.
     *
     * @param documentNumber is the original document number for reference to link the {@link Collection} items to
     * @param perDiemList is the {@link List} that holds the per diem items. Please let this not be null
     * @param perDiemId is the id of the {@link PerDiem} object used for this item
     * @param start is the {@link Date} for the start of the trip
     * @param end is the {@link Date} for the end of the trip
     */
    void updatePerDiemItemsFor(final TravelDocument document, final List<PerDiemExpense> perDiemList, final Integer perDiemId, final Timestamp start, final Timestamp end);

    /**
     * Wrapper function to retrieve by document number
     *
     * @param document
     * @return
     * @throws WorkflowException
     */
    Map<String, List<Document>> getDocumentsRelatedTo(final TravelDocument document) throws WorkflowException;

    /**
     *  Get DV, TA, TAA, TAC, TR, and AV documents related to the given <code>documentNumber</code>. travel document either
     * have a TEM document number or they have the value of the <code>travelDocumentIdentifier</code> in their organization doc
     * ids.
     *
     * @param documentNumber
     * @return
     * @throws WorkflowException
     */
    Map<String, List<Document>> getDocumentsRelatedTo(final String documentNumber) throws WorkflowException;

    /**
     * Get related document lists filtering by the document type
     *
     * @param document
     * @param documentType
     * @return
     */
    List<Document> getDocumentsRelatedTo(final TravelDocument document, String... documentType);


    /**
     * This method will add fyi notes to initiator when document is cancelled, closed, etc.
     *
     * @param document
     */
    void addAdHocFYIRecipient(final Document document);

    /**
     * This method will add fyi notes to initiator when document is cancelled, closed, etc.
     *
     * @param document
     * @param initiatorUserId
     */
    void addAdHocFYIRecipient(final Document document, String initiatorUserId);

    /**
     * This method will add notes to initiator when document is cancelled, closed, etc.
     *
     * @param document
     * @param initiatorUserId
     */
    void addAdHocRecipient(Document document, String initiatorUserId, String actionRequested);

    void routeToFiscalOfficer(final TravelDocument document, final String noteText) throws WorkflowException, Exception;

    /**
     * This method copies the per diem expense object
     *
     * @param perDiemExpense
     * @return the copied per diem expense
     */
    PerDiemExpense copyPerDiemExpense(PerDiemExpense perDiemExpense);

    /**
     * This method calculates mileage and returns calculated value
     *
     * @param actualExpense
     * @return mileageAmount
     */
    KualiDecimal calculateMileage(ActualExpense actualExpense);


    /**
     * This method calculates & updates expense amount for parent & detail records
     *
     * @param actualExpenses
     */
    public KualiDecimal calculateExpenseAmountTotalForMileage(final List<ActualExpense> actualExpenses);

    /**
     *
     */
    public void handleNewActualExpense(final ActualExpense newActualExpenseLine);

    /**
     * This method calculates the daily total for a given per diem mileage expense
     *
     * @param perDiemMilaeage
     * @return a map for each expense (mileage, lodging, per diem)
     */
    Map<String, KualiDecimal> calculateDailyTotal(PerDiemExpense perDiemMilaeage);

    /**
     * This method calculates the daily totals for a list of per diem mileage expenses
     *
     * @param perDiemExpenses
     * @return a list of mapped totals
     */
    List<Map<String, KualiDecimal>> calculateDailyTotals(List<PerDiemExpense> perDiemExpenses);

    /**
     * This method copies from one per diem mileage down the rest of the list
     *
     * @param copyIndex
     * @param perDiemExpenses
     * @return the modified list of perDiemExpenses back
     */
    void copyDownPerDiemExpense(int copyIndex, List<PerDiemExpense> perDiemExpenses);

    /**
     * Determines if an object with an expense type is that of a "hosted" meal. In TEM a hosted meal is a meal that has been
     * provided by a hosting institution and cannot be taken as a reimbursement.
     *
     * @param havingExpenseType has an expense type to check for meal hosting
     * @return true if the expense is a hosted meal or not
     */
    boolean isHostedMeal(final ExpenseTypeAware havingExpenseType);

    /**
     * Check to see if the user has the travel manager role assigned to them
     *
     * @param user
     * @return true if the user is a travel manager, false otherwise
     */
    public boolean isTravelManager(final Person user);

    public Integer calculateProratePercentage(PerDiemExpense perDiemExpense, String perDiemCalcMethod, Timestamp tripEnd);

    public boolean isOpen(TravelDocument document);

    public boolean isProcessed(TravelDocument document);

    public boolean isFinal(TravelDocument document);

    /**
     * Check if the Travel authorization document has been successfully processed
     *
     * @param document
     * @return
     */
    public boolean isTravelAuthorizationProcessed(TravelAuthorizationDocument document);

    /**
     * Check if the Travel authroization document is processed AND is open for reimbursement in the app doc status
     *
     * @param document
     * @return
     */
    public boolean isTravelAuthorizationOpened(TravelAuthorizationDocument document);

    public boolean isUnsuccessful(TravelDocument document);

    public Integer calculatePerDiemPercentageFromTimestamp(PerDiemExpense perDiemExpense, Timestamp tripEnd);

    public KualiDecimal getAmountDueFromInvoice(String documentNumber, KualiDecimal requestedAmount);

    public TravelAuthorizationDocument findCurrentTravelAuthorization(TravelDocument document);

    public TravelDocument findRootForTravelReimbursement(String travelDocumentIdentifier);

    public KualiDecimal getTotalCumulativeReimbursements(TravelDocument document);

    public KualiDecimal getTotalAuthorizedEncumbrance(TravelDocument document);

    public boolean isResponsibleForAccountsOn(final TravelDocument document, String principalId);

    public boolean checkNonEmployeeTravelerTypeCode(String travelerTypeCode);

    /**
     * This is a ajax method, will be used to retrieve all states based on the country code passed
     *
     * @param countryCode
     * @return String
     */
    public String getAllStates(final String countryCode);

    /**
     * Copies group travelers and sets new document number
     */
    public List<GroupTraveler> copyGroupTravelers(List<GroupTraveler> groupTravelers, String documentNumber);

    /**
     * Copies other travel expenses and sets new document number
     */
    public List<? extends TemExpense> copyActualExpenses(List<? extends TemExpense> actualExpenses, String documentNumber);

    /**
     * Copies per diem expenses and sets new document number
     */
    public List<PerDiemExpense> copyPerDiemExpenses(List<PerDiemExpense> perDiemExpenses, String documentNumber);

    /**
     * Copies travel advances and sets new document number
     */
    public List<TravelAdvance> copyTravelAdvances(List<TravelAdvance> travelAdvances, String documentNumber);

    /**
     * Copies special circumstances and sets new document number
     */
    public List<SpecialCircumstances> copySpecialCircumstances(List<SpecialCircumstances> specialCircumstancesList, String documentNumber);

    /**
     * Copies transportation mode details and sets new document number
     */
    public List<TransportationModeDetail> copyTransportationModeDetails(List<TransportationModeDetail> transportationModeDetails, String documentNumber);

    /**
     *
     * @param document
     */
    public void showNoTravelAuthorizationError(TravelReimbursementDocument document);

    //Map<String,Object> calculateTotalsFor(final TravelDocument travelDocument);

    /**
     *
     * This method gets the total of all advances given for the trip relating to the travel document.
     * @param travelDocument
     * @return {@linK KualiDecimal} that is the total of all advances
     */
    KualiDecimal getAdvancesTotalFor(final TravelDocument travelDocument);

    /**
     * get all outstanding travel advances by the given invoice document numbers. The advances must have not been used to generate
     * taxable ramification
     *
     * @param arInvoiceDocNumbers the given AR invoice document numbers
     * @return a list of outstanding travel advances
     */

    List<TravelAdvance> getOutstandingTravelAdvanceByInvoice(Set<String> arInvoiceDocNumber);

    String retrieveAddressFromLocationCode(String locationCode);

    /**
     * Remove the imported expense from the document (though DB)
     *
     * @param document
     */
    public void detachImportedExpenses(TravelDocument document);

    /**
     * Adding the imported expense to the document (in the DB)
     *
     * @param document
     */
    public void attachImportedExpenses(TravelDocument document);

    public boolean checkHoldGLPEs(TravelDocument document);

    public void revertOriginalDocument(TravelDocument travelDocument, String status);

    /**
     * find the latest taxable ramification notification date
     *
     * @return the latest taxable ramification notification date
     */
    Date findLatestTaxableRamificationNotificationDate();

    /**
     * Perform validation on accounting lines that have already been entered, but have the potential to have bad data inserted into the db.
     * @param TravelDocument
     * @return
     *      true if valid, false otherwise
     */
    public boolean validateSourceAccountingLines(TravelDocument travelDocument, boolean addToErrorPath);

    /**
     * Get the Generic document type name of the travel document
     *
     * TA(not TAC, TAA), TR, ENT, RELO
     *
     * @param document
     * @return
     */
    public String getDocumentType(TravelDocument document);

    /**
     *
     * This method creates a key-value pair of MileageRates that are valid for the searchDate.
     * @param searchDate
     * @return
     */
    List<KeyValue> getMileageRateKeyValues(Date searchDate);

    /**
     * Import {@link GroupTraveler} instances into a {@link TravelDocument} via CSV data
     *
     *  @param document to add {@link GroupTraveler} instances to
     *  @param csvData
     *  @throws Exception when there's an error parsing the CSV data
     */
    List<GroupTraveler> importGroupTravelers(final TravelDocument document, final String csvData) throws Exception;

    /**
     *
     * This method imports the file and convert it to a list of objects (of the class specified in the parameter)
     * @param formFile
     * @param c
     * @param attributeNames
     * @param tabErrorKey
     * @return
     */
    <T> List<T> importFile(final String fileContents, final Class<T> c, final String[] attributeNames,
                           final Map<String,List<String>> defaultValues, final Integer[] attributeMaxLength, final String tabErrorKey);

    /**
     * Returns all travel advances associated with the passed in trip id
     * @param travelDocumentIdentifier the trip id of the reimbursement
     * @return a List of all TravelAdvances associated with that trip
     */
    public List<TravelAdvance> getTravelAdvancesForTrip(String travelDocumentIdentifier);

    /**
     * @return the default AR organization options for TEM documents
     */
    public AccountsReceivableOrganizationOptions getOrgOptions();

    /**
     * This method searches to make sure that the expense entered doesn't already exist
     * If they exist, disable them in the per diem table and notify the user.
     * @param trDocument
     *          the current doc.
     * @param actualExpense
     *          the expense in question
     */
    public void disableDuplicateExpenses(TravelDocument trDocument, ActualExpense actualExpense);

    /**
     * Sets the meal and incidental amounts on the given per diem expense
     * @param expense the expense to set amounts on
     * @param perDiem the per diem record amounts are based off of
     * @param tripType the trip type being taken
     * @param tripEnd the end time of the trip
     * @param shouldProrate whether this expense should be prorated
     */
    public void setPerDiemMealsAndIncidentals(PerDiemExpense expense, PerDiem perDiem, TripType tripType, Timestamp tripEnd, boolean shouldProrate);

    /**
     * Finds the mileage rate for the given expense type code and expense date
     * @param expenseTypeCode the expense type to find a related mileage rate for
     * @param expenseDate the date of the expense date, passed in as mileage rates are effectively dated
     * @return the matching mileage rate
     */
    public MileageRate getMileageRate(String expenseTypeCode, java.sql.Date expenseDate);

    /**
     *
     * This method gets the current travel document by travel document identifier
     *  @param travelDocumentIdentifier
     *  @return
     */
    public TravelDocument getTravelDocument(String travelDocumentIdentifier);



    /**
     * Determines if the per diem and actual expense total on the given document equal that of the persisted document
     * (useful if trying to figure out if mileage rate or per diem rates changed)
     * @param travelDocument the travel document to check
     * @return true if the totals are unchanged, false otherwise
     */
    public boolean travelDocumentTotalsUnchangedFromPersisted(TravelDocument travelDocument);

    /**
     * This method retrieves a list of approved documents related to a travelDocumentIdentifier. It will grab the most current TA (TAA, TAC)
     * and all TR, ENT, and RELO for the given identifier.
     *
     * @param travelDocumentIdentifier
     * @return
     */
    public Collection<String> getApprovedTravelDocumentNumbersByTrip(String travelDocumentIdentifier);

    /**
     * find matching trips  for the same traveler, dates
     *
     */
    public List<String> findMatchingTrips(TravelDocument document);

    /**
     * If a value on a per diem expense has been zero'd out, this method will restore it to its default value
     * @param document the document with per diem expenses
     * @param property the property to restore
     */
    public void restorePerDiemProperty(TravelDocument document, String property);

}
