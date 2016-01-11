/*
 * Copyright 2009 The Kuali Foundation.
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
package edu.arizona.kfs.sys.identity;

import org.kuali.kfs.coa.businessobject.ObjectSubType;

public class ArizonaKfsKimAttributes extends org.kuali.kfs.sys.identity.KfsKimAttributes {
    public static final String OBJECT_SUB_TYPE_CODE = "financialObjectSubTypeCode";
        
    protected String financialObjectSubTypeCode;
    protected ObjectSubType objectSubType;

    /**
     * Gets the financialObjectSubTypeCode attribute. 
     * @return Returns the financialObjectSubTypeCode.
     */
    public String getFinancialObjectSubTypeCode() {
        return financialObjectSubTypeCode;
    }

    /**
     * Sets the financialObjectSubTypeCode attribute value.
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }

    /**
     * Gets the objectSubType attribute. 
     * @return Returns the objectSubType.
     */
    public ObjectSubType getObjectSubType() {
        return objectSubType;
    }

    /**
     * Sets the objectSubType attribute value.
     * @param objectSubType The objectSubType to set.
     */
    public void setObjectSubType(ObjectSubType objectSubType) {
        this.objectSubType = objectSubType;
    }
}

