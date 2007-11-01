/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Aug 24, 2004
 */
package org.kuali.module.pdp.form.paymentmaintenance;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 */

public class PaymentMaintenanceForm extends ActionForm {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintenanceForm.class);

    private String changeText;
    private Integer changeId;
    private Integer paymentDetailId; // used for cancelling disbursements from EPIC
    private String action;

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        LOG.debug("Entered validate().");
        // create instance of ActionErrors to send errors to user
        ActionErrors actionErrors = new ActionErrors();
        String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);

        if (buttonPressed.startsWith("btnUpdateSave")) {
            if (GeneralUtilities.isStringEmpty(this.changeText)) {
                actionErrors.add("errors", new ActionMessage("paymentMaintenanceForm.changeText.empty"));
            }
            if (GeneralUtilities.isStringFieldAtMostNLength(this.changeText, 250)) {
                actionErrors.add("errors", new ActionMessage("paymentMaintenanceForm.changeText.over250"));
            }
        }
        LOG.debug("Exiting validate()  There were " + actionErrors.size() + " ActionMessages found.");
        return actionErrors;
    }

    /**
     * @return Returns the action.
     */
    public String getAction() {
        return action;
    }

    /**
     * @return Returns the changeText.
     */
    public String getChangeText() {
        return changeText;
    }

    /**
     * @return Returns the changeId.
     */
    public Integer getChangeId() {
        return changeId;
    }

    /**
     * @param action The action to set.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @param changeText The changeText to set.
     */
    public void setChangeText(String changeText) {
        this.changeText = changeText;
    }

    /**
     * @param detailId The detailId to set.
     */
    public void setChangeId(Integer changeId) {
        this.changeId = changeId;
    }

    /**
     * @return Returns the paymentDetailId.
     */
    public Integer getPaymentDetailId() {
        return paymentDetailId;
    }

    /**
     * @param paymentDetailId The paymentDetailId to set.
     */
    public void setPaymentDetailId(Integer paymentDetailId) {
        this.paymentDetailId = paymentDetailId;
    }
}
