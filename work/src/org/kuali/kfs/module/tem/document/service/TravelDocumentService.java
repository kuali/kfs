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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeAware;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Travel Document Service
 */
public interface TravelDocumentService {
    Map<String, List<Document>> getDocumentsRelatedTo(final String documentNumber) throws WorkflowException;

    public String getMessageFrom(final String messageType, String... args);

    void setDocumentService(DocumentService documentService);

    void setDataDictionaryService(DataDictionaryService dataDictionaryService);

    List<SpecialCircumstances> findActiveSpecialCircumstances(String documentNumber, String documentType);

    <T> List<T> find(final Class<T> travelDocumentClass, final String travelDocumentNumber) throws WorkflowException;

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
     * Get DV, TA, TAA, TAC, TR, and AV documents related to the given <code>document</code>. travel document either have a travel
     * document number or they have the value of the <code>document</code> in their organization doc ids.
     * 
     * @param document {@link TravelDocument} to get other document instances related to
     * @return A {@link Map} of {@link Document} instances where the key is the document type name
     */
    Map<String, List<Document>> getDocumentsRelatedTo(final TravelDocument document) throws WorkflowException;

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
     * This method returns total expense amount for the detail records
     * 
     * @param actualExpense
     */
    public KualiDecimal getExpenseAmountTotalFromDetail(ActualExpense actualExpense);

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

    void setTravelDocumentDao(final TravelDocumentDao travelDocumentDao);

    /**
     * Converts an {@link Encumbrance} instance to a {@link GeneralLedgerPendingEntrySourceDetail}. The purpose is to create
     * disencumbering transactions for the {@link TravelAuthorizationCloseDocument}
     * 
     * @param encumbrance to convert
     * @return {@link GeneralLedgerPendingEntrySourceDetail} converted
     */
    GeneralLedgerPendingEntrySourceDetail convertTo(final Encumbrance toConvert);

    void disencumberFunds(TravelDocument taDoc);

    void updateEncumbranceObjectCode(TravelDocument taDoc, SourceAccountingLine line);

    public void adjustEncumbranceForClose(TravelDocument taDocument);

    public void adjustEncumbranceForAmendment(TravelDocument taDocument);

    public void processRelatedDocuments(TravelDocument document);

    public void liquidateEncumbrance(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document);

    /**
     * Determines if an object with an expense type is that of a "hosted" meal. In TEM a hosted meal is a meal that has been
     * provided by a hosting institution and cannot be taken as a reimbursement.
     * 
     * @param havingExpenseType has an expense type to check for meal hosting
     * @return true if the expense is a hosted meal or not
     */
    boolean isHostedMeal(final ExpenseTypeAware havingExpenseType);


    /**
     * This method creates the pending entry based on the document and encumbrance
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @return pendingEntry The completed pending entry.
     */
    public GeneralLedgerPendingEntry setupPendingEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document);   
    
    /**
     * This method creates the offset entry based on the pending entry, document, and encumbrance
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @param pendingEntry The pending entry that will accompany the offset entry.
     * @return offsetEntry The completed offset entry.
     */
    public GeneralLedgerPendingEntry setupOffsetEntry(Encumbrance encumbrance, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry);

    /**
     * Check to see if the user has the travel arranger role assigned to them
     * 
     * @param user
     * @param primaryDepartmentCode
     * @return true if the user is a travel arranger, false otherwise
     */
    public boolean isTravelArranger(final Person user, final String primaryDepartmentCode);
    
    /**
     * Check to see if the user has the travel arranger role assigned to them
     * 
     * @param user
     * @return true if the user is a travel arranger, false otherwise
     */
    public boolean isTravelArranger(final Person user);

    /**
     * Check to see if the user has the travel manager role assigned to them
     * 
     * @param user
     * @return true if the user is a travel manager, false otherwise
     */
    public boolean isTravelManager(final Person user);
    
    /**
     * Check to see if the user has the fiscal officer role assigned to them
     * 
     * @param user
     * @return true if the user is a fiscal officer, false otherwise
     */
    public boolean isFiscalOfficer(final Person user);    

    public Integer calculateProratePercentage(PerDiemExpense perDiemExpense, String perDiemCalcMethod, Timestamp tripEnd);
    
    public boolean isOpen(TravelDocument document);

    public boolean isProcessed(TravelDocument document);

    public boolean isFinal(TravelDocument document);  

    public boolean isUnsuccessful(TravelDocument document);

    public Integer calculatePerDiemPercentageFromTimestamp(PerDiemExpense perDiemExpense, Timestamp tripEnd);

    public KualiDecimal getAmountDueFromInvoice(String documentNumber, KualiDecimal requestedAmount);
        
    public TravelDocument findCurrentTravelAuthorization(TravelDocument document) throws WorkflowException;  
    
    public KualiDecimal getTotalCumulativeReimbursements(TravelDocument document);
    
    public KualiDecimal getTotalAuthorizedEncumbrance(TravelDocument document);
    
    public boolean isResponsibleForAccountsOn(final TravelDocument document, String principalId);
    
    public void populateRequisitionFields(RequisitionDocument reqsDoc,TravelDocument document);
    
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
    public List<? extends TEMExpense> copyActualExpenses(List<? extends TEMExpense> actualExpenses, String documentNumber);
    
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
    
    public boolean checkPersonRole(final Person user, String role, String parameterNamespace);
    
    public boolean checkOrganizationRole(final Person user, String role, String parameterNamespace, String primaryDepartmentCode);
    
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

    public void detachImportedExpenses(TravelDocument document);
    
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
     * Get Document type of the travel document
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
    List<KeyLabelPair> getMileageRateKeyValues(Date searchDate);
    
    /**
     * Truncate the financial system document header to have maximum length (40)
     * 
     * @param header
     */
    void trimFinancialSystemDocumentHeader(FinancialSystemDocumentHeader header);
}
