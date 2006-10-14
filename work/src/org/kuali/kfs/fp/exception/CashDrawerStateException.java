/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.financial.exceptions;


public class CashDrawerStateException extends RuntimeException {
    private final String verificationUnit;
    private final String controllingDocumentId;
    private final String currentDrawerStatus;
    private final String desiredDrawerStatus;


    public CashDrawerStateException(String verificationUnit, String controllingDocumentId, String currentDrawerStatus, String desiredDrawerStatus) {
        this.verificationUnit = verificationUnit;
        this.controllingDocumentId = controllingDocumentId;
        this.currentDrawerStatus = currentDrawerStatus;
        this.desiredDrawerStatus = desiredDrawerStatus;
    }


    /**
     * @return current value of verificationUnit.
     */
    public String getVerificationUnit() {
        return verificationUnit;
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
}
