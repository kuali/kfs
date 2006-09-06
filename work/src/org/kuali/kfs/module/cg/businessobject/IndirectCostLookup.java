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
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class IndirectCostLookup extends BusinessObjectBase {

    private boolean budgetOnCampusIndicator;
    private String budgetPurposeCode;
    private KualiDecimal budgetIndirectCostRate;
    private BudgetPurposeCode budgetPurpose;

    /**
     * Default no-arg constructor.
     */
    public IndirectCostLookup() {

    }

    /**
     * Gets the budgetOnCampusIndicator attribute.
     * 
     * @return - Returns the budgetOnCampusIndicator
     * 
     */
    public boolean getBudgetOnCampusIndicator() {
        return budgetOnCampusIndicator;
    }

    /**
     * Sets the budgetOnCampusIndicator attribute.
     * 
     * @param budgetOnCampusIndicator The budgetOnCampusIndicator to set.
     * 
     */
    public void setBudgetOnCampusIndicator(boolean budgetOnCampusIndicator) {
        this.budgetOnCampusIndicator = budgetOnCampusIndicator;
    }

    /**
     * Gets the budgetPurposeCode attribute.
     * 
     * @return - Returns the budgetPurposeCode
     * 
     */
    public String getBudgetPurposeCode() {
        return budgetPurposeCode;
    }

    /**
     * Sets the budgetPurposeCode attribute.
     * 
     * @param budgetPurposeCode The budgetPurposeCode to set.
     * 
     */
    public void setBudgetPurposeCode(String budgetPurposeCode) {
        this.budgetPurposeCode = budgetPurposeCode;
    }

    /**
     * Gets the budgetIndirectCostRate attribute.
     * 
     * @return - Returns the budgetIndirectCostRate
     * 
     */
    public KualiDecimal getBudgetIndirectCostRate() {
        return budgetIndirectCostRate;
    }

    /**
     * Sets the budgetIndirectCostRate attribute.
     * 
     * @param budgetIndirectCostRate The budgetIndirectCostRate to set.
     * 
     */
    public void setBudgetIndirectCostRate(KualiDecimal budgetIndirectCostRate) {
        this.budgetIndirectCostRate = budgetIndirectCostRate;
    }
    
    /**
     * Gets the budgetPurpose attribute. 
     * @return Returns the budgetPurpose.
     */
    public BudgetPurposeCode getBudgetPurpose() {
        return budgetPurpose;
    }

    /**
     * Sets the budgetPurpose attribute value.
     * @param budgetPurpose The budgetPurpose to set.
     */
    public void setBudgetPurpose(BudgetPurposeCode budgetPurpose) {
        this.budgetPurpose = budgetPurpose;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        // m.put("<unique identifier 1>", this.<UniqueIdentifier1>());
        // m.put("<unique identifier 2>", this.<UniqueIdentifier2>());

        return m;
    }
}
