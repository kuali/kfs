/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent an invoice address detail business object.
 */
public class InvoiceAddressDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String customerNumber;
    private Integer customerAddressIdentifier;
    private String customerAddressTypeCode;
    private String customerAddressName;
    private String preferredCustomerInvoiceTemplateCode;
    private String customerInvoiceTemplateCode;
    private String invoiceIndicatorCode;
    private String preferredInvoiceIndicatorCode;
    private long noteId;

    private CustomerAddress customerAddress;

    /**
     * Default constructor.
     */
    public InvoiceAddressDetail() {

    }

    /**
     * Gets the invoiceIndicatorCode attribute.
     *
     * @return Returns the invoiceIndicatorCode.
     */
    public String getInvoiceIndicatorCode() {
        return invoiceIndicatorCode;
    }

    /**
     * Gets the noteId attribute.
     *
     * @return Returns the noteId.
     */
    public long getNoteId() {
        return noteId;
    }

    /**
     * Sets the noteId attribute value.
     *
     * @param noteId The noteId to set.
     */
    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    /**
     * Sets the invoiceIndicatorCode attribute value.
     *
     * @param invoiceIndicatorCode The invoiceIndicatorCode to set.
     */
    public void setInvoiceIndicatorCode(String invoiceIndicatorCode) {
        this.invoiceIndicatorCode = invoiceIndicatorCode;
    }

    /**
     * Gets the preferredInvoiceIndicatorCode attribute.
     *
     * @return Returns the preferredInvoiceIndicatorCode.
     */
    public String getPreferredInvoiceIndicatorCode() {
        return preferredInvoiceIndicatorCode;
    }

    /**
     * Sets the preferredInvoiceIndicatorCode attribute value.
     *
     * @param preferredInvoiceIndicatorCode The preferredInvoiceIndicatorCode to set.
     */
    public void setPreferredInvoiceIndicatorCode(String preferredInvoiceIndicatorCode) {
        this.preferredInvoiceIndicatorCode = preferredInvoiceIndicatorCode;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     *
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public CustomerAddress getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
    }

    /**
     * Gets the customerAddressIdentifier attribute.
     *
     * @return Returns the customerAddressIdentifier
     */
    public Integer getCustomerAddressIdentifier() {
        return customerAddressIdentifier;
    }

    /**
     * Sets the customerAddressIdentifier attribute.
     *
     * @param customerAddressIdentifier The customerAddressIdentifier to set.
     */
    public void setCustomerAddressIdentifier(Integer customerAddressIdentifier) {
        this.customerAddressIdentifier = customerAddressIdentifier;
    }

    public String getCustomerAddressTypeCode() {
        return customerAddressTypeCode;
    }

    public void setCustomerAddressTypeCode(String customerAddressTypeCode) {
        this.customerAddressTypeCode = customerAddressTypeCode;
    }

    /**
     * Gets the customerAddressName attribute.
     *
     * @return Returns the customerAddressName
     */
    public String getCustomerAddressName() {
        return customerAddressName;
    }

    /**
     * Sets the customerAddressName attribute.
     *
     * @param customerAddressName The customerAddressName to set.
     */
    public void setCustomerAddressName(String customerAddressName) {
        this.customerAddressName = customerAddressName;
    }

    /**
     * Gets the customerInvoiceTemplateCode attribute.
     *
     * @return Returns the customerInvoiceTemplateCode.
     */
    public String getCustomerInvoiceTemplateCode() {

        return customerInvoiceTemplateCode;
    }

    /**
     * Sets the customerInvoiceTemplateCode attribute value.
     *
     * @param customerInvoiceTemplateCode The customerInvoiceTemplateCode to set.
     */
    public void setCustomerInvoiceTemplateCode(String customerInvoiceTemplateCode) {
        this.customerInvoiceTemplateCode = customerInvoiceTemplateCode;
    }

    /**
     * Gets the preferredCustomerInvoiceTemplateCode attribute.
     *
     * @return Returns the preferredCustomerInvoiceTemplateCode.
     */
    public String getPreferredCustomerInvoiceTemplateCode() {

        return preferredCustomerInvoiceTemplateCode;
    }

    /**
     * Sets the preferredCustomerInvoiceTemplateCode attribute value.
     *
     * @param preferredCustomerInvoiceTemplateCode The preferredCustomerInvoiceTemplateCode to set.
     */
    public void setPreferredCustomerInvoiceTemplateCode(String preferredCustomerInvoiceTemplateCode) {
        this.preferredCustomerInvoiceTemplateCode = preferredCustomerInvoiceTemplateCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put(customerNumber, this.customerNumber);
        m.put(customerAddressTypeCode, this.customerAddressTypeCode);
        m.put(customerAddressName, this.customerAddressName);
        m.put(preferredCustomerInvoiceTemplateCode, this.preferredCustomerInvoiceTemplateCode);
        m.put(customerInvoiceTemplateCode, this.customerInvoiceTemplateCode);
        m.put(invoiceIndicatorCode, invoiceIndicatorCode);
        m.put(preferredInvoiceIndicatorCode, this.preferredInvoiceIndicatorCode);
        return m;
    }

}
