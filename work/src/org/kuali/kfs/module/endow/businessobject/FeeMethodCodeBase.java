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
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Base class that is used by Fee Method related objects
 */
public class FeeMethodCodeBase extends PersistableBusinessObjectBase {
    private static final Logger LOG = Logger.getLogger(FeeMethodCodeBase.class);
            
    private String feeMethodCode;
    private boolean include;
    
    private FeeMethod feeMethod;
    
    /**
     * Default constructor.
     */   
    public FeeMethodCodeBase() {
       super();
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.FEE_METHOD_CODE, this.feeMethodCode);
        return m;
        
    }

    /**
     * This method gets feeMethodCode
     * 
     * @return feeMethodCode
     */
    public String getFeeMethodCode() {
        return feeMethodCode;
    }

    /**
     * This method sets feeMethodCode.
     * 
     * @param feeMethod
     */
    public void setFeeMethodCode(String feeMethodCode) {
        this.feeMethodCode = feeMethodCode;
    }
        
        
    /**
     * Gets the feeMethod. 
     * @return Returns the feeMethod.
     */
    public FeeMethod getFeeMethod() {
        return feeMethod;
    }

    /**
     * Sets the FeeMethod.
     * @param FeeMethod The FeeMethod to set.
     */
    public void setFeeMethod(FeeMethod feeMethod) {
        this.feeMethod = feeMethod;
    }
    
    /**
     * Gets the include attribute. 
     * @return Returns the include.
     */
    public boolean getInclude() {
        return include;
    }

    /**
     * Sets the include attribute value.
     * @param include The include to set.
     */
    public void setInclude(boolean include) {
        this.include = include;
    }
}
