/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kns.web.struts.form.KualiForm;

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

    public void clear() {
        this.accountNumber = null;
        this.customerNumber = null;
        this.chartCode = null;
        this.orgCode = null;
    }


}