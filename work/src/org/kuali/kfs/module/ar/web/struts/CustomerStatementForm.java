/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class is the action form for Customer Aging Reports.
 */
public class CustomerStatementForm extends KualiForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CustomerStatementForm.class);

    private String chartCode;
    private String orgCode;
    private String accountNumber;
    private String customerNumber;
    private String statementFormat;
    private String statementFormatOption;
    private String includeZeroBalanceCustomers;
    private String message;  

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

    /**
     * Gets the customerNumber attribute. 
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the accountNumber attribute. 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    /**
     * Gets the satementFormat attribute
     */
    public String getStatementFormat() {
        return statementFormat;
    }

    /**
     * Sets the satementFormat attribute
     * @return statementFormat
     */
    public void setStatementFormat(String statementFormat) {
        this.statementFormat = statementFormat;
    }

    /**
     * Gets the incldueZeroBalanceCustomer attribute
     */
    public String getIncludeZeroBalanceCustomers() {
        return this.includeZeroBalanceCustomers;
    }

    /**
     * Sets the incldueZeroBalanceCustomer attribute
     * @return incldueZeroBalanceCustomer
     */
    public void setIncludeZeroBalanceCustomers(String includeZeroBalanceCustomers) {
        this.includeZeroBalanceCustomers = includeZeroBalanceCustomers;
    }

    /**
     * @return the statementFormatOption
     */
    public String getStatementFormatOption() {
        return statementFormatOption;
    }

    /**
     * @param statementFormatOption the statementFormatOption to set
     */
    public void setStatementFormatOption(String statementFormatOption) {
        this.statementFormatOption = statementFormatOption;
    }

    public void clear() {
        this.accountNumber = null;
        this.customerNumber = null;
        this.chartCode = null;
        this.orgCode = null;
        this.statementFormat = null;
        this.includeZeroBalanceCustomers = null;
        this.message = null;
    }

    /**
    * KRAD Conversion: creates extra buttons.
    * 
    * No use of data dictionary.
    */
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
        if (KRADConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && "printStatementPDF".equals(methodToCallParameterValue)) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

}
