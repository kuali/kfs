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
package org.kuali.kfs.module.tem.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface TemExpenseService {

    public Map<String, AccountingDistribution> getAccountingDistribution(TravelDocument document);

    /**
     * Calculate distribution total
     *
     * @param document
     * @param distributionMap
     * @param expenses
     */
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TemExpense> expenses);

    /**
     * Get the appropriate expense detail from the document
     *
     * @param document
     * @return
     */
    public List<? extends TemExpense> getExpenseDetails(TravelDocument document);

    /**
     * Validation for expense calculation
     *
     * @param expenses
     */
    public boolean validateExpenseCalculation(TemExpense expense);

    /**
     *
     * @param document
     * @param includeNonReimbursable
     * @return
     */
    public KualiDecimal getAllExpenseTotal(TravelDocument document, boolean includeNonReimbursable);

    /**
     *
     * @param document
     * @return
     */
    public KualiDecimal getNonReimbursableExpenseTotal(TravelDocument document);

    /**
     *
     * @param travelDocument
     */
    public void processExpense(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * Processes expense when it has been finally claimed on a document
     * @param travelDocument
     */
    public void updateExpense(TravelDocument travelDocument);
}
