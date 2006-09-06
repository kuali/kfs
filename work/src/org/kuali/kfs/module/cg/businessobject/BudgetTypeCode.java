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

package org.kuali.module.kra.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetTypeCode extends BusinessObjectBase {

    private String budgetTypeCode;
    private String budgetTypeDescription;

    /**
     * Default no-arg constructor.
     */
    public BudgetTypeCode() {

    }

    /**
     * Gets the budgetTypeCode attribute.
     * 
     * @return - Returns the budgetTypeCode
     * 
     */
    public String getBudgetTypeCode() {
        return budgetTypeCode;
    }

    /**
     * Sets the budgetTypeCode attribute.
     * 
     * @param budgetTypeCode The budgetTypeCode to set.
     * 
     */
    public void setBudgetTypeCode(String budgetTypeCode) {
        this.budgetTypeCode = budgetTypeCode;
    }

    /**
     * Gets the budgetTypeDescription attribute.
     * 
     * @return - Returns the budgetTypeDescription
     * 
     */
    public String getBudgetTypeDescription() {
        return budgetTypeDescription;
    }

    /**
     * Sets the budgetTypeDescription attribute.
     * 
     * @param budgetTypeDescription The budgetTypeDescription to set.
     * 
     */
    public void setBudgetTypeDescription(String budgetTypeDescription) {
        this.budgetTypeDescription = budgetTypeDescription;
    }

    /**
     * @see org.kuali.bo.BusinessObjectType#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        // m.put("<unique identifier 1>", this.<UniqueIdentifier1>());
        // m.put("<unique identifier 2>", this.<UniqueIdentifier2>());

        return m;
    }
}
