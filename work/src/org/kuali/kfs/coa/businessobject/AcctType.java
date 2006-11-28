/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/AcctType.java,v $
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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class AcctType extends BusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public AcctType() {

    }

    private String accountTypeCode;
    private String accountTypeName;

    /**
     * Gets the accountTypeCode attribute.
     * 
     * @return Returns the accountTypeCode
     * 
     */
    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    /**
     * Sets the accountTypeCode attribute.
     * 
     * @param accountTypeCode The accountTypeCode to set.
     * 
     */
    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

    /**
     * Gets the accountTypeName attribute.
     * 
     * @return Returns the accountTypeName
     * 
     */
    public String getAccountTypeName() {
        return accountTypeName;
    }

    /**
     * Sets the accountTypeName attribute.
     * 
     * @param accountTypeName The accountTypeName to set.
     * 
     */
    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("accountTypeCode", this.accountTypeCode);
        return m;
    }
}
