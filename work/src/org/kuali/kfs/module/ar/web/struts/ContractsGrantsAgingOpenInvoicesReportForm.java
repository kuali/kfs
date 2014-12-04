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

import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * Lookup form for ContractsGrantsAgingOpenInvoices Report.
 */
public class ContractsGrantsAgingOpenInvoicesReportForm extends LookupForm {

    private String htmlFormAction;

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
