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
