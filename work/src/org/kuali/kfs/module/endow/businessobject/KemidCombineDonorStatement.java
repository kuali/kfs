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
