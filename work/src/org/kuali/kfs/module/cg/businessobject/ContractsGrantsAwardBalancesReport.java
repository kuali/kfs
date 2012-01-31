/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.cg.businessobject;

import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Defines a Contracts Grants Award Balances Report object.
 */
public class ContractsGrantsAwardBalancesReport extends Award {

    private String awardPrimaryProjectDirectorName;
    private String awardPrimaryFundManagerName;

    private KualiDecimal totalBilledToDate;
    private KualiDecimal totalPaymentsToDate;
    private KualiDecimal amountCurrentlyDue;

    private KualiDecimal awardTotalAmountForReport;

    /**
     * @return
     */
    public KualiDecimal getTotalBilledToDate() {
        return totalBilledToDate;
    }

    /**
     * @param totalBilledToDate
     */
    public void setTotalBilledToDate(KualiDecimal totalBilledToDate) {
        this.totalBilledToDate = totalBilledToDate;
    }

    /**
     * @return
     */
    public KualiDecimal getTotalPaymentsToDate() {
        return totalPaymentsToDate;
    }

    /**
     * @param totalPaymentsToDate
     */
    public void setTotalPaymentsToDate(KualiDecimal totalPaymentsToDate) {
        this.totalPaymentsToDate = totalPaymentsToDate;
    }

    /**
     * @return
     */
    public KualiDecimal getAmountCurrentlyDue() {
        return amountCurrentlyDue;
    }

    /**
     * @param amountCurrentlyDue
     */
    public void setAmountCurrentlyDue(KualiDecimal amountCurrentlyDue) {
        this.amountCurrentlyDue = amountCurrentlyDue;
    }

    /**
     * @return
     */
    public String getAwardPrimaryProjectDirectorName() {
        return awardPrimaryProjectDirectorName;
    }

    /**
     * @param awardPrimaryProjectDirectorName
     */
    public void setAwardPrimaryProjectDirectorName(String awardPrimaryProjectDirectorName) {
        this.awardPrimaryProjectDirectorName = awardPrimaryProjectDirectorName;
    }

    /**
     * @return
     */
    public String getAwardPrimaryFundManagerName() {
        return awardPrimaryFundManagerName;
    }

    /**
     * @param awardPrimaryFundManagerName
     */
    public void setAwardPrimaryFundManagerName(String awardPrimaryFundManagerName) {
        this.awardPrimaryFundManagerName = awardPrimaryFundManagerName;
    }

    /**
     * Gets the awardTotalAmountForReport attribute.
     * 
     * @return Returns the awardTotalAmountForReport.
     */
    public KualiDecimal getAwardTotalAmountForReport() {
        return awardTotalAmountForReport;
    }

    /**
     * Sets the awardTotalAmountForReport attribute value.
     * 
     * @param awardTotalAmountForReport The awardTotalAmountForReport to set.
     */
    public void setAwardTotalAmountForReport(KualiDecimal awardTotalAmountForReport) {
        this.awardTotalAmountForReport = awardTotalAmountForReport;
    }


}
