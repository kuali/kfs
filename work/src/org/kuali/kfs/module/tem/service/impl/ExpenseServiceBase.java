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
package org.kuali.kfs.module.tem.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TemExpenseService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class ExpenseServiceBase implements TemExpenseService {

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#getAccountingDistribution(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public Map<String, AccountingDistribution> getAccountingDistribution(TravelDocument document) {
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();
        calculateDistributionTotals(document, distributionMap, getExpenseDetails(document));
        return distributionMap;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#calculateDistributionTotals(org.kuali.kfs.module.tem.document.TravelDocument, java.util.Map, java.util.List)
     */
    @Override
    abstract public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TemExpense> expenses);

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#getExpenseDetails(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    abstract public List<? extends TemExpense> getExpenseDetails(TravelDocument document);

    public ObjectCodeService getObjectCodeService() {
        return SpringContext.getBean(ObjectCodeService.class);
    }

    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#getAllExpenseTotal(org.kuali.kfs.module.tem.document.TravelDocument, boolean)
     */
    @Override
    public KualiDecimal getAllExpenseTotal(TravelDocument document, boolean includeNonReimbursable) {
        KualiDecimal total = KualiDecimal.ZERO;
        if (includeNonReimbursable){
            total = calculateTotals(total, getExpenseDetails(document), TemConstants.ExpenseTypeReimbursementCodes.ALL);
        }
        else{
            total = calculateTotals(total, getExpenseDetails(document), TemConstants.ExpenseTypeReimbursementCodes.REIMBURSABLE);
        }

        return total;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#getNonReimbursableExpenseTotal(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public KualiDecimal getNonReimbursableExpenseTotal(TravelDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;

        total = calculateTotals(total, getExpenseDetails(document), TemConstants.ExpenseTypeReimbursementCodes.NON_REIMBURSABLE);
        return total;
    }

    /**
     * Calculate total expenses recursively through the expense list and each of its details
     *
     * @param total
     * @param expenses
     * @param code
     * @return
     */
    private KualiDecimal calculateTotals(KualiDecimal total, List<? extends TemExpense> expenses, String code){
        for (TemExpense expense : expenses){
            //allow for custom validation per different expense type
            if (validateExpenseCalculation(expense)){
                //NOTE: CTS Expense did not sum the details, may have been a bug - CLEANUP
                if (expense.getExpenseDetails() != null && expense.getExpenseDetails().size() > 0){
                    total = calculateTotals(total, expense.getExpenseDetails(), code);
                }
                else{
                    if (TemConstants.ExpenseTypeReimbursementCodes.ALL.equals(code)){
                        total = total.add(expense.getConvertedAmount());
                    }
                    else if (TemConstants.ExpenseTypeReimbursementCodes.NON_REIMBURSABLE.equals(code)){
                        if ((expense.getExpenseTypeObjectCode() != null && expense.getExpenseTypeObjectCode().getExpenseType().isPrepaidExpense()) || expense.getNonReimbursable()) {
                            total = total.add(expense.getConvertedAmount());
                        }
                    }
                    else if (TemConstants.ExpenseTypeReimbursementCodes.REIMBURSABLE.equals(code)){
                        if (((!ObjectUtils.isNull(expense.getExpenseTypeObjectCode()) && !expense.getExpenseTypeObjectCode().getExpenseType().isPrepaidExpense()) || ObjectUtils.isNull(expense.getExpenseTypeObjectCode())) && !expense.getNonReimbursable()) {
                            total = total.add(expense.getConvertedAmount());
                        }
                    }
                }
            }
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#validateExpenseCalculation(org.kuali.kfs.module.tem.businessobject.TEMExpense)
     */
    @Override
    public boolean validateExpenseCalculation(TemExpense expense){
        //no validation needed by default
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#processExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    abstract public void processExpense(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#updateExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    abstract public void updateExpense(TravelDocument travelDocument);
}
