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

import org.kuali.rice.krad.bo.PersistableBusinessObject;

public interface EndowmentTransactionSecurity extends PersistableBusinessObject {

    /**
     * Get the documentNumber
     * 
     * @return documentNumber
     */
    public String getDocumentNumber();
    
    /**
     * Set the documentNumber
     * 
     * @param documentNumber
     */
    public void setDocumentNumber (String documentNumber);
    
    /**
     * Gets the securityLineTypeCode.
     * 
     * @return securityLineTypeCode
     */
    public String getSecurityLineTypeCode();
    
    /**
     *Sets the securityLineTypeCode.
     * 
     * @param securityLineTypeCode
     */
    public void setSecurityLineTypeCode(String securityLineTypeCode);
        
    /**
     * @return Returns the securityID.
     */
    public String getSecurityID();

    /**
     * @param securityID The securityID to set.
     */
    public void setSecurityID(String securityID);

    /**
     * @return Returns the security object.
     */
    public Security getSecurity();    
    
    /**
     * @param security The security object to set.
     */
    public void setSecurity(Security security);
        
    /**
     * @return Returns the registrationCode.
     */
    public String getRegistrationCode();

    /**
     * @param registrationCode The registrationCode to set.
     */
    public void setRegistrationCode(String registrationCode);

    /**
     * @return Returns the registrationCode object.
     */
    public RegistrationCode getRegistrationCodeObj();    
    
    /**
     * @param registrationCodeObj The registrationCode object to set.
     */
    public void setRegistrationCodeObj(RegistrationCode registrationCodeObj);
    

}

