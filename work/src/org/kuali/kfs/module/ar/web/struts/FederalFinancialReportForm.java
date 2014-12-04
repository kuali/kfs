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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Form class for Federal Financial Report service.
 */
public class FederalFinancialReportForm extends KualiForm {

    private String proposalNumber;
    private String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();
    private String reportingPeriod;
    private String federalForm;
    private String agencyNumber;
    private String error;

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber.
     */
    public String getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Gets the federalForm attribute.
     * 
     * @return Returns the federalForm.
     */
    public String getFederalForm() {
        return federalForm;
    }

    /**
     * Sets the federalForm attribute value.
     * 
     * @param federalForm The federalForm to set.
     */
    public void setFederalForm(String federalForm) {
        this.federalForm = federalForm;
    }

    /**
     * Gets the error attribute.
     * 
     * @return Returns the error.
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the error attribute value.
     * 
     * @param error The error to set.
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Gets the agency attribute.
     * 
     * @return Returns the agency.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agency attribute value.
     * 
     * @param agency The agency to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the fiscalYear attribute.
     * 
     * @return Returns the fiscalYear.
     */
    public String getFiscalYear() {
        return fiscalYear;
    }

    /**
     * Sets the fiscalYear attribute value.
     * 
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * Gets the reportingPeriod attribute.
     * 
     * @return Returns the reportingPeriod.
     */
    public String getReportingPeriod() {
        return reportingPeriod;
    }

    /**
     * Sets the reportingPeriod attribute value.
     * 
     * @param reportingPeriod The reportingPeriod to set.
     */
    public void setReportingPeriod(String reportingPeriod) {
        this.reportingPeriod = reportingPeriod;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
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

}
