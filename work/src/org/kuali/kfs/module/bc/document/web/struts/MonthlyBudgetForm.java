/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.web.struts.form;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;

public class MonthlyBudgetForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MonthlyBudgetForm.class);
    
    private BudgetConstructionMonthly budgetConstructionMonthly;
    private String docTypeName;

    public MonthlyBudgetForm(){
        super();
        setBudgetConstructionMonthly(new BudgetConstructionMonthly());
        getBudgetConstructionMonthly().setDocumentNumber("1234");
        this.setDocTypeName("KualiBudgetConstructionDocument");
    }

    /**
     * Gets the budgetConstructioMonthly attribute. 
     * @return Returns the budgetConstructioMonthly.
     */
    public BudgetConstructionMonthly getBudgetConstructionMonthly() {
        return budgetConstructionMonthly;
    }

    /**
     * Sets the budgetConstructioMonthly attribute value.
     * @param budgetConstructioMonthly The budgetConstructioMonthly to set.
     */
    public void setBudgetConstructionMonthly(BudgetConstructionMonthly budgetConstructionMonthly) {
        this.budgetConstructionMonthly = budgetConstructionMonthly;
    }

    /**
     * Gets the docTypeName attribute. 
     * @return Returns the docTypeName.
     */
    public String getDocTypeName() {
        return docTypeName;
    }

    /**
     * Sets the docTypeName attribute value.
     * @param docTypeName The docTypeName to set.
     */
    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }
    
    
}
