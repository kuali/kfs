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
package org.kuali.kfs.fp.document.web.struts;

import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This class is the Struts form for the Cash Management Status page.
 */
public class CashManagementStatusForm extends KualiForm {
    String verificationUnit;
    String controllingDocumentId;
    String currentDrawerStatus;
    String desiredDrawerStatus;


    /**
     * Constructs a CashManagementStatusForm.
     */
    public CashManagementStatusForm() {
        super();
    }


    /**
     * @return current value of controllingDocumentId.
     */
    public String getControllingDocumentId() {
        return controllingDocumentId;
    }

    /**
     * Sets the controllingDocumentId attribute value.
     * 
     * @param controllingDocumentId The controllingDocumentId to set.
     */
    public void setControllingDocumentId(String controllingDocumentId) {
        this.controllingDocumentId = controllingDocumentId;
    }


    /**
     * @return current value of currentDrawerStatus.
     */
    public String getCurrentDrawerStatus() {
        return currentDrawerStatus;
    }

    /**
     * Sets the currentDrawerStatus attribute value.
     * 
     * @param currentDrawerStatus The currentDrawerStatus to set.
     */
    public void setCurrentDrawerStatus(String currentDrawerStatus) {
        this.currentDrawerStatus = currentDrawerStatus;
    }


    /**
     * @return current value of desiredDrawerStatus.
     */
    public String getDesiredDrawerStatus() {
        return desiredDrawerStatus;
    }

    /**
     * Sets the desiredDrawerStatus attribute value.
     * 
     * @param desiredDrawerStatus The desiredDrawerStatus to set.
     */
    public void setDesiredDrawerStatus(String desiredDrawerStatus) {
        this.desiredDrawerStatus = desiredDrawerStatus;
    }


    /**
     * @return current value of verificationUnit.
     */
    public String getVerificationUnit() {
        return verificationUnit;
    }

    /**
     * Sets the verificationUnit attribute value.
     * 
     * @param verificationUnit The verificationUnit to set.
     */
    public void setVerificationUnit(String verificationUnit) {
        this.verificationUnit = verificationUnit;
    }
}
