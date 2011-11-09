/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.report.util;

import java.util.Map;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class...
 */
public class CustomerAgingReportDataHolder {

    private Map<String, Object> knownCustomers;
    private KualiDecimal total0to30;
    private KualiDecimal total31to60;
    private KualiDecimal total61to90;
    private KualiDecimal total91toSYSPR;
    private KualiDecimal totalSYSPRplus1orMore;
    private KualiDecimal totalAmountDue;
    /**
     * Gets the knownCustomers attribute. 
     * @return Returns the knownCustomers.
     */
    public Map<String, Object> getKnownCustomers() {
        return knownCustomers;
    }
    /**
     * Sets the knownCustomers attribute value.
     * @param knownCustomers The knownCustomers to set.
     */
    public void setKnownCustomers(Map<String, Object> knownCustomers) {
        this.knownCustomers = knownCustomers;
    }
    /**
     * Gets the total0to30 attribute. 
     * @return Returns the total0to30.
     */
    public KualiDecimal getTotal0to30() {
        return total0to30;
    }
    /**
     * Sets the total0to30 attribute value.
     * @param total0to30 The total0to30 to set.
     */
    public void setTotal0to30(KualiDecimal total0to30) {
        this.total0to30 = total0to30;
    }
    /**
     * Gets the total31to60 attribute. 
     * @return Returns the total31to60.
     */
    public KualiDecimal getTotal31to60() {
        return total31to60;
    }
    /**
     * Sets the total31to60 attribute value.
     * @param total31to60 The total31to60 to set.
     */
    public void setTotal31to60(KualiDecimal total31to60) {
        this.total31to60 = total31to60;
    }
    /**
     * Gets the total61to90 attribute. 
     * @return Returns the total61to90.
     */
    public KualiDecimal getTotal61to90() {
        return total61to90;
    }
    /**
     * Sets the total61to90 attribute value.
     * @param total61to90 The total61to90 to set.
     */
    public void setTotal61to90(KualiDecimal total61to90) {
        this.total61to90 = total61to90;
    }
    /**
     * Gets the total91toSYSPR attribute. 
     * @return Returns the total91toSYSPR.
     */
    public KualiDecimal getTotal91toSYSPR() {
        return total91toSYSPR;
    }
    /**
     * Sets the total91toSYSPR attribute value.
     * @param total91toSYSPR The total91toSYSPR to set.
     */
    public void setTotal91toSYSPR(KualiDecimal total91toSYSPR) {
        this.total91toSYSPR = total91toSYSPR;
    }
    /**
     * Gets the totalSYSPRplus1orMore attribute. 
     * @return Returns the totalSYSPRplus1orMore.
     */
    public KualiDecimal getTotalSYSPRplus1orMore() {
        return totalSYSPRplus1orMore;
    }
    /**
     * Sets the totalSYSPRplus1orMore attribute value.
     * @param totalSYSPRplus1orMore The totalSYSPRplus1orMore to set.
     */
    public void setTotalSYSPRplus1orMore(KualiDecimal totalSYSPRplus1orMore) {
        this.totalSYSPRplus1orMore = totalSYSPRplus1orMore;
    }
    /**
     * Gets the totalAmountDue attribute. 
     * @return Returns the totalAmountDue.
     */
    public KualiDecimal getTotalAmountDue() {
        return totalAmountDue;
    }
    /**
     * Sets the totalAmountDue attribute value.
     * @param totalAmountDue The totalAmountDue to set.
     */
    public void setTotalAmountDue(KualiDecimal totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }

    /**
     * 
     * This method clears all the amount fields and resets them to zero.
     */
    public void clearAllAmounts() {
        this.total0to30 = KualiDecimal.ZERO;
        this.total31to60 = KualiDecimal.ZERO;
        this.total61to90 = KualiDecimal.ZERO;
        this.total91toSYSPR = KualiDecimal.ZERO;
        this.totalSYSPRplus1orMore = KualiDecimal.ZERO;
        this.totalAmountDue = KualiDecimal.ZERO;
    }
}
