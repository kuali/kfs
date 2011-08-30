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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

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
     * static instantiate an A21ICRAccount from an ICRAccount
     *
     * @param icrAccount
     * @return
     */
    public static A21IndirectCostRecoveryAccount copyICRAccount(IndirectCostRecoveryAccount icrAccount) {
        A21IndirectCostRecoveryAccount a21icrAccount = new A21IndirectCostRecoveryAccount();
        a21icrAccount.setAccountLinePercent(icrAccount.getAccountLinePercent());
        a21icrAccount.setIndirectCostRecoveryFinCoaCode(icrAccount.getIndirectCostRecoveryFinCoaCode());
        a21icrAccount.setIndirectCostRecoveryAccountNumber(icrAccount.getIndirectCostRecoveryAccountNumber());
        return a21icrAccount;
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.a21IndirectCostRecoveryAccountGeneratedIdentifier != null) {
            m.put("a21IndirectCostRecoveryAccountGeneratedIdentifier", this.a21IndirectCostRecoveryAccountGeneratedIdentifier.toString());
        }
        return m;
    }

}
