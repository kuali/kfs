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
