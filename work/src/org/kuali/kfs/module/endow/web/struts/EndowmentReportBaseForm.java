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
package org.kuali.kfs.module.endow.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.util.KRADConstants;

public class EndowmentReportBaseForm extends KualiForm {

    protected String kemid;
    protected String benefittingOrganziationCampus;
    protected String benefittingOrganziationChart;
    protected String benefittingOrganziation;
    protected String typeCode;
    protected String purposeCode;
    protected String combineGroupCode;
    protected String endowmentOption;
    protected String listKemidsInHeader;
    protected String closedIndicator;
    protected String message;
    
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
    }
    
    /**
    * KRAD Conversion: Performs customization of the extra buttons.
    * 
    * No uses data dictionary.
    */
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
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (KRADConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && "printStatementPDF".equals(methodToCallParameterValue)) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }
    
    /**
     * Clears all the fields
     */
    public void clear() {
        this.kemid = null;
        this.benefittingOrganziationCampus = null;
        this.benefittingOrganziationChart = null;
        this.benefittingOrganziation = null;
        this.typeCode = null;
        this.purposeCode = null;
        this.combineGroupCode = null;
        this.endowmentOption = EndowConstants.EndowmentReport.BOTH;
        this.message = null;
    }

    public String getKemid() {
        return kemid;
    }

    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    public String getBenefittingOrganziationCampus() {
        return benefittingOrganziationCampus;
    }

    public void setBenefittingOrganziationCampus(String benefittingOrganziationCampus) {
        this.benefittingOrganziationCampus = benefittingOrganziationCampus;
    }

    public String getBenefittingOrganziationChart() {
        return benefittingOrganziationChart;
    }

    public void setBenefittingOrganziationChart(String benefittingOrganziationChart) {
        this.benefittingOrganziationChart = benefittingOrganziationChart;
    }

    public String getBenefittingOrganziation() {
        return benefittingOrganziation;
    }

    public void setBenefittingOrganziation(String benefittingOrganziation) {
        this.benefittingOrganziation = benefittingOrganziation;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getCombineGroupCode() {
        return combineGroupCode;
    }

    public void setCombineGroupCode(String combineGroupCode) {
        this.combineGroupCode = combineGroupCode;
    }

    public String getEndowmentOption() {
        return endowmentOption;
    }

    public void setEndowmentOption(String endowmentOption) {
        this.endowmentOption = endowmentOption;
    }

    public String getListKemidsInHeader() {
        return listKemidsInHeader;
    }

    public void setListKemidsInHeader(String listKemidsInHeader) {
        this.listKemidsInHeader = listKemidsInHeader;
    }

    public String getClosedIndicator() {
        return closedIndicator;
    }

    public void setClosedIndicator(String closedIndicator) {
        this.closedIndicator = closedIndicator;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
