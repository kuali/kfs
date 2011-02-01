/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.ar.web.struts.CustomerStatementForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.ui.ExtraButton;

public class TrialBalanceForm extends KualiForm {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TrialBalanceForm.class);
    
    private String kemid;
    private String benefittingOrganziationCampus;
    private String benefittingOrganziationChart;
    private String benefittingOrganziation;
    private String typeCode;
    private String purposeCode;
    private String combineGroupCode;
    private String asOfDate;
    private String endowmnetOption;
    private String message;
    
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
    }
    
    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> buttons = new ArrayList<ExtraButton>();
     //   HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();
       
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
        if (KNSConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && "printStatementPDF".equals(methodToCallParameterValue)) {
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
        this.asOfDate = null;
        this.endowmnetOption = null;
        this.message = null;
    }

    /**
     * 
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * 
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * 
     */
    public String getBenefittingOrganziationCampus() {
        return benefittingOrganziationCampus;
    }

    /**
     * 
     */
    public void setBenefittingOrganziationCampus(String benefittingOrganziationCampus) {
        this.benefittingOrganziationCampus = benefittingOrganziationCampus;
    }

    /**
     * 
     */
    public String getBenefittingOrganziationChart() {
        return benefittingOrganziationChart;
    }

    /**
     * 
     */
    public void setBenefittingOrganziationChart(String benefittingOrganziationChart) {
        this.benefittingOrganziationChart = benefittingOrganziationChart;
    }

    /**
     * 
     */
    public String getBenefittingOrganziation() {
        return benefittingOrganziation;
    }

    /**
     * 
     */
    public void setBenefittingOrganziation(String benefittingOrganziation) {
        this.benefittingOrganziation = benefittingOrganziation;
    }

    /**
     * 
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * 
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * 
     */
    public String getPurposeCode() {
        return purposeCode;
    }

    /**
     * 
     */
    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    /**
     * 
     */
    public String getCombineGroupCode() {
        return combineGroupCode;
    }

    /**
     * 
     */
    public void setCombineGroupCode(String combineGroupCode) {
        this.combineGroupCode = combineGroupCode;
    }

    /**
     * 
     */
    public String getAsOfDate() {
        return asOfDate;
    }

    /**
     * 
     */
    public void setAsOfDate(String asOfDate) {
        this.asOfDate = asOfDate;
    }

    /**
     * 
     */
    public String getEndowmnetOption() {
        return endowmnetOption;
    }

    /**
     * 
     */
    public void setEndowmnetOption(String endowmnetOption) {
        this.endowmnetOption = endowmnetOption;
    }

    /**
     * 
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
}
