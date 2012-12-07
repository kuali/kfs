/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.integration.ar;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
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

    /**
     * Gets the paymentAccount attribute.
     *
     * @return Returns the paymentAccount.
     */
    public abstract Account getPaymentAccount();

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

    public void setPaymentChartOfAccountsCode(String processingChartCode);

    public void setPaymentOrganizationReferenceIdentifier(String processingOrgCode);

    public String getBillByChartOfAccountCode();

    public String getBilledByOrganizationCode();

    public void setPrintInvoiceIndicator(String printInvoiceIndicator);

    public void setInvoiceTermsText(String organizationPaymentTermsText);

    public void addSourceAccountingLine(SourceAccountingLine detail);

    public void setCustomerInvoiceRecurrenceDetails(AccountsReceivableCustomerInvoiceRecurrenceDetails recurrenceDetails);

    public void setAccountsReceivableDocumentHeader(org.kuali.kfs.integration.ar.AccountsReceivableDocumentHeader accountsReceivableDocumentHeader);

}