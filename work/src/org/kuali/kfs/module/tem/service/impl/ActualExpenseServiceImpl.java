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

public class ActualExpenseServiceImpl implements TEMExpenseService {

    @Override
    public Map<String, AccountingDistribution> getAccountingDistribution(TravelDocument document) {
        final List<AccountingDistribution> retval = new ArrayList<AccountingDistribution>();
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();
    
        // Actual expenses
        if (document.getActualExpenses() != null) {
            calculateDistributionTotals(document, distributionMap, document.getActualExpenses());
        }
        return distributionMap;
    }
    
    private void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List expenses){
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        for (TEMExpense expense : (List<TEMExpense>)expenses) {
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

                        String key = objCode.getCode() + "-" + TemConstants.NOT_APPLICABLE;
                        if (distributionMap.containsKey(key)){
                            distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getConvertedAmount()));
                            distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getConvertedAmount()));
                        }
                        else{
                            distribution = new AccountingDistribution();
                            distribution.setObjectCode(objCode.getCode());
                            distribution.setObjectCodeName(objCode.getName());
                            distribution.setCardType(TemConstants.NOT_APPLICABLE);
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

    @Override
    public String getExpenseType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TEMExpense> getExpenseDetails(TravelDocument document) {       
        return null;
    }

    /**
     * Gets the objectCodeService attribute.
     * 
     * @return Returns the objectCodeService.
     */
    public ObjectCodeService getObjectCodeService() {
        return SpringContext.getBean(ObjectCodeService.class);
    }
    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
    
    @Override
    public KualiDecimal getAllExpenseTotal(TravelDocument document, boolean includeNonReimbursable) {
        KualiDecimal total = KualiDecimal.ZERO;
        if (includeNonReimbursable){
            total = calculateTotals(total, document.getActualExpenses(), TemConstants.ExpenseTypeReimbursementCodes.ALL);
        }
        else{
            total = calculateTotals(total, document.getActualExpenses(), TemConstants.ExpenseTypeReimbursementCodes.REIMBURSABLE);
        }
        
        return total;
    }

    @Override
    public KualiDecimal getNonReimbursableExpenseTotal(TravelDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;

        total = calculateTotals(total, document.getActualExpenses(), TemConstants.ExpenseTypeReimbursementCodes.NON_REIMBURSABLE);
        return total;
    }

    private KualiDecimal calculateTotals(KualiDecimal total, List expenses, String code){
        for (TEMExpense expense : (List<TEMExpense>)expenses){
            if (expense.getExpenseDetails() != null && expense.getExpenseDetails().size() > 0){
                total = total.add(calculateTotals(total, expense.getExpenseDetails(), code));
            }
            else{
                if (code.equals(TemConstants.ExpenseTypeReimbursementCodes.ALL)){
                    total = total.add(expense.getConvertedAmount());
                }
                else if (code.equals(TemConstants.ExpenseTypeReimbursementCodes.NON_REIMBURSABLE)){
                    if ((expense.getTravelExpenseTypeCode() != null && expense.getTravelExpenseTypeCode().isPrepaidExpense()) || expense.getNonReimbursable()) {
                        total = total.add(expense.getExpenseAmount());
                    }
                }
                else if (code.equals(TemConstants.ExpenseTypeReimbursementCodes.REIMBURSABLE)){
                    if ((expense.getTravelExpenseTypeCode() != null && !expense.getTravelExpenseTypeCode().isPrepaidExpense()) && !expense.getNonReimbursable()) {
                        total = total.add(expense.getExpenseAmount());
                    }
                }
            }
        }
        return total;
    }
    
    
    @Override
    public void processExpense(TravelDocument travelDocument) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateExpense(TravelDocument travelDocument) {
        // TODO Auto-generated method stub
        
    }
}
