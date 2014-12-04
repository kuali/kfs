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
