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
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraConstants;

/**
 * 
 */
public class BudgetIndirectCost extends BusinessObjectBase {

    private String documentNumber;

    /**
     * This is the top left of the IDC parameters form.
     */
    private String budgetPurposeCode; // BDGT_PRPS_CD
    private String budgetBaseCode; // BDGT_BASE_CD
    private String budgetManualRateIndicator; // BDGT_MAN_RT_IND

    /**
     * This is the top right of the IDC parameters form.
     */
    private boolean budgetIndirectCostCostShareIndicator; // BDGT_IDC_CST_SHR_IND
    private boolean budgetUnrecoveredIndirectCostIndicator; // BDGT_URCV_IDC_IND
    private String budgetIndirectCostJustificationText; // BDGT_IDC_JSTF_TXT

    /**
     * This is ???? in the IDC parameters form.
     */
    private boolean budgetManualMtdcIndicator; // BDGT_MAN_MTDC_IND


    private List budgetTaskPeriodIndirectCostItems;

    /**
     * Default no-arg constructor.
     */
    public BudgetIndirectCost() {
        super();

        // Set up default values for the IDC object.
        // We do this in case we are entering a budget for the first time.
        this.setBudgetTaskPeriodIndirectCostItems(new ArrayList());
        this.setBudgetIndirectCostCostShareIndicator(false);
        this.setBudgetUnrecoveredIndirectCostIndicator(false);
        this.setBudgetManualMtdcIndicator(false);

        KualiConfigurationService configurationService = SpringServiceLocator.getKualiConfigurationService();
        this.setBudgetPurposeCode(configurationService.getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.BUDGET_PURPOSE_CODE_DEFAULT_VALUE_PARAMETER_NAME));
        this.setBudgetBaseCode(configurationService.getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.BUDGET_BASE_CODE_DEFAULT_VALUE_PARAMETER_NAME));
        this.setBudgetManualRateIndicator(configurationService.getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.BUDGET_MANUAL_RATE_INDICATOR_DEFAULT_VALUE_PARAMETER_NAME));

        this.budgetTaskPeriodIndirectCostItems = new ArrayList();
    }

    /**
     * Constructor with documentNumber.
     * 
     * @param String documentNumber
     */
    public BudgetIndirectCost(String documentNumber) {
        this();
        this.setDocumentNumber(documentNumber);
    }

    /**
     * Constructor to create a new idc object based on passed idc object.
     * 
     * @param BudgetIndirectCost idc
     */
    public BudgetIndirectCost(BudgetIndirectCost idc) {
        // First call default constructor.
        this();

        this.setDocumentNumber(idc.getDocumentNumber());
        this.setVersionNumber(idc.getVersionNumber());
        this.setObjectId(idc.getObjectId());

        this.setBudgetPurposeCode(idc.getBudgetPurposeCode());
        this.setBudgetBaseCode(idc.getBudgetBaseCode());
        this.setBudgetManualRateIndicator(idc.getBudgetManualRateIndicator());
        this.setBudgetIndirectCostCostShareIndicator(idc.getBudgetIndirectCostCostShareIndicator());
        this.setBudgetUnrecoveredIndirectCostIndicator(idc.getBudgetUnrecoveredIndirectCostIndicator());
        this.setBudgetIndirectCostJustificationText(idc.getBudgetIndirectCostJustificationText());
        this.setBudgetManualMtdcIndicator(idc.getBudgetManualMtdcIndicator());
        this.setBudgetTaskPeriodIndirectCostItems(idc.getBudgetTaskPeriodIndirectCostItems());
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the budgetBaseCode attribute.
     * 
     * @return Returns the budgetBaseCode
     * 
     */
    public String getBudgetBaseCode() {
        return budgetBaseCode;
    }

    /**
     * Sets the budgetBaseCode attribute.
     * 
     * @param budgetBaseCode The budgetBaseCode to set.
     * 
     */
    public void setBudgetBaseCode(String budgetBaseCode) {
        this.budgetBaseCode = budgetBaseCode;
    }

    /**
     * Gets the budgetIndirectCostCostShareIndicator attribute.
     * 
     * @return Returns the budgetIndirectCostCostShareIndicator
     * 
     */
    public boolean getBudgetIndirectCostCostShareIndicator() {
        return budgetIndirectCostCostShareIndicator;
    }

    /**
     * Sets the budgetIndirectCostCostShareIndicator attribute.
     * 
     * @param budgetIndirectCostCostShareIndicator The budgetIndirectCostCostShareIndicator to set.
     * 
     */
    public void setBudgetIndirectCostCostShareIndicator(boolean budgetIndirectCostCostShareIndicator) {
        this.budgetIndirectCostCostShareIndicator = budgetIndirectCostCostShareIndicator;
    }

    /**
     * Get boolean value of idc cost share indicator.
     */
    public boolean isBudgetIndirectCostCostShareIndicator() {
        return this.budgetIndirectCostCostShareIndicator;
    }

    /**
     * Gets the budgetIndirectCostJustificationText attribute.
     * 
     * @return Returns the budgetIndirectCostJustificationText
     * 
     */
    public String getBudgetIndirectCostJustificationText() {
        return budgetIndirectCostJustificationText;
    }

    /**
     * Sets the budgetIndirectCostJustificationText attribute.
     * 
     * @param budgetIndirectCostJustificationText The budgetIndirectCostJustificationText to set.
     * 
     */
    public void setBudgetIndirectCostJustificationText(String budgetIndirectCostJustificationText) {
        this.budgetIndirectCostJustificationText = budgetIndirectCostJustificationText;
    }

    /**
     * Gets the budgetManualMtdcIndicator attribute.
     * 
     * @return Returns the budgetManualMtdcIndicator
     * 
     */
    public boolean getBudgetManualMtdcIndicator() {
        return budgetManualMtdcIndicator;
    }

    /**
     * Sets the budgetManualMtdcIndicator attribute.
     * 
     * @param budgetManualMtdcIndicator The budgetManualMtdcIndicator to set.
     * 
     */
    public void setBudgetManualMtdcIndicator(boolean budgetManualMtdcIndicator) {
        this.budgetManualMtdcIndicator = budgetManualMtdcIndicator;
    }

    /**
     * Gets the budgetManualRateIndicator attribute.
     * 
     * @return Returns the budgetManualRateIndicator
     * 
     */
    public String getBudgetManualRateIndicator() {
        return budgetManualRateIndicator;
    }

    /**
     * Sets the budgetManualRateIndicator attribute.
     * 
     * @param budgetManualRateIndicator The budgetManualRateIndicator to set.
     * 
     */
    public void setBudgetManualRateIndicator(String budgetManualRateIndicator) {
        this.budgetManualRateIndicator = budgetManualRateIndicator;
    }

    /**
     * Gets the budgetPurposeCode attribute.
     * 
     * @return Returns the budgetPurposeCode
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
     * Gets the budgetUnrecoveredIndirectCostIndicator attribute.
     * 
     * @return Returns the budgetUnrecoveredIndirectCostIndicator
     * 
     */
    public boolean getBudgetUnrecoveredIndirectCostIndicator() {
        return budgetUnrecoveredIndirectCostIndicator;
    }

    /**
     * Sets the budgetUnrecoveredIndirectCostIndicator attribute.
     * 
     * @param budgetUnrecoveredIndirectCostIndicator The budgetUnrecoveredIndirectCostIndicator to set.
     * 
     */
    public void setBudgetUnrecoveredIndirectCostIndicator(boolean budgetUnrecoveredIndirectCostIndicator) {
        this.budgetUnrecoveredIndirectCostIndicator = budgetUnrecoveredIndirectCostIndicator;
    }

    /**
     * Get boolean value of idc cost share indicator.
     */
    public boolean isBudgetUnrecoveredIndirectCostIndicator() {
        return this.budgetUnrecoveredIndirectCostIndicator;
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

    /**
     * Gets task/period IDC.
     * 
     * @return List
     */
    public List<BudgetTaskPeriodIndirectCost> getBudgetTaskPeriodIndirectCostItems() {
        return budgetTaskPeriodIndirectCostItems;
    }

    /**
     * Sets the task/period IDC list.
     * 
     * @param budgetTaskPeriodIndirectCostItems
     */
    public void setBudgetTaskPeriodIndirectCostItems(List budgetTaskPeriodIndirectCostItems) {
        this.budgetTaskPeriodIndirectCostItems = budgetTaskPeriodIndirectCostItems;
    }


    /**
     * Retreive a particular taskPeriod.
     * 
     * @param index
     * @return
     */
    public BudgetTaskPeriodIndirectCost getBudgetTaskPeriodIndirectCostItem(int index) {
        while (getBudgetTaskPeriodIndirectCostItems().size() <= index) {
            getBudgetTaskPeriodIndirectCostItems().add(new BudgetTaskPeriodIndirectCost());
        }
        return (BudgetTaskPeriodIndirectCost) getBudgetTaskPeriodIndirectCostItems().get(index);
    }
}
