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
package org.kuali.kfs.module.tem.service;

import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.OtherExpense;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

public interface TravelExpenseService {

    /**
     *
     * This method returns the {@link ExpenseTypeObjectCode} for the Expense.
     * @param expense
     * @return
     */
    public ExpenseTypeObjectCode getExpenseType(String expense, String documentType, String tripType, String travelerType);

    /**
     * Looks up the ExpenseTypeObjectCode by the expense type and a document (which supplies document type, traveler type, and trip type)
     * @param travelExpenseCode the expense type code of the ExpenseTypeObjectCode to look up
     * @param documentNumber the document number of the document to find the ExpenseTypeObjectCode for
     * @return the most matching ExpenseTypeObjectCode
     */
    public ExpenseTypeObjectCode getExpenseTypeObjectCode(String travelExpenseCode, String documentNumber);

    /**
     * Finds a list of ExpenseType records which can be used on a given document type
     * @param documentTypeName the name of the document type to find ExpenseType records for
     * @param tripType the trip type to find expense types for; if null or empty, trip type will not be consulted into which expense types to pull back
     * @param travelerType the traveler type to find expense types for; if null or empty, the traveler type will not be consulted into which expense types to pull back
     * @param groupOnly if true, only group expense types will be returned
     * @return a List, sorted in alphabetical order, of ExpenseType records
     */
    public List<ExpenseType> getExpenseTypesForDocument(String documentTypeName, String tripType, String travelerType, boolean groupOnly);

    /**
     *
     * This method creates a new {@link HistoricalTravelExpense} from the {@link AgencyStagingData}.
     * @param agency
     * @return
     */
    public HistoricalTravelExpense createHistoricalTravelExpense(AgencyStagingData agency);

    /**
     *
     * This method creates a new {@link HistoricalTravelExpense} from the {@link CreditCardStagingData}.
     * @param creditCard
     * @return
     */
    public HistoricalTravelExpense createHistoricalTravelExpense(CreditCardStagingData creditCard);

    /**
     *
     * This method creates a new {@link HistoricalTravelExpense} from the {@link AgencyStagingData} and {@link CreditCardStagingData}.
     * @param agency
     * @param creditCard
     * @param travelExpenseType
     * @return
     */
    public HistoricalTravelExpense createHistoricalTravelExpense(AgencyStagingData agency, CreditCardStagingData creditCard, ExpenseTypeObjectCode travelExpenseType);

    /**
     *
     * This method returns all {@link AgencyStagingData} that are valid and have not already been moved to the historical table.
     * @return List<AgencyStagingData>
     */
    public List<AgencyStagingData> retrieveValidAgencyData();

    /**
     *
     * This method returns all {@link AgencyStagingData} that are valid and have not already been moved to the historical table, based on the import type.
     * @param importBy
     * @return
     */
    public List<AgencyStagingData> retrieveValidAgencyDataByImportType(String importBy);

    /**
     *
     * This method returns all {@link CreditCardStagingData} that are valid and have not already been moved to the historical table.
     * @return List<CreditCardStagingData>
     */
    public List<CreditCardStagingData> retrieveValidCreditCardData();

    public CreditCardStagingData findImportedCreditCardExpense(KualiDecimal amount, String itineraryNumber);

    public CreditCardStagingData findImportedCreditCardExpense(KualiDecimal amount, String ticketNumber, String serviceFeeNumber);

    /**
     * Return the ExpenseService base on expenseType
     *
     * @param expenseType
     * @return
     */
    public TemExpenseService getExpenseServiceByType(TemConstants.ExpenseType expenseType);

    /**
     * Check the expense amount against the travel expense threshold value (if provided)
     *
     * @param expense
     * @return
     */
    public boolean isTravelExpenseExceedReceiptRequirementThreshold(OtherExpense expense);

    /**
     * Sets the taxabile indicator of the given actual expense based on the taxability of the expense type object code and the ability of the given user to
     * @param actualExpense the actual expense to update
     * @param document the travel document the actual expense is associated with
     * @param currentUser the KIM Person of the user who is updating this actual expense
     */
    public void updateTaxabilityOfActualExpense(ActualExpense actualExpense, TravelDocument document, Person currentUser);

    /**
     * Looks up the default expense type associated with the given category
     * @param category the expense type category to find the default for
     * @return the default expense type, or null if one cannot be found
     */
    public ExpenseType getDefaultExpenseTypeForCategory(TemConstants.ExpenseTypeMetaCategory category);

    /**
     * @return true if all values in the TripAccountingInformation record are empty, false otherwise
     */
    public boolean isTripAccountingInformationEmpty(TripAccountingInformation accountingInformation);
}
