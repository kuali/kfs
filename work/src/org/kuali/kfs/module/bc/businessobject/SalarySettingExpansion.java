/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.rice.KNSServiceLocator;

/**
 * TODO is this needed??? probably need to just point OJB repository to PBGL class or
 * this should extend PBGL if something extra is needed
 * 
 */
public class SalarySettingExpansion extends PendingBudgetConstructionGeneralLedger {

    // Total Fields - First Total Line
    private KualiInteger csfAmountTotal;
    private BigDecimal csfFullTimeEmploymentQuantityTotal;
    private KualiInteger appointmentRequestedAmountTotal;
    private BigDecimal appointmentRequestedFteQuantityTotal;
    private KualiDecimal percentChangeTotal;
     
    /**
     * Default constructor.
     */
    public SalarySettingExpansion() {
        super();
        zeroTotals();

    }

    /**
     * 
     * Zeros the totals appearing on the Salary Setting Screen
     */
    public void zeroTotals() {

        csfAmountTotal = new KualiInteger(0);
        csfFullTimeEmploymentQuantityTotal = new BigDecimal(0).setScale(5,BigDecimal.ROUND_HALF_EVEN);
        appointmentRequestedAmountTotal = new KualiInteger(0);
        appointmentRequestedFteQuantityTotal = new BigDecimal(0).setScale(5,BigDecimal.ROUND_HALF_EVEN);
        percentChangeTotal = new KualiDecimal(0.00);
    }

    /**
     * Gets the appointmentRequestedAmountTotal attribute. 
     * @return Returns the appointmentRequestedAmountTotal.
     */
    public KualiInteger getAppointmentRequestedAmountTotal() {
        return appointmentRequestedAmountTotal;
    }

    /**
     * Sets the appointmentRequestedAmountTotal attribute value.
     * @param appointmentRequestedAmountTotal The appointmentRequestedAmountTotal to set.
     */
    public void setAppointmentRequestedAmountTotal(KualiInteger appointmentRequestedAmountTotal) {
        this.appointmentRequestedAmountTotal = appointmentRequestedAmountTotal;
    }

    /**
     * Gets the appointmentRequestedFteQuantityTotal attribute. 
     * @return Returns the appointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedFteQuantityTotal() {
        return appointmentRequestedFteQuantityTotal;
    }

    /**
     * Sets the appointmentRequestedFteQuantityTotal attribute value.
     * @param appointmentRequestedFteQuantityTotal The appointmentRequestedFteQuantityTotal to set.
     */
    public void setAppointmentRequestedFteQuantityTotal(BigDecimal appointmentRequestedFteQuantityTotal) {
        this.appointmentRequestedFteQuantityTotal = appointmentRequestedFteQuantityTotal;
    }

    /**
     * Gets the csfAmountTotal attribute. 
     * @return Returns the csfAmountTotal.
     */
    public KualiInteger getCsfAmountTotal() {
        return csfAmountTotal;
    }

    /**
     * Sets the csfAmountTotal attribute value.
     * @param csfAmountTotal The csfAmountTotal to set.
     */
    public void setCsfAmountTotal(KualiInteger csfAmountTotal) {
        this.csfAmountTotal = csfAmountTotal;
    }

    /**
     * Gets the csfFullTimeEmploymentQuantityTotal attribute. 
     * @return Returns the csfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantityTotal() {
        return csfFullTimeEmploymentQuantityTotal;
    }

    /**
     * Sets the csfFullTimeEmploymentQuantityTotal attribute value.
     * @param csfFullTimeEmploymentQuantityTotal The csfFullTimeEmploymentQuantityTotal to set.
     */
    public void setCsfFullTimeEmploymentQuantityTotal(BigDecimal csfFullTimeEmploymentQuantityTotal) {
        this.csfFullTimeEmploymentQuantityTotal = csfFullTimeEmploymentQuantityTotal;
    }


    /**
     * Gets the percentChangeTotal attribute. 
     * @return Returns the percentChangeTotal.
     */
    public KualiDecimal getPercentChangeTotal() {

        if (appointmentRequestedAmountTotal == null || csfAmountTotal.isZero()){
            setPercentChangeTotal(new KualiDecimal(0.00));
        } else {
            BigDecimal diffRslt = (appointmentRequestedAmountTotal.bigDecimalValue().setScale(4)).subtract(csfAmountTotal.bigDecimalValue().setScale(4));
            BigDecimal divRslt = diffRslt.divide((csfAmountTotal.bigDecimalValue().setScale(4)),BigDecimal.ROUND_HALF_UP);
            setPercentChangeTotal(new KualiDecimal(divRslt.multiply(BigDecimal.valueOf(100)).setScale(2))); 
        }

        return percentChangeTotal;
    }

    /**
     * Sets the percentChangeTotal attribute value.
     * @param percentChangeTotal The percentChangeTotal to set.
     */
    public void setPercentChangeTotal(KualiDecimal percentChangeTotal) {
        this.percentChangeTotal = percentChangeTotal;
    }

  
}
