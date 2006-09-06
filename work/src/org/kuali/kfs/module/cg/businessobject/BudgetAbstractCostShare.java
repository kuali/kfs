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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public abstract class BudgetAbstractCostShare extends BusinessObjectBase {

    protected String documentHeaderId;
    protected Integer budgetCostShareSequenceNumber;
    protected String budgetCostShareDescription;
    protected List budgetPeriodCostShare;

    /**
     * Default no-arg constructor.
     */
    public BudgetAbstractCostShare() {
        super();
        budgetPeriodCostShare = new ArrayList();
    }

    /**
     * Populates the key fields for BudgetAbstractPeriodCostShare object. This could be done on object creation, unfortunatly at that time
     * we don't have budgetCostShareSequenceNumber set yet (object is created on page load, while sequence number is set on pressing "add"
     * on the page). Thus we opted for this solution.
     * @param documentHeaderId
     * @param periods
     * @param budgetAbstractCostShare
     */
    public void populateKeyFields(String documentHeaderId, List<BudgetPeriod> periods) {
        this.setDocumentHeaderId(documentHeaderId);

        for (int i = 0; i < periods.size(); i++) {
            BudgetPeriod period = periods.get(i);
            BudgetAbstractPeriodCostShare budgetAbstractPeriodCostShare = this.getBudgetPeriodCostShareItem(i);

            budgetAbstractPeriodCostShare.setDocumentHeaderId(documentHeaderId);
            budgetAbstractPeriodCostShare.setBudgetCostShareSequenceNumber(this.getBudgetCostShareSequenceNumber());
            budgetAbstractPeriodCostShare.setBudgetPeriodSequenceNumber(period.getBudgetPeriodSequenceNumber());
        }
    }
    
    public abstract List getBudgetPeriodCostShare();
    public abstract void setBudgetPeriodCostShare(List budgetPeriodCostShare);
    public abstract BudgetAbstractPeriodCostShare getBudgetPeriodCostShareItem(int index);
    
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
     * Gets the budgetThirdPartyCostShareSequenceNumber attribute.
     * 
     * @return - Returns the budgetThirdPartyCostShareSequenceNumber
     * 
     */
    public Integer getBudgetCostShareSequenceNumber() {
        return budgetCostShareSequenceNumber;
    }

    /**
     * Sets the budgetThirdPartyCostShareSequenceNumber attribute.
     * 
     * @param budgetThirdPartyCostShareSequenceNumber The budgetThirdPartyCostShareSequenceNumber to set.
     * 
     */
    public void setBudgetCostShareSequenceNumber(Integer budgetThirdPartyCostShareSequenceNumber) {
        this.budgetCostShareSequenceNumber = budgetThirdPartyCostShareSequenceNumber;
    }

    /**
     * Gets the budgetThirdPartyCostShareDescription attribute.
     * 
     * @return - Returns the budgetThirdPartyCostShareDescription
     * 
     */
    public String getBudgetCostShareDescription() {
        return budgetCostShareDescription;
    }

    /**
     * Sets the budgetThirdPartyCostShareDescription attribute.
     * 
     * @param budgetThirdPartyCostShareDescription The budgetThirdPartyCostShareDescription to set.
     * 
     */
    public void setBudgetCostShareDescription(String budgetThirdPartyCostShareDescription) {
        this.budgetCostShareDescription = budgetThirdPartyCostShareDescription;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentHeaderId", this.documentHeaderId);
        m.put("budgetCostShareSequenceNumber", this.budgetCostShareSequenceNumber);

        return m;
    }
}