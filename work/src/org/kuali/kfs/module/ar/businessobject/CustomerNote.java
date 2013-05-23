/*
 * Copyright 2012 The Kuali Foundation.
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

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.document.service.CustomerNoteService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines notes/comments to a given customer’s record.
 *
 * @author mpritmani
 */
public class CustomerNote extends PersistableBusinessObjectBase implements Comparable<CustomerNote> {

    private String customerNumber;
    private Integer customerNoteIdentifier;
    private String noteText;
    private Date notePostedDate;
    private String authorPrincipalId;

    private Customer customer;
    private Person authorUniversal;

    /**
     * Default constructor.
     */
    public CustomerNote() {
        super();
        this.setNotePostedDateToCurrent();
        this.setNoteText(KRADConstants.EMPTY_STRING);
    }

    /**
     * Sets the {@link #setNotePostedDate(Date)} to the current time.
     */
    public void setNotePostedDateToCurrent() {
        final Date now = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        this.setNotePostedDate(now);
    }

    /**
     * Sets the authorUniversal attribute to the current logged in user.
     */
    public void setAuthorUniversalToCurrentUser() {
        if (ObjectUtils.isNotNull(GlobalVariables.getUserSession()) && ObjectUtils.isNotNull(GlobalVariables.getUserSession().getPerson())) {
            authorUniversal = GlobalVariables.getUserSession().getPerson();
            authorPrincipalId = authorUniversal.getPrincipalId();
        }
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns customerNumber.
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

    /**
     * Gets the customerNoteIdentifier attribute.
     *
     * @return Returns customerNoteIdentifier.
     */
    public Integer getCustomerNoteIdentifier() {
        return customerNoteIdentifier;
    }

    /**
     * Sets the customerNoteIdentifier attribute.
     *
     * @param customerNoteIdentifier The customerNoteIdentifier to set.
     */
    public void setCustomerNoteIdentifier(Integer customerNoteIdentifier) {
        this.customerNoteIdentifier = customerNoteIdentifier;
    }

    /**
     * Gets the noteText attribute.
     *
     * @return Returns noteText.
     */
    public String getNoteText() {
        return noteText;
    }

    /**
     * Sets the noteText attribute.
     *
     * @param noteText The noteText to set.
     */
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    /**
     * Gets the notePostedDate attribute.
     *
     * @return Returns the notePostedDate.
     */
    public Date getNotePostedDate() {
        return notePostedDate;
    }

    /**
     * Sets the notePostedDate attribute.
     *
     * @param notePostedDate The notePostedDate to set.
     */
    public void setNotePostedDate(Date notePostedDate) {
        this.notePostedDate = notePostedDate;
    }

    /**
     * Gets the authorPrincipalId attribute.
     *
     * @return Returns the authorPrincipalId.
     */
    public String getAuthorPrincipalId() {
        return authorPrincipalId;
    }

    /**
     * Sets the authorPrincipalId attribute.
     *
     * @param authorPrincipalId The authorPrincipalId to set.
     */
    public void setAuthorPrincipalId(String authorPrincipalId) {
        this.authorPrincipalId = authorPrincipalId;
    }

    /**
     * Gets the customer attribute.
     *
     * @return Returns the customer object.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute.
     *
     * @param customer The customer object to set.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the authorUniversal attribute.
     *
     * @return Returns the authorUniversal.
     */
    public Person getAuthorUniversal() {
        authorUniversal = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(authorPrincipalId, authorUniversal);
        return authorUniversal;
    }

    /**
     * Sets the authorUniversal attribute value.
     *
     * @param authorUniversal The authorUniversal to set.
     */
    public void setAuthorUniversal(Person authorUniversal) {
        this.authorUniversal = authorUniversal;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.CUSTOMER_NUMBER, this.customerNumber);
        m.put("customerNoteIdentifier", this.customerNoteIdentifier);
        if (ObjectUtils.isNotNull(this.noteText)) {
            m.put("noteText", this.noteText);
        }
        return m;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(CustomerNote arg0) {
        return this.customerNoteIdentifier.compareTo(arg0.customerNoteIdentifier);
    }

    /**
     * This method sets the customerNoteIdentifier before inserting in database.
     *
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override protected void prePersist() {
        super.prePersist();
        CustomerNoteService customerNoteService = SpringContext.getBean(CustomerNoteService.class);
        int customerNoteIdentifier = customerNoteService.getNextCustomerNoteIdentifier();
        this.setCustomerNoteIdentifier(customerNoteIdentifier);
    }

}
