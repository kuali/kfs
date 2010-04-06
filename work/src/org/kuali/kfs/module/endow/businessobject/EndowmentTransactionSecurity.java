/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public abstract class EndowmentTransactionSecurity extends PersistableBusinessObjectBase {

    private String securityId;
    private String registrationCode;
    private String securityLineTypeCode;

    private Security security;

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public String getSecurityId() {
        return securityId;
    }


    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;

    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;

    }

    /**
     * Gets the securityLineTypeCode attribute.
     * 
     * @return Returns the securityLineTypeCode.
     */
    public String getSecurityLineTypeCode() {
        return securityLineTypeCode;
    }

    /**
     * Sets the securityLineTypeCode attribute value.
     * 
     * @param securityLineTypeCode The securityLineTypeCode to set.
     */
    public void setSecurityLineTypeCode(String securityLineTypeCode) {
        this.securityLineTypeCode = securityLineTypeCode;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }


}
