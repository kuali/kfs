/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.kra.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */

public class NonpersonnelObjectCode extends PersistableBusinessObjectBase implements Comparable {
    private static final long serialVersionUID = -5150973847161350622L;

    /**
     * Default no-arg constructor.
     */
    public NonpersonnelObjectCode() {
        super();
    }

    public NonpersonnelObjectCode(String nonpersonnelCategoryCode, String nonpersonnelSubCategoryCode) {
        super();
        this.budgetNonpersonnelCategoryCode = nonpersonnelCategoryCode;
        this.budgetNonpersonnelSubCategoryCode = nonpersonnelSubCategoryCode;
        this.refresh();
    }


    private String budgetNonpersonnelCategoryCode;
    private String budgetNonpersonnelSubCategoryCode;
    private boolean active;
    private String budgetNonpersonnelObjectCode;
    private NonpersonnelCategory nonpersonnelCategory;
    private NonpersonnelSubCategory nonpersonnelSubCategory;

    /**
     * @param o
     */
    public void setBudgetNonpersonnelCategoryCode(String o) {
        budgetNonpersonnelCategoryCode = o;
    }

    /**
     * @return budgetNonpersonnelCategoryCode
     */
    public String getBudgetNonpersonnelCategoryCode() {
        return budgetNonpersonnelCategoryCode;
    }

    /**
     * @param o
     */
    public void setBudgetNonpersonnelSubCategoryCode(String o) {
        budgetNonpersonnelSubCategoryCode = o;
    }

    /**
     * @return budgetNonpersonnelSubCategoryCode
     */
    public String getBudgetNonpersonnelSubCategoryCode() {
        return budgetNonpersonnelSubCategoryCode;
    }

    /**
     * @param o
     */
    public void setActive(boolean o) {
        active = o;
    }

    /**
     * @return active
     */
    public boolean getActive() {
        return active;
    }

    /**
     * @param o
     */
    public void setBudgetNonpersonnelObjectCode(String o) {
        budgetNonpersonnelObjectCode = o;
    }

    /**
     * @return budgetNonpersonnelObjectCode
     */
    public String getBudgetNonpersonnelObjectCode() {
        return budgetNonpersonnelObjectCode;
    }

    /**
     * @return Returns the nonpersonelCategory.
     */
    public NonpersonnelCategory getNonpersonnelCategory() {
        return nonpersonnelCategory;
    }

    /**
     * @param nonpersonelCategory The nonpersonelCategory to set.
     */
    public void setNonpersonnelCategory(NonpersonnelCategory nonpersonnelCategory) {
        this.nonpersonnelCategory = nonpersonnelCategory;
    }

    /**
     * @return Returns the nonpersonnelSubCategory.
     */
    public NonpersonnelSubCategory getNonpersonnelSubCategory() {
        return nonpersonnelSubCategory;
    }

    /**
     * @param nonpersonnelSubCategory The nonpersonnelSubCategory to set.
     */
    public void setNonpersonnelSubCategory(NonpersonnelSubCategory nonpersonnelSubCategory) {
        this.nonpersonnelSubCategory = nonpersonnelSubCategory;
    }

    public int compareTo(Object o) {
        return this.getNonpersonnelSubCategory().compareTo(((NonpersonnelObjectCode) o).getNonpersonnelSubCategory());
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("budgetNonpersonnelCategoryCode", this.budgetNonpersonnelCategoryCode);
        m.put("budgetNonpersonnelSubCategoryCode", this.budgetNonpersonnelSubCategoryCode);
        return m;
    }
}
