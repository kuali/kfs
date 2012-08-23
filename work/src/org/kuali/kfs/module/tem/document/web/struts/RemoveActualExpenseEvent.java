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

import static org.kuali.kfs.module.tem.util.BufferedLogger.*;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.rice.kns.service.KualiRuleService;

public class RemoveActualExpenseEvent implements Observer {
    private static final int WRAPPER_ARG_IDX       = 0;
    private static final int SELECTED_LINE_ARG_IDX = 1;
    
    private TravelDocumentService travelDocumentService;
    private AccountingDistributionService accountingDistributionService;
    private KualiRuleService ruleService;
    
    @Override
    public void update(Observable arg0, Object arg1) {
        if (!(arg1 instanceof Object[])) {
            return;
        }
        final Object[] args = (Object[]) arg1;
        debug(args[WRAPPER_ARG_IDX]);
        if (!(args[WRAPPER_ARG_IDX] instanceof TravelMvcWrapperBean)) {
            return;
        }
        final TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) args[WRAPPER_ARG_IDX];

        final TravelDocument document = wrapper.getTravelDocument();
        final Integer deleteIndex = (Integer) args[SELECTED_LINE_ARG_IDX];
        
        ActualExpense line = document.getActualExpenses().get(deleteIndex.intValue());
        document.removeExpense(line, deleteIndex);
        wrapper.getNewActualExpenseLines().remove(deleteIndex.intValue());
                
        List<ActualExpense> actualExpenses = wrapper.getNewActualExpenseLines();
        
        wrapper.setDistribution(getAccountingDistributionService().buildDistributionFrom(document));
    }
  
    /**
     * Gets the travelReimbursementService attribute.
     * 
     * @return Returns the travelReimbursementService.
     */
    protected TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }
    
    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    /**
     * Gets the kualiRulesService attribute.
     * 
     * @return Returns the kualiRuleseService.
     */
    protected KualiRuleService getRuleService() {
        return ruleService;
    }
    
    public void setRuleService(final KualiRuleService ruleService) {
        this.ruleService = ruleService;
    }
    
    protected AccountingDistributionService getAccountingDistributionService() {
        if (accountingDistributionService == null) {
            accountingDistributionService = org.kuali.kfs.sys.context.SpringContext.getBean(AccountingDistributionService.class);
        }
        return accountingDistributionService;
    }  
    
    public void setAccountingDistributionService(final AccountingDistributionService accountingDistributionService) {
        this.accountingDistributionService = accountingDistributionService;
    }
}
