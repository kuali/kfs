/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import org.kuali.core.web.struts.form.KualiForm;

/**
 * This class is the Struts form for the Cash Management Status page.
 * 
 * @author Kuali Financial Transactions Team ()
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
