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
package org.kuali.kfs.fp.batch;

import java.sql.Date;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This class is created for generating Procurement Card Report object.
 */
public class ProcurementCardReportType extends TransientBusinessObjectBase {
    private Date transactionPostingDate;
    private String formattedPostingDate;
    private int totalDocNumber;
    private int totalTranNumber;
    private String totalAmount;

    /**
     * Gets the transactionPostingDate attribute.
     * @return Returns the transactionPostingDate.
     */
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }


    /**
     * Sets the transactionPostingDate attribute value.
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }


    /**
     * Gets the totalDocNumber attribute.
     * @return Returns the totalDocNumber.
     */
    public int getTotalDocNumber() {
        return totalDocNumber;
    }


    /**
     * Sets the totalDocNumber attribute value.
     * @param totalDocNumber The totalDocNumber to set.
     */
    public void setTotalDocNumber(int totalDocNumber) {
        this.totalDocNumber = totalDocNumber;
    }


    /**
     * Gets the totalTranNumber attribute.
     * @return Returns the totalTranNumber.
     */
    public int getTotalTranNumber() {
        return totalTranNumber;
    }


    /**
     * Sets the totalTranNumber attribute value.
     * @param totalTranNumber The totalTranNumber to set.
     */
    public void setTotalTranNumber(int totalTranNumber) {
        this.totalTranNumber = totalTranNumber;
    }


    /**
     * Gets the totalAmount attribute.
     * @return Returns the totalAmount.
     */
    public String getTotalAmount() {
        return totalAmount;
    }


    /**
     * Sets the totalAmount attribute value.
     * @param totalAmount The totalAmount to set.
     */
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }



    /**
     * Gets the formattedPostingDate attribute.
     * @return Returns the formattedPostingDate.
     */
    public String getFormattedPostingDate() {
        return formattedPostingDate;
    }



    /**
     * Sets the formattedPostingDate attribute value.
     * @param formattedPostingDate The formattedPostingDate to set.
     */
    public void setFormattedPostingDate(String formattedPostingDate) {
        this.formattedPostingDate = formattedPostingDate;
    }



}
