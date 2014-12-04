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
