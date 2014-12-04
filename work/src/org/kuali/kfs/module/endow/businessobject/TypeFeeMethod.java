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
