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
