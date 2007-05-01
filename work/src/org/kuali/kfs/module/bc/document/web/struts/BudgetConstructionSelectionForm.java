/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.web.struts.form;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class...
 */
public class BudgetConstructionSelectionForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSelectionForm.class);

    private BudgetConstructionHeader budgetConstructionHeader;
    private boolean hideDetails = false;

    //holds the BC fiscal year that is currently active 
    private Integer universityFiscalYear;

    public BudgetConstructionSelectionForm(){
        super();
        setBudgetConstructionHeader(new BudgetConstructionHeader());
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);
        
        final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] {"chartOfAccounts", "account", "subAccount", "budgetConstructionAccountReports"}));
        KNSServiceLocator.getPersistenceService().retrieveReferenceObjects(this.getBudgetConstructionHeader(), REFRESH_FIELDS);
        
    }

    /**
     * Gets the budgetConstructionHeader attribute. 
     * @return Returns the budgetConstructionHeader.
     */
    public BudgetConstructionHeader getBudgetConstructionHeader() {
        return budgetConstructionHeader;
    }

    /**
     * Sets the budgetConstructionHeader attribute value.
     * @param budgetConstructionHeader The budgetConstructionHeader to set.
     */
    public void setBudgetConstructionHeader(BudgetConstructionHeader budgetConstructionHeader) {
        this.budgetConstructionHeader = budgetConstructionHeader;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the hideDetails attribute. 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * Sets the hideDetails attribute value.
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

}
