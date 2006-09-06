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
public class BudgetThirdPartyCostShare extends BudgetAbstractCostShare {

    private String budgetCostShareSourceName;

    /**
     * Default no-arg constructor.
     */
    public BudgetThirdPartyCostShare() {
        super();
    }

    public BudgetThirdPartyCostShare(BudgetThirdPartyCostShare budgetThirdPartyCostShare) {
        this.documentHeaderId = budgetThirdPartyCostShare.getDocumentHeaderId();
        this.budgetCostShareSequenceNumber = budgetThirdPartyCostShare.getBudgetCostShareSequenceNumber();
        this.budgetCostShareDescription = budgetThirdPartyCostShare.getBudgetCostShareDescription();
        this.budgetPeriodCostShare = budgetThirdPartyCostShare.getBudgetPeriodCostShare();
    }
    
    /**
     * Gets the budgetThirdPartyCostShareSourceName attribute.
     * 
     * @return - Returns the budgetThirdPartyCostShareSourceName
     * 
     */
    public String getBudgetCostShareSourceName() {
        return budgetCostShareSourceName;
    }

    /**
     * Sets the budgetThirdPartyCostShareSourceName attribute.
     * 
     * @param budgetThirdPartyCostShareSourceName The budgetThirdPartyCostShareSourceName to set.
     * 
     */
    public void setBudgetCostShareSourceName(String budgetThirdPartyCostShareSourceName) {
        this.budgetCostShareSourceName = budgetThirdPartyCostShareSourceName;
    }

    /**
     * Gets the budgetPeriodThirdPartyCostShare attribute.
     * 
     * @return - Returns the budgetPeriodThirdPartyCostShare
     * 
     */
    public List<BudgetPeriodThirdPartyCostShare> getBudgetPeriodCostShare() {
        return budgetPeriodCostShare;
    }

    public BudgetPeriodThirdPartyCostShare getBudgetPeriodCostShareItem(int index) {
        while (getBudgetPeriodCostShare().size() <= index) {
            getBudgetPeriodCostShare().add(new BudgetPeriodThirdPartyCostShare());
        }
        return (BudgetPeriodThirdPartyCostShare) getBudgetPeriodCostShare().get(index);
    }

    /**
     * Sets the budgetPeriodThirdPartyCostShare attribute.
     * 
     * @param budgetPeriodThirdPartyCostShare The budgetPeriodThirdPartyCostShare to set.
     * 
     */
    public void setBudgetPeriodCostShare(List budgetPeriodCostShare) {
        this.budgetPeriodCostShare = budgetPeriodCostShare;
    }
}