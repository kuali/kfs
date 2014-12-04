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
 * This KemidUseCriteria class defines the Donor restrictions.
 */
public class KemidUseCriteria extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String kemid;
    private KualiInteger useCriteriaSeq;
    private String useCriteriaCode;
    private String useCriteriaAdditionalInfo;
    private boolean active;

    private KEMID kemidObjRef;
    private UseCriteriaCode useCriteria;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_USE_CRIT_SEQ, String.valueOf(useCriteriaSeq));
        m.put(EndowPropertyConstants.KEMID_USE_CRIT_CD, String.valueOf(useCriteriaCode));
        return m;
    }

    /**
     * Gets the kemid.
     * 
     * @param kemidObjRef
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
     * Gets the useCriteria.
     * 
     * @return useCriteria
     */
    public UseCriteriaCode getUseCriteria() {
        return useCriteria;
    }

    /**
     * Sets the useCriteria.
     * 
     * @param useCriteria
     */
    public void setUseCriteria(UseCriteriaCode useCriteria) {
        this.useCriteria = useCriteria;
    }

    /**
     * Gets the useCriteriaAdditionalInfo.
     * 
     * @return useCriteriaAdditionalInfo
     */
    public String getUseCriteriaAdditionalInfo() {
        return useCriteriaAdditionalInfo;
    }

    /**
     * Sets the useCriteriaAdditionalInfo.
     * 
     * @param useCriteriaAdditionalInfo
     */
    public void setUseCriteriaAdditionalInfo(String useCriteriaAdditionalInfo) {
        this.useCriteriaAdditionalInfo = useCriteriaAdditionalInfo;
    }

    /**
     * Gets the useCriteriaCode.
     * 
     * @return useCriteriaCode
     */
    public String getUseCriteriaCode() {
        return useCriteriaCode;
    }

    /**
     * Sets the useCriteriaCode.
     * 
     * @param useCriteriaCode
     */
    public void setUseCriteriaCode(String useCriteriaCode) {
        this.useCriteriaCode = useCriteriaCode;
    }

    /**
     * Gets the useCriteriaSeq.
     * 
     * @return useCriteriaSeq
     */
    public KualiInteger getUseCriteriaSeq() {
        return useCriteriaSeq;
    }

    /**
     * Sets the useCriteriaSeq.
     * 
     * @param useCriteriaSeq
     */
    public void setUseCriteriaSeq(KualiInteger useCriteriaSeq) {
        this.useCriteriaSeq = useCriteriaSeq;
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

}
