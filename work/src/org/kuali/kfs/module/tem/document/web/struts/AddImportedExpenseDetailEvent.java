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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_IMPORTED_EXPENSE_LINES;

import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddImportedExpenseDetailLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.KualiRuleService;

public class AddImportedExpenseDetailEvent implements Observer {

    public static Logger LOG = Logger.getLogger(AddImportedExpenseDetailEvent.class);

    private static final int WRAPPER_ARG_IDX       = 0;
    private static final int SELECTED_LINE_ARG_IDX = 1;

    @SuppressWarnings("null")
    @Override
    public void update(Observable arg0, Object arg1) {
        if (!(arg1 instanceof Object[])) {
            return;
        }
        final Object[] args = (Object[]) arg1;
        LOG.debug(args[WRAPPER_ARG_IDX]);
        if (!(args[WRAPPER_ARG_IDX] instanceof TravelMvcWrapperBean)) {
            return;
        }
        final TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) args[WRAPPER_ARG_IDX];

        final TravelDocument document = wrapper.getTravelDocument();
        final Integer index = (Integer) args[SELECTED_LINE_ARG_IDX];

        final ImportedExpense newImportedExpenseLine = wrapper.getNewImportedExpenseLines().get(index);

        if(newImportedExpenseLine != null){
            newImportedExpenseLine.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
        }

        ImportedExpense line = document.getImportedExpenses().get(index);
        boolean rulePassed = true;

        // check any business rules
        rulePassed &= getRuleService().applyRules(new AddImportedExpenseDetailLineEvent<ImportedExpense>(NEW_IMPORTED_EXPENSE_LINES + "["+index + "]", document, newImportedExpenseLine));

        if (rulePassed){
            if(newImportedExpenseLine != null && line != null){
                newImportedExpenseLine.setTemExpenseTypeCode(null);
                newImportedExpenseLine.setCardType(line.getCardType());
                document.addExpenseDetail(newImportedExpenseLine, index);
                newImportedExpenseLine.setExpenseDetails(null);
            }

            KualiDecimal detailTotal = line.getTotalDetailExpenseAmount();

            ImportedExpense newExpense = new ImportedExpense();
            try {
                BeanUtils.copyProperties(newExpense, line);
                newExpense.setConvertedAmount(null);
                newExpense.setExpenseParentId(newExpense.getId());
                newExpense.setId(null);
                newExpense.setNotes(null);
            }
            catch (IllegalAccessException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            if (detailTotal.isLessThan(line.getExpenseAmount())){
                KualiDecimal remainderExpense = line.getExpenseAmount().subtract(detailTotal);
                KualiDecimal remainderConverted = line.getConvertedAmount().subtract(detailTotal.multiply(line.getCurrencyRate()));
                newExpense.setExpenseAmount(remainderExpense);
                newExpense.setConvertedAmount(remainderConverted);
            }
            else{
                newExpense.setExpenseAmount(KualiDecimal.ZERO);
            }
            wrapper.getNewImportedExpenseLines().add(index,newExpense);
            wrapper.getNewImportedExpenseLines().remove(index+1);

            wrapper.setDistribution(getAccountingDistributionService().buildDistributionFrom(document));
        }

    }

    /**
     * Gets the travelReimbursementService attribute.
     *
     * @return Returns the travelReimbursementService.
     */
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }


    /**
     * Gets the kualiRulesService attribute.
     *
     * @return Returns the kualiRuleseService.
     */
    protected KualiRuleService getRuleService() {
        return SpringContext.getBean(KualiRuleService.class);
    }

    protected AccountingDistributionService getAccountingDistributionService() {
        return SpringContext.getBean(AccountingDistributionService.class);
    }
}
