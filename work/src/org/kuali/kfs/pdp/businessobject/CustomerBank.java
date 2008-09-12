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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;

/**
 * 
 */
public class CustomerBank extends TimestampedBusinessObjectBase {
    private Integer id; // CUST_BNK_ID

    private Integer customerId;
    private CustomerProfile customerProfile; // CUST_ID

    private String bankCode;
    private Bank bank; // BNK_ID

    private String disbursementTypeCode;
    private DisbursementType disbursementType; // DISB_TYP_CD

    /**
     * 
     */
    public CustomerBank() {
        super();
    }
   
    /**
     * @hibernate.many-to-one column="CUST_ID" class="edu.iu.uis.pdp.bo.CustomerProfile" not-null="true"
     * @return Returns the customerId.
     */
    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    /**
     * @param customerId The customerId to set.
     */
    public void setCustomerProfile(CustomerProfile customer) {
        this.customerProfile = customer;
    }

    /**
     * @hibernate.many-to-one column="BNK_ID" class="edu.iu.uis.pdp.bo.Bank" not-null="true"
     * @return Returns the bankId.
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * @param bankId The bankId to set.
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }
    
    /**
     * Gets the bankCode attribute.
     * 
     * @return Returns the bankCode.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bankCode attribute value.
     * 
     * @param bankCode The bankCode to set.
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
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
     * @hibernate.id column="CUST_BNK_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_CUST_BNK_ID_SEQ"
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

    public boolean equals(Object obj) {
        if (!(obj instanceof CustomerBank)) {
            return false;
        }
        CustomerBank o = (CustomerBank) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(83, 37).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

}
