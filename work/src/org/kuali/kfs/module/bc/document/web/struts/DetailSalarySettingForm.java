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

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;

/**
 * This class...
 */
public class DetailSalarySettingForm extends KualiForm {

    // detail salary setting screen totals used in position and incumbent salary setting
    private KualiInteger bcafAppointmentRequestedCsfAmountTotal;
    private BigDecimal bcafAppointmentRequestedCsfTimePercentTotal;
    private BigDecimal bcafAppointmentRequestedCsfStandardHoursTotal;
    private BigDecimal bcafAppointmentRequestedCsfFteQuantityTotal;
    private KualiInteger bcafAppointmentRequestedAmountTotal;
    private BigDecimal bcafAppointmentRequestedTimePercentTotal;
    private BigDecimal bcafAppointmentRequestedStandardHoursTotal;
    private BigDecimal bcafAppointmentRequestedFteQuantityTotal;
    private KualiInteger bcsfCsfAmountTotal;
    private BigDecimal bcsfCsfTimePercentTotal;
    private BigDecimal bcsfCsfStandardHoursTotal;
    private BigDecimal bcsfCsfFullTimeEmploymentQuantityTotal;


    public DetailSalarySettingForm() {
        super();
        zeroTotals();
    }

    /**
     * This zeros totals displayed on the detail salary setting screen (position or incumbent)
     */
    public void zeroTotals() {

        bcafAppointmentRequestedCsfAmountTotal = new KualiInteger(BigDecimal.ZERO);
        bcafAppointmentRequestedCsfTimePercentTotal = new BigDecimal(0).setScale(5, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedCsfStandardHoursTotal = new BigDecimal(0).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedCsfFteQuantityTotal = new BigDecimal(0).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedAmountTotal = new KualiInteger(0);
        bcafAppointmentRequestedTimePercentTotal = new BigDecimal(0).setScale(5, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedStandardHoursTotal = new BigDecimal(0).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcafAppointmentRequestedFteQuantityTotal = new BigDecimal(0).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcsfCsfAmountTotal = new KualiInteger(0);
        bcsfCsfTimePercentTotal = new BigDecimal(0).setScale(5, KualiDecimal.ROUND_BEHAVIOR);
        bcsfCsfStandardHoursTotal = new BigDecimal(0).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        bcsfCsfFullTimeEmploymentQuantityTotal = new BigDecimal(0).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
    }

    public void addBCAFLineToTotals(PendingBudgetConstructionAppointmentFunding line) {

        // add to totals
        // bcnCalculatedSalaryFoundationTracker is a list with either zero or one entries
        if (line.getBcnCalculatedSalaryFoundationTracker().size() > 0) {
            if (line.getBcnCalculatedSalaryFoundationTracker().get(0).getCsfAmount() != null) {
                setBcsfCsfAmountTotal(getBcsfCsfAmountTotal().add(line.getBcnCalculatedSalaryFoundationTracker().get(0).getCsfAmount()));
            }
            if (line.getBcnCalculatedSalaryFoundationTracker().get(0).getCsfTimePercent() != null) {
                setBcsfCsfTimePercentTotal(getBcsfCsfTimePercentTotal().add(line.getBcnCalculatedSalaryFoundationTracker().get(0).getCsfTimePercent()));
            }
            if (line.getBcnCalculatedSalaryFoundationTracker().get(0).getCsfFullTimeEmploymentQuantity() != null) {
                setBcsfCsfFullTimeEmploymentQuantityTotal(getBcsfCsfFullTimeEmploymentQuantityTotal().add(line.getBcnCalculatedSalaryFoundationTracker().get(0).getCsfFullTimeEmploymentQuantity()));
            }
        }
        if (line.getAppointmentRequestedAmount() != null) {
            setBcafAppointmentRequestedAmountTotal(getBcafAppointmentRequestedAmountTotal().add(line.getAppointmentRequestedAmount()));
        }
        if (line.getAppointmentRequestedTimePercent() != null) {
            setBcafAppointmentRequestedTimePercentTotal(getBcafAppointmentRequestedTimePercentTotal().add(line.getAppointmentRequestedTimePercent()));
        }
        if (line.getAppointmentRequestedFteQuantity() != null) {
            setBcafAppointmentRequestedFteQuantityTotal(getBcafAppointmentRequestedFteQuantityTotal().add(line.getAppointmentRequestedFteQuantity()));
        }

        if (line.getAppointmentRequestedCsfAmount() != null) {
            setBcafAppointmentRequestedCsfAmountTotal(getBcafAppointmentRequestedCsfAmountTotal().add(line.getAppointmentRequestedCsfAmount()));
        }
        if (line.getAppointmentRequestedCsfTimePercent() != null) {
            setBcafAppointmentRequestedCsfTimePercentTotal(getBcafAppointmentRequestedCsfTimePercentTotal().add(line.getAppointmentRequestedCsfTimePercent()));
        }
        if (line.getAppointmentRequestedCsfFteQuantity() != null) {
            setBcafAppointmentRequestedCsfFteQuantityTotal(getBcafAppointmentRequestedCsfFteQuantityTotal().add(line.getAppointmentRequestedCsfFteQuantity()));
        }
    }

    /**
     * Gets the bcafAppointmentRequestedAmountTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedAmountTotal.
     */
    public KualiInteger getBcafAppointmentRequestedAmountTotal() {
        return bcafAppointmentRequestedAmountTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedAmountTotal attribute value.
     * 
     * @param bcafAppointmentRequestedAmountTotal The bcafAppointmentRequestedAmountTotal to set.
     */
    public void setBcafAppointmentRequestedAmountTotal(KualiInteger bcafAppointmentRequestedAmountTotal) {
        this.bcafAppointmentRequestedAmountTotal = bcafAppointmentRequestedAmountTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedCsfAmountTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedCsfAmountTotal.
     */
    public KualiInteger getBcafAppointmentRequestedCsfAmountTotal() {
        return bcafAppointmentRequestedCsfAmountTotal;
    }


    /**
     * Sets the bcafAppointmentRequestedCsfAmountTotal attribute value.
     * 
     * @param bcafAppointmentRequestedCsfAmountTotal The bcafAppointmentRequestedCsfAmountTotal to set.
     */
    public void setBcafAppointmentRequestedCsfAmountTotal(KualiInteger bcafAppointmentRequestedCsfAmountTotal) {
        this.bcafAppointmentRequestedCsfAmountTotal = bcafAppointmentRequestedCsfAmountTotal;
    }


    /**
     * Gets the bcafAppointmentRequestedCsfFteQuantityTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedCsfFteQuantityTotal.
     */
    public BigDecimal getBcafAppointmentRequestedCsfFteQuantityTotal() {
        return bcafAppointmentRequestedCsfFteQuantityTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedCsfFteQuantityTotal attribute value.
     * 
     * @param bcafAppointmentRequestedCsfFteQuantityTotal The bcafAppointmentRequestedCsfFteQuantityTotal to set.
     */
    public void setBcafAppointmentRequestedCsfFteQuantityTotal(BigDecimal bcafAppointmentRequestedCsfFteQuantityTotal) {
        this.bcafAppointmentRequestedCsfFteQuantityTotal = bcafAppointmentRequestedCsfFteQuantityTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedCsfStandardHoursTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedCsfStandardHoursTotal.
     */
    public BigDecimal getBcafAppointmentRequestedCsfStandardHoursTotal() {
        bcafAppointmentRequestedCsfStandardHoursTotal = bcafAppointmentRequestedCsfTimePercentTotal.multiply(BigDecimal.valueOf(0.4).setScale(2, KualiDecimal.ROUND_BEHAVIOR)).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        return bcafAppointmentRequestedCsfStandardHoursTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedCsfStandardHoursTotal attribute value.
     * 
     * @param bcafAppointmentRequestedCsfStandardHoursTotal The bcafAppointmentRequestedCsfStandardHoursTotal to set.
     */
    public void setBcafAppointmentRequestedCsfStandardHoursTotal(BigDecimal bcafAppointmentRequestedCsfStandardHoursTotal) {
        this.bcafAppointmentRequestedCsfStandardHoursTotal = bcafAppointmentRequestedCsfStandardHoursTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedCsfTimePercentTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedCsfTimePercentTotal.
     */
    public BigDecimal getBcafAppointmentRequestedCsfTimePercentTotal() {
        return bcafAppointmentRequestedCsfTimePercentTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedCsfTimePercentTotal attribute value.
     * 
     * @param bcafAppointmentRequestedCsfTimePercentTotal The bcafAppointmentRequestedCsfTimePercentTotal to set.
     */
    public void setBcafAppointmentRequestedCsfTimePercentTotal(BigDecimal bcafAppointmentRequestedCsfTimePercentTotal) {
        this.bcafAppointmentRequestedCsfTimePercentTotal = bcafAppointmentRequestedCsfTimePercentTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedFteQuantityTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getBcafAppointmentRequestedFteQuantityTotal() {
        return bcafAppointmentRequestedFteQuantityTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedFteQuantityTotal attribute value.
     * 
     * @param bcafAppointmentRequestedFteQuantityTotal The bcafAppointmentRequestedFteQuantityTotal to set.
     */
    public void setBcafAppointmentRequestedFteQuantityTotal(BigDecimal bcafAppointmentRequestedFteQuantityTotal) {
        this.bcafAppointmentRequestedFteQuantityTotal = bcafAppointmentRequestedFteQuantityTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedStandardHoursTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedStandardHoursTotal.
     */
    public BigDecimal getBcafAppointmentRequestedStandardHoursTotal() {
        bcafAppointmentRequestedStandardHoursTotal = bcafAppointmentRequestedTimePercentTotal.multiply(BigDecimal.valueOf(0.4).setScale(2, KualiDecimal.ROUND_BEHAVIOR)).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        return bcafAppointmentRequestedStandardHoursTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedStandardHoursTotal attribute value.
     * 
     * @param bcafAppointmentRequestedStandardHoursTotal The bcafAppointmentRequestedStandardHoursTotal to set.
     */
    public void setBcafAppointmentRequestedStandardHoursTotal(BigDecimal bcafAppointmentRequestedStandardHoursTotal) {
        this.bcafAppointmentRequestedStandardHoursTotal = bcafAppointmentRequestedStandardHoursTotal;
    }

    /**
     * Gets the bcafAppointmentRequestedTimePercentTotal attribute.
     * 
     * @return Returns the bcafAppointmentRequestedTimePercentTotal.
     */
    public BigDecimal getBcafAppointmentRequestedTimePercentTotal() {
        return bcafAppointmentRequestedTimePercentTotal;
    }

    /**
     * Sets the bcafAppointmentRequestedTimePercentTotal attribute value.
     * 
     * @param bcafAppointmentRequestedTimePercentTotal The bcafAppointmentRequestedTimePercentTotal to set.
     */
    public void setBcafAppointmentRequestedTimePercentTotal(BigDecimal bcafAppointmentRequestedTimePercentTotal) {
        this.bcafAppointmentRequestedTimePercentTotal = bcafAppointmentRequestedTimePercentTotal;
    }

    /**
     * Gets the bcsfCsfAmountTotal attribute.
     * 
     * @return Returns the bcsfCsfAmountTotal.
     */
    public KualiInteger getBcsfCsfAmountTotal() {
        return bcsfCsfAmountTotal;
    }

    /**
     * Sets the bcsfCsfAmountTotal attribute value.
     * 
     * @param bcsfCsfAmountTotal The bcsfCsfAmountTotal to set.
     */
    public void setBcsfCsfAmountTotal(KualiInteger bcsfCsfAmountTotal) {
        this.bcsfCsfAmountTotal = bcsfCsfAmountTotal;
    }

    /**
     * Gets the bcsfCsfFullTimeEmploymentQuantityTotal attribute.
     * 
     * @return Returns the bcsfCsfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getBcsfCsfFullTimeEmploymentQuantityTotal() {
        return bcsfCsfFullTimeEmploymentQuantityTotal;
    }

    /**
     * Sets the bcsfCsfFullTimeEmploymentQuantityTotal attribute value.
     * 
     * @param bcsfCsfFullTimeEmploymentQuantityTotal The bcsfCsfFullTimeEmploymentQuantityTotal to set.
     */
    public void setBcsfCsfFullTimeEmploymentQuantityTotal(BigDecimal bcsfCsfFullTimeEmploymentQuantityTotal) {
        this.bcsfCsfFullTimeEmploymentQuantityTotal = bcsfCsfFullTimeEmploymentQuantityTotal;
    }

    /**
     * Gets the bcsfCsfStandardHoursTotal attribute.
     * 
     * @return Returns the bcsfCsfStandardHoursTotal.
     */
    public BigDecimal getBcsfCsfStandardHoursTotal() {
        bcsfCsfStandardHoursTotal = bcsfCsfTimePercentTotal.multiply(BigDecimal.valueOf(0.4).setScale(2, KualiDecimal.ROUND_BEHAVIOR)).setScale(2, KualiDecimal.ROUND_BEHAVIOR);
        return bcsfCsfStandardHoursTotal;
    }

    /**
     * Sets the bcsfCsfStandardHoursTotal attribute value.
     * 
     * @param bcsfCsfStandardHoursTotal The bcsfCsfStandardHoursTotal to set.
     */
    public void setBcsfCsfStandardHoursTotal(BigDecimal bcsfCsfStandardHoursTotal) {
        this.bcsfCsfStandardHoursTotal = bcsfCsfStandardHoursTotal;
    }

    /**
     * Gets the bcsfCsfTimePercentTotal attribute.
     * 
     * @return Returns the bcsfCsfTimePercentTotal.
     */
    public BigDecimal getBcsfCsfTimePercentTotal() {
        return bcsfCsfTimePercentTotal;
    }

    /**
     * Sets the bcsfCsfTimePercentTotal attribute value.
     * 
     * @param bcsfCsfTimePercentTotal The bcsfCsfTimePercentTotal to set.
     */
    public void setBcsfCsfTimePercentTotal(BigDecimal bcsfCsfTimePercentTotal) {
        this.bcsfCsfTimePercentTotal = bcsfCsfTimePercentTotal;
    }

}
