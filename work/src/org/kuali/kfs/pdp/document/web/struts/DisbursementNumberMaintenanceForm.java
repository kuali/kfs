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
package org.kuali.module.pdp.form.disbursementnumbermaintenance;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.utilities.DateHandler;
import org.kuali.module.pdp.utilities.GeneralUtilities;

public class DisbursementNumberMaintenanceForm extends ActionForm {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementNumberMaintenanceForm.class);

    private Integer id; // DISB_NBR_RANGE_ID
    private String physCampusProcCode; // PHYS_CMP_PROC_CD
    private String beginDisbursementNbr; // BEG_DISB_NBR
    private String lastAssignedDisbNbr; // LST_ASND_DISB_NBR
    private String endDisbursementNbr; // END_DISB_NBR
    private String disbNbrEffectiveDt; // DISB_NBR_EFF_DT
    private String disbNbrExpirationDt; // DISB_NBR_EXPR_DT
    private Timestamp lastUpdate; // LST_UPDT_TS
    private PdpUser lastUpdateUser;
    private String lastUpdateUserId; // LST_UPDT_USR_ID
    private Integer version; // VER_NBR
    private Integer bankId;

    public DisbursementNumberMaintenanceForm() {

    }

    public DisbursementNumberMaintenanceForm(DisbursementNumberRange dnr) {
        setForm(dnr);
    }

    public void setForm(DisbursementNumberRange dnr) {
        this.setBankId(dnr.getBank().getId());
        this.setBeginDisbursementNbr(GeneralUtilities.convertIntegerToString(dnr.getBeginDisbursementNbr()));
        this.setDisbNbrEffectiveDt(GeneralUtilities.convertDateToString(dnr.getDisbNbrEffectiveDt()));
        this.setDisbNbrExpirationDt(GeneralUtilities.convertDateToString(dnr.getDisbNbrExpirationDt()));
        this.setEndDisbursementNbr(GeneralUtilities.convertIntegerToString(dnr.getEndDisbursementNbr()));
        this.setId(dnr.getId());
        this.setLastAssignedDisbNbr(GeneralUtilities.convertIntegerToString(dnr.getLastAssignedDisbNbr()));
        this.setLastUpdate(dnr.getLastUpdate());
        this.setLastUpdateUser(dnr.getLastUpdateUser());
        this.setLastUpdateUserId(dnr.getLastUpdateUserId());
        this.setPhysCampusProcCode(dnr.getPhysCampusProcCode());
        this.setVersion(dnr.getVersion());
    }

    public DisbursementNumberRange getDisbursementNumberRange() {
        DisbursementNumberRange dnr = new DisbursementNumberRange();
        dnr.setBeginDisbursementNbr(GeneralUtilities.convertStringToInteger(this.getBeginDisbursementNbr()));
        try {
            dnr.setDisbNbrEffectiveDt(DateHandler.makeStringTimestamp(this.getDisbNbrEffectiveDt()));
        }
        catch (Exception e) {
            // Form Validation Nullifies this
        }
        try {
            dnr.setDisbNbrExpirationDt(DateHandler.makeStringTimestamp(this.getDisbNbrExpirationDt()));
        }
        catch (Exception e) {
            // Form Validation Nullifies this
        }
        dnr.setEndDisbursementNbr(GeneralUtilities.convertStringToInteger(this.getEndDisbursementNbr()));
        dnr.setId(this.getId());
        dnr.setLastAssignedDisbNbr(GeneralUtilities.convertStringToInteger(this.getLastAssignedDisbNbr()));
        dnr.setLastUpdate(this.getLastUpdate());
        dnr.setLastUpdateUser(this.getLastUpdateUser());
        dnr.setLastUpdateUserId(this.getLastUpdateUserId());
        dnr.setPhysCampusProcCode(this.getPhysCampusProcCode().toUpperCase());
        dnr.setVersion(this.getVersion());

        return dnr;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        LOG.debug("validate() started");

        ActionErrors actionErrors = new ActionErrors();

        String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);
        if (buttonPressed.startsWith("btnSave")) {
            // check for bank radio button being selected
            if (this.getBankId() == null) {
                actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.bankId.missing"));
            }
            // Check for validity of the Dates entered
            if (!GeneralUtilities.isStringEmpty(this.getDisbNbrEffectiveDt())) {
                actionErrors = DateHandler.validDate(actionErrors, "errors", this.getDisbNbrEffectiveDt());
            }
            else {
                actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.disbNbrEffectiveDt.missing"));
            }
            if (!GeneralUtilities.isStringEmpty(this.getDisbNbrExpirationDt())) {
                actionErrors = DateHandler.validDate(actionErrors, "errors", this.getDisbNbrExpirationDt());
            }
            else {
                actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.disbNbrExpirationDt.missing"));
            }

            // Check that the Disbursement Numbers are Integers
            int begin = 0;
            int end = 0;
            int last = 0;

            try {
                beginDisbursementNbr = beginDisbursementNbr.trim();
                begin = Integer.parseInt(beginDisbursementNbr);
                if ( begin <= 0 ) {
                    actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.beginDisbursementNbr.invalid"));
                }
            } catch (NumberFormatException nfe) {
                actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.beginDisbursementNbr.invalid"));
            }
            try {
                endDisbursementNbr = endDisbursementNbr.trim();
                end = Integer.parseInt(endDisbursementNbr);
                if ( end <= 0 ) {
                    actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.endDisbursementNbr.invalid"));
                }
            } catch (NumberFormatException nfe) {
                actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.endDisbursementNbr.invalid"));
            }
            lastAssignedDisbNbr = lastAssignedDisbNbr.trim();
            if ( ! GeneralUtilities.isStringEmpty(this.getLastAssignedDisbNbr())) {
                try {
                    last = Integer.parseInt(lastAssignedDisbNbr);
                    if ( last <= 0 ) {
                        actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.lastAssignedDisburseNbr.invalid"));
                    }
                } catch (NumberFormatException nfe) {
                    actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.lastAssignedDisburseNbr.invalid"));
                }
            } else {
                last = begin;
                lastAssignedDisbNbr = beginDisbursementNbr;
            }
            if ( actionErrors.size() == 0 ) {
                if ( end < begin ) {
                    actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.endAssignedDisburseNbr.smaller"));
                }
                if ( (last < begin) || (last > end) ) {
                    actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.lastAssignedDisburseNbr.outofrange"));
                }
            }
            if (GeneralUtilities.isStringEmpty(this.getPhysCampusProcCode())) {
                actionErrors.add("errors", new ActionMessage("DisbursementNumberMaintenanceForm.physCampusProcCode.invalid"));
            }
        }

        LOG.debug("validate() There were " + actionErrors.size() + " ActionMessages found.");
        return actionErrors;
    }

    /**
     * @return
     */
    public void clearForm() {

        this.setBankId(null);
        this.setBeginDisbursementNbr(null);
        this.setDisbNbrEffectiveDt(null);
        this.setDisbNbrExpirationDt(null);
        this.setEndDisbursementNbr(null);
        this.setId(null);
        this.setLastAssignedDisbNbr(null);
        this.setLastUpdate(null);
        this.setLastUpdateUser(null);
        this.setLastUpdateUserId(null);
        this.setPhysCampusProcCode(null);
        this.setVersion(null);

    }

    /**
     * @return Returns the bank.
     */
    public Integer getBankId() {
        return bankId;
    }

    /**
     * @return Returns the beginDisbursementNbr.
     */
    public String getBeginDisbursementNbr() {
        return beginDisbursementNbr;
    }

    /**
     * @return Returns the disbNbrEffectiveDt.
     */
    public String getDisbNbrEffectiveDt() {
        return disbNbrEffectiveDt;
    }

    /**
     * @return Returns the disbNbrExpirationDt.
     */
    public String getDisbNbrExpirationDt() {
        return disbNbrExpirationDt;
    }

    /**
     * @return Returns the endDisbursementNbr.
     */
    public String getEndDisbursementNbr() {
        return endDisbursementNbr;
    }

    /**
     * @return Returns the id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return Returns the lastAssignedDisbNbr.
     */
    public String getLastAssignedDisbNbr() {
        return lastAssignedDisbNbr;
    }

    /**
     * @return Returns the lastUpdate.
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return Returns the lastUpdateUser.
     */
    public PdpUser getLastUpdateUser() {
        return lastUpdateUser;
    }

    /**
     * @return Returns the lastUpdateUserId.
     */
    public String getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    /**
     * @return Returns the physCampusProcCode.
     */
    public String getPhysCampusProcCode() {
        return physCampusProcCode;
    }

    /**
     * @return Returns the version.
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param bank The bank to set.
     */
    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    /**
     * @param beginDisbursementNbr The beginDisbursementNbr to set.
     */
    public void setBeginDisbursementNbr(String beginDisbursementNbr) {
        this.beginDisbursementNbr = beginDisbursementNbr;
    }

    /**
     * @param disbNbrEffectiveDt The disbNbrEffectiveDt to set.
     */
    public void setDisbNbrEffectiveDt(String disbNbrEffectiveDt) {
        this.disbNbrEffectiveDt = disbNbrEffectiveDt;
    }

    /**
     * @param disbNbrExpirationDt The disbNbrExpirationDt to set.
     */
    public void setDisbNbrExpirationDt(String disbNbrExpirationDt) {
        this.disbNbrExpirationDt = disbNbrExpirationDt;
    }

    /**
     * @param endDisbursementNbr The endDisbursementNbr to set.
     */
    public void setEndDisbursementNbr(String endDisbursementNbr) {
        this.endDisbursementNbr = endDisbursementNbr;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @param lastAssignedDisbNbr The lastAssignedDisbNbr to set.
     */
    public void setLastAssignedDisbNbr(String lastAssignedDisbNbr) {
        this.lastAssignedDisbNbr = lastAssignedDisbNbr;
    }

    /**
     * @param lastUpdate The lastUpdate to set.
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @param lastUpdateUser The lastUpdateUser to set.
     */
    public void setLastUpdateUser(PdpUser lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    /**
     * @param lastUpdateUserId The lastUpdateUserId to set.
     */
    public void setLastUpdateUserId(String lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    /**
     * @param physCampusProcCode The physCampusProcCode to set.
     */
    public void setPhysCampusProcCode(String physCampusProcCode) {
        this.physCampusProcCode = physCampusProcCode;
    }

    /**
     * @param version The version to set.
     */
    public void setVersion(Integer version) {
        this.version = version;
    }
}
