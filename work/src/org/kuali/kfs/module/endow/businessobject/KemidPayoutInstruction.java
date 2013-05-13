/*
 * Copyright 2009 The Kuali Foundation.
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
