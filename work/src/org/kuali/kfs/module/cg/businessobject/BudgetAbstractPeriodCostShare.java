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
import org.kuali.core.util.KualiInteger;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public abstract class BudgetAbstractPeriodCostShare extends BusinessObjectBase {

    protected String documentHeaderId;
    protected Integer budgetPeriodSequenceNumber;
    protected Integer budgetCostShareSequenceNumber;
    protected KualiInteger budgetCostShareAmount;

    /**
     * Default no-arg constructor.
     */
    public BudgetAbstractPeriodCostShare() {

    }

    /**
     * Gets the documentHeaderId attribute.
     * 
     * @return - Returns the documentHeaderId
     * 
     */
    public String getDocumentHeaderId() {
        return documentHeaderId;
    }

    /**
     * Sets the documentHeaderId attribute.
     * 
     * @param documentHeaderId The documentHeaderId to set.
     * 
     */
    public void setDocumentHeaderId(String documentHeaderId) {
        this.documentHeaderId = documentHeaderId;
    }

    /**
     * Gets the budgetThirdPartyCostShareAmount attribute.
     * 
     * @return - Returns the budgetThirdPartyCostShareAmount
     * 
     */
    public Integer getBudgetPeriodSequenceNumber() {
        return budgetPeriodSequenceNumber;
    }

    /**
     * Sets the budgetThirdPartyCostShareAmount attribute.
     * 
     * @param budgetThirdPartyCostShareAmount The budgetThirdPartyCostShareAmount to set.
     * 
     */
    public void setBudgetPeriodSequenceNumber(Integer budgetPeriodSequenceNumber) {
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
    }

    /**
     * Gets the budgetThirdPartyCostShareAmount attribute.
     * 
     * @return - Returns the budgetThirdPartyCostShareAmount
     * 
     */
    public Integer getBudgetCostShareSequenceNumber() {
        return budgetCostShareSequenceNumber;
    }

    /**
     * Sets the budgetThirdPartyCostShareAmount attribute.
     * 
     * @param budgetThirdPartyCostShareAmount The budgetThirdPartyCostShareAmount to set.
     * 
     */
    public void setBudgetCostShareSequenceNumber(Integer budgetThirdPartyCostShareSequenceNumber) {
        this.budgetCostShareSequenceNumber = budgetThirdPartyCostShareSequenceNumber;
    }

    /**
     * Gets the budgetThirdPartyCostShareAmount attribute.
     * 
     * @return - Returns the budgetThirdPartyCostShareAmount
     * 
     */
    public KualiInteger getBudgetCostShareAmount() {
        return budgetCostShareAmount;
    }

    /**
     * Sets the budgetThirdPartyCostShareAmount attribute.
     * 
     * @param budgetThirdPartyCostShareAmount The budgetThirdPartyCostShareAmount to set.
     * 
     */
    public void setBudgetCostShareAmount(KualiInteger budgetThirdPartyCostShareAmount) {
        this.budgetCostShareAmount = budgetThirdPartyCostShareAmount;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentHeaderId", this.documentHeaderId);
        m.put("budgetPeriodSequenceNumber", this.budgetPeriodSequenceNumber);
        m.put("budgetCostShareSequenceNumber", this.budgetCostShareSequenceNumber);

        return m;
    }
}