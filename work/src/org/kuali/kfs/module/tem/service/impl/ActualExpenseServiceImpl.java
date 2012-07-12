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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DEFAULT_CHART_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.AbstractExpense;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;

public class ActualExpenseServiceImpl extends ExpenseServiceBase implements TEMExpenseService {
    
    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#calculateDistributionTotals(org.kuali.kfs.module.tem.document.TravelDocument, java.util.Map, java.util.List)
     */
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TEMExpense> expenses){
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        for (TEMExpense expense : expenses) {
            if (expense.getExpenseDetails() != null && expense.getExpenseDetails().size() > 0){
                calculateDistributionTotals(document, distributionMap, expense.getExpenseDetails());
            }
            else {
                if (expense.getTravelExpenseTypeCodeId() != null
                        && !expense.getTravelExpenseTypeCode().isPrepaidExpense() && !expense.getNonReimbursable()) {

                    TemTravelExpenseTypeCode code = SpringContext.getBean(TravelExpenseService.class).getExpenseType(expense.getTravelExpenseTypeCodeId());
                    expense.setTravelExpenseTypeCode(code);
                    
                    String financialObjectCode = null;
                    
                    if (document instanceof TravelAuthorizationDocument) {
                        if (document.getTripType() != null) {
                            financialObjectCode = document.getTripType().getEncumbranceObjCode();
                        }
                    }
                    else {
                        financialObjectCode = expense.getTravelExpenseTypeCode() != null ? expense.getTravelExpenseTypeCode().getFinancialObjectCode() : code != null ? code.getFinancialObjectCode() : null;
                    }

                    final ObjectCode objCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, financialObjectCode);
                    if (objCode != null) {                    
                        AccountingDistribution distribution = null;

                        String key = objCode.getCode() + "-" + TemConstants.ACTUAL_EXPENSE;
                        if (distributionMap.containsKey(key)){
                            distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getConvertedAmount()));
                            distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getConvertedAmount()));
                        }
                        else{
                            distribution = new AccountingDistribution();
                            distribution.setObjectCode(objCode.getCode());
                            distribution.setObjectCodeName(objCode.getName());
                            distribution.setCardType(TemConstants.ACTUAL_EXPENSE);
                            distribution.setRemainingAmount(expense.getConvertedAmount());
                            distribution.setSubTotal(expense.getConvertedAmount());
                            distributionMap.put(key, distribution);

                            debug("Subtotal distribution ", distribution.getSubTotal());
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
