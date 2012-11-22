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

import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_ACTUAL_EXPENSE_LINE;

import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.beanutils.BeanUtils;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;


public class AddActualExpenseEvent implements Observer {

    @Override
    public void update(Observable arg0, Object arg1) {
        if (!(arg1 instanceof TravelMvcWrapperBean)) {
            return;
        }
        final TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) arg1;

        final TravelDocument document = wrapper.getTravelDocument();
        final ActualExpense newActualExpenseLine = wrapper.getNewActualExpenseLine();
        
        if(newActualExpenseLine != null){
            newActualExpenseLine.refreshReferenceObject("travelExpenseTypeCode");
        }
        
        boolean rulePassed = true;

        // check any business rules
        rulePassed &= getRuleService().applyRules(new AddActualExpenseLineEvent<ActualExpense>(NEW_ACTUAL_EXPENSE_LINE, document, newActualExpenseLine));
        
        if (rulePassed){
            if(newActualExpenseLine != null){
                document.addExpense(newActualExpenseLine);                
            }
            
            ActualExpense newExpense = new ActualExpense();
            try {
                BeanUtils.copyProperties(newExpense, newActualExpenseLine);
                newExpense.setConvertedAmount(null);
                newExpense.setExpenseParentId(newExpense.getId());
                newExpense.setId(null);
                newExpense.setNotes(null);
                newExpense.setTemExpenseTypeCode(null);
            }
            catch (IllegalAccessException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
                        
            wrapper.setNewActualExpenseLine(new ActualExpense());
            wrapper.getNewActualExpenseLines().add(newExpense);
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

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
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
