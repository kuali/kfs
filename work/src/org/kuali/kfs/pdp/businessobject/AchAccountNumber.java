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
 * Created on Jul 9, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;

/**
 * @author delyea
 * @hibernate.class table="PDP.PDP_ACH_ACCT_NBR_T"
 */

public class AchAccountNumber extends TimestampedBusinessObjectBase {

    private Integer id; // PMT_GRP_ID Primary Key
    private String achBankAccountNbr; // ACH_BNK_ACCT_NBR
    
    public AchAccountNumber() {
        super();
    }

    /**
     * returns a partial string of the account number if the account number is zero chars it returns empty string if the account
     * number is 1-4 chars it returns everything masked if the account number is 5-8 chars it returns the last 2 numbers non masked
     * if the account number is > 8 chars it returns the last 4 numbers non masked
     * 
     * @return
     * @hibernate.property column="ACH_BNK_ACCT_NBR" length="17"
     */
    public String getPartialMaskAchBankAccountNbr() {
        String partialAccountNumber = "";
        String numbers = "";
        int accountLength = achBankAccountNbr.length();
        if (accountLength == 0) {
            return "";
        }
        else if ((accountLength == 3) || (accountLength == 4)) {
            numbers = achBankAccountNbr.substring(accountLength - 1);
        }
        else if ((accountLength < 8) && (accountLength > 4)) {
            numbers = achBankAccountNbr.substring(accountLength - 3);
        }
        else if (accountLength >= 8) {
            numbers = achBankAccountNbr.substring(accountLength - 4);
        }
        for (int i = 0; i < (accountLength - numbers.length()); i++) {
            partialAccountNumber = partialAccountNumber + "*";
        }
        return partialAccountNumber + numbers;
    }

    /**
     * @return
     * @hibernate.property column="ACH_BNK_ACCT_NBR" length="17"
     */
    public String getAchBankAccountNbr() {
        return achBankAccountNbr;
    }

    /**
     * @return
     * @hibernate.id column="PMT_GRP_ID" generator-class="assigned"
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param integer
     */
    public void setAchBankAccountNbr(String s) {
        achBankAccountNbr = s;
    }

    /**
     * @param integer
     */
    public void setId(Integer integer) {
        id = integer;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AchAccountNumber)) {
            return false;
        }
        AchAccountNumber o = (AchAccountNumber) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(67, 79).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("id", this.id);
        
        return m;
    }
}
