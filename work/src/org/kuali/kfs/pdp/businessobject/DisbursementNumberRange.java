/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.UniversalUserService;

/**
 * 
 */
public class DisbursementNumberRange extends TimestampedBusinessObjectBase {

    private Integer id; // DISB_NBR_RANGE_ID
    private String physCampusProcCode; // PHYS_CMP_PROC_CD
    private Integer beginDisbursementNbr; // BEG_DISB_NBR
    private Integer lastAssignedDisbNbr; // LST_ASND_DISB_NBR
    private Integer endDisbursementNbr; // END_DISB_NBR
    private Timestamp disbNbrEffectiveDt; // DISB_NBR_EFF_DT
    private Timestamp disbNbrExpirationDt; // DISB_NBR_EXPR_DT
    private Integer version; // VER_NBR

    private Integer bankId;
    private Bank bank;

    public DisbursementNumberRange() {
        super();
    }
    /**
     * @hibernate.id column="DISB_NBR_RANGE_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_DISB_NBR_RANGE_ID_SEQ"
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param documentTypeId The documentTypeId to set.
     */
    public void setId(Integer documentTypeId) {
        this.id = documentTypeId;
    }
    /**
     * @return
     * @hibernate.version column="VER_NBR" not-null="true"
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @return
     * 
     */
   
    public Bank getBank() {
        return bank;
    }

    /**
     * @return
     * @hibernate.property column="BEG_DISB_NBR"
     */
    public Integer getBeginDisbursementNbr() {
        return beginDisbursementNbr;
    }

    /**
     * @return
     * @hibernate.property column="DISB_NBR_EFF_DT"
     */
    public Timestamp getDisbNbrEffectiveDt() {
        return disbNbrEffectiveDt;
    }

    /**
     * @return
     * @hibernate.property column="DISB_NBR_EXPR_DT"
     */
    public Timestamp getDisbNbrExpirationDt() {
        return disbNbrExpirationDt;
    }

    /**
     * @return
     * @hibernate.property column="END_DISB_NBR"
     */
    public Integer getEndDisbursementNbr() {
        return endDisbursementNbr;
    }

    /**
     * @return
     * @hibernate.property column="LST_ASND_DISB_NBR"
     */
    public Integer getLastAssignedDisbNbr() {
        return lastAssignedDisbNbr;
    }

    /**
     * @return
     * @hibernate.property column="PHYS_CMP_PROC_CD" length="2"
     */
    public String getPhysCampusProcCode() {
        return physCampusProcCode;
    }

    /**
     * @param Bank
     */
    @Deprecated
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * @param integer
     */
    public void setBeginDisbursementNbr(Integer integer) {
        beginDisbursementNbr = integer;
    }

    /**
     * @param timestamp
     */
    public void setDisbNbrEffectiveDt(Timestamp timestamp) {
        disbNbrEffectiveDt = timestamp;
    }

    /**
     * @param timestamp
     */
    public void setDisbNbrExpirationDt(Timestamp timestamp) {
        disbNbrExpirationDt = timestamp;
    }

    /**
     * @param integer
     */
    public void setEndDisbursementNbr(Integer integer) {
        endDisbursementNbr = integer;
    }

    /**
     * @param integer
     */
    public void setLastAssignedDisbNbr(Integer integer) {
        lastAssignedDisbNbr = integer;
    }

    /**
     * @param string
     */
    public void setPhysCampusProcCode(String string) {
        physCampusProcCode = string;
    }

    /**
     * @param integer
     */
    public void setVersion(Integer integer) {
        version = integer;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DisbursementNumberRange)) {
            return false;
        }
        DisbursementNumberRange o = (DisbursementNumberRange) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(83, 43).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }
    
    public Integer getBankId() {
        return bankId;
    }
    
    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }
}
