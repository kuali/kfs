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

package org.kuali.module.budget.bo;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.TypedArrayList;

/**
 * TODO is this needed??? probably need to just point OJB repository to PBGL class or this should extend PBGL if something extra is
 * needed
 */
public class SalarySettingExpansion extends PendingBudgetConstructionGeneralLedger {

    // Total Fields - First Total Line
    private KualiInteger csfAmountTotal;
    private BigDecimal csfFullTimeEmploymentQuantityTotal;
    private KualiInteger appointmentRequestedAmountTotal;
    private BigDecimal appointmentRequestedFteQuantityTotal;
    private KualiDecimal percentChangeTotal;

    private List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding;

    /**
     * Default constructor.
     */
    public SalarySettingExpansion() {
        super();
        zeroTotals();
        pendingBudgetConstructionAppointmentFunding = new TypedArrayList(PendingBudgetConstructionAppointmentFunding.class);
    }

    /**
     * Zeros the totals appearing on the Salary Setting Screen
     */
    public void zeroTotals() {

        csfAmountTotal = new KualiInteger(0);
        csfFullTimeEmploymentQuantityTotal = new BigDecimal(0).setScale(5, KualiDecimal.ROUND_BEHAVIOR);
        appointmentRequestedAmountTotal = new KualiInteger(0);
        appointmentRequestedFteQuantityTotal = new BigDecimal(0).setScale(5, KualiDecimal.ROUND_BEHAVIOR);
        percentChangeTotal = new KualiDecimal(0.00);
    }

    /**
     * Gets the appointmentRequestedAmountTotal attribute.
     * 
     * @return Returns the appointmentRequestedAmountTotal.
     */
    public KualiInteger getAppointmentRequestedAmountTotal() {
        return appointmentRequestedAmountTotal;
    }

    /**
     * Sets the appointmentRequestedAmountTotal attribute value.
     * 
     * @param appointmentRequestedAmountTotal The appointmentRequestedAmountTotal to set.
     */
    public void setAppointmentRequestedAmountTotal(KualiInteger appointmentRequestedAmountTotal) {
        this.appointmentRequestedAmountTotal = appointmentRequestedAmountTotal;
    }

    /**
     * Gets the appointmentRequestedFteQuantityTotal attribute.
     * 
     * @return Returns the appointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedFteQuantityTotal() {
        return appointmentRequestedFteQuantityTotal;
    }

    /**
     * Sets the appointmentRequestedFteQuantityTotal attribute value.
     * 
     * @param appointmentRequestedFteQuantityTotal The appointmentRequestedFteQuantityTotal to set.
     */
    public void setAppointmentRequestedFteQuantityTotal(BigDecimal appointmentRequestedFteQuantityTotal) {
        this.appointmentRequestedFteQuantityTotal = appointmentRequestedFteQuantityTotal;
    }

    /**
     * Gets the csfAmountTotal attribute.
     * 
     * @return Returns the csfAmountTotal.
     */
    public KualiInteger getCsfAmountTotal() {
        return csfAmountTotal;
    }

    /**
     * Sets the csfAmountTotal attribute value.
     * 
     * @param csfAmountTotal The csfAmountTotal to set.
     */
    public void setCsfAmountTotal(KualiInteger csfAmountTotal) {
        this.csfAmountTotal = csfAmountTotal;
    }

    /**
     * Gets the csfFullTimeEmploymentQuantityTotal attribute.
     * 
     * @return Returns the csfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantityTotal() {
        return csfFullTimeEmploymentQuantityTotal;
    }

    /**
     * Sets the csfFullTimeEmploymentQuantityTotal attribute value.
     * 
     * @param csfFullTimeEmploymentQuantityTotal The csfFullTimeEmploymentQuantityTotal to set.
     */
    public void setCsfFullTimeEmploymentQuantityTotal(BigDecimal csfFullTimeEmploymentQuantityTotal) {
        this.csfFullTimeEmploymentQuantityTotal = csfFullTimeEmploymentQuantityTotal;
    }


    /**
     * Gets the percentChangeTotal attribute.
     * 
     * @return Returns the percentChangeTotal.
     */
    public KualiDecimal getPercentChangeTotal() {

        if (appointmentRequestedAmountTotal == null || csfAmountTotal.isZero()) {
            setPercentChangeTotal(new KualiDecimal(0.00));
        }
        else {
            BigDecimal diffRslt = (appointmentRequestedAmountTotal.bigDecimalValue().setScale(4)).subtract(csfAmountTotal.bigDecimalValue().setScale(4));
            BigDecimal divRslt = diffRslt.divide((csfAmountTotal.bigDecimalValue().setScale(4)), KualiDecimal.ROUND_BEHAVIOR);
            setPercentChangeTotal(new KualiDecimal(divRslt.multiply(BigDecimal.valueOf(100)).setScale(2)));
        }

        return percentChangeTotal;
    }

    /**
     * Sets the percentChangeTotal attribute value.
     * 
     * @param percentChangeTotal The percentChangeTotal to set.
     */
    public void setPercentChangeTotal(KualiDecimal percentChangeTotal) {
        this.percentChangeTotal = percentChangeTotal;
    }

    /**
     * Gets the pendingBudgetConstructionAppointmentFunding attribute.
     * 
     * @return Returns the pendingBudgetConstructionAppointmentFunding.
     */
    public List<PendingBudgetConstructionAppointmentFunding> getPendingBudgetConstructionAppointmentFunding() {
        return pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * Sets the pendingBudgetConstructionAppointmentFunding attribute value.
     * 
     * @param pendingBudgetConstructionAppointmentFunding The pendingBudgetConstructionAppointmentFunding to set.
     */
    @Deprecated
    public void setPendingBudgetConstructionAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding) {
        this.pendingBudgetConstructionAppointmentFunding = pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        // return super.buildListOfDeletionAwareLists();
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(this.getPendingBudgetConstructionAppointmentFunding());
        return managedLists;
    }
}
