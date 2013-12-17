/*
 * Copyright 2010 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * Trip Type
 *
 */
@Entity
@Table(name="TEM_TRIP_TYP_T")
public class TripType extends KualiCodeBase implements MutableInactivatable {
    private Boolean generateEncumbrance = Boolean.FALSE;
    private String encumbranceBalanceType;
    private String encumbranceObjCode;
    private Boolean contactInfoRequired = Boolean.FALSE;
    private Boolean blanketTravel = Boolean.FALSE;
    private KualiDecimal autoTravelReimbursementLimit;
    private Boolean usePerDiem = Boolean.FALSE;
    private Boolean travelAuthorizationRequired = Boolean.FALSE;
    private String perDiemCalcMethod = TemConstants.PERCENTAGE;
    private BalanceType balanceType;

    @Column(name="gen_enc_ind",nullable=false,length=1)
    public boolean isGenerateEncumbrance() {
        return generateEncumbrance;
    }

    public void setGenerateEncumbrance(boolean generateEncumbrance) {
        this.generateEncumbrance = generateEncumbrance;
    }

    @Column(name="enc_bal_typ",length=2)
    public String getEncumbranceBalanceType() {
        return encumbranceBalanceType;
    }

    public void setEncumbranceBalanceType(String encumbranceBalanceType) {
        this.encumbranceBalanceType = encumbranceBalanceType;
    }

    @Column(name="enc_obj_cd",length=4)
    public String getEncumbranceObjCode() {
        return encumbranceObjCode;
    }

    public void setEncumbranceObjCode(String encumbranceObjCode) {
        this.encumbranceObjCode = encumbranceObjCode;
    }

    @Column(name="cont_info_req_ind",nullable=false,length=1)
    public boolean isContactInfoRequired() {
        return contactInfoRequired;
    }

    public void setContactInfoRequired(boolean contactInfoRequired) {
        this.contactInfoRequired = contactInfoRequired;
    }

    @Column(name="blanket_ind",nullable=false,length=1)
    public boolean isBlanketTravel() {
        return blanketTravel;
    }

    public void setBlanketTravel(boolean blanketTravel) {
        this.blanketTravel = blanketTravel;
    }

    /**
     * Gets the autoTravelReimbursementLimit attribute.
     * @return Returns the autoTravelReimbursementLimit.
     */
    @Column(name="AUTO_TR_LIMIT",precision=19,scale=2,nullable=false)
    public KualiDecimal getAutoTravelReimbursementLimit() {
        return autoTravelReimbursementLimit;
    }

    /**
     * Sets the autoTravelReimbursementLimit attribute value.
     * @param autoTravelReimbursementLimit The autoTravelReimbursementLimit to set.
     */
    public void setAutoTravelReimbursementLimit(KualiDecimal autoTravelReimbursementLimit) {
        this.autoTravelReimbursementLimit = autoTravelReimbursementLimit;
    }

    /**
     * Gets the usePerDiem attribute.
     * @return Returns the usePerDiem.
     */
    @Column(name="USE_PER_DIEM",nullable=false,length=1)
    public boolean getUsePerDiem() {
        return usePerDiem;
    }

    /**
     * Sets the usePerDiem attribute value.
     * @param usePerDiem The usePerDiem to set.
     */
    public void setUsePerDiem(boolean usePerDiem) {
        this.usePerDiem = usePerDiem;
    }

    /**
     * Gets the travelAuthorizationRequired attribute.
     * @return Returns the travelAuthorizationRequired.
     */
    @Column(name="TA_REQUIRED",nullable=false,length=1)
    public boolean getTravelAuthorizationRequired() {
        return travelAuthorizationRequired;
    }

    /**
     * Sets the travelAuthorizationRequired attribute value.
     * @param travelAuthorizationRequired The travelAuthorizationRequired to set.
     */
    public void setTravelAuthorizationRequired(boolean travelAuthorizationRequired) {
        this.travelAuthorizationRequired = travelAuthorizationRequired;
    }

    /**
     * Gets the perDiemCalcMethod attribute.
     * @return Returns the perDiemCalcMethod.
     */
    @Column(name="PER_DIEM_CALC_METHOD",nullable=false,length=1)
    public String getPerDiemCalcMethod() {
        return perDiemCalcMethod;
    }

    /**
     * Sets the perDiemCalcMethod attribute value.
     * @param perDiemCalcMethod The perDiemCalcMethod to set.
     */
    public void setPerDiemCalcMethod(String perDiemCalcMethod) {
        this.perDiemCalcMethod = perDiemCalcMethod;
    }

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("code", code);

        return map;
    }
}
