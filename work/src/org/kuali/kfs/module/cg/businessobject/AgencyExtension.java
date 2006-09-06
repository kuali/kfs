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
import org.kuali.module.cg.bo.Agency;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AgencyExtension extends BusinessObjectBase {

    private String agencyNumber;
    private boolean agencyModularIndicator;
    private KualiInteger budgetModularIncrementAmount;
    private KualiInteger budgetPeriodMaximumAmount;
    private boolean agencyNsfOutputIndicator;
    private Agency agency;

    /**
     * Default no-arg constructor.
     */
    public AgencyExtension() {

    }

    /**
     * Gets the agencyModularIndicator attribute.
     * 
     * @return - Returns the agencyModularIndicator
     * 
     */
    public boolean isAgencyModularIndicator() {
        return agencyModularIndicator;
    }

    /**
     * Sets the agencyModularIndicator attribute.
     * 
     * @param agencyModularIndicator The agencyModularIndicator to set.
     * 
     */
    public void setAgencyModularIndicator(boolean agencyModularIndicator) {
        this.agencyModularIndicator = agencyModularIndicator;
    }

    /**
     * Gets the budgetModularIncrementAmount attribute.
     * 
     * @return - Returns the budgetModularIncrementAmount
     * 
     */
    public KualiInteger getBudgetModularIncrementAmount() {
        return budgetModularIncrementAmount;
    }

    /**
     * Sets the budgetModularIncrementAmount attribute.
     * 
     * @param budgetModularIncrementAmount The budgetModularIncrementAmount to set.
     * 
     */
    public void setBudgetModularIncrementAmount(KualiInteger budgetModularIncrementAmount) {
        this.budgetModularIncrementAmount = budgetModularIncrementAmount;
    }

    /**
     * Gets the budgetPeriodMaximumAmount attribute.
     * 
     * @return - Returns the budgetPeriodMaximumAmount
     * 
     */
    public KualiInteger getBudgetPeriodMaximumAmount() {
        return budgetPeriodMaximumAmount;
    }

    /**
     * Sets the budgetPeriodMaximumAmount attribute.
     * 
     * @param budgetPeriodMaximumAmount The budgetPeriodMaximumAmount to set.
     * 
     */
    public void setBudgetPeriodMaximumAmount(KualiInteger budgetPeriodMaximumAmount) {
        this.budgetPeriodMaximumAmount = budgetPeriodMaximumAmount;
    }

    /**
     * Gets the agencyNsfOutputIndicator attribute.
     * 
     * @return - Returns the agencyNsfOutputIndicator
     * 
     */
    public boolean isAgencyNsfOutputIndicator() {
        return agencyNsfOutputIndicator;
    }

    /**
     * Sets the agencyNsfOutputIndicator attribute.
     * 
     * @param agencyNsfOutputIndicator The agencyNsfOutputIndicator to set.
     * 
     */
    public void setAgencyNsfOutputIndicator(boolean agencyNsfOutputIndicator) {
        this.agencyNsfOutputIndicator = agencyNsfOutputIndicator;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return - Returns the agencyNumber
     * 
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     * 
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the agency attribute.
     * 
     * @return - Returns the agency
     * 
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute.
     * 
     * @param agency The agency to set.
     * 
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
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
