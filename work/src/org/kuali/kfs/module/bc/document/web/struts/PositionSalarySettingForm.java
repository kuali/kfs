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

import java.util.Map;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.bo.BudgetConstructionPosition;

/**
 * This class...
 */
public class PositionSalarySettingForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PositionSalarySettingForm.class);

    private BudgetConstructionPosition budgetConstructionPosition; 

    //TODO probably need to push these and some url parms to new superclass BCExpansionForm??
    private boolean hideDetails = false;

    //TODO not sure editingMode is valid here since context is account,subaccount (document)
    //maybe bcdoc needs to have an editingMode map and an ojb ref to bcdoc added in bcaf?
    protected Map editingMode;

    // url parameters sent from BCDoc
    private String returnAnchor;
    private String returnFormKey;

    private Integer universityFiscalYear;
    private String positionNumber;

    //set and pass these when budgetByAccountMode to prefill the add line
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;

    //pass the value of this as a url parm, false setting means budget by organization
    //this also controls where we return the user when done
    private Boolean budgetByAccountMode;

    /**
     * Constructs a PositionSalarySettingForm.java.
     */
    public PositionSalarySettingForm() {
        super();
        setBudgetConstructionPosition(new BudgetConstructionPosition());
    }

    /**
     * Gets the budgetConstructionPosition attribute. 
     * @return Returns the budgetConstructionPosition.
     */
    public BudgetConstructionPosition getBudgetConstructionPosition() {
        return budgetConstructionPosition;
    }

    /**
     * Sets the budgetConstructionPosition attribute value.
     * @param budgetConstructionPosition The budgetConstructionPosition to set.
     */
    public void setBudgetConstructionPosition(BudgetConstructionPosition budgetConstructionPosition) {
        this.budgetConstructionPosition = budgetConstructionPosition;
    }

    /**
     * Gets the positionNumber attribute. 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
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
     * Gets the returnAnchor attribute. 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute. 
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }

    /**
     * Gets the editingMode attribute. 
     * @return Returns the editingMode.
     */
    public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map editingMode) {
        this.editingMode = editingMode;
    }

}
