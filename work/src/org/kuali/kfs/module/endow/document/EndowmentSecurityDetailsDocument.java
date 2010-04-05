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
package org.kuali.kfs.module.endow.document;

public interface EndowmentSecurityDetailsDocument extends EndowmentTransactionLinesDocument {

    /**
     * Gets the security Id.
     * 
     * @return the security id
     */
    public String getSecurityId();

    /**
     * Sets the security Id.
     * 
     * @param securityId
     */
    public void setSecurityId(String securityId);

    /**
     * Gets the security class code.
     * 
     * @return the security class code
     */
    public String getSecurityClassCode();

    /**
     * Sets the security class code.
     * 
     * @param securityClassCode
     */
    public void setSecurityClassCode(String securityClassCode);

    /**
     * Gets the security transaction code.
     * 
     * @return the security transaction code
     */
    public String getSecurityTransactionCode();

    /**
     * Sets the security transaction code.
     * 
     * @param securityTransactionCode
     */
    public void setSecurityTransactionCode(String securityTransactionCode);

    /**
     * Gets the security tax lot indicator.
     * 
     * @return the security tax lot indicator
     */
    public String getSecurityTaxLotIndicator();

    /**
     * Sets the security tax lot indicator.
     * 
     * @param securityTaxLotIndicator
     */
    public void setSecurityTaxLotIndicator(String securityTaxLotIndicator);

    /**
     * Gets the registration code.
     * 
     * @return the registration code
     */
    public String getRegistrationCode();

    /**
     * Sets the security registration code.
     * 
     * @param securityRegistrationCode
     */
    public void setRegistrationCode(String securityRegistrationCode);
}
