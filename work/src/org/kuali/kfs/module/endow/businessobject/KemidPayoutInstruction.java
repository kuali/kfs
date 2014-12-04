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
package org.kuali.kfs.module.endow.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This KemidPayoutInstruction class provides the KEMID to which the payout should go. The default is 100% to the income of the
 * KEMID.
 */
public class KemidPayoutInstruction extends PersistableBusinessObjectBase {

    private String kemid;
    private KualiInteger payoutIncomeSequenceNumber;
    private String payIncomeToKemid;
    private KualiDecimal percentOfIncomeToPayToKemid;
    private Date startDate;
    private Date endDate;

    private KEMID kemidObjRef;
    private KEMID payIncomeToKemidObjRef;


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_PAY_INC_SEQ_NBR, String.valueOf(this.payoutIncomeSequenceNumber));
        m.put(EndowPropertyConstants.KEMID_PAY_INC_TO_KEMID, this.payIncomeToKemid);
        return m;
    }


    /**
     * Gets the endDate.
     * 
     * @return endDate
     */
    public Date getEndDate() {
        return endDate;
    }


    /**
     * Sets the endDate.
     * 
     * @param endDate
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    /**
     * Gets the kemid.
     * 
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }


    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }


    /**
     * Gets the kemidObjRef.
     * 
     * @return kemidObjRef
     */
    public KEMID getKemidObjRef() {
        return kemidObjRef;
    }


    /**
     * Sets the kemidObjRef.
     * 
     * @param kemidObjRef
     */
    public void setKemidObjRef(KEMID kemidObjRef) {
        this.kemidObjRef = kemidObjRef;
    }


    /**
     * Gets the payIncomeToKemid.
     * 
     * @return payIncomeToKemid
     */
    public String getPayIncomeToKemid() {
        return payIncomeToKemid;
    }


    /**
     * Sets the payIncomeToKemid.
     * 
     * @param payIncomeToKemid
     */
    public void setPayIncomeToKemid(String payIncomeToKemid) {
        this.payIncomeToKemid = payIncomeToKemid;
    }


    /**
     * Gets the payIncomeToKemidObjRef.
     * 
     * @return payIncomeToKemidObjRef
     */
    public KEMID getPayIncomeToKemidObjRef() {
        return payIncomeToKemidObjRef;
    }


    /**
     * Sets the payIncomeToKemidObjRef.
     * 
     * @param payIncomeToKemidObjRef
     */
    public void setPayIncomeToKemidObjRef(KEMID payIncomeToKemidObjRef) {
        this.payIncomeToKemidObjRef = payIncomeToKemidObjRef;
    }


    /**
     * Gets the payoutIncomeSequenceNumber.
     * 
     * @return payoutIncomeSequenceNumber
     */
    public KualiInteger getPayoutIncomeSequenceNumber() {
        return payoutIncomeSequenceNumber;
    }


    /**
     * Sets the payoutIncomeSequenceNumber.
     * 
     * @param payoutIncomeSequenceNumber
     */
    public void setPayoutIncomeSequenceNumber(KualiInteger payoutIncomeSequenceNumber) {
        this.payoutIncomeSequenceNumber = payoutIncomeSequenceNumber;
    }


    /**
     * Gets the percentOfIncomeToPayToKemid.
     * 
     * @return percentOfIncomeToPayToKemid
     */
    public KualiDecimal getPercentOfIncomeToPayToKemid() {
        return percentOfIncomeToPayToKemid;
    }


    /**
     * Sets the percentOfIncomeToPayToKemid.
     * 
     * @param percentOfIncomeToPayToKemid
     */
    public void setPercentOfIncomeToPayToKemid(KualiDecimal percentOfIncomeToPayToKemid) {
        this.percentOfIncomeToPayToKemid = percentOfIncomeToPayToKemid;
    }


    /**
     * Gets the startDate.
     * 
     * @return startDate
     */
    public Date getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate.
     * 
     * @param startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

}
