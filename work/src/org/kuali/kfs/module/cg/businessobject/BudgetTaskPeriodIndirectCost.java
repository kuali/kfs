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
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetTaskPeriodIndirectCost extends BusinessObjectBase implements Comparable {

    private String documentHeaderId; // ER_REF_TRACK_NBR
    private Integer budgetTaskSequenceNumber; // BDGT_TSK_SEQ_NBR
    private Integer budgetPeriodSequenceNumber; // BDGT_PRD_SEQ_NBR
    private KualiDecimal budgetManualIndirectCostRate; // BDGT_MAN_IDC_RT
    private KualiInteger budgetManualMtdcAmount; // BDGT_MAN_MTDC_AMT

    // We will need to remove this, since this is properly storedin the parent IDC object.
    private String budgetPurposeCode; // BDGT_PRPS_CD

    private BudgetTask task; // BudgetTask associated with this taskPeriodLine.
    private BudgetPeriod period; // BudgetPeriod associated with this taskPeriodLine.

    /**
     * For more information on how these values are used, see the impl.
     * 
     * @see org.kuali.module.kra.budget.service.impl.BudgetIndirectCostServiceImpl
     */
    private KualiInteger totalDirectCost; // Calcluated totalDirectCost for this taskPeriod.
    private KualiInteger baseCost; // Calculated baseCost for this taskPeriod.
    private KualiDecimal indirectCostRate; // IDC rate used to calculate calculatedIndirectCost.
    private KualiInteger calculatedIndirectCost; // Calculated indirectCost for this taskPeriod.
    private KualiInteger costShareBaseCost; // Calculated cost share baseCost for this taskPeriod.
    private KualiDecimal costShareIndirectCostRate; // IDC rate used to calculate cost share calculatedIndirectCost.
    private KualiInteger costShareCalculatedIndirectCost; // Calculated cost share indirect cost.
    private KualiInteger costShareUnrecoveredIndirectCost; // Difference between calculated idc.

    /**
     * Default no-arg constructor.
     */
    public BudgetTaskPeriodIndirectCost() {
        super();
        
        // Set our totals to zero by default.
        this.setTotalDirectCost(new KualiInteger(0));
        this.setBaseCost(new KualiInteger(0));
        this.setCalculatedIndirectCost(new KualiInteger(0));

        this.setCostShareBaseCost(new KualiInteger(0));
        this.setCostShareCalculatedIndirectCost(new KualiInteger(0));
        this.setCostShareIndirectCostRate(new KualiDecimal(0));
        this.setCostShareUnrecoveredIndirectCost(new KualiInteger(0));

        this.setBudgetManualIndirectCostRate(new KualiDecimal(0));
        this.setBudgetManualMtdcAmount(new KualiInteger(0));
    }
    
    public BudgetTaskPeriodIndirectCost(BudgetTaskPeriodIndirectCost template) {
        this();
     
        this.documentHeaderId = template.getDocumentHeaderId();
        this.budgetTaskSequenceNumber = template.getBudgetTaskSequenceNumber();
        this.budgetPeriodSequenceNumber = template.getBudgetPeriodSequenceNumber();
        this.budgetManualIndirectCostRate = template.getBudgetManualIndirectCostRate();
        this.budgetManualMtdcAmount = template.getBudgetManualMtdcAmount();

        this.task = new BudgetTask(template.getTask());
        this.period = new BudgetPeriod(template.getPeriod());
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
     * Gets the budgetTaskSequenceNumber attribute.
     * 
     * @return - Returns the budgetTaskSequenceNumber
     * 
     */
    public Integer getBudgetTaskSequenceNumber() {
        return budgetTaskSequenceNumber;
    }

    /**
     * Sets the budgetTaskSequenceNumber attribute.
     * 
     * @param budgetTaskSequenceNumber The budgetTaskSequenceNumber to set.
     * 
     */
    public void setBudgetTaskSequenceNumber(Integer budgetTaskSequenceNumber) {
        this.budgetTaskSequenceNumber = budgetTaskSequenceNumber;
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
     * Gets the budgetManualIndirectCostRate attribute.
     * 
     * @return - Returns the budgetManualIndirectCostRate
     * 
     */
    public KualiDecimal getBudgetManualIndirectCostRate() {
        return budgetManualIndirectCostRate;
    }

    /**
     * Sets the budgetManualIndirectCostRate attribute.
     * 
     * @param budgetManualIndirectCostRate The budgetManualIndirectCostRate to set.
     * 
     */
    public void setBudgetManualIndirectCostRate(KualiDecimal budgetManualIndirectCostRate) {
        this.budgetManualIndirectCostRate = budgetManualIndirectCostRate;
    }

    /**
     * Gets the budgetManualMtdcAmount attribute.
     * 
     * @return - Returns the budgetManualMtdcAmount
     * 
     */
    public KualiInteger getBudgetManualMtdcAmount() {
        return budgetManualMtdcAmount;
    }

    /**
     * Sets the budgetManualMtdcAmount attribute.
     * 
     * @param budgetManualMtdcAmount The budgetManualMtdcAmount to set.
     * 
     */
    public void setBudgetManualMtdcAmount(KualiInteger budgetManualMtdcAmount) {
        this.budgetManualMtdcAmount = budgetManualMtdcAmount;
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        // m.put("<unique identifier 1>", this.<UniqueIdentifier1>());
        // m.put("<unique identifier 2>", this.<UniqueIdentifier2>());

        return m;
    }

    public BudgetPeriod getPeriod() {
        return period;
    }

    public void setPeriod(BudgetPeriod period) {
        this.period = period;
    }

    public BudgetTask getTask() {
        return task;
    }

    public void setTask(BudgetTask task) {
        this.task = task;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        BudgetTaskPeriodIndirectCost taskPeriodLine = (BudgetTaskPeriodIndirectCost) o;
        return new Integer(this.budgetTaskSequenceNumber.toString() + this.budgetPeriodSequenceNumber.toString()).compareTo(new Integer(taskPeriodLine.getBudgetTaskSequenceNumber().toString() + taskPeriodLine.getBudgetPeriodSequenceNumber().toString()));
    }

    /**
     * @return Returns the baseCost.
     */
    public KualiInteger getBaseCost() {
        return baseCost;
    }

    /**
     * @param baseCost The baseCost to set.
     */
    public void setBaseCost(KualiInteger baseCost) {
        this.baseCost = baseCost;
    }

    /**
     * @return Returns the calculatedIndirectCost.
     */
    public KualiInteger getCalculatedIndirectCost() {
        return calculatedIndirectCost;
    }

    /**
     * @param calculatedIndirectCost The calculatedIndirectCost to set.
     */
    public void setCalculatedIndirectCost(KualiInteger calculatedIndirectCost) {
        this.calculatedIndirectCost = calculatedIndirectCost;
    }

    /**
     * @return Returns the totalDirectCost.
     */
    public KualiInteger getTotalDirectCost() {
        return totalDirectCost;
    }

    /**
     * @param totalDirectCost The totalDirectCost to set.
     */
    public void setTotalDirectCost(KualiInteger totalDirectCost) {
        this.totalDirectCost = totalDirectCost;
    }

    /**
     * @return Returns the indirectCostRate.
     */
    public KualiDecimal getIndirectCostRate() {
        return indirectCostRate;
    }

    /**
     * @param indirectCostRate The indirectCostRate to set.
     */
    public void setIndirectCostRate(KualiDecimal indirectCostRate) {
        this.indirectCostRate = indirectCostRate;
    }

    /**
     * @return Returns the costShareBaseCost.
     */
    public KualiInteger getCostShareBaseCost() {
        return costShareBaseCost;
    }

    /**
     * @param costShareBaseCost The costShareBaseCost to set.
     */
    public void setCostShareBaseCost(KualiInteger costShareBaseCost) {
        this.costShareBaseCost = costShareBaseCost;
    }

    /**
     * @return Returns the costShareIndirectCostRate.
     */
    public KualiDecimal getCostShareIndirectCostRate() {
        return costShareIndirectCostRate;
    }

    /**
     * @param costShareIndirectCostRate The costShareIndirectCostRate to set.
     */
    public void setCostShareIndirectCostRate(KualiDecimal costShareIndirectCostRate) {
        this.costShareIndirectCostRate = costShareIndirectCostRate;
    }

    /**
     * @return Returns the costShareUnrecoveredIndirectCost.
     */
    public KualiInteger getCostShareUnrecoveredIndirectCost() {
        return costShareUnrecoveredIndirectCost;
    }

    /**
     * @param costShareUnrecoveredIndirectCost The costShareUnrecoveredIndirectCost to set.
     */
    public void setCostShareUnrecoveredIndirectCost(KualiInteger costShareUnrecoveredIndirectCost) {
        this.costShareUnrecoveredIndirectCost = costShareUnrecoveredIndirectCost;
    }

    /**
     * @return Returns the costShareCalculatedIndirectCost.
     */
    public KualiInteger getCostShareCalculatedIndirectCost() {
        return costShareCalculatedIndirectCost;
    }

    /**
     * @param costShareCalculatedIndirectCost The costShareCalculatedIndirectCost to set.
     */
    public void setCostShareCalculatedIndirectCost(KualiInteger costShareCalculatedIndirectCost) {
        this.costShareCalculatedIndirectCost = costShareCalculatedIndirectCost;
    }
}