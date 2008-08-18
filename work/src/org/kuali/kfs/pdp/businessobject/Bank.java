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
 * Created on Jul 7, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.UniversalUserService;

/**
 * @author jsissom
 * @hibernate.class table="PDP.PDP_BNK_CD_T"
 */
public class Bank extends TimestampedBusinessObjectBase {
    private Integer id;
    private Integer version;
    private String description;
    private String name;
    private String routingNumber;
    private String accountNumber;
    private Boolean active;
    private DisbursementType disbursementType;
    private String disbursementTypeCode;
   
    /**
     * 
     */
    public Bank() {
        super();
        active = Boolean.FALSE;
    }
    
    /**
     * @hibernate.property column="BNK_ACCT_NBR" length="17" not-null="true"
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @hibernate.property column="BNK_ACTV_IND" type="yes_no" not-null="true"
     * @return Returns the active.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active The active to set.
     */
    public void setActive(Boolean active) {
        if (active == null) {
            this.active = Boolean.FALSE;
        }
        else {
            this.active = active;
        }
    }

    /**
     * @hibernate.property column="BNK_DESC" length="25" not-null="true"
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @hibernate.many-to-one column="DISB_TYP_CD" class="edu.iu.uis.pdp.bo.DisbursementType" not-null="true"
     * @return Returns the disbursementType.
     */
    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    /**
     * @param disbursementType The disbursementType to set.
     */
    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    /**
     * @hibernate.id column="BNK_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_BANK_ID_SEQ"
     * @return Returns the id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @hibernate.property column="BNK_NM" not-null="true"
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @hibernate.version column="VER_NBR"
     * @return Returns the version.
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param ojbVerNbr The ojbVerNbr to set.
     */
    public void setVersion(Integer ver) {
        this.version = ver;
    }

    /**
     * @hibernate.property column="BNK_RTNG_NBR" length="9" not-null="true"
     * @return Returns the routingNumber.
     */
    public String getRoutingNumber() {
        return routingNumber;
    }

    /**
     * @param routingNumber The routingNumber to set.
     */
    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Bank)) {
            return false;
        }
        Bank o = (Bank) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(83, 67).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }

}
