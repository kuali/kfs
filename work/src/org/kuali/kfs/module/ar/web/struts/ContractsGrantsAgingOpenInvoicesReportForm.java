/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * Lookup form for ContractsGrantsAgingOpenInvoices Report.
 */
public class ContractsGrantsAgingOpenInvoicesReportForm extends LookupForm {

    private String htmlFormAction;
    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public ContractsGrantsAgingOpenInvoicesReportForm() {
        setHtmlFormAction("arContractsGrantsAgingOpenInvoicesReportLookup");
    }

    /**
     * Gets the htmlFormAction attribute.
     * 
     * @return Returns the htmlFormAction.
     */
    public String getHtmlFormAction() {
        return htmlFormAction;
    }

    /**
     * Sets the htmlFormAction attribute value.
     * 
     * @param htmlFormAction The htmlFormAction to set.
     */
    public void setHtmlFormAction(String htmlFormAction) {
        this.htmlFormAction = htmlFormAction;
    }


}
