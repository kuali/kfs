/*
 * Copyright 2012 The Kuali Foundation.
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
