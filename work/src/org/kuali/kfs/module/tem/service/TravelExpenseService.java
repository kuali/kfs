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

import org.kuali.kfs.module.tem.TemConstants.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.rice.kns.util.KualiDecimal;

public interface TravelExpenseService {
    
    /**
     * 
     * This method returns the {@link TemTravelExpenseTypeCode} for the Expense.
     * @param expense
     * @return
     */
    public TemTravelExpenseTypeCode getExpenseType(String expense, String documentType, String tripType, String travelerType);    
    
    /**
     * 
     * This method returns the {@link CreditCardAgency} for the Credit Card or Agency Code.
     * @param code
     * @return
     */
    public CreditCardAgency getCreditCardAgency(String code);
    
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
    public HistoricalTravelExpense createHistoricalTravelExpense(AgencyStagingData agency, CreditCardStagingData creditCard, TemTravelExpenseTypeCode travelExpenseType);

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
    public TEMExpenseService getExpenseServiceByType(ExpenseType expenseType);
    
    public TemTravelExpenseTypeCode getExpenseType(Long travelExpenseTypeCodeId);

    public Long getExpenseTypeId(String travelExpenseCode, String documentNumber);

}
