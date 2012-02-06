/*
 * Copyright 2008-2009 The Kuali Foundation
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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class CustomerInvoiceRecurrenceDetails extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String invoiceNumber;
    private String customerNumber;
    private Date documentRecurrenceBeginDate;
    private Date documentRecurrenceEndDate;
    private Integer documentTotalRecurrenceNumber;
    private String documentRecurrenceIntervalCode;
    private String documentInitiatorUserIdentifier;
    private Date documentLastCreateDate;
    private boolean active;

    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    private CustomerInvoiceDocument customerInvoiceDocument;
    private Customer customer;
    private Person documentInitiatorUser;
    
    
    /**
     * Default constructor.
     */
    public CustomerInvoiceRecurrenceDetails() {

    }




    /**
     * Gets the invoiceNumber attribute.
     * 
     * @return Returns the invoiceNumber
     * 
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }


    /**
     * Sets the invoiceNumber attribute.
     * 
     * @param invoiceNumber The invoiceNumber to set.
     * 
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customerNumber
     * 
     */
    public String getCustomerNumber() { 
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     * 
     * @param customerNumber The customerNumber to set.
     * 
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    /**
     * Gets the documentRecurrenceBeginDate attribute.
     * 
     * @return Returns the documentRecurrenceBeginDate
     * 
     */
    public Date getDocumentRecurrenceBeginDate() { 
        return documentRecurrenceBeginDate;
    }

    /**
     * Sets the documentRecurrenceBeginDate attribute.
     * 
     * @param documentRecurrenceBeginDate The documentRecurrenceBeginDate to set.
     * 
     */
    public void setDocumentRecurrenceBeginDate(Date documentRecurrenceBeginDate) {
        this.documentRecurrenceBeginDate = documentRecurrenceBeginDate;
    }


    /**
     * Gets the documentRecurrenceEndDate attribute.
     * 
     * @return Returns the documentRecurrenceEndDate
     * 
     */
    public Date getDocumentRecurrenceEndDate() { 
        return documentRecurrenceEndDate;
    }

    /**
     * Sets the documentRecurrenceEndDate attribute.
     * 
     * @param documentRecurrenceEndDate The documentRecurrenceEndDate to set.
     * 
     */
    public void setDocumentRecurrenceEndDate(Date documentRecurrenceEndDate) {
        this.documentRecurrenceEndDate = documentRecurrenceEndDate;
    }


    /**
     * Gets the documentTotalRecurrenceNumber attribute.
     * 
     * @return Returns the documentTotalRecurrenceNumber
     * 
     */
    public Integer getDocumentTotalRecurrenceNumber() { 
        return documentTotalRecurrenceNumber;
    }

    /**
     * Sets the documentTotalRecurrenceNumber attribute.
     * 
     * @param documentTotalRecurrenceNumber The documentTotalRecurrenceNumber to set.
     * 
     */
    public void setDocumentTotalRecurrenceNumber(Integer documentTotalRecurrenceNumber) {
        this.documentTotalRecurrenceNumber = documentTotalRecurrenceNumber;
    }


    /**
     * Gets the documentRecurrenceIntervalCode attribute.
     * 
     * @return Returns the documentRecurrenceIntervalCode
     * 
     */
    public String getDocumentRecurrenceIntervalCode() { 
        return documentRecurrenceIntervalCode;
    }

    /**
     * Sets the documentRecurrenceIntervalCode attribute.
     * 
     * @param documentRecurrenceIntervalCode The documentRecurrenceIntervalCode to set.
     * 
     */
    public void setDocumentRecurrenceIntervalCode(String documentRecurrenceIntervalCode) {
        this.documentRecurrenceIntervalCode = documentRecurrenceIntervalCode;
    }

    /**
     * Gets the documentInitiatorUserIdentifier attribute.
     * 
     * @return Returns the documentInitiatorUserIdentifier
     * 
     */
    public String getDocumentInitiatorUserIdentifier() { 
        return documentInitiatorUserIdentifier;
    }

    /**
     * Sets the documentInitiatorUserIdentifier attribute.
     * 
     * @param documentInitiatorUserIdentifier The documentInitiatorUserIdentifier to set.
     * 
     */
    public void setDocumentInitiatorUserIdentifier(String documentInitiatorUserIdentifier) {
        this.documentInitiatorUserIdentifier = documentInitiatorUserIdentifier;
    }


    public Person getDocumentInitiatorUser() {
        documentInitiatorUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(documentInitiatorUserIdentifier, documentInitiatorUser);
        return documentInitiatorUser;
    }

    /**
     * @param documentInitiatorUser The documentInitiatorUser to set.
     */
    public void setDocumentInitiatorUser(Person documentInitiatorUser) {
        this.documentInitiatorUser = documentInitiatorUser;
    }
    
    /**
     * The network id of the document initiator
     * 
     * @return the network id of the document initiator
     */
    public String getDocumentInitiatorUserPersonUserIdentifier() {
        return this.getDocumentInitiatorUser().getPrincipalName();
    }


    /**
     * Gets the documentLastCreateDate attribute.
     * 
     * @return Returns the documentLastCreateDate
     * 
     */
    public Date getDocumentLastCreateDate() { 
        return documentLastCreateDate;
    }

    /**
     * Sets the documentLastCreateDate attribute.
     * 
     * @param documentLastCreateDate The documentLastCreateDate to set.
     * 
     */
    public void setDocumentLastCreateDate(Date documentLastCreateDate) {
        this.documentLastCreateDate = documentLastCreateDate;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     * 
     */
    public boolean isActive() { 
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     * 
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the customer attribute. 
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     * @param customer The customer to set.
     * @deprecated
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.invoiceNumber);
        return m;
    }

    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    /**
     * Sets the accountsReceivableDocumentHeader attribute value.
     * @param accountsReceivableDocumentHeader The AccountsReceivableDocumentHeader to set.
     * @deprecated
     */
    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }

    
    /**
     * Gets the customerName attribute.
     * 
     * @return Returns the customerName
     * 
     */
    public String getCustomerName() {
        return this.getCustomer().getCustomerName();
    }


    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    /**
     * Sets the customerInvoiceDocument attribute value.
     * @param customerInvoiceDocument The customerInvoiceDocument to set.
     * @deprecated
     */
    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

}

