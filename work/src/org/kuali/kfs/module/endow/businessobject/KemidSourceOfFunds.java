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
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * The KemidSourceOfFunds class describes the source of funds and the corpus total for endowments.
 */
public class KemidSourceOfFunds extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String kemid;
    private KualiInteger kemidFundSourceSequenceNumber;
    private String fundSourceCode;
    private String openedFromKemid;
    private String fundHistory;
    private String additionalSourceData;
    private boolean active;

    private KEMID kemidObjRef;
    private KEMID openedFromKemidObjRef;
    private FundSourceCode fundSource;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_FND_SRC_SEQ_NBR, String.valueOf(kemidFundSourceSequenceNumber));
        m.put(EndowPropertyConstants.KEMID_FND_SRC_CD, fundSourceCode);
        return m;
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
     * Gets the kemidFundSourceSequenceNumber.
     * 
     * @return kemidFundSourceSequenceNumber
     */
    public KualiInteger getKemidFundSourceSequenceNumber() {
        return kemidFundSourceSequenceNumber;
    }

    /**
     * Sets the kemidFundSourceSequenceNumber.
     * 
     * @param kemidFundSourceSequenceNumber
     */
    public void setKemidFundSourceSequenceNumber(KualiInteger kemidFundSourceSequenceNumber) {
        this.kemidFundSourceSequenceNumber = kemidFundSourceSequenceNumber;
    }

    /**
     * Gets the fundSourceCode.
     * 
     * @return fundSourceCode
     */
    public String getFundSourceCode() {
        return fundSourceCode;
    }

    /**
     * Sets the fundSourceCode.
     * 
     * @param fundSourceCode
     */
    public void setFundSourceCode(String fundSourceCode) {
        this.fundSourceCode = fundSourceCode;
    }

    /**
     * Gets the openedFromKemid.
     * 
     * @return openedFromKemid
     */
    public String getOpenedFromKemid() {
        return openedFromKemid;
    }

    /**
     * Sets the openedFromKemid.
     * 
     * @param openedFromKemid
     */
    public void setOpenedFromKemid(String openedFromKemid) {
        this.openedFromKemid = openedFromKemid;
    }

    /**
     * Sets the fundHistory.
     * 
     * @return fundHistory
     */
    public String getFundHistory() {
        return fundHistory;
    }

    /**
     * Sets the fundHistory.
     * 
     * @param fundHistory
     */
    public void setFundHistory(String fundHistory) {
        this.fundHistory = fundHistory;
    }

    /**
     * Gets the additionalSourceData.
     * 
     * @return additionalSourceData
     */
    public String getAdditionalSourceData() {
        return additionalSourceData;
    }

    /**
     * Sets the additionalSourceData.
     * 
     * @param additionalSourceData
     */
    public void setAdditionalSourceData(String additionalSourceData) {
        this.additionalSourceData = additionalSourceData;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
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
     * Gets the fundSource.
     * 
     * @return fundSource
     */
    public FundSourceCode getFundSource() {
        return fundSource;
    }

    /**
     * Sets the fundSource.
     * 
     * @param fundSource
     */
    public void setFundSource(FundSourceCode fundSource) {
        this.fundSource = fundSource;
    }

    /**
     * Gets the openedFromKemidObjRef.
     * 
     * @return openedFromKemidObjRef
     */
    public KEMID getOpenedFromKemidObjRef() {
        return openedFromKemidObjRef;
    }

    /**
     * Sets the openedFromKemidObjRef.
     * 
     * @param openedFromKemidObjRef
     */
    public void setOpenedFromKemidObjRef(KEMID openedFromKemidObjRef) {
        this.openedFromKemidObjRef = openedFromKemidObjRef;
    }
   
}
