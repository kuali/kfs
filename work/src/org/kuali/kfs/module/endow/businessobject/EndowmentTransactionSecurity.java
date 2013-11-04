/*
 * Copyright 2010 The Kuali Foundation.
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

