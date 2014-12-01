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
