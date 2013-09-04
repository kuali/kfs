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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.rice.krad.util.ObjectUtils;

public class ActualExpenseServiceImpl extends ExpenseServiceBase implements TEMExpenseService {

    public static Logger LOG = Logger.getLogger(ActualExpenseServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#calculateDistributionTotals(org.kuali.kfs.module.tem.document.TravelDocument, java.util.Map, java.util.List)
     */
    @Override
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TEMExpense> expenses){

        //calculate the distribution map for all actual expenses
        for (TEMExpense expense : expenses) {

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

                        String key = objCode.getCode() + "-" + document.getExpenseTypeCode();
                        if (distributionMap.containsKey(key)){
                            distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getConvertedAmount()));
                            distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getConvertedAmount()));
                        }
                        else{
                            distribution = new AccountingDistribution();
                            distribution.setObjectCode(objCode.getCode());
                            distribution.setObjectCodeName(objCode.getName());
                            distribution.setCardType(document.getExpenseTypeCode());
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
     * @see org.kuali.kfs.module.tem.service.TEMExpenseService#getExpenseDetails(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public List<? extends TEMExpense> getExpenseDetails(TravelDocument document) {
        return document.getActualExpenses();
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#processExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void processExpense(TravelDocument travelDocument) {
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
