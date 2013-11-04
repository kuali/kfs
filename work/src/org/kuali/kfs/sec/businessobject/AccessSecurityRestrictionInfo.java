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
package org.kuali.kfs.sec.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;


/**
 * Holds information regarding an access restriction that was found. Used by AccessSecurityService to provide information back when checking access
 */
public class AccessSecurityRestrictionInfo extends TransientBusinessObjectBase {
    private String securityAttributeName;
    private String propertyName;
    private String propertyLabel;
    private String retrictedValue;
    private String documentNumber;

    public AccessSecurityRestrictionInfo() {

    }

    /**
     * Gets the securityAttributeName attribute.
     * 
     * @return Returns the securityAttributeName.
     */
    public String getSecurityAttributeName() {
        return securityAttributeName;
    }


    /**
     * Sets the securityAttributeName attribute value.
     * 
     * @param securityAttributeName The securityAttributeName to set.
     */
    public void setSecurityAttributeName(String securityAttributeName) {
        this.securityAttributeName = securityAttributeName;
    }


    /**
     * Gets the propertyName attribute.
     * 
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }


    /**
     * Sets the propertyName attribute value.
     * 
     * @param propertyName The propertyName to set.
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }


    /**
     * Gets the retrictedValue attribute.
     * 
     * @return Returns the retrictedValue.
     */
    public String getRetrictedValue() {
        return retrictedValue;
    }


    /**
     * Sets the retrictedValue attribute value.
     * 
     * @param retrictedValue The retrictedValue to set.
     */
    public void setRetrictedValue(String retrictedValue) {
        this.retrictedValue = retrictedValue;
    }


    /**
     * Gets the propertyLabel attribute.
     * 
     * @return Returns the propertyLabel.
     */
    public String getPropertyLabel() {
        return propertyLabel;
    }

    /**
     * Sets the propertyLabel attribute value.
     * 
     * @param propertyLabel The propertyLabel to set.
     */
    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(SecPropertyConstants.SECURITY_ATTRIBUTE_NAME, this.securityAttributeName);

        return m;
    }

}
