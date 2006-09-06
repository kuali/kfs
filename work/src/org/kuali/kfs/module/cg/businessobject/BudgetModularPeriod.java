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
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetModularPeriod extends BusinessObjectBase {

    // Stored values
    private String documentHeaderId;
    private Integer budgetPeriodSequenceNumber;
    private KualiInteger budgetAdjustedModularDirectCostAmount;

    // Derived values
    private KualiInteger actualDirectCostAmount;
    private KualiInteger consortiumAmount;
    private KualiInteger totalPeriodDirectCostAmount;

    /**
     * Default no-arg constructor.
     */
    public BudgetModularPeriod() {
    }

    public BudgetModularPeriod(String documentHeaderId, Integer budgetPeriodSequenceNumber) {
        this.documentHeaderId = documentHeaderId;
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
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
     * Gets the budgetPeriodSequenceNumber attribute.
     * 
     * @return - Returns the budgetPeriodSequenceNumber
     * 
     */
    public Integer getBudgetPeriodSequenceNumber() {
        return budgetPeriodSequenceNumber;
    }

    /**
     * Sets the budgetPeriodSequenceNumber attribute.
     * 
     * @param budgetPeriodSequenceNumber The budgetPeriodSequenceNumber to set.
     * 
     */
    public void setBudgetPeriodSequenceNumber(Integer budgetPeriodSequenceNumber) {
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
    }

    /**
     * Gets the budgetAdjustedModularDirectCostAmount attribute.
     * 
     * @return - Returns the budgetAdjustedModularDirectCostAmount
     * 
     */
    public KualiInteger getBudgetAdjustedModularDirectCostAmount() {
        return budgetAdjustedModularDirectCostAmount;
    }

    /**
     * Sets the budgetAdjustedModularDirectCostAmount attribute.
     * 
     * @param budgetAdjustedModularDirectCostAmount The budgetAdjustedModularDirectCostAmount to set.
     * 
     */
    public void setBudgetAdjustedModularDirectCostAmount(KualiInteger budgetAdjustedModularDirectCostAmount) {
        this.budgetAdjustedModularDirectCostAmount = budgetAdjustedModularDirectCostAmount;
    }

    public void setActualDirectCostAmount(KualiInteger actualDirectCostAmount) {
        this.actualDirectCostAmount = actualDirectCostAmount;
    }

    public KualiInteger getActualDirectCostAmount() {
        return this.actualDirectCostAmount;
    }

    public KualiInteger getConsortiumAmount() {
        return consortiumAmount;
    }

    public void setConsortiumAmount(KualiInteger consortiumAmount) {
        this.consortiumAmount = consortiumAmount;
    }

    public KualiInteger getTotalPeriodDirectCostAmount() {
        return totalPeriodDirectCostAmount;
    }

    public void setTotalPeriodDirectCostAmount(KualiInteger totalPeriodDirectCostAmount) {
        this.totalPeriodDirectCostAmount = totalPeriodDirectCostAmount;
    }

    public KualiInteger getModularVarianceAmount() {
        if (this.getBudgetAdjustedModularDirectCostAmount() != null && this.getActualDirectCostAmount() != null) {
            return this.getBudgetAdjustedModularDirectCostAmount().subtract(this.getActualDirectCostAmount());
        }
        return new KualiInteger(0);
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
