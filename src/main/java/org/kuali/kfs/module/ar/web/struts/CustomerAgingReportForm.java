/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
