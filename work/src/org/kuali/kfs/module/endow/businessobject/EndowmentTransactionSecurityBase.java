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

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public abstract class EndowmentTransactionSecurityBase extends PersistableBusinessObjectBase implements  EndowmentTransactionSecurity{

    private String documentNumber;
    private String securityLineTypeCode;
    private String securityID;
    private String registrationCode;

    private Security security;
    private RegistrationCode registrationCodeObj;
    
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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
    
    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#getSecurityID()
     */
    public String getSecurityID() {
        return securityID;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#setSecurityID(java.lang.String)
     */
    public void setSecurityID(String securityID ) {
        this.securityID = securityID;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#getSecurity()
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#setSecurity(org.kuali.kfs.module.endow.businessobject.Security)
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#getRegistrationCode()
     */
    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#setRegistrationCode(java.lang.String)
     */
    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;

    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#getRegistrationCodeObj()
     */
    public RegistrationCode getRegistrationCodeObj() {
        return registrationCodeObj;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity#setRegistrationCodeObj(org.kuali.kfs.module.endow.businessobject.RegistrationCode)
     */
    public void setRegistrationCodeObj(RegistrationCode registrationCodeObj) {
        this.registrationCodeObj = registrationCodeObj;

    }

}
