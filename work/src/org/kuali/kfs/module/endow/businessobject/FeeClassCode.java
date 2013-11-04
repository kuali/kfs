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
 * Business Object for Fee Class Code.
 */
public class FeeClassCode extends FeeMethodCodeBase {
    private static final Logger LOG = Logger.getLogger(FeeClassCode.class);
    
    private String feeClassCode;
    
    private ClassCode classCode;
    
    /**
     * Default constructor.
     */   
    public FeeClassCode() {
       super();
    }
   
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.FEE_METHOD_CODE, super.getFeeMethodCode());
        m.put(EndowPropertyConstants.FEE_CLASS_CODE, this.getFeeClassCode());        
        return m;
        
    }

    /**
     * This method gets feeClassCode
     * 
     * @return feeClassCode
     */
    public String getFeeClassCode() {
        return feeClassCode;
    }

    /**
     * This method sets feeClassCode.
     * 
     * @param feeClassCode
     */
    public void setFeeClassCode(String feeClassCode) {
        this.feeClassCode = feeClassCode;
    }
        
    /**
     * This method gets the classCode.
     * 
     * @return classCode
     */
    public ClassCode getClassCode() {
        return classCode;
    }

    /**
     * This method sets the classCode.
     * 
     * @param classCode
     */
    public void setClassCode(ClassCode classCode) {
        this.classCode = classCode;
    }
}
