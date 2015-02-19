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
