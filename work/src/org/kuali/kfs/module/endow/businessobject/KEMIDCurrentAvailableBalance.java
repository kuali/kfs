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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KEMIDCurrentAvailableBalance extends PersistableBusinessObjectBase {

    private String kemid;
    private BigDecimal availableIncomeCash;
    private BigDecimal availablePrincipalCash;
    private BigDecimal availableTotalCash;

    private KEMID kemidObj;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        return m;
    }

    /**
     * Gets the availableIncomeCash.
     * 
     * @return availableIncomeCash
     */
    public BigDecimal getAvailableIncomeCash() {
        return availableIncomeCash;
    }

    /**
     * Sets the availableIncomeCash.
     * 
     * @param availableIncomeCash
     */
    public void setAvailableIncomeCash(BigDecimal availableIncomeCash) {
        this.availableIncomeCash = availableIncomeCash;
    }

    /**
     * Gets the availablePrincipalCash.
     * 
     * @return availablePrincipalCash
     */
    public BigDecimal getAvailablePrincipalCash() {
        return availablePrincipalCash;
    }

    /**
     * Sets the availablePrincipalCash.
     * 
     * @param availablePrincipalCash
     */
    public void setAvailablePrincipalCash(BigDecimal availablePrincipalCash) {
        this.availablePrincipalCash = availablePrincipalCash;
    }

    /**
     * Gets the availableTotalCash.
     * 
     * @return availableTotalCash
     */
    public BigDecimal getAvailableTotalCash() {
        return availableTotalCash;
    }

    /**
     * Sets the availableTotalCash.
     * 
     * @param availableTotalCash
     */
    public void setAvailableTotalCash(BigDecimal availableTotalCash) {
        this.availableTotalCash = availableTotalCash;
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
     * Gets the kemidObj.
     * 
     * @return kemidObj
     */
    public KEMID getKemidObj() {
        return kemidObj;
    }

    /**
     * Sets the kemidObj.
     * 
     * @param kemidObj
     */
    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    /**
     * Gets the Balance Date which is the Current System Process date
     * 
     * @return the Balance Date
     */
    public Date getBalanceDate() {

        return SpringContext.getBean(KEMService.class).getCurrentDate();
    }

}
