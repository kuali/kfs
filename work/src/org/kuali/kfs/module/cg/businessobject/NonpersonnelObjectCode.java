package org.kuali.module.kra.budget.bo;

/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu) NOT AUTO GENERATED (should go back and generate later)
 */

public class NonpersonnelObjectCode extends BusinessObjectBase implements Comparable {
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
