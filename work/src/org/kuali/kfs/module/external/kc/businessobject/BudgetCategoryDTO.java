/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.businessobject;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.kc.BudgetCategory;
import org.kuali.rice.kns.bo.BusinessObjectBase;

public class BudgetCategoryDTO extends BusinessObjectBase implements BudgetCategory, Serializable {

    private String budgetCategoryCode;
    private String budgetCategoryTypeCode;
    private String description;
    
    public String getBudgetCategoryCode() {
        return budgetCategoryCode;
    }
    public void setBudgetCategoryCode(String budgetCategoryCode) {
        this.budgetCategoryCode = budgetCategoryCode;
    }
        
    public String getBudgetCategoryTypeCode() {
        return budgetCategoryTypeCode;
    }
    
    public void setBudgetCategoryTypeCode(String budgetCategoryTypeCode) {
        this.budgetCategoryTypeCode = budgetCategoryTypeCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void prepareForWorkflow() {
        // TODO Auto-generated method stub       
    }

    public void refresh() {
        // TODO Auto-generated method stub        
    }

    protected LinkedHashMap<String,String> toStringMapper() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();
        m.put("BudgetCategoryDTO", this.budgetCategoryCode);
        return m;
    }
    
}
