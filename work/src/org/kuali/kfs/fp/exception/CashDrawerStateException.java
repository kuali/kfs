/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.exception;

import java.util.Properties;

import org.kuali.rice.core.api.exception.KualiException;



public class CashDrawerStateException extends KualiException {
    private final String campusCode;
    private final String controllingDocumentId;
    private final String currentDrawerStatus;
    private final String desiredDrawerStatus;
    
    private final static String CASH_DRAWER_STATE_EXCEPTION_SESSION_KEY = "CASH_DRAWER_STATE_EXCEPTION";


    public CashDrawerStateException(String campusCode, String controllingDocumentId, String currentDrawerStatus, String desiredDrawerStatus) {
        super("Cash Drawer State Exception; this exception should simply serve to redirect the page to the Cash Drawer Status page");
        this.campusCode = campusCode;
        this.controllingDocumentId = controllingDocumentId;
        this.currentDrawerStatus = currentDrawerStatus;
        this.desiredDrawerStatus = desiredDrawerStatus;
    }


    /**
     * @return current value of campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * @return current value of currentDrawerStatus.
     */
    public String getCurrentDrawerStatus() {
        return currentDrawerStatus;
    }

    /**
     * @return current value of desiredDrawerStatus.
     */
    public String getDesiredDrawerStatus() {
        return desiredDrawerStatus;
    }

    /**
     * @return current value of controllingDocumentId.
     */
    public String getControllingDocumentId() {
        return controllingDocumentId;
    }
    
    /**
     * Creates a Properties object, based on the properties in this exception
     * @return a Properties object
     */
    public Properties toProperties() {
        Properties properties = new Properties();
        properties.setProperty("verificationUnit", getCampusCode());
        properties.setProperty("controllingDocumentId", getControllingDocumentId());
        properties.setProperty("currentDrawerStatus", getCurrentDrawerStatus());
        properties.setProperty("desiredDrawerStatus", getDesiredDrawerStatus());
        properties.setProperty("methodToCall", "displayPage");
        return properties;
    }
}
