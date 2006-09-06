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
 * @author mmorgan
 */
public class BudgetIndirectCost extends BusinessObjectBase {

    private String documentHeaderId; // ER_REF_TRACK_NBR

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
     * Default no-arg constructor. TODO Add lookup to APC for budgetPurposeCode and budgetBaseCode instead of hardcoding the
     * defaults.
     */
    public BudgetIndirectCost() {
        super();

        // Set up default values for the IDC object.
        // We do this in case we are entering a budget for the first time.
        this.setBudgetTaskPeriodIndirectCostItems(new ArrayList());
        this.setBudgetIndirectCostCostShareIndicator(false);
        this.setBudgetUnrecoveredIndirectCostIndicator(false);
        this.setBudgetManualMtdcIndicator(false);

        // Default values for budgetPurposeCode, budgetBaseCode and budgetManualRateIndicator should be pulled
        // from the application constants instead of being hard-coded here.
        this.setBudgetPurposeCode("RS");
        this.setBudgetBaseCode("MT");
        this.setBudgetManualRateIndicator("N");

        this.budgetTaskPeriodIndirectCostItems = new ArrayList();
    }

    /**
     * Constructor with documentHeaderId.
     * 
     * @param String documentHeaderId
     */
    public BudgetIndirectCost(String documentHeaderId) {
        this();
        this.setDocumentHeaderId(documentHeaderId);
    }

    /**
     * Constructor to create a new idc object based on passed idc object.
     * 
     * @param BudgetIndirectCost idc
     */
    public BudgetIndirectCost(BudgetIndirectCost idc) {
        // First call default constructor.
        this();

        // TODO this should probably be called in super()
        this.setDocumentHeaderId(idc.getDocumentHeaderId());
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
     * Gets the budgetBaseCode attribute.
     * 
     * @return - Returns the budgetBaseCode
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
     * @return - Returns the budgetIndirectCostCostShareIndicator
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
     * @return - Returns the budgetIndirectCostJustificationText
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
     * @return - Returns the budgetManualMtdcIndicator
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
     * @return - Returns the budgetManualRateIndicator
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
     * Gets the budgetUnrecoveredIndirectCostIndicator attribute.
     * 
     * @return - Returns the budgetUnrecoveredIndirectCostIndicator
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
