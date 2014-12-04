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
package org.kuali.kfs.module.tem.document.web.struts;

import java.io.Serializable;

import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class HistoryValueObject implements Serializable {
    private String documentNumber;
    private String date;
    private String status;
    private String onHold;
    private String cancel;
    private KualiDecimal amount;
    
    public HistoryValueObject(final TravelReimbursementDocument document) {
        setDocumentNumber(document.getDocumentHeader().getDocumentNumber());
        if (document.getTripBegin() != null) {
            setDate(document.getTripBegin().toString());
        }
        setStatus(document.getAppDocStatus());
        if(document.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD)) {
            setOnHold("" + true);
        } else {
            setOnHold("" + false);
        }
        
        if(document.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.CANCELLED)) {
            setCancel("" + true);
        } else {
            setCancel("" + false);
        }
        
        setAmount(document.getTotalDollarAmount());
    }
    
    /**
     * Gets the value of documentNumber
     *
     * @return the value of documentNumber
     */
    public String getDocumentNumber() {
        return this.documentNumber;
    }
    
    /**
     * Sets the value of documentNumber
     *
     * @param argDocumentNumber Value to assign to this.documentNumber
     */
    public void setDocumentNumber(final String argDocumentNumber) {
        this.documentNumber = argDocumentNumber;
    }
    
    /**
     * Gets the value of date
     *
     * @return the value of date
     */
    public String getDate() {
        return this.date;
    }
    
    /**
     * Sets the value of date
     *
     * @param argDate Value to assign to this.date
     */
    public void setDate(final String argDate) {
        this.date = argDate;
    }
    
    /**
     * Gets the value of status
     *
     * @return the value of status
     */
    public String getStatus() {
        return this.status;
    }
    
    /**
     * Sets the value of status
     *
     * @param argStatus Value to assign to this.status
     */
    public void setStatus(final String argStatus) {
        this.status = argStatus;
    }
    
    /**
     * Gets the value of onHold
     *
     * @return the value of onHold
     */
    public String getOnHold() {
        return this.onHold;
    }
    
    /**
     * Sets the value of onHold
     *
     * @param argOnHold Value to assign to this.onHold
     */
    public void setOnHold(final String argOnHold) {
        this.onHold = argOnHold;
    }
    
    /**
     * Gets the value of cancel
     *
     * @return the value of cancel
     */
    public String getCancel() {
        return this.cancel;
    }
    
    /**
     * Sets the value of cancel
     *
     * @param argCancel Value to assign to this.cancel
     */
    public void setCancel(final String argCancel) {
        this.cancel = argCancel;
    }
    
    /**
     * Gets the value of amount
     *
     * @return the value of amount
     */
    public KualiDecimal getAmount() {
        return this.amount;
    }
    
    /**
     * Sets the value of amount
     *
     * @param argAmount Value to assign to this.amount
     */
    public void setAmount(final KualiDecimal argAmount) {
        this.amount = argAmount;
    }
}
