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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
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
    private String perDiemCalcMethod;
    private BalanceType balanceType;
    private ObjectCode objectCode;

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

    public ObjectCode getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("code", code);

        return map;
    }
}
