/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.util;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;

public class SalarySettingCalculator {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingCalculator.class);

    /**
     * calculate the standard working hours through the given time percent
     * 
     * @param timePercent the given time percent
     * @return the standard working hour calculated from the given time percent
     */
    public static BigDecimal getStandarHours(BigDecimal timePercent) {
        BigDecimal standarHours = timePercent.multiply(BCConstants.STANDARD_WEEKLY_WORK_HOUR_AS_DECIMAL);
        return standarHours;
    }

    /**
     * calcaulte the total requested csf amount for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total requested csf amount for the given appointment funding lines
     */
    public static KualiInteger getAppointmentRequestedCsfAmountTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        KualiInteger appointmentRequestedCsfAmountTotal = KualiInteger.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            KualiInteger requestedCsfAmount = appointmentFunding.getAppointmentRequestedCsfAmount();

            if (requestedCsfAmount != null) {
                appointmentRequestedCsfAmountTotal = appointmentRequestedCsfAmountTotal.add(requestedCsfAmount);
            }
        }

        return appointmentRequestedCsfAmountTotal;
    }

    /**
     * calcaulte the total requested csf time percent for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total requested csf time percent for the given appointment funding lines
     */
    public static BigDecimal getAppointmentRequestedCsfTimePercentTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        BigDecimal appointmentRequestedCsfTimePercentTotal = BigDecimal.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            BigDecimal requestedCsfTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent();

            if (requestedCsfTimePercent != null) {
                appointmentRequestedCsfTimePercentTotal = appointmentRequestedCsfTimePercentTotal.add(requestedCsfTimePercent);
            }
        }

        return appointmentRequestedCsfTimePercentTotal;
    }

    /**
     * calcaulte the total requested csf standard hours for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total requested csf standard hours for the given appointment funding lines
     */
    public static BigDecimal getAppointmentRequestedCsfStandardHoursTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        return getStandarHours(getAppointmentRequestedCsfTimePercentTotal(AppointmentFundings));
    }

    /**
     * calcaulte the total requested csf full time employee quantity for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total requested csf full time employee quantity for the given appointment funding lines
     */
    public static BigDecimal getAppointmentRequestedCsfFteQuantityTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        BigDecimal appointmentRequestedCsfFteQuantityTotal = BigDecimal.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            BigDecimal requestedCsfFteQuantity = appointmentFunding.getAppointmentRequestedCsfFteQuantity();

            if (requestedCsfFteQuantity != null) {
                appointmentRequestedCsfFteQuantityTotal = appointmentRequestedCsfFteQuantityTotal.add(requestedCsfFteQuantity);
            }
        }

        return appointmentRequestedCsfFteQuantityTotal;
    }

    /**
     * calcaulte the total requested amount for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total requested amount for the given appointment funding lines
     */
    public static KualiInteger getAppointmentRequestedAmountTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        KualiInteger appointmentRequestedAmountTotal = KualiInteger.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();

            if (requestedAmount != null) {
                appointmentRequestedAmountTotal = appointmentRequestedAmountTotal.add(requestedAmount);
            }
        }
        return appointmentRequestedAmountTotal;
    }

    /**
     * calcaulte the total requested time percent for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total requested time percent for the given appointment funding lines
     */
    public static BigDecimal getAppointmentRequestedTimePercentTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        BigDecimal appointmentRequestedTimePercentTotal = BigDecimal.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            BigDecimal requestedTimePercent = appointmentFunding.getAppointmentRequestedTimePercent();

            if (requestedTimePercent != null) {
                appointmentRequestedTimePercentTotal = appointmentRequestedTimePercentTotal.add(requestedTimePercent);
            }
        }
        return appointmentRequestedTimePercentTotal;
    }

    /**
     * calcaulte the total requested standard hours for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total requested standard hours for the given appointment funding lines
     */
    public static BigDecimal getAppointmentRequestedStandardHoursTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        return getStandarHours(getAppointmentRequestedTimePercentTotal(AppointmentFundings));
    }

    /**
     * calcaulte the total requested full time employee quantity for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total requested full time employee quantity for the given appointment funding lines
     */
    public static BigDecimal getAppointmentRequestedFteQuantityTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        BigDecimal appointmentRequestedFteQuantityTotal = BigDecimal.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();

            if (requestedFteQuantity != null) {
                appointmentRequestedFteQuantityTotal = appointmentRequestedFteQuantityTotal.add(requestedFteQuantity);
            }
        }
        return appointmentRequestedFteQuantityTotal;
    }

    /**
     * calcaulte the total csf amount for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total csf amount for the given appointment funding lines
     */
    public static KualiInteger getCsfAmountTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        KualiInteger csfAmountTotal = KualiInteger.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();

            if (csfTracker != null && csfTracker.getCsfAmount() != null) {
                csfAmountTotal = csfAmountTotal.add(csfTracker.getCsfAmount());
            }
        }
        return csfAmountTotal;
    }

    /**
     * calcaulte the total csf time percent for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total csf time percent for the given appointment funding lines
     */
    public static BigDecimal getCsfTimePercentTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        BigDecimal csfTimePercentTotal = BigDecimal.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();

            if (csfTracker != null && csfTracker.getCsfTimePercent() != null) {
                csfTimePercentTotal = csfTimePercentTotal.add(csfTracker.getCsfTimePercent());
            }
        }
        return csfTimePercentTotal;
    }

    /**
     * calcaulte the total csf standard hours for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total csf standard hours for the given appointment funding lines
     */
    public static BigDecimal getCsfStandardHoursTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        return getStandarHours(getCsfTimePercentTotal(AppointmentFundings));
    }

    /**
     * calcaulte the total csf full time employee quantity for the given appointment funding lines
     * 
     * @param AppointmentFundings the given appointment funding lines
     * @return the total csf full time employee quantity for the given appointment funding lines
     */
    public static BigDecimal getCsfFullTimeEmploymentQuantityTotal(List<PendingBudgetConstructionAppointmentFunding> AppointmentFundings) {
        BigDecimal csfFullTimeEmploymentQuantityTotal = BigDecimal.ZERO;

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : AppointmentFundings) {
            BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();

            if (csfTracker != null && csfTracker.getCsfFullTimeEmploymentQuantity() != null) {
                csfFullTimeEmploymentQuantityTotal = csfFullTimeEmploymentQuantityTotal.add(csfTracker.getCsfFullTimeEmploymentQuantity());
            }
        }
        return csfFullTimeEmploymentQuantityTotal;
    }

    /**
     * calculate the changing percent between the requested amount and the base amount
     * 
     * @param baseAmount the given base amount
     * @param requestedAmount the requested amount
     * @return the changing percent between the requested amount and the base amount if both of amounts are numbers; otherwise, return null 
     */
    public static KualiDecimal getPercentChange(KualiInteger baseAmount, KualiInteger requestedAmount) {
        KualiDecimal percentChange = null;

        if (requestedAmount != null && baseAmount != null && baseAmount.isNonZero()) {
            KualiInteger difference = requestedAmount.subtract(baseAmount);
            BigDecimal percentChangeAsBigDecimal = difference.multiply(KFSConstants.ONE_HUNDRED).divide(baseAmount);

            percentChange = new KualiDecimal(percentChangeAsBigDecimal);
        }

        return percentChange;
    }
}
