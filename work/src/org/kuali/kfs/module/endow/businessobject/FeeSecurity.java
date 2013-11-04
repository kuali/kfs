/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;

/**
 * Business Object for Fee Security table.
 */
public class FeeSecurity extends FeeMethodCodeBase {
    private static final Logger LOG = Logger.getLogger(FeeSecurity.class);
    
    private String securityCode;
    
    private Security security;
    
    /**
     * Default constructor.
     */   
    public FeeSecurity() {
       super();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.FEE_METHOD_CODE, super.getFeeMethodCode());
        m.put(EndowPropertyConstants.FEE_SECURITY_CODE, this.getSecurityCode());        
        return m;
        
    }
        
    /**
     * This method gets securityCode
     * 
     * @return securityCode
     */
    public String getSecurityCode() {
        return securityCode;
    }

    /**
     * This method sets securityCode.
     * 
     * @param securityCode
     */
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
            
    /**
     * This method gets the security.
     * 
     * @return security
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * This method sets the security.
     * 
     * @param security
     */
    public void setSecurity(Security security) {
        this.security = security;
    }
}
