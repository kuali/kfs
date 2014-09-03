/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.rice.kns.web.struts.form.LookupForm;


/**
 * This class is the action form for Customer Aging Reports.
 */
public class CustomerAgingReportForm extends LookupForm {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CustomerAgingReportForm.class);

    private String total0to30;
    private String total31to60;
    private String total61to90;
    private String total91toSYSPR;
    private String totalSYSPRplus1orMore;

    private String totalOpenInvoices;
    private String totalWriteOffs;

    /**
     * Gets the total0to30 attribute.
     *
     * @return Returns the total0to30.
     */
    public String getTotal0to30() {
        return total0to30;
    }

    /**
     * Sets the total0to30 attribute value.
     *
     * @param total0to30 The total0to30 to set.
     */
    public void setTotal0to30(String total0to30) {
        this.total0to30 = total0to30;
    }

    /**
     * Gets the total31to60 attribute.
     *
     * @return Returns the total31to60.
     */
    public String getTotal31to60() {
        return total31to60;
    }

    /**
     * Sets the total31to60 attribute value.
     *
     * @param total31to60 The total31to60 to set.
     */
    public void setTotal31to60(String total31to60) {
        this.total31to60 = total31to60;
    }

    /**
     * Gets the total61to90 attribute.
     *
     * @return Returns the total61to90.
     */
    public String getTotal61to90() {
        return total61to90;
    }

    /**
     * Sets the total61to90 attribute value.
     *
     * @param total61to90 The total61to90 to set.
     */
    public void setTotal61to90(String total61to90) {
        this.total61to90 = total61to90;
    }

    /**
     * Gets the total91toSYSPR attribute.
     *
     * @return Returns the total91toSYSPR.
     */
    public String getTotal91toSYSPR() {
        return total91toSYSPR;
    }

    /**
     * Sets the total91toSYSPR attribute value.
     *
     * @param total91toSYSPR The total91toSYSPR to set.
     */
    public void setTotal91toSYSPR(String total91toSYSPR) {
        this.total91toSYSPR = total91toSYSPR;
    }

    /**
     * Gets the totalSYSPRplus1orMore attribute.
     *
     * @return Returns the totalSYSPRplus1orMore.
     */
    public String getTotalSYSPRplus1orMore() {
        return totalSYSPRplus1orMore;
    }

    /**
     * Sets the totalSYSPRplus1orMore attribute value.
     *
     * @param totalSYSPRplus1orMore The totalSYSPRplus1orMore to set.
     */
    public void setTotalSYSPRplus1orMore(String totalSYSPRplus1orMore) {
        this.totalSYSPRplus1orMore = totalSYSPRplus1orMore;
    }

    /**
     * Gets the totalOpenInvoices attribute.
     *
     * @return Returns the totalOpenInvoices.
     */
    public String getTotalOpenInvoices() {
        return totalOpenInvoices;
    }

    /**
     * Sets the totalOpenInvoices attribute value.
     *
     * @param totalOpenInvoices The totalOpenInvoices to set.
     */
    public void setTotalOpenInvoices(String totalOpenInvoices) {
        this.totalOpenInvoices = totalOpenInvoices;
    }

    /**
     * Gets the totalWriteOffs attribute.
     *
     * @return Returns the totalWriteOffs.
     */
    public String getTotalWriteOffs() {
        return totalWriteOffs;
    }

    /**
     * Sets the totalWriteOffs attribute value.
     *
     * @param totalWriteOffs The totalWriteOffs to set.
     */
    public void setTotalWriteOffs(String totalWriteOffs) {
        this.totalWriteOffs = totalWriteOffs;
    }

}
