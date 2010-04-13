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
import java.util.List;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public abstract class EndowmentTransactionSecurityBase extends PersistableBusinessObjectBase implements  EndowmentTransactionSecurity{

    private String documentNumber;
    private String securityLineTypeCode;
    private String securityID;
    private String registrationCode;

    private Security security;
    private RegistrationCode registrationCodeObj;
    
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.TRANSACTION_SECURITY_DOCUMENT_NUMBER, this.documentNumber);
        m.put(EndowPropertyConstants.TRANSACTION_SECURITY_LINE_TYPE_CODE, this.securityLineTypeCode);
        return m;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#getDocumentNumber()
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#setDocumentNumber(java.lang.String)
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#getTransactionLineTypeCode()
     */
    public String getSecurityLineTypeCode() {
        return securityLineTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#setTransactionLineTypeCode(java.lang.String)
     */
    public void setSecurityLineTypeCode(String securityLineTypeCode) {
        this.securityLineTypeCode = securityLineTypeCode;
    }
    
    public String getSecurityID() {
        return securityID;
    }
    
    public void setSecurityID(String securityID ) {
        this.securityID = securityID;
    }
    
    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;

    }

    public RegistrationCode getRegistrationCodeObj() {
        return registrationCodeObj;
    }

    public void setRegistrationCodeObj(RegistrationCode registrationCodeObj) {
        this.registrationCodeObj = registrationCodeObj;

    }

}
