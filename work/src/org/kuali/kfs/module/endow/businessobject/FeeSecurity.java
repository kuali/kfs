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
