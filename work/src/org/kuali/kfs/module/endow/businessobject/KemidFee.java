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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This KemidFee class provides infrormation about fees that may be charged against the KEMID.
 */
public class KemidFee extends PersistableBusinessObjectBase {

    private String kemid;
    private String feeMethodCode;
    private KualiInteger feeMethodSeq;
    private String chargeFeeToKemid;
    private KualiDecimal percentOfFeeChargedToIncome;
    private KualiDecimal percentOfFeeChargedToPrincipal;
    private boolean accrueFees;
    private KualiDecimal totalAccruedFees;
    private boolean waiveFees;
    private KualiDecimal totalWaivedFeesThisFiscalYear;
    private KualiDecimal totalWaivedFees;
    private Date feeStartDate;
    private Date feeEndDate;

    private KEMID kemidObjRef;
    private FeeMethod feeMethod;
    private KEMID chargeFeeToKemidObjRef;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_FEE_SEQ, String.valueOf(this.feeMethodSeq));
        m.put(EndowPropertyConstants.KEMID_FEE_MTHD_CD, this.feeMethodCode);
        return m;
    }

    /**
     * Gets the accrueFees.
     * 
     * @return accrueFees
     */
    public boolean isAccrueFees() {
        return accrueFees;
    }

    /**
     * Sets the accrueFees.
     * 
     * @param accrueFees
     */
    public void setAccrueFees(boolean accrueFees) {
        this.accrueFees = accrueFees;
    }

    /**
     * Gets the chargeFeeToKemid.
     * 
     * @return chargeFeeToKemid
     */
    public String getChargeFeeToKemid() {
        return chargeFeeToKemid;
    }

    /**
     * Sets the chargeFeeToKemid.
     * 
     * @param chargeFeeToKemid
     */
    public void setChargeFeeToKemid(String chargeFeeToKemid) {
        this.chargeFeeToKemid = chargeFeeToKemid;
    }

    /**
     * Gets the chargeFeeToKemidObjRef.
     * 
     * @return chargeFeeToKemidObjRef
     */
    public KEMID getChargeFeeToKemidObjRef() {
        return chargeFeeToKemidObjRef;
    }

    /**
     * Sets the chargeFeeToKemidObjRef.
     * 
     * @param chargeFeeToKemidObjRef
     */
    public void setChargeFeeToKemidObjRef(KEMID chargeFeeToKemidObjRef) {
        this.chargeFeeToKemidObjRef = chargeFeeToKemidObjRef;
    }

    /**
     * Gets the feeEndDate.
     * 
     * @return feeEndDate
     */
    public Date getFeeEndDate() {
        return feeEndDate;
    }

    /**
     * Sets the feeEndDate.
     * 
     * @param feeEndDate
     */
    public void setFeeEndDate(Date feeEndDate) {
        this.feeEndDate = feeEndDate;
    }

    /**
     * Gets the feeMethodCode.
     * 
     * @return feeMethodCode
     */
    public String getFeeMethodCode() {
        return feeMethodCode;
    }

    /**
     * Sets the feeMethodCode.
     * 
     * @param feeMethodCode
     */
    public void setFeeMethodCode(String feeMethodCode) {
        this.feeMethodCode = feeMethodCode;
    }

    /**
     * Gets the feeMethod.
     * 
     * @return feeMethod
     */
    public FeeMethod getFeeMethod() {
        return feeMethod;
    }

    /**
     * Sets the feeMethod.
     * 
     * @param feeMethod
     */
    public void setFeeMethod(FeeMethod feeMethod) {
        this.feeMethod = feeMethod;
    }

    /**
     * Gets the feeMethodSeq.
     * 
     * @return feeMethodSeq
     */
    public KualiInteger getFeeMethodSeq() {
        return feeMethodSeq;
    }

    /**
     * Sets the feeMethodSeq.
     * 
     * @param feeMethodSeq
     */
    public void setFeeMethodSeq(KualiInteger feeMethodSeq) {
        this.feeMethodSeq = feeMethodSeq;
    }

    /**
     * Gets the feeStartDate.
     * 
     * @return feeStartDate
     */
    public Date getFeeStartDate() {
        return feeStartDate;
    }

    /**
     * Sets the feeStartDate.
     * 
     * @param feeStartDate
     */
    public void setFeeStartDate(Date feeStartDate) {
        this.feeStartDate = feeStartDate;
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
     * Sets the kemid
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
     * Gets the percentOfFeeChargedToIncome.
     * 
     * @return percentOfFeeChargedToIncome
     */
    public KualiDecimal getPercentOfFeeChargedToIncome() {
        return percentOfFeeChargedToIncome;
    }

    /**
     * Sets the percentOfFeeChargedToIncome.
     * 
     * @param percentOfFeeChargedToIncome
     */
    public void setPercentOfFeeChargedToIncome(KualiDecimal percentOfFeeChargedToIncome) {
        this.percentOfFeeChargedToIncome = percentOfFeeChargedToIncome;
    }

    /**
     * Gets the percentOfFeeChargedToPrincipal.
     * 
     * @return percentOfFeeChargedToPrincipal
     */
    public KualiDecimal getPercentOfFeeChargedToPrincipal() {
        return percentOfFeeChargedToPrincipal;
    }

    /**
     * Sets the percentOfFeeChargedToPrincipal.
     * 
     * @param percentOfFeeChargedToPrincipal
     */
    public void setPercentOfFeeChargedToPrincipal(KualiDecimal percentOfFeeChargedToPrincipal) {
        this.percentOfFeeChargedToPrincipal = percentOfFeeChargedToPrincipal;
    }

    /**
     * Gets the totalAccruedFees.
     * 
     * @return totalAccruedFees
     */
    public KualiDecimal getTotalAccruedFees() {
        return totalAccruedFees;
    }

    /**
     * Sets the totalAccruedFees.
     * 
     * @param totalAccruedFees
     */
    public void setTotalAccruedFees(KualiDecimal totalAccruedFees) {
        this.totalAccruedFees = totalAccruedFees;
    }

    /**
     * Gets the totalWaivedFees.
     * 
     * @return totalWaivedFees
     */
    public KualiDecimal getTotalWaivedFees() {
        return totalWaivedFees;
    }

    /**
     * Sets the totalWaivedFees.
     * 
     * @param totalWaivedFees
     */
    public void setTotalWaivedFees(KualiDecimal totalWaivedFees) {
        this.totalWaivedFees = totalWaivedFees;
    }

    /**
     * Gets the totalWaivedFeesThisFiscalYear.
     * 
     * @return totalWaivedFeesThisFiscalYear
     */
    public KualiDecimal getTotalWaivedFeesThisFiscalYear() {
        return totalWaivedFeesThisFiscalYear;
    }

    /**
     * Sets the totalWaivedFeesThisFiscalYear.
     * 
     * @param totalWaivedFeesThisFiscalYear
     */
    public void setTotalWaivedFeesThisFiscalYear(KualiDecimal totalWaivedFeesThisFiscalYear) {
        this.totalWaivedFeesThisFiscalYear = totalWaivedFeesThisFiscalYear;
    }

    /**
     * Gets the waiveFees.
     * 
     * @return waiveFees
     */
    public boolean isWaiveFees() {
        return waiveFees;
    }

    /**
     * Sets the waiveFees.
     * 
     * @param waiveFees
     */
    public void setWaiveFees(boolean waiveFees) {
        this.waiveFees = waiveFees;
    }

}
