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
