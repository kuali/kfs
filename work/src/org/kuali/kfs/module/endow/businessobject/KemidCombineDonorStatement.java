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
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This KemidCombineDonorStatement class provides the KEMIDs to be combined for data on a Donor Statement.
 */
public class KemidCombineDonorStatement extends PersistableBusinessObjectBase {

    private String kemid;
    private KualiInteger combineDonorSeq;
    private String combineWithKemid;
    private Date combineDate;
    private Date terminateCombineDate;

    private KEMID kemidObjRef;
    private KEMID combineWithKemidObjRef;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_COMBINE_DONOR_STATEMENT_SEQ, String.valueOf(combineDonorSeq));
        return m;
    }

    /**
     * Gets the combineDate.
     * 
     * @return combineDate
     */
    public Date getCombineDate() {
        return combineDate;
    }

    /**
     * Sets the combineDate.
     * 
     * @param combineDate
     */
    public void setCombineDate(Date combineDate) {
        this.combineDate = combineDate;
    }

    /**
     * Gets the combineDonorSeq.
     * 
     * @return combineDonorSeq
     */
    public KualiInteger getCombineDonorSeq() {
        return combineDonorSeq;
    }

    /**
     * Sets the combineDonorSeq.
     * 
     * @param combineDonorSeq
     */
    public void setCombineDonorSeq(KualiInteger combineDonorSeq) {
        this.combineDonorSeq = combineDonorSeq;
    }

    /**
     * Gets the combineWithKemid.
     * 
     * @return combineWithKemid
     */
    public String getCombineWithKemid() {
        return combineWithKemid;
    }

    /**
     * Sets the combineWithKemid.
     * 
     * @param combineWithKemid
     */
    public void setCombineWithKemid(String combineWithKemid) {
        this.combineWithKemid = combineWithKemid;
    }

    /**
     * Gets the combineWithKemidObjRef.
     * 
     * @return combineWithKemidObjRef
     */
    public KEMID getCombineWithKemidObjRef() {
        return combineWithKemidObjRef;
    }

    /**
     * Sets the combineWithKemidObjRef.
     * 
     * @param combineWithKemidObjRef
     */
    public void setCombineWithKemidObjRef(KEMID combineWithKemidObjRef) {
        this.combineWithKemidObjRef = combineWithKemidObjRef;
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
     * Gets the terminateCombineDate.
     * 
     * @return terminateCombineDate
     */
    public Date getTerminateCombineDate() {
        return terminateCombineDate;
    }

    /**
     * Sets the terminateCombineDate.
     * 
     * @param terminateCombineDate
     */
    public void setTerminateCombineDate(Date terminateCombineDate) {
        this.terminateCombineDate = terminateCombineDate;
    }

}
