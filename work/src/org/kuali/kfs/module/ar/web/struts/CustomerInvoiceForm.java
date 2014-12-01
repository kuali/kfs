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
package org.kuali.kfs.module.ar.web.struts;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * This class is the action form for Customer Aging Reports.
 */
public class CustomerInvoiceForm extends KualiForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CustomerInvoiceForm.class);

    private String chartCode;
    private String orgCode;
    private String orgType;
    private Date runDate;
    private String message;
    private String userId;

    /**
     * Gets the userId attribute. 
     * @return Returns the userId.
     */
    public String getUserId() {
        return userId;
    }




    /**
     * Sets the userId attribute value.
     * @param userId The userId to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }




    /**
     * Gets the message attribute. 
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }




    /**
     * Sets the message attribute value.
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }




    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> buttons = new ArrayList<ExtraButton>();

        // Print button
        ExtraButton printButton = new ExtraButton();
        printButton.setExtraButtonProperty("methodToCall.print");
        printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_genprintfile.gif");
        printButton.setExtraButtonAltText("Print");
        buttons.add(printButton);

        // Clear button
        ExtraButton clearButton = new ExtraButton();
        clearButton.setExtraButtonProperty("methodToCall.clear");
        clearButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_clear.gif");
        clearButton.setExtraButtonAltText("Clear");
        buttons.add(clearButton);

        // Cancel button
        ExtraButton cancelButton = new ExtraButton();
        cancelButton.setExtraButtonProperty("methodToCall.cancel");
        cancelButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_cancel.gif");
        cancelButton.setExtraButtonAltText("Cancel");
        buttons.add(cancelButton);

        return buttons;
    }




    
    /**
     * Gets the runDate attribute. 
     * @return Returns the runDate.
     */
    public Date getRunDate() {
        return runDate;
    }



    /**
     * Sets the runDate attribute value.
     * @param runDate The runDate to set.
     */
    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }


    /**
     * Gets the orgType attribute. 
     * @return Returns the orgType.
     */
    public String getOrgType() {
        return orgType;
    }



    /**
     * Sets the orgType attribute value.
     * @param orgType The orgType to set.
     */
    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }



    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
    }



    /**
     * Gets the chartCode attribute. 
     * @return Returns the chartCode.
     */
    public String getChartCode() {
        return chartCode;
    }



    /**
     * Sets the chartCode attribute value.
     * @param chartCode The chartCode to set.
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }



    /**
     * Gets the orgCode attribute. 
     * @return Returns the orgCode.
     */
    public String getOrgCode() {
        return orgCode;
    }



    /**
     * Sets the orgCode attribute value.
     * @param orgCode The orgCode to set.
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }




}
