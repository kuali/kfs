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
package org.kuali.kfs.integration.ar;

import java.sql.Date;

import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.DocumentHeader;


public interface AccountsReceivableCustomerInvoice {

    /**
     * This method calculates the outstanding balance on an invoice.
     *
     * @return the outstanding balance on this invoice
     */
    public abstract KualiDecimal getOpenAmount();

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public abstract String getDocumentNumber();

    /**
     * Gets the invoiceAttentionLineText attribute.
     *
     * @return Returns the invoiceAttentionLineText
     */
    public abstract String getInvoiceAttentionLineText();

    /**
     * Gets the invoiceDueDate attribute.
     *
     * @return Returns the invoiceDueDate
     */
    public abstract Date getInvoiceDueDate();

    /**
     * Gets the billingDate attribute.
     *
     * @return Returns the billingDate
     */
    public abstract Date getBillingDate();

    /**
     * This method returns the age of an invoice (i.e. current date - billing date)
     *
     * @return
     */
    public abstract Integer getAge();

    /**
     * Gets the invoiceTermsText attribute.
     *
     * @return Returns the invoiceTermsText
     */
    public abstract String getInvoiceTermsText();

    /**
     * Gets the openInvoiceIndicator attribute.
     *
     * @return Returns the openInvoiceIndicator
     */
    public abstract boolean isOpenInvoiceIndicator();

    public abstract KualiDecimal getTotalDollarAmount();

    public abstract String getCustomerName();

    public void setBillByChartOfAccountCode(String billByChartOfAccountCode);

    public void setBilledByOrganizationCode(String billedByOrganizationCode);

    public void setOpenInvoiceIndicator(boolean openInvoiceIndicator);

    public void setCustomerBillToAddressIdentifier(Integer customerBillToAddressIdentifier);

    public void setCustomerBillToAddress(AccountsReceivableCustomerAddress customerBillToAddress);

    public void setBillingAddressName(String customerName);

    public void setBillingLine2StreetAddress(String customerLine2StreetAddress);

    public void setBillingLine1StreetAddress(String customerLine1StreetAddress);

    public void setBillingCityName(String customerCityName);

    public void setBillingStateCode(String customerStateCode);

    public void setBillingZipCode(String customerZipCode);

    public void setBillingCountryCode(String customerCountryCode);

    public void setBillingAddressInternationalProvinceName(String customerAddressInternationalProvinceName);

    public void setBillingInternationalMailCode(String customerInternationalMailCode);

    public void setBillingEmailAddress(String customerEmailAddress);

    public void setBillingAddressTypeCodeAsPrimary();

    public DocumentHeader getDocumentHeader();

    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader();

    public void setBillingDate(Date date);

    public void setInvoiceDueDate(Date date);

    public void setOrganizationInvoiceNumber(String string);

    public String getBillByChartOfAccountCode();

    public String getBilledByOrganizationCode();

    public void setPrintInvoiceIndicator(String printInvoiceIndicator);

    public void setInvoiceTermsText(String organizationPaymentTermsText);

    public void addSourceAccountingLine(SourceAccountingLine detail);

    public void setCustomerInvoiceRecurrenceDetails(AccountsReceivableCustomerInvoiceRecurrenceDetails recurrenceDetails);

    public void setAccountsReceivableDocumentHeader(org.kuali.kfs.integration.ar.AccountsReceivableDocumentHeader accountsReceivableDocumentHeader);

}
