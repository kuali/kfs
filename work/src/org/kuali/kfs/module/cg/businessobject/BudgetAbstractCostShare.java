/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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