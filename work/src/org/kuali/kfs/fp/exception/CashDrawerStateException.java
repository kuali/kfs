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
