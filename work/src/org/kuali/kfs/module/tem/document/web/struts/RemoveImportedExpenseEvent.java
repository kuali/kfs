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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ObjectUtils;

public class RemoveImportedExpenseEvent implements Observer {
    
    public static Logger LOG = Logger.getLogger(RemoveImportedExpenseEvent.class);
    
    private static final int WRAPPER_ARG_IDX       = 0;
    private static final int SELECTED_LINE_ARG_IDX = 1;
    
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
        final Integer deleteIndex = (Integer) args[SELECTED_LINE_ARG_IDX];
        
        ImportedExpense line = document.getImportedExpenses().get(deleteIndex);
        document.removeExpense(line, deleteIndex);
        
        //Unassign historical expense.
        ExpenseUtils.assignExpense(line.getHistoricalTravelExpenseId(), null,null,null, false);
        List<ImportedExpense> importedExpenses = wrapper.getNewImportedExpenseLines();
        
        
        if (wrapper.getNewImportedExpenseLines().size() > deleteIndex.intValue()){
            wrapper.getNewImportedExpenseLines().remove(deleteIndex.intValue());
        }
        //wrapper.getNewImportedExpenseLines().add(new ImportedExpense());
        wrapper.setDistribution(getAccountingDistributionService().buildDistributionFrom(document));

    }

    private int getNextDetailIndex(List<ImportedExpense> importedExpenses, Long id){
        int index = 0;
        for(ImportedExpense detailLine: importedExpenses){            
            if(ObjectUtils.isNotNull(detailLine.getExpenseParentId()) && detailLine.getExpenseParentId().equals(id)){
                return index; 
            }
            index++;
        }
        return -1;
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
