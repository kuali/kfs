/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeAware;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;


/**
 * Mock Travel Document Service Implementation
 */
public class MockTravelDocumentServiceImpl implements TravelDocumentService {

    protected static Logger LOG = Logger.getLogger(MockTravelDocumentServiceImpl.class);

    protected TravelDocumentService realTravelDocumentService;
    protected DateTimeService dateTimeService;

    public static final String VALID_DOCUMENT_IDENTIFIER = "T-MOCK";
    public static final int VALID_TEM_PROFILE_ID = 37;

    @Override
    public TravelDocument findRootForTravelReimbursement(String travelDocumentIdentifier) {

        if (StringUtils.equals(travelDocumentIdentifier, VALID_DOCUMENT_IDENTIFIER)) {
            return createTA();
        }
        else {
            return realTravelDocumentService.findRootForTravelReimbursement(travelDocumentIdentifier);
        }
    }

    protected TravelAuthorizationDocument createTA() {
        TravelAuthorizationDocument ta = new TravelAuthorizationDocument();
        ta.setTravelDocumentIdentifier(VALID_DOCUMENT_IDENTIFIER);

        ta.setTemProfile(createTemProfile());
        ta.setTemProfileId(ta.getTemProfile().getProfileId());

        return ta;
    }

    protected TemProfile createTemProfile() {
        TemProfile profile = new TemProfile();
        profile.setProfileId(VALID_TEM_PROFILE_ID);
        profile.getTemProfileAddress().setProfileId(VALID_TEM_PROFILE_ID);
        profile.setDefaultChartCode("BL");
        profile.setDefaultAccount("1031400");
        profile.setDefaultSubAccount("ADV");
        profile.setDefaultProjectCode("KUL");
        profile.setDateOfBirth(dateTimeService.getCurrentSqlDate());
        profile.setGender("M");
        profile.setHomeDeptOrgCode("BL");
        profile.setHomeDeptChartOfAccountsCode("BL");
        return profile;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.realTravelDocumentService = travelDocumentService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

/** methods which haven't been changed for testing **/

    @Override
    public String getMessageFrom(final String messageType, String... args) {
        return realTravelDocumentService.getMessageFrom(messageType, args);
    }

    @Override
    public List<SpecialCircumstances> findActiveSpecialCircumstances(String documentNumber, String documentType) {
        return realTravelDocumentService.findActiveSpecialCircumstances(documentNumber, documentType);
    }

    @Override
    public List<TravelAuthorizationDocument> findAuthorizationDocuments(final String travelDocumentNumber) {
        return realTravelDocumentService.findAuthorizationDocuments(travelDocumentNumber);
    }

    @Override
    public List<String> findAuthorizationDocumentNumbers(final String travelDocumentNumber) {
        return realTravelDocumentService.findAuthorizationDocumentNumbers(travelDocumentNumber);
    }

    @Override
    public List<TravelReimbursementDocument> findReimbursementDocuments(final String travelDocumentNumber) {
        return realTravelDocumentService.findReimbursementDocuments(travelDocumentNumber);
    }

    @Override
    public void updatePerDiemItemsFor(final TravelDocument document, final List<PerDiemExpense> perDiemList, final Integer perDiemId, final Timestamp start, final Timestamp end) {
        realTravelDocumentService.updatePerDiemItemsFor(document, perDiemList, perDiemId, start, end);
    }

    @Override
    public Map<String, List<Document>> getDocumentsRelatedTo(final TravelDocument document) throws WorkflowException {
        return realTravelDocumentService.getDocumentsRelatedTo(document);
    }

    @Override
    public Map<String, List<Document>> getDocumentsRelatedTo(final String documentNumber) throws WorkflowException {
        return realTravelDocumentService.getDocumentsRelatedTo(documentNumber);
    }

    @Override
    public List<Document> getDocumentsRelatedTo(final TravelDocument document, String... documentType) {
        return realTravelDocumentService.getDocumentsRelatedTo(document, documentType);
    }

    @Override
    public void addAdHocFYIRecipient(final Document document) {
        realTravelDocumentService.addAdHocFYIRecipient(document);
    }

    @Override
    public void addAdHocFYIRecipient(final Document document, String initiatorUserId) {
        realTravelDocumentService.addAdHocFYIRecipient(document, initiatorUserId);
    }

    @Override
    public void addAdHocRecipient(Document document, String initiatorUserId, String actionRequested) {
        realTravelDocumentService.addAdHocRecipient(document, initiatorUserId, actionRequested);
    }

    @Override
    public void routeToFiscalOfficer(final TravelDocument document, final String noteText) throws WorkflowException, Exception {
        realTravelDocumentService.routeToFiscalOfficer(document, noteText);
    }

    @Override
    public PerDiemExpense copyPerDiemExpense(PerDiemExpense perDiemExpense) {
        return realTravelDocumentService.copyPerDiemExpense(perDiemExpense);
    }

    @Override
    public KualiDecimal calculateMileage(ActualExpense actualExpense) {
        return realTravelDocumentService.calculateMileage(actualExpense);
    }

    @Override
    public KualiDecimal calculateExpenseAmountTotalForMileage(final List<ActualExpense> actualExpenses) {
        return realTravelDocumentService.calculateExpenseAmountTotalForMileage(actualExpenses);
    }

    @Override
    public void handleNewActualExpense(final ActualExpense newActualExpenseLine) {
        realTravelDocumentService.handleNewActualExpense(newActualExpenseLine);
    }

    @Override
    public Map<String, KualiDecimal> calculateDailyTotal(PerDiemExpense perDiemMilaeage) {
        return realTravelDocumentService.calculateDailyTotal(perDiemMilaeage);
    }

    @Override
    public List<Map<String, KualiDecimal>> calculateDailyTotals(List<PerDiemExpense> perDiemExpenses) {
        return realTravelDocumentService.calculateDailyTotals(perDiemExpenses);
    }

    @Override
    public void copyDownPerDiemExpense(int copyIndex, List<PerDiemExpense> perDiemExpenses) {
        realTravelDocumentService.copyDownPerDiemExpense(copyIndex, perDiemExpenses);
    }

    @Override
    public boolean isHostedMeal(final ExpenseTypeAware havingExpenseType) {
        return realTravelDocumentService.isHostedMeal(havingExpenseType);
    }

    @Override
    public boolean isTravelManager(final Person user) {
        return realTravelDocumentService.isTravelManager(user);
    }

    @Override
    public Integer calculateProratePercentage(PerDiemExpense perDiemExpense, String perDiemCalcMethod, Timestamp tripEnd) {
        return realTravelDocumentService.calculateProratePercentage(perDiemExpense, perDiemCalcMethod, tripEnd);
    }

    @Override
    public boolean isOpen(TravelDocument document) {
        return realTravelDocumentService.isOpen(document);
    }

    @Override
    public boolean isProcessed(TravelDocument document) {
        return realTravelDocumentService.isProcessed(document);
    }

    @Override
    public boolean isFinal(TravelDocument document) {
        return realTravelDocumentService.isFinal(document);
    }

    @Override
    public boolean isTravelAuthorizationProcessed(TravelAuthorizationDocument document) {
        return realTravelDocumentService.isTravelAuthorizationProcessed(document);
    }

    @Override
    public boolean isTravelAuthorizationOpened(TravelAuthorizationDocument document) {
        return realTravelDocumentService.isTravelAuthorizationOpened(document);
    }

    @Override
    public boolean isUnsuccessful(TravelDocument document) {
        return realTravelDocumentService.isUnsuccessful(document);
    }

    @Override
    public Integer calculatePerDiemPercentageFromTimestamp(PerDiemExpense perDiemExpense, Timestamp tripEnd) {
        return realTravelDocumentService.calculatePerDiemPercentageFromTimestamp(perDiemExpense, tripEnd);
    }

    @Override
    public KualiDecimal getAmountDueFromInvoice(String documentNumber, KualiDecimal requestedAmount) {
        return realTravelDocumentService.getAmountDueFromInvoice(documentNumber, requestedAmount);
    }

    @Override
    public TravelAuthorizationDocument findCurrentTravelAuthorization(TravelDocument document) {
        return realTravelDocumentService.findCurrentTravelAuthorization(document);
    }

    @Override
    public KualiDecimal getTotalCumulativeReimbursements(TravelDocument document) {
        return realTravelDocumentService.getTotalCumulativeReimbursements(document);
    }

    @Override
    public KualiDecimal getTotalAuthorizedEncumbrance(TravelDocument document) {
        return realTravelDocumentService.getTotalAuthorizedEncumbrance(document);
    }

    @Override
    public boolean isResponsibleForAccountsOn(final TravelDocument document, String principalId) {
        return realTravelDocumentService.isResponsibleForAccountsOn(document, principalId);
    }

    @Override
    public boolean checkNonEmployeeTravelerTypeCode(String travelerTypeCode) {
        return realTravelDocumentService.checkNonEmployeeTravelerTypeCode(travelerTypeCode);
    }

    @Override
    public String getAllStates(final String countryCode) {
        return realTravelDocumentService.getAllStates(countryCode);
    }

    @Override
    public List<GroupTraveler> copyGroupTravelers(List<GroupTraveler> groupTravelers, String documentNumber) {
        return realTravelDocumentService.copyGroupTravelers(groupTravelers, documentNumber);
    }

    @Override
    public List<? extends TemExpense> copyActualExpenses(List<? extends TemExpense> actualExpenses, String documentNumber) {
        return realTravelDocumentService.copyActualExpenses(actualExpenses, documentNumber);
    }

    @Override
    public List<PerDiemExpense> copyPerDiemExpenses(List<PerDiemExpense> perDiemExpenses, String documentNumber) {
        return realTravelDocumentService.copyPerDiemExpenses(perDiemExpenses, documentNumber);
    }

    @Override
    public List<TravelAdvance> copyTravelAdvances(List<TravelAdvance> travelAdvances, String documentNumber) {
        return realTravelDocumentService.copyTravelAdvances(travelAdvances, documentNumber);
    }

    @Override
    public List<SpecialCircumstances> copySpecialCircumstances(List<SpecialCircumstances> specialCircumstancesList, String documentNumber) {
        return realTravelDocumentService.copySpecialCircumstances(specialCircumstancesList, documentNumber);
    }

    @Override
    public List<TransportationModeDetail> copyTransportationModeDetails(List<TransportationModeDetail> transportationModeDetails, String documentNumber) {
        return realTravelDocumentService.copyTransportationModeDetails(transportationModeDetails, documentNumber);
    }

    @Override
    public void showNoTravelAuthorizationError(TravelReimbursementDocument document) {
        realTravelDocumentService.showNoTravelAuthorizationError(document);
    }

    @Override
    public KualiDecimal getAdvancesTotalFor(final TravelDocument travelDocument) {
        return realTravelDocumentService.getAdvancesTotalFor(travelDocument);
    }

    @Override
    public List<TravelAdvance> getOutstandingTravelAdvanceByInvoice(Set<String> arInvoiceDocNumber) {
        return realTravelDocumentService.getOutstandingTravelAdvanceByInvoice(arInvoiceDocNumber);
    }

    @Override
    public String retrieveAddressFromLocationCode(String locationCode) {
        return realTravelDocumentService.retrieveAddressFromLocationCode(locationCode);
    }

    @Override
    public void detachImportedExpenses(TravelDocument document) {
        realTravelDocumentService.detachImportedExpenses(document);
    }

    @Override
    public void attachImportedExpenses(TravelDocument document) {
        realTravelDocumentService.attachImportedExpenses(document);
    }

    @Override
    public boolean checkHoldGLPEs(TravelDocument document) {
        return realTravelDocumentService.checkHoldGLPEs(document);
    }

    @Override
    public void revertOriginalDocument(TravelDocument travelDocument, String status) {
        realTravelDocumentService.revertOriginalDocument(travelDocument, status);
    }

    @Override
    public Date findLatestTaxableRamificationNotificationDate() {
        return realTravelDocumentService.findLatestTaxableRamificationNotificationDate();
    }

    @Override
    public boolean validateSourceAccountingLines(TravelDocument travelDocument, boolean addToErrorPath) {
        return realTravelDocumentService.validateSourceAccountingLines(travelDocument, addToErrorPath);
    }

    @Override
    public String getDocumentType(TravelDocument document) {
        return realTravelDocumentService.getDocumentType(document);
    }

    @Override
    public List<KeyValue> getMileageRateKeyValues(Date searchDate) {
        return realTravelDocumentService.getMileageRateKeyValues(searchDate);
    }

    @Override
    public List<GroupTraveler> importGroupTravelers(final TravelDocument document, final String csvData) throws Exception {
        return realTravelDocumentService.importGroupTravelers(document, csvData);
    }

    @Override
    public <T> List<T> importFile(final String fileContents, final Class<T> c, final String[] attributeNames,
            final Map<String,List<String>> defaultValues, final Integer[] attributeMaxLength, final String tabErrorKey) {
        return realTravelDocumentService.importFile(fileContents, c, attributeNames, defaultValues, attributeMaxLength, tabErrorKey);
    }

    @Override
    public List<TravelAdvance> getTravelAdvancesForTrip(String travelDocumentIdentifier) {
        return realTravelDocumentService.getTravelAdvancesForTrip(travelDocumentIdentifier);
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrgOptions() {
        return realTravelDocumentService.getOrgOptions();
    }

    @Override
    public void disableDuplicateExpenses(TravelDocument trDocument, ActualExpense actualExpense) {
        realTravelDocumentService.disableDuplicateExpenses(trDocument, actualExpense);
    }

    @Override
    public void setPerDiemMealsAndIncidentals(PerDiemExpense expense, PerDiem perDiem, TripType tripType, Timestamp tripEnd, boolean shouldProrate) {
        realTravelDocumentService.setPerDiemMealsAndIncidentals(expense, perDiem, tripType, tripEnd, shouldProrate);
    }

    @Override
    public MileageRate getMileageRate(String expenseTypeCode, Date expenseDate) {
        return realTravelDocumentService.getMileageRate(expenseTypeCode, expenseDate);
    }

    @Override
    public TravelDocument getTravelDocument(String travelDocumentIdentifier) {
        return realTravelDocumentService.getTravelDocument(travelDocumentIdentifier);
    }

    @Override
    public boolean travelDocumentTotalsUnchangedFromPersisted(TravelDocument travelDocument) {
        return realTravelDocumentService.travelDocumentTotalsUnchangedFromPersisted(travelDocument);
    }
    
    @Override
    public List<String> getApprovedTravelDocumentNumbersByTrip(String travelDocumentIdentifier) {
        return realTravelDocumentService.getApprovedTravelDocumentNumbersByTrip(travelDocumentIdentifier);
    }

    @Override
    public List<String> findMatchingTrips(TravelDocument document) {
        return realTravelDocumentService.findMatchingTrips(document);
    }

}
