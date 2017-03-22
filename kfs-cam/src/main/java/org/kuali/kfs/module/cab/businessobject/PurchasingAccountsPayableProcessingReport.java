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
package org.kuali.kfs.module.cab.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class PurchasingAccountsPayableProcessingReport extends GeneralLedgerEntry {
    private Integer purapDocumentIdentifier;
    private KualiDecimal reportAmount;
    // Add invoice status field for CAB AP searching
    private String invoiceStatus;

    /**
     * Gets the purapDocumentIdentifier attribute. 
     * @return Returns the purapDocumentIdentifier.
     */
    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    /**
     * Sets the purapDocumentIdentifier attribute value.
     * @param purapDocumentIdentifier The purapDocumentIdentifier to set.
     */
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    /**
     * Gets the reportAmount attribute. 
     * @return Returns the reportAmount.
     */
    public KualiDecimal getReportAmount() {
        return reportAmount;
    }

    /**
     * Sets the reportAmount attribute value.
     * @param reportAmount The reportAmount to set.
     */
    public void setReportAmount(KualiDecimal reportAmount) {
        this.reportAmount = reportAmount;
    }

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}
    
}
