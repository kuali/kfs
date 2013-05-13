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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This KemidCurrentCash class provides the current cash infor: income and principal for a KEMID.
 */
public class KemidCurrentCash extends PersistableBusinessObjectBase {

    private String kemid;
    private KualiDecimal currentIncomeCash;
    private KualiDecimal currentPrincipalCash;

    private KEMID kemidObjRef;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        return m;
    }

    /**
     * Gets the currentIncomeCash.
     * 
     * @return currentIncomeCash
     */
    public KualiDecimal getCurrentIncomeCash() {
        return currentIncomeCash;
    }

    /**
     * Sets the currentIncomeCash.
     * 
     * @param currentIncomeCash
     */
    public void setCurrentIncomeCash(KualiDecimal currentIncomeCash) {
        this.currentIncomeCash = currentIncomeCash;
    }

    /**
     * Gets the currentPrincipalCash.
     * 
     * @return currentPrincipalCash
     */
    public KualiDecimal getCurrentPrincipalCash() {
        return currentPrincipalCash;
    }

    /**
     * Sets the currentPrincipalCash.
     * 
     * @param currentPrincipalCash
     */
    public void setCurrentPrincipalCash(KualiDecimal currentPrincipalCash) {
        this.currentPrincipalCash = currentPrincipalCash;
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

}
