/*
 * Copyright 2010 The Kuali Foundation.
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

import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KemidHistoricalCash extends PersistableBusinessObjectBase{
    private String kemid;
    private KualiInteger monthEndDateId;
    private KualiDecimal historicalIncomeCash;
    private KualiDecimal historicalPrincipalCash;

    private KEMID kemidObjRef;
    private MonthEndDate monthEndDate;
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.MONTH_END_DATE_ID, this.monthEndDateId);
        return m;
    }

    /**
     * Gets the historicalIncomeCash.
     * 
     * @return historicalIncomeCash
     */
    public KualiDecimal getHistoricalIncomeCash() {
        return historicalIncomeCash;
    }

    /**
     * Sets the historicalIncomeCash.
     * 
     * @param historicalIncomeCash
     */
    public void setHistoricalIncomeCash(KualiDecimal historicalIncomeCash) {
        this.historicalIncomeCash = historicalIncomeCash;
    }

    /**
     * Gets the historicalPrincipalCash.
     * 
     * @return historicalPrincipalCash
     */
    public KualiDecimal getHistoricalPrincipalCash() {
        return historicalPrincipalCash;
    }

    /**
     * Sets the historicalPrincipalCash.
     * 
     * @param historicalPrincipalCash
     */
    public void setHistoricalPrincipalCash(KualiDecimal historicalPrincipalCash) {
        this.historicalPrincipalCash = historicalPrincipalCash;
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
     * Gets the monthEndDateId.
     * 
     * @return monthEndDateId
     */
    public KualiInteger getMonthEndDateId() {
        return monthEndDateId;
    }

    /**
     * Sets the monthEndDateId.
     * 
     * @param monthEndDateId
     */
    public void setMonthEndDateId(KualiInteger monthEndDateId) {
        this.monthEndDateId = monthEndDateId;
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
     * Gets the monthEndDate.
     * 
     * @return monthEndDate
     */
    public MonthEndDate getMonthEndDate() {
        return monthEndDate;
    }

    /**
     * Sets the monthEndDate.
     * 
     * @param monthEndDate
     */
    public void setMonthEndDate(MonthEndDate monthEndDate) {
        this.monthEndDate = monthEndDate;
    }

}
