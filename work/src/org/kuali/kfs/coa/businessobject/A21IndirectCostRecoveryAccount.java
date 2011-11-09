/*
 * Copyright 2011 The Kuali Foundation
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

package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

/**
 * IndirectCostRecoveryAccount for A21SubAccount
 */
public class A21IndirectCostRecoveryAccount extends IndirectCostRecoveryAccount {
    private static Logger LOG = Logger.getLogger(A21IndirectCostRecoveryAccount.class);

    private Integer a21IndirectCostRecoveryAccountGeneratedIdentifier;
    
    //additional foreign keys to SubAccount
    private String subAccountNumber;
    
    /**
     * Default constructor.
     */
    public A21IndirectCostRecoveryAccount() {
    }
    
    /**
     * private constructor 
     * 
     * @param icr
     */
    private A21IndirectCostRecoveryAccount(IndirectCostRecoveryAccount icr) {
        BeanUtils.copyProperties(icr,this);
    }
    
    /**
     * static instantiate an A21ICRAccount from an ICRAccount
     *
     * @param icrAccount
     * @return
     */
    public static A21IndirectCostRecoveryAccount copyICRAccount(IndirectCostRecoveryAccount icrAccount) {
        return new A21IndirectCostRecoveryAccount(icrAccount); 
    }

    public Integer getA21IndirectCostRecoveryAccountGeneratedIdentifier() {
        return a21IndirectCostRecoveryAccountGeneratedIdentifier;
    }

    public void setA21IndirectCostRecoveryAccountGeneratedIdentifier(Integer a21IndirectCostRecoveryAccountGeneratedIdentifier) {
        this.a21IndirectCostRecoveryAccountGeneratedIdentifier = a21IndirectCostRecoveryAccountGeneratedIdentifier;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.a21IndirectCostRecoveryAccountGeneratedIdentifier != null) {
            m.put("a21IndirectCostRecoveryAccountGeneratedIdentifier", this.a21IndirectCostRecoveryAccountGeneratedIdentifier.toString());
        }
        return m;
    }

}
