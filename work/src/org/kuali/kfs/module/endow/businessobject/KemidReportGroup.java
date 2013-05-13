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
 * This KemidReportGroup class provides the report group for consolidated reporting to which the KEMID belongs.
 */
public class KemidReportGroup extends PersistableBusinessObjectBase {

    private String kemid;
    private KualiInteger combineGroupSeq;
    private String combineGroupCode;
    private Date dateAdded;
    private Date dateTerminated;

    private KEMID kemidObjRef;
    private CombineGroupCode combineGroup;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_REPORT_GRP_SEQ, String.valueOf(combineGroupSeq));
        m.put(EndowPropertyConstants.KEMID_REPORT_GRP_CD, combineGroupCode);
        return m;
    }

    /**
     * Gets the combineGroup.
     * 
     * @return combineGroup
     */
    public CombineGroupCode getCombineGroup() {
        return combineGroup;
    }

    /**
     * Sets the combineGroup.
     * 
     * @param combineGroup
     */
    public void setCombineGroup(CombineGroupCode combineGroup) {
        this.combineGroup = combineGroup;
    }

    /**
     * Gets the combineGroupCode.
     * 
     * @return combineGroupCode
     */
    public String getCombineGroupCode() {
        return combineGroupCode;
    }

    /**
     * Sets the combineGroupCode.
     * 
     * @param combineGroupCode
     */
    public void setCombineGroupCode(String combineGroupCode) {
        this.combineGroupCode = combineGroupCode;
    }

    /**
     * Gets the combineGroupSeq.
     * 
     * @return combineGroupSeq
     */
    public KualiInteger getCombineGroupSeq() {
        return combineGroupSeq;
    }

    /**
     * Sets the combineGroupSeq.
     * 
     * @param combineGroupSeq
     */
    public void setCombineGroupSeq(KualiInteger combineGroupSeq) {
        this.combineGroupSeq = combineGroupSeq;
    }

    /**
     * Gets the dateAdded.
     * 
     * @return dateAdded
     */
    public Date getDateAdded() {
        return dateAdded;
    }

    /**
     * Sets the dateAdded.
     * 
     * @param dateAdded
     */
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     * Gets the dateTerminated.
     * 
     * @return dateTerminated
     */
    public Date getDateTerminated() {
        return dateTerminated;
    }

    /**
     * Sets the dateTerminated.
     * 
     * @param dateTerminated
     */
    public void setDateTerminated(Date dateTerminated) {
        this.dateTerminated = dateTerminated;
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
