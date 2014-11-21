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
package org.kuali.kfs.module.tem.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TemExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.krad.util.ObjectUtils;

public class ActualExpenseServiceImpl extends ExpenseServiceBase implements TemExpenseService {

    public static Logger LOG = Logger.getLogger(ActualExpenseServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#calculateDistributionTotals(org.kuali.kfs.module.tem.document.TravelDocument, java.util.Map, java.util.List)
     */
    @Override
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TemExpense> expenses){

        //calculate the distribution map for all actual expenses
        for (TemExpense expense : expenses) {

            if (expense.getExpenseDetails() != null && expense.getExpenseDetails().size() > 0){
                //calculate using detail as it might have different details' object code
                calculateDistributionTotals(document, distributionMap, expense.getExpenseDetails());
            }
            else {
                if (!ObjectUtils.isNull(expense.getExpenseTypeObjectCode()) && !expense.getExpenseTypeObjectCode().getExpenseType().isPrepaidExpense() && !expense.getNonReimbursable()) {

                    boolean skipDistribution = false;
                    String financialObjectCode = null;

                    if (document.isTravelAuthorizationDoc()) {
                        //check trip generate encumbrance
                        if (((TravelAuthorizationDocument)document).isTripGenerateEncumbrance()) {
                            financialObjectCode = document.getTripType().getEncumbranceObjCode();
                        }else{
                            //non encumbrance actual expense in TA are informational only - no need to distribute
                            skipDistribution = true;
                        }
                    }else {
                        financialObjectCode = !ObjectUtils.isNull(expense.getExpenseTypeObjectCode()) ? expense.getExpenseTypeObjectCode().getFinancialObjectCode() : null;
                    }

                    final ObjectCode objCode = getObjectCodeService().getByPrimaryIdForCurrentYear(ExpenseUtils.getDefaultChartCode(document), financialObjectCode);
                    if (objCode != null && !skipDistribution) {
                        AccountingDistribution distribution = null;

                        String key = objCode.getCode() + "-" + document.getDefaultCardTypeCode();
                        if (distributionMap.containsKey(key)){
                            distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getConvertedAmount()));
                            distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getConvertedAmount()));
                        }
                        else{
                            distribution = new AccountingDistribution();
                            distribution.setObjectCode(objCode.getCode());
                            distribution.setObjectCodeName(objCode.getName());
                            distribution.setCardType(document.getDefaultCardTypeCode());
                            distribution.setRemainingAmount(expense.getConvertedAmount());
                            distribution.setSubTotal(expense.getConvertedAmount());
                            distributionMap.put(key, distribution);

                            LOG.debug("Subtotal distribution " + distribution.getSubTotal());
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemExpenseService#getExpenseDetails(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public List<? extends TemExpense> getExpenseDetails(TravelDocument document) {
        return document.getActualExpenses();
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#processExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void processExpense(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        //do nothing
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#updateExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void updateExpense(TravelDocument travelDocument) {
      //do nothing
    }

}
