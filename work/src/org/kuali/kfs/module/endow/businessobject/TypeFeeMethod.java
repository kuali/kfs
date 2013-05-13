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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * Business Object for Type Fee Method table.
 */
public class TypeFeeMethod extends KualiCodeBase {
    private static final Logger LOG = Logger.getLogger(TypeFeeMethod.class);

    private String feeMethodCode;

    private TypeCode typeCode;
    private FeeMethod feeMethod;

    /**
     * Default constructor.
     */
    public TypeFeeMethod() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.TYPE_CODE, super.code);
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
     * @see org.kuali.rice.krad.bo.KualiCodeBase#getCodeAndDescription()
     */
    @Override
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return super.getCodeAndDescription();
    }

    /**
     * Gets the feeMethod.
     * 
     * @return Returns the feeMethod.
     */
    public FeeMethod getFeeMethod() {
        return feeMethod;
    }

    /**
     * Sets the FeeMethod.
     * 
     * @param FeeMethod The FeeMethod to set.
     */
    public void setFeeMethod(FeeMethod feeMethod) {
        this.feeMethod = feeMethod;
    }

    /**
     * This method gets the typeCode.
     * 
     * @return typeCode
     */
    public TypeCode getTypeCode() {
        return typeCode;
    }

    /**
     * This method sets the typeCode.
     * 
     * @param typeCode
     */
    public void setTypeCode(TypeCode typeCode) {
        this.typeCode = typeCode;
    }
}
